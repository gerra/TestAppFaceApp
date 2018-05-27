package com.german.topphotoviewer;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.german.topphotoviewer.net.PhotosService;

import junit.framework.Assert;

public class PhotoLibConfig {
    @NonNull
    private final PhotosService mPhotosService;

    PhotoLibConfig(@NonNull PhotosService photosService) {
        mPhotosService = photosService;
    }

    @NonNull
    public PhotosService getPhotosService() {
        return mPhotosService;
    }

    public static class Builder {
        @Nullable
        private PhotosService mPhotosService;

        @NonNull
        public Builder photosService(@NonNull PhotosService photosService) {
            mPhotosService = photosService;
            return this;
        }

        @NonNull
        public PhotoLibConfig build() {
            Assert.assertNotNull("PhotosService should be initialized", mPhotosService);
            return new PhotoLibConfig(mPhotosService);
        }
    }
}
