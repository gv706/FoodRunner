package com.example.foodrunner.activity

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.RecyclerView
import android.provider.Settings
import android.view.MenuItem
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.foodrunner.FoodItem
import com.example.foodrunner.R
import com.example.foodrunner.adapter.OrderItemsAdapter
import com.example.model.foodrunner.ConnectionManager
import com.google.gson.Gson
import org.json.JSONArray
import org.json.JSONObject


class CartActivity : AppCompatActivity() {
     lateinit var toolbar: Toolbar
    var restaurantId:String?=null
    var restaurantName:String?=null
    var userId:String?=null
    lateinit var btnPlaceOrder: Button
    lateinit var textView: TextView
    lateinit var progressDialog:ProgressDialog
    var list:String?=null
    lateinit var animation:Animation
    lateinit var linearLayoutManager:LinearLayoutManager
     lateinit var orderItemsList:ArrayList<FoodItem>
    lateinit var ordersRecyclerView: RecyclerView
    lateinit var orderItemsAdapter: OrderItemsAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)
        toolbar=findViewById(R.id.toolbar)
        textView=findViewById(R.id.text)
        btnPlaceOrder=findViewById(R.id.btnPlaceOrder)
        orderItemsList=ArrayList()
        val intent=intent
        val bundle:Bundle?=intent.extras
        if (bundle != null) {
            restaurantId=bundle.getString("RESTAURANT_ID")
            restaurantName=bundle.getString("RESTAURANT_NAME")
            userId=bundle.getString("USER_ID")
            list=bundle.getString("ORDER_LIST")
        }
        else{
            Toast.makeText(applicationContext,"Error Occured",Toast.LENGTH_LONG).show()
            finish()
        }
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.title="My Cart"
        textView.append(" $restaurantName")
        ordersRecyclerView=findViewById(R.id.recyclerOrderItems)
        orderItemsList.addAll(
            Gson().fromJson(list, Array<FoodItem>::class.java).asList()
        )
       orderItemsAdapter= OrderItemsAdapter(this, orderItemsList)
        animation= AnimationUtils.loadAnimation(this,
            R.anim.animationforacardview
        )
        ordersRecyclerView.startAnimation(animation)
        linearLayoutManager= LinearLayoutManager(this)
        ordersRecyclerView.layoutManager=linearLayoutManager
        linearLayoutManager.orientation=LinearLayoutManager.VERTICAL
        ordersRecyclerView.adapter=orderItemsAdapter
        var price:Int=0
        for (i in orderItemsList )
        {
            price += i.itemPrice.toInt()
        }
        btnPlaceOrder.append(" ( Rs.${price}/- )")
        btnPlaceOrder.setOnClickListener {
            val queue= Volley.newRequestQueue(this)
            val url="http://13.235.250.119/v2/place_order/fetch_result/"
            if(ConnectionManager().isNetworkavailable(this))
            {
                val jsonObject= JSONObject()
                jsonObject.put("user_id",userId)
                jsonObject.put("restaurant_id",restaurantId)
                jsonObject.put("total_cost",price)
                val jsonArray:JSONArray= JSONArray()
                var n:Int=0;
                for(obj in orderItemsList)
                {
                   val jsonObj=JSONObject()
                   jsonObj.put("food_item_id",obj.itemId)
                   jsonArray.put(n,jsonObj)
                    n += 1
                }
                jsonObject.put("food",jsonArray)
                progressDialog= ProgressDialog(this)
                progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER)
                progressDialog.setCancelable(false)
                progressDialog.setTitle("Please wait..")
                progressDialog.setCanceledOnTouchOutside(false)
                progressDialog.show()
                val jsonObjectRequest=object : JsonObjectRequest(Request.Method.POST,url,jsonObject,
                    Response.Listener {
                        val datajsonobject: JSONObject =it.getJSONObject("data")
                        if(datajsonobject.getBoolean("success"))
                        {
                            progressDialog.cancel()
                            val i=Intent(this,
                                OrderSuccessfulActivity::class.java)
                            startActivity(i)
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
                val dialog= android.app.AlertDialog.Builder(this)
                dialog.setMessage("Internet Connection is failed")
                dialog.setCancelable(false)
                dialog.setPositiveButton("Open Settings"){ text,listener->
                    text.cancel()
                    val i=Intent(Settings.ACTION_WIRELESS_SETTINGS)
                    startActivity(i)
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

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId==android.R.id.home)
        {
          finish()
        }
        return true
    }

}
