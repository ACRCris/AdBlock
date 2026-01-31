package com.copiloto.addblock.data;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashSet;
import java.util.Set;

/**
 * Wrapper over SharedPreferences for app settings persistence.
 */
public class Preferences {

    private static final String PREFS_NAME = "adblock_prefs";
    private static final String KEY_BLOCKED_PACKAGES = "blocked_packages";
    private static final String KEY_FIREWALL_ENABLED = "firewall_enabled";

    private final SharedPreferences prefs;

    public Preferences(Context context) {
        this.prefs = context.getApplicationContext()
                .getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    /**
     * Get the set of blocked package names.
     */
    public Set<String> getBlockedPackages() {
        return new HashSet<>(prefs.getStringSet(KEY_BLOCKED_PACKAGES, new HashSet<>()));
    }

    /**
     * Save the set of blocked package names.
     */
    public void setBlockedPackages(Set<String> packages) {
        prefs.edit()
                .putStringSet(KEY_BLOCKED_PACKAGES, new HashSet<>(packages))
                .apply();
    }

    /**
     * Add a package to the blocked list.
     */
    public void addBlockedPackage(String packageName) {
        Set<String> blocked = getBlockedPackages();
        blocked.add(packageName);
        setBlockedPackages(blocked);
    }

    /**
     * Remove a package from the blocked list.
     */
    public void removeBlockedPackage(String packageName) {
        Set<String> blocked = getBlockedPackages();
        blocked.remove(packageName);
        setBlockedPackages(blocked);
    }

    /**
     * Check if a package is blocked.
     */
    public boolean isBlocked(String packageName) {
        return getBlockedPackages().contains(packageName);
    }

    /**
     * Get firewall enabled state.
     */
    public boolean isFirewallEnabled() {
        return prefs.getBoolean(KEY_FIREWALL_ENABLED, false);
    }

    /**
     * Set firewall enabled state.
     */
    public void setFirewallEnabled(boolean enabled) {
        prefs.edit()
                .putBoolean(KEY_FIREWALL_ENABLED, enabled)
                .apply();
    }
}
