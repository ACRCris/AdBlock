package com.copiloto.addblock.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import com.copiloto.addblock.ui.theme.*

@Composable
fun InfoScreen(
    modifier: Modifier = Modifier,
    onPrivacyPolicyClick: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Gray50)
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        EssentialInfoCard()
        HowBlockingWorksCard()
        LimitationsInfoCard()
        PrivacyInfoCard(onPrivacyPolicyClick = onPrivacyPolicyClick)
        Spacer(modifier = Modifier.height(8.dp))
    }
}

@Composable
private fun EssentialInfoCard() {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.elevatedCardColors(containerColor = BlueLight),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier.size(40.dp).clip(RoundedCornerShape(8.dp)).background(Color(0xFFFEF3C7)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Lock, null, tint = Color(0xFFF59E0B), modifier = Modifier.size(24.dp))
                }
                Spacer(Modifier.width(12.dp))
                Text("AdBlock Firewall", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.height(12.dp))
            BulletText("Usa la API de VPN de Android para bloquear apps seleccionadas.")
            BulletText("No enviamos datos fuera del dispositivo.")
            BulletText("Las apps bloqueadas quedan sin internet.")
        }
    }
}

@Composable
private fun BulletText(text: String) {
    Row(modifier = Modifier.padding(vertical = 2.dp)) {
        Text("•", modifier = Modifier.padding(end = 8.dp), fontWeight = FontWeight.Bold)
        Text(text, fontSize = 14.sp, color = Color(0xFF374151))
    }
}

@Composable
private fun HowBlockingWorksCard() {
    var showDetails by remember { mutableStateOf(false) }
    Column {
        Text("Cómo se bloquea", fontSize = 18.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(12.dp))
        ElevatedCard(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.elevatedCardColors(containerColor = Color(0xFFF0F9FF)),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.width(50.dp)) {
                        Box(
                            modifier = Modifier.size(44.dp).clip(RoundedCornerShape(10.dp)).background(Color(0xFF3B82F6)),
                            contentAlignment = Alignment.Center
                        ) { Icon(Icons.Default.Person, null, tint = Color.White, modifier = Modifier.size(26.dp)) }
                        Spacer(Modifier.height(4.dp))
                        Text("App", fontSize = 11.sp, color = Color.Gray)
                    }
                    Text("→", fontSize = 18.sp, color = Color(0xFF9CA3AF), modifier = Modifier.padding(bottom = 18.dp))
                    Surface(shape = RoundedCornerShape(20.dp), color = Color(0xFF3B82F6), modifier = Modifier.padding(bottom = 18.dp)) {
                        Text("Túnel VPN local", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Medium, modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp))
                    }
                    Text("→", fontSize = 18.sp, color = Color(0xFF9CA3AF), modifier = Modifier.padding(bottom = 18.dp))
                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.width(60.dp).padding(bottom = 18.dp)) {
                        Text("No reenvía", fontSize = 11.sp, color = Color(0xFF374151))
                        Text("paquetes", fontSize = 11.sp, color = Color(0xFF374151))
                    }
                    Text("→", fontSize = 18.sp, color = Color(0xFF9CA3AF), modifier = Modifier.padding(bottom = 18.dp))
                    Box(
                        modifier = Modifier.padding(bottom = 18.dp).size(36.dp).clip(CircleShape).border(2.dp, Color(0xFFEF4444), CircleShape),
                        contentAlignment = Alignment.Center
                    ) { Icon(Icons.Default.Close, null, tint = Color(0xFFEF4444), modifier = Modifier.size(20.dp)) }
                }
                Spacer(Modifier.height(12.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    OutlinedButton(onClick = { showDetails = !showDetails }, shape = RoundedCornerShape(8.dp)) {
                        Text(if (showDetails) "Ocultar detalles" else "Ver detalles técnicos", fontSize = 12.sp)
                    }
                }
                if (showDetails) {
                    Spacer(Modifier.height(12.dp))
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFFF3F4F6)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text("Detalles técnicos", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                            Spacer(Modifier.height(8.dp))
                            Text("• Usa VpnService API de Android\n• Sin root ni modificaciones\n• Intercepta y descarta paquetes\n• Sin servidor remoto", fontSize = 12.sp, color = Color(0xFF6B7280), lineHeight = 18.sp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun LimitationsInfoCard() {
    Column {
        Text("Limitaciones importantes", fontSize = 18.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(12.dp))
        ElevatedCard(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.elevatedCardColors(containerColor = Color.White),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                LimitItem(Icons.Default.Warning, AmberLight, AmberDark, "Una VPN a la vez:", "Android permite una VPN activa. Si usas otra VPN, esta puede desconectarse.")
                LimitItem(Icons.Default.Notifications, AmberLight, AmberDark, "Notificaciones push:", "Algunas apps pueden seguir recibiendo notificaciones vía Google Play Services.")
                LimitItem(Icons.Default.Lock, Color(0xFFDBEAFE), BlueDark, "Apps del sistema:", "Algunos servicios del sistema no se pueden bloquear.")
            }
        }
    }
}

@Composable
private fun LimitItem(icon: ImageVector, bgColor: Color, tint: Color, title: String, desc: String) {
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.Top) {
        Box(modifier = Modifier.size(32.dp).clip(RoundedCornerShape(8.dp)).background(bgColor), contentAlignment = Alignment.Center) {
            Icon(icon, null, tint = tint, modifier = Modifier.size(18.dp))
        }
        Spacer(Modifier.width(12.dp))
        Text(buildAnnotatedString { withStyle(SpanStyle(fontWeight = FontWeight.SemiBold)) { append(title) }; append(" $desc") }, fontSize = 14.sp, color = Color(0xFF374151), lineHeight = 20.sp, modifier = Modifier.weight(1f))
    }
}

@Composable
private fun PrivacyInfoCard(onPrivacyPolicyClick: () -> Unit) {
    Column {
        Text("Privacidad", fontSize = 18.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(12.dp))
        ElevatedCard(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.elevatedCardColors(containerColor = Color.White),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.Top) {
                    Box(modifier = Modifier.size(32.dp).clip(RoundedCornerShape(8.dp)).background(Color(0xFFD1FAE5)), contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.Info, null, tint = Color(0xFF059669), modifier = Modifier.size(18.dp))
                    }
                    Spacer(Modifier.width(12.dp))
                    Text("Todo el procesamiento ocurre localmente en tu dispositivo. No recopilamos ni enviamos datos.", fontSize = 14.sp, color = Color(0xFF374151), lineHeight = 20.sp, modifier = Modifier.weight(1f))
                }
                Spacer(Modifier.height(12.dp))
                TextButton(onClick = onPrivacyPolicyClick, modifier = Modifier.align(Alignment.End)) {
                    Text("Ver política de privacidad", color = BlueDark)
                }
            }
        }
    }
}
