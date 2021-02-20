package com.example.foodrunner.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName="Restaurants")
data class FavRestaurantsEntity (
    @PrimaryKey val restaurant_id:Int,
    @ColumnInfo(name = "restaurant_name") val restaurantName: String,
    @ColumnInfo(name = "restaurant_rating")val restaurantRating:String,
    @ColumnInfo(name = "restaurant_cost_for_one")val restaurantCostForOne:String,
    @ColumnInfo(name = "restaurant_image_url") val restaurantImage:String
)