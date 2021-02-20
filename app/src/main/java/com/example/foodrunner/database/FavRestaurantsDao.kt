package com.example.foodrunner.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query


@Dao
interface FavRestaurantsDao {
    @Insert
    fun insertRestaurant(favRestaurantsEntity: FavRestaurantsEntity)

    @Delete
    fun deleteRestaurant(favRestaurantsEntity: FavRestaurantsEntity)

    @Query("SELECT * FROM Restaurants")
    fun getAllRestaurants():MutableList<FavRestaurantsEntity>

    @Query("SELECT * FROM Restaurants WHERE restaurant_id=:restaurantId")
    fun getRestaurantById(restaurantId:String):FavRestaurantsEntity
}