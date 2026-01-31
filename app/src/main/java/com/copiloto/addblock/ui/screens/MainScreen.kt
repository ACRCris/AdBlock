package com.copiloto.addblock.ui.screens

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.copiloto.addblock.ui.navigation.NavDestination
import com.copiloto.addblock.ui.state.AppsUiState
import com.copiloto.addblock.ui.state.FirewallUiState
import com.copiloto.addblock.ui.theme.Purple700

@OptIn(ExperimentalMaterial3Api::class)
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
    var showHowItWorksSheet by remember { mutableStateOf(false) }
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

    // Modal Bottom Sheet - Cómo funciona
    if (showHowItWorksSheet) {
        HowItWorksSheet(
            onDismiss = { showHowItWorksSheet = false },
            onPrivacyPolicyClick = {
                showHowItWorksSheet = false
                showPrivacyPolicy = true
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("AdBlock Firewall") },
                actions = {
                    // Icono de información que lleva a "Cómo funciona"
                    IconButton(onClick = { showHowItWorksSheet = true }) {
                        Icon(
                            Icons.Outlined.Info,
                            contentDescription = "Información"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Purple700,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White,
                    actionIconContentColor = Color.White
                )
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
                onMoreInfoClick = { showHowItWorksSheet = true }
            )
            1 -> AppsScreen(
                modifier = Modifier.padding(paddingValues),
                uiState = appsState,
                onSearchQueryChange = onSearchQueryChange,
                onFilterChange = onFilterChange,
                onToggleAppBlocked = onToggleAppBlocked,
                onShowCriticalAppsChange = onShowCriticalAppsChange
            )
        }
    }
}
