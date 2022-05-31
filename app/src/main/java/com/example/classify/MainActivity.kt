package com.example.classify

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val petCareButton: ImageView = findViewById(R.id.petCareButton)
        val meditationButton: ImageView = findViewById(R.id.meditateButton)
        val scheduleButton: ImageView = findViewById(R.id.scheduleButton)

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
}