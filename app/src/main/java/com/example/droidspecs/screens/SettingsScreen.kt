package com.example.droidspecs.screens

import android.content.pm.PackageManager
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowForwardIos
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


private val ScreenBg = Color(0xFFF8F9FA)
private val TextGray = Color(0xFF757575)


@Composable
fun SettingsScreen(onBackClick: () -> Unit) {
    val context = LocalContext.current
    var showPrivacyDialog by remember { mutableStateOf(false) }

    val versionName = try {
        context.packageManager
            .getPackageInfo(context.packageName, 0)
            .versionName ?: "1.0"
    } catch (_: PackageManager.NameNotFoundException) {
        "1.0"
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ScreenBg)
    ) {


        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface)
                .padding(
                    top = 48.dp,
                    bottom = 16.dp,
                    start = 16.dp,
                    end = 16.dp
                ),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBackClick) {
                Icon(
                    Icons.AutoMirrored.Rounded.ArrowForwardIos,
                    contentDescription = "Back",
                    modifier = Modifier.rotate(180f)
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Settings",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Column(modifier = Modifier.padding(20.dp)) {


            SettingsSectionTitle("About")

            SettingsCard {
                SettingsActionRow(
                    icon = Icons.Rounded.PrivacyTip,
                    iconColor = Color(0xFF4CAF50),
                    title = "Privacy Policy",
                    subtitle = "This app does not collect personal data",
                    onClick = { showPrivacyDialog = true }
                )

                HorizontalDivider(color = Color.LightGray.copy(alpha = 0.2f))


                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconBox(Icons.Rounded.Info, Color(0xFF2196F3))
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text("App Version", fontWeight = FontWeight.SemiBold)
                        Text(
                            "v$versionName",
                            fontSize = 13.sp,
                            color = TextGray
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))



            Text(
                text = "This app is developed by a student for learning and helping users understand their device better.",
                fontSize = 12.sp,
                color = TextGray,
                lineHeight = 16.sp,
                modifier = Modifier.padding(horizontal = 8.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "App icon by Kalashnyk - Flaticon",
                fontSize = 11.sp,
                color = TextGray,
                modifier = Modifier.padding(horizontal = 8.dp)
            )

        }
    }



    if (showPrivacyDialog) {
        PrivacyPolicyDialog { showPrivacyDialog = false }
    }
}



@Composable
fun PrivacyPolicyDialog(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("OK")
            }
        },
        title = {
            Text(
                text = "Privacy Policy",
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Text(
                text = """
This app does not collect, store, or share any personal data.

All device, hardware, and network information shown in this app is processed locally on your device using Android system APIs.

• No ads
• No analytics
• No trackers
• No third-party SDKs

This app is developed by a student for educational and informational purposes only.
                """.trimIndent(),
                fontSize = 14.sp,
                lineHeight = 20.sp
            )
        }
    )
}


@Composable
fun SettingsSectionTitle(text: String) {
    Text(
        text = text,
        fontSize = 13.sp,
        color = Color.Gray,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(bottom = 8.dp, start = 4.dp)
    )
}

@Composable
fun SettingsCard(content: @Composable () -> Unit) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(4.dp),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Column { content() }
    }
}

@Composable
fun SettingsActionRow(
    icon: ImageVector,
    iconColor: Color,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconBox(icon, iconColor)
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(title, fontWeight = FontWeight.SemiBold)
            Text(subtitle, fontSize = 13.sp, color = TextGray)
        }
        Icon(
            Icons.AutoMirrored.Rounded.ArrowForwardIos,
            contentDescription = null,
            modifier = Modifier.size(16.dp),
            tint = Color.LightGray
        )
    }
}

@Composable
fun IconBox(icon: ImageVector, color: Color) {
    Box(
        modifier = Modifier
            .size(36.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(color.copy(alpha = 0.15f)),
        contentAlignment = Alignment.Center
    ) {
        Icon(icon, null, tint = color, modifier = Modifier.size(20.dp))
    }
}
