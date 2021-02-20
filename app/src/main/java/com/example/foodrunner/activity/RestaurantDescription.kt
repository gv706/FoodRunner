package com.example.foodrunner.activity

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.MenuItem
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.foodrunner.*
import com.example.foodrunner.adapter.RestaurantMenuAdapter
import com.example.model.foodrunner.ConnectionManager
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_restaurant_description.*
import org.json.JSONException

class RestaurantDescription : AppCompatActivity(), ProceedtocartInterface,
    ListItems {
    lateinit var progressDialog:ProgressDialog
    lateinit var animation:Animation
     var list:ArrayList<FoodItem>? = null
    lateinit var alert:AlertDialog
    lateinit var btnPlaceOrder:Button
     var restaurantId: String?=null
    var restaurantMenuList= arrayListOf<RestaurantMenu>()
    var restaurantName:String?=null
    lateinit var sharedPreferences: SharedPreferences
    lateinit var toolbar: Toolbar
    lateinit var restaurantDescriptionAdapter: RestaurantMenuAdapter
    lateinit var recyclerDescription:RecyclerView
   var userId:String?=null
    lateinit var linearLayoutManager:LinearLayoutManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_restaurant_description)
        sharedPreferences=getSharedPreferences(getString(R.string.preferences_file_name),Context.MODE_PRIVATE)
        userId= sharedPreferences.getString("userid","noid")
        restaurantId=intent.getStringExtra("RESTAURANT_ID")
        restaurantName=intent.getStringExtra("RESTAURANT_NAME")
        toolbar=findViewById(R.id.toolbar)
        btnPlaceOrder=findViewById(R.id.btnPlaceOrder)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.title=restaurantName
        recyclerDescription=findViewById(R.id.recyclerRestaurantDescription)
        animation= AnimationUtils.loadAnimation(this,
            R.anim.animationforacardview
        )
        recyclerDescription.startAnimation(animation)
        linearLayoutManager= LinearLayoutManager(this)
        progressDialog = ProgressDialog(this)
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER)
        progressDialog.setCancelable(false)
        progressDialog.setTitle("Loading $restaurantName items ..")
        progressDialog.setCanceledOnTouchOutside(false)
        progressDialog.show()
        val queue= Volley.newRequestQueue(this)
        val url="http://13.235.250.119/v2/restaurants/fetch_result/${restaurantId}"
        if(ConnectionManager().isNetworkavailable(this))
        {
            val jsonObjectRequest=object : JsonObjectRequest(
                Request.Method.GET,url,null, Response.Listener
                {
                    try{
                        progressDialog.cancel()
                        val jsonObject=it.getJSONObject("data")
                        val success=jsonObject.getBoolean("success")
                        if(success)
                        {
                            val data=jsonObject.getJSONArray("data")
                            for(i in 0 until data.length())
                            {
                                val jsonobj=data.getJSONObject(i)
                                val restaurantMenu= RestaurantMenu(
                                    jsonobj.getString("id"),
                                    jsonobj.getString("name"),
                                    jsonobj.getString("cost_for_one"),
                                    jsonobj.getString("restaurant_id")

                                )
                                restaurantMenuList.add(restaurantMenu)
                                restaurantDescriptionAdapter=
                                    RestaurantMenuAdapter(
                                        this,
                                        restaurantMenuList,
                                        this,
                                        this
                                    )
                                recyclerRestaurantDescription.layoutManager=linearLayoutManager
                                linearLayoutManager.orientation=LinearLayoutManager.VERTICAL
                                recyclerRestaurantDescription.adapter=restaurantDescriptionAdapter


                            }
                        }
                        else
                        {
                            Toast.makeText(applicationContext as Context,"Some error occured", Toast.LENGTH_LONG).show()

                        }
                    }
                    catch (e: JSONException)
                    {
                        Toast.makeText(applicationContext,"JSON Error Occured", Toast.LENGTH_LONG).show()

                    }

                }, Response.ErrorListener {
                    progressDialog.cancel()

                    Toast.makeText(applicationContext,"Unexpected error occured", Toast.LENGTH_LONG).show()

                }){
                override fun getHeaders(): MutableMap<String, String> {
                    val headers=HashMap<String,String>()
                    headers["Content-type"]="application/json"
                    headers["token"]="4df0840b6f0628"

                    return headers
                }
            }
            queue.add(jsonObjectRequest)
        }
        else{
            progressDialog.cancel()
            val dialog= AlertDialog.Builder(this)
            dialog.setCancelable(false)
            dialog.setMessage("Internet Connection failed")
            dialog.setPositiveButton("Open Settings"){ text,listener->
                val intent= Intent(Settings.ACTION_WIRELESS_SETTINGS)
                startActivity(intent)
                finish()
            }
            dialog.setNegativeButton("Exit"){ text,listener->
                text.cancel()
                ActivityCompat.finishAffinity(this)

            }
            dialog.create()
            dialog.show()
        }

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId==android.R.id.home  )
        {
            if(list?.size!=0) {
                val dialog = AlertDialog.Builder(this)
                dialog.setMessage("Going back will reset cart items.Do you still want to proceed?")
                dialog.setTitle("Confirmation")
                dialog.setCancelable(false)
                dialog.setPositiveButton("YES") { text, listener ->
                    text.cancel()
                    finish()
                }
                dialog.setNegativeButton("No") { text, listener ->
                    text.cancel()
                }
                alert = dialog.create()
                alert.show()
            }
            else
            {
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }
   override fun proceedtocart(flag:Int,orderList: ArrayList<FoodItem>)
    {
        if (flag==1) {
            btnPlaceOrder.visibility = View.VISIBLE
        }
        else
            btnPlaceOrder.visibility=View.GONE
        btnPlaceOrder.setOnClickListener {
           val intent=Intent(this, CartActivity::class.java)
            val list=Gson().toJson(orderList)
            val bundle= Bundle()
            bundle.putString("USER_ID",userId)
            bundle.putString("RESTAURANT_ID",restaurantId)
            bundle.putString("RESTAURANT_NAME",restaurantName)
            bundle.putString("ORDER_LIST",list)
            intent.putExtras(bundle)
            startActivity(intent)
        }
    }

    override fun onBackPressed() {
        if(list?.size!=0) {
            val dialog = AlertDialog.Builder(this)
            dialog.setMessage("Going back will reset cart items.Do you still want to proceed?")
            dialog.setTitle("Confirmation")
            dialog.setCancelable(false)
            dialog.setPositiveButton("YES") { text, listener ->
                text.cancel()
                finish()
            }
            dialog.setNegativeButton("No") { text, listener ->
                text.cancel()
            }
            alert = dialog.create()
            alert.show()
        }
        else
            finish()

    }
    override fun putList(orderList: ArrayList<FoodItem>) {
        this.list=orderList
    }

}
