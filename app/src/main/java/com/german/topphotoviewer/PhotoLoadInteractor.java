package com.german.topphotoviewer;

import android.support.annotation.NonNull;

public interface PhotoLoadInteractor {
    void onListLoaded(@NonNull TopPhotoList topPhotoList);
    void onListLoadFailed();
    void onPhotoLoaded(@NonNull TopPhoto topPhoto);
    void onPhotoLoadFailed(@NonNull TopPhoto topPhoto);
    void onAllPhotosLoaded();
}
