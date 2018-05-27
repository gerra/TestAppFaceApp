package com.german.topphotoviewer.ui;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.german.topphotoviewer.R;
import com.german.topphotoviewer.data.TopPhotoList;

public class PhotosAdapter extends RecyclerView.Adapter<PhotoViewHolder> {
    @NonNull
    private final Context mContext;
    @NonNull
    private TopPhotoList mTopPhotoList;

    public PhotosAdapter(@NonNull Context context, @NonNull TopPhotoList topPhotoList) {
        mContext = context;
        mTopPhotoList = topPhotoList;
    }

    public void setTopPhotoList(@NonNull TopPhotoList topPhotoList) {
        mTopPhotoList = topPhotoList;
    }

    @NonNull
    @Override
    public PhotoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new PhotoViewHolder(LayoutInflater.from(mContext).inflate(R.layout.photo_list_item, null, false));
    }

    @Override
    public void onBindViewHolder(PhotoViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }
}
