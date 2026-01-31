package com.copiloto.addblock.util;

import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Threading utilities for background and UI thread execution.
 */
public final class Threading {

    private static final Executor BACKGROUND_EXECUTOR = Executors.newFixedThreadPool(4);
    private static final Handler MAIN_HANDLER = new Handler(Looper.getMainLooper());

    private Threading() {
        // Utility class
    }

    /**
     * Execute a runnable on a background thread.
     */
    public static void runOnBackground(Runnable runnable) {
        BACKGROUND_EXECUTOR.execute(runnable);
    }

    /**
     * Execute a runnable on the main/UI thread.
     */
    public static void runOnMain(Runnable runnable) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            runnable.run();
        } else {
            MAIN_HANDLER.post(runnable);
        }
    }

    /**
     * Execute a runnable on the main thread with a delay.
     */
    public static void runOnMainDelayed(Runnable runnable, long delayMillis) {
        MAIN_HANDLER.postDelayed(runnable, delayMillis);
    }

    /**
     * Get the background executor for custom use.
     */
    public static Executor getBackgroundExecutor() {
        return BACKGROUND_EXECUTOR;
    }
}
