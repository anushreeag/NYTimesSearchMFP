package com.mfp.nytsearchmfp.model;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by anushree on 9/19/2017.
 */

public class Article implements Parcelable {

    String web_url;
    String thumbnail;
    String title;
    String new_desk;
    String snippet;


    protected Article(Parcel in) {
        web_url = in.readString();
        thumbnail = in.readString();
        title = in.readString();
        new_desk = in.readString();
        snippet = in.readString();

    }

    public static final Creator<Article> CREATOR = new Creator<Article>() {
        @Override
        public Article createFromParcel(Parcel in) {
            return new Article(in);
        }

        @Override
        public Article[] newArray(int size) {
            return new Article[size];
        }
    };

    public String getWeb_url() {
        return web_url;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public String getTitle() {
        return title;
    }
    public String getNew_desk() {
        return new_desk;
    }

    public String getSnippet() {
        return snippet;
    }



    public Article(JSONObject object) throws JSONException {
        this.web_url = object.getString("web_url");
        this.title = object.getJSONObject("headline").getString("main");
        JSONArray multimedia = object.getJSONArray("multimedia");
        if(multimedia.length()>0) {
            this.thumbnail = "http://www.nytimes.com/" + multimedia.getJSONObject(0).getString("url");
        }
        else {
            this.thumbnail = "";
        }
       this.new_desk = object.getString("new_desk");
        this.snippet = object.getString("snippet");

    }



    public Article(Doc doc) throws JSONException {
        this.web_url = doc.getWebUrl();
        this.title = doc.getHeadline().getMain();
        List<Multimedium> multimedia = doc.getMultimedia();
        if(multimedia.size()>0) {
            this.thumbnail = "http://www.nytimes.com/" + multimedia.get(0).getUrl();
        }
        else {
            this.thumbnail = "";
        }

        String newsDesk = doc.getNewDesk();
        if(newsDesk!=null) {
            this.new_desk = newsDesk;
        }
        else {
            this.new_desk = "";
        }
        this.snippet = doc.getSnippet();

    }




    public static ArrayList<Article> getArticleList(List<Doc> docs){

        ArrayList<Article> articlelist =  new ArrayList<>();
        for(int i=0;i<docs.size();i++){
            try {
                articlelist.add(new Article(docs.get(i)));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }


        return articlelist;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {

        parcel.writeString(web_url);
        parcel.writeString(thumbnail);
        parcel.writeString(title);
        parcel.writeString(new_desk);
        parcel.writeString(snippet);
    }
}
