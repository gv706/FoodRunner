package com.example.foodrunner.adapter


import android.content.Context

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.widget.Button
import android.widget.TextView

import androidx.recyclerview.widget.RecyclerView
import com.example.foodrunner.*


class RestaurantMenuAdapter(var context: Context, private var list:ArrayList<RestaurantMenu>, var proceedtocartInterface: ProceedtocartInterface, var listItems: ListItems):RecyclerView.Adapter<RestaurantMenuAdapter.RecyclerViewHolder>(){
    lateinit var animation: Animation
     var orderlist=ArrayList<FoodItem>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerViewHolder {
        val view=LayoutInflater.from(parent.context).inflate(R.layout.restaurantitemslayout,parent,false)
        return RecyclerViewHolder(view)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: RecyclerViewHolder, position: Int) {
        val listobj=list[position]
        val foodItem: FoodItem = FoodItem(
            listobj.id,
            listobj.name,
            listobj.cost_for_one
        )
        holder.id.text=listobj.id
        holder.name.text=listobj.name
        holder.cost_for_one.text= "Rs.${listobj.cost_for_one}/-"
        listItems.putList(orderlist)
        holder.btnadd.setOnClickListener {
            orderlist.add(foodItem)
           listItems.putList(orderlist)
            if(orderlist.size>0)
            {
                proceedtocartInterface.proceedtocart(1,orderlist)

            }
            holder.btnadd.visibility=View.GONE
            holder.btnremove.visibility=View.VISIBLE
        }
        holder.btnremove.setOnClickListener {

            orderlist.remove(foodItem)
            listItems.putList(orderlist)
            if(orderlist.size==0)
            {
                proceedtocartInterface.proceedtocart(0,orderlist)
            }
            holder.btnadd.visibility=View.VISIBLE
            holder.btnremove.visibility=View.GONE

        }
    }

    class RecyclerViewHolder(view: View): RecyclerView.ViewHolder(view)
    {
        var id:TextView=view.findViewById(R.id.txtitemnumber)
        var name:TextView=view.findViewById(R.id.txtitemname)
        var cost_for_one:TextView=view.findViewById(R.id.txtitemprice)
        var btnadd:Button=view.findViewById(R.id.btnitemadd)
        var btnremove:Button=view.findViewById(R.id.btnitemremove)

    }
}