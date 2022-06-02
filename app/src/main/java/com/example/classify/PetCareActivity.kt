package com.example.classify


import android.graphics.drawable.Animatable2
import android.graphics.drawable.AnimationDrawable
import android.os.Bundle
import android.util.Log
import android.view.animation.Animation
import android.view.animation.Animation.AnimationListener
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlin.random.Random


class PetCareActivity : AppCompatActivity(){

    private lateinit var petAnimation: AnimationDrawable
    private lateinit var eatAnimation: AnimationDrawable
    lateinit var petPic: ImageView

    private fun feeding (){
        petPic.clearAnimation()
        petPic.apply{
            setBackgroundResource(R.drawable.eat_animation)
            eatAnimation = background as AnimationDrawable
            eatAnimation.start()
        }

    }

    private fun dancing (){
        petPic.apply{
            setBackgroundResource(R.drawable.dance_animation)
            petAnimation = background as AnimationDrawable
            petAnimation.start()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pet_care)

        petPic = findViewById(R.id.petPic)
        dancing()


        val wordsHappy = listOf("Happy Pride!", "Di, Derrick, and Bec deserve an A!", "Cats Rule!", "Do you like my sick moves?", "I love this app!", "wee woo wee woo",
            "Live Laugh Love yourself", "Hams my favorite food!", "I am happy!", "Good vibes!", "Having a great time!")
        val wordsNeutral = listOf("Today is okay.", "I have no complaints.", "I am feeling mid.", "I am whelmed.", "I guess I will dance...", "I am bored.", "Can you feed me?", "When's dinner?",
            "I am living but not laughing.", "This is fine I guess", "My shoulders hurt.")
        val wordsUnhappy = listOf("Can we do more today?", "Can I have more attention please?", "Let's get some stuff done today!", "I am feeling sick..", "Complete tasks and improve my health!",
            "Earn points by fulfilling tasks!", "My energy comes from your productivity!", "I am happier when tasks are done!", "I'd be happier with some ham.", "Feed me please.", "Hello?")


        val duration = Toast.LENGTH_SHORT
        var text: String

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

            } else {
                text = "Not enough food :("
                Toast.makeText(applicationContext,text, duration).show()
                Log.d("PETTEST", "TOAST FOOD")
            }
        }
        val waterButton = findViewById<Button>(R.id.water_button)
        waterButton.setOnClickListener{
            text = "Slurp!"
            Toast.makeText(applicationContext,text, duration).show()
            Log.d("PETTEST", "TOAST WATER")
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

        }

    }
}