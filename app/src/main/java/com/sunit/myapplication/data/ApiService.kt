package com.sunit.myapplication.data

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


object ApiService {
    val retrofit = Retrofit.Builder()
        .baseUrl("https://api.jsonbin.io/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    var service = retrofit.create(Api::class.java)
}

