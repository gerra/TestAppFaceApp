package com.german.topphotoviewer.servicemodel;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.JobIntentService;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.german.topphotoviewer.PhotoLoadInteractor;
import com.german.topphotoviewer.PhotosModel;
import com.german.topphotoviewer.data.TopPhoto;
import com.german.topphotoviewer.data.TopPhotoList;

public class PhotosServiceModel implements PhotosModel {
    public static final String ACTION_LIST_LOADED = "com.german.topphotoviewer.LIST_LOADED";
    public static final String ACTION_LIST_LOAD_FAILED = "com.german.topphotoviewer.LIST_LOAD_FAILED";
    public static final String ACTION_PHOTO_LOADED = "com.german.topphotoviewer.PHOTO_LOADED";
    public static final String ACTION_PHOTO_LOAD_FAILED = "com.german.topphotoviewer.PHOTO_LOAD_FAILED";
    public static final String ACTION_ALL_PHOTOS_LOADED = "com.german.topphotoviewer.ALL_PHOTO_LOADED";

    public static final String EXTRA_LIST = "LIST";
    public static final String EXTRA_PHOTO = "PHOTO";

    private static final IntentFilter INTENT_FILTER = new IntentFilter();
    static {
        INTENT_FILTER.addAction(ACTION_LIST_LOADED);
        INTENT_FILTER.addAction(ACTION_PHOTO_LOADED);
        INTENT_FILTER.addAction(ACTION_ALL_PHOTOS_LOADED);
    }

    @Override
    public void loadPhotos(@NonNull Context context, @NonNull PhotoLoadInteractor photoLoadInteractor) {
        JobIntentService.enqueueWork(context,
                                     PhotosLoadService.class,
                                     PhotosLoadService.TOP_PHOTO_JOB_ID,
                                     new Intent());
        LocalBroadcastManager.getInstance(context)
                .registerReceiver(new InteractorBroadcastReceiver(photoLoadInteractor), INTENT_FILTER);
    }

    private class InteractorBroadcastReceiver extends BroadcastReceiver {
        private static final String TAG = "[InteractorReceiver]";

        @NonNull
        private final PhotoLoadInteractor mPhotoLoadInteractor;

        InteractorBroadcastReceiver(@NonNull PhotoLoadInteractor photoLoadInteractor) {
            mPhotoLoadInteractor = photoLoadInteractor;
        }

        @Override
        public void onReceive(@NonNull Context context, @Nullable Intent intent) {
            if (intent == null) {
                Log.e(TAG, "intent = null");
                return;
            }
            String action = intent.getAction();
            if (action == null) {
                Log.e(TAG, "action = null");
                return;
            }
            switch (action) {
                case ACTION_LIST_LOADED: {
                    TopPhotoList topPhotoList = intent.getParcelableExtra(EXTRA_LIST);
                    mPhotoLoadInteractor.onListLoaded(topPhotoList);
                    break;
                }
                case ACTION_LIST_LOAD_FAILED: {
                    mPhotoLoadInteractor.onListLoadFailed();
                    break;
                }
                case ACTION_PHOTO_LOADED: {
                    TopPhoto topPhoto = intent.getParcelableExtra(EXTRA_PHOTO);
                    mPhotoLoadInteractor.onPhotoLoaded(topPhoto);
                    break;
                }
                case ACTION_PHOTO_LOAD_FAILED: {
                    TopPhoto topPhoto = intent.getParcelableExtra(EXTRA_PHOTO);
                    mPhotoLoadInteractor.onPhotoLoadFailed(topPhoto);
                    break;
                }
                case ACTION_ALL_PHOTOS_LOADED: {
                    mPhotoLoadInteractor.onAllPhotosLoaded();
                    LocalBroadcastManager.getInstance(context)
                            .unregisterReceiver(this);
                    break;
                }
                default: {
                    Log.e(TAG, "Unknown action: " + action);
                    break;
                }
            }
        }
    }
}
