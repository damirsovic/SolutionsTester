package com.example.damirsovic.solutionstester.sensor.proximity

import android.content.Context
import android.content.Context.SENSOR_SERVICE
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.PowerManager
import android.util.Log
import io.reactivex.subjects.PublishSubject
import java.util.concurrent.TimeUnit

/**
 * Transition table of audio output state changes
 *           +-------------------------------------------+
 *           |               Current state               |
 * +---------+------------+------------+-----------------+
 * | sensor  | HANDS_FREE |  HANDS_ON  | Manual HANDS_ON |
 * +---------+------------+------------+-----------------+
 * | isFar   | HANDS_FREE | HANDS_FREE | Manual HANDS_ON |
 * | isClose | HANDS_ON   | HANDS_ON   | Manual HANDS_ON |
 * +---------+------------+------------+-----------------+
 */

class ProximitySensorService(val context: Context) : SensorEventListener {

    companion object {
        const val TAG = "Proximity"
        const val WAKE_LOCK_TAG = TAG + ":WakeLock"
    }

    private val powerManager: PowerManager by lazy {
        context.getSystemService(Context.POWER_SERVICE) as PowerManager
    }

    private val sensorManager: SensorManager by lazy {
        context.getSystemService(SENSOR_SERVICE) as SensorManager
    }

    private val wakeLock: PowerManager.WakeLock by lazy {
        powerManager.newWakeLock(PowerManager.PROXIMITY_SCREEN_OFF_WAKE_LOCK, WAKE_LOCK_TAG)
    }
    private val proximitySensor: Sensor? by lazy {
        sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY)
    }

    private var isFar: Boolean = true
    private var threshold: Float = 0.5f
    private var isManual: Boolean = false

    private val source = PublishSubject.create<AudioOutputType>()

    init {
        register()
    }

    @Throws(NoSensorException::class)
    private fun register() {
        if (proximitySensor == null) {
            throw NoSensorException("Proximity sensor not found")
        } else {
            threshold = proximitySensor!!.maximumRange / 2
            source.buffer(
                source.debounce(500, TimeUnit.MILLISECONDS)
                    .doOnNext({ type ->
                        Log.d(TAG, "Proximity sensor requestOyutput = " + type)
                    })
            )
                .takeLast(1)
                .subscribe()
            Log.d(TAG, "Proximity sensor registered")
        }
    }

    fun unregister() {
        source.onComplete()
        Log.d(TAG, "Proximity sensor unregistered")
    }

    private fun enable() {
        Log.d(TAG, "Proximity Sensor enabled")
        val proximitySensor = this.proximitySensor ?: return
        isFar = powerManager.isScreenOn
        isManual = false
        threshold = proximitySensor.maximumRange / 2
        sensorManager.registerListener(this, proximitySensor, SensorManager.SENSOR_DELAY_FASTEST)
        wakeLock.acquire()
    }

    private fun disable() {
        Log.d(TAG, "Proximity Sensor disabled")
        proximitySensor ?: return
        if (wakeLock.isHeld) {
            Log.d(TAG, "wakelock release")
            wakeLock.release(PowerManager.PROXIMITY_SCREEN_OFF_WAKE_LOCK)
            sensorManager.unregisterListener(this)
        }
    }

    // From SensorEventListener
    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    override fun onSensorChanged(event: SensorEvent) {
        isFar = event.values.get(0) > threshold
        Log.i(TAG, "Proximity change far=$isFar")
        processState()
    }

    private fun processState() {
        if (!isManual) {
            if (isFar) {
                Log.d(TAG, "Proximity sensor: Processing state - new output: HANDS_FREE")
                source.onNext(AudioOutputType.HANDS_FREE)
            } else {
                Log.d(TAG, "Proximity sensor: Processing state - new output: HANDS_ON")
                source.onNext(AudioOutputType.HANDS_ON)
            }
        }
    }

    fun changeState(newState: AudioOutputType) {
        isManual = newState != AudioOutputType.HANDS_FREE
    }
}