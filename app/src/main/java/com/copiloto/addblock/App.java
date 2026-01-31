package com.copiloto.addblock;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

import com.copiloto.addblock.util.Logger;

/**
 * Application class for global initialization.
 */
public class App extends Application {

    private static final String TAG = "App";

    @Override
    public void onCreate() {
        super.onCreate();
        Logger.d(TAG, "Application created");

        createNotificationChannels();
    }

    private void createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel vpnChannel = new NotificationChannel(
                    "firewall_vpn_channel",
                    "Firewall VPN Service",
                    NotificationManager.IMPORTANCE_LOW
            );
            vpnChannel.setDescription("Shows when the firewall is active");
            vpnChannel.setShowBadge(false);

            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(vpnChannel);
            }
        }
    }
}
