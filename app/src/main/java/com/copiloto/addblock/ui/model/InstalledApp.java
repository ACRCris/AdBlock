package com.copiloto.addblock.ui.model;

import android.graphics.drawable.Drawable;

/**
 * Data Transfer Object representing an installed application.
 */
public class InstalledApp {

    private final String label;
    private final String packageName;
    private final Drawable icon;
    private final boolean systemApp;
    private boolean blocked;

    public InstalledApp(String label, String packageName, Drawable icon) {
        this(label, packageName, icon, false, false);
    }

    public InstalledApp(String label, String packageName, Drawable icon, boolean blocked) {
        this(label, packageName, icon, blocked, false);
    }

    public InstalledApp(String label, String packageName, Drawable icon, boolean blocked, boolean systemApp) {
        this.label = label;
        this.packageName = packageName;
        this.icon = icon;
        this.blocked = blocked;
        this.systemApp = systemApp;
    }

    public String getLabel() {
        return label;
    }

    // Alias for Kotlin compatibility
    public String getAppName() {
        return label;
    }

    public String getPackageName() {
        return packageName;
    }

    public Drawable getIcon() {
        return icon;
    }

    public boolean isBlocked() {
        return blocked;
    }

    public boolean getBlocked() {
        return blocked;
    }

    public void setBlocked(boolean blocked) {
        this.blocked = blocked;
    }

    public boolean isSystemApp() {
        return systemApp;
    }

    public boolean getSystemApp() {
        return systemApp;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        InstalledApp that = (InstalledApp) o;
        return packageName.equals(that.packageName);
    }

    @Override
    public int hashCode() {
        return packageName.hashCode();
    }
}
