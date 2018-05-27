package com.german.topphotoviewer;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.Closeable;
import java.io.IOException;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

//TODO : create FileUtils/NetworkUtils/etc.
public class Utils {
    private static final String TAG = "[Utils]";

    private static final char[] HEXES = "0123456789ABCDEF".toCharArray();

    private Utils() {
        // no instance
    }

    public static void closeSilently(@Nullable Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (IOException ignored) {
            }
        }
    }

    @NonNull
    public static String generateFileName(@NonNull String url) {
        String md5FileName = generateMD5HexString(url);
        return md5FileName != null
                ? md5FileName
                : String.format("%02X", url.hashCode());
    }

    @Nullable
    private static String generateMD5HexString(@NonNull String generateFrom) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            messageDigest.update(generateFrom.getBytes(Charset.forName("UTF-8")));
            return bytesToHex(messageDigest.digest());
        } catch (NoSuchAlgorithmException e) {
            // Something weird. No MD5?
            Log.e(TAG, e.getMessage(), e);
        }
        return null;
    }

    @Nullable
    private static String bytesToHex(@Nullable byte[] bytes) {
        if (bytes != null) {
            char[] hexChars = new char[bytes.length * 2];
            for (int j = 0; j < bytes.length; j++) {
                int v = bytes[j] & 0xFF;
                hexChars[j * 2] = HEXES[v >>> 4];
                hexChars[j * 2 + 1] = HEXES[v & 0x0F];
            }
            return new String(hexChars);
        }

        return null;
    }

    public static final int NETWORK_UNAVAILABLE = 0;
    public static final int NETWORK_CONNECTED = 1;
    public static final int NETWORK_CONNECTING = 2;

    @NetworkState
    public static int getNetworkState(@NonNull Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            try {
                NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
                if (networkInfo != null) {
                    switch (networkInfo.getState()) {
                        case CONNECTED:
                            return NETWORK_CONNECTED;
                        case CONNECTING:
                            return NETWORK_CONNECTING;
                        default:
                            return NETWORK_UNAVAILABLE;
                    }
                }
            } catch (SecurityException e) {
                Log.e(TAG, "", e);
                // ConnectivityManager.getActiveNetworkInfo() may throw SecurityException if our process has no
                // required permission (possibly in Cyanogen (or whatever else) Android OS)
            }
        }

        return NETWORK_UNAVAILABLE;
    }

    public static boolean isConnected(@NonNull Context context) {
        int networkState = getNetworkState(context);
        return networkState == NETWORK_CONNECTED;
    }

    public static boolean isConnectedOrConnecting(@NonNull Context context) {
        int networkState = getNetworkState(context);
        return networkState == NETWORK_CONNECTED || networkState == NETWORK_CONNECTING;
    }

    public static boolean isServerError(int error) {
        return error % 100 == 5;
    }

    @Retention(RetentionPolicy.SOURCE)
    @IntDef(value = {NETWORK_UNAVAILABLE, NETWORK_CONNECTED, NETWORK_CONNECTING})
    public @interface NetworkState {
    }
}
