package com.copiloto.addblock.ui.model;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * Unit tests for InstalledApp model.
 * Tests constructors, getters, setters, equals, and hashCode.
 * Pure JUnit test - no Android dependencies needed.
 */
public class InstalledAppTest {

    // ==================== Constructor Tests ====================

    @Test
    public void constructor_withMinimalParams_createsAppWithDefaults() {
        // Act
        InstalledApp app = new InstalledApp("Test App", "com.test.app", null);

        // Assert
        assertEquals("Test App", app.getLabel());
        assertEquals("com.test.app", app.getPackageName());
        assertNull(app.getIcon());
        assertFalse(app.isBlocked());
        assertFalse(app.isSystemApp());
    }

    @Test
    public void constructor_withBlockedParam_setsBlockedState() {
        // Act
        InstalledApp app = new InstalledApp("Test App", "com.test.app", null, true);

        // Assert
        assertTrue(app.isBlocked());
        assertFalse(app.isSystemApp());
    }

    @Test
    public void constructor_withAllParams_setsAllValues() {
        // Act
        InstalledApp app = new InstalledApp("System App", "com.android.system", null, true, true);

        // Assert
        assertEquals("System App", app.getLabel());
        assertEquals("com.android.system", app.getPackageName());
        assertTrue(app.isBlocked());
        assertTrue(app.isSystemApp());
    }

    @Test
    public void constructor_withBlockedFalseAndSystemTrue() {
        // Act
        InstalledApp app = new InstalledApp("System", "com.system", null, false, true);

        // Assert
        assertFalse(app.isBlocked());
        assertTrue(app.isSystemApp());
    }

    // ==================== Getter Tests ====================

    @Test
    public void getLabel_returnsCorrectValue() {
        // Arrange
        InstalledApp app = new InstalledApp("My Label", "com.test", null);

        // Assert
        assertEquals("My Label", app.getLabel());
    }

    @Test
    public void getAppName_returnsLabel() {
        // Arrange (getAppName is alias for getLabel)
        InstalledApp app = new InstalledApp("App Name", "com.test", null);

        // Assert
        assertEquals("App Name", app.getAppName());
        assertEquals(app.getLabel(), app.getAppName());
    }

    @Test
    public void getPackageName_returnsCorrectValue() {
        // Arrange
        InstalledApp app = new InstalledApp("Test", "com.example.package", null);

        // Assert
        assertEquals("com.example.package", app.getPackageName());
    }

    @Test
    public void getIcon_returnsNullWhenSetToNull() {
        // Arrange
        InstalledApp app = new InstalledApp("Test", "com.test", null);

        // Assert
        assertNull(app.getIcon());
    }

    @Test
    public void isBlocked_returnsFalse_byDefault() {
        // Arrange
        InstalledApp app = new InstalledApp("Test", "com.test", null);

        // Assert
        assertFalse(app.isBlocked());
    }

    @Test
    public void getBlocked_isAliasForIsBlocked() {
        // Arrange
        InstalledApp app = new InstalledApp("Test", "com.test", null, true);

        // Assert
        assertEquals(app.isBlocked(), app.getBlocked());
        assertTrue(app.getBlocked());
    }

    @Test
    public void isSystemApp_returnsFalse_byDefault() {
        // Arrange
        InstalledApp app = new InstalledApp("Test", "com.test", null);

        // Assert
        assertFalse(app.isSystemApp());
    }

    @Test
    public void getSystemApp_isAliasForIsSystemApp() {
        // Arrange
        InstalledApp app = new InstalledApp("Test", "com.test", null, false, true);

        // Assert
        assertEquals(app.isSystemApp(), app.getSystemApp());
        assertTrue(app.getSystemApp());
    }

    // ==================== Setter Tests ====================

    @Test
    public void setBlocked_changesBlockedStateToTrue() {
        // Arrange
        InstalledApp app = new InstalledApp("Test", "com.test", null);

        // Act
        app.setBlocked(true);

        // Assert
        assertTrue(app.isBlocked());
    }

    @Test
    public void setBlocked_changesBlockedStateToFalse() {
        // Arrange
        InstalledApp app = new InstalledApp("Test", "com.test", null, true);

        // Act
        app.setBlocked(false);

        // Assert
        assertFalse(app.isBlocked());
    }

    @Test
    public void setBlocked_canToggleMultipleTimes() {
        // Arrange
        InstalledApp app = new InstalledApp("Test", "com.test", null);

        // Act & Assert
        app.setBlocked(true);
        assertTrue(app.isBlocked());

        app.setBlocked(false);
        assertFalse(app.isBlocked());

        app.setBlocked(true);
        assertTrue(app.isBlocked());
    }

    // ==================== equals() Tests ====================

    @Test
    public void equals_returnsTrueForSameObject() {
        // Arrange
        InstalledApp app = new InstalledApp("Test", "com.test", null);

        // Assert
        assertEquals(app, app);
    }

    @Test
    public void equals_returnsTrueForSamePackageName() {
        // Arrange
        InstalledApp app1 = new InstalledApp("Test 1", "com.same.package", null);
        InstalledApp app2 = new InstalledApp("Test 2", "com.same.package", null);

        // Assert
        assertEquals(app1, app2);
        assertEquals(app2, app1);
    }

    @Test
    public void equals_returnsFalseForDifferentPackageName() {
        // Arrange
        InstalledApp app1 = new InstalledApp("Test", "com.package.one", null);
        InstalledApp app2 = new InstalledApp("Test", "com.package.two", null);

        // Assert
        assertNotEquals(app1, app2);
    }

    @Test
    public void equals_returnsFalseForNull() {
        // Arrange
        InstalledApp app = new InstalledApp("Test", "com.test", null);

        // Assert
        assertNotEquals(null, app);
    }

    @Test
    public void equals_returnsFalseForDifferentClass() {
        // Arrange
        InstalledApp app = new InstalledApp("Test", "com.test", null);

        // Assert
        assertNotEquals("com.test", app);
        assertNotEquals(Integer.valueOf(123), app);
    }

    @Test
    public void equals_ignoresLabelDifference() {
        // Arrange - same package, different label
        InstalledApp app1 = new InstalledApp("Name1", "com.same", null);
        InstalledApp app2 = new InstalledApp("Name2", "com.same", null);

        // Assert
        assertEquals(app1, app2);
    }

    @Test
    public void equals_ignoresBlockedStateDifference() {
        // Arrange - same package, different blocked state
        InstalledApp app1 = new InstalledApp("Test", "com.same", null, true);
        InstalledApp app2 = new InstalledApp("Test", "com.same", null, false);

        // Assert
        assertEquals(app1, app2);
    }

    @Test
    public void equals_ignoresSystemAppDifference() {
        // Arrange - same package, different systemApp state
        InstalledApp app1 = new InstalledApp("Test", "com.same", null, false, true);
        InstalledApp app2 = new InstalledApp("Test", "com.same", null, false, false);

        // Assert
        assertEquals(app1, app2);
    }

    // ==================== hashCode() Tests ====================

    @Test
    public void hashCode_isSameForEqualObjects() {
        // Arrange
        InstalledApp app1 = new InstalledApp("Test 1", "com.same.package", null);
        InstalledApp app2 = new InstalledApp("Test 2", "com.same.package", null);

        // Assert
        assertEquals(app1.hashCode(), app2.hashCode());
    }

    @Test
    public void hashCode_isDifferentForDifferentPackages() {
        // Arrange
        InstalledApp app1 = new InstalledApp("Test", "com.package.one", null);
        InstalledApp app2 = new InstalledApp("Test", "com.package.two", null);

        // Assert - not guaranteed but highly likely
        assertNotEquals(app1.hashCode(), app2.hashCode());
    }

    @Test
    public void hashCode_isConsistentAfterBlockedChange() {
        // Arrange
        InstalledApp app = new InstalledApp("Test", "com.test", null);
        int hash1 = app.hashCode();

        // Act - modify mutable state
        app.setBlocked(true);
        int hash2 = app.hashCode();

        // Assert - hash should be consistent (based on immutable packageName)
        assertEquals(hash1, hash2);
    }

    @Test
    public void hashCode_isBasedOnPackageName() {
        // Arrange
        String packageName = "com.example.test";
        InstalledApp app = new InstalledApp("Test", packageName, null);

        // Assert
        assertEquals(packageName.hashCode(), app.hashCode());
    }

    // ==================== Edge Cases ====================

    @Test
    public void constructor_handlesEmptyLabel() {
        // Act
        InstalledApp app = new InstalledApp("", "com.test", null);

        // Assert
        assertEquals("", app.getLabel());
        assertEquals("", app.getAppName());
    }

    @Test
    public void constructor_handlesEmptyPackageName() {
        // Act
        InstalledApp app = new InstalledApp("Test", "", null);

        // Assert
        assertEquals("", app.getPackageName());
    }

    @Test
    public void equals_worksWithEmptyPackageName() {
        // Arrange
        InstalledApp app1 = new InstalledApp("Test1", "", null);
        InstalledApp app2 = new InstalledApp("Test2", "", null);

        // Assert
        assertEquals(app1, app2);
    }

    @Test
    public void hashCode_worksWithEmptyPackageName() {
        // Arrange
        InstalledApp app = new InstalledApp("Test", "", null);

        // Assert - should not throw
        assertEquals("".hashCode(), app.hashCode());
    }

    // ==================== Collection Behavior Tests ====================

    @Test
    public void canBeUsedInHashSet() {
        // Arrange
        java.util.Set<InstalledApp> set = new java.util.HashSet<>();
        InstalledApp app1 = new InstalledApp("App 1", "com.test", null);
        InstalledApp app2 = new InstalledApp("App 2", "com.test", null); // Same package

        // Act
        set.add(app1);
        set.add(app2);

        // Assert - should have only 1 element since same package
        assertEquals(1, set.size());
    }

    @Test
    public void canBeUsedInHashMap() {
        // Arrange
        java.util.Map<InstalledApp, String> map = new java.util.HashMap<>();
        InstalledApp app = new InstalledApp("Test", "com.test", null);

        // Act
        map.put(app, "value");

        // Assert
        assertEquals("value", map.get(app));
    }

    @Test
    public void differentPackagesAreDistinctInSet() {
        // Arrange
        java.util.Set<InstalledApp> set = new java.util.HashSet<>();
        InstalledApp app1 = new InstalledApp("App", "com.package1", null);
        InstalledApp app2 = new InstalledApp("App", "com.package2", null);

        // Act
        set.add(app1);
        set.add(app2);

        // Assert
        assertEquals(2, set.size());
    }
}
