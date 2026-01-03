package com.example.droidspecs.screens

import android.hardware.Sensor
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.RotateRight
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.droidspecs.SensorInfo
import com.example.droidspecs.SensorViewModel

@Composable
fun SensorScreen(
    viewModel: SensorViewModel = hiltViewModel()
) {
    val sensors by viewModel.sensorList.collectAsStateWithLifecycle()
    var selectedSensor by remember { mutableStateOf<SensorInfo?>(null) }

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Text(
                    "SENSOR",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    letterSpacing = 1.sp,
                    modifier = Modifier.padding(bottom = 8.dp, start = 4.dp)
                )
            }

            items(sensors) { sensor ->
                SensorCard(sensor) {
                    if (sensor.isAvailable) {
                        selectedSensor = sensor
                        viewModel.startTest(sensor.type)
                    }
                }
            }
            item { Spacer(modifier = Modifier.height(80.dp)) }
        }

        if (selectedSensor != null) {
            SensorTestDialog(
                sensor = selectedSensor!!,
                viewModel = viewModel,
                onDismiss = {
                    viewModel.stopTest()
                    selectedSensor = null
                }
            )
        }
    }
}

@Composable
fun SensorCard(sensor: SensorInfo, onClick: () -> Unit) {
    val icon = when (sensor.type) {
        -1 -> Icons.Rounded.Nfc
        Sensor.TYPE_ACCELEROMETER -> Icons.Rounded.Explore
        Sensor.TYPE_GYROSCOPE -> Icons.AutoMirrored.Rounded.RotateRight
        Sensor.TYPE_MAGNETIC_FIELD -> Icons.Rounded.Navigation
        Sensor.TYPE_LIGHT -> Icons.Rounded.LightMode
        Sensor.TYPE_PROXIMITY -> Icons.Rounded.Sensors
        Sensor.TYPE_STEP_COUNTER -> Icons.Rounded.DirectionsWalk
        Sensor.TYPE_PRESSURE -> Icons.Rounded.Compress
        else -> Icons.Rounded.DeveloperBoard
    }

    val isAvailable = sensor.isAvailable
    val cardColor = if (isAvailable) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.surfaceVariant.copy(alpha=0.5f)

    Card(
        colors = CardDefaults.cardColors(containerColor = cardColor),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(if (isAvailable) 2.dp else 0.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = isAvailable) { onClick() }
    ) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                color = if (isAvailable) MaterialTheme.colorScheme.primary.copy(alpha = 0.1f) else Color.Transparent,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.size(48.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = if (isAvailable)  Color(0xFF32cd32) else Color.Gray
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = sensor.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = if (isAvailable) sensor.vendor else "Sensor missing",
                    style = MaterialTheme.typography.bodySmall,
                    color = if (isAvailable) Color.Gray else MaterialTheme.colorScheme.error,
                    maxLines = 1
                )
            }

            if (isAvailable) {
                Icon(
                    Icons.Rounded.ChevronRight,
                    null,
                    tint = Color.Gray.copy(alpha = 0.5f)
                )
            }
        }
    }
}

@Composable
fun SensorTestDialog(sensor: SensorInfo, viewModel: SensorViewModel, onDismiss: () -> Unit) {
    val values by viewModel.liveData.collectAsStateWithLifecycle()

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .wrapContentHeight(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = sensor.name,
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Real-time Data",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(50))
                    ) {
                        Icon(Icons.Rounded.Close, null)
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(250.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFF1E1E1E))
                        .padding(2.dp),
                    contentAlignment = Alignment.Center
                ) {
                    when {
                        sensor.type == -1 -> NFCDisplay(status = sensor.vendor)
                        values.size >= 3 -> OscilloscopeGraph(values[0], values[1], values[2])
                        else -> SingleValueGauge(values[0], sensor.type)
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                if (sensor.type != -1) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        DigitalReadout("X", values.getOrElse(0) { 0f }, Color(0xFFFF5252))
                        if (values.size >= 2) DigitalReadout("Y", values[1], Color(0xFF4CAF50))
                        if (values.size >= 3) DigitalReadout("Z", values[2], Color(0xFF448AFF))
                    }
                }
            }
        }
    }
}

