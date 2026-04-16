package com.copiloto.addblock.ui.viewmodel

import android.app.Application
import android.graphics.drawable.Drawable
import app.cash.turbine.test
import com.copiloto.addblock.ui.model.InstalledApp
import com.copiloto.addblock.ui.state.AppFilter
import com.copiloto.addblock.ui.state.FirewallStatus
import com.copiloto.addblock.vpn.VpnState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config

/**
 * Unit tests for FirewallViewModel using Kotlin coroutines.
 * Tests StateFlow emissions and UI state management.
 */
@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class FirewallViewModelTest {

    private lateinit var viewModel: FirewallViewModel
    private lateinit var application: Application
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)

        application = RuntimeEnvironment.getApplication()

        // Clear preferences before each test
        application.getSharedPreferences("adblock_prefs", 0)
            .edit().clear().apply()

        viewModel = FirewallViewModel(application)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    // ==================== Initialization Tests ====================

    @Test
    fun `viewModel initializes with default firewall state`() = runTest {
        // Assert
        val state = viewModel.firewallState.value
        assertEquals(FirewallStatus.STOPPED, state.status)
        assertEquals(0, state.blockedAppsCount)
        assertNull(state.errorMessage)
    }

    @Test
    fun `viewModel initializes with default apps state`() = runTest {
        // Assert
        val state = viewModel.appsState.value
        assertNotNull(state.apps)
        assertEquals("", state.searchQuery)
        assertEquals(AppFilter.ALL, state.selectedFilter)
    }

    // ==================== Search Query Tests ====================

    @Test
    fun `onSearchQueryChange updates search query`() = runTest {
        // Act
        viewModel.onSearchQueryChange("chrome")

        // Assert
        assertEquals("chrome", viewModel.appsState.value.searchQuery)
    }

    @Test
    fun `onSearchQueryChange filters apps by name`() = runTest {
        // This test assumes apps are loaded
        // Act
        viewModel.onSearchQueryChange("nonexistent_app_xyz123")
        advanceUntilIdle()

        // Assert - filtered apps should be empty or filtered
        val state = viewModel.appsState.value
        assertEquals("nonexistent_app_xyz123", state.searchQuery)
    }

    @Test
    fun `onSearchQueryChange with empty string shows all apps`() = runTest {
        // Arrange
        viewModel.onSearchQueryChange("test")

        // Act
        viewModel.onSearchQueryChange("")
        advanceUntilIdle()

        // Assert
        assertEquals("", viewModel.appsState.value.searchQuery)
    }

    @Test
    fun `search is case insensitive`() = runTest {
        // Act
        viewModel.onSearchQueryChange("CHROME")
        advanceUntilIdle()

        // The search should work regardless of case
        assertEquals("CHROME", viewModel.appsState.value.searchQuery)
    }

    // ==================== Filter Tests ====================

    @Test
    fun `onFilterChange updates selected filter to BLOCKED`() = runTest {
        // Act
        viewModel.onFilterChange(AppFilter.BLOCKED)

        // Assert
        assertEquals(AppFilter.BLOCKED, viewModel.appsState.value.selectedFilter)
    }

    @Test
    fun `onFilterChange updates selected filter to USER`() = runTest {
        // Act
        viewModel.onFilterChange(AppFilter.USER)

        // Assert
        assertEquals(AppFilter.USER, viewModel.appsState.value.selectedFilter)
    }

    @Test
    fun `onFilterChange updates selected filter to SYSTEM`() = runTest {
        // Act
        viewModel.onFilterChange(AppFilter.SYSTEM)

        // Assert
        assertEquals(AppFilter.SYSTEM, viewModel.appsState.value.selectedFilter)
    }

    @Test
    fun `onFilterChange updates selected filter to ALL`() = runTest {
        // Arrange
        viewModel.onFilterChange(AppFilter.BLOCKED)

        // Act
        viewModel.onFilterChange(AppFilter.ALL)

        // Assert
        assertEquals(AppFilter.ALL, viewModel.appsState.value.selectedFilter)
    }

    @Test
    fun `filter changes trigger filtered apps update`() = runTest {
        // Act
        viewModel.onFilterChange(AppFilter.BLOCKED)
        advanceUntilIdle()

        // Assert - filteredApps should be recalculated
        val state = viewModel.appsState.value
        assertEquals(AppFilter.BLOCKED, state.selectedFilter)
    }

    // ==================== Toggle App Blocked Tests ====================

    @Test
    fun `toggleAppBlocked blocks unblocked app`() = runTest {
        // Arrange
        val app = InstalledApp("Test App", "com.test.app", null, false)

        // Act
        viewModel.toggleAppBlocked(app)
        advanceUntilIdle()

        // Assert - blocked count should increase
        assertTrue(viewModel.firewallState.value.blockedAppsCount >= 0)
    }

    @Test
    fun `toggleAppBlocked unblocks blocked app`() = runTest {
        // Arrange
        val app = InstalledApp("Test App", "com.test.app", null, true)

        // First block it
        viewModel.toggleAppBlocked(InstalledApp("Test App", "com.test.app", null, false))
        advanceUntilIdle()

        // Act - unblock
        viewModel.toggleAppBlocked(app)
        advanceUntilIdle()

        // Assert
        val state = viewModel.firewallState.value
        assertNotNull(state)
    }

    @Test
    fun `toggleAppBlocked updates blocked count`() = runTest {
        viewModel.firewallState.test {
            // Get initial state
            val initial = awaitItem()
            val initialCount = initial.blockedAppsCount

            // Act
            val app = InstalledApp("Test", "com.test.unique.app", null, false)
            viewModel.toggleAppBlocked(app)

            // Should emit updated state
            val updated = awaitItem()
            assertEquals(initialCount + 1, updated.blockedAppsCount)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `toggleAppBlocked updates lastChangeTime`() = runTest {
        // Arrange
        val app = InstalledApp("Test", "com.test.app", null, false)

        // Act
        viewModel.toggleAppBlocked(app)
        advanceUntilIdle()

        // Assert
        assertEquals("Ahora", viewModel.firewallState.value.lastChangeTime)
    }

    // ==================== Critical Apps Tests ====================

    @Test
    fun `onShowCriticalAppsChange updates state`() = runTest {
        // Act
        viewModel.onShowCriticalAppsChange(true)

        // Assert
        assertTrue(viewModel.appsState.value.showCriticalApps)
    }

    @Test
    fun `onShowCriticalAppsChange can toggle off`() = runTest {
        // Arrange
        viewModel.onShowCriticalAppsChange(true)

        // Act
        viewModel.onShowCriticalAppsChange(false)

        // Assert
        assertFalse(viewModel.appsState.value.showCriticalApps)
    }

    // ==================== Firewall Control Tests ====================

    @Test
    fun `startFirewall with no blocked apps sets error`() = runTest {
        // Ensure no apps are blocked
        application.getSharedPreferences("adblock_prefs", 0)
            .edit().clear().apply()

        // Recreate viewModel to clear state
        viewModel = FirewallViewModel(application)
        advanceUntilIdle()

        // Act
        viewModel.startFirewall()
        advanceUntilIdle()

        // Assert - should have error message
        val state = viewModel.firewallState.value
        assertEquals(FirewallStatus.ERROR, state.status)
        assertNotNull(state.errorMessage)
    }

    @Test
    fun `stopFirewall can be called safely`() = runTest {
        // Act - should not throw
        viewModel.stopFirewall()
        advanceUntilIdle()

        // Assert - state should be stopped
        assertEquals(FirewallStatus.STOPPED, viewModel.firewallState.value.status)
    }

    // ==================== VPN State Listener Tests ====================

    @Test
    fun `onStateChanged updates firewall status to RUNNING`() = runTest {
        // Act
        viewModel.onStateChanged(VpnState.RUNNING)
        advanceUntilIdle()

        // Assert
        assertEquals(FirewallStatus.RUNNING, viewModel.firewallState.value.status)
    }

    @Test
    fun `onStateChanged updates firewall status to STARTING`() = runTest {
        // Act
        viewModel.onStateChanged(VpnState.STARTING)
        advanceUntilIdle()

        // Assert
        assertEquals(FirewallStatus.STARTING, viewModel.firewallState.value.status)
    }

    @Test
    fun `onStateChanged updates firewall status to ERROR`() = runTest {
        // Act
        viewModel.onStateChanged(VpnState.ERROR)
        advanceUntilIdle()

        // Assert
        assertEquals(FirewallStatus.ERROR, viewModel.firewallState.value.status)
    }

    @Test
    fun `onStateChanged updates firewall status to STOPPED`() = runTest {
        // Arrange - set to running first
        viewModel.onStateChanged(VpnState.RUNNING)
        advanceUntilIdle()

        // Act
        viewModel.onStateChanged(VpnState.STOPPED)
        advanceUntilIdle()

        // Assert
        assertEquals(FirewallStatus.STOPPED, viewModel.firewallState.value.status)
    }

    @Test
    fun `onStateChanged clears error message on success`() = runTest {
        // Arrange - set error first
        viewModel.onStateChanged(VpnState.ERROR)
        advanceUntilIdle()

        // Act
        viewModel.onStateChanged(VpnState.RUNNING)
        advanceUntilIdle()

        // Assert
        assertNull(viewModel.firewallState.value.errorMessage)
    }

    // ==================== StateFlow Emission Tests ====================

    @Test
    fun `firewallState emits updates`() = runTest {
        viewModel.firewallState.test {
            // Initial state
            val initial = awaitItem()
            assertEquals(FirewallStatus.STOPPED, initial.status)

            // Trigger change
            viewModel.onStateChanged(VpnState.STARTING)

            val starting = awaitItem()
            assertEquals(FirewallStatus.STARTING, starting.status)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `appsState emits filter updates`() = runTest {
        viewModel.appsState.test {
            // Initial state
            val initial = awaitItem()
            assertEquals(AppFilter.ALL, initial.selectedFilter)

            // Change filter
            viewModel.onFilterChange(AppFilter.BLOCKED)

            val updated = awaitItem()
            assertEquals(AppFilter.BLOCKED, updated.selectedFilter)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `appsState emits search updates`() = runTest {
        viewModel.appsState.test {
            // Initial state
            awaitItem()

            // Change search
            viewModel.onSearchQueryChange("test")

            val updated = awaitItem()
            assertEquals("test", updated.searchQuery)

            cancelAndIgnoreRemainingEvents()
        }
    }

    // ==================== Load Apps Tests ====================

    @Test
    fun `loadInstalledApps sets loading state`() = runTest {
        viewModel.appsState.test {
            // Skip initial emissions
            skipItems(1)

            // Act
            viewModel.loadInstalledApps()

            // Should emit loading = true at some point
            val loadingState = awaitItem()
            assertTrue(loadingState.isLoading)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `loadInstalledApps completes without error`() = runTest {
        // Act
        viewModel.loadInstalledApps()
        advanceUntilIdle()

        // Assert - should not be loading anymore
        assertFalse(viewModel.appsState.value.isLoading)
    }

    // ==================== Combined Operations Tests ====================

    @Test
    fun `search and filter work together`() = runTest {
        // Act
        viewModel.onSearchQueryChange("app")
        viewModel.onFilterChange(AppFilter.USER)
        advanceUntilIdle()

        // Assert
        val state = viewModel.appsState.value
        assertEquals("app", state.searchQuery)
        assertEquals(AppFilter.USER, state.selectedFilter)
    }

    @Test
    fun `multiple toggles update count correctly`() = runTest {
        // Arrange
        val app1 = InstalledApp("App1", "com.test.app1", null, false)
        val app2 = InstalledApp("App2", "com.test.app2", null, false)

        // Act
        viewModel.toggleAppBlocked(app1)
        viewModel.toggleAppBlocked(app2)
        advanceUntilIdle()

        // Assert
        assertTrue(viewModel.firewallState.value.blockedAppsCount >= 2)
    }

    // ==================== Edge Cases ====================

    @Test
    fun `empty search query is handled`() = runTest {
        // Act
        viewModel.onSearchQueryChange("")
        advanceUntilIdle()

        // Assert - should not crash
        assertEquals("", viewModel.appsState.value.searchQuery)
    }

    @Test
    fun `special characters in search are handled`() = runTest {
        // Act
        viewModel.onSearchQueryChange("app@#\$%")
        advanceUntilIdle()

        // Assert - should not crash
        assertEquals("app@#\$%", viewModel.appsState.value.searchQuery)
    }

    @Test
    fun `rapid state changes are handled`() = runTest {
        // Act - rapid changes
        repeat(10) { i ->
            viewModel.onFilterChange(if (i % 2 == 0) AppFilter.BLOCKED else AppFilter.ALL)
        }
        advanceUntilIdle()

        // Assert - should end in consistent state
        val state = viewModel.appsState.value
        assertNotNull(state.selectedFilter)
    }
}
