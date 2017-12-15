package com.mfp.nytsearchmfp.model;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ArticleModel {

    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("copyright")
    @Expose
    private String copyright;
    @SerializedName("response")
    @Expose
    private Response response;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCopyright() {
        return copyright;
    }

    public void setCopyright(String copyright) {
        this.copyright = copyright;
    }

    public Response getResponse() {
        return response;
    }


    public static ArticleModel parseJSON(String response){
        Gson gson = new GsonBuilder().create();
        ArticleModel model1 = gson.fromJson(response,ArticleModel.class);
        return model1;
    }


    public void setResponse(Response response) {
        this.response = response;
    }

}