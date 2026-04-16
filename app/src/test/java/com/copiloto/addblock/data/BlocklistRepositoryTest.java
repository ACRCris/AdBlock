package com.copiloto.addblock.data;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.HashSet;
import java.util.Set;

/**
 * Unit tests for BlocklistRepository.
 * Tests the repository layer with mocked Preferences.
 */
public class BlocklistRepositoryTest {

    @Mock
    private Preferences mockPreferences;

    private BlocklistRepository repository;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        repository = new BlocklistRepository(mockPreferences);
    }

    // ==================== getBlockedPackages() Tests ====================

    @Test
    public void getBlockedPackages_returnsEmptySet_whenNoPackagesBlocked() {
        // Arrange
        when(mockPreferences.getBlockedPackages()).thenReturn(new HashSet<>());

        // Act
        Set<String> result = repository.getBlockedPackages();

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(mockPreferences).getBlockedPackages();
    }

    @Test
    public void getBlockedPackages_returnsCorrectSet_whenPackagesExist() {
        // Arrange
        Set<String> blockedSet = new HashSet<>();
        blockedSet.add("com.example.app1");
        blockedSet.add("com.example.app2");
        when(mockPreferences.getBlockedPackages()).thenReturn(blockedSet);

        // Act
        Set<String> result = repository.getBlockedPackages();

        // Assert
        assertEquals(2, result.size());
        assertTrue(result.contains("com.example.app1"));
        assertTrue(result.contains("com.example.app2"));
    }

    // ==================== setBlocked() Tests ====================

    @Test
    public void setBlocked_addsPackage_whenBlockedIsTrue() {
        // Arrange
        String packageName = "com.example.newapp";

        // Act
        repository.setBlocked(packageName, true);

        // Assert
        verify(mockPreferences).addBlockedPackage(packageName);
        verify(mockPreferences, never()).removeBlockedPackage(anyString());
    }

    @Test
    public void setBlocked_removesPackage_whenBlockedIsFalse() {
        // Arrange
        String packageName = "com.example.existingapp";

        // Act
        repository.setBlocked(packageName, false);

        // Assert
        verify(mockPreferences).removeBlockedPackage(packageName);
        verify(mockPreferences, never()).addBlockedPackage(anyString());
    }

    @Test
    public void setBlocked_handlesEmptyPackageName() {
        // Arrange
        String packageName = "";

        // Act
        repository.setBlocked(packageName, true);

        // Assert
        verify(mockPreferences).addBlockedPackage(packageName);
    }

    // ==================== isBlocked() Tests ====================

    @Test
    public void isBlocked_returnsTrue_whenPackageIsBlocked() {
        // Arrange
        String packageName = "com.example.blockedapp";
        when(mockPreferences.isBlocked(packageName)).thenReturn(true);

        // Act
        boolean result = repository.isBlocked(packageName);

        // Assert
        assertTrue(result);
        verify(mockPreferences).isBlocked(packageName);
    }

    @Test
    public void isBlocked_returnsFalse_whenPackageIsNotBlocked() {
        // Arrange
        String packageName = "com.example.unblockedapp";
        when(mockPreferences.isBlocked(packageName)).thenReturn(false);

        // Act
        boolean result = repository.isBlocked(packageName);

        // Assert
        assertFalse(result);
    }

    @Test
    public void isBlocked_returnsFalse_forNonExistentPackage() {
        // Arrange
        String packageName = "com.nonexistent.app";
        when(mockPreferences.isBlocked(packageName)).thenReturn(false);

        // Act
        boolean result = repository.isBlocked(packageName);

        // Assert
        assertFalse(result);
    }

    // ==================== getBlockedCount() Tests ====================

    @Test
    public void getBlockedCount_returnsZero_whenNoPackagesBlocked() {
        // Arrange
        when(mockPreferences.getBlockedPackages()).thenReturn(new HashSet<>());

        // Act
        int count = repository.getBlockedCount();

        // Assert
        assertEquals(0, count);
    }

    @Test
    public void getBlockedCount_returnsCorrectCount_whenPackagesExist() {
        // Arrange
        Set<String> blockedSet = new HashSet<>();
        blockedSet.add("com.example.app1");
        blockedSet.add("com.example.app2");
        blockedSet.add("com.example.app3");
        when(mockPreferences.getBlockedPackages()).thenReturn(blockedSet);

        // Act
        int count = repository.getBlockedCount();

        // Assert
        assertEquals(3, count);
    }

    // ==================== clearAll() Tests ====================

    @Test
    public void clearAll_setsEmptySet() {
        // Act
        repository.clearAll();

        // Assert
        verify(mockPreferences).setBlockedPackages(argThat(set -> set.isEmpty()));
    }

    @Test
    public void clearAll_canBeCalledMultipleTimes() {
        // Act
        repository.clearAll();
        repository.clearAll();

        // Assert
        verify(mockPreferences, times(2)).setBlockedPackages(argThat(set -> set.isEmpty()));
    }

    // ==================== Integration-like Tests ====================

    @Test
    public void blockAndCheckSequence_worksCorrectly() {
        // Arrange
        String packageName = "com.example.testapp";

        // First call returns false (not blocked)
        when(mockPreferences.isBlocked(packageName)).thenReturn(false);
        assertFalse(repository.isBlocked(packageName));

        // Block the app
        repository.setBlocked(packageName, true);
        verify(mockPreferences).addBlockedPackage(packageName);

        // Configure mock to return true after blocking
        when(mockPreferences.isBlocked(packageName)).thenReturn(true);
        assertTrue(repository.isBlocked(packageName));
    }
}
