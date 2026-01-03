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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.droidspecs.DashboardViewModel
import com.example.droidspecs.R

fun formatNetUsage(bytesPerSec: Long): String {
    if (bytesPerSec <= 0) return "0 KB/s"

    val kb = bytesPerSec / 1024.0
    val mb = kb / 1024.0

    return when {
        mb >= 1 -> String.format("%.1f MB/s", mb)
        kb >= 1 -> String.format("%.0f KB/s", kb)
        else -> "$bytesPerSec B/s"
    }
}

@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel = hiltViewModel()
) {
    val ram by viewModel.ram.collectAsStateWithLifecycle()
    val battery by viewModel.battery.collectAsStateWithLifecycle()
    val storage by viewModel.storage.collectAsStateWithLifecycle()
    val network by viewModel.network.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        SectionTitle("SYSTEM")
        DeviceIdentityCard(
            name = viewModel.deviceOverview.deviceName,
            manufacturer = viewModel.deviceOverview.manufacturer,
            androidVer = viewModel.deviceOverview.androidVersion,
            releaseName = viewModel.deviceOverview.releaseName,
            networkType = network.type,
            networkUsage = formatNetUsage(network.speedBytesPerSec)
        )

        Spacer(modifier = Modifier.height(16.dp))

        SectionTitle("MEMORY (RAM)")
        RamCard(
            usedPercent = ram.usedPercent,
            usedGb = ram.getFormattedUsed(),
            totalGb = ram.getFormattedTotal(),
            freeGb = ram.getFormattedFree()
        )

        Spacer(modifier = Modifier.height(16.dp))

        SectionTitle("STORAGE")
        StorageCardFull(
            usedGb = storage.getFormattedTotal(),
            freeGb = storage.getFormattedFree(),
            usedPercent = storage.getUsedPercent()
        )

        Spacer(modifier = Modifier.height(16.dp))

        BatterySensorRow(
            batteryLevel = battery.levelPercent,
            isCharging = battery.isCharging,
            batteryTemp = battery.temperature,
            batteryVoltage = battery.voltage,
            sensorCount = viewModel.sensorCount
        )

        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
fun SectionTitle(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.labelMedium,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
        modifier = Modifier.padding(bottom = 8.dp, start = 4.dp)
    )
}

