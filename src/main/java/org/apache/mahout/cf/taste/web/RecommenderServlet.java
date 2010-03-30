/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.mahout.cf.taste.web;

import com.spreecommerce.ProductRecommender;
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
import java.util.Arrays;
import org.apache.commons.lang.ArrayUtils;

        
/**
 * <p>A servlet which returns recommendations, as its name implies. The servlet accepts GET and POST
 * HTTP requests, and looks for two parameters:</p>
 *
 * <ul>
 * <li><em>userID</em>: the user ID for which to produce recommendations</li>
 * <li><em>howMany</em>: the number of recommendations to produce</li>
 * <li><em>debug</em>: (optional) output a lot of information that is useful in debugging.
 * Defaults to false, of course.</li>
 * </ul>
 *
 * <p>The response is text, and contains a list of the IDs of recommended items, in descending
 * order of relevance, one per line.</p>
 *
 * <p>For example, you can get 10 recommendations for user 123 from the following URL (assuming
 * you are running taste in a web application running locally on port 8080):<br/>
 * <code>http://localhost:8080/taste/RecommenderServlet?userID=123&amp;howMany=10</code></p>
 *
 * <p>This servlet requires one <code>init-param</code> in <code>web.xml</code>: it must find
 * a parameter named "recommender-class" which is the name of a class that implements
 * {@link Recommender} and has a no-arg constructor. The servlet will instantiate and use
 * this {@link Recommender} to produce recommendations.</p>
 */
public final class RecommenderServlet extends HttpServlet {

  private static final int NUM_TOP_PREFERENCES = 20;
  private static final int DEFAULT_HOW_MANY = 20;

  private ProductRecommender recommender;

  @Override
  public void init(ServletConfig config) throws ServletException {
    super.init(config);
    String recommenderClassName = config.getInitParameter("recommender-class");
    if (recommenderClassName == null) {
      throw new ServletException("Servlet init-param \"recommender-class\" is not defined");
    }
    try {
//      RecommenderSingleton.initializeIfNeeded(recommenderClassName);
      recommender = new ProductRecommender();
    } catch (TasteException te) {
      throw new ServletException(te);
    } catch (IOException e) {
      throw new ServletException(e);
    }
  }

  @Override
  public void doGet(HttpServletRequest request,
                    HttpServletResponse response) throws ServletException {


    String howManyString = request.getParameter("howMany");
    int howMany = howManyString == null ? DEFAULT_HOW_MANY : Integer.parseInt(howManyString);

    boolean debug = Boolean.parseBoolean(request.getParameter("debug"));



    String userIDString = request.getParameter("userID");
    String productIDsString = request.getParameter("productIDs");
    if (userIDString == null && productIDsString == null) {
      throw new ServletException("Either productIDs or a userID must be specified");
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




  private static void writeJSON(HttpServletResponse response, Iterable<RecommendedItem> items) throws IOException {
    response.setContentType("text/plain");
    response.setCharacterEncoding("UTF-8");
    response.setHeader("Cache-Control", "no-cache");
    PrintWriter writer = response.getWriter();
    writer.print("{\"recommendedItems\":{\"item\":[");
    for (RecommendedItem recommendedItem : items) {
      writer.print("{\"value\":\"");
      writer.print(recommendedItem.getValue());
      writer.print("\",\"id\":\"");
      writer.print(recommendedItem.getItemID());
      writer.print("\"},");
    }
    writer.println("]}}");
  }

  private void writePlainText(HttpServletResponse response,
                              Iterable<RecommendedItem> items) throws IOException, TasteException {
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

  @Override
  public String toString() {
    return "RecommenderServlet[recommender:" + recommender + ']';
  }

}
