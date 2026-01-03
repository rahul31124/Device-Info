package com.example.droidspecs.Screens

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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.droidspecs.DashboardViewModel
import com.example.droidspecs.R

private val PrimaryPurple = Color(0xFFda9100)
private val LightPurpleBg = Color(0xFFF3E5F5)
private val SurfaceWhite = Color.White
private val TextBlack = Color(0xFF1A1C1E)
private val TextGray = Color(0xFF757575)

@Composable
fun CpuScreen(
    viewModel: DashboardViewModel = hiltViewModel()
) {
    val details by viewModel.cpuDetails.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F9FA))
            .verticalScroll(rememberScrollState())
            .padding(20.dp)
    ) {

        ProcessorHeader(
            name = details.processorName,
            hardware = android.os.Build.HARDWARE
        )

        Spacer(modifier = Modifier.height(20.dp))


        Text(
            text = "Core Specs",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = TextGray,
            modifier = Modifier.padding(bottom = 12.dp, start = 4.dp)
        )

        Row(Modifier.fillMaxWidth()) {
            SpecCard(
                label = "Core Count",
                value = "${details.coreCount}",
                icon = Icons.Rounded.Apps,
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(12.dp))
            SpecCard(
                label = "Architecture",
                value = details.architecture,
                icon = Icons.Rounded.Architecture,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(Modifier.fillMaxWidth()) {
            SpecCard(
                label = "BogoMIPS",
                value = details.bogomips,
                icon = Icons.Rounded.Speed,
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(12.dp))
            SpecCard(
                label = "Bootloader",
                value = android.os.Build.BOOTLOADER,
                icon = Icons.Rounded.SystemSecurityUpdate,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "System Status",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = TextGray,
            modifier = Modifier.padding(bottom = 12.dp, start = 4.dp)
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(2.dp)
        ) {
            Row(
                modifier = Modifier.padding(20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(50),
                    color = LightPurpleBg,
                    modifier = Modifier.size(48.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(Icons.Rounded.Timer, null, tint = PrimaryPurple)
                    }
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column {
                    Text("System Uptime", style = MaterialTheme.typography.labelMedium, color = TextGray)

                    Text(
                        text = details.uptime,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        color = PrimaryPurple
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Advanced Details",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = TextGray,
            modifier = Modifier.padding(bottom = 12.dp, start = 4.dp)
        )

        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            TechnicalRow(
                label = "CPU Governor",
                value = details.governor.uppercase(),
                icon = Icons.Rounded.SettingsInputComponent
            )
            TechnicalRow(
                label = "Scaling Driver",
                value = details.scalingDriver,
                icon = Icons.Rounded.DeveloperBoard
            )
            TechnicalRow(
                label = "Supported ABIs",
                value = details.supportedAbis,
                icon = Icons.Rounded.Code
            )
            TechnicalRow(
                label = "GPU Renderer",
                value = "Adreno / Mali (GLES)",
                icon = Icons.Rounded.VideogameAsset
            )
        }

        Spacer(modifier = Modifier.height(50.dp))
    }
}

@Composable
fun SpecCard(
    label: String,
    value: String,
    icon: ImageVector,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
        shape = RoundedCornerShape(3.dp),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Icon(icon, null, tint = PrimaryPurple, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = TextGray
            )


            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = TextBlack
            )
        }
    }
}

@Composable
fun TechnicalRow(
    label: String,
    value: String,
    icon: ImageVector
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
        shape = RoundedCornerShape(3.dp),
        elevation = CardDefaults.cardElevation(1.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                color = LightPurpleBg,
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.size(40.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(icon, null, tint = PrimaryPurple, modifier = Modifier.size(20.dp))
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column {

                Text(
                    text = label,
                    style = MaterialTheme.typography.labelMedium,
                    color = TextGray
                )
                Text(
                    text = value,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    color = TextBlack
                )
            }
        }
    }
}

@Composable
fun ProcessorHeader(name: String, hardware: String) {

    val logoRes = when {
        name.contains("Snapdragon", ignoreCase = true) ||
                name.contains("Qualcomm", ignoreCase = true) -> R.drawable.logo_qualcomm


        name.contains("Exynos", ignoreCase = true) -> R.drawable.logo_exynos
        name.contains("Tensor", ignoreCase = true) -> R.drawable.logo_google_tensor
        name.contains("Dimensity", ignoreCase = true) -> R.drawable.logo_mediatek

        else -> null
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(150.dp)
            .clip(RoundedCornerShape(5.dp))
            .background(
                Brush.horizontalGradient(
                    colors = listOf(Color(0xFF000000), Color(0xFFdaa520))
                )
            )
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawCircle(Color.White.copy(alpha=0.1f), center = center.copy(x = size.width * 0.9f), radius = 180f)
            drawCircle(Color.White.copy(alpha=0.05f), center = center.copy(x = size.width * 0.1f), radius = 120f)
        }

        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                color = Color.White,
                shape = RoundedCornerShape(18.dp),
                modifier = Modifier.size(72.dp),
                shadowElevation = 4.dp
            ) {
                Box(contentAlignment = Alignment.Center) {
                    if (logoRes != null) {
                        Icon(
                            painter = painterResource(id = logoRes),
                            contentDescription = null,
                            tint = Color.Unspecified,
                            modifier = Modifier.size(48.dp)
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Rounded.Memory,
                            contentDescription = null,
                            tint = PrimaryPurple,
                            modifier = Modifier.size(36.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(20.dp))

            Column {
                Text(
                    text = "PROCESSOR / SoC",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White.copy(alpha = 0.7f),
                    letterSpacing = 1.sp
                )
                Text(
                    text = name,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White,
                    maxLines = 2
                )
                Text(
                    text = hardware.uppercase(),
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.8f)
                )
            }
        }
    }
}