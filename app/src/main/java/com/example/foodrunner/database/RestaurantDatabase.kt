package com.example.foodrunner.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [FavRestaurantsEntity::class],version = 1)
abstract class RestaurantDatabase:RoomDatabase() {

    abstract fun favRestaurantsDao():FavRestaurantsDao

}