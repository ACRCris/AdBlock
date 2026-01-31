package com.copiloto.addblock.ui

import android.content.Intent
import android.net.VpnService
import android.os.Bundle
import android.text.Html
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.copiloto.addblock.R
import com.copiloto.addblock.ui.screens.MainScreen
import com.copiloto.addblock.ui.theme.AdBlockFirewallTheme
import com.copiloto.addblock.ui.viewmodel.FirewallViewModel
import com.copiloto.addblock.util.Logger

/**
 * Activity principal con Jetpack Compose.
 * Implementa el nuevo diseño Material 3 del firewall.
 */
class ComposeMainActivity : ComponentActivity() {

    companion object {
        private const val TAG = "ComposeMainActivity"
    }

    private val viewModel: FirewallViewModel by viewModels()

    private val vpnPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            Logger.d(TAG, "VPN permission granted")
            viewModel.startFirewall()
        } else {
            Logger.w(TAG, "VPN permission denied")
            Toast.makeText(
                this,
                "Se requiere permiso VPN para bloquear apps",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            AdBlockFirewallTheme {
                val firewallState by viewModel.firewallState.collectAsState()
                val appsState by viewModel.appsState.collectAsState()

                MainScreen(
                    firewallState = firewallState,
                    appsState = appsState,
                    onStartFirewall = { requestVpnPermissionAndStart() },
                    onStopFirewall = { viewModel.stopFirewall() },
                    onTestBlock = { showTestBlockInfo() },
                    onHelpClick = { showHelpDialog() },
                    onPrivacyClick = { showPrivacyDialog() },
                    onSearchQueryChange = { viewModel.onSearchQueryChange(it) },
                    onFilterChange = { viewModel.onFilterChange(it) },
                    onToggleAppBlocked = { viewModel.toggleAppBlocked(it) },
                    onShowCriticalAppsChange = { viewModel.onShowCriticalAppsChange(it) }
                )
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.loadInstalledApps()
    }

    private fun requestVpnPermissionAndStart() {
        val state = viewModel.firewallState.value
        if (state.blockedAppsCount == 0) {
            Toast.makeText(
                this,
                "Selecciona al menos una app para bloquear",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        val intent = VpnService.prepare(this)
        if (intent != null) {
            vpnPermissionLauncher.launch(intent)
        } else {
            viewModel.startFirewall()
        }
    }

    private fun showTestBlockInfo() {
        AlertDialog.Builder(this)
            .setTitle("Probar bloqueo")
            .setMessage(
                "Para verificar que el firewall funciona:\n\n" +
                "1. Abre una app bloqueada\n" +
                "2. Intenta usar funciones que requieran internet\n" +
                "3. Deberían fallar o mostrar error de conexión"
            )
            .setPositiveButton("Entendido", null)
            .show()
    }

    private fun showHelpDialog() {
        AlertDialog.Builder(this)
            .setTitle(R.string.how_it_works_title)
            .setMessage(Html.fromHtml(getString(R.string.how_it_works_content), Html.FROM_HTML_MODE_LEGACY))
            .setPositiveButton(R.string.got_it, null)
            .show()
    }

    private fun showPrivacyDialog() {
        AlertDialog.Builder(this)
            .setTitle("Política de privacidad")
            .setMessage(
                "AdBlock Firewall respeta tu privacidad:\n\n" +
                "• No recopilamos datos personales\n" +
                "• No inspeccionamos el contenido del tráfico\n" +
                "• Solo controlamos qué apps pueden acceder a internet\n" +
                "• Todos los datos se almacenan localmente en tu dispositivo"
            )
            .setPositiveButton("Cerrar", null)
            .show()
    }
}
