package com.example.classify

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import java.time.LocalDate
import java.util.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

val today = Calendar.getInstance()
var localDate: LocalDate = LocalDate.of(today.get(Calendar.YEAR), today.get(Calendar.MONTH)+1, today.get(Calendar.DAY_OF_MONTH))
var hour: Int = -1
var minute: Int = -1
var name: String = ""
var comment: String = ""

// Define a listener interface for this fragment
interface EnterTodoListener {
    // Listeners all implement this interface
    fun todoEntered(localDate: LocalDate, hour: Int, minute: Int, name: String, comment: String)
}

/**
 * A simple [Fragment] subclass.
 * Use the [EnterTodoDialogFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class EnterTodoDialogFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    var listener: EnterTodoListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_enter_todo_dialog, container, false)

        // DatePicker - Due date of task
        val datePicker = view.findViewById<DatePicker>(R.id.calendarView)
        val today = Calendar.getInstance()
        datePicker.init(
            today.get(Calendar.YEAR), today.get(Calendar.MONTH), today.get(Calendar.DAY_OF_MONTH)
        ) { view, year, month, day ->
            localDate = LocalDate.of(year, month + 1, day)
            Log.d("todo dialog", "date: $localDate")
        }

        // Time Picker - Begin time of task
        val timePicker = view.findViewById<TimePicker>(R.id.timePicker)
        timePicker.setIs24HourView(true)
        timePicker.setOnTimeChangedListener {
                timePicker, i, i2 ->
                hour = i
                minute = i2
                Log.d("todo dialog", "Hours:$i, Minutes: $i2")
        }

        // Entered Name - User entered name
        val enteredName = view.findViewById<EditText>(R.id.name_text)
        enteredName.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(s: Editable) {}

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                name = s.toString()
                Log.d("todo dialog", "name: $name")
            }
        })

        // Entered Comment - User entered comment
        val enteredComment = view.findViewById<EditText>(R.id.comment_text)
        enteredComment.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(s: Editable) {}

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                comment = s.toString()
                Log.d("todo dialog", "comment: $comment")
            }
        })

        // Button - Finalize task
        val okButton = view.findViewById<Button>(R.id.button)
        okButton.setOnClickListener {
            listener?.todoEntered(localDate, hour, minute, name, comment)
        }

        return view
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment EnterTodoDialogFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            EnterTodoDialogFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
//        @JvmStatic
//        fun newInstance(param1: EnterTodoListener) : EnterTodoDialogFragment {
//            val enterTodoDialogFrag = EnterTodoDialogFragment()
//            enterTodoDialogFrag.listener = param1
//            return enterTodoDialogFrag
//        }
    }
}