package com.example.classify

import android.content.Context
import android.graphics.drawable.AnimationDrawable
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.util.*
import kotlin.random.Random


lateinit var PETCARE_ACTIVITY: PetCareActivity

class PetCareActivity : AppCompatActivity() {

    private lateinit var moneyStepsFragment: MoneyStepsFragment
    private lateinit var petAnimation: AnimationDrawable
    private lateinit var eatAnimation: AnimationDrawable
    lateinit var petPic: ImageView
    lateinit var heartPic: ImageView
    lateinit var waterDrop: ImageView
    private var prevTotalSteps = 0f

    private fun feeding (){
        petPic.clearAnimation()
        petPic.apply{
            setBackgroundResource(R.drawable.eat_animation)
            eatAnimation = background as AnimationDrawable
            eatAnimation.start()
        }
    }

    private fun watering() {
        // Show drop
        //      Note: For some reason, the code below causes the app to crash
//        Handler(mainLooper).postDelayed(
//            { waterDrop.setImageResource(R.drawable.waterdrop_sprite) },
//            1
//        )
//
//        //Then make it transparent after 1 second
//        Handler(mainLooper).postDelayed(
//            { waterDrop.setImageResource(android.R.color.transparent) },
//            1000
//        )
    }

    private fun dancing (){
        petPic.apply{
            setBackgroundResource(R.drawable.dance_animation)
            petAnimation = background as AnimationDrawable
            petAnimation.start()
        }
    }

    private fun updateHeart(){
        if(happiness >= 95){
            heartPic.setImageResource(R.drawable.heart2)
        } else if(happiness in 81..94){
            heartPic.setImageResource(R.drawable.heart_90)
        } else if(happiness in 67..79){
            heartPic.setImageResource(R.drawable.heart_80)
        } else if(happiness in 54..66){
            heartPic.setImageResource(R.drawable.heart_65)
        } else if(happiness in 41..53){
            heartPic.setImageResource(R.drawable.heart_45)
        } else if(happiness in 28..40){
            heartPic.setImageResource(R.drawable.heart_25)
        } else if(happiness in 14..27){
            heartPic.setImageResource(R.drawable.heart_10)
        } else {
            heartPic.setImageResource(R.drawable.heart_0)
        }
    }

    private fun updateBalance(newBalance: Int) {
        balance = newBalance
        moneyStepsFragment.updateValues()
        with(sf.edit()) {
            putInt("balance", balance)
            apply()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pet_care)

        PETCARE_ACTIVITY = this

        moneyStepsFragment = MoneyStepsFragment()
        supportFragmentManager.beginTransaction().apply {
            add(R.id.moneyStepsFragmentContainerView, moneyStepsFragment, "moneySteps")
            commit()
        }

        sf = getPreferences(Context.MODE_PRIVATE)
        updateBalance(sf.getInt("balance", 0))
        prevTotalSteps = sf.getFloat("prevSteps", 0f)

        moneyStepsFragment.updateValues()

        heartPic = findViewById(R.id.heart)
        petPic = findViewById(R.id.petPic)
        dancing()
        updateHeart()

        val wordsHappy = listOf("Happy Pride!", "Di, Derrick, and Bec deserve an A!", "Cats Rule!", "Do you like my sick moves?", "I love this app!", "wee woo wee woo",
            "Live Laugh Love yourself", "Hams my favorite food!", "I am happy!", "Good vibes!", "Having a great time!")
        val wordsNeutral = listOf("Today is okay.", "I have no complaints.", "I am feeling mid.", "I am whelmed.", "I guess I will dance...", "I am bored.", "Can you feed me?", "When's dinner?",
            "I am living but not laughing.", "This is fine I guess", "My shoulders hurt.")
        val wordsUnhappy = listOf("Can we do more today?", "Can I have more attention please?", "Let's get some stuff done today!", "I am feeling sick..", "Complete tasks and improve my health!",
            "Earn points by fulfilling tasks!", "My energy comes from your productivity!", "I am happier when tasks are done!", "I'd be happier with some ham.", "Feed me please.", "Hello?")


        val duration = Toast.LENGTH_SHORT
        var text: String

        var cal = Calendar.getInstance()
        var hour = cal.get(Calendar.HOUR_OF_DAY)
        var nextAvail = hour
        var ableToWater = nextAvail - hour <= 0


        val feedButton = findViewById<Button>(R.id.Feed_button)
        feedButton.setOnClickListener{
            if(balance >= 5){
                balance -= 5
                updateBalance(balance)

                if(happiness < 98){
                    happiness += 5
                }
                feeding()

                text = "Yummy food!"
                Toast.makeText(applicationContext, text, duration).show()
                Log.d("PETTEST", "TOAST FOOD")

                updateHeart()

            } else {
                text = "Not enough food :("
                Toast.makeText(applicationContext, text, duration).show()
                Log.d("PETTEST", "TOAST FOOD")
            }
        }


        val waterButton = findViewById<Button>(R.id.water_button)
        waterButton.setOnClickListener{
            hour = cal.get(Calendar.HOUR_OF_DAY)
            if(ableToWater){
                nextAvail += 4
                happiness += 4

                text = "Slurp!"
                Toast.makeText(applicationContext, text, duration).show()
                Log.d("PETTEST","Drinking!")

                updateHeart()
                watering()
            } else {
                if(nextAvail > 1){
                    text = "Not thirsty.. wait $nextAvail more hours"
                } else {
                    text = "Not thirsty.. wait less than an hour!"
                }
                Toast.makeText(applicationContext,text, duration).show()
                Log.d("PETTEST","WAIT ${nextAvail-hour}")
            }

            dancing()
        }


        val talkButton = findViewById<Button>(R.id.talk_button)
        talkButton.setOnClickListener {
            if(happiness >= 70){
                text = wordsHappy[(Random.nextInt(0,10))]
                Toast.makeText(applicationContext,text, duration).show()
                if(happiness < 100){
                    happiness += 2
                }
                Log.d("PETTEST", "TOAST TALK HAPPY")
            } else if(happiness in 30..69){
                text = wordsNeutral[(Random.nextInt(0, 10))]
                Toast.makeText(applicationContext,text, duration).show()
                Log.d("PETTEST", "TOAST TALK NEUTRAL")
                happiness += 2
            } else {
                text = wordsUnhappy[(Random.nextInt(0, 10))]
                Toast.makeText(applicationContext,text, duration).show()
                Log.d("PETTEST", "TOAST TALK UNHAPPY")
                happiness += 2
            }

            dancing()
            updateHeart()
        }
    }
}