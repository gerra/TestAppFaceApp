package com.german.topphotoviewer.dto;

import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

public class PhotoVariations {
    @SerializedName("orig")
    private PhotoInfo mOriginalPhotoInfo;
    @SerializedName("M")
    private PhotoInfo mMediumPhotoInfo;

    public PhotoInfo getOriginalPhotoInfo() {
        return mOriginalPhotoInfo;
    }

    public void setOriginalPhotoInfo(PhotoInfo originalPhotoInfo) {
        mOriginalPhotoInfo = originalPhotoInfo;
    }

    public PhotoInfo getMediumPhotoInfo() {
        return mMediumPhotoInfo;
    }

    public void setMediumPhotoInfo(PhotoInfo mediumPhotoInfo) {
        mMediumPhotoInfo = mediumPhotoInfo;
    }
}
