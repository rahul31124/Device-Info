package com.example.droidspecs.Screens

import android.app.ActivityManager
import android.content.Context
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.droidspecs.DashboardViewModel
import com.example.droidspecs.screens.SectionHeader

private val TextGray = Color(0xFF757575)
private val BgColor = Color(0xFFF8F9FA)

@Composable
fun MemoryScreen(
    viewModel: DashboardViewModel = hiltViewModel()
) {
    val mem = viewModel.memoryDetails.collectAsStateWithLifecycle().value

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BgColor)
            .verticalScroll(rememberScrollState())
            .padding(20.dp)
    ) {
        SectionHeader(title = "Physical & Virtual Memory", icon = Icons.Rounded.Memory)

        MemoryUsageCard(
            title = "Physical RAM",
            used = mem.ramUsed,
            total = mem.ramTotal,
            percent = mem.ramPercent,
            icon = Icons.Rounded.Memory,
            color = Color(0xFF6200EA)
        )

        Spacer(modifier = Modifier.height(16.dp))

        MemoryUsageCard(
            title = "ZRAM (Swap)",
            used = mem.zramUsed,
            total = mem.zramTotal,
            percent = mem.zramPercent,
            icon = Icons.Rounded.SwapHoriz,
            color = Color(0xFFFF9800)
        )

        Spacer(modifier = Modifier.height(24.dp))

        SectionHeader(title = "Storage Partitions", icon = Icons.Rounded.Storage)

        MemoryUsageCard(
            title = "Internal Storage (Data)",
            used = mem.romUsed,
            total = mem.romTotal,
            percent = mem.romPercent,
            icon = Icons.Rounded.Storage,
            color = Color(0xFF009688)
        )

        Spacer(modifier = Modifier.height(16.dp))

        MemoryUsageCard(
            title = "System Root",
            used = mem.systemUsed,
            total = mem.systemTotal,
            percent = mem.systemPercent,
            icon = Icons.Rounded.Dns,
            color = Color(0xFF607D8B)
        )

        Spacer(modifier = Modifier.height(24.dp))


        SectionHeader(title = "Advanced Capabilities", icon = Icons.Rounded.DeveloperBoard)


        AdvancedStatsCard()

        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
fun AdvancedStatsCard() {
    val context = LocalContext.current


    val am = remember { context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager }
    val runtime = remember { Runtime.getRuntime() }

    val heapSize = "${am.memoryClass} MB"
    val largeHeap = "${am.largeMemoryClass} MB"
    val isLowRam = if (am.isLowRamDevice) "Yes (Go Edition)" else "No"

    val jvmTotal = runtime.totalMemory() / (1024 * 1024)
    val jvmFree = runtime.freeMemory() / (1024 * 1024)
    val jvmUsed = "${jvmTotal - jvmFree} MB used"

    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(1.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(vertical = 8.dp)) {
            StatRow(label = "Java VM Heap", value = jvmUsed)


            StatRow(label = "Standard App Limit", value = heapSize)
            StatRow(label = "Large App Limit", value = largeHeap)

            StatRow(label = "Low RAM Flag", value = isLowRam, isLast = true)
        }
    }
}

@Composable
fun MemoryUsageCard(
    title: String,
    used: String,
    total: String,
    percent: Int,
    icon: ImageVector,
    color: Color
) {
    val animatedProgress by animateFloatAsState(
        targetValue = percent / 100f,
        animationSpec = tween(1000),
        label = "progress"
    )

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(1.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Surface(
                color = color.copy(alpha = 0.1f),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.size(48.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = color,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    Text(
                        text = "$percent%",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = color
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))


                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(50))
                        .background(color.copy(alpha = 0.15f))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(animatedProgress)
                            .fillMaxHeight()
                            .clip(RoundedCornerShape(50))
                            .background(color)
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "$used / $total",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextGray
                )
            }
        }
    }
}

@Composable
private fun StatRow(label: String, value: String, isLast: Boolean = false) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 12.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = TextGray,
            fontWeight = FontWeight.Medium
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.SemiBold,
            color = Color.Black,
            fontFamily = FontFamily.Monospace
        )
    }

    if (!isLast) {
        Divider(
            color = Color.LightGray.copy(alpha = 0.15f),
            thickness = 1.dp,
            modifier = Modifier.padding(horizontal = 20.dp)
        )
    }
}