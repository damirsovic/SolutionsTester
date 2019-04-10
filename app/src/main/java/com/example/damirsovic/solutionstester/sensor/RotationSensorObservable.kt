package com.example.damirsovic.solutionstester.sensor

import android.content.Context
import android.content.Context.SENSOR_SERVICE
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import io.reactivex.Observable
import io.reactivex.ObservableEmitter
import io.reactivex.ObservableOnSubscribe
import io.reactivex.functions.Cancellable


class RotationSensorObservable(context: Context) {
    val sensorManager: SensorManager by lazy {
        context.getSystemService(SENSOR_SERVICE) as SensorManager
    }

    val rotationSensor: Sensor by lazy {
        sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)
    }

    fun sensorEventObservable(
        samplingPeriodUs: Int
    ): Observable<SensorEvent> {
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
}