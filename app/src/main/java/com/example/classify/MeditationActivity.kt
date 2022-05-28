package com.example.classify

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button

class MeditationActivity : AppCompatActivity(), MeditationListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_meditation)

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
    }

    override fun startMeditation(duration: Int) {
//        if(supportFragmentManager.findFragmentByTag("meditation")?.isAdded == true) {
//            return
//        }
        Log.d("dirk","Starting meditation")
        val meditationFragment: MeditationFragment = MeditationFragment.newInstance(duration, this)
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.meditationPageFragment, meditationFragment, "meditation")
            commit()
        }
    }
}