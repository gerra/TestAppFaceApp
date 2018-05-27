package com.german.topphotoviewer;

import android.support.annotation.NonNull;

import com.german.topphotoviewer.data.TopPhoto;

public interface PhotosView {
    void showProgress();
    void hideProgress();
    void showStubs();
    void showPhoto(@NonNull TopPhoto topPhoto);
    void showListLoadError();
}
