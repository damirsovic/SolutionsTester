package com.example.damirsovic.solutionstester.timer

import android.util.Log
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit

class TimeoutCounter(val time: Long, val timeUnit: TimeUnit, val timerEvent: TimeoutEventListener) {
    private var timerObservable: Disposable? = null

    fun start() {
        timerObservable = Observable.intervalRange(1, time, 0, 1, timeUnit)
                .observeOn(Schedulers.io())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        { next ->
                            Log.v("CounterTest onNext", "next = $next")
                        },
                        { error ->
                            Log.e("CounterTest Error", error.message)
                        },
                        {
                            Log.v("CounterTest onFinish", "Calling timerEnd")
                            timerEvent.onTimerEnd()
                        }
                )
    }

    fun stop() {
        timerObservable?.dispose()
    }

    fun process() {
        stop()
        start()
    }
}