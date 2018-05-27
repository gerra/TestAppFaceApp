package com.german.topphotoviewer.data;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

public class TopPhoto implements Parcelable {
    private int mIndex;
    @NonNull
    private final String mMediumUrl;
    @NonNull
    private final String mOriginalUrl;

    public TopPhoto(int index, @NonNull String mediumUrl, @NonNull String originalUrl) {
        mIndex = index;
        mMediumUrl = mediumUrl;
        mOriginalUrl = originalUrl;
    }

    protected TopPhoto(Parcel in) {
        mIndex = in.readInt();
        mMediumUrl = in.readString();
        mOriginalUrl = in.readString();
    }

    public static final Creator<TopPhoto> CREATOR = new Creator<TopPhoto>() {
        @Override
        public TopPhoto createFromParcel(Parcel in) {
            return new TopPhoto(in);
        }

        @Override
        public TopPhoto[] newArray(int size) {
            return new TopPhoto[size];
        }
    };

    public int getIndex() {
        return mIndex;
    }

    @NonNull
    public String getMediumUrl() {
        return mMediumUrl;
    }

    @NonNull
    public String getOriginalUrl() {
        return mOriginalUrl;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(mIndex);
        dest.writeString(mMediumUrl);
        dest.writeString(mOriginalUrl);
    }
}
