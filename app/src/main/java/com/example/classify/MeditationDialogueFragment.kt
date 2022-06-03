package com.example.classify

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import org.json.JSONObject
import java.lang.Thread.sleep
import java.net.URL
import kotlin.concurrent.thread
import kotlin.concurrent.timer

interface MeditationListener {
    fun endMeditation(duration: Int)
    fun startMeditation(duration: Int)
}


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [MeditationDialogueFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class MeditationDialogueFragment : Fragment() {
    // TODO: Rename and change types of parameters
    var listener: MeditationListener? = null
    val cache_size = 30

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_meditation_dialogue, container, false)
        val quoteUrl = URL("https://api.forismatic.com/api/1.0/?method=getQuote&lang=en&format=json")
        val oneMinuteButton: ImageView = view.findViewById(R.id.one_minute)
        oneMinuteButton.setOnClickListener {
            listener?.startMeditation(1)
        }
        val threeMinuteButton: ImageView = view.findViewById(R.id.three_minutes)
        threeMinuteButton.setOnClickListener {
            listener?.startMeditation(3)
        }
        val fiveMinuteButton: ImageView = view.findViewById(R.id.five_minutes)
        fiveMinuteButton.setOnClickListener {
            listener?.startMeditation(5)
        }

        val quoteText: TextView = view.findViewById(R.id.quote_text)
        val authorText: TextView = view.findViewById(R.id.author_text)
        val dbman = getContext()?.let { QuoteDatabaseManager(it) }
        var newQuote = dbman?.getQuote()
        newQuote?.let {
            val qt = newQuote.text
            quoteText.text = qt
            val at = newQuote.author
            authorText.text = at
            Log.d("dirk", at)
            Log.d("dirk", qt)
        } ?: run {
            thread {
                Log.d("dirk","Filling table")
                timer(period=2000) {
                    val quoteInfo = JSONObject(quoteUrl.readText())
                    val insertQuote: Quote = Quote(quoteInfo.getString("quoteText"), quoteInfo.getString("quoteAuthor"))
                    dbman?.insert(insertQuote)
                    if(dbman?.getSize()!! >= cache_size) {
                        cancel()
                    }
                }
                sleep(2000)
                val newQuote = dbman?.getQuote()
                activity?.runOnUiThread {
                    newQuote?.let {
                        quoteText.text = "${newQuote.text}"
                        authorText.text = "${newQuote.author}"
                    }
                }
            }
        }
        return view
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @return A new instance of fragment MeditationDialogueFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: MeditationListener) : MeditationDialogueFragment {
            val meditationDialogueFragment = MeditationDialogueFragment()
            meditationDialogueFragment.listener = param1
            return meditationDialogueFragment
        }
    }
}