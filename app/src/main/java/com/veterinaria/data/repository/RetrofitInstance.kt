package com.veterinaria.data.repository

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {

    // ¡¡¡MUY IMPORTANTE!!!
    // Esta es la IP que usa el emulador de Android para conectarse
    // al 'localhost' (127.0.0.1) de tu computadora.
    private const val BASE_URL = "http://192.168.100.6:5001/"

    // Creación "perezosa" (lazy) de la instancia de Retrofit
    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create()) // Usa Gson
            .build()
    }

    // Instancia pública de tu ApiService
    val api: ApiService by lazy {
        retrofit.create(ApiService::class.java)
    }
}
