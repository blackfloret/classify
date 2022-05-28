package com.example.classify

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.drawable.AnimationDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import java.util.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [PetFragment.newInstance] factory method to
 * create an instance of this fragment.
 */

/*class petView(context: Context): View(context) {
    val bitmap1: Bitmap
    val bitmap2: Bitmap
    var xpos: Float
    var ypos: Float
    var count: Int
    var paint: Paint


    init {
        bitmap1 = BitmapFactory.decodeResource(resources, R.drawable.sprite_up)
        bitmap2 = BitmapFactory.decodeResource(resources, R.drawable.sprite_down)

        xpos = 0F
        ypos = 0F
        count = 0
        paint = Paint()
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        if (count++ % 2 == 0) {
            canvas?.drawBitmap(bitmap1, xpos, ypos, paint)
        } else {
            canvas?.drawBitmap(bitmap2, xpos, ypos, paint)
        }
    }
} */

private lateinit var petAnimation: AnimationDrawable

class PetFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

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
        val view = inflater.inflate(R.layout.fragment_pet, container, false)
        var counter = 0

        val petPic = view.findViewById<ImageView>(R.id.petPic).apply{
            setBackgroundResource(R.drawable.dance_animation)
            petAnimation = background as AnimationDrawable
        }
        petAnimation.start()

        return view
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment PetFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            PetFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}