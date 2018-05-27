package com.german.topphotoviewer;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import java.util.List;

public class MainActivity extends AppCompatActivity implements PhotosView {
    private static final String TAG = "[MainActivity]";

    private PhotosPresenter mPhotosPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RecyclerView recyclerView = findViewById(R.id.photosList);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
//        recyclerView.setAdapter();

        mPhotosPresenter = new PhotosPresenterImpl();
        mPhotosPresenter.attachView(this);
        mPhotosPresenter.loadPhotos(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.refresh) {
            mPhotosPresenter.loadPhotos(this);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void showProgress() {

    }

    @Override
    public void hideProgress() {

    }

    @Override
    public void showStubs() {

    }

    @Override
    public void showPhoto(@NonNull TopPhoto topPhoto) {
        Log.d(TAG, "show " + topPhoto.getMediumUrl());
    }

    @Override
    public void showListLoadError() {

    }
}
