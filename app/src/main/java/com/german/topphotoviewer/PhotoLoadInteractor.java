package com.german.topphotoviewer;

import android.support.annotation.NonNull;

import com.german.topphotoviewer.data.TopPhoto;
import com.german.topphotoviewer.data.TopPhotoList;

public interface PhotoLoadInteractor {
    void onListLoaded(@NonNull TopPhotoList topPhotoList);
    void onListLoadFailed();
    void onPhotoLoaded(@NonNull TopPhoto topPhoto);
    void onPhotoLoadFailed(@NonNull TopPhoto topPhoto);
    void onAllPhotosLoaded();
}
