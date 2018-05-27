package com.german.topphotoviewer.dto;

import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

public class PhotoInfo {
    @SerializedName("height")
    private int mHeight;
    @SerializedName("width")
    private int mWidth;
    @SerializedName("href")
    private String mUrl;

    public int getHeight() {
        return mHeight;
    }

    public void setHeight(int height) {
        mHeight = height;
    }

    public int getWidth() {
        return mWidth;
    }

    public void setWidth(int width) {
        mWidth = width;
    }

    public String getUrl() {
        return mUrl;
    }

    public void setUrl(String url) {
        mUrl = url;
    }
}
