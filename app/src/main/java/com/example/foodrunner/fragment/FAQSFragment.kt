package com.example.foodrunner.fragment

import android.content.Context
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Shader
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import com.example.foodrunner.R

class FAQSFragment : Fragment() {
lateinit var scrollView: ScrollView
    lateinit var animation: Animation
    lateinit var q1: TextView
    lateinit var q2: TextView
    lateinit var q3: TextView
    lateinit var q4: TextView
    lateinit var q5: TextView
    lateinit var q6: TextView
    lateinit var linearLayout: LinearLayout
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view=inflater.inflate(R.layout.fragment_faq, container, false)
        q1=view.findViewById(R.id.q1)
        q2=view.findViewById(R.id.q2)
        q3=view.findViewById(R.id.q3)
        q4=view.findViewById(R.id.q4)
        q5=view.findViewById(R.id.q5)
        q6=view.findViewById(R.id.q6)
        linearLayout=view.findViewById(R.id.linearLayout)
        val shader = LinearGradient(0f, 0f, 0f, q1.textSize, Color.parseColor("#0053b3"), Color.parseColor("#0078ff") , Shader.TileMode.CLAMP)
        q1.paint.shader = shader
        q2.paint.shader = shader
        q3.paint.shader = shader
        q4.paint.shader = shader
        q5.paint.shader = shader
        q6.paint.shader = shader
        scrollView=view.findViewById(R.id.scrollView)
        animation= AnimationUtils.loadAnimation(activity as Context,
            R.anim.animationforacardview
        )
        linearLayout.startAnimation(animation)

        return view

    }


}
