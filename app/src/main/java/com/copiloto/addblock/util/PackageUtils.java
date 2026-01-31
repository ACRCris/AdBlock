package com.copiloto.addblock.util;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;

import com.copiloto.addblock.ui.model.InstalledApp;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Utilities for loading installed applications.
 */
public final class PackageUtils {

    private PackageUtils() {
        // Utility class
    }

    /**
     * Get list of installed user apps (apps with launcher icon).
     * This includes apps from Play Store and excludes pure system services.
     *
     * @param context Application context
     * @return List of InstalledApp sorted by label
     */
    public static List<InstalledApp> getInstalledUserApps(Context context) {
        PackageManager pm = context.getPackageManager();
        String ownPackage = context.getPackageName();

        // Get all apps that have a launcher activity (appear in app drawer)
        Intent launcherIntent = new Intent(Intent.ACTION_MAIN);
        launcherIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        List<ResolveInfo> launcherApps = pm.queryIntentActivities(launcherIntent, 0);

        // Collect package names of launchable apps
        Set<String> launchablePackages = new HashSet<>();
        for (ResolveInfo info : launcherApps) {
            launchablePackages.add(info.activityInfo.packageName);
        }

        List<InstalledApp> userApps = new ArrayList<>();
        List<ApplicationInfo> apps = pm.getInstalledApplications(PackageManager.GET_META_DATA);

        for (ApplicationInfo appInfo : apps) {
            // Skip our own app
            if (appInfo.packageName.equals(ownPackage)) {
                continue;
            }

            // Only include apps that have a launcher (appear in app drawer)
            if (!launchablePackages.contains(appInfo.packageName)) {
                continue;
            }

            String label = pm.getApplicationLabel(appInfo).toString();
            String packageName = appInfo.packageName;
            Drawable icon = pm.getApplicationIcon(appInfo);
            boolean isSystemApp = (appInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0;

            InstalledApp installedApp = new InstalledApp(label, packageName, icon, false, isSystemApp);
            userApps.add(installedApp);
        }

        // Sort alphabetically by label
        Collections.sort(userApps, (a, b) -> a.getLabel().compareToIgnoreCase(b.getLabel()));

        Logger.d("PackageUtils", "Found " + userApps.size() + " launchable apps");
        return userApps;
    }

    /**
     * Check if a package is installed.
     */
    public static boolean isPackageInstalled(Context context, String packageName) {
        try {
            context.getPackageManager().getPackageInfo(packageName, 0);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }
}
