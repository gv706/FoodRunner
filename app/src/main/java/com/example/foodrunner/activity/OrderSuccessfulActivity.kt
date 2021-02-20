package com.example.foodrunner.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager
import android.widget.Button
import com.example.foodrunner.R

class OrderSuccessfulActivity : AppCompatActivity() {
   lateinit var btnOK:Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //show the activity in full screen
        setContentView(R.layout.activity_order_successful)
        btnOK=findViewById(R.id.btnOK)
        btnOK.setOnClickListener {
            val i=Intent(this@OrderSuccessfulActivity,
                MainActivity::class.java)
            startActivity(i)
            finish()

        }
    }

    override fun onBackPressed() {
        //nothing to do
    }
}
