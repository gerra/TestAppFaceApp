package com.german.topphotoviewer;

import android.content.Context;
import android.support.annotation.NonNull;

public interface PhotosPresenter {
    void attachView(@NonNull PhotosView view);
    void detachView();
    void loadPhotos(@NonNull Context context);
}
