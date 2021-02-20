package com.example.foodrunner.fragment


import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import com.example.foodrunner.R

class ProfileFragment : Fragment() {
    lateinit var animation: Animation
    var sharedPreferences: SharedPreferences?=null
    lateinit var name:TextView
    lateinit var mobilenumber:TextView
    lateinit var email:TextView
    lateinit var headericon:ImageView
    lateinit var address:TextView
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        animation= AnimationUtils.loadAnimation(context,
            R.anim.animationforacardview
        )
        val view=inflater.inflate(R.layout.fragment_profile, container, false)
        sharedPreferences=activity?.getSharedPreferences(getString(R.string.preferences_file_name),Context.MODE_PRIVATE)
        val username=sharedPreferences?.getString("username",null)
        val usermobilenumber=sharedPreferences?.getString("usermobilenumber",null)
        val useremail=sharedPreferences?.getString("useremail",null)
       val useraddress= sharedPreferences?.getString("useraddress",null)
        headericon=view.findViewById(R.id.headericon) as ImageView
        name=view.findViewById(R.id.name) as TextView
        mobilenumber=view.findViewById(R.id.mobilenumber) as TextView
        email=view.findViewById(R.id.email) as TextView
        address=view.findViewById(R.id.address) as TextView
        headericon.startAnimation(animation)
        name.startAnimation(animation)
        mobilenumber.startAnimation(animation)
        email.startAnimation(animation)
        address.startAnimation(animation)
        name.text=username
        mobilenumber.text= "+91-$usermobilenumber"
        email.text=useremail
        address.text=useraddress
        return view
    }


}
