package com.copiloto.addblock.util;

import android.util.Log;

import com.copiloto.addblock.BuildConfig;

/**
 * Centralized logging utility for debugging.
 * In release builds, debug logs are disabled by default.
 */
public final class Logger {

    private static final String TAG = "AdBlock";
    private static boolean debugEnabled = BuildConfig.DEBUG;

    private Logger() {
        // Utility class
    }

    public static void setDebugEnabled(boolean enabled) {
        debugEnabled = enabled;
    }

    public static void d(String message) {
        if (debugEnabled) {
            Log.d(TAG, message);
        }
    }

    public static void d(String tag, String message) {
        if (debugEnabled) {
            Log.d(TAG + "/" + tag, message);
        }
    }

    public static void i(String message) {
        Log.i(TAG, message);
    }

    public static void i(String tag, String message) {
        Log.i(TAG + "/" + tag, message);
    }

    public static void w(String message) {
        Log.w(TAG, message);
    }

    public static void w(String tag, String message) {
        Log.w(TAG + "/" + tag, message);
    }

    public static void e(String message) {
        Log.e(TAG, message);
    }

    public static void e(String tag, String message) {
        Log.e(TAG + "/" + tag, message);
    }

    public static void e(String message, Throwable throwable) {
        Log.e(TAG, message, throwable);
    }

    public static void e(String tag, String message, Throwable throwable) {
        Log.e(TAG + "/" + tag, message, throwable);
    }
}
