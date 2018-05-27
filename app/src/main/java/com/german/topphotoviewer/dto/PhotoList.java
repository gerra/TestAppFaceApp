package com.german.topphotoviewer.dto;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class PhotoList {
    @SerializedName("entries")
    private List<PhotoEntry> mPhotoEntries;

    public List<PhotoEntry> getPhotoEntries() {
        return mPhotoEntries;
    }

    public void setPhotoEntries(List<PhotoEntry> photoEntries) {
        mPhotoEntries = photoEntries;
    }
}
