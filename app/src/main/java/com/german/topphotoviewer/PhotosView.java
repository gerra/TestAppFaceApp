package com.german.topphotoviewer;

import android.net.Uri;
import android.support.annotation.NonNull;

import java.util.List;

public interface PhotosView {
    void showProgress();
    void hideProgress();
    void showStubs();
    void showPhoto(@NonNull TopPhoto topPhoto);
    void showListLoadError();
}
