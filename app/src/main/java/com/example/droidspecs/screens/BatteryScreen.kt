package com.example.droidspecs.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.droidspecs.DashboardViewModel

@Composable
fun BatteryScreen(
    viewModel: DashboardViewModel = hiltViewModel()
) {
    val battery = viewModel.batteryDetails.collectAsStateWithLifecycle().value

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        BatteryHeaderCard(
            level = battery.levelPercent,
            status = battery.status,
            health = battery.health,
            tech = battery.technology
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            "POWER MONITOR",
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
            modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
        )

        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(1.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(modifier = Modifier.fillMaxWidth()) {
                    PowerItem(
                        title = "Voltage",
                        value = battery.voltage,
                        icon = Icons.Rounded.Bolt,
                        color = Color(0xFFFF9800),
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    PowerItem(
                        title = "Temperature",
                        value = battery.temperature,
                        icon = Icons.Rounded.Thermostat,
                        color = Color(0xFFF44336),
                        modifier = Modifier.weight(1f)
                    )
                }

                Divider(modifier = Modifier.padding(vertical = 16.dp), color = MaterialTheme.colorScheme.outlineVariant.copy(alpha=0.3f))

                Row(modifier = Modifier.fillMaxWidth()) {
                    PowerItem(
                        title = "Current (Now)",
                        value = battery.currentNow,
                        icon = Icons.Rounded.ElectricMeter,
                        color = Color(0xFF2196F3),
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    PowerItem(
                        title = "Wattage",
                        value = battery.powerWattage,
                        icon = Icons.Rounded.FlashOn,
                        color = Color(0xFFFFC107),
                        modifier = Modifier.weight(1f)
                    )
                }

                Divider(modifier = Modifier.padding(vertical = 16.dp), color = MaterialTheme.colorScheme.outlineVariant.copy(alpha=0.3f))

                Row(modifier = Modifier.fillMaxWidth()) {
                    PowerItem(
                        title = "Design Capacity",
                        value = battery.designCapacity,
                        icon = Icons.Rounded.BatteryStd,
                        color = Color(0xFF4CAF50),
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    PowerItem(
                        title = "Charge Counter",
                        value = battery.chargeCounter,
                        icon = Icons.Rounded.HourglassTop,
                        color = Color(0xFF9C27B0),
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(12.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Rounded.Power, null, tint = Color.Gray)
                    Spacer(modifier = Modifier.width(12.dp))
                    Text("Power Source", fontWeight = FontWeight.SemiBold)
                }
                Text(battery.powerSource, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
            }
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}


@Composable
fun PowerItem(
    title: String,
    value: String,
    icon: ImageVector,
    color: Color,
    modifier: Modifier = Modifier
) {
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        Surface(
            color = color.copy(alpha = 0.1f),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.size(36.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(icon, null, tint = color, modifier = Modifier.size(20.dp))
            }
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(title, style = MaterialTheme.typography.labelSmall, color = Color.Gray)
            Text(value, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun BatteryHeaderCard(level: Int, status: String, health: String, tech: String) {
    val progressAnim by animateFloatAsState(targetValue = level / 100f, animationSpec = tween(1000), label = "bat")

    val levelColor = when {
        level <= 15 -> MaterialTheme.colorScheme.error
        level <= 30 -> Color(0xFFFF9800)
        else -> MaterialTheme.colorScheme.primary
    }

    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .padding(20.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(contentAlignment = Alignment.Center) {
                Canvas(modifier = Modifier.size(70.dp)) {
                    drawArc(
                        color = levelColor.copy(alpha = 0.2f),
                        startAngle = 0f, sweepAngle = 360f, useCenter = false,
                        style = Stroke(width = 8.dp.toPx(), cap = StrokeCap.Round)
                    )
                    drawArc(
                        color = levelColor,
                        startAngle = -90f, sweepAngle = 360 * progressAnim, useCenter = false,
                        style = Stroke(width = 8.dp.toPx(), cap = StrokeCap.Round)
                    )
                }
                Text(
                    text = "$level%",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = levelColor
                )
            }

            Spacer(modifier = Modifier.width(20.dp))

            Column {
                Text(status, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    StatusChip(health, if(health == "Good") Color(0xFF4CAF50) else Color.Red)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("•  $tech", style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
                }
            }
        }
    }
}

@Composable
fun StatusChip(text: String, color: Color) {
    Surface(
        color = color.copy(alpha = 0.1f),
        shape = RoundedCornerShape(4.dp)
    ) {
        Text(
            text = text,
            color = color,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
        )
    }
}