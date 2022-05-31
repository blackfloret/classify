package com.example.classify

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import java.security.AccessController.getContext

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val petCareButton: Button = findViewById(R.id.petCareButton)
        val meditationButton: Button = findViewById(R.id.meditateButton)
        val scheduleButton: Button = findViewById(R.id.scheduleButton)

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