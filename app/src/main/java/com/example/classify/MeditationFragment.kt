package com.example.classify

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import kotlin.concurrent.timer

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "duration"
private const val ARG_PARAM2 = "listener"

/**
 * A simple [Fragment] subclass.
 * Use the [MeditationFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class MeditationFragment : Fragment() {
    // TODO: Rename and change types of parameters
    var minutesDuration: Int = 0
    var listener: MeditationListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_meditation, container, false)
        val endButton: Button = view.findViewById(R.id.stop_meditation_button)
        endButton.setOnClickListener {
            listener?.endMeditation(0)
        }
        val timerText : TextView = view.findViewById(R.id.meditation_timer)
        val instructionText: TextView = view.findViewById(R.id.meditation_instruction)
        val goal = minutesDuration.times(60)
        var elapsed = 0
        var state = 0
        timerText.text = convertToMinutesSeconds(elapsed)

        timer(period = 1000) {
            elapsed++
            if(elapsed % 4 == 0) {
                state++
                state %= 4
                getActivity()?.runOnUiThread {
                    when(state) {
                        0 -> instructionText.text = getString(R.string.inhale)
                        1 -> instructionText.text = getString(R.string.hold)
                        2 -> instructionText.text = getString(R.string.exhale)
                        3 -> instructionText.text = getString(R.string.hold)
                    }
                }

            }
            getActivity()?.runOnUiThread {
                timerText.text = convertToMinutesSeconds(goal-elapsed)
            }
        }


        return view
    }

    fun convertToMinutesSeconds(secondsDuration: Int) : String {
        val minutes = secondsDuration/60
        val seconds = secondsDuration%60
        val minutesString: String = minutes.toString()
        var secondsString: String = seconds.toString()
        if(seconds < 10) {
            secondsString = "0" + secondsString
        }
        return "$minutesString:$secondsString"
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @return A new instance of fragment MeditationFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(duration: Int, listener: MeditationListener): MeditationFragment {
            val meditationFragment = MeditationFragment()
            meditationFragment.minutesDuration = duration
            meditationFragment.listener = listener
            return meditationFragment
        }
    }
}