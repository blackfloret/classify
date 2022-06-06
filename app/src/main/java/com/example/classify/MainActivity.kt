package com.example.classify

import android.Manifest.permission.ACTIVITY_RECOGNITION
import android.app.job.JobParameters
import android.app.job.JobService
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import android.hardware.SensorManager
import android.os.Build
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat

lateinit var MAINACTIVITY: MainActivity
lateinit var sf: SharedPreferences
var balance = 0
var steps = 0
var happiness = 0

class MainActivity : AppCompatActivity(), SensorEventListener {
    lateinit var dialFrag: AboutFragment
    private var sensorManager: SensorManager? = null
    private var running = false
    private var totalSteps = 0f
    private var prevTotalSteps = 0f
    lateinit var moneyStepsFragment: MoneyStepsFragment

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        MAINACTIVITY = this

        sf = getPreferences(Context.MODE_PRIVATE)
        val petCareButton: ImageView = findViewById(R.id.petCareButton)
        val meditationButton: ImageView = findViewById(R.id.meditateButton)
        val scheduleButton: ImageView = findViewById(R.id.scheduleButton)

        moneyStepsFragment = MoneyStepsFragment()
        supportFragmentManager.beginTransaction().apply {
            add(R.id.moneyStepsFragmentContainerView, moneyStepsFragment, "moneySteps")
            commit()
        }

        updateBalance(sf.getInt("balance", 0))
        prevTotalSteps = sf.getFloat("prevSteps", 0f)

        moneyStepsFragment.updateValues()


        if(ActivityCompat.checkSelfPermission(this, ACTIVITY_RECOGNITION) != PERMISSION_GRANTED) {
            requestPermissions(arrayOf(ACTIVITY_RECOGNITION), 1)
        }

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager


        petCareButton.setOnClickListener {
            Intent(this, PetCareActivity::class.java).also {
                startActivity(it)
            }
        }

        meditationButton.setOnClickListener {
            Intent(this, MeditationActivity::class.java).also {
                startActivity(it)
            }
        }

        scheduleButton.setOnClickListener {
            Intent(this, ScheduleActivity::class.java).also {
                startActivity(it)
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        with(sf.edit()) {
            putInt("balance", balance)
            apply()
        }
    }

    fun updateBalance(newBalance: Int) {
        balance = newBalance
        moneyStepsFragment.updateValues()
        with(sf.edit()) {
            putInt("balance", balance)
            apply()
        }
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if(running) {
            totalSteps = event!!.values[0]
            Log.d("dirk", "$totalSteps")
            steps = totalSteps.toInt() - prevTotalSteps.toInt()
            // updateBalance()
            moneyStepsFragment.updateValues()
        }
    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
        // unnneeded
    }

    override fun onResume() {
        super.onResume()
        running = true

        val stepSensor = sensorManager?.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)

        if(stepSensor == null) {
            Toast.makeText(this, "Pedometer unable to be accessed", Toast.LENGTH_SHORT).show()
        } else {
            sensorManager?.registerListener(this, stepSensor, SensorManager.SENSOR_DELAY_NORMAL)
        }
    }

    override fun onPause() {
        running = false
        sensorManager?.unregisterListener(this)
        super.onPause()
    }

    fun resetSteps() {
        val cashedSteps = (totalSteps.toInt() - prevTotalSteps.toInt()) / 100
        val remSteps = (totalSteps.toInt() - prevTotalSteps.toInt()) % 100
        updateBalance(balance + cashedSteps)
        prevTotalSteps = totalSteps - remSteps
        moneyStepsFragment.updateValues()

        with(sf.edit()) {
            putFloat("prevSteps", prevTotalSteps)
            apply()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.about_item -> {
                displayAbout()
                true
            }
            R.id.reset_item -> {
                resetSteps()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    fun displayAbout() {
            Log.d("main activity", "about option clicked")
            dialFrag = AboutFragment.newInstance("hi", "bye")
            dialFrag.show(supportFragmentManager, "Showing 'About' page")
           // dialFrag.listener = this
    }

}

class StepJobService: JobService(), SensorEventListener {
    override fun onStartJob(jobParamters: JobParameters?): Boolean {
        val sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        val stepCounterSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
        stepCounterSensor?.let {
            sensorManager.registerListener(this@StepJobService, it, SensorManager.SENSOR_DELAY_FASTEST)
        }
        return true
    }

    override fun onStopJob(p0: JobParameters?): Boolean {
        return true
    }

    override fun onSensorChanged(sensorEvent: SensorEvent?) {
        sensorEvent ?: return
        sensorEvent.values.firstOrNull()?.let {

        }
    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
        Log.d("dirk", "accurancyChanged")
    }
}