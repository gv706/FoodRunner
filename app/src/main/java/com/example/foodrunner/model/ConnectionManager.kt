package com.example.model.foodrunner

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.content.getSystemService

class ConnectionManager {
    fun isNetworkavailable(context: Context):Boolean{
        val connectivityManager=context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo:NetworkInfo?=connectivityManager.activeNetworkInfo
        return networkInfo!=null && networkInfo.isConnected
    }
}