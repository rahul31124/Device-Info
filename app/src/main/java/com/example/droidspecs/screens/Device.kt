package com.example.droidspecs.Screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.droidspecs.DashboardViewModel
import com.example.droidspecs.R
import com.example.droidspecs.screens.SectionHeader

private val PrimaryPurple = Color(0xFFff0000)
private val TextGray = Color(0xFF757575)
private val BgColor = Color(0xFFF8F9FA)

@Composable
fun HardWareScreen(
    viewModel: DashboardViewModel = hiltViewModel()
) {
    val details = viewModel.hardwareDetail

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BgColor)
            .verticalScroll(rememberScrollState())
            .padding(20.dp)
    ) {

        BrandHeader(manufacturer = details.manufacturer)

        Spacer(modifier = Modifier.height(32.dp))


        SectionHeader(title = "Identity", icon = Icons.Rounded.Smartphone)

        Card(
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(12.dp),
            elevation = CardDefaults.cardElevation(1.dp)
        ) {
            Column(modifier = Modifier.padding(vertical = 4.dp)) {
                HardwareInfoRow(label = "Model Name", value = details.modelName)
                HardwareInfoRow(label = "Manufacturer", value = details.manufacturer)
                HardwareInfoRow(label = "Brand", value = details.brand)
                HardwareInfoRow(label = "Device Codename", value = details.device, isLast = true)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))


        SectionHeader(title = "Board & Hardware", icon = Icons.Rounded.DeveloperBoard)

        Card(
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(12.dp),
            elevation = CardDefaults.cardElevation(1.dp)
        ) {
            Column(modifier = Modifier.padding(vertical = 4.dp)) {
                HardwareInfoRow(label = "Board Platform", value = details.board)
                HardwareInfoRow(label = "Hardware", value = details.hardware)
                HardwareInfoRow(label = "Bootloader", value = details.bootloader)
                HardwareInfoRow(label = "Product ID", value = details.product, isLast = true)
            }
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
fun BrandHeader(manufacturer: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .border(width = 2.dp, color = Color.Black, shape = RoundedCornerShape(5.dp))
            ,
        contentAlignment = Alignment.Center
    ) {
        val logoRes = getLogoResource(manufacturer)

        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier

                .clip(RoundedCornerShape(20.dp))


        ) {
            if (logoRes != R.drawable.logo_android) {

                Image(
                    painter = painterResource(logoRes),
                    contentDescription = "$manufacturer Logo",
                    modifier = Modifier.size(80.dp),
                    contentScale = ContentScale.Fit
                )
            } else {
                Icon(
                    imageVector = Icons.Rounded.Android,
                    contentDescription = null,
                    tint = PrimaryPurple,
                    modifier = Modifier.size(80.dp)
                )
            }
        }
    }
}


@Composable
fun getLogoResource(manufacturer: String): Int {
    val name = manufacturer.lowercase()
    return when {
        name.contains("google") -> R.drawable.logo_google
        name.contains("samsung") -> R.drawable.logo_samsung
        name.contains("xiaomi") || name.contains("redmi") || name.contains("poco") -> R.drawable.logo_xiaomi
        name.contains("oneplus") -> R.drawable.logo_oneplus
        name.contains("realme") -> R.drawable.logo_realme
        name.contains("motorola") || name.contains("moto") -> R.drawable.logo_motorola
        else -> R.drawable.logo_android
    }
}

@Composable
private fun HardwareInfoRow(label: String, value: String, isLast: Boolean = false) {
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
            color = Color.Black
        )
    }

    if (!isLast) {
        HorizontalDivider(
            modifier = Modifier.padding(horizontal = 20.dp),
            thickness = 1.dp,
            color = Color.LightGray.copy(alpha = 0.15f)
        )
    }
}
