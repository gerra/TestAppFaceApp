package com.german.topphotoviewer.data;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import java.util.Collections;
import java.util.List;

public class TopPhotoList implements Parcelable {
    public static final TopPhotoList EMPTY = new TopPhotoList(Collections.<TopPhoto>emptyList());

    @NonNull
    private final List<TopPhoto> mTopPhotos;

    public TopPhotoList(@NonNull List<TopPhoto> topPhotos) {
        mTopPhotos = topPhotos;
    }

    protected TopPhotoList(Parcel in) {
        mTopPhotos = in.createTypedArrayList(TopPhoto.CREATOR);
    }

    public static final Creator<TopPhotoList> CREATOR = new Creator<TopPhotoList>() {
        @Override
        public TopPhotoList createFromParcel(Parcel in) {
            return new TopPhotoList(in);
        }

        @Override
        public TopPhotoList[] newArray(int size) {
            return new TopPhotoList[size];
        }
    };

    @NonNull
    public List<TopPhoto> getTopPhotos() {
        return mTopPhotos;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(mTopPhotos);
    }
}
