package com.example.droidspecs.screens

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.droidspecs.DashboardViewModel
import com.example.droidspecs.NetworkDetails


private val ScreenBg = Color(0xFFF6F7F3)
private val Amber = Color(0xFFF9A825)
private val Red = Color(0xFFC62828)
private val TextGray = Color(0xFF616161)

@Composable
fun NetworkScreen(
    viewModel: DashboardViewModel = hiltViewModel()
) {
    val net = viewModel.networkDetails.collectAsStateWithLifecycle().value
    val context = LocalContext.current

    var hasPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasPermission = granted
        if (granted) viewModel.refreshNetwork()
    }

    val isWifi = net.wifiFrequency > 0
    val isCellular = !isWifi && net.radioTechnology != "--"

    val connectionTitle = when {
        isWifi -> "Connected via Wi-Fi"
        isCellular -> "Connected via Mobile"
        else -> "No Connection"
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ScreenBg)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {


        ConnectionSummaryCard(
            title = connectionTitle,
            signal = if (hasPermission) net.signalStrength else "-- dBm",
            quality = net.signalQuality,
            down = net.downloadSpeed,
            up = net.uploadSpeed,
            isWifi = isWifi,
            hasPermission = hasPermission,
            onPermissionClick = {
                permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            }
        )

        Spacer(Modifier.height(20.dp))


        when {
            isWifi -> WifiDetailsCard(net)
            isCellular -> MobileDetailsCard(net)
        }

        Spacer(Modifier.height(20.dp))

        TechnicalDetailsCard(net)

        Spacer(Modifier.height(24.dp))
    }
}


@Composable
fun ConnectionSummaryCard(
    title: String,
    signal: String,
    quality: String,
    down: String,
    up: String,
    isWifi: Boolean,
    hasPermission: Boolean,
    onPermissionClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(5.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {
        Column(modifier = Modifier.padding(20.dp)) {

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = if (isWifi) Icons.Rounded.Wifi else Icons.Rounded.SignalCellularAlt,
                    contentDescription = null,
                    tint = Amber,
                    modifier = Modifier.size(26.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text(title, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }

            Spacer(Modifier.height(12.dp))

            Text(signal, fontSize = 30.sp, fontWeight = FontWeight.ExtraBold)
            Text(quality, color = TextGray)

            if (!hasPermission) {
                Spacer(Modifier.height(6.dp))
                Text(
                    "Tap to grant permission",
                    fontSize = 12.sp,
                    color = Red,
                    modifier = Modifier.clickable { onPermissionClick() }
                )
            }

            Spacer(Modifier.height(12.dp))

            Row {
                Text("↓ $down", modifier = Modifier.weight(1f))
                Text("↑ $up", modifier = Modifier.weight(1f))
            }
        }
    }
}



@Composable
fun WifiDetailsCard(net: NetworkDetails) {
    InfoSectionCard("Wi-Fi Details", Icons.Rounded.Wifi) {
        InfoRow("SSID", net.ssid)
        InfoRow("Band", net.wifiBand)
        InfoRow("Frequency", "${net.wifiFrequency} MHz")
        InfoRow("Link Speed", "${net.wifiLinkSpeed} Mbps")
    }
}


@Composable
fun MobileDetailsCard(net: NetworkDetails) {
    InfoSectionCard("Mobile Network", Icons.Rounded.CellTower) {
        InfoRow("Operator", net.operatorName)
        InfoRow("Technology", net.radioTechnology)
        InfoRow("Generation", net.networkGeneration)
        InfoRow("Data Enabled", if (net.isDataEnabled) "Yes" else "No")
        InfoRow("Roaming", if (net.isRoaming) "Yes" else "No")
    }
}


@Composable
fun TechnicalDetailsCard(net: NetworkDetails) {
    InfoSectionCard("IP & DNS", Icons.Rounded.Dns) {
        InfoRowMultiline("IPv4", net.ipV4)
        InfoRowMultiline("IPv6", net.ipV6)

        DividerSection()

        net.dnsServers.forEachIndexed { i, dns ->
            InfoRowMultiline(
                label = "DNS ${i + 1}",
                value = dns,
                isLast = i == net.dnsServers.lastIndex
            )
        }
    }
}


@Composable
fun InfoSectionCard(
    title: String,
    icon: ImageVector,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(5.dp),
        elevation = CardDefaults.cardElevation(2.dp),
                colors = CardDefaults.cardColors(
                containerColor = Color.White
                )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(icon, null, tint = Amber)
                Spacer(Modifier.width(8.dp))
                Text(title, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
            Spacer(Modifier.height(12.dp))
            content()
        }
    }
}

@Composable
fun InfoRow(label: String, value: String) {
    Column(modifier = Modifier.padding(vertical = 6.dp)) {
        Text(label, fontSize = 12.sp, color = TextGray)
        Text(value, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
fun InfoRowMultiline(
    label: String,
    value: String,
    isLast: Boolean = false
) {
    Column(modifier = Modifier.padding(vertical = 6.dp)) {
        Text(label, fontSize = 12.sp, color = TextGray)
        Text(
            value,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            softWrap = true,
            maxLines = Int.MAX_VALUE,
            overflow = TextOverflow.Visible
        )
    }
    if (!isLast) DividerSection()
}

@Composable
fun DividerSection() {
    Divider(color = Color.LightGray.copy(alpha = 0.3f))
}
