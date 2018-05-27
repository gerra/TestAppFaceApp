package com.german.topphotoviewer;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.german.topphotoviewer.servicemodel.PhotosServiceModel;

public class PhotosPresenterImpl implements PhotosPresenter {
    @Nullable
    private PhotosView mPhotosView;
    @NonNull
    private final PhotosModel mPhotosModel;
    private boolean mIsLoading;

    public PhotosPresenterImpl() {
        mPhotosModel = new PhotosServiceModel();
    }

    @Override
    public void attachView(@NonNull PhotosView view) {
        mPhotosView = view;
    }

    @Override
    public void detachView() {
        mPhotosView = null;
    }

    @Override
    public void loadPhotos(@NonNull Context context) {
        if (mIsLoading) {
            return;
        }
        mIsLoading = true;
        if (mPhotosView != null) {
            mPhotosView.showProgress();
        }
        mPhotosModel.loadPhotos(context, new PhotoLoadInteractorImpl());
    }

    private class PhotoLoadInteractorImpl implements PhotoLoadInteractor {
        @Override
        public void onListLoaded(@NonNull TopPhotoList topPhotoList) {
            if (mPhotosView != null) {
                mPhotosView.showStubs();
            }
        }

        @Override
        public void onListLoadFailed() {
            if (mPhotosView != null) {
                mPhotosView.showListLoadError();
            }
            mIsLoading = false;
        }

        @Override
        public void onPhotoLoaded(@NonNull TopPhoto topPhoto) {
            if (mPhotosView != null) {
                mPhotosView.showPhoto(topPhoto);
            }
        }

        @Override
        public void onPhotoLoadFailed(@NonNull TopPhoto topPhoto) {

        }

        @Override
        public void onAllPhotosLoaded() {
            mIsLoading = false;
        }
    }
}
