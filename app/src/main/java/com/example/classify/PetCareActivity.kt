package com.example.classify



import android.graphics.drawable.AnimationDrawable
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.view.animation.Animation
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import java.util.*
import kotlin.concurrent.timer
import kotlin.random.Random

lateinit var PETCARE_ACTIVITY: PetCareActivity

class PetCareActivity : AppCompatActivity(), BalanceListener {

    private lateinit var petAnimation: AnimationDrawable
    private lateinit var eatAnimation: AnimationDrawable
    lateinit var petPic: ImageView
    lateinit var heartPic: ImageView
    lateinit var balanceText: TextView
    lateinit var stepsText: TextView
    lateinit var waterDrop: ImageView
    var waterTimer: Timer = Timer()
    var dropInitialY = 0f
    var dropFinalY = 0f


    override fun onAddBalance(value: Int) {
        balance += value
        balanceText.text = "$${balance}"
        Log.d("todo removed", "balance = $balance")
    }

    private fun feeding (){
        petPic.clearAnimation()
        petPic.apply{
            setBackgroundResource(R.drawable.eat_animation)
            eatAnimation = background as AnimationDrawable
            eatAnimation.start()
        }

    }

    fun watering() {
//        waterDrop.isVisible = true
//        waterDrop.translationY = dropInitialY
//
//        val dropRate = dropInitialY * 2f / (0.1f * 60f) / 100f
//        waterTimer = timer(period = 2) {
//            runOnUiThread {
//                waterDrop.translationY += dropRate
//            }
//
//        }
//        waterDrop.isVisible = false

        // Show drop
        Handler(mainLooper).postDelayed(
            { waterDrop.setImageResource(R.drawable.waterdrop_sprite) },
            1
        )

        //Then make it transparent after 1 second
        Handler(mainLooper).postDelayed(
            { waterDrop.setImageResource(android.R.color.transparent) },
            1000
        )
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
        } else if(happiness in 85..94){
            heartPic.setImageResource(R.drawable.heart_90)
        } else if(happiness in 75..84){
            heartPic.setImageResource(R.drawable.heart_80)
        } else if(happiness in 65..74){
            heartPic.setImageResource(R.drawable.heart_65)
        } else if(happiness in 45..64){
            heartPic.setImageResource(R.drawable.heart_45)
        } else if(happiness in 25..44){
            heartPic.setImageResource(R.drawable.heart_25)
        } else if(happiness in 10..43){
            heartPic.setImageResource(R.drawable.heart_10)
        } else {
            heartPic.setImageResource(R.drawable.heart_0)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pet_care)

        PETCARE_ACTIVITY = this

        waterDrop = findViewById(R.id.drop)
        dropInitialY = waterDrop.translationY
        dropFinalY = dropInitialY * -1f

        balanceText = findViewById(R.id.balance_text)
        stepsText = findViewById(R.id.steps_text)
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
            if(food >= 3){
                food -= 3

                if(happiness < 98){
                    happiness += 3
                }
                feeding()

                text = "Yummy food!"
                Toast.makeText(applicationContext,text, duration).show()
                Log.d("PETTEST", "TOAST FOOD")
                updateHeart()

            } else {
                text = "Not enough food :("
                Toast.makeText(applicationContext,text, duration).show()
                Log.d("PETTEST", "TOAST FOOD")
            }
        }


        val waterButton = findViewById<Button>(R.id.water_button)
        waterButton.setOnClickListener{
            hour = cal.get(Calendar.HOUR_OF_DAY)
            if(ableToWater){
                nextAvail += 4
                text = "Slurp!"
                happiness += 4
                Log.d("PETTEST","Drinking!")
                Toast.makeText(applicationContext,text, duration).show()
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
                    happiness+= 1
                }
                Log.d("PETTEST", "TOAST TALK HAPPY")
            } else if(happiness in 30..69){
                text = wordsNeutral[(Random.nextInt(0, 10))]
                Toast.makeText(applicationContext,text, duration).show()
                Log.d("PETTEST", "TOAST TALK NEUTRAL")
                happiness += 1
            } else {
                text = wordsUnhappy[(Random.nextInt(0, 10))]
                Toast.makeText(applicationContext,text, duration).show()
                Log.d("PETTEST", "TOAST TALK UNHAPPY")
                happiness += 1
            }
            dancing()
            updateHeart()

        }

    }
}