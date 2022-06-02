package com.example.classify

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import java.net.URL
import java.util.*
import kotlin.concurrent.timer


class MeditationActivity : AppCompatActivity(), MeditationListener {
    var sunTimer: Timer = Timer()
    var sunInitialY = 0f
    var sunFinalY = 0f
    lateinit var sun: ImageView
    lateinit var skyBackground: ConstraintLayout
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_meditation)
        sun = findViewById(R.id.sun)
        sunInitialY = sun.translationY
        sunFinalY = sunInitialY * -1f
        skyBackground = findViewById(R.id.sky_background)
        val dialogueFragment: MeditationDialogueFragment = MeditationDialogueFragment.newInstance(this)
        supportFragmentManager.beginTransaction().apply {
            add(R.id.meditationPageFragment, dialogueFragment, "meditationDialogue")
            commit()
        }
    }

    override fun endMeditation(duration: Int) {
//        if(supportFragmentManager.findFragmentByTag("meditationDialogue")?.isAdded == true) {
//            return
//        }
        Log.d("dirk","Ending meditation")
        val dialogueFragment: MeditationDialogueFragment = MeditationDialogueFragment.newInstance(this)

        // to do "increase money by duration meditated"

        supportFragmentManager.beginTransaction().apply {
            replace(R.id.meditationPageFragment, dialogueFragment, "meditationDialogue")
            commit()
        }
        sunTimer.cancel()
    }

    override fun startMeditation(duration: Int) {
//        if(supportFragmentManager.findFragmentByTag("meditation")?.isAdded == true) {
//            return
//        }
        sun.translationY = sunInitialY
        skyBackground.alpha = 0f

        Log.d("dirk","Starting meditation")
        val meditationFragment: MeditationFragment = MeditationFragment.newInstance(duration, this)
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.meditationPageFragment, meditationFragment, "meditation")
            commit()
        }
        val sunriseRate = sunInitialY * 2 / (duration * 60) / 100
        val skyColorRate = 1f / (duration * 60f) / 100
        sunTimer = timer(period = 10) {
            runOnUiThread {
                sun.translationY -= sunriseRate
                skyBackground.alpha += skyColorRate
            }

        }
    }
}