package com.copiloto.addblock.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SheetState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Modal Bottom Sheet que explica cómo funciona el firewall.
 * Diseño escaneable en 10-15 segundos según lineamientos.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HowItWorksSheet(
    onDismiss: () -> Unit,
    onPrivacyPolicyClick: () -> Unit,
    sheetState: SheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface,
        dragHandle = null
    ) {
        HowItWorksContent(
            onDismiss = onDismiss,
            onPrivacyPolicyClick = onPrivacyPolicyClick
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun HowItWorksContent(
    onDismiss: () -> Unit,
    onPrivacyPolicyClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 24.dp)
    ) {
        // Header
        HowItWorksHeader(onDismiss = onDismiss)

        // Contenido scrolleable
        Column(
            modifier = Modifier
                .weight(1f, fill = false)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Sección 1: Lo esencial
            EssentialSection()

            // Sección 2: Cómo se bloquea
            HowBlockingWorksSection()

            // Sección 3: Limitaciones importantes
            LimitationsSection()

            // Sección 4: Privacidad
            PrivacySection()
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Footer fijo
        FooterSection(
            onDismiss = onDismiss,
            onPrivacyPolicyClick = onPrivacyPolicyClick
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun HowItWorksHeader(onDismiss: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Cómo funciona AdBlock Firewall",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            IconButton(onClick = onDismiss) {
                Icon(
                    Icons.Default.Close,
                    contentDescription = "Cerrar"
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Chips informativos - estilo como en la imagen
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Chip azul para VPN local
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = Color(0xFF3B82F6)
            ) {
                Text(
                    text = "VPN local",
                    color = Color.White,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                )
            }
            // Chip gris para sin inspección
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = Color(0xFFE5E7EB)
            ) {
                Text(
                    text = "Sin inspección de contenido",
                    color = Color(0xFF374151),
                    fontSize = 12.sp,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                )
            }
        }
    }
}

@Composable
private fun EssentialSection() {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.elevatedCardColors(
            containerColor = Color(0xFFF0F4FF)
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Icono de candado amarillo
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFFFEF3C7)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Lock,
                        contentDescription = null,
                        tint = Color(0xFFF59E0B),
                        modifier = Modifier.size(24.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "AdBlock Firewall",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            BulletPoint("Usa la API de VPN de Android para bloquear apps seleccionadas.")
            BulletPoint("No enviamos datos fuera del dispositivo.")
            BulletPoint("Las apps bloqueadas quedan sin internet.")
        }
    }
}

@Composable
private fun HowBlockingWorksSection() {
    var showDetails by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = "Cómo se bloquea",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Diagrama visual como en la imagen
        ElevatedCard(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.elevatedCardColors(
                containerColor = Color(0xFFF0F9FF)
            ),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                // Fila principal del diagrama
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // App icon con label debajo
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.width(50.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color(0xFF3B82F6)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.Person,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(26.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "App",
                            fontSize = 11.sp,
                            color = Color.Gray
                        )
                    }

                    // Arrow 1
                    Text(
                        text = "→",
                        fontSize = 18.sp,
                        color = Color(0xFF9CA3AF),
                        modifier = Modifier.padding(bottom = 18.dp)
                    )

                    // Túnel VPN local (chip azul) - con padding para alinear
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = Color(0xFF3B82F6),
                        modifier = Modifier.padding(bottom = 18.dp)
                    ) {
                        Text(
                            text = "Túnel VPN local",
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp)
                        )
                    }

                    // Arrow 2
                    Text(
                        text = "→",
                        fontSize = 18.sp,
                        color = Color(0xFF9CA3AF),
                        modifier = Modifier.padding(bottom = 18.dp)
                    )

                    // No reenvía paquetes - con padding para alinear
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .width(60.dp)
                            .padding(bottom = 18.dp)
                    ) {
                        Text(
                            text = "No reenvía",
                            fontSize = 11.sp,
                            color = Color(0xFF374151)
                        )
                        Text(
                            text = "paquetes",
                            fontSize = 11.sp,
                            color = Color(0xFF374151)
                        )
                    }

                    // Arrow 3
                    Text(
                        text = "→",
                        fontSize = 18.sp,
                        color = Color(0xFF9CA3AF),
                        modifier = Modifier.padding(bottom = 18.dp)
                    )

                    // Icono de internet bloqueado (X roja) - con padding para alinear
                    Box(
                        modifier = Modifier
                            .padding(bottom = 18.dp)
                            .size(36.dp)
                            .clip(CircleShape)
                            .border(2.dp, Color(0xFFEF4444), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.Close,
                            contentDescription = null,
                            tint = Color(0xFFEF4444),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Botón ver detalles técnicos alineado a la derecha
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    OutlinedButton(
                        onClick = { showDetails = !showDetails },
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = Color(0xFF6B7280)
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = "Ver detalles técnicos",
                            fontSize = 12.sp
                        )
                    }
                }

                // Detalles expandibles
                AnimatedVisibility(
                    visible = showDetails,
                    enter = expandVertically(),
                    exit = shrinkVertically()
                ) {
                    Column(
                        modifier = Modifier.padding(top = 12.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "• Las apps seleccionadas se enrutan por un túnel VPN local.",
                            fontSize = 13.sp,
                            color = Color(0xFF6B7280)
                        )
                        Text(
                            text = "• El túnel no reenvía paquetes, cortando su acceso a internet.",
                            fontSize = 13.sp,
                            color = Color(0xFF6B7280)
                        )
                        Text(
                            text = "• Las apps no bloqueadas usan la conexión normal.",
                            fontSize = 13.sp,
                            color = Color(0xFF6B7280)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun LimitationsSection() {
    Column {
        Text(
            text = "Limitaciones importantes",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        ElevatedCard(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.elevatedCardColors(
                containerColor = Color.White
            ),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                LimitationItem(
                    icon = Icons.Default.Warning,
                    iconBackgroundColor = Color(0xFFDBEAFE),
                    iconTint = Color(0xFF3B82F6),
                    title = "Una VPN a la vez:",
                    description = "Android permite una VPN activa. Si usas otra VPN, esta puede desconectarse."
                )

                LimitationItem(
                    icon = Icons.Default.Notifications,
                    iconBackgroundColor = Color(0xFFFEF3C7),
                    iconTint = Color(0xFFF59E0B),
                    title = "Notificaciones push:",
                    description = "Algunas apps pueden seguir recibiendo notificaciones vía Google Play Services."
                )

                LimitationItem(
                    icon = Icons.Default.Lock,
                    iconBackgroundColor = Color(0xFFDBEAFE),
                    iconTint = Color(0xFF3B82F6),
                    title = "Apps del sistema:",
                    description = "Algunos servicios del sistema no se pueden bloquear."
                )
            }
        }
    }
}

@Composable
private fun LimitationItem(
    icon: ImageVector,
    iconBackgroundColor: Color,
    iconTint: Color,
    title: String,
    description: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
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
        Text(
            text = buildAnnotatedString {
                withStyle(style = SpanStyle(fontWeight = FontWeight.SemiBold)) {
                    append(title)
                }
                append(" ")
                append(description)
            },
            fontSize = 14.sp,
            color = Color(0xFF374151),
            lineHeight = 20.sp,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun PrivacySection() {
    Column {
        Text(
            text = "Privacidad",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        ElevatedCard(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.elevatedCardColors(
                containerColor = Color(0xFFF0FDF4)
            ),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                PrivacyCheckItem("Sin inspección o registro de tráfico")
                PrivacyCheckItem("Sin recolección de datos")
                PrivacyCheckItem("No requiere internet para funcionar")
            }
        }
    }
}

@Composable
private fun PrivacyCheckItem(text: String) {
    Row(
        modifier = Modifier.padding(vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(20.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(Color(0xFF10B981)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Default.Check,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(14.dp)
            )
        }
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = text,
            fontSize = 14.sp,
            color = Color(0xFF374151)
        )
    }
}

@Composable
private fun FooterSection(
    onDismiss: () -> Unit,
    onPrivacyPolicyClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(
            onClick = onDismiss,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Entendido")
        }

        Spacer(modifier = Modifier.height(8.dp))

        TextButton(
            onClick = onPrivacyPolicyClick
        ) {
            Text("Política de privacidad")
        }
    }
}

@Composable
private fun BulletPoint(text: String) {
    Row(
        modifier = Modifier.padding(vertical = 2.dp)
    ) {
        Text(
            text = "•",
            modifier = Modifier.padding(end = 8.dp),
            fontWeight = FontWeight.Bold
        )
        Text(
            text = text,
            fontSize = 14.sp
        )
    }
}