@Composable
fun OscilloscopeGraph(x: Float, y: Float, z: Float) {
    val historyX = remember { mutableStateListOf<Float>() }
    val historyY = remember { mutableStateListOf<Float>() }
    val historyZ = remember { mutableStateListOf<Float>() }

    LaunchedEffect(x, y, z) {
        historyX.add(x); if (historyX.size > 100) historyX.removeAt(0)
        historyY.add(y); if (historyY.size > 100) historyY.removeAt(0)
        historyZ.add(z); if (historyZ.size > 100) historyZ.removeAt(0)
    }

    Canvas(modifier = Modifier.fillMaxSize()) {
        val width = size.width
        val height = size.height
        val midY = height / 2f

        val maxVal = maxOf(
            historyX.maxOfOrNull { kotlin.math.abs(it) } ?: 1f,
            historyY.maxOfOrNull { kotlin.math.abs(it) } ?: 1f,
            historyZ.maxOfOrNull { kotlin.math.abs(it) } ?: 1f,
            5f
        )
        val scaleY = (height / 2.2f) / maxVal

        val gridColor = Color.White.copy(alpha = 0.1f)
        drawLine(gridColor, Offset(0f, midY), Offset(width, midY), 2f)
        drawLine(gridColor, Offset(0f, height * 0.25f), Offset(width, height * 0.25f), 1f)
        drawLine(gridColor, Offset(0f, height * 0.75f), Offset(width, height * 0.75f), 1f)
        for (i in 1..9) {
            val xPos = width * (i / 10f)
            drawLine(gridColor, Offset(xPos, 0f), Offset(xPos, height), 1f)
        }

        fun drawWaveform(data: List<Float>, color: Color) {
            if (data.isEmpty()) return
            val path = Path()
            val stepX = width / 100f

            path.moveTo(0f, midY - (data[0] * scaleY))

            for (i in 1 until data.size) {
                val pointX = i * stepX
                val pointY = midY - (data[i] * scaleY)
                path.lineTo(pointX, pointY)
            }

            drawPath(
                path = path,
                color = color,
                style = Stroke(width = 5f, cap = StrokeCap.Round)
            )
        }

        drawWaveform(historyX, Color(0xFFFF5252))
        drawWaveform(historyY, Color(0xFF4CAF50))
        drawWaveform(historyZ, Color(0xFF448AFF))
    }
}

@Composable
fun SingleValueGauge(value: Float, type: Int) {
    val (unit, icon) = when(type) {
        Sensor.TYPE_LIGHT -> "Lux" to Icons.Rounded.LightMode
        Sensor.TYPE_PROXIMITY -> "cm" to Icons.Rounded.Sensors
        Sensor.TYPE_PRESSURE -> "hPa" to Icons.Rounded.Compress
        Sensor.TYPE_STEP_COUNTER -> "Steps" to Icons.Rounded.DirectionsWalk
        else -> "" to Icons.Rounded.Info
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        Icon(icon, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(48.dp))
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "%.1f".format(value),
            style = MaterialTheme.typography.displayLarge,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
        Text(unit, style = MaterialTheme.typography.titleMedium, color = Color.Gray)
    }
}

@Composable
fun DigitalReadout(label: String, value: Float, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = color
        )
        Surface(
            color = color.copy(alpha = 0.1f),
            shape = RoundedCornerShape(8.dp),
            border = BorderStroke(1.dp, color.copy(alpha = 0.3f))
        ) {
            Text(
                text = "%.2f".format(value),
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
fun NFCDisplay(status: String) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            Icons.Rounded.Nfc,
            null,
            modifier = Modifier.size(80.dp),
            tint = if(status == "Enabled") Color(0xFF4CAF50) else Color.Gray
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = status.uppercase(),
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
    }
}