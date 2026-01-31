package com.copiloto.addblock.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.copiloto.addblock.ui.state.ActivityEvent
import com.copiloto.addblock.ui.state.EventType
import com.copiloto.addblock.ui.theme.BlueDark
import com.copiloto.addblock.ui.theme.Gray50
import com.copiloto.addblock.ui.theme.Gray500
import com.copiloto.addblock.ui.theme.Green500
import com.copiloto.addblock.ui.theme.RedDark

@Composable
fun ActividadScreen(
    modifier: Modifier = Modifier
) {
    // Datos de ejemplo - conectar con repositorio real
    val events = remember {
        listOf(
            ActivityEvent(
                id = 1L,
                message = "Firewall iniciado",
                timestamp = "Hace 2 min",
                type = EventType.START
            ),
            ActivityEvent(
                id = 2L,
                message = "YouTube bloqueado",
                timestamp = "Hace 5 min",
                type = EventType.BLOCK
            ),
            ActivityEvent(
                id = 3L,
                message = "Instagram bloqueado",
                timestamp = "Hace 10 min",
                type = EventType.BLOCK
            ),
            ActivityEvent(
                id = 4L,
                message = "Chrome desbloqueado",
                timestamp = "Hace 15 min",
                type = EventType.UNBLOCK
            ),
            ActivityEvent(
                id = 5L,
                message = "Firewall detenido",
                timestamp = "Hace 1 hora",
                type = EventType.STOP
            )
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Gray50)
            .padding(16.dp)
    ) {
        Text(
            text = "Historial de actividad",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Registro de eventos del firewall",
            fontSize = 14.sp,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (events.isEmpty()) {
            // Estado vacío
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Sin actividad reciente",
                    fontSize = 16.sp,
                    color = Gray500
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Los eventos aparecerán aquí cuando uses el firewall",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(events, key = { it.id }) { event ->
                    EventItem(event)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Botón de exportar diagnóstico
        OutlinedButton(
            onClick = { /* Exportar diagnóstico */ },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(Icons.Default.Download, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Exportar diagnóstico")
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "El diagnóstico no incluye datos personales ni contenido de tráfico.",
            fontSize = 12.sp,
            color = Color.Gray,
            modifier = Modifier.padding(horizontal = 8.dp)
        )
    }
}

@Composable
fun EventItem(event: ActivityEvent) {
    val (icon, iconColor) = getEventIconAndColor(event.type)

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
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconColor
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = event.message,
                    fontWeight = FontWeight.Medium,
                    fontSize = 14.sp
                )
                Text(
                    text = event.timestamp,
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
        }
    }
}

@Composable
private fun getEventIconAndColor(type: EventType): Pair<ImageVector, Color> {
    return when (type) {
        EventType.START -> Icons.Default.PlayArrow to Green500
        EventType.STOP -> Icons.Default.Stop to Gray500
        EventType.BLOCK -> Icons.Default.Block to RedDark
        EventType.UNBLOCK -> Icons.Default.CheckCircle to BlueDark
        EventType.ERROR -> Icons.Default.Error to RedDark
    }
}
