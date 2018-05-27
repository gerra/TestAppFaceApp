package com.german.topphotoviewer.dto;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

public class PhotoEntry {
    @Nullable
    @SerializedName("id")
    private String mId;
    @Nullable
    @SerializedName("img")
    private PhotoVariations mPhotoVariations;

    @Nullable
    public PhotoVariations getPhotoVariations() {
        return mPhotoVariations;
    }

    public void setPhotoVariations(@NonNull PhotoVariations photoVariations) {
        mPhotoVariations = photoVariations;
    }

    @Nullable
    public String getId() {
        return mId;
    }

    public void setId(String id) {
        mId = id;
    }
}
