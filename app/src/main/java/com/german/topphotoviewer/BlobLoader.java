package com.german.topphotoviewer;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.support.annotation.WorkerThread;
import android.text.TextUtils;
import android.util.Log;

import com.german.topphotoviewer.cache.Cache;
import com.german.topphotoviewer.cache.FileCache;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class BlobLoader<C extends Cache> {
    private static final String TAG = "[SL:BlobLoader]";

    private static final String STATIC_IMAGES_DIR = "imageCache";

    @NonNull
    private final C mCache;
    @Nullable
    private final Transformer<InputStream> mPreLoadTransformer;
    @Nullable
    private final Transformer<InputStream> mPostLoadTransformer;

    @NonNull
    public static File createCacheDirectory(@NonNull Context context, @NonNull String entriesType) {
        File cacheDir = new File(context.getCacheDir(), entriesType);
        if (!cacheDir.exists()) {
            //noinspection ResultOfMethodCallIgnored
            cacheDir.mkdirs();
        }
        return cacheDir;
    }

    @NonNull
    public static FileCache createStaticImagesFileCache(@NonNull Context context) {
        return new DummyFileCache(createCacheDirectory(context, STATIC_IMAGES_DIR));
    }

    BlobLoader(@NonNull C cache,
               @Nullable Transformer<InputStream> preLoadTransformer,
               @Nullable Transformer<InputStream> postLoadTransformer) {
        mCache = cache;
        mPreLoadTransformer = preLoadTransformer;
        mPostLoadTransformer = postLoadTransformer;
    }

    @WorkerThread
    public <T> void load(@NonNull Context context, @NonNull String url, @NonNull Transformer<T> transformer, @NonNull Consumer<T> consumer) {
        if (TextUtils.isEmpty(url)) {
            consumer.onError(new IllegalArgumentException("Empty url"));
            return;
        }
        InputStream is = null;
        try {
            is = mCache.loadFromCache(url);
            if (is != null) {
                is = transformAndConsume(is, transformer, consumer);
                Log.d(TAG, String.format("Blob %s loaded from cache.", url));
            } else {
                Log.e(TAG, String.format("Load %s from cache failed. Trying to load from network...", url));
                loadFromNetwork(context, url, transformer, consumer);
                Log.d(TAG, String.format("Blob %s loaded from network.", url));
            }
        } finally {
            Utils.closeSilently(is);
        }
    }

    public <T> void loadFromCache(@NonNull String url, @NonNull Transformer<T> transformer, @NonNull Consumer<T> consumer) {
        InputStream is = null;
        try {
            is = mCache.loadFromCache(url);
            if (is != null) {
                is = transformAndConsume(is, transformer, consumer);
                Log.d(TAG, String.format("Blob %s loaded from cache.", url));
            } else {
                consumer.onError(new CacheException("There is no entry with key=" + url + " in cache"));
            }
        } finally {
            Utils.closeSilently(is);
        }
    }

    @NonNull
    public C getCache() {
        return mCache;
    }

    @VisibleForTesting
    @WorkerThread
    <T> void loadFromNetwork(@NonNull Context context,
                             @NonNull String url,
                             @NonNull Transformer<T> transformer,
                             @NonNull Consumer<T> consumer) {
        if (!Utils.isConnectedOrConnecting(context)) {
            consumer.onError(new DownloadException("No internet connection"));
            return;
        }

        HttpURLConnection connection = null;
        try {
            connection = createConnection(url);

            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                InputStream actualInputStream = null;
                try {
                    actualInputStream = mPreLoadTransformer != null
                            ? mPreLoadTransformer.transform(connection.getInputStream())
                            : connection.getInputStream();
                    if (actualInputStream == null) {
                        consumer.onError(new TransformException("PreLoadTransformer returned null"));
                    } else if (mCache.saveToCache(actualInputStream, url)) {
                        loadFromCache(url, transformer, consumer);
                    } else {
                        consumer.onError(new CacheException("Error while saving in cache"));
                    }
                } finally {
                    Utils.closeSilently(actualInputStream);
                }
            } else {
                consumer.onError(new DownloadException("Bad response code"));
            }
        } catch (IOException e) {
            consumer.onError(new DownloadException(e));
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    @VisibleForTesting
    @WorkerThread
    @NonNull
    HttpURLConnection createConnection(@NonNull String url) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        connection.connect();
        return connection;
    }

    @Nullable
    private <T> InputStream transformAndConsume(@NonNull InputStream inputStream,
                                                @NonNull Transformer<T> transformer,
                                                @NonNull Consumer<T> consumer) {
        InputStream actualInputStream = null;
        try {
            actualInputStream = mPostLoadTransformer != null
                    ? mPostLoadTransformer.transform(inputStream)
                    : inputStream;
            if (actualInputStream == null) {
                consumer.onError(new TransformException("PostLoadTransformer returned null"));
            } else {
                T transformed = transformer.transform(actualInputStream);
                if (transformed instanceof InputStream) {
                    actualInputStream = (InputStream) transformed;
                }
                consumer.onData(transformed);
            }
        } catch (OutOfMemoryError oom) {
            consumer.onError(oom);
        }
        return actualInputStream;
    }

    public static class Builder<C extends Cache> {
        @NonNull
        private final C mCache;
        @Nullable
        private Transformer<InputStream> mPreLoadTransformer;
        @Nullable
        private Transformer<InputStream> mPostLoadTransformer;

        public Builder(@NonNull C cache) {
            mCache = cache;
        }

        @NonNull
        public Builder<C> preLoadTransformer(@NonNull Transformer<InputStream> preLoadTransformer) {
            mPreLoadTransformer = preLoadTransformer;
            return this;
        }

        @NonNull
        public Builder<C> postLoadTransformer(@NonNull Transformer<InputStream> preLoadTransformer) {
            mPostLoadTransformer = preLoadTransformer;
            return this;
        }

        @NonNull
        public BlobLoader<C> build() {
            return new BlobLoader<>(mCache, mPreLoadTransformer, mPostLoadTransformer);
        }
    }

    public interface Transformer<T> {
        Transformer EMPTY = new Transformer() {
            @Nullable
            @Override
            public Object transform(@NonNull InputStream input) {
                try {
                    return null;
                } finally {
                    Utils.closeSilently(input);
                }
            }
        };

        Transformer<Bitmap> IMAGE_TRANSFORMER = new Transformer<Bitmap>() {
            @Nullable
            @Override
            public Bitmap transform(@NonNull InputStream input) {
                return BitmapFactory.decodeStream(input);
            }
        };

        /**
         * Transform input stream to Object.
         * If type parameter T is InputStream and returned InputStream is not a wrapper on original input stream
         * (and not the original input stream itself), override method should close the stream. Because of caller
         * always closes a returned InputStream.
         *
         * @param input {@link InputStream} to transform
         * @return transformed from input stream object
         */
        @Nullable
        T transform(@NonNull InputStream input);
    }

    public interface Consumer<T> {
        Consumer EMPTY = new Consumer() {
            @Override
            public void onData(@Nullable Object data) {
            }

            @Override
            public void onError(@NonNull Throwable error) {
            }
        };

        void onData(@Nullable T data);

        void onError(@NonNull Throwable error);
    }

    public static class DownloadException extends Exception {
        public DownloadException(@Nullable String detailMessage) {
            super(detailMessage);
        }

        public DownloadException(@Nullable Throwable throwable) {
            super(throwable);
        }
    }

    public static class CacheException extends Exception {
        public CacheException(String message) {
            super(message);
        }
    }

    public static class TransformException extends Exception {
        public TransformException(String message) {
            super(message);
        }
    }

    private static class DummyFileCache extends FileCache {

        public DummyFileCache(@NonNull File cacheDir) {
            super(cacheDir);
        }

        @Override
        protected void onSave(@NonNull String savedKey) {
            // do nothing
        }
    }
}
