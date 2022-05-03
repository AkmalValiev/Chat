package uz.evkalipt.sevenmodullesson412.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build

@Suppress("DEPRECATION")
class NetworkHelper constructor(private val context: Context) {

    fun isNetWorkConnected():Boolean{

        var result = false
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT>= Build.VERSION_CODES.M){
            val network = connectivityManager.activeNetwork ?: return false
            val activityNetwork =
                connectivityManager.getNetworkCapabilities(network) ?: return false
            result = when{
                activityNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)->true
                activityNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)->true
                activityNetwork.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)->true
                else->false
            }
        }else{
            connectivityManager.run {
                connectivityManager.activeNetworkInfo?.run {
                    result = when(type){
                        ConnectivityManager.TYPE_WIFI->true
                        ConnectivityManager.TYPE_MOBILE->true
                        ConnectivityManager.TYPE_ETHERNET->true
                        else->false
                    }
                }
            }
        }

        return result

    }

}