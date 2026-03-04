package com.alfredo.geoeventosandroid.client

import com.alfredo.geoeventosandroid.service.EventoApiService

object RetrofitClient {
    private const val BASE_URL = "http://10.0.2.2:8080/" // IP para el emulador

    val instance: EventoApiService by lazy {
        retrofit2.Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(retrofit2.converter.gson.GsonConverterFactory.create())
            .build()
            .create(EventoApiService::class.java)
    }
}