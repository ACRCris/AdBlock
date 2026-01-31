package com.copiloto.addblock.data;

import android.content.Context;

import java.util.Set;

/**
 * Repository for managing the blocklist of apps.
 * Provides a clean API for blocklist operations.
 */
public class BlocklistRepository {

    private final Preferences preferences;

    public BlocklistRepository(Context context) {
        this.preferences = new Preferences(context);
    }

    public BlocklistRepository(Preferences preferences) {
        this.preferences = preferences;
    }

    /**
     * Get all blocked package names.
     */
    public Set<String> getBlockedPackages() {
        return preferences.getBlockedPackages();
    }

    /**
     * Set the blocked state for a package.
     *
     * @param packageName Package to block/unblock
     * @param blocked     true to block, false to unblock
     */
    public void setBlocked(String packageName, boolean blocked) {
        if (blocked) {
            preferences.addBlockedPackage(packageName);
        } else {
            preferences.removeBlockedPackage(packageName);
        }
    }

    /**
     * Check if a package is blocked.
     */
    public boolean isBlocked(String packageName) {
        return preferences.isBlocked(packageName);
    }

    /**
     * Get the count of blocked packages.
     */
    public int getBlockedCount() {
        return preferences.getBlockedPackages().size();
    }

    /**
     * Clear all blocked packages.
     */
    public void clearAll() {
        preferences.setBlockedPackages(new java.util.HashSet<>());
    }
}
