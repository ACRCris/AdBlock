package com.copiloto.addblock.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.drawable.toBitmap
import com.copiloto.addblock.ui.model.InstalledApp
import com.copiloto.addblock.ui.state.AppFilter
import com.copiloto.addblock.ui.state.AppsUiState
import com.copiloto.addblock.ui.theme.AmberDark
import com.copiloto.addblock.ui.theme.AmberLight
import com.copiloto.addblock.ui.theme.BlueLight
import com.copiloto.addblock.ui.theme.BlueDark
import com.copiloto.addblock.ui.theme.Gray50
import com.copiloto.addblock.ui.theme.RedDark
import com.copiloto.addblock.ui.theme.RedLight

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppsScreen(
    modifier: Modifier = Modifier,
    uiState: AppsUiState,
    onSearchQueryChange: (String) -> Unit,
    onFilterChange: (AppFilter) -> Unit,
    onToggleAppBlocked: (InstalledApp) -> Unit,
    onShowCriticalAppsChange: (Boolean) -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Gray50)
            .padding(16.dp)
    ) {
        // Barra de búsqueda
        OutlinedTextField(
            value = uiState.searchQuery,
            onValueChange = onSearchQueryChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Buscar apps...") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Buscar") },
            shape = RoundedCornerShape(12.dp),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Filtros
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(AppFilter.entries) { filter ->
                FilterChip(
                    selected = uiState.selectedFilter == filter,
                    onClick = { onFilterChange(filter) },
                    label = { Text(filter.label, fontSize = 12.sp) }
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Toggle para mostrar apps críticas
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Mostrar apps críticas",
                fontSize = 14.sp,
                color = Color.Gray
            )
            Spacer(modifier = Modifier.weight(1f))
            Switch(
                checked = uiState.showCriticalApps,
                onCheckedChange = onShowCriticalAppsChange
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Contador de apps
        Text(
            text = "${uiState.filteredApps.size} apps",
            fontSize = 12.sp,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Lista de apps
        if (uiState.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            val listState = rememberLazyListState()

            LazyColumn(
                state = listState,
                modifier = Modifier.testTag("apps_list"),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(
                    items = uiState.filteredApps,
                    key = { it.packageName }
                ) { app ->
                    AppListItem(
                        app = app,
                        isBlocked = app.isBlocked,
                        onBlockedChange = { onToggleAppBlocked(app) }
                    )
                }
            }
        }
    }
}

@Composable
fun AppListItem(
    app: InstalledApp,
    isBlocked: Boolean,
    onBlockedChange: () -> Unit
) {
    val isCritical = remember(app.packageName) { app.isCriticalApp() }

    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icono de la app
            if (app.icon != null) {
                val bitmap = remember(app.packageName) { app.icon.toBitmap().asImageBitmap() }
                Image(
                    bitmap = bitmap,
                    contentDescription = app.appName,
                    modifier = Modifier.size(40.dp)
                )
            } else {
                Surface(
                    modifier = Modifier.size(40.dp),
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFFE0E0E0)
                ) { }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = app.appName,
                        fontWeight = FontWeight.Medium,
                        fontSize = 16.sp,
                        maxLines = 1
                    )

                    // Badge de sistema
                    if (app.isSystemApp) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Badge(
                            text = "Sistema",
                            backgroundColor = BlueLight,
                            textColor = BlueDark
                        )
                    }

                    // Badge de bloqueado - usar el parámetro isBlocked
                    if (isBlocked) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Badge(
                            text = "Blocked",
                            backgroundColor = RedLight,
                            textColor = RedDark
                        )
                    }

                    // Badge de crítica
                    if (isCritical) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(
                            Icons.Default.Warning,
                            contentDescription = "App crítica",
                            tint = AmberDark,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                Text(
                    text = app.packageName,
                    fontSize = 12.sp,
                    color = Color.Gray,
                    maxLines = 1
                )
            }

            Switch(
                checked = isBlocked,
                onCheckedChange = { onBlockedChange() }
            )
        }
    }
}

@Composable
fun Badge(
    text: String,
    backgroundColor: Color,
    textColor: Color
) {
    Surface(
        shape = RoundedCornerShape(4.dp),
        color = backgroundColor
    ) {
        Text(
            text = text,
            fontSize = 10.sp,
            color = textColor,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
        )
    }
}

/**
 * Extensión para determinar si una app es crítica
 */
fun InstalledApp.isCriticalApp(): Boolean {
    val criticalPackages = listOf(
        "com.android.settings",
        "com.android.phone",
        "com.android.dialer",
        "com.android.systemui",
        "com.android.launcher",
        "com.google.android.dialer",
        "com.samsung.android.dialer",
        "com.android.emergency"
    )
    return criticalPackages.any { packageName.contains(it, ignoreCase = true) }
}
