package com.example.idsign.data

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.wifi.WifiManager
import android.os.Build
import android.text.format.Formatter
import android.util.Log
import com.example.idsign.MainActivity
import com.example.idsign.network.IdSignApiService
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.net.Inet4Address
import java.net.InetAddress
import java.net.NetworkInterface
import java.util.Collections

interface AppContainer {
    val idSignDataRespository: IdSignDataRespository
}

//class DefaultAppContainer(): AppContainer {
//
//    private val baseUrl = "http://10.60.254.185:8080/"
////    private val baseUrl = "http://${ipAddress}:8080/"
//
////private val baseUrl = "http://${getWifiIpAddress(context) ?: getDeviceIpAddress() ?: "10.60.254.185"}:8080/" // Fallback to device IP or hardcoded IP
//
//    private val client = createOkHttpClient()
//
//    private val retrofit = Retrofit.Builder()
//        .addConverterFactory(ScalarsConverterFactory.create())
//        .client(client)
//        .baseUrl(baseUrl)
//        .build()
//
//    private val retrofitService: IdSignApiService by lazy {
//        retrofit.create(IdSignApiService::class.java)
//    }
//
//    override val idSignDataRespository: IdSignDataRespository by lazy {
//        IdSignDataRepositoryImpl(retrofitService)
//    }
//
//}
//
//fun createOkHttpClient(): OkHttpClient {
//    val logging = HttpLoggingInterceptor()
//    logging.setLevel(HttpLoggingInterceptor.Level.BODY)
//
//    return OkHttpClient.Builder()
//        .addInterceptor(logging)
//        .build()
//}
//

class DefaultAppContainer : AppContainer {

    companion object {
        var ipAddress: String = " "
            set(value) {
                field = value
                // Rebuild Retrofit when the IP is set
                instance?.buildRetrofit()
            }

        // Use this instance to access the AppContainer singleton
        var instance: DefaultAppContainer? = null
            private set
    }

    private var retrofitService: IdSignApiService? = null

    // This will hold your Retrofit instance
    init {
        instance = this
    }

    override val idSignDataRespository: IdSignDataRespository
        get() {
            retrofitService?.let {
                return IdSignDataRepositoryImpl(it)
            } ?: throw IllegalStateException("Retrofit service is not initialized yet")
        }

    // Build Retrofit instance only when IP is set
    fun buildRetrofit() {
        val baseUrl = "http://$ipAddress:8080/"

        val client = createOkHttpClient()

        retrofitService = Retrofit.Builder()
            .addConverterFactory(ScalarsConverterFactory.create())
            .client(client)
            .baseUrl(baseUrl)
            .build()
            .create(IdSignApiService::class.java)
    }
}

fun createOkHttpClient(): OkHttpClient {
    val logging = HttpLoggingInterceptor()
    logging.setLevel(HttpLoggingInterceptor.Level.BODY)

    return OkHttpClient.Builder()
        .addInterceptor(logging)
        .build()
}

