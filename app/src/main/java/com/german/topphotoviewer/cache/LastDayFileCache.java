package com.german.topphotoviewer.cache;

import android.content.Context;
import android.support.annotation.NonNull;

import com.german.topphotoviewer.BlobLoader;

import java.io.File;
import java.util.concurrent.TimeUnit;

public class LastDayFileCache extends FileCache {
    private static final String TOP_PHOTOS_ENTITY = "topPhoto";

    public LastDayFileCache(@NonNull Context context) {
        super(BlobLoader.createCacheDirectory(context, TOP_PHOTOS_ENTITY));
    }

    @Override
    protected void onSave(@NonNull String savedKey) {
        File[] files = mCacheDir.listFiles();
        if (files == null || files.length == 0) {
            return;
        }
        for (File file : files) {
            if (file.lastModified() + TimeUnit.DAYS.toMillis(1) < System.currentTimeMillis()) {
                //noinspection ResultOfMethodCallIgnored
                file.delete();
            }
        }
    }
}
