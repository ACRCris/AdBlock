package com.copiloto.addblock.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.outlined.Call
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material.icons.outlined.Contacts
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.copiloto.addblock.ui.state.FirewallStatus
import com.copiloto.addblock.ui.state.FirewallUiState
import com.copiloto.addblock.ui.theme.AmberDark
import com.copiloto.addblock.ui.theme.AmberLight
import com.copiloto.addblock.ui.theme.BlueDark
import com.copiloto.addblock.ui.theme.BlueLight
import com.copiloto.addblock.ui.theme.Gray50
import com.copiloto.addblock.ui.theme.Gray500
import com.copiloto.addblock.ui.theme.GreenDark
import com.copiloto.addblock.ui.theme.GreenLight
import com.copiloto.addblock.ui.theme.RedDark
import com.copiloto.addblock.ui.theme.RedLight

@Composable
fun InicioScreen(
    modifier: Modifier = Modifier,
    uiState: FirewallUiState,
    onStartFirewall: () -> Unit,
    onStopFirewall: () -> Unit,
    onTestBlock: () -> Unit,
    onMoreInfoClick: () -> Unit = {}
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Gray50)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // Card de Estado
        StatusCard(
            uiState = uiState,
            onStartFirewall = onStartFirewall,
            onStopFirewall = onStopFirewall,
            onTestBlock = onTestBlock
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Card de Método de bloqueo
        BlockingMethodCard(onMoreInfoClick = onMoreInfoClick)

        Spacer(modifier = Modifier.height(16.dp))

        // Card de Recomendaciones
        RecommendationsCard()
    }
}

@Composable
fun StatusCard(
    uiState: FirewallUiState,
    onStartFirewall: () -> Unit,
    onStopFirewall: () -> Unit,
    onTestBlock: () -> Unit
) {
    val isRunning = uiState.status == FirewallStatus.RUNNING
    val isStarting = uiState.status == FirewallStatus.STARTING
    val isError = uiState.status == FirewallStatus.ERROR
    val needsPermission = uiState.status == FirewallStatus.NEEDS_PERMISSION

    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            // Status Chip
            StatusChip(status = uiState.status)

            Spacer(modifier = Modifier.height(12.dp))

            // Título
            Text(
                text = when (uiState.status) {
                    FirewallStatus.RUNNING -> "Firewall activo"
                    FirewallStatus.STARTING -> "Iniciando firewall..."
                    FirewallStatus.ERROR -> "Error en firewall"
                    FirewallStatus.NEEDS_PERMISSION -> "Permiso requerido"
                    FirewallStatus.STOPPED -> "Firewall detenido"
                },
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Métricas
            Text(
                text = "Apps bloqueadas: ${uiState.blockedAppsCount}",
                fontSize = 14.sp,
                color = Color.Gray
            )
            if (uiState.lastChangeTime.isNotEmpty()) {
                Text(
                    text = "Último cambio: ${uiState.lastChangeTime}",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }

            // Mensaje de error si existe
            if (isError && uiState.errorMessage != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = RedLight
                ) {
                    Text(
                        text = uiState.errorMessage,
                        fontSize = 12.sp,
                        color = RedDark,
                        modifier = Modifier.padding(8.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Botón principal
            Button(
                onClick = when {
                    isRunning -> onStopFirewall
                    needsPermission -> onStartFirewall
                    else -> onStartFirewall
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                enabled = !isStarting,
                colors = ButtonDefaults.buttonColors(
                    containerColor = when {
                        isRunning -> GreenDark
                        isError -> RedDark
                        else -> GreenDark
                    }
                ),
                contentPadding = PaddingValues(vertical = 14.dp)
            ) {
                Text(
                    text = when {
                        isRunning -> "Detener"
                        isStarting -> "Iniciando..."
                        needsPermission -> "Conceder permiso"
                        isError -> "Reintentar"
                        else -> "Iniciar"
                    },
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            // Botón secundario: Probar bloqueo (solo cuando está corriendo)
            if (isRunning) {
                Spacer(modifier = Modifier.height(12.dp))

                OutlinedButton(
                    onClick = onTestBlock,
                    modifier = Modifier.align(Alignment.End),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Text("Probar bloqueo")
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        Icons.Outlined.ChevronRight,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun StatusChip(status: FirewallStatus) {
    val (backgroundColor, textColor, text) = when (status) {
        FirewallStatus.RUNNING -> Triple(GreenLight, GreenDark, "RUNNING")
        FirewallStatus.STARTING -> Triple(AmberLight, AmberDark, "STARTING")
        FirewallStatus.ERROR -> Triple(RedLight, RedDark, "ERROR")
        FirewallStatus.NEEDS_PERMISSION -> Triple(AmberLight, AmberDark, "PERMISO")
        FirewallStatus.STOPPED -> Triple(Color(0xFFEEEEEE), Gray500, "STOPPED")
    }

    Surface(
        shape = RoundedCornerShape(16.dp),
        color = backgroundColor
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = text,
                color = textColor,
                fontWeight = FontWeight.Medium,
                fontSize = 12.sp
            )
            if (status == FirewallStatus.RUNNING) {
                Spacer(modifier = Modifier.width(4.dp))
                Icon(
                    Icons.Default.KeyboardArrowDown,
                    contentDescription = null,
                    tint = textColor,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

@Composable
fun BlockingMethodCard(onMoreInfoClick: () -> Unit = {}) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = "Método de bloqueo",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Opción de bloqueo por app
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = BlueLight
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    // Icono de check
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .background(BlueDark, RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.Check,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Bloquear por app (MVP)",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.Black
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Controla acceso a red por aplicación.\nNo inspecciona contenido.",
                            fontSize = 13.sp,
                            color = Color.Gray,
                            lineHeight = 18.sp
                        )
                    }

                    // Botón Más info
                    OutlinedButton(
                        onClick = onMoreInfoClick,
                        shape = RoundedCornerShape(20.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "Más info",
                            fontSize = 12.sp
                        )
                        Spacer(modifier = Modifier.width(2.dp))
                        Icon(
                            Icons.Outlined.ChevronRight,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun RecommendationsCard() {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = "Recomendaciones",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Advertencia
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = AmberLight
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Warning,
                            contentDescription = null,
                            tint = AmberDark,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "No bloquees apps del criticas del sistema",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.Black
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Lista de apps del sistema
                    SystemAppsList()
                }
            }
        }
    }
}

@Composable
private fun SystemAppsList() {
    // Lista de apps críticas con iconos Material 3 Outlined (consistente)
    val systemApps = listOf(
        SystemAppItem(Icons.Outlined.Settings, "Ajustes"),
        SystemAppItem(Icons.Outlined.Call, "Teléfono"),
        SystemAppItem(Icons.Outlined.ChatBubbleOutline, "Mensajes"),
        SystemAppItem(Icons.Outlined.Email, "Correo"),
        SystemAppItem(Icons.Outlined.Contacts, "Contactos")
    )

    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        systemApps.forEach { app ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Icon(
                    imageVector = app.icon,
                    contentDescription = app.name,
                    modifier = Modifier.size(20.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = app.name,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

private data class SystemAppItem(
    val icon: ImageVector,
    val name: String
)

@Composable
fun TrustMessageCard() {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = "🔒 Tu privacidad es importante",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.Black
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "No inspeccionamos tu contenido. Solo controlamos el acceso de red por aplicación.",
                fontSize = 14.sp,
                color = Color.Gray,
                lineHeight = 20.sp
            )
        }
    }
}
