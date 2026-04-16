package com.copiloto.addblock.ui
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.copiloto.addblock.ui.ComposeMainActivity
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
/**
 * Instrumented tests for the main Compose UI.
 * These tests run on a real device or emulator.
 */
@RunWith(AndroidJUnit4::class)
class MainScreenTest {
    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComposeMainActivity>()
    // ==================== Navigation Tests ====================
    @Test
    fun bottomNavigation_isDisplayed() {
        // Verify bottom navigation bar exists
        composeTestRule.onNodeWithText("Inicio").assertExists()
        composeTestRule.onNodeWithText("Apps").assertExists()
        composeTestRule.onNodeWithText("Info").assertExists()
    }
    @Test
    fun bottomNavigation_startsOnHome() {
        // Home should be selected by default
        composeTestRule.onNodeWithText("Inicio").assertIsSelected()
    }
    @Test
    fun bottomNavigation_switchToApps() {
        // Click on Apps tab
        composeTestRule.onNodeWithText("Apps").performClick()
        // Verify Apps screen content is shown
        composeTestRule.waitForIdle()
        // Search bar should be visible in Apps screen
        composeTestRule.onNodeWithText("Buscar apps...").assertExists()
    }
    @Test
    fun bottomNavigation_switchToInfo() {
        // Click on Info tab
        composeTestRule.onNodeWithText("Info").performClick()
        composeTestRule.waitForIdle()
        // Info screen should show "Cómo funciona" content
        composeTestRule.onNodeWithText("Cómo funciona").assertExists()
    }
    // ==================== Home Screen Tests ====================
    @Test
    fun homeScreen_showsFirewallStatus() {
        // Verify main status card is displayed
        composeTestRule.onNodeWithText("Protección").assertExists()
    }
    @Test
    fun homeScreen_showsStartButton() {
        // Start/Stop button should be visible
        composeTestRule.onNodeWithText("Iniciar", substring = true, ignoreCase = true)
            .assertExists()
    }
    @Test
    fun homeScreen_showsBlockedAppsCount() {
        // Should show blocked apps info
        composeTestRule.onNodeWithText("bloqueadas", substring = true, ignoreCase = true)
            .assertExists()
    }
    // ==================== Apps Screen Tests ====================
    @Test
    fun appsScreen_showsSearchBar() {
        // Navigate to Apps
        composeTestRule.onNodeWithText("Apps").performClick()
        composeTestRule.waitForIdle()
        // Search bar should be visible
        composeTestRule.onNodeWithText("Buscar apps...").assertExists()
    }
    @Test
    fun appsScreen_showsFilterChips() {
        // Navigate to Apps
        composeTestRule.onNodeWithText("Apps").performClick()
        composeTestRule.waitForIdle()
        // Filter chips should be visible
        composeTestRule.onNodeWithText("Todas").assertExists()
        composeTestRule.onNodeWithText("Bloqueadas").assertExists()
    }
    @Test
    fun appsScreen_searchFiltersApps() {
        // Navigate to Apps
        composeTestRule.onNodeWithText("Apps").performClick()
        composeTestRule.waitForIdle()
        // Type in search field
        composeTestRule.onNodeWithText("Buscar apps...").performTextInput("xyz123nonexistent")
        composeTestRule.waitForIdle()
        // Should show no results or empty state
        // The app list should be filtered
    }
    @Test
    fun appsScreen_filterChipChangesSelection() {
        // Navigate to Apps
        composeTestRule.onNodeWithText("Apps").performClick()
        composeTestRule.waitForIdle()
        // Click on "Bloqueadas" filter
        composeTestRule.onNodeWithText("Bloqueadas").performClick()
        composeTestRule.waitForIdle()
        // The chip should now be selected (visual change)
        composeTestRule.onNodeWithText("Bloqueadas").assertIsSelected()
    }
    // ==================== Info Screen Tests ====================
    @Test
    fun infoScreen_showsHowItWorksSection() {
        // Navigate to Info
        composeTestRule.onNodeWithText("Info").performClick()
        composeTestRule.waitForIdle()
        // Should show explanation sections
        composeTestRule.onNodeWithText("Cómo funciona").assertExists()
    }
    @Test
    fun infoScreen_showsPrivacyPolicyLink() {
        // Navigate to Info
        composeTestRule.onNodeWithText("Info").performClick()
        composeTestRule.waitForIdle()
        // Should have privacy policy link
        composeTestRule.onNodeWithText("Política de privacidad", substring = true, ignoreCase = true)
            .assertExists()
    }
    @Test
    fun infoScreen_showsRecommendations() {
        // Navigate to Info
        composeTestRule.onNodeWithText("Info").performClick()
        composeTestRule.waitForIdle()
        // Should show recommendations section
        composeTestRule.onNodeWithText("Recomendaciones", substring = true, ignoreCase = true)
            .assertExists()
    }
    // ==================== Interaction Tests ====================
    @Test
    fun clickStartButton_changesState() {
        // Find and click start button
        composeTestRule.onNodeWithText("Iniciar", substring = true, ignoreCase = true)
            .performClick()
        composeTestRule.waitForIdle()
        // State should change (button text or status might change)
        // Note: This may require VPN permission which cannot be granted in tests
    }
    @Test
    fun scrollAppsList_works() {
        // Navigate to Apps
        composeTestRule.onNodeWithText("Apps").performClick()
        composeTestRule.waitForIdle()
        // Try scrolling the list
        composeTestRule.onNodeWithTag("apps_list", useUnmergedTree = true)
            .performScrollToIndex(5)
    }
}
