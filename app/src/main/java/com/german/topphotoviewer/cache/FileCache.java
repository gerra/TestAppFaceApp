package com.german.topphotoviewer.cache;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.german.topphotoviewer.Utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public abstract class FileCache extends Cache {
    private static final String TAG = "[SL:FileCache]";

    private static final int BUFFER_SIZE = 8 * 1024;

    @NonNull
    protected final File mCacheDir;

    public FileCache(@NonNull File cacheDir) {
        super();
        mCacheDir = cacheDir;
    }

    @Override
    public boolean existsInCache(@NonNull String key) {
        return getCacheFile(key).exists();
    }

    @Override
    protected boolean saveToCacheInner(@NonNull InputStream is, @NonNull String key) {
        if (!mCacheDir.exists() && !mCacheDir.mkdirs()) {
            return false;
        }

        File cacheFile = getCacheFile(key);
        if (!cacheFile.exists()) {
            try {
                if (!cacheFile.createNewFile()) {
                    return false;
                }
            } catch (IOException e) {
                return false;
            }
        }

        OutputStream os;
        try {
            os = new BufferedOutputStream(new FileOutputStream(cacheFile));
        } catch (FileNotFoundException ignored) {
            Log.e(TAG, "File not found. Wtf? We just created it");
            return false;
        }

        try {
            int bytesRead;
            byte[] buffer = new byte[BUFFER_SIZE];
            while ((bytesRead = is.read(buffer, 0, buffer.length)) >= 0) {
                os.write(buffer, 0, bytesRead);
            }
        } catch (IOException e) {
            //noinspection ResultOfMethodCallIgnored
            cacheFile.delete();
            return false;
        } finally {
            Utils.closeSilently(os);
        }

        return true;
    }

    @Nullable
    @Override
    public InputStream loadFromCache(@NonNull String key) {
        if (!existsInCache(key)) {
            return null;
        }
        File cacheFile = getCacheFile(key);
        if (!cacheFile.exists()) {
            return null;
        }
        try {
            return new BufferedInputStream(new FileInputStream(cacheFile));
        } catch (FileNotFoundException e) {
            Log.e(TAG, "File not found. Wtf? We just checked it exists in cache");
            return null;
        }
    }

    @Override
    public void deleteFromCache(@NonNull String key) {
        //noinspection ResultOfMethodCallIgnored
        getCacheFile(key).delete();
    }

    @NonNull
    public File getCacheFile(@NonNull String key) {
        return new File(mCacheDir, getCacheFileName(key));
    }

    @NonNull
    protected String getCacheFileName(@NonNull String key) {
        return Utils.generateFileName(key);
    }
}
