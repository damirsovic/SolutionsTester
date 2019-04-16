package com.example.damirsovic.solutionstester.sensor

import android.content.Context
import android.content.Context.SENSOR_SERVICE
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.util.Log
import io.reactivex.Observable
import io.reactivex.ObservableEmitter
import io.reactivex.ObservableOnSubscribe
import io.reactivex.functions.Cancellable


class RotationSensorObservable(context: Context, val sensorType: Int) {
    val sensorManager: SensorManager by lazy {
        context.getSystemService(SENSOR_SERVICE) as SensorManager
    }

    val rotationSensor: Sensor by lazy {
        sensorManager.getDefaultSensor(sensorType)
    }

    init {
        detectSensors()
    }

    fun sensorEventObservable(samplingPeriodUs: Int): Observable<SensorEvent> {

        return Observable.create(object : ObservableOnSubscribe<SensorEvent> {
            @Throws(Exception::class)
            override fun subscribe(emitter: ObservableEmitter<SensorEvent>) {
                val sensorListener = object : SensorEventListener {
                    override fun onSensorChanged(sensorEvent: SensorEvent) {
                        emitter.onNext(sensorEvent)
                    }

                    override fun onAccuracyChanged(originSensor: Sensor, i: Int) {
                        // ignored for this example
                    }
                }
                // (1) - unregistering listener when unsubscribed
                emitter.setCancellable(object : Cancellable {
                    @Throws(Exception::class)
                    override fun cancel() {
                        sensorManager.unregisterListener(sensorListener, rotationSensor)
                    }
                })
                sensorManager.registerListener(sensorListener, rotationSensor, samplingPeriodUs)
            }
        })
    }

    fun detectSensors() {
        Observable.fromIterable(sensorManager.getSensorList(Sensor.TYPE_ALL))
            .subscribe(
                { sensor -> Log.d("Sensor", sensor.name + " of type " + sensor.stringType + " type = " + sensor.type) }
            )
    }
}