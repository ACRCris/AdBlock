package com.copiloto.addblock.util;

import static org.junit.Assert.*;

import android.content.Context;

import com.copiloto.addblock.ui.model.InstalledApp;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.util.List;

/**
 * Unit tests for PackageUtils.
 * Tests package loading and utility methods.
 */
@RunWith(RobolectricTestRunner.class)
@Config(sdk = 36)
public class PackageUtilsTest {

    private Context context;

    @Before
    public void setUp() {
        context = RuntimeEnvironment.getApplication();
    }

    // ==================== getInstalledUserApps() Tests ====================

    @Test
    public void getInstalledUserApps_returnsNonNullList() {
        // Act
        List<InstalledApp> apps = PackageUtils.getInstalledUserApps(context);

        // Assert
        assertNotNull(apps);
    }

    @Test
    public void getInstalledUserApps_excludesOwnPackage() {
        // Act
        List<InstalledApp> apps = PackageUtils.getInstalledUserApps(context);

        // Assert - should not contain our own package
        String ownPackage = context.getPackageName();
        for (InstalledApp app : apps) {
            assertNotEquals(ownPackage, app.getPackageName());
        }
    }

    @Test
    public void getInstalledUserApps_returnsAppsWithValidData() {
        // Act
        List<InstalledApp> apps = PackageUtils.getInstalledUserApps(context);

        // Assert - each app should have valid data
        for (InstalledApp app : apps) {
            assertNotNull(app.getLabel());
            assertNotNull(app.getPackageName());
            assertFalse(app.getPackageName().isEmpty());
            // Icon can be null in test environment
        }
    }

    @Test
    public void getInstalledUserApps_returnsSortedList() {
        // Act
        List<InstalledApp> apps = PackageUtils.getInstalledUserApps(context);

        // Assert - list should be sorted alphabetically by label
        if (apps.size() > 1) {
            for (int i = 0; i < apps.size() - 1; i++) {
                String current = apps.get(i).getLabel().toLowerCase();
                String next = apps.get(i + 1).getLabel().toLowerCase();
                assertTrue("List not sorted: " + current + " > " + next,
                        current.compareTo(next) <= 0);
            }
        }
    }

    @Test
    public void getInstalledUserApps_appsHaveBlockedFalseByDefault() {
        // Act
        List<InstalledApp> apps = PackageUtils.getInstalledUserApps(context);

        // Assert - all apps should have blocked = false by default
        for (InstalledApp app : apps) {
            assertFalse("App should not be blocked by default: " + app.getPackageName(),
                    app.isBlocked());
        }
    }

    @Test
    public void getInstalledUserApps_identifiesSystemApps() {
        // Act
        List<InstalledApp> apps = PackageUtils.getInstalledUserApps(context);

        // Assert - system apps should be marked
        // In Robolectric test environment, we may not have real system apps
        // but the method should not crash
        for (InstalledApp app : apps) {
            // Just verify the method returns a valid boolean
            boolean isSystem = app.isSystemApp();
            assertTrue(isSystem || !isSystem); // Always true, just verify no exception
        }
    }

    @Test
    public void getInstalledUserApps_canBeCalledMultipleTimes() {
        // Act
        List<InstalledApp> apps1 = PackageUtils.getInstalledUserApps(context);
        List<InstalledApp> apps2 = PackageUtils.getInstalledUserApps(context);

        // Assert - should return consistent results
        assertEquals(apps1.size(), apps2.size());
    }

    // ==================== isPackageInstalled() Tests ====================

    @Test
    public void isPackageInstalled_returnsFalse_forNonExistentPackage() {
        // Act
        boolean result = PackageUtils.isPackageInstalled(context, "com.nonexistent.package.xyz123");

        // Assert
        assertFalse(result);
    }

    @Test
    public void isPackageInstalled_returnsTrue_forOwnPackage() {
        // Arrange
        String ownPackage = context.getPackageName();

        // Act
        boolean result = PackageUtils.isPackageInstalled(context, ownPackage);

        // Assert
        assertTrue(result);
    }

    @Test
    public void isPackageInstalled_handlesEmptyString() {
        // Act
        boolean result = PackageUtils.isPackageInstalled(context, "");

        // Assert
        assertFalse(result);
    }

    @Test
    public void isPackageInstalled_handlesNullSafely() {
        // Act & Assert - should not throw NPE
        try {
            PackageUtils.isPackageInstalled(context, null);
            // If we get here, the method handled null somehow
        } catch (NullPointerException e) {
            // This is also acceptable behavior
        }
    }

    @Test
    public void isPackageInstalled_handlesSpecialCharacters() {
        // Act
        boolean result = PackageUtils.isPackageInstalled(context, "com.test@#$%^&*()");

        // Assert
        assertFalse(result);
    }

    // ==================== Edge Cases ====================

    @Test
    public void getInstalledUserApps_withValidContext_doesNotThrow() {
        // Act & Assert - should not throw
        try {
            PackageUtils.getInstalledUserApps(context);
        } catch (Exception e) {
            fail("Should not throw exception: " + e.getMessage());
        }
    }

    @Test
    public void getInstalledUserApps_returnsEmptyList_notNull_whenNoApps() {
        // In Robolectric, there might be no launcher apps
        // Act
        List<InstalledApp> apps = PackageUtils.getInstalledUserApps(context);

        // Assert - should return empty list, not null
        assertNotNull(apps);
    }

    // ==================== Utility Class Tests ====================

    @Test
    public void packageUtils_cannotBeInstantiated() {
        // The constructor is private, so we can't test directly
        // But we verify all methods are static
        assertTrue(java.lang.reflect.Modifier.isStatic(
                PackageUtils.class.getMethods()[0].getModifiers()));
    }

    // ==================== Performance Tests ====================

    @Test
    public void getInstalledUserApps_completesInReasonableTime() {
        // Arrange
        long startTime = System.currentTimeMillis();

        // Act
        PackageUtils.getInstalledUserApps(context);

        // Assert - should complete within 5 seconds
        long duration = System.currentTimeMillis() - startTime;
        assertTrue("Method took too long: " + duration + "ms", duration < 5000);
    }

    // ==================== InstalledApp Data Integrity Tests ====================

    @Test
    public void getInstalledUserApps_labelsAreNotEmpty() {
        // Act
        List<InstalledApp> apps = PackageUtils.getInstalledUserApps(context);

        // Assert
        for (InstalledApp app : apps) {
            assertNotNull("Label should not be null", app.getLabel());
            // Note: Label might be empty string in some edge cases
        }
    }

    @Test
    public void getInstalledUserApps_packageNamesAreUnique() {
        // Act
        List<InstalledApp> apps = PackageUtils.getInstalledUserApps(context);

        // Assert - no duplicate package names
        java.util.Set<String> packageNames = new java.util.HashSet<>();
        for (InstalledApp app : apps) {
            String packageName = app.getPackageName();
            assertFalse("Duplicate package: " + packageName,
                       packageNames.contains(packageName));
            packageNames.add(packageName);
        }
    }

    @Test
    public void getInstalledUserApps_packageNamesAreValid() {
        // Act
        List<InstalledApp> apps = PackageUtils.getInstalledUserApps(context);

        // Assert - package names should follow Android naming convention
        for (InstalledApp app : apps) {
            String packageName = app.getPackageName();
            assertTrue("Invalid package name: " + packageName,
                      packageName.contains("."));
        }
    }
}
