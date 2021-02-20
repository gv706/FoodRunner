package com.example.foodrunner.fragment


import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Shader
import android.os.AsyncTask
import android.os.Bundle
import android.provider.Settings
import android.view.*
import androidx.fragment.app.Fragment
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.SearchView
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.foodrunner.R
import com.example.foodrunner.Restaurant
import com.example.foodrunner.adapter.RecyclerAdapter
import com.example.foodrunner.database.FavRestaurantsEntity
import com.example.foodrunner.database.RestaurantDatabase
import com.example.model.foodrunner.ConnectionManager
import org.json.JSONException
import java.util.*
import kotlin.Comparator
import kotlin.collections.HashMap

/**
 * A simple [Fragment] subclass.
 */
class HomeFragment : Fragment() {
    lateinit var recyclerHome: RecyclerView
    lateinit var recyclerAdapter: RecyclerAdapter
    lateinit var searchView:SearchView
    lateinit var animation:Animation
    var ratingComparator=Comparator<Restaurant>{
            restaurant1,restaurant2->
        if(restaurant1.rating.compareTo(restaurant2.rating,true)==0){
            restaurant1.name.compareTo(restaurant2.name,true)
        }
        else
        restaurant1.rating.compareTo(restaurant2.rating,true)
    }
    var costComparator=Comparator<Restaurant>{
            restaurant1,restaurant2->
        if(restaurant1.cost_for_one.compareTo(restaurant2.cost_for_one,true)==0)
        {
            restaurant1.name.compareTo(restaurant2.name,true)
        }
        else
        restaurant1.cost_for_one.compareTo(restaurant2.cost_for_one,true)
    }
    lateinit var linearLayoutManager: LinearLayoutManager
    lateinit var progressDialog: ProgressDialog
    var restaurantList= arrayListOf<Restaurant>()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        val  view=inflater.inflate(R.layout.fragment_home, container, false)
        searchView=view.findViewById(R.id.searchView)
        
        searchView.setOnQueryTextListener(object:SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
              recyclerAdapter.filter.filter(query)
                return false

            }
            override fun onQueryTextChange(newText: String?): Boolean {
                recyclerAdapter.filter.filter(newText)
                return false
            }

        })

        recyclerHome=view.findViewById(R.id.recyclerhome)
        setHasOptionsMenu(true)
        animation= AnimationUtils.loadAnimation(context,
            R.anim.animationforacardview
        )
        recyclerHome.startAnimation(animation)
        linearLayoutManager= LinearLayoutManager(activity)
        progressDialog = ProgressDialog(context)
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER)
        progressDialog.setCancelable(false)
        progressDialog.setTitle("Loading Restaurants..")
        progressDialog.setCanceledOnTouchOutside(false)
        progressDialog.show()
        val queue= Volley.newRequestQueue(activity as Context)
        val url="http://13.235.250.119/v2/restaurants/fetch_result/"
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
                            val restaurant= Restaurant(
                                jsonobj.getString("id"),
                                jsonobj.getString("name"),
                                jsonobj.getString("cost_for_one"),
                                jsonobj.getString("rating"),
                                jsonobj.getString("image_url")
                            )

                            restaurantList.add(restaurant)
                            recyclerAdapter= RecyclerAdapter(
                                activity as Context,
                                restaurantList
                            )
                            recyclerHome.layoutManager=linearLayoutManager
                            linearLayoutManager.orientation=LinearLayoutManager.VERTICAL
                            recyclerHome.adapter=recyclerAdapter


                        }
                    }
                    else
                    {
                        Toast.makeText(activity as Context,"Some error occured",Toast.LENGTH_LONG).show()

                    }
                }
                catch (e: JSONException)
                {
                    Toast.makeText(activity as Context,"JSON Error Occured",Toast.LENGTH_LONG).show()

                }

            },Response.ErrorListener {
                    progressDialog.cancel()

                Toast.makeText(activity as Context,"Unexpected error occured",Toast.LENGTH_LONG).show()

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
    class DbAsynctask(val context: Context, private val favRestaurantsEntity: FavRestaurantsEntity,
                      private val mode:Int):
        AsyncTask<Void, Void, Boolean>()
    {
       var db=Room.databaseBuilder(context,RestaurantDatabase::class.java,"restaurants-db").build()
        override fun doInBackground(vararg p0: Void?): Boolean {

            when(mode)
            {
                1->{
                    val favRestaurants:FavRestaurantsEntity? =db.favRestaurantsDao().getRestaurantById(favRestaurantsEntity.restaurant_id.toString())
                    db.close()
                    return favRestaurants!=null
                }
                2->{
                    db.favRestaurantsDao().insertRestaurant(favRestaurantsEntity)
                    db.close()
                    return true
                }
                3->{
                    db.favRestaurantsDao().deleteRestaurant(favRestaurantsEntity)
                    db.close()
                    return true
                }
            }
            return false
        }

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.homefragmentmenu,menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId==R.id.sort){
            val dialog= AlertDialog.Builder(activity as Context)
            val view=LayoutInflater.from(context).inflate(R.layout.sortitems,null)
            val radioGroup:RadioGroup=view.findViewById(R.id.radio)
            val sortBy:TextView=view.findViewById(R.id.sortBy)
            val shader = LinearGradient(0f, 0f, 0f, sortBy.textSize, Color.parseColor("#0053b3"), Color.parseColor("#0078ff") , Shader.TileMode.CLAMP)
            sortBy.paint.shader=shader
            dialog.setView(view)
            dialog.setCancelable(false)
            dialog.setPositiveButton("OK"){ text,listener->
                val selectedId=radioGroup.checkedRadioButtonId
                if (selectedId==-1)
                {
                    Toast.makeText(context,"Please choose the option",Toast.LENGTH_LONG).show()
                }
                else{
                    text.cancel()
                    when(selectedId)
                    {
                      R.id.costhightolow->{
                          Collections.sort(restaurantList,costComparator)
                          restaurantList.reverse()
                          recyclerAdapter.notifyDataSetChanged()
                      }
                        R.id.costlowtohigh->
                        {
                            Collections.sort(restaurantList,costComparator)
                            recyclerAdapter.notifyDataSetChanged()
                        }
                        R.id.rating->
                        {
                            Collections.sort(restaurantList,ratingComparator)
                            restaurantList.reverse()
                            recyclerAdapter.notifyDataSetChanged()
                        }
                    }
                }
            }
            dialog.setNegativeButton("CANCEL"){ text,listener->
                text.cancel()
            }
            dialog.create()
            dialog.show()

        }
        return true
    }


}
