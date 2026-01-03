package com.example.droidspecs

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.droidspecs.Utils.SystemReader
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val systemReader: SystemReader
) : ViewModel() {

    private fun <T> kotlinx.coroutines.flow.Flow<T>.stateInViewModel(initialValue: T): StateFlow<T> {
        return this.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = initialValue
        )
    }

    val ram = systemReader.observeRam().stateInViewModel(RamSignal(1, 1, 0))
    val battery = systemReader.observeBattery().stateInViewModel(BatteryStatus(0, false, 0f, 0))
    val storage = systemReader.observeStorage().stateInViewModel(StorageStatus(1, 0))
    val network = systemReader.observeNetwork().stateInViewModel(NetworkStatus("Scanning...", 0, 0))

    val hardwareDetail = systemReader.getHardwareDetails()
    val deviceOverview = systemReader.getDeviceOverview()
    val sensorCount = systemReader.getSensorCount()
    val coreCount = Runtime.getRuntime().availableProcessors()
    private val _refreshTrigger = MutableSharedFlow<Unit>(replay = 1).apply { tryEmit(Unit) }

    @OptIn(ExperimentalCoroutinesApi::class)
    val networkDetails = _refreshTrigger.flatMapLatest {
        flow {
            while(true) {
                emit(systemReader.getNetworkDetails())
                delay(1000)
            }
        }
    }
        .flowOn(Dispatchers.IO)
        .stateInViewModel(NetworkDetails())

    fun refreshNetwork() {
        _refreshTrigger.tryEmit(Unit)
    }
    val batteryDetails = flow {
        while (true) {
            emit(systemReader.getBatteryDetails())
            delay(2000)
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = BatteryFullDetails(0, "...", "...", "...", "0", "0", "...", "0", "...", "...", "...") )
    private val historyBuffer = java.util.LinkedList<Float>()
    private val MAX_HISTORY_POINTS = 50

    val memoryDetails = flow {
        while(true) {
            val details = systemReader.getMemoryDetails()

            val ramDecimal = details.ramPercent / 100f

            historyBuffer.add(ramDecimal)
            if (historyBuffer.size > MAX_HISTORY_POINTS) {
                historyBuffer.removeFirst()
            }


            emit(details.copy(ramHistory = ArrayList(historyBuffer)))

            delay(1000)
        }
    }.flowOn(Dispatchers.IO)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), MemoryDetails())

    init {
        loadApps()
    }

    private fun loadApps() {
        viewModelScope.launch(Dispatchers.IO) {
            val apps = systemReader.getInstalledApps()
            _appList.value = apps
        }
    }

    val displayDetails = systemReader.getDisplayDetails()
    val cpuDetails = flow {
        while(true) {
            emit(systemReader.getCpuDetails())
            delay(1000)
        }
    }.flowOn(Dispatchers.IO)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), CpuDetails())


    private val _appList = MutableStateFlow<List<AppItemDetails>>(emptyList())
    val appList = _appList.asStateFlow()

    private val _sensorList = MutableStateFlow<List<SensorSpec>>(emptyList())
    val sensorList = _sensorList.asStateFlow()

    init {
        loadApps()
        loadSensors()
    }

    private fun loadSensors() {
        viewModelScope.launch(Dispatchers.IO) {
            val sensors = systemReader.getSensorList()
            _sensorList.value = sensors
        }
    }


}