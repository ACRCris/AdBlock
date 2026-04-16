package com.copiloto.addblock.ui.screens

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.outlined.PhoneAndroid
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.copiloto.addblock.ui.components.AppTopBar
import com.copiloto.addblock.ui.theme.AmberDark
import com.copiloto.addblock.ui.theme.AmberLight
import com.copiloto.addblock.ui.theme.BlueDark
import com.copiloto.addblock.ui.theme.GreenDark

@Composable
fun PrivacyPolicyScreen(
    onBackClick: () -> Unit,
    onClearDataClick: () -> Unit = {}
) {
    Scaffold(
        topBar = {
            AppTopBar(
                title = "Política de privacidad",
                canNavigateBack = true,
                onNavigateBack = onBackClick
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF5F5F5))
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Card: En resumen
            SummaryCard()

            // Sección: Qué hace el firewall
            WhatFirewallDoesSection()

            // Sección: Qué se guarda en tu dispositivo
            WhatIsSavedSection(onClearDataClick = onClearDataClick)

            // Sección: Limitaciones
            LimitationsSection()

            Spacer(modifier = Modifier.height(8.dp))

            // Botón Cerrar
            Button(
                onClick = onBackClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp),
                shape = RoundedCornerShape(24.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = BlueDark
                )
            ) {
                Text(
                    text = "Cerrar",
                    fontSize = 16.sp,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun SummaryCard() {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = Color(0xFFF3E8FF) // Light purple
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Header con icono de candado
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFFFEF3C7)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Lock,
                        contentDescription = null,
                        tint = Color(0xFFF59E0B),
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "En resumen",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Lista de puntos
            PrivacyCheckItem("No recopilamos datos personales.")
            Spacer(modifier = Modifier.height(8.dp))
            PrivacyCheckItem("No inspeccionamos el contenido del tráfico (HTTPS/HTTP).")
            Spacer(modifier = Modifier.height(8.dp))
            PrivacyCheckItem("No enviamos datos a servidores externos.")
        }
    }
}

@Composable
private fun PrivacyCheckItem(text: String) {
    Row(
        verticalAlignment = Alignment.Top
    ) {
        Icon(
            Icons.Default.Check,
            contentDescription = null,
            tint = GreenDark,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = text,
            fontSize = 14.sp,
            color = Color(0xFF374151),
            lineHeight = 20.sp
        )
    }
}

@Composable
private fun WhatFirewallDoesSection() {
    Column {
        Text(
            text = "Qué hace el firewall",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )

        Spacer(modifier = Modifier.height(8.dp))

        ElevatedCard(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.elevatedCardColors(
                containerColor = Color.White
            )
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.Top
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFFDBEAFE)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Info,
                        contentDescription = null,
                        tint = BlueDark,
                        modifier = Modifier.size(18.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "Decide qué apps pueden acceder a internet utilizando una VPN local de Android.",
                    fontSize = 14.sp,
                    color = Color(0xFF374151),
                    lineHeight = 20.sp,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun WhatIsSavedSection(onClearDataClick: () -> Unit) {
    Column {
        Text(
            text = "Qué se guarda en tu dispositivo",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )

        Spacer(modifier = Modifier.height(8.dp))

        ElevatedCard(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.elevatedCardColors(
                containerColor = Color.White
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                // Item 1: Lista de apps bloqueadas
                SavedDataItem(
                    icon = Icons.Outlined.PhoneAndroid,
                    iconBackgroundColor = Color(0xFFE0E7FF),
                    iconTint = BlueDark,
                    title = "Lista de apps bloqueadas",
                    subtitle = "Estado del firewall (activo/detenido)"
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Item 2: Historial local
                SavedDataItem(
                    icon = Icons.Default.Info,
                    iconBackgroundColor = Color(0xFFDBEAFE),
                    iconTint = BlueDark,
                    title = "(Opcional) Historial local de funcionamiento",
                    subtitle = "(sin URLs ni contenido)"
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Botón borrar datos
                OutlinedButton(
                    onClick = onClearDataClick,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "Borrar datos locales",
                        color = Color(0xFF374151)
                    )
                }
            }
        }
    }
}

@Composable
private fun SavedDataItem(
    icon: ImageVector,
    iconBackgroundColor: Color,
    iconTint: Color,
    title: String,
    subtitle: String
) {
    Row(
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(iconBackgroundColor),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                icon,
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(18.dp)
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(
                text = title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Black
            )
            Text(
                text = subtitle,
                fontSize = 13.sp,
                color = Color.Gray
            )
        }
    }
}

@Composable
private fun LimitationsSection() {
    Column {
        Text(
            text = "Limitaciones",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )

        Spacer(modifier = Modifier.height(8.dp))

        ElevatedCard(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.elevatedCardColors(
                containerColor = AmberLight
            )
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.Top
            ) {
                Icon(
                    Icons.Default.Warning,
                    contentDescription = null,
                    tint = AmberDark,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "Algunas notificaciones pueden llegar vía servicios del sistema (Google Play Services).",
                    fontSize = 14.sp,
                    color = Color(0xFF374151),
                    lineHeight = 20.sp,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}