@Composable
fun DeviceIdentityCard(
    name: String,
    manufacturer: String,
    androidVer: String,
    releaseName: String,
    networkType: String,
    networkUsage: String
) {
    val isWifi = networkType.equals("Wi-Fi", ignoreCase = true)
    val isOffline = networkType == "No Connection" || networkType == "Offline"

    val badgeText = when {
        isWifi -> "Wi-Fi"
        isOffline -> "Offline"
        else -> "Cellular"
    }

    val badgeIcon = when {
        isWifi -> Icons.Rounded.Wifi
        isOffline -> Icons.Rounded.SignalCellularOff
        else -> Icons.Rounded.SignalCellularAlt
    }

    val badgeColor = if (isOffline) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary

    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(6.dp),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.size(56.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        BrandIcon(
                            manufacturer = manufacturer,
                            modifier = Modifier.size(32.dp),
                            tint = Color.Unspecified
                        )
                    }
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = manufacturer.uppercase(),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.secondary
                    )
                    Text(
                        text = name,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                Surface(
                    color = badgeColor.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(50),
                    border = BorderStroke(1.dp, badgeColor.copy(alpha = 0.2f))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = badgeIcon,
                            contentDescription = null,
                            tint = badgeColor,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = badgeText,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = badgeColor
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.4f),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(1f, fill = false)
                ) {
                    Text(
                        text = "Android $androidVer • $releaseName",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                if (!isOffline) {
                    Surface(
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.ArrowDownward,
                                contentDescription = null,
                                modifier = Modifier.size(14.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Usage: $networkUsage",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun RamCard(
    usedPercent: Int,
    usedGb: String,
    totalGb: String,
    freeGb: String
) {
    val trackColor = MaterialTheme.colorScheme.surfaceVariant
    val statusColor = when {
        usedPercent > 85 -> MaterialTheme.colorScheme.error
        usedPercent > 70 -> MaterialTheme.colorScheme.tertiary
        else -> Color(0xFF4CAF50)
    }

    val statusText = when {
        usedPercent > 85 -> "Critical"
        usedPercent > 70 -> "Heavy"
        else -> "Optimal"
    }

    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(6.dp),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Memory,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Memory (RAM)",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Surface(
                    color = statusColor.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(50),
                    border = BorderStroke(1.dp, statusColor.copy(alpha = 0.5f))
                ) {
                    Text(
                        text = statusText,
                        color = statusColor,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(contentAlignment = Alignment.Center) {
                    val animatedProgress by animateFloatAsState(
                        targetValue = usedPercent / 100f,
                        animationSpec = tween(1000),
                        label = "RamAnimation"
                    )

                    Canvas(modifier = Modifier.size(80.dp)) {
                        drawArc(
                            color = trackColor,
                            startAngle = 0f, sweepAngle = 360f, useCenter = false,
                            style = Stroke(width = 18f, cap = StrokeCap.Round)
                        )
                        drawArc(
                            color = statusColor,
                            startAngle = -90f, sweepAngle = 360 * animatedProgress, useCenter = false,
                            style = Stroke(width = 18f, cap = StrokeCap.Round)
                        )
                    }
                    Text(
                        text = "$usedPercent%",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.width(20.dp))

                Column(modifier = Modifier.weight(1f)) {
                    RamStatRow("Used", usedGb, statusColor)
                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                    RamStatRow("Free", freeGb, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f))

                    Text(
                        text = "Total Capacity: $totalGb",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.Gray,
                        modifier = Modifier.padding(top = 6.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun RamStatRow(label: String, value: String, color: Color) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = color
        )
    }
}

@Composable
fun StorageCardFull(usedGb: String, freeGb: String, usedPercent: Float) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(6.dp),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Rounded.Storage, null, tint = Color(0xFF009688))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Internal Storage", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                }
                Text(
                    text = "$freeGb Free",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            LinearProgressIndicator(
                progress = { usedPercent },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(10.dp)
                    .clip(RoundedCornerShape(50)),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.surfaceVariant,
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                Text("Total: $usedGb", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
            }
        }
    }
}

@Composable
fun BatterySensorRow(
    batteryLevel: Int,
    isCharging: Boolean,
    batteryTemp: Float,
    batteryVoltage: Int,
    sensorCount: Int
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        InfoCardCompact(
            modifier = Modifier.weight(1f),
            title = "BATTERY",
            icon = Icons.Rounded.BatteryStd,
            accentColor = if (isCharging) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error,
            mainValue = "$batteryLevel%",
            status = if (isCharging) "Charging" else "${batteryTemp.toInt()}°C · $batteryVoltage mV"
        )

        InfoCardCompact(
            modifier = Modifier.weight(1f),
            title = "SENSORS",
            icon = Icons.Rounded.Speed,
            accentColor = MaterialTheme.colorScheme.tertiary,
            mainValue = sensorCount.toString(),
            status = "Active Modules"
        )
    }
}

@Composable
private fun InfoCardCompact(
    modifier: Modifier = Modifier,
    title: String,
    icon: ImageVector,
    accentColor: Color,
    mainValue: String,
    status: String
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(3.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = accentColor,
                    modifier = Modifier.size(16.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = mainValue,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = status,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun BrandIcon(
    manufacturer: String,
    modifier: Modifier = Modifier,
    tint: Color
) {
    val name = manufacturer.lowercase()
    val brandRes = when {
        name.contains("google") -> R.drawable.logo_google
        name.contains("samsung") -> R.drawable.logo_samsung
        name.contains("xiaomi") || name.contains("redmi") || name.contains("poco") -> R.drawable.logo_xiaomi
        name.contains("oneplus") -> R.drawable.logo_oneplus
        name.contains("realme") -> R.drawable.logo_realme
        name.contains("motorola") || name.contains("moto") -> R.drawable.logo_motorola
        name.contains("oppo") -> R.drawable.logo_oppo
        else -> R.drawable.logo_android
    }

    if (brandRes != R.drawable.logo_android) {
        Icon(
            painter = painterResource(id = brandRes),
            contentDescription = "$manufacturer Logo",
            modifier = modifier,
            tint = tint
        )
    } else {
        Icon(
            imageVector = Icons.Rounded.Android,
            contentDescription = "Generic Device",
            modifier = modifier,
            tint = tint
        )
    }
}