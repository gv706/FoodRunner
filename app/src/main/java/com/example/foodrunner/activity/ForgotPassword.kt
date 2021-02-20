package com.example.foodrunner.activity

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.util.Patterns
import android.view.MenuItem
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.Toast
import androidx.cardview.widget.CardView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.foodrunner.R
import com.example.model.foodrunner.ConnectionManager
import com.google.android.material.textfield.TextInputLayout
import org.json.JSONObject
import java.util.regex.Pattern
class ForgotPassword : AppCompatActivity() {
    lateinit var progressDialog: ProgressDialog
    lateinit var toolbar:androidx.appcompat.widget.Toolbar
    lateinit var pattern: Pattern
    lateinit var cardView: CardView
    lateinit var animation: Animation
    lateinit var btnnext:Button
    lateinit var edemail:TextInputLayout
    lateinit var edmobilenumber:TextInputLayout
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)
        cardView=findViewById(R.id.card)
        animation= AnimationUtils.loadAnimation(this,
            R.anim.animationforacardview
        )
        cardView.startAnimation(animation)
        toolbar=findViewById(R.id.toolbar)
         setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        titleColor= Color.parseColor("#0078ff")
        supportActionBar?.title="Forgot Password"
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        pattern = Patterns.EMAIL_ADDRESS
        edemail=findViewById(R.id.edemail)
        edmobilenumber=findViewById(R.id.edmobilenumber)
        btnnext=findViewById(R.id.btnnext)
        btnnext.setOnClickListener {
            val mobileno = edmobilenumber.editText!!.text.toString().trim()
            val email = edemail.editText!!.text.toString().trim()
            if (mobileno.isEmpty()) {
                edmobilenumber.setError("Please enter the mobile number")
                edemail.error = null
                edmobilenumber.requestFocus()
                return@setOnClickListener
            } else if (mobileno.length!=10) {
                edmobilenumber.setError("Invalid mobile number")
                edemail.error = null
                edmobilenumber.requestFocus()
                return@setOnClickListener
            }
            if(email.isEmpty())
            {
                edemail.error = "Please enter the email"
                edmobilenumber.error = null
                edemail.requestFocus()
                return@setOnClickListener
            }
            else if(!pattern.matcher(email).matches()){
                edemail.error="Invalid email"
                edmobilenumber.error=null
                edemail.requestFocus()
                return@setOnClickListener
            }
            val queue= Volley.newRequestQueue(this)
            val url="http://13.235.250.119/v2/forgot_password/fetch_result"
            if(ConnectionManager().isNetworkavailable(this))
            {
                val jsonObject= JSONObject()
                jsonObject.put("mobile_number",mobileno)
                jsonObject.put("email",email)
                progressDialog= ProgressDialog(this)
                progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER)
                progressDialog.setCancelable(false)
                progressDialog.setTitle("Verifying details..")
                progressDialog.setCanceledOnTouchOutside(false)
                progressDialog.show()
                val jsonObjectRequest=object : JsonObjectRequest(
                    Request.Method.POST,url,jsonObject,
                    Response.Listener {
                        val datajsonobject: JSONObject =it.getJSONObject("data")
                        if(datajsonobject.getBoolean("success"))
                        {
                            progressDialog.cancel()
                            Toast.makeText(applicationContext,"Please refer to the last email for OTP",Toast.LENGTH_LONG).show()
                            val intent= Intent(this,
                                ResetNewPassword::class.java)
                            intent.putExtra("mobile_number",mobileno)
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
                dialog.setMessage("Internet Connection is failed")
                dialog.setCancelable(false)
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
