package com.example.droidspecs.Screens

import android.graphics.drawable.Drawable
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.drawable.toBitmap
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.droidspecs.AppItemDetails
import com.example.droidspecs.DashboardViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

private val PrimaryColor = Color(0xFF673AB7)
private val SystemAppColor = Color(0xFF009688)
private val BackgroundColor = Color(0xFFF8F9FA)

@Composable
fun AppsScreen(
    viewModel: DashboardViewModel = hiltViewModel()
) {
    val allApps by viewModel.appList.collectAsStateWithLifecycle()


    var selectedFilter by remember { mutableStateOf("User Installed") }
    var selectedApp by remember { mutableStateOf<AppItemDetails?>(null) }


    val filteredApps = remember(allApps, selectedFilter) {
        when (selectedFilter) {
            "User Installed" -> allApps.filter { !it.isSystemApp }
            "System Apps" -> allApps.filter { it.isSystemApp }
            else -> allApps
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundColor)
            .padding(horizontal = 20.dp)
    ) {

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Applications",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.Black.copy(alpha = 0.8f)
                )
                Text(
                    text = "${filteredApps.size} apps found",
                    style = MaterialTheme.typography.labelMedium,
                    color = Color.Gray
                )
            }

            FilterDropdown(
                currentSelection = selectedFilter,
                onSelectionChanged = { selectedFilter = it }
            )
        }

        if (allApps.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = PrimaryColor)
            }
        } else if (filteredApps.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Rounded.SearchOff, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(48.dp))
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("No apps in this category", color = Color.Gray)
                }
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(bottom = 100.dp), // Space for bottom bar
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(filteredApps, key = { it.packageName }) { app ->
                    AppRowCard(app = app, onClick = { selectedApp = app })
                }
            }
        }
    }


    if (selectedApp != null) {
        PermissionDialog(app = selectedApp!!, onDismiss = { selectedApp = null })
    }
}


@Composable
fun AppRowCard(app: AppItemDetails, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AppIconImage(packageName = app.packageName)

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = app.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "v${app.versionName}",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )

                    if(app.isSystemApp) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Surface(
                            color = SystemAppColor.copy(alpha = 0.1f),
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                text = "SYS",
                                style = MaterialTheme.typography.labelSmall,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = SystemAppColor,
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                            )
                        }
                    }
                }
            }

            Surface(
                color = BackgroundColor,
                shape = RoundedCornerShape(50),
                modifier = Modifier.padding(start = 8.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Permissions",
                        style = MaterialTheme.typography.labelSmall,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryColor
                    )
                    Spacer(modifier = Modifier.width(2.dp))
                    Icon(
                        imageVector = Icons.Rounded.ChevronRight,
                        contentDescription = null,
                        tint = PrimaryColor,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}
@Composable
fun AppIconImage(packageName: String) {
    val context = LocalContext.current
    var iconDrawable by remember { mutableStateOf<Drawable?>(null) }

    LaunchedEffect(packageName) {
        withContext(Dispatchers.IO) {
            try {
                iconDrawable = context.packageManager.getApplicationIcon(packageName)
            } catch (e: Exception) { /* Ignore */ }
        }
    }

    if (iconDrawable != null) {
        Image(
            bitmap = iconDrawable!!.toBitmap(width = 100, height = 100).asImageBitmap(),
            contentDescription = null,
            modifier = Modifier.size(48.dp)
        )
    } else {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(Color.LightGray.copy(alpha = 0.3f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Rounded.Android, contentDescription = null, tint = Color.Gray)
        }
    }
}


@Composable
fun FilterDropdown(
    currentSelection: String,
    onSelectionChanged: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val options = listOf("User Installed", "System Apps", "All Apps")

    Box {
        Surface(
            color = Color.White,
            shape = RoundedCornerShape(50),
            border = androidx.compose.foundation.BorderStroke(1.dp, PrimaryColor.copy(alpha = 0.3f)),
            modifier = Modifier.clickable { expanded = true }
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = currentSelection,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryColor
                )
                Spacer(modifier = Modifier.width(4.dp))
                Icon(
                    imageVector = Icons.Rounded.ArrowDropDown,
                    contentDescription = null,
                    tint = PrimaryColor,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.background(Color.White)
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = {
                        Text(
                            text = option,
                            fontWeight = if(option == currentSelection) FontWeight.Bold else FontWeight.Normal,
                            color = if(option == currentSelection) PrimaryColor else Color.Black
                        )
                    },
                    onClick = {
                        onSelectionChanged(option)
                        expanded = false
                    },
                    leadingIcon = {
                        if (option == currentSelection) {
                            Icon(Icons.Rounded.Check, contentDescription = null, tint = PrimaryColor, modifier = Modifier.size(18.dp))
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun PermissionDialog(app: AppItemDetails, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(
                Icons.Rounded.Security,
                contentDescription = null,
                tint = if(app.isSystemApp) SystemAppColor else PrimaryColor,
                modifier = Modifier.size(32.dp)
            )
        },
        title = {
            Text(text = "Permissions", fontWeight = FontWeight.Bold)
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "${app.name} has requested access to:",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.height(16.dp))

                if (app.permissions.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                            .background(Color(0xFFE8F5E9), RoundedCornerShape(8.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No sensitive permissions found.",
                            color = Color(0xFF2E7D32),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .heightIn(max = 300.dp)
                            .fillMaxWidth()
                            .border(1.dp, Color.LightGray.copy(alpha=0.3f), RoundedCornerShape(8.dp))
                    ) {
                        items(app.permissions) { perm ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 12.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.Key,
                                    contentDescription = null,
                                    tint = Color.Gray,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    text = perm.lowercase().replaceFirstChar { it.uppercase() },
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color.DarkGray
                                )
                            }
                            Divider(color = Color.LightGray.copy(alpha=0.2f))
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryColor),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Close")
            }
        },
        containerColor = Color.White,
        shape = RoundedCornerShape(24.dp)
    )
}