package com.example.foodrunner.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.foodrunner.FoodItem
import com.example.foodrunner.R


class OrderItemsAdapter(var context: Context,var list:List<FoodItem>):RecyclerView.Adapter<OrderItemsAdapter.RecyclerViewHolder>(){
    lateinit var animation: Animation
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerViewHolder {
        val view=LayoutInflater.from(parent.context).inflate(R.layout.orderitemslayout,parent,false)
        return RecyclerViewHolder(view)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: RecyclerViewHolder, position: Int) {
        val item=list[position]
        holder.itemname.text=item.itemName
        holder.itemprice.text="Rs.${item.itemPrice}/-"
    }
    class RecyclerViewHolder(view: View): RecyclerView.ViewHolder(view)
    {
        var itemname:TextView=view.findViewById(R.id.txtitemname)
        var itemprice:TextView=view.findViewById(R.id.txtitemprice)
    }
}