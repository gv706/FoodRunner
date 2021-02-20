package com.example.foodrunner.adapter

import android.content.Context
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Shader
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.foodrunner.FoodItem
import com.example.foodrunner.OrderHistory
import com.example.foodrunner.R
import com.google.gson.Gson


class HistoryItemsAdapter(var context: Context,var list:ArrayList<OrderHistory>):RecyclerView.Adapter<HistoryItemsAdapter.RecyclerViewHolder>(){
    lateinit var animation: Animation
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerViewHolder {
        val view=LayoutInflater.from(parent.context).inflate(R.layout.orderhistorysingleitemlayout,parent,false)
        return RecyclerViewHolder(view)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: RecyclerViewHolder, position: Int) {

        val orderHistoryItem=list[position]
        val shader = LinearGradient(0f, 0f, 0f, holder.restaurantName.textSize,Color.parseColor("#0053b3"),Color.parseColor("#0078ff") ,Shader.TileMode.CLAMP)
        holder.restaurantName.paint.shader = shader
        val shader1 = LinearGradient(0f, 0f, 0f, holder.orderPlacedAt.textSize,Color.parseColor("#0053b3"),Color.parseColor("#0078ff"), Shader.TileMode.CLAMP)
        holder.orderPlacedAt.paint.shader=shader1
        holder.restaurantName.text=orderHistoryItem.restaurantName
        holder.orderPlacedAt.text=orderHistoryItem.orderPlacedAt.substring(0,8).replace('-','/')
        val foodItemString=orderHistoryItem.foodItems
        val foodItemsList=ArrayList<FoodItem>()
        foodItemsList.addAll(
            Gson().fromJson(foodItemString, Array<FoodItem>::class.java).asList()
        )

        holder.linearLayout.removeAllViewsInLayout()
        for(foodItem in foodItemsList){

            val itemName=foodItem.itemName
            val itemPrice=foodItem.itemPrice
            val params:LinearLayout.LayoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.MATCH_PARENT);
            val orderItemsLayout=LayoutInflater.from(context).inflate(R.layout.orderitemslayout,null)
            orderItemsLayout.layoutParams=params
            val txtitemname:TextView=orderItemsLayout.findViewById(R.id.txtitemname)
            val txtitemprice:TextView=orderItemsLayout.findViewById(R.id.txtitemprice)
            txtitemname.text=itemName
            txtitemprice.text="Rs.${itemPrice}/-"
            holder.linearLayout.addView(orderItemsLayout)

        }

    }
    class RecyclerViewHolder(view: View): RecyclerView.ViewHolder(view)
    {
        var restaurantName:TextView=view.findViewById(R.id.txtresname)
        var orderPlacedAt:TextView=view.findViewById(R.id.txtdate)
        var linearLayout:LinearLayout=view.findViewById(R.id.linearLayout)
    }
}