package com.copiloto.addblock.ui.screens

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.copiloto.addblock.ui.components.AppTopBar
import com.copiloto.addblock.ui.navigation.NavDestination
import com.copiloto.addblock.ui.state.AppsUiState
import com.copiloto.addblock.ui.state.FirewallUiState

@Composable
fun MainScreen(
    firewallState: FirewallUiState,
    appsState: AppsUiState,
    onStartFirewall: () -> Unit,
    onStopFirewall: () -> Unit,
    onTestBlock: () -> Unit,
    onHelpClick: () -> Unit,
    onPrivacyClick: () -> Unit,
    onSearchQueryChange: (String) -> Unit,
    onFilterChange: (com.copiloto.addblock.ui.state.AppFilter) -> Unit,
    onToggleAppBlocked: (com.copiloto.addblock.ui.model.InstalledApp) -> Unit,
    onShowCriticalAppsChange: (Boolean) -> Unit,
    onClearDataClick: () -> Unit = {}
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    var showPrivacyPolicy by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }

    // Mostrar pantalla de Política de Privacidad
    if (showPrivacyPolicy) {
        PrivacyPolicyScreen(
            onBackClick = { showPrivacyPolicy = false },
            onClearDataClick = onClearDataClick
        )
        return
    }

    // Título dinámico según la pestaña seleccionada
    val currentTitle = when (selectedTab) {
        0 -> "AdBlock Firewall"
        1 -> "Apps"
        2 -> "Info"
        else -> "AdBlock Firewall"
    }

    Scaffold(
        topBar = {
            AppTopBar(
                title = currentTitle,
                canNavigateBack = false
            )
        },
        bottomBar = {
            NavigationBar {
                NavDestination.items.forEachIndexed { index, destination ->
                    NavigationBarItem(
                        icon = {
                            Icon(
                                if (selectedTab == index) destination.selectedIcon
                                else destination.unselectedIcon,
                                contentDescription = destination.label
                            )
                        },
                        label = { Text(destination.label) },
                        selected = selectedTab == index,
                        onClick = { selectedTab = index }
                    )
                }
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        when (selectedTab) {
            0 -> InicioScreen(
                modifier = Modifier.padding(paddingValues),
                uiState = firewallState,
                onStartFirewall = onStartFirewall,
                onStopFirewall = onStopFirewall,
                onTestBlock = onTestBlock,
                onMoreInfoClick = { selectedTab = 2 }
            )
            1 -> AppsScreen(
                modifier = Modifier.padding(paddingValues),
                uiState = appsState,
                onSearchQueryChange = onSearchQueryChange,
                onFilterChange = onFilterChange,
                onToggleAppBlocked = onToggleAppBlocked,
                onShowCriticalAppsChange = onShowCriticalAppsChange
            )
            2 -> InfoScreen(
                modifier = Modifier.padding(paddingValues),
                onPrivacyPolicyClick = { showPrivacyPolicy = true }
            )
        }
    }
}
