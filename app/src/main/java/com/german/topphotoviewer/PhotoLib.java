package com.german.topphotoviewer;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.UiThread;

import com.german.topphotoviewer.net.PhotosService;

public class PhotoLib {
    @Nullable
    private static volatile PhotoLib sInstance;

    @NonNull
    private final PhotosService mPhotosService;

    protected PhotoLib(@NonNull PhotoLibConfig photoLibConfig) {
        mPhotosService = photoLibConfig.getPhotosService();
    }

    @NonNull
    protected PhotosService getPhotosService() {
        return mPhotosService;
    }

    @UiThread
    static void init(@NonNull PhotoLibConfig photoLibConfig) {
        if (sInstance != null) {
            throw new IllegalStateException("Already inited");
        }
        synchronized (PhotoLib.class) {
            sInstance = new PhotoLib(photoLibConfig);
        }
    }

    @NonNull
    public static PhotosService getPhotoService() {
        return impl().getPhotosService();
    }

    @NonNull
    private static PhotoLib impl() {
        synchronized (PhotoLib.class) {
            PhotoLib local = sInstance;
            if (local == null) {
                throw new IllegalStateException("Call init()");
            }
            return local;
        }
    }
}
