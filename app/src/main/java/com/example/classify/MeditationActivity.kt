package com.example.classify

import android.annotation.SuppressLint
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import org.json.JSONObject
import org.w3c.dom.Text
import java.net.URL
import java.util.*
import kotlin.concurrent.timer

class Quote(val text: String, val author: String) {}

class QuoteDatabaseManager(context: Context) : SQLiteOpenHelper(context, "QuoteDB", null, 1) {
    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL("CREATE TABLE IF NOT EXISTS QUOTES(quote, author)")
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        TODO("Not yet implemented")
    }

    fun insert(quote: Quote) {
        writableDatabase.execSQL("INSERT INTO QUOTES VALUES(\"${quote.text}\",\"${quote.author}\")")
    }

    @SuppressLint("Range")
    fun getQuote(): Quote? {
        val cursor = writableDatabase.rawQuery("SELECT * FROM QUOTES LIMIT 1", null)
        if(cursor.getCount() == 0) {
            cursor.close()
            return null
        }
        cursor.moveToFirst()
        Log.d("dirk", cursor.getCount().toString())
        val quoteText = cursor.getString(0)
        val authorText = cursor.getString(1)
        val result = Quote(quoteText, authorText)
        writableDatabase.execSQL("DELETE FROM QUOTES WHERE quote LIKE \"$quoteText\"")
        cursor.close()
        return result
    }

}

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