package com.example.classify


import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button


class PetCareActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pet_care)

        val feedButton = findViewById<Button>(R.id.Feed_button)
        feedButton.setOnClickListener{
            PetFragment.feedPet()
        }

    }
}