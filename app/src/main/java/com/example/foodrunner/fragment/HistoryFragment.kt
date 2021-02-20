package com.example.foodrunner.fragment

import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.provider.Settings
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.foodrunner.FoodItem
import com.example.foodrunner.OrderHistory
import com.example.foodrunner.R
import com.example.foodrunner.adapter.HistoryItemsAdapter
import com.example.model.foodrunner.ConnectionManager
import com.google.gson.Gson
import org.json.JSONArray
import org.json.JSONException


class HistoryFragment : Fragment() {
    lateinit var recyclerOrderHistory:RecyclerView
    lateinit var animation:Animation
    var userId:String?=null
    lateinit var foodItemList:ArrayList<FoodItem>
    lateinit var orderHistoryList:ArrayList<OrderHistory>
    lateinit var sharedPreferences: SharedPreferences
    lateinit var progressDialog:ProgressDialog
    lateinit var historyItemsAdapter: HistoryItemsAdapter
    lateinit var linearLayoutManager: LinearLayoutManager
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
    {
         val view=inflater.inflate(R.layout.fragment_history, container, false)

        recyclerOrderHistory=view.findViewById(R.id.recyclerOrderItems)
        animation= AnimationUtils.loadAnimation(context,
            R.anim.animationforacardview
        )
        recyclerOrderHistory.startAnimation(animation)
        linearLayoutManager= LinearLayoutManager(activity)
        val dividerItemDecoration =DividerItemDecoration(
            recyclerOrderHistory.context,
            linearLayoutManager.orientation
        )
        recyclerOrderHistory.addItemDecoration(dividerItemDecoration);
        orderHistoryList=ArrayList<OrderHistory>()
        sharedPreferences= activity!!.getSharedPreferences(getString(R.string.preferences_file_name),Context.MODE_PRIVATE)
        userId= sharedPreferences.getString("userid","noid")
        progressDialog = ProgressDialog(context)
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER)
        progressDialog.setCancelable(false)
        progressDialog.setTitle("Loading history..")
        progressDialog.setCanceledOnTouchOutside(false)
        progressDialog.setCancelable(false)
        progressDialog.show()
        val queue= Volley.newRequestQueue(activity as Context)
        val url="http://13.235.250.119/v2/orders/fetch_result/${userId}"
        if(ConnectionManager().isNetworkavailable(activity as Context))
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
                                val foodArray:JSONArray=jsonobj.getJSONArray("food_items")
                                foodItemList=ArrayList<FoodItem>()
                                for(j in 0 until foodArray.length())
                                {
                                    val foodjsonobj=foodArray.getJSONObject(j)
                                    val itemId=foodjsonobj.getString("food_item_id")
                                    val itemName=foodjsonobj.getString("name")
                                    val itemCost=foodjsonobj.getString("cost")
                                    val foodItem=
                                        FoodItem(itemId, itemName, itemCost)
                                    foodItemList.add(foodItem)
                                }
                                val foodItemString=Gson().toJson(foodItemList)
                                val orderId=jsonobj.getString("order_id")
                                val restaurantName=jsonobj.getString("restaurant_name")
                                val totalCost=jsonobj.getString("total_cost")
                                val orderPlacedAt=jsonobj.getString("order_placed_at")
                                val orderHistory= OrderHistory(
                                    orderId,
                                    restaurantName,
                                    totalCost,
                                    orderPlacedAt,
                                    foodItemString
                                )
                                orderHistoryList.add(orderHistory)

                            }

                            historyItemsAdapter= HistoryItemsAdapter(
                                activity as Context,
                                orderHistoryList
                            )
                            recyclerOrderHistory.layoutManager=linearLayoutManager
                            linearLayoutManager.orientation=LinearLayoutManager.VERTICAL
                            recyclerOrderHistory.adapter=historyItemsAdapter
                        }
                        else
                        {
                            Toast.makeText(activity as Context,"Some error occured", Toast.LENGTH_LONG).show()

                        }
                    }
                    catch (e: JSONException)
                    {
                        Toast.makeText(activity as Context,"JSON Error Occured", Toast.LENGTH_LONG).show()

                    }

                }, Response.ErrorListener {
                    progressDialog.cancel()

                    Toast.makeText(activity as Context,"Unexpected error occured", Toast.LENGTH_LONG).show()

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
            val dialog= AlertDialog.Builder(activity as Context)
            dialog.setMessage("Internet Connection failed")
            dialog.setCancelable(false)
            dialog.setPositiveButton("Open Settings"){ text,listener->
                val intent= Intent(Settings.ACTION_WIRELESS_SETTINGS)
                startActivity(intent)
                activity?.finish()
            }
            dialog.setNegativeButton("Exit"){ text,listener->
                text.cancel()
                ActivityCompat.finishAffinity(activity as Activity)

            }
            dialog.create()
            dialog.show()
        }


         return  view
    }


}
