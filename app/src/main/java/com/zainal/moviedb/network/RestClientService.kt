package com.zainal.moviedb.network

import com.google.gson.GsonBuilder
import com.zainal.moviedb.helper.PrefsHelper
import com.zainal.moviedb.util.Constant
import okhttp3.ConnectionPool
import okhttp3.OkHttpClient
import okhttp3.Protocol
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class RestClientService(private var prefsHelper: PrefsHelper) {
    fun getInstance(): ApiService {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val mClient = OkHttpClient.Builder().apply {
            readTimeout(30, TimeUnit.SECONDS)
            connectTimeout(50, TimeUnit.SECONDS)
            writeTimeout(50, TimeUnit.SECONDS)
            callTimeout(50, TimeUnit.SECONDS)
            connectionPool(ConnectionPool(0, 5, TimeUnit.MINUTES))
            protocols(listOf(Protocol.HTTP_1_1))
            addNetworkInterceptor(logging)
            addInterceptor { chain ->
                chain.proceed(chain.request().newBuilder()
                    .header("accept", "application/json")
                    .header("Authorization", "Bearer ${prefsHelper.accessToken()}")
                    .build()
                )
            }
        }

        val retrofit by lazy { Retrofit.Builder()
            .baseUrl(Constant.BASE_URL)
            .client(mClient.build())
            .addConverterFactory(GsonConverterFactory.create(GsonBuilder().setLenient().create()))
            .build()
        }
        return retrofit.create(ApiService::class.java)
    }
}