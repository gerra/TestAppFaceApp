package com.german.topphotoviewer.cache;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.io.InputStream;

public abstract class Cache {
    public final boolean saveToCache(@NonNull InputStream is, @NonNull String key) {
        boolean saved = saveToCacheInner(is, key);
        onSave(key);
        return saved;
    }

    @Nullable
    public abstract InputStream loadFromCache(@NonNull String key);

    public abstract void deleteFromCache(@NonNull String key);

    public abstract boolean existsInCache(@NonNull String key);

    protected abstract boolean saveToCacheInner(@NonNull InputStream is, @NonNull String key);

    protected abstract void onSave(@NonNull String savedKey);
}
