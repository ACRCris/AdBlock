package com.copiloto.addblock.vpn;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ServiceInfo;
import android.net.VpnService;
import android.os.Build;
import android.os.ParcelFileDescriptor;

import androidx.core.app.NotificationCompat;
import androidx.core.app.ServiceCompat;

import com.copiloto.addblock.BuildConfig;
import com.copiloto.addblock.R;
import com.copiloto.addblock.data.BlocklistRepository;
import com.copiloto.addblock.ui.MainActivity;
import com.copiloto.addblock.util.Logger;

import java.io.IOException;
import java.util.Set;

/**
 * VPN Service that blocks network traffic for selected apps.
 *
 * Strategy: Use addAllowedApplication() to capture ONLY the blocked apps
 * into the VPN tunnel. Since we don't forward any packets, these apps
 * effectively have no network access.
 */
public class FirewallVpnService extends VpnService {

    private static final String TAG = "FirewallVpnService";
    private static final String CHANNEL_ID = "firewall_vpn_channel";
    private static final int NOTIFICATION_ID = 1;

    public static final String ACTION_START = "com.copiloto.addblock.START_VPN";
    public static final String ACTION_STOP = "com.copiloto.addblock.STOP_VPN";

    private ParcelFileDescriptor vpnInterface;
    private BlocklistRepository blocklistRepository;

    private static VpnState currentState = VpnState.STOPPED;
    private static StateListener stateListener;

    public interface StateListener {
        void onStateChanged(VpnState state);
    }

    public static void setStateListener(StateListener listener) {
        stateListener = listener;
    }

    public static VpnState getCurrentState() {
        return currentState;
    }

    private void updateState(VpnState state) {
        currentState = state;
        if (stateListener != null) {
            stateListener.onStateChanged(state);
        }
        Logger.d(TAG, "State changed to: " + state);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        blocklistRepository = new BlocklistRepository(this);
        createNotificationChannel();
        Logger.d(TAG, "Service created");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // CRITICAL: Call startForeground IMMEDIATELY to avoid ForegroundServiceDidNotStartInTimeException
        // This must happen before any other logic when started with startForegroundService()
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                startForeground(NOTIFICATION_ID, createNotification(),
                        ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE);
            } else {
                startForeground(NOTIFICATION_ID, createNotification());
            }
        } catch (Exception e) {
            Logger.e(TAG, "Error starting foreground", e);
        }

        if (intent == null) {
            stopSelf();
            return START_NOT_STICKY;
        }

        String action = intent.getAction();
        if (ACTION_STOP.equals(action)) {
            stopVpn();
            return START_NOT_STICKY;
        }

        if (ACTION_START.equals(action)) {
            startVpn();
        }

        return START_STICKY;
    }

    private void startVpn() {
        updateState(VpnState.STARTING);

        Set<String> blockedPackages = blocklistRepository.getBlockedPackages();

        if (blockedPackages.isEmpty()) {
            Logger.w(TAG, "No apps to block, stopping service");
            updateState(VpnState.STOPPED);
            stopVpn();
            return;
        }

        try {

            Builder builder = new Builder();

            // Configure the VPN interface
            builder.setSession("AdBlock Firewall")
                    .addAddress("10.0.0.2", 32)
                    .addRoute("0.0.0.0", 0)
                    .addDnsServer("1.1.1.1")
                    .setBlocking(true);

            // Add ONLY the blocked apps to the VPN tunnel
            // These apps will be "captured" and since we don't forward packets,
            // they effectively have no network access
            // Note: We use addAllowedApplication ONLY - cannot mix with addDisallowedApplication
            int addedCount = 0;
            for (String packageName : blockedPackages) {
                // Skip our own app if somehow it's in the list
                if (packageName.equals(getPackageName())) {
                    continue;
                }
                try {
                    builder.addAllowedApplication(packageName);
                    addedCount++;
                    // Only log package names in debug builds for privacy
                    if (BuildConfig.DEBUG) {
                        Logger.d(TAG, "Blocking: " + packageName);
                    }
                } catch (PackageManager.NameNotFoundException e) {
                    Logger.w(TAG, "Package not found: " + packageName);
                }
            }

            if (addedCount == 0) {
                Logger.w(TAG, "No valid packages to block");
                updateState(VpnState.STOPPED);
                stopSelf();
                return;
            }

            vpnInterface = builder.establish();

            if (vpnInterface != null) {
                updateState(VpnState.RUNNING);
                Logger.i(TAG, "VPN established, blocking " + addedCount + " apps");
            } else {
                updateState(VpnState.ERROR);
                Logger.e(TAG, "Failed to establish VPN interface");
                stopSelf();
            }

        } catch (Exception e) {
            Logger.e(TAG, "Error starting VPN", e);
            updateState(VpnState.ERROR);
            stopSelf();
        }
    }

    private void stopVpn() {
        Logger.d(TAG, "Stopping VPN");

        if (vpnInterface != null) {
            try {
                vpnInterface.close();
            } catch (IOException e) {
                Logger.e(TAG, "Error closing VPN interface", e);
            }
            vpnInterface = null;
        }

        updateState(VpnState.STOPPED);
        ServiceCompat.stopForeground(this, ServiceCompat.STOP_FOREGROUND_REMOVE);
        stopSelf();
    }

    @Override
    public void onDestroy() {
        stopVpn();
        super.onDestroy();
        Logger.d(TAG, "Service destroyed");
    }

    @Override
    public void onRevoke() {
        Logger.w(TAG, "VPN permission revoked");
        stopVpn();
        super.onRevoke();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Firewall VPN Service",
                    NotificationManager.IMPORTANCE_LOW
            );
            channel.setDescription("Shows when the firewall is active");

            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }
    }

    private Notification createNotification() {
        // Intent to open the app
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                this, 0, notificationIntent,
                PendingIntent.FLAG_IMMUTABLE
        );

        // Intent to stop the VPN (kill switch)
        Intent stopIntent = new Intent(this, FirewallVpnService.class);
        stopIntent.setAction(ACTION_STOP);
        PendingIntent stopPendingIntent = PendingIntent.getService(
                this, 0, stopIntent,
                PendingIntent.FLAG_IMMUTABLE
        );

        int blockedCount = blocklistRepository.getBlockedCount();

        return new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Firewall Active")
                .setContentText("Blocking " + blockedCount + " app(s)")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentIntent(pendingIntent)
                .addAction(android.R.drawable.ic_menu_close_clear_cancel, "Detener", stopPendingIntent)
                .setOngoing(true)
                .build();
    }
}
