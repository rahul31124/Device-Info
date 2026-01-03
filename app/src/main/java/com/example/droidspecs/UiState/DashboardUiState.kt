package com.example.droidspecs

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector

data class RamSignal(
    val totalBytes: Long,
    val usedBytes: Long,
    val usedPercent: Int
) {
    fun getFormattedTotal(): String = "%.1f GB".format(totalBytes / (1024f * 1024f * 1024f))
    fun getFormattedUsed(): String = "%.1f GB".format(usedBytes / (1024f * 1024f * 1024f))
    fun getFormattedFree(): String {
        val freeBytes = totalBytes - usedBytes
        return "%.1f GB".format(freeBytes / (1024f * 1024f * 1024f))
    }
}

data class BatteryStatus(
    val levelPercent: Int,
    val isCharging: Boolean,
    val temperature: Float,
    val voltage: Int
)

data class StorageStatus(
    val totalBytes: Long,
    val usedBytes: Long
) {
    fun getFormattedTotal(): String = "%.0f GB".format(totalBytes / (1024f * 1024f * 1024f))
    fun getFormattedFree(): String = "%.0f GB".format((totalBytes - usedBytes) / (1024f * 1024f * 1024f))
    fun getUsedPercent(): Float = usedBytes.toFloat() / totalBytes.toFloat()
}

data class NetworkStatus(
    val type: String,
    val speedBytesPerSec: Long,
    val frequencyMhz: Int
)
data class DeviceOverview(
    val deviceName: String,
    val manufacturer: String,
    val androidVersion: String,
    val releaseName: String
)
data class HardwareDetail(
    val modelName: String,
    val manufacturer: String,
    val brand: String,
    val board: String,
    val hardware: String,
    val product: String,
    val device: String,
    val bootloader: String,
    val displayRes: String,
    val screenDensity: String
)


data class BatteryFullDetails(
    val levelPercent: Int,
    val status: String,
    val health: String,
    val technology: String,
    val powerSource: String,


    val voltage: String,
    val temperature: String,
    val currentNow: String,
    val powerWattage: String,
    val designCapacity: String,
    val chargeCounter: String
)

data class DisplayDetails(
    val resolution: String,
    val densityDpi: String,
    val physicalSize: String,
    val refreshRate: String,
    val hdrCapabilities: String,
    val brightnessLevel: String,
    val adaptiveBrightness: String,
    val orientation: String,
    val screenTimeout: String,
    val wideColorGamut: String,
    val scaleFactor: String,
    val exactPpi: String
)
data class TabItem(
    val title: String,
    val icon: ImageVector,
    val screen: @Composable () -> Unit
)
data class NetworkDetails(


    val operatorName: String = "Unknown",
    val countryCode: String = "--",
    val networkType: String = "Unknown",
    val networkGeneration: String = "--",
    val radioTechnology: String = "--",
    val isRoaming: Boolean = false,
    val isDataEnabled: Boolean = false,


    val signalStrength: String = "-- dBm",
    val signalLevel: Int = 0,
    val signalQuality: String = "Unknown",


    val ssid: String = "<Unknown>",
    val wifiFrequency: Int = 0,
    val wifiBand: String = "--",
    val wifiLinkSpeed: Int = 0,

    val ipV4: String = "0.0.0.0",
    val ipV6: String = "::",
    val subnetMask: String = "--",
    val macAddress: String = "--",
    val dnsServers: List<String> = emptyList(),


    val downloadSpeed: String = "0 KB/s",
    val uploadSpeed: String = "0 KB/s",
    val rawDownSpeed: Long = 0L
)


data class CpuDetails(
    val processorName: String = "Unknown",
    val architecture: String = "Unknown",
    val coreCount: Int = 0,
    val governor: String = "Unknown",
    val scalingDriver: String = "Unknown",
    val freqRange: String = "Unknown",


    val coreData: List<Pair<Long, Long>> = emptyList(),


    val perCoreFrequencies: List<String> = emptyList(),


    val supportedAbis: String = "Unknown",
    val bogomips: String = "Unknown",
    val uptime: String = "00:00:00",


    val gpuVendor: String = "Unknown",
    val gpuRenderer: String = "Unknown"
)

data class MemoryDetails(
    val ramTotal: String = "0 GB",
    val ramUsed: String = "0 GB",
    val ramPercent: Int = 0,


    val zramTotal: String = "0 GB",
    val zramUsed: String = "0 GB",
    val zramPercent: Int = 0,


    val romTotal: String = "0 GB",
    val romUsed: String = "0 GB",
    val romPercent: Int = 0,

    val systemTotal: String = "0 GB",
    val systemUsed: String = "0 GB",
    val systemPercent: Int = 0,
    val ramHistory: List<Float> = emptyList()

)

data class AppItemDetails(
    val name: String,
    val packageName: String,
    val versionName: String,
    val permissions: List<String>,
    val isSystemApp: Boolean
)

data class SensorSpec(
    val name: String,
    val vendor: String,
    val version: Int,
    val power: Float,
    val maxRange: Float,
    val resolution: Float,
    val typeString: String
)

data class SensorInfo(
    val name: String,
    val type: Int,
    val isAvailable: Boolean,
    val vendor: String = ""
)