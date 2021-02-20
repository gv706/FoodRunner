package com.example.foodrunner.activity

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
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
import androidx.appcompat.app.AlertDialog
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

class Register : AppCompatActivity() {
    lateinit var edname:TextInputLayout
    lateinit var edemail:TextInputLayout
    lateinit var edphone:TextInputLayout
    lateinit var edpassword:TextInputLayout
    lateinit var edconfirmpassword:TextInputLayout
    lateinit var edaddress:TextInputLayout
    lateinit var btnregister:Button
    lateinit var cardView:CardView
    lateinit var animation:Animation
    lateinit var progressDialog:ProgressDialog
    lateinit var pattern: Pattern
    lateinit var sharedPreferences:SharedPreferences
    lateinit var toolbar:androidx.appcompat.widget.Toolbar
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        cardView=findViewById(R.id.card)
        animation= AnimationUtils.loadAnimation(this,
            R.anim.animationforacardview
        )
        cardView.startAnimation(animation)
        toolbar=findViewById(R.id.toolbar)
        edname=findViewById(R.id.edname)
        edemail=findViewById(R.id.edemail)
        edphone=findViewById(R.id.edphonenumber)
        edaddress=findViewById(R.id.edaddress)
        edpassword=findViewById(R.id.edpassword)
        edconfirmpassword=findViewById(R.id.edconfirmpassword)
        btnregister=findViewById(R.id.btnregister)
        sharedPreferences=getSharedPreferences(getString(R.string.preferences_file_name), Context.MODE_PRIVATE)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        setSupportActionBar(toolbar)
        titleColor=Color.parseColor("#0078ff")

        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        pattern = Patterns.EMAIL_ADDRESS
        supportActionBar?.title="Register Yourself"
        btnregister.setOnClickListener {
            val sname= edname.editText?.text.toString().trim()
            val semail=edemail.editText?.text.toString().trim()
            val sphonenumber=edphone.editText?.text.toString().trim()
            val saddress=edaddress.editText?.text.toString().trim()
            val spassword=edpassword.editText?.text.toString()
            val sconfirmpassword=edconfirmpassword.editText?.text.toString()
            if(sname.isEmpty())
            {
                edname.error="Name field is empty"
                edname.requestFocus()
                return@setOnClickListener
            }
            else if(sname.length<3)
            {
                edname.error="Minimum 3 characters"
                edname.requestFocus()
                return@setOnClickListener
            }
            if(semail.isEmpty())
            {
                edemail.error="Email field is empty"
                edemail.requestFocus()
                return@setOnClickListener
            }
            else if(!pattern.matcher(semail).matches())
            {
                edemail.error="Invalid email"
                edemail.requestFocus()
                return@setOnClickListener
            }
            if(sphonenumber.isEmpty())
            {
              edphone.error="Mobile number should n't be empty"
                edphone.requestFocus()
                return@setOnClickListener
            }
            else if(sphonenumber.length!=10)
            {
                edphone.error="Invalid mobile number"
                edphone.requestFocus()
                return@setOnClickListener
            }
            if(saddress.isEmpty())
            {
              edaddress.error="Address field is empty"
                edaddress.requestFocus()
                return@setOnClickListener
            }
            if(spassword.isEmpty())
            {
                edpassword.error="Password field is empty"
                edpassword.requestFocus()
                return@setOnClickListener
            }
            else if(spassword.length<4)
            {
                edpassword.error="Minimum 4 characters"
                edpassword.requestFocus()
                return@setOnClickListener
            }
             if(sconfirmpassword.isEmpty())
            {
                edconfirmpassword.error="Confirm Password field is empty"
                edconfirmpassword.requestFocus()
                return@setOnClickListener
            }
            else if(sconfirmpassword.length!=spassword.length)
             {
                 edconfirmpassword.error="Passwords should match"
                 edconfirmpassword.requestFocus()
                 return@setOnClickListener
             }
            val queue= Volley.newRequestQueue(this)
            val url="http://13.235.250.119/v2/register/fetch_result"
            if(ConnectionManager().isNetworkavailable(this))
            {
                progressDialog= ProgressDialog(this)
                progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER)
                progressDialog.setCancelable(false)
               progressDialog.setTitle("Registering..")
                progressDialog.setCanceledOnTouchOutside(false)
                progressDialog.show()
                val jsonObject= JSONObject()
                jsonObject.put("name",sname)
                jsonObject.put("email",semail)
                jsonObject.put("mobile_number",sphonenumber)
                jsonObject.put("address",saddress)
                jsonObject.put("password",spassword)
                val jsonObjectRequest=object : JsonObjectRequest(
                    Request.Method.POST,url,jsonObject,
                    Response.Listener {
                        val datajsonobject: JSONObject =it.getJSONObject("data")
                        if(datajsonobject.getBoolean("success"))
                        {
                            progressDialog.cancel()
                            val jsonobj: JSONObject =datajsonobject.getJSONObject("data")
                            sharedPreferences.edit().putBoolean("isloggedin",true).apply()
                            sharedPreferences.edit().putString("userid",jsonobj.getString("user_id")).apply()
                            sharedPreferences.edit().putString("username",jsonobj.getString("name")).apply()
                            sharedPreferences.edit().putString("usermobilenumber",jsonobj.getString("mobile_number")).apply()
                            sharedPreferences.edit().putString("useremail",jsonobj.getString("email")).apply()
                            sharedPreferences.edit().putString("useraddress",jsonobj.getString("address")).apply()
                            val intent= Intent(this, MainActivity::class.java)
                            intent.putExtra("NAME",jsonobj.getString("name"))
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
                dialog.setMessage("Internet Connection failed")
                dialog.setCancelable(false)
                dialog.setPositiveButton("Open Settings"){ text,listener->
                    val intent= Intent(Settings.ACTION_WIRELESS_SETTINGS)
                    text.cancel()
                    startActivity(intent)

                }
                dialog.setNegativeButton("cancel"){ text,listener->
                    text.cancel()
                }
                dialog.create()
                dialog.show()
            }
        }


    }



    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId==android.R.id.home)
        {
            finish()
        }
       return true
    }
}
