package com.veterinaria.data.repository

import com.veterinaria.data.model.Mascota
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path

interface ApiService {
    @GET("mascotas")
    suspend fun getAllPets(): List<Mascota>
    @GET("mascotas/{id}")
    suspend fun getPetById(@Path("id") id: Int): Mascota?

    @Multipart
    @POST("mascotas")
    suspend fun insertPet(
        @Part imagen: MultipartBody.Part?,
        @Part("nombre") nombre: RequestBody,
        @Part("especie") especie: RequestBody,
        @Part("fechaNacimiento") fechaNacimiento: RequestBody,
        @Part("vacunado") vacunado: RequestBody
    ): Mascota

    @Multipart
    @PUT("mascotas/{id}")
    suspend fun updatePet(
        @Path("id") id: Int,
        @Part imagen: MultipartBody.Part?,
        @Part("nombre") nombre: RequestBody,
        @Part("especie") especie: RequestBody,
        @Part("fechaNacimiento") fechaNacimiento: RequestBody,
        @Part("vacunado") vacunado: RequestBody
    ): Mascota // Devuelve la mascota actualizada


    @DELETE("mascotas/{id}")
    suspend fun deletePet(@Path("id") id: Int): Response<Unit> // Usamos Response<Unit> para verificar el código 204

}