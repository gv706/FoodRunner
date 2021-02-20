package com.example.foodrunner.activity

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.*
import androidx.cardview.widget.CardView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.foodrunner.R
import com.example.model.foodrunner.ConnectionManager
import com.google.android.material.textfield.TextInputLayout
import kotlinx.android.synthetic.main.activity_login.*
import org.json.JSONObject

class Login : AppCompatActivity() {
    lateinit var edmoilenumber:TextInputLayout
    lateinit var edpassword:TextInputLayout
    lateinit var animation: Animation
    lateinit var navtosignup:TextView
    lateinit var navtoforgotpassword:TextView
    lateinit var btnlogin:Button
    lateinit var cardView: CardView
    lateinit var progressDialog:ProgressDialog
    lateinit var sharedPreferences: SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        cardView=findViewById(R.id.card)
        animation= AnimationUtils.loadAnimation(this,
            R.anim.animationforacardview
        )
        cardView.startAnimation(animation)
        edmoilenumber=findViewById(R.id.edmobilenumber)
        edpassword=findViewById(R.id.edpassword)
        navtosignup = findViewById(R.id.txtsignup)
        navtoforgotpassword=findViewById(R.id.txtforgotpassword)
        btnlogin=findViewById(R.id.btnlogin)
        sharedPreferences=getSharedPreferences(getString(R.string.preferences_file_name),Context.MODE_PRIVATE)
        edmoilenumber.editText?.setOnFocusChangeListener { view, b ->
            edmoilenumber.isFocusable=true
            edpassword.isFocusable=false
        }
        edpassword.editText?.setOnFocusChangeListener { view, b ->
            if(b)
            {
                edmoilenumber.isFocusable=false
                edpassword.isFocusable=true
            }
        }
        navtosignup.setOnClickListener {
            edmoilenumber.editText?.setText("")
            edpassword.editText?.setText("")
            edmobilenumber.error=null
            edpassword.error=null
            val intent = Intent(this@Login, Register::class.java)
            startActivity(intent)

        }
        navtoforgotpassword.setOnClickListener {
            edmoilenumber.editText?.setText("")
            edpassword.editText?.setText("")
            edmobilenumber.error=null
            edpassword.error=null
            val intent = Intent(this@Login, ForgotPassword::class.java)
            startActivity(intent)
        }
        btnlogin.setOnClickListener {
            val mobileno = edmoilenumber.editText!!.text.toString().trim()
            val password = edpassword.editText!!.text.toString()
            if (mobileno.isEmpty()) {
                edmoilenumber.setError("Please enter the mobile number")
                edpassword.error = null
                edmoilenumber.requestFocus()
                return@setOnClickListener
            } else if (mobileno.length!=10) {
                edmoilenumber.setError("Invalid mobile number")
                edpassword.error = null
                edmoilenumber.requestFocus()
                return@setOnClickListener
            }

            if (password.isEmpty()) {
                edpassword.error = "Please enter the password"
                edmoilenumber.setError(null)
                edpassword.requestFocus()
                return@setOnClickListener
            }
            else if(password.length<4){
                edpassword.error="Invalid password"
                edmoilenumber.error=null
                edpassword.requestFocus()
                return@setOnClickListener
            }
            val queue= Volley.newRequestQueue(this)
            val url="http://13.235.250.119/v2/login/fetch_result/"
            if(ConnectionManager().isNetworkavailable(this))
            {
                val jsonObject=JSONObject()
                jsonObject.put("mobile_number",mobileno)
                jsonObject.put("password",password)
                progressDialog= ProgressDialog(this)
                progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER)
                progressDialog.setCancelable(false)
                progressDialog.setTitle("Logging in..")
                progressDialog.setCanceledOnTouchOutside(false)
                progressDialog.show()
                val jsonObjectRequest=object : JsonObjectRequest(Request.Method.POST,url,jsonObject,
                    Response.Listener {
                        val datajsonobject:JSONObject=it.getJSONObject("data")
                        if(datajsonobject.getBoolean("success"))
                        {
                            progressDialog.cancel()
                            val jsonobj:JSONObject=datajsonobject.getJSONObject("data")
                            sharedPreferences.edit().putBoolean("isloggedin",true).apply()
                            sharedPreferences.edit().putString("userid",jsonobj.getString("user_id")).apply()
                            sharedPreferences.edit().putString("username",jsonobj.getString("name")).apply()
                            sharedPreferences.edit().putString("usermobilenumber",jsonobj.getString("mobile_number")).apply()
                            sharedPreferences.edit().putString("useremail",jsonobj.getString("email")).apply()
                            sharedPreferences.edit().putString("useraddress",jsonobj.getString("address")).apply()
                            val intent=Intent(this, MainActivity::class.java)
                            intent.putExtra("NAME",jsonobj.getString("name"))
                            startActivity(intent)
                            finish()

                        }
                        else{
                            progressDialog.cancel()
                            Toast.makeText(applicationContext,datajsonobject.getString("errorMessage"),Toast.LENGTH_LONG).show()

                        }

                },
                    Response.ErrorListener {
                        progressDialog.cancel()
                        Toast.makeText(applicationContext,"Unexpected error occured",Toast.LENGTH_LONG).show()



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
                    val intent=Intent(Settings.ACTION_WIRELESS_SETTINGS)
                    startActivity(intent)
                    text.cancel()
                    finish()
                }
                dialog.setNegativeButton("Cancel"){ text,listener->
                    text.cancel()


                }
                dialog.create()
                dialog.show()
            }

        }

    }
}
