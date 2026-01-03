package com.example.droidspecs

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.* import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.droidspecs.Screens.AppsScreen
import com.example.droidspecs.Screens.CpuScreen
import com.example.droidspecs.Screens.HardWareScreen
import com.example.droidspecs.Screens.MemoryScreen
import com.example.droidspecs.screens.* import com.example.droidspecs.ui.theme.DroidSpecsTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            var showSplash by remember { mutableStateOf(true) }

            if (showSplash) {
                SplashScreen(onSplashFinished = {
                    showSplash = false
                })
            } else {
                DroidSpecsTheme {
                    CompactMainTabScreen()
                }
            }
        }
    }
}


@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun CompactMainTabScreen() {
    val tabs = remember {
        listOf(
            TabItem("Dashboard", Icons.Rounded.Dashboard) { DashboardScreen() },
            TabItem("System", Icons.Rounded.Smartphone) { HardWareScreen() },
            TabItem("Memory", Icons.Rounded.Memory) { MemoryScreen() },
            TabItem("CPU", Icons.Rounded.Apps) { CpuScreen() },
            TabItem("Network", Icons.Rounded.Wifi) { NetworkScreen() },
            TabItem("Battery", Icons.Rounded.BatteryStd) { BatteryScreen() },
            TabItem("Display", Icons.Rounded.PhoneAndroid) { DisplayScreen() },
            TabItem("Sensors", Icons.Rounded.Speed) { SensorScreen() },
            TabItem("Apps", Icons.Rounded.Apps) { AppsScreen() }
        )
    }

    val pagerState = rememberPagerState(pageCount = { tabs.size })
    val coroutineScope = rememberCoroutineScope()
    val primaryColor = Color(0xFFfcc200)
    val surfaceColor = MaterialTheme.colorScheme.surface
    var showSettings by remember { mutableStateOf(false) }

    if (showSettings) {
        SettingsScreen(onBackClick = { showSettings = false })
    } else {
        Scaffold(
            topBar = {
                Column(modifier = Modifier.background(surfaceColor).padding(top = 27.dp)) {

                    Row(
                        modifier = Modifier.fillMaxWidth().height(50.dp).padding(horizontal = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = {}) {
                            Icon(
                                Icons.Rounded.Smartphone,
                                "Logo",
                                tint = primaryColor,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Text(
                            "Device Info",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(start = 4.dp).weight(1f)
                        )
                        IconButton(onClick = { showSettings = true }) {
                            Icon(
                                Icons.Rounded.Settings,
                                "Settings",
                                tint = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }

                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.2f))

                    ScrollableTabRow(
                        selectedTabIndex = pagerState.currentPage,
                        edgePadding = 12.dp,
                        containerColor = surfaceColor,
                        contentColor = primaryColor,
                        modifier = Modifier.height(44.dp),
                        indicator = { tabPositions ->
                            TabRowDefaults.SecondaryIndicator(
                                Modifier.tabIndicatorOffset(tabPositions[pagerState.currentPage]),
                                height = 2.dp,
                                color = primaryColor
                            )
                        },
                        divider = {}
                    ) {
                        tabs.forEachIndexed { index, item ->
                            val isSelected = pagerState.currentPage == index
                            Tab(
                                selected = isSelected,
                                onClick = {
                                    coroutineScope.launch {
                                        pagerState.animateScrollToPage(
                                            index
                                        )
                                    }
                                },
                                modifier = Modifier.height(44.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(horizontal = 8.dp)
                                ) {
                                    Icon(
                                        item.icon,
                                        null,
                                        tint = if (isSelected) primaryColor else Color.Gray,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        item.title,
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        color = if (isSelected) primaryColor else Color.Gray
                                    )
                                }
                            }
                        }
                    }
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.1f))
                }
            }
        ) { paddingValues ->
            HorizontalPager(
                state = pagerState,
                beyondViewportPageCount = 1,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(MaterialTheme.colorScheme.background)
            ) { index ->
                tabs[index].screen()
            }
        }
    }
    BackHandler(enabled = showSettings) {
        showSettings = false
    }
}