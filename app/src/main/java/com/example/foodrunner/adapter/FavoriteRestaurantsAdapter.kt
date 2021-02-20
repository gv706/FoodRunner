package com.example.foodrunner.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.clothes.RemovingFavoritesInterface
import com.example.foodrunner.fragment.HomeFragment
import com.example.foodrunner.R
import com.example.foodrunner.activity.RestaurantDescription
import com.example.foodrunner.database.FavRestaurantsEntity

import com.squareup.picasso.Picasso


class FavoriteRestaurantsAdapter(var context: Context,var list:List<FavRestaurantsEntity>,var removingFavoritesInterface: RemovingFavoritesInterface):RecyclerView.Adapter<FavoriteRestaurantsAdapter.RecyclerViewHolder>(){
    lateinit var animation: Animation
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerViewHolder {
        val view=LayoutInflater.from(parent.context).inflate(R.layout.recyclerdashboardsinglerow,parent,false)
        return RecyclerViewHolder(view)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: RecyclerViewHolder, position: Int) {
        val listobj=list[position]
        holder.name.text=listobj.restaurantName
        Picasso.get().load(listobj.restaurantImage).placeholder(R.drawable.defaultimage).into(holder.image_url)
        holder.rating.text=listobj.restaurantRating
        holder.cost_for_one.text= "Rs.${listobj.restaurantCostForOne}/-"
        holder.card.setOnClickListener {
            val intent= Intent(context, RestaurantDescription::class.java)
            intent.putExtra("RESTAURANT_ID",listobj.restaurant_id.toString())
            intent.putExtra("RESTAURANT_NAME",listobj.restaurantName)
            context.startActivity(intent)

        }
        val favRestaurantsEntity=FavRestaurantsEntity(
            listobj.restaurant_id,
            listobj.restaurantName,
            listobj.restaurantRating,
            listobj.restaurantCostForOne,
            listobj.restaurantImage
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
        holder.favredfullimage.setOnClickListener {
           removingFavoritesInterface.onRemoveItemClick(position)
            holder.favredimage.visibility=View.VISIBLE
            holder.favredfullimage.visibility=View.GONE
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