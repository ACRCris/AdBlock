package com.copiloto.addblock.vpn;

import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.copiloto.addblock.data.BlocklistRepository;
import com.copiloto.addblock.util.Logger;

import java.util.Set;

/**
 * Helper class to control the VPN service.
 */
public final class VpnController {

    private static final String TAG = "VpnController";

    private VpnController() {
        // Utility class
    }

    /**
     * Start the VPN service.
     *
     * @param context Application context
     */
    public static void start(Context context) {
        Logger.d(TAG, "Starting VPN service");
        Intent intent = new Intent(context, FirewallVpnService.class);
        intent.setAction(FirewallVpnService.ACTION_START);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intent);
        } else {
            context.startService(intent);
        }
    }

    /**
     * Stop the VPN service.
     *
     * @param context Application context
     */
    public static void stop(Context context) {
        Logger.d(TAG, "Stopping VPN service");
        Intent intent = new Intent(context, FirewallVpnService.class);
        intent.setAction(FirewallVpnService.ACTION_STOP);
        context.startService(intent);
    }

    /**
     * Restart the VPN service if it's currently running.
     * Useful when the blocklist changes.
     * If no apps are blocked, stops the VPN instead of restarting.
     *
     * @param context Application context
     */
    public static void restartIfRunning(Context context) {
        if (isRunning()) {
            // Check if there are still blocked apps
            BlocklistRepository repository = new BlocklistRepository(context);
            Set<String> blockedPackages = repository.getBlockedPackages();

            if (blockedPackages == null || blockedPackages.isEmpty()) {
                // No apps to block, just stop the VPN
                Logger.d(TAG, "No apps blocked, stopping VPN");
                stop(context);
                return;
            }

            Logger.d(TAG, "Restarting VPN service");
            stop(context);
            // Small delay to ensure service stops before restarting
            new android.os.Handler(android.os.Looper.getMainLooper())
                    .postDelayed(() -> start(context), 500);
        }
    }

    /**
     * Check if the VPN is currently running.
     */
    public static boolean isRunning() {
        return FirewallVpnService.getCurrentState() == VpnState.RUNNING;
    }

    /**
     * Get the current VPN state.
     */
    public static VpnState getState() {
        return FirewallVpnService.getCurrentState();
    }
}
