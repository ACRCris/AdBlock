package com.copiloto.addblock.ui.state

import com.copiloto.addblock.ui.model.InstalledApp

/**
 * Estados del firewall VPN
 */
enum class FirewallStatus {
    STOPPED,
    STARTING,
    RUNNING,
    NEEDS_PERMISSION,
    ERROR
}

/**
 * Estado de la UI principal del firewall
 */
data class FirewallUiState(
    val status: FirewallStatus = FirewallStatus.STOPPED,
    val blockedAppsCount: Int = 0,
    val lastChangeTime: String = "",
    val errorMessage: String? = null
)

/**
 * Estado de la pantalla de Apps
 */
data class AppsUiState(
    val apps: List<InstalledApp> = emptyList(),
    val filteredApps: List<InstalledApp> = emptyList(),
    val isLoading: Boolean = false,
    val searchQuery: String = "",
    val selectedFilter: AppFilter = AppFilter.ALL,
    val showCriticalApps: Boolean = false,
    val isSelectionMode: Boolean = false,
    val selectedApps: Set<String> = emptySet(),
    val updateTrigger: Long = 0L  // Incrementar para forzar recomposición
)

/**
 * Filtros disponibles para la lista de apps
 */
enum class AppFilter(val label: String) {
    ALL("Todas"),
    BLOCKED("Bloqueadas"),
    USER("Usuario"),
    SYSTEM("Sistema")
}

/**
 * Evento de actividad del firewall
 */
data class ActivityEvent(
    val id: Long = System.currentTimeMillis(),
    val message: String,
    val timestamp: String,
    val type: EventType
)

/**
 * Tipos de eventos de actividad
 */
enum class EventType {
    START,
    STOP,
    BLOCK,
    UNBLOCK,
    ERROR
}
