package com.example.foodrunner.fragment


import android.app.ProgressDialog
import android.content.Context
import android.os.AsyncTask
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.loader.content.AsyncTaskLoader
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.example.clothes.RemovingFavoritesInterface
import com.example.foodrunner.R
import com.example.foodrunner.adapter.FavoriteRestaurantsAdapter
import com.example.foodrunner.database.FavRestaurantsEntity
import com.example.foodrunner.database.RestaurantDatabase

class FavoritesFragment : Fragment(),RemovingFavoritesInterface{



    lateinit var recyclerFavorites:RecyclerView
    lateinit var animation:Animation
    lateinit var db: RestaurantDatabase
lateinit var relativeLayout: RelativeLayout
    lateinit var favouriteRecyclerAdapter: FavoriteRestaurantsAdapter
    lateinit var linearLayoutManager: LinearLayoutManager
    var dbRestaurantslist= mutableListOf<FavRestaurantsEntity>()
    lateinit var progressDialog:ProgressDialog
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View?
    {
        val  view=inflater.inflate(R.layout.fragment_favorites, container, false)
        recyclerFavorites=view.findViewById(R.id.recyclerfavorites)
        relativeLayout=view.findViewById(R.id.relativelayout)
        animation= AnimationUtils.loadAnimation(context,
            R.anim.animationforacardview
        )
        recyclerFavorites.startAnimation(animation)
        linearLayoutManager= LinearLayoutManager(activity)
        progressDialog = ProgressDialog(context)
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER)
        progressDialog.setCancelable(false)
        progressDialog.setTitle("Loading Favorite Restaurants..")
        progressDialog.setCanceledOnTouchOutside(false)
        progressDialog.setCancelable(false)
        progressDialog.show()
        dbRestaurantslist=
            RecyclerFavourites(activity as Context)
                .execute().get()
        if(dbRestaurantslist.size==0)
        {
            progressDialog.dismiss()
            relativeLayout.visibility=View.VISIBLE
            recyclerFavorites.visibility=View.GONE
            return view

        }
        if(activity!=null)
        {
            progressDialog.dismiss()
            favouriteRecyclerAdapter= FavoriteRestaurantsAdapter(
                activity as Context,
                dbRestaurantslist,
                this
            )
            recyclerFavorites.adapter=favouriteRecyclerAdapter
            recyclerFavorites.layoutManager=linearLayoutManager

        }
        return  view

    }

    class RecyclerFavourites(val context: Context): AsyncTask<Void,Void,Void>(){
        lateinit var db: RestaurantDatabase

        override fun loadInBackground(): Void? {
            TODO("Not yet implemented")
        }

    }

    override fun onRemoveItemClick(position: Int) {
        val favRestaurantsEntity=FavRestaurantsEntity(
            dbRestaurantslist[position].restaurant_id,
            dbRestaurantslist[position].restaurantName,
            dbRestaurantslist[position].restaurantRating,
            dbRestaurantslist[position].restaurantCostForOne,
            dbRestaurantslist[position].restaurantImage
        )
        val status= HomeFragment.DbAsynctask(
            activity as Context,
            favRestaurantsEntity,
            3
        ).execute().get()
        if(status)
        {
            Toast.makeText(activity,"${dbRestaurantslist[position].restaurantName} is removed from favorites",Toast.LENGTH_LONG).show()

            dbRestaurantslist.removeAt(position)
            if(dbRestaurantslist.size==0){
                relativeLayout.visibility=View.VISIBLE
                recyclerFavorites.visibility=View.GONE
            }
            else{
                relativeLayout.visibility=View.GONE
                recyclerFavorites.visibility=View.VISIBLE

            }
            favouriteRecyclerAdapter.notifyDataSetChanged()
            favouriteRecyclerAdapter.notifyItemRemoved(position)
            favouriteRecyclerAdapter.notifyItemChanged(position)
        }
        else
            Toast.makeText(activity,"Unexpected error occurred",Toast.LENGTH_LONG).show()

    }

}
