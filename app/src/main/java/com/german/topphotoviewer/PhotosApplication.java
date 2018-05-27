package com.german.topphotoviewer;

import android.app.Application;
import android.support.annotation.NonNull;

import io.reactivex.Scheduler;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class PhotosApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        PhotoLibConfig config = new PhotoLibConfig.Builder()
                .photosService(createRetrofitPhotosService())
                .build();
        PhotoLib.init(config);
    }

    @NonNull
    private PhotosService createRetrofitPhotosService() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(getString(R.string.yandex_photo_base_url))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        return retrofit.create(PhotosService.class);
    }
}
