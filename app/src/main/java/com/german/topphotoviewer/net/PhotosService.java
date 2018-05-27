package com.german.topphotoviewer.net;

import android.support.annotation.NonNull;

import com.german.topphotoviewer.dto.PhotoList;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Headers;

public interface PhotosService {
    @GET("podhistory/")
    @NonNull
    @Headers({
            "Accept: application/json"
    })
    Observable<PhotoList> getTopPhotoList();
}
