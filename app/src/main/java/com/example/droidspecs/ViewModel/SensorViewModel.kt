package com.example.droidspecs

import android.content.Context
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.nfc.NfcAdapter
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject


@HiltViewModel
class SensorViewModel @Inject constructor(
    @ApplicationContext private val context: Context
) : ViewModel(), SensorEventListener {

    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager

    private val _sensorList = MutableStateFlow<List<SensorInfo>>(emptyList())
    val sensorList = _sensorList.asStateFlow()

    private val _liveData = MutableStateFlow<FloatArray>(floatArrayOf(0f, 0f, 0f))
    val liveData = _liveData.asStateFlow()

    private var activeSensor: Sensor? = null

    init {
        checkAllSensors()
    }

    private fun checkAllSensors() {
        val targets = listOf(
            Sensor.TYPE_ACCELEROMETER to "Accelerometer",
            Sensor.TYPE_GYROSCOPE to "Gyroscope",
            Sensor.TYPE_MAGNETIC_FIELD to "Magnetometer",
            Sensor.TYPE_PROXIMITY to "Proximity",
            Sensor.TYPE_LIGHT to "Light Sensor",
            Sensor.TYPE_GRAVITY to "Gravity",
            Sensor.TYPE_STEP_COUNTER to "Step Counter",
            Sensor.TYPE_PRESSURE to "Barometer"
        )

        val result = targets.map { (type, name) ->
            val sensor = sensorManager.getDefaultSensor(type)
            SensorInfo(
                name = name,
                type = type,
                isAvailable = sensor != null,
                vendor = sensor?.vendor ?: "Unknown"
            )
        }.toMutableList()


        val hasNfcHardware = context.packageManager.hasSystemFeature(PackageManager.FEATURE_NFC)
        val nfcAdapter = NfcAdapter.getDefaultAdapter(context)
        val nfcEnabled = nfcAdapter?.isEnabled == true

        val nfcStatus = if (!hasNfcHardware) "Not Supported" else if (nfcEnabled) "Enabled" else "Disabled"

        result.add(
            SensorInfo(
                name = "NFC",
                type = -1,
                isAvailable = hasNfcHardware,
                vendor = nfcStatus
            )
        )

        _sensorList.value = result
    }

    fun startTest(type: Int) {
        stopTest()

        if (type == -1) return

        val sensor = sensorManager.getDefaultSensor(type)
        if (sensor != null) {
            activeSensor = sensor
            sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_UI)
        }
    }

    fun stopTest() {
        if (activeSensor != null) {
            sensorManager.unregisterListener(this)
            activeSensor = null
            _liveData.value = floatArrayOf(0f, 0f, 0f)
        }
    }

    override fun onSensorChanged(event: SensorEvent?) {
        event?.let {
            _liveData.value = it.values.clone()
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
    }

    override fun onCleared() {
        super.onCleared()
        stopTest()
    }
}