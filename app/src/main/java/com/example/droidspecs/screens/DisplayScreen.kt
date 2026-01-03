package com.example.droidspecs.screens

import android.app.Activity
import android.media.MediaDrm
import android.view.WindowManager
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.droidspecs.DashboardViewModel
import java.util.UUID

private val PrimaryPurple = Color(0xFFfcc200)
private val LightPurpleBg = Color(0xFFF3E5F5)
private val TextGray = Color(0xFF757575)
private val BgColor = Color(0xFFF8F9FA)

@Composable
fun DisplayScreen(
    viewModel: DashboardViewModel = hiltViewModel()
) {
    val details = viewModel.displayDetails

    var showDeadPixelTest by remember { mutableStateOf(false) }
    var showTouchTest by remember { mutableStateOf(false) }

    val drmInfo = remember { getDrmInfo() }
    val isL1 = drmInfo["securityLevel"] == "L1"

    when {
        details.resolution.startsWith("1440") || details.resolution.contains("3200") -> "QHD+"
        details.resolution.startsWith("1080") || details.resolution.contains("2400") -> "FHD+"
        details.resolution.startsWith("720") -> "HD+"
        else -> "Display"
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BgColor)
            .verticalScroll(rememberScrollState())
            .padding(20.dp)
    ) {




        SectionHeader(title = "Panel Specifications", icon = Icons.Rounded.AspectRatio)

        Card(
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(12.dp),
            elevation = CardDefaults.cardElevation(1.dp)
        ) {
            Column(modifier = Modifier.padding(vertical = 4.dp)) {
                DisplayInfoRow(label = "Resolution", value = details.resolution)
                DisplayInfoRow(label = "Density (DPI)", value = details.densityDpi)
                DisplayInfoRow(label = "Exact PPI", value = details.exactPpi)
                DisplayInfoRow(label = "Scale Factor", value = details.scaleFactor)
                DisplayInfoRow(label = "Refresh Rate", value = details.refreshRate, isLast = true)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        SectionHeader(title = "Features & Security", icon = Icons.Rounded.SettingsBrightness)

        Card(
            colors = CardDefaults.cardColors(containerColor = if(isL1) Color(0xFFE8F5E9) else Color(0xFFFFEBEE)),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    if(isL1) Icons.Rounded.CheckCircle else Icons.Rounded.Warning,
                    null,
                    tint = if(isL1) Color(0xFF2E7D32) else Color(0xFFC62828),
                    modifier = Modifier.size(28.dp)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(if(isL1) "Widevine L1 Certified" else "Widevine L3 (Low Security)", fontWeight = FontWeight.Bold, color = Color.Black)
                    Text(if(isL1) "Full HD/4K Streaming Supported" else "SD (480p) Streaming Only", style = MaterialTheme.typography.bodySmall, color = TextGray)
                }
            }
        }

        Card(
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(12.dp),
            elevation = CardDefaults.cardElevation(1.dp)
        ) {
            Column(modifier = Modifier.padding(vertical = 4.dp)) {
                DisplayInfoRow(label = "HDR Support", value = details.hdrCapabilities)
                DisplayInfoRow(label = "Wide Color Gamut", value = details.wideColorGamut)
                DisplayInfoRow(label = "Brightness Mode", value = details.adaptiveBrightness)
                DisplayInfoRow(label = "Current Brightness", value = details.brightnessLevel)
                DisplayInfoRow(label = "Screen Timeout", value = details.screenTimeout)
                DisplayInfoRow(label = "Orientation", value = details.orientation, isLast = true)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
        Text(
            "TOOLS",
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = TextGray,
            modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            TestCapsule(
                text = "Dead Pixels",
                icon = Icons.Rounded.GridOn,
                color = Color(0xFFE91E63),
                onClick = { showDeadPixelTest = true }
            )
            TestCapsule(
                text = "Multi-Touch",
                icon = Icons.Rounded.TouchApp,
                color = Color(0xFF2196F3),
                onClick = { showTouchTest = true }
            )
        }



    }

    if (showDeadPixelTest) DeadPixelTest { showDeadPixelTest = false }
    if (showTouchTest) TouchTest { showTouchTest = false }
}


fun getDrmInfo(): Map<String, String> {
    val widevineUuid = UUID.fromString("edef8ba9-79d6-4ace-a3c8-27dcd51d21ed")
    return try {
        val mediaDrm = MediaDrm(widevineUuid)
        val level = mediaDrm.getPropertyString("securityLevel")
        mediaDrm.close()
        mapOf("securityLevel" to level)
    } catch (e: Exception) {
        mapOf("securityLevel" to "Unknown")
    }
}


@Composable
fun TestCapsule(
    text: String,
    icon: ImageVector,
    color: Color,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(50),
        color = Color.White,
        border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = 0.3f)),
        modifier = Modifier.height(40.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.padding(horizontal = 16.dp)
        ) {
            Icon(icon, null, tint = color, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(text, fontWeight = FontWeight.SemiBold, fontSize = 13.sp, color = Color.Black.copy(0.8f))
        }
    }
}

@Composable
fun SectionHeader(title: String, icon: ImageVector) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(bottom = 8.dp, start = 4.dp)) {
        Icon(icon, null, tint = PrimaryPurple, modifier = Modifier.size(18.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = Color.Black)
    }
}

@Composable
fun DisplayInfoRow(label: String, value: String, isLast: Boolean = false) {
    Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 12.dp)) {
        Text(label, style = MaterialTheme.typography.labelMedium, color = TextGray, fontWeight = FontWeight.Medium)
        Spacer(modifier = Modifier.height(4.dp))
        Text(value, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold, color = Color.Black)
    }
    if (!isLast) Divider(color = Color.LightGray.copy(alpha = 0.15f), thickness = 1.dp, modifier = Modifier.padding(horizontal = 20.dp))
}


@Composable
fun DeadPixelTest(onDismiss: () -> Unit) {
    val colors = listOf(Color.White, Color.Black, Color.Red, Color.Green, Color.Blue)
    var colorIndex by remember { mutableStateOf(0) }

    val view = LocalView.current
    val window = (view.context as Activity).window

    DisposableEffect(Unit) {
        val controller = WindowCompat.getInsetsController(window, view)
        controller.hide(WindowInsetsCompat.Type.systemBars())
        controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        onDispose { controller.show(WindowInsetsCompat.Type.systemBars()) }
    }

    Dialog(onDismissRequest = onDismiss, properties = DialogProperties(usePlatformDefaultWidth = false, decorFitsSystemWindows = false)) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(colors[colorIndex])
                .clickable {
                    if (colorIndex < colors.lastIndex) colorIndex++ else onDismiss()
                },
            contentAlignment = Alignment.Center
        ) {
            if (colorIndex == 0) {
                Surface(shape = RoundedCornerShape(12.dp), color = Color.White.copy(0.9f)) {
                    Text("Tap to cycle colors.\nCheck for dead pixels.", modifier = Modifier.padding(16.dp), color = Color.Black)
                }
            }
        }
    }
}

@Composable
fun TouchTest(onDismiss: () -> Unit) {
    val points = remember { mutableStateMapOf<Long, Offset>() }

    val view = LocalView.current
    val window = (view.context as Activity).window

    DisposableEffect(Unit) {
        val controller = WindowCompat.getInsetsController(window, view)
        controller.hide(WindowInsetsCompat.Type.systemBars())
        onDispose { controller.show(WindowInsetsCompat.Type.systemBars()) }
    }

    Dialog(onDismissRequest = onDismiss, properties = DialogProperties(usePlatformDefaultWidth = false, decorFitsSystemWindows = false)) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
                .pointerInput(Unit) {
                    awaitEachGesture {
                        while (true) {
                            val event = awaitPointerEvent()
                            event.changes.forEach { change ->
                                if (change.pressed) points[change.id.value] = change.position
                                else points.remove(change.id.value)
                            }
                        }
                    }
                }
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                points.forEach { (_, pos) ->
                    drawCircle(Color.Cyan, radius = 80f, center = pos)
                    drawCircle(Color.White, radius = 85f, center = pos, style = Stroke(width = 5f))
                }
            }
            if (points.isNotEmpty()) {
                Text("${points.size}", color = Color.White, fontSize = 80.sp, fontWeight = FontWeight.Bold, modifier = Modifier.align(Alignment.Center))
            } else {
                Text("Touch with multiple fingers", color = Color.Gray, modifier = Modifier.align(Alignment.Center))
            }
            IconButton(onClick = onDismiss, modifier = Modifier.align(Alignment.TopEnd).padding(32.dp).background(Color.White.copy(0.2f), CircleShape)) {
                Icon(Icons.Rounded.Close, null, tint = Color.White)
            }
        }
    }
}