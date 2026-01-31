  package com.copiloto.addblock.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.copiloto.addblock.data.BlocklistRepository
import com.copiloto.addblock.ui.model.InstalledApp
import com.copiloto.addblock.ui.state.AppFilter
import com.copiloto.addblock.ui.state.AppsUiState
import com.copiloto.addblock.ui.state.FirewallStatus
import com.copiloto.addblock.ui.state.FirewallUiState
import com.copiloto.addblock.util.Logger
import com.copiloto.addblock.util.PackageUtils
import com.copiloto.addblock.vpn.FirewallVpnService
import com.copiloto.addblock.vpn.VpnController
import com.copiloto.addblock.vpn.VpnState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * ViewModel para la pantalla principal con Jetpack Compose.
 * Gestiona el estado del firewall y la lista de apps.
 */
class FirewallViewModel(application: Application) : AndroidViewModel(application),
    FirewallVpnService.StateListener {

    companion object {
        private const val TAG = "FirewallViewModel"
    }

    private val blocklistRepository = BlocklistRepository(application)

    // Estado del firewall
    private val _firewallState = MutableStateFlow(FirewallUiState())
    val firewallState: StateFlow<FirewallUiState> = _firewallState.asStateFlow()

    // Estado de la lista de apps
    private val _appsState = MutableStateFlow(AppsUiState())
    val appsState: StateFlow<AppsUiState> = _appsState.asStateFlow()

    // Lista completa de apps (sin filtrar)
    private var allApps: List<InstalledApp> = emptyList()

    init {
        // Registrar listener de estado VPN
        FirewallVpnService.setStateListener(this)
        updateVpnState(VpnController.getState())

        // Cargar apps
        loadInstalledApps()
    }

    /**
     * Carga las apps instaladas del usuario
     */
    fun loadInstalledApps() {
        viewModelScope.launch {
            _appsState.update { it.copy(isLoading = true) }

            withContext(Dispatchers.IO) {
                val apps = PackageUtils.getInstalledUserApps(getApplication())
                val blocked = blocklistRepository.blockedPackages

                // Marcar apps bloqueadas
                apps.forEach { app ->
                    app.isBlocked = blocked.contains(app.packageName)
                }

                allApps = apps

                withContext(Dispatchers.Main) {
                    _appsState.update {
                        it.copy(
                            apps = apps,
                            filteredApps = filterApps(apps, it.searchQuery, it.selectedFilter, it.showCriticalApps),
                            isLoading = false
                        )
                    }
                    _firewallState.update {
                        it.copy(blockedAppsCount = blocked.size)
                    }
                    Logger.d(TAG, "Loaded ${apps.size} apps, ${blocked.size} blocked")
                }
            }
        }
    }

    /**
     * Actualiza la búsqueda de apps
     */
    fun onSearchQueryChange(query: String) {
        _appsState.update { state ->
            state.copy(
                searchQuery = query,
                filteredApps = filterApps(allApps, query, state.selectedFilter, state.showCriticalApps)
            )
        }
    }

    /**
     * Actualiza el filtro seleccionado
     */
    fun onFilterChange(filter: AppFilter) {
        _appsState.update { state ->
            state.copy(
                selectedFilter = filter,
                filteredApps = filterApps(allApps, state.searchQuery, filter, state.showCriticalApps)
            )
        }
    }

    /**
     * Toggle para mostrar/ocultar apps críticas
     */
    fun onShowCriticalAppsChange(show: Boolean) {
        _appsState.update { state ->
            state.copy(
                showCriticalApps = show,
                filteredApps = filterApps(allApps, state.searchQuery, state.selectedFilter, show)
            )
        }
    }

    /**
     * Toggle del estado bloqueado de una app
     */
    fun toggleAppBlocked(app: InstalledApp) {
        val newBlockedState = !app.isBlocked

        // Actualizar en el repositorio primero
        blocklistRepository.setBlocked(app.packageName, newBlockedState)

        // Crear una nueva lista con el estado actualizado
        // Esto es necesario para que Compose detecte el cambio
        allApps = allApps.map { existingApp ->
            if (existingApp.packageName == app.packageName) {
                InstalledApp(
                    existingApp.appName,
                    existingApp.packageName,
                    existingApp.icon,
                    newBlockedState,
                    existingApp.isSystemApp
                )
            } else {
                existingApp
            }
        }

        // Actualizar contador de bloqueadas
        _firewallState.update {
            it.copy(
                blockedAppsCount = blocklistRepository.blockedCount,
                lastChangeTime = "Ahora"
            )
        }

        // Actualizar la UI con la nueva lista
        val currentState = _appsState.value
        _appsState.value = currentState.copy(
            apps = allApps,
            filteredApps = filterApps(allApps, currentState.searchQuery, currentState.selectedFilter, currentState.showCriticalApps),
            updateTrigger = currentState.updateTrigger + 1
        )

        Logger.d(TAG, "App ${app.packageName} blocked: $newBlockedState")

        // Si VPN está corriendo, reiniciar para aplicar cambios
        if (VpnController.isRunning()) {
            VpnController.restartIfRunning(getApplication())
        }
    }

    /**
     * Inicia el firewall VPN
     */
    fun startFirewall() {
        if (blocklistRepository.blockedCount == 0) {
            Logger.w(TAG, "No apps blocked, cannot start firewall")
            _firewallState.update {
                it.copy(
                    status = FirewallStatus.ERROR,
                    errorMessage = "Selecciona al menos una app para bloquear"
                )
            }
            return
        }
        VpnController.start(getApplication())
    }

    /**
     * Detiene el firewall VPN
     */
    fun stopFirewall() {
        VpnController.stop(getApplication())
    }

    /**
     * Filtra las apps según criterios
     */
    private fun filterApps(
        apps: List<InstalledApp>,
        query: String,
        filter: AppFilter,
        showCritical: Boolean
    ): List<InstalledApp> {
        return apps.filter { app ->
            val matchesSearch = query.isEmpty() ||
                    app.appName.contains(query, ignoreCase = true) ||
                    app.packageName.contains(query, ignoreCase = true)

            val matchesFilter = when (filter) {
                AppFilter.ALL -> true
                AppFilter.BLOCKED -> app.isBlocked
                AppFilter.USER -> !app.isSystemApp
                AppFilter.SYSTEM -> app.isSystemApp
            }

            val matchesCritical = showCritical || !isCriticalApp(app.packageName)

            matchesSearch && matchesFilter && matchesCritical
        }
    }

    /**
     * Determina si una app es crítica del sistema
     */
    private fun isCriticalApp(packageName: String): Boolean {
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

    /**
     * Actualiza el estado de la UI según el estado del VPN
     */
    private fun updateVpnState(state: VpnState) {
        val status = when (state) {
            VpnState.STOPPED -> FirewallStatus.STOPPED
            VpnState.STARTING -> FirewallStatus.STARTING
            VpnState.RUNNING -> FirewallStatus.RUNNING
            VpnState.ERROR -> FirewallStatus.ERROR
        }
        _firewallState.update { it.copy(status = status, errorMessage = null) }
    }

    // Implementación de FirewallVpnService.StateListener
    override fun onStateChanged(state: VpnState) {
        viewModelScope.launch(Dispatchers.Main) {
            updateVpnState(state)
        }
    }

    override fun onCleared() {
        super.onCleared()
        FirewallVpnService.setStateListener(null)
    }
}
