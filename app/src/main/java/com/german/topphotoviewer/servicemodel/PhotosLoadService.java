package com.german.topphotoviewer.servicemodel;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.JobIntentService;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;

import com.german.topphotoviewer.BlobLoader;
import com.german.topphotoviewer.PhotoLib;
import com.german.topphotoviewer.TopPhoto;
import com.german.topphotoviewer.TopPhotoList;
import com.german.topphotoviewer.Utils;
import com.german.topphotoviewer.cache.LastDayFileCache;
import com.german.topphotoviewer.dto.PhotoEntry;
import com.german.topphotoviewer.dto.PhotoInfo;
import com.german.topphotoviewer.dto.PhotoList;
import com.german.topphotoviewer.dto.PhotoVariations;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

// TODO : cache for list as interface
public class PhotosLoadService extends JobIntentService {
    private static final String TAG = "[PhotosLoadService]";

    public static final int TOP_PHOTO_JOB_ID = 56827;

    private static final String TOP_PHOTOS_PREFS_KEY = "TOP_PHOTOS_PREFS";

    private static final String TOP_PHOTOS_KEY = "TOP_PHOTOS";
    private static final String FIELD_SEPARATOR = ";;";
    private static final String ITEM_SEPARATOR = "&&";

    SharedPreferences mPrefs;
    private LastDayFileCache mLastDayFileCache;
    private BlobLoader<LastDayFileCache> mBlobLoader;
    LocalBroadcastManager mLocalBroadcastManager;

    @Override
    public void onCreate() {
        super.onCreate();
        mPrefs = getSharedPreferences(TOP_PHOTOS_PREFS_KEY, MODE_PRIVATE);
        mLastDayFileCache = new LastDayFileCache(this);
        mBlobLoader = new BlobLoader.Builder<>(mLastDayFileCache)
                .build();
        mLocalBroadcastManager = LocalBroadcastManager.getInstance(this);
    }

