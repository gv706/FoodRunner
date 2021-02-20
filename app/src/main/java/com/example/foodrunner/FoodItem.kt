package com.example.foodrunner

import android.os.Parcelable
import java.io.Serializable

data class FoodItem(
   var itemId:String,
   var itemName:String,
   var itemPrice:String
):Serializable