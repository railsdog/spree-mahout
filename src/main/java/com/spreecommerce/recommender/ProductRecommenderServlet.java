/*
 *  Copyright 2010 davidnorth.
 * 
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 * 
 *       http://www.apache.org/licenses/LICENSE-2.0
 * 
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *  under the License.
 */
package com.spreecommerce.recommender;

import com.spreecommerce.recommender.ProductRecommender;
import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.apache.mahout.cf.taste.recommender.Recommender;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

/**
 *
 * @author davidnorth
 */
public final class ProductRecommenderServlet extends HttpServlet {

    private static final int NUM_TOP_PREFERENCES = 20;
    private static final int DEFAULT_HOW_MANY = 20;
    private ProductRecommender recommender;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        try {
            recommender = new ProductRecommender();
        } catch (TasteException te) {
            throw new ServletException(te);
        } catch (IOException e) {
            throw new ServletException(e);
        }
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException {

        String howManyString = request.getParameter("count");
        int howMany = howManyString == null ? DEFAULT_HOW_MANY : Integer.parseInt(howManyString);

        String userIDString = request.getParameter("user");
        String productIDsString = request.getParameter("products");
        if (userIDString == null && productIDsString == null) {
            throw new ServletException("Either 'user' or 'products' parameters must be provided");
        }

        try {

            List<RecommendedItem> items;

            if (userIDString != null) {
                long userID = Long.parseLong(userIDString);
                items = recommender.recommend(userID, howMany);

            } else {
                String[] productIDStrings = productIDsString.split(",");
                long[] productIDs = new long[productIDStrings.length];

                for (int i = 0; i < productIDStrings.length; i++) {
                    productIDs[i] = Long.parseLong(productIDStrings[i]);
                }
                items = recommender.mostSimilarItems(productIDs, howMany);
            }

            writePlainText(response, items);


        } catch (TasteException te) {
            throw new ServletException(te);
        } catch (IOException ioe) {
            throw new ServletException(ioe);
        }

    }

    private void writePlainText(HttpServletResponse response, Iterable<RecommendedItem> items) throws IOException, TasteException {
        response.setContentType("text/plain");
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Cache-Control", "no-cache");
        PrintWriter writer = response.getWriter();
        for (RecommendedItem recommendedItem : items) {
            //writer.print(recommendedItem.getValue());
            //writer.print('\t');
            writer.println(recommendedItem.getItemID());
        }
    }

    @Override
    public void doPost(HttpServletRequest request,
            HttpServletResponse response) throws ServletException {
        doGet(request, response);
    }
}