    @Override
    protected void onHandleWork(@NonNull Intent work) {
        if (Utils.isConnected(this)) {
            PhotoLib.getPhotoService()
                    .getTopPhotoList()
                    .retry(3)
                    .doOnError(new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            mLocalBroadcastManager.sendBroadcast(new Intent(PhotosServiceModel.ACTION_LIST_LOAD_FAILED));
                        }
                    })
                    .map(new Function<PhotoList, TopPhotoList>() {
                        @NonNull
                        @Override
                        public TopPhotoList apply(@Nullable PhotoList photoList) throws Exception {
                            return convertDTO(photoList);
                        }
                    })
                    .doOnNext(new Consumer<TopPhotoList>() {
                        @Override
                        public void accept(TopPhotoList topPhotoList) throws Exception {
                            saveToPrefs(mPrefs, topPhotoList);
                            Intent intent = new Intent(PhotosServiceModel.ACTION_LIST_LOADED)
                                    .putExtra(PhotosServiceModel.EXTRA_LIST, topPhotoList);
                            mLocalBroadcastManager.sendBroadcast(intent);
                        }
                    })
                    .flatMap(new Function<TopPhotoList, ObservableSource<TopPhoto>>() {
                        @NonNull
                        @Override
                        public ObservableSource<TopPhoto> apply(TopPhotoList topPhotoList) throws Exception {
                            return Observable.fromIterable(topPhotoList.getTopPhotos())
                                    .subscribeOn(Schedulers.io());
                        }
                    })
                    .doOnNext(new Consumer<TopPhoto>() {
                        @Override
                        public void accept(TopPhoto topPhoto) throws Exception {
                            loadPhoto(topPhoto);
                        }
                    })
                    .subscribe(new Observer<TopPhoto>() {
                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        @Override
                        public void onNext(TopPhoto topPhoto) {

                        }

                        @Override
                        public void onError(Throwable e) {
                            mLocalBroadcastManager.sendBroadcast(new Intent(PhotosServiceModel.ACTION_LIST_LOAD_FAILED));
                            Log.e(TAG, "", e);
                        }

                        @Override
                        public void onComplete() {
                            mLocalBroadcastManager.sendBroadcast(new Intent(PhotosServiceModel.ACTION_ALL_PHOTOS_LOADED));
                        }
                    });
        } else {
            TopPhotoList topPhotoList = loadFromPrefs(mPrefs);
            if (topPhotoList == null) {
                mLocalBroadcastManager.sendBroadcast(new Intent(PhotosServiceModel.ACTION_LIST_LOAD_FAILED));
            } else {
                Intent intent = new Intent(PhotosServiceModel.ACTION_LIST_LOADED)
                        .putExtra(PhotosServiceModel.EXTRA_LIST, topPhotoList);
                mLocalBroadcastManager.sendBroadcast(intent);

                mLocalBroadcastManager.sendBroadcast(new Intent(PhotosServiceModel.ACTION_ALL_PHOTOS_LOADED));
            }
        }
    }

    void saveToPrefs(@NonNull SharedPreferences sharedPreferences, @NonNull TopPhotoList topPhotoList) {
        List<TopPhoto> topPhotos = topPhotoList.getTopPhotos();
        List<String> topPhotoStrings = new ArrayList<>(topPhotos.size());
        for (TopPhoto topPhoto : topPhotos) {
            String topPhotoString = new StringBuilder()
                    .append(topPhoto.getIndex())
                    .append(FIELD_SEPARATOR)
                    .append(topPhoto.getMediumUrl())
                    .append(FIELD_SEPARATOR)
                    .append(topPhoto.getOriginalUrl())
                    .toString();
            topPhotoStrings.add(topPhotoString);
        }
        String topPhotosString = TextUtils.join(ITEM_SEPARATOR, topPhotoStrings);
        sharedPreferences.edit()
                .putString(TOP_PHOTOS_KEY, topPhotosString)
                .apply();
    }

    @Nullable
    TopPhotoList loadFromPrefs(@NonNull SharedPreferences sharedPreferences) {
        String topPhotosString = sharedPreferences.getString(TOP_PHOTOS_KEY, null);
        if (topPhotosString == null) {
            return null;
        }
        String[] topPhotoStrings = topPhotosString.split(ITEM_SEPARATOR);
        List<TopPhoto> topPhotos = new ArrayList<>(topPhotoStrings.length);
        for (String topPhotoString : topPhotoStrings) {
            String[] fields = topPhotoString.split(FIELD_SEPARATOR);
            topPhotos.add(new TopPhoto(Integer.parseInt(fields[0]), fields[1], fields[2]));
        }
        return new TopPhotoList(topPhotos);
    }

    // TODO : add adapter to lib
    @NonNull
    TopPhotoList convertDTO(@Nullable PhotoList photoList) {
        if (photoList == null) {
            return TopPhotoList.EMPTY;
        }

        List<PhotoEntry> photoEntries = photoList.getPhotoEntries();
        if (photoEntries == null) {
            return TopPhotoList.EMPTY;
        }

        List<TopPhoto> topPhotos = new ArrayList<>(photoEntries.size());
        for (int i = 0; i < photoEntries.size(); i++) {
            PhotoEntry photoEntry = photoEntries.get(i);
            PhotoVariations photoVariations = photoEntry.getPhotoVariations();
            if (photoVariations == null) {
                continue;
            }
            PhotoInfo original = photoVariations.getOriginalPhotoInfo();
            if (original == null || original.getUrl() == null) {
                continue;
            }
            PhotoInfo medium = photoVariations.getMediumPhotoInfo();
            medium = medium != null && medium.getUrl() != null ? medium : original;

            topPhotos.add(new TopPhoto(i,
                                       medium.getUrl(),
                                       original.getUrl()));
        }

        return new TopPhotoList(topPhotos);
    }

    void loadPhoto(@Nullable TopPhoto topPhoto) {
        if (topPhoto == null) {
            return;
        }
        PhotoConsumer photoConsumer = new PhotoConsumer(mLocalBroadcastManager, topPhoto);
        mBlobLoader.load(this, topPhoto.getMediumUrl(), BlobLoader.Transformer.EMPTY, photoConsumer);
    }

    private static class PhotoConsumer implements BlobLoader.Consumer {
        @NonNull
        private final LocalBroadcastManager mLocalBroadcastManager;
        @NonNull
        private final TopPhoto mPhoto;

        PhotoConsumer(@NonNull LocalBroadcastManager localBroadcastManager,
                              @NonNull TopPhoto photo) {
            mLocalBroadcastManager = localBroadcastManager;
            mPhoto = photo;
        }

        @Override
        public void onData(@Nullable Object data) {
            sendBroadcast(true);
        }

        @Override
        public void onError(@NonNull Throwable error) {
            sendBroadcast(false);
        }

        private void sendBroadcast(boolean success) {
            Intent intent = new Intent(success
                                               ? PhotosServiceModel.ACTION_PHOTO_LOADED
                                               : PhotosServiceModel.ACTION_PHOTO_LOAD_FAILED);
            intent.putExtra(PhotosServiceModel.EXTRA_PHOTO, mPhoto);
            mLocalBroadcastManager.sendBroadcast(intent);
        }
    }
}
