package com.copiloto.addblock.data;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.util.HashSet;
import java.util.Set;

/**
 * Unit tests for Preferences using Robolectric.
 * Tests SharedPreferences operations with in-memory storage.
 */
@RunWith(RobolectricTestRunner.class)
@Config(sdk = 36)
public class PreferencesTest {

    private Preferences preferences;

    @Before
    public void setUp() {
        var context = RuntimeEnvironment.getApplication();
        // Clear any existing preferences before each test
        context.getSharedPreferences("adblock_prefs", 0)
               .edit().clear().apply();

        preferences = new Preferences(context);
    }

    // ==================== getBlockedPackages() Tests ====================

    @Test
    public void getBlockedPackages_returnsEmptySet_byDefault() {
        // Act
        Set<String> result = preferences.getBlockedPackages();

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    public void getBlockedPackages_returnsDefensiveCopy() {
        // Arrange
        preferences.addBlockedPackage("com.example.app");

        // Act
        Set<String> result1 = preferences.getBlockedPackages();
        result1.add("com.example.modified");
        Set<String> result2 = preferences.getBlockedPackages();

        // Assert - modification to result1 should not affect stored data
        assertEquals(1, result2.size());
        assertFalse(result2.contains("com.example.modified"));
    }

    // ==================== setBlockedPackages() Tests ====================

    @Test
    public void setBlockedPackages_storesPackagesCorrectly() {
        // Arrange
        Set<String> packages = new HashSet<>();
        packages.add("com.example.app1");
        packages.add("com.example.app2");

        // Act
        preferences.setBlockedPackages(packages);
        Set<String> result = preferences.getBlockedPackages();

        // Assert
        assertEquals(2, result.size());
        assertTrue(result.contains("com.example.app1"));
        assertTrue(result.contains("com.example.app2"));
    }

    @Test
    public void setBlockedPackages_overwritesExistingPackages() {
        // Arrange
        Set<String> initialPackages = new HashSet<>();
        initialPackages.add("com.example.old");
        preferences.setBlockedPackages(initialPackages);

        Set<String> newPackages = new HashSet<>();
        newPackages.add("com.example.new");

        // Act
        preferences.setBlockedPackages(newPackages);
        Set<String> result = preferences.getBlockedPackages();

        // Assert
        assertEquals(1, result.size());
        assertTrue(result.contains("com.example.new"));
        assertFalse(result.contains("com.example.old"));
    }

    @Test
    public void setBlockedPackages_handlesEmptySet() {
        // Arrange
        preferences.addBlockedPackage("com.example.app");

        // Act
        preferences.setBlockedPackages(new HashSet<>());
        Set<String> result = preferences.getBlockedPackages();

        // Assert
        assertTrue(result.isEmpty());
    }

    // ==================== addBlockedPackage() Tests ====================

    @Test
    public void addBlockedPackage_addsNewPackage() {
        // Act
        preferences.addBlockedPackage("com.example.newapp");

        // Assert
        assertTrue(preferences.isBlocked("com.example.newapp"));
        assertEquals(1, preferences.getBlockedPackages().size());
    }

    @Test
    public void addBlockedPackage_doesNotDuplicateExisting() {
        // Arrange
        preferences.addBlockedPackage("com.example.app");

        // Act
        preferences.addBlockedPackage("com.example.app");

        // Assert
        assertEquals(1, preferences.getBlockedPackages().size());
    }

    @Test
    public void addBlockedPackage_addsMultiplePackages() {
        // Act
        preferences.addBlockedPackage("com.example.app1");
        preferences.addBlockedPackage("com.example.app2");
        preferences.addBlockedPackage("com.example.app3");

        // Assert
        assertEquals(3, preferences.getBlockedPackages().size());
    }

    // ==================== removeBlockedPackage() Tests ====================

    @Test
    public void removeBlockedPackage_removesExistingPackage() {
        // Arrange
        preferences.addBlockedPackage("com.example.app");

        // Act
        preferences.removeBlockedPackage("com.example.app");

        // Assert
        assertFalse(preferences.isBlocked("com.example.app"));
        assertTrue(preferences.getBlockedPackages().isEmpty());
    }

    @Test
    public void removeBlockedPackage_doesNothingForNonExistent() {
        // Arrange
        preferences.addBlockedPackage("com.example.existing");

        // Act
        preferences.removeBlockedPackage("com.example.nonexistent");

        // Assert
        assertEquals(1, preferences.getBlockedPackages().size());
        assertTrue(preferences.isBlocked("com.example.existing"));
    }

    @Test
    public void removeBlockedPackage_handlesEmptyList() {
        // Act - should not throw
        preferences.removeBlockedPackage("com.example.app");

        // Assert
        assertTrue(preferences.getBlockedPackages().isEmpty());
    }

    // ==================== isBlocked() Tests ====================

    @Test
    public void isBlocked_returnsTrue_forBlockedPackage() {
        // Arrange
        preferences.addBlockedPackage("com.example.blocked");

        // Act & Assert
        assertTrue(preferences.isBlocked("com.example.blocked"));
    }

    @Test
    public void isBlocked_returnsFalse_forUnblockedPackage() {
        // Act & Assert
        assertFalse(preferences.isBlocked("com.example.unblocked"));
    }

    @Test
    public void isBlocked_returnsFalse_afterRemoval() {
        // Arrange
        preferences.addBlockedPackage("com.example.app");
        preferences.removeBlockedPackage("com.example.app");

        // Act & Assert
        assertFalse(preferences.isBlocked("com.example.app"));
    }

    // ==================== isFirewallEnabled() Tests ====================

    @Test
    public void isFirewallEnabled_returnsFalse_byDefault() {
        // Act & Assert
        assertFalse(preferences.isFirewallEnabled());
    }

    @Test
    public void isFirewallEnabled_returnsTrue_afterEnabled() {
        // Arrange
        preferences.setFirewallEnabled(true);

        // Act & Assert
        assertTrue(preferences.isFirewallEnabled());
    }

    // ==================== setFirewallEnabled() Tests ====================

    @Test
    public void setFirewallEnabled_storesTrue() {
        // Act
        preferences.setFirewallEnabled(true);

        // Assert
        assertTrue(preferences.isFirewallEnabled());
    }

    @Test
    public void setFirewallEnabled_storesFalse() {
        // Arrange
        preferences.setFirewallEnabled(true);

        // Act
        preferences.setFirewallEnabled(false);

        // Assert
        assertFalse(preferences.isFirewallEnabled());
    }

    @Test
    public void setFirewallEnabled_persistsAcrossInstances() {
        // Arrange
        preferences.setFirewallEnabled(true);

        // Act - Create new instance
        Preferences newPreferences = new Preferences(RuntimeEnvironment.getApplication());

        // Assert
        assertTrue(newPreferences.isFirewallEnabled());
    }

    // ==================== Persistence Tests ====================

    @Test
    public void blockedPackages_persistAcrossInstances() {
        // Arrange
        preferences.addBlockedPackage("com.example.persistent");

        // Act - Create new instance
        Preferences newPreferences = new Preferences(RuntimeEnvironment.getApplication());

        // Assert
        assertTrue(newPreferences.isBlocked("com.example.persistent"));
    }

    @Test
    public void multipleOperations_workCorrectly() {
        // Perform a series of operations
        preferences.addBlockedPackage("com.example.app1");
        preferences.addBlockedPackage("com.example.app2");
        preferences.addBlockedPackage("com.example.app3");
        preferences.removeBlockedPackage("com.example.app2");
        preferences.setFirewallEnabled(true);

        // Assert final state
        Set<String> blocked = preferences.getBlockedPackages();
        assertEquals(2, blocked.size());
        assertTrue(blocked.contains("com.example.app1"));
        assertFalse(blocked.contains("com.example.app2"));
        assertTrue(blocked.contains("com.example.app3"));
        assertTrue(preferences.isFirewallEnabled());
    }
}
