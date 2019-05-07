package com.example.damirsovic.solutionstester

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import com.example.damirsovic.solutionstester.books.BooksActivity
import com.example.damirsovic.solutionstester.sensor.RotationSensorObservable
import com.example.damirsovic.solutionstester.timer.TimeoutCounter
import com.example.damirsovic.solutionstester.timer.TimeoutEventListener
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.activity_main.*
import java.util.concurrent.TimeUnit


class MainActivity : AppCompatActivity() {

    var rotationObservable: Disposable? = null

    private val source = PublishSubject.create<Boolean>()
    private var timeoutCounter: TimeoutCounter? = null
    private var count : Int = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onResume() {
        super.onResume()
        timeoutCounter = TimeoutCounter(1000, TimeUnit.MILLISECONDS,timerEnd())
        rotationObservable = RotationSensorObservable(this, 6)
            .sensorEventObservable(500)
            .buffer(3)
            .subscribe(
                { next ->
                    for (i in 0..(next.size - 1)) {
                        val strBuffer = StringBuffer()
                        strBuffer.append("Values ").append(i).append(": ")
                        for (j in 0..(next[i].values.size - 1)) {
                            strBuffer.append(next[i].values.get(j)).append(", ")
                        }
                        Log.d("Rotation", strBuffer.toString())
                    }
                },
                { error ->
                    Log.e("Rotation", error.message)
                    error.printStackTrace()
                }
            )

        source
            .buffer(
                source.debounce(400, TimeUnit.MILLISECONDS)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .doOnNext({e -> txtLabel.text = "Last click sets " +  e} )
            )
            .takeLast(1)
            .subscribe ()

    }

    override fun onPause() {
        super.onPause()
        rotationObservable?.dispose()
        source.onComplete()
        timeoutCounter?.stop()
    }

    fun clickDispatcher(view: View) {
        Log.d("CounterTest", "Pressed button " + view.id)
        if(view.id == R.id.btnTrue)
            source.onNext(true)
        if(view.id == R.id.btnFalse)
            source.onNext(false)
        if(view.id == R.id.btnStart) {
            txtLabel.text = "Pressed ${++count} times"
            Log.d ("CounterTest",  "Pressed $count times")
            timeoutCounter?.process()
        }
        if (view.id == R.id.btnBooks) {
            val intent = Intent(this, BooksActivity::class.java)
            startActivity(intent)
        }
    }

    fun timerEnd() = object: TimeoutEventListener{
        override fun onTimerEnd() {
            Log.d ("CounterTest",  "Reset")
            txtLabel.text = "Timeout! Counter reset to 0"
            count = 0
            timeoutCounter?.stop()
        }

    }
}
