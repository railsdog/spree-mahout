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

package com.spreecommerce;


import org.apache.mahout.cf.taste.common.Refreshable;
import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.impl.recommender.GenericItemBasedRecommender;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.impl.model.file.FileDataModel;

import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.apache.mahout.cf.taste.recommender.Recommender;
import org.apache.mahout.cf.taste.recommender.Rescorer;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.io.File;
import org.apache.mahout.cf.taste.impl.similarity.LogLikelihoodSimilarity;
import org.apache.mahout.cf.taste.similarity.ItemSimilarity;




/**
 *
 * @author davidnorth
 */
public class ProductRecommender implements Recommender  {


    private final GenericItemBasedRecommender recommender;


    /**
     * @throws IOException if an error occurs while creating the {@link GroupLensDataModel}
     * @throws TasteException if an error occurs while initializing this {@link GroupLensRecommender}
     */
    public ProductRecommender() throws IOException, TasteException {
        this(new FileDataModel(new File("src/data.txt")));
    }

    /**
     * <p>Alternate constructor that takes a {@link DataModel} argument, which allows this {@link Recommender}
     * to be used with the {@link org.apache.mahout.cf.taste.eval.RecommenderEvaluator} framework.</p>
     *
     * @param dataModel data model
     * @throws TasteException if an error occurs while initializing this {@link GroupLensRecommender}
     */
    public ProductRecommender(DataModel dataModel) throws TasteException, IOException {
        ItemSimilarity itemSimilarity = new LogLikelihoodSimilarity(dataModel);
        recommender = new GenericItemBasedRecommender(dataModel, itemSimilarity);
    }




    public List<RecommendedItem> mostSimilarItems(long itemID, int howMany) throws TasteException {
        return recommender.mostSimilarItems(itemID, howMany);
    }

    public List<RecommendedItem> mostSimilarItems(long[] itemIDs, int howMany) throws TasteException {
        return recommender.mostSimilarItems(itemIDs, howMany);
    }


    @Override
    public List<RecommendedItem> recommend(long userID, int howMany) throws TasteException {
        return recommender.recommend(userID, howMany);
    }

    @Override
    public List<RecommendedItem> recommend(long userID, int howMany, Rescorer<Long> rescorer) throws TasteException {
        return recommender.recommend(userID, howMany, rescorer);
    }

    @Override
    public float estimatePreference(long userID, long itemID) throws TasteException {
        return recommender.estimatePreference(userID, itemID);
    }

    @Override
    public void setPreference(long userID, long itemID, float value) throws TasteException {
        recommender.setPreference(userID, itemID, value);
    }

    @Override
    public void removePreference(long userID, long itemID) throws TasteException {
        recommender.removePreference(userID, itemID);
    }

    @Override
    public DataModel getDataModel() {
        return recommender.getDataModel();
    }

    @Override
    public void refresh(Collection<Refreshable> alreadyRefreshed) {
        recommender.refresh(alreadyRefreshed);
    }


    

}
