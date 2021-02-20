package com.example.foodrunner.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.foodrunner.fragment.HomeFragment
import com.example.foodrunner.R
import com.example.foodrunner.Restaurant
import com.example.foodrunner.activity.RestaurantDescription
import com.example.foodrunner.database.FavRestaurantsEntity

import com.squareup.picasso.Picasso
import java.lang.Character.toLowerCase
import java.util.*
import kotlin.collections.ArrayList


class RecyclerAdapter(var context: Context,var list:ArrayList<Restaurant>):RecyclerView.Adapter<RecyclerAdapter.RecyclerViewHolder>(),Filterable{

    var filterList=ArrayList<Restaurant>()
    lateinit var animation: Animation

    init {
        this.filterList=list
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerViewHolder {
        val view=LayoutInflater.from(parent.context).inflate(R.layout.recyclerdashboardsinglerow,parent,false)
        return RecyclerViewHolder(view)
    }

    override fun getItemCount(): Int {
        return filterList.size
    }

    override fun onBindViewHolder(holder: RecyclerViewHolder, position: Int) {
        val listobj=filterList[position]
        holder.name.text=listobj.name
        Picasso.get().load(listobj.image_url).placeholder(R.drawable.defaultimage).into(holder.image_url)
        holder.rating.text=listobj.rating
        holder.cost_for_one.text= "Rs.${listobj.cost_for_one}/person"
        holder.card.setOnClickListener {
            val intent= Intent(context, RestaurantDescription::class.java)
            intent.putExtra("RESTAURANT_ID",listobj.id)
            intent.putExtra("RESTAURANT_NAME",listobj.name)
            context.startActivity(intent)
        }
        val favRestaurantsEntity=FavRestaurantsEntity(
            listobj.id.toInt(),
            listobj.name,
            listobj.rating,
            listobj.cost_for_one,
            listobj.image_url
        )
        val checkFav= HomeFragment.DbAsynctask(
            context,
            favRestaurantsEntity,
            1
        ).execute()
        val isFav=checkFav.get()
        if(isFav) {
            holder.favredfullimage.visibility=View.VISIBLE
            holder.favredimage.visibility=View.GONE
        }
        else
        {
            holder.favredfullimage.visibility=View.GONE
            holder.favredimage.visibility=View.VISIBLE

        }
        holder.favredimage.setOnClickListener {
            val result= HomeFragment.DbAsynctask(
                context,
                favRestaurantsEntity,
                2
            ).execute().get()
            if(result)
            {
                Toast.makeText(context,"${listobj.name} restaurant is added into your favourites",Toast.LENGTH_LONG).show()
                holder.favredimage.visibility=View.GONE
                holder.favredfullimage.visibility=View.VISIBLE
            }
            else
                Toast.makeText(context,"Error Occured",Toast.LENGTH_LONG).show()

        }
        holder.favredfullimage.setOnClickListener {
            val result= HomeFragment.DbAsynctask(
                context,
                favRestaurantsEntity,
                3
            ).execute().get()
            if(result)
            {
                Toast.makeText(context,"${listobj.name} restaurant is removed from your favourites",Toast.LENGTH_LONG).show()
                holder.favredimage.visibility=View.VISIBLE
                holder.favredfullimage.visibility=View.GONE
            }
            else
                Toast.makeText(context,"Error Occured",Toast.LENGTH_LONG).show()

        }


    }
    override fun getFilter(): Filter {

      return object :Filter(){
          override fun performFiltering(p0: CharSequence?): FilterResults {
              val charSearch = p0.toString()
              if (charSearch.isEmpty()) {
                  filterList = list
              } else {
                  val resultList = ArrayList<Restaurant>()
                  for (row in list) {
                      if (row.name.toLowerCase(Locale.ROOT).contains(charSearch.toLowerCase(Locale.ROOT))) {
                          resultList.add(row)
                      }
                  }
                  filterList = resultList
              }
              val filterResults = FilterResults()
              filterResults.values = filterList
              return filterResults
          }

          override fun publishResults(p0: CharSequence?, p1: FilterResults?) {

              filterList = p1?.values as ArrayList<Restaurant>
              notifyDataSetChanged()
          }

      }

    }

    class RecyclerViewHolder(view: View): RecyclerView.ViewHolder(view)
    {
        var name:TextView=view.findViewById(R.id.name)
        var cost_for_one:TextView=view.findViewById(R.id.cost_for_one)
        var rating:TextView=view.findViewById(R.id.rating)
        var image_url:ImageView=view.findViewById(R.id.image_url)
        var card:CardView=view.findViewById(R.id.card)
        val favredimage:ImageView=view.findViewById(R.id.favredimage)
        val favredfullimage:ImageView=view.findViewById(R.id.favredfullimage)
    }
}