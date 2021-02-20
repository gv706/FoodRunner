package com.example.foodrunner.activity

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import com.example.foodrunner.R

class WelcomeScreen : AppCompatActivity() {

    lateinit var sharedPreferences: SharedPreferences
lateinit var iconname:TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome_screen)
        iconname=findViewById(R.id.appname)
        sharedPreferences=getSharedPreferences(getString(R.string.preferences_file_name),Context.MODE_PRIVATE)
        Thread(Runnable {

                try {
                    // Sleep for 200 milliseconds.
                    Thread.sleep(4000)
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }


            if(isLoggedin())
            {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
            else{
                val intent = Intent(this, Login::class.java)
                startActivity(intent)
                finish()
            }

        }).start()
    }

    private fun isLoggedin(): Boolean {
        return sharedPreferences.getBoolean("isloggedin",false)

    }
}
