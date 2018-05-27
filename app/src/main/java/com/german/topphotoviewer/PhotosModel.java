package com.german.topphotoviewer;

import android.content.Context;
import android.support.annotation.NonNull;

public interface PhotosModel {
    void loadPhotos(@NonNull Context context, @NonNull PhotoLoadInteractor photoLoadInteractor);
}
