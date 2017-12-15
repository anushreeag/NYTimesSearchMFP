package com.mfp.nytsearchmfp.utils;

import com.mfp.nytsearchmfp.model.ArticleModel;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;

/**
 * Created by anushree on 9/23/2017.
 */

public interface ApiService {

    /*
    Retrofit get annotation with our URL
    And our method that will return us the List of ContactList
    */
    @GET("/svc/search/v2/articlesearch.json?api-key=72b7f2dfb0764545a2e8378566e2d0af")
    Call<ArticleModel> getMyJSON(@QueryMap Map<String,Object> params);



}
