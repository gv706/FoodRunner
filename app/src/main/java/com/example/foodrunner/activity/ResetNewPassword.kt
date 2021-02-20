package com.example.foodrunner.activity

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.MenuItem
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.cardview.widget.CardView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.foodrunner.R
import com.example.model.foodrunner.ConnectionManager
import com.google.android.material.textfield.TextInputLayout
import org.json.JSONObject

class ResetNewPassword : AppCompatActivity() {
    lateinit var  progressDialog: ProgressDialog
    lateinit var toolbar:Toolbar
    lateinit var cardView:CardView
    lateinit var animation: Animation
    lateinit var edotp:TextInputLayout
    var mobilenumber:String?=null
    lateinit var edpassword:TextInputLayout
    lateinit var edconfirmpassword:TextInputLayout
    lateinit var btnsubmit:Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reset_new_password)
        cardView=findViewById(R.id.card)
        animation= AnimationUtils.loadAnimation(this,
            R.anim.animationforacardview
        )
        cardView.startAnimation(animation)
        toolbar=findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        titleColor= Color.parseColor("#0078ff")
        supportActionBar?.title="Set New Password"
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        edotp=findViewById(R.id.edotp)
        edpassword=findViewById(R.id.edpassword)
        edconfirmpassword=findViewById(R.id.edconfirmpassword)
        btnsubmit=findViewById(R.id.btnsubmit)
        val intent:Intent= intent
         mobilenumber=intent.getStringExtra("mobile_number")
        btnsubmit.setOnClickListener {
            val otp:String= edotp.editText?.text.toString().trim()
            val password:String= edpassword.editText?.text.toString()
            val confirmpassword:String= edconfirmpassword.editText?.text.toString()
            if(otp.isEmpty())
            {
                edotp.error="Please enter the OTP"
                edpassword.error=null
                edconfirmpassword.error=null
                return@setOnClickListener
            }
            else if(otp.length!=4)
            {
                edotp.error="Invalid OTP entered"
                edpassword.error=null
                edconfirmpassword.error=null
                return@setOnClickListener
            }
            if(password.isEmpty())
            {
              edpassword.error="Please enter the password"
               edotp.error=null
                edconfirmpassword.error=null
                return@setOnClickListener
            }
            else if(password.length<4)
            {
                edpassword.error="Minimum 4 characters"
                edotp.error=null
                edconfirmpassword.error=null
                return@setOnClickListener
            }
            if(confirmpassword.isEmpty())
            {
                edpassword.error=null
                edotp.error=null
                edconfirmpassword.error="Please enter the confirm password"
                return@setOnClickListener
            }
            else if(password.length!=confirmpassword.length)
            {
                edpassword.error=null
                edotp.error=null
                edconfirmpassword.error="Passwords should match"
                return@setOnClickListener
            }
            val queue= Volley.newRequestQueue(this)
            val url="http://13.235.250.119/v2/reset_password/fetch_result"
            if(ConnectionManager().isNetworkavailable(this))
            {
                val jsonObject= JSONObject()
                jsonObject.put("mobile_number",mobilenumber)
                jsonObject.put("password",password)
                jsonObject.put("otp",otp)
                progressDialog= ProgressDialog(this)
                progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER)
                progressDialog.setCancelable(false)
                progressDialog.setTitle("Please wait..")
                progressDialog.setCanceledOnTouchOutside(false)
                progressDialog.show()
                val jsonObjectRequest=object : JsonObjectRequest(
                    Request.Method.POST,url,jsonObject,
                    Response.Listener {
                        val datajsonobject: JSONObject =it.getJSONObject("data")
                        if(datajsonobject.getBoolean("success"))
                        {
                            progressDialog.cancel()
                             Toast.makeText(applicationContext,"Your password has been changed successfully",Toast.LENGTH_LONG).show()
                            val intent= Intent(this, Login::class.java)
                            startActivity(intent)
                            finish()

                        }
                        else{
                            progressDialog.cancel()
                            Toast.makeText(applicationContext,datajsonobject.getString("errorMessage"),
                                Toast.LENGTH_LONG).show()

                        }

                    },
                    Response.ErrorListener {
                        progressDialog.cancel()
                        Toast.makeText(applicationContext,"Unexpected error occured", Toast.LENGTH_LONG).show()
                    }){
                    override fun getHeaders(): MutableMap<String, String> {
                        val header=HashMap<String,String>()
                        header["Content-type"]="application/json"
                        header["token"]="4df0840b6f0628"
                        return header
                    }

                }
                queue.add(jsonObjectRequest)
            }
            else{
                val dialog= AlertDialog.Builder(this)
                dialog.setCancelable(false)
                dialog.setMessage("Internet Connection is failed")
                dialog.setPositiveButton("Open Settings"){ text,listener->
                    text.cancel()
                    val intent= Intent(Settings.ACTION_WIRELESS_SETTINGS)
                    startActivity(intent)
                    text.cancel()

                }
                dialog.setNegativeButton("Cancel"){ text,listener->
                    text.cancel()
                }
                dialog.create()
                dialog.show()
            }


        }


    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId==android.R.id.home)
            finish()
        return super.onOptionsItemSelected(item)
    }
}
