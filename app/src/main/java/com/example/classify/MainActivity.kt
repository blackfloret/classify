package com.example.classify

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView

lateinit var MAINACTIVITY: MainActivity
var balance = 0
class MainActivity : AppCompatActivity() {
    lateinit var balanceText: TextView
    lateinit var stepsText: TextView
    lateinit var sf: SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        MAINACTIVITY = this

        sf = getPreferences(Context.MODE_PRIVATE)
        balanceText = findViewById(R.id.balance_text)
        stepsText = findViewById(R.id.steps_text)
        val petCareButton: ImageView = findViewById(R.id.petCareButton)
        val meditationButton: ImageView = findViewById(R.id.meditateButton)
        val scheduleButton: ImageView = findViewById(R.id.scheduleButton)

        balance = sf.getInt("balance", 0)

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

    fun updateBalance(newBalance: Int) {
        balance = newBalance
        val newBalText = "$\$balance"
        balanceText.text = newBalText
        with(sf.edit()) {
            putInt("balance", balance)
            apply()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        with(sf.edit()) {
            putInt("balance", balance)
            apply()
        }
    }


}