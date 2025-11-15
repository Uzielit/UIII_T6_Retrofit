package com.veterinaria.data.repository

import android.content.Context
import android.net.Uri
import com.veterinaria.data.model.Mascota

import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody

/*
class RepositoryMascota ( private val mascotaDao: MascotaDao)  {

    fun getAllPets (): Flow<List<Mascota>> =  mascotaDao.getAllPets()
    suspend fun insertPet (mascota: Mascota) {
        mascotaDao.insertPet(mascota)
    }
    suspend fun updatePet (mascota: Mascota) {
        mascotaDao.updatePet(mascota)
    }
    suspend fun deletePet (mascota: Mascota) {
        mascotaDao.deletePet(mascota)
    }
   suspend fun getPetById (id: Int): Mascota? {
       return mascotaDao.getPetById(id)
   }



}
 */
class MascotaRepository(
    private val apiService: ApiService,
    private val context: Context
) {


    suspend fun getAllPets(): List<Mascota> {

        return apiService.getAllPets()
    }

    suspend fun getPetById(id: Int): Mascota? {
        return apiService.getPetById(id)
    }

    suspend fun insertPet(mascota: Mascota, imageUri: Uri?) {

        val nombreBody = mascota.nombre.toRequestBody("text/plain".toMediaTypeOrNull())
        val especieBody = mascota.especie.toRequestBody("text/plain".toMediaTypeOrNull())
        val fechaBody = mascota.fechaNacimiento.toString().toRequestBody("text/plain".toMediaTypeOrNull())
        val vacunadoBody = mascota.vacunado.toString().toRequestBody("text/plain".toMediaTypeOrNull())

        val imagePart: MultipartBody.Part? = imageUri?.let { uri ->

            context.contentResolver.openInputStream(uri)?.use { inputStream ->
                val fileBytes = inputStream.readBytes()

                val mimeType = context.contentResolver.getType(uri)
                val requestFile = fileBytes.toRequestBody(mimeType?.toMediaTypeOrNull())

                val filename = File(uri.path ?: "image.jpg").name

                MultipartBody.Part.createFormData("imagen", filename, requestFile)
            }
        }


        apiService.insertPet(
            imagen = imagePart,
            nombre = nombreBody,
            especie = especieBody,
            fechaNacimiento = fechaBody,
            vacunado = vacunadoBody
        )
    }


    suspend fun updatePet(mascota: Mascota, newImageUri: Uri?) {

        val imagePart = uriToMultipartPart(newImageUri)
        apiService.updatePet(
            id = mascota.id,
            imagen = imagePart, // Será null si no hay imagen nueva
            nombre = textToRequestBody(mascota.nombre),
            especie = textToRequestBody(mascota.especie),
            fechaNacimiento = textToRequestBody(mascota.fechaNacimiento.toString()),
            vacunado = textToRequestBody(mascota.vacunado.toString())
        )
    }
    private fun textToRequestBody(text: String): RequestBody {
        return text.toRequestBody("text/plain".toMediaTypeOrNull())
    }
    private fun uriToMultipartPart(uri: Uri?): MultipartBody.Part? {
        if (uri == null) return null
        return context.contentResolver.openInputStream(uri)?.use { inputStream ->
            val fileBytes = inputStream.readBytes()
            val mimeType = context.contentResolver.getType(uri)
            val requestFile = fileBytes.toRequestBody(mimeType?.toMediaTypeOrNull())
            val filename = File(uri.path ?: "image.jpg").name
            MultipartBody.Part.createFormData("imagen", filename, requestFile)
        }
    }



    suspend fun deletePet(mascota: Mascota) {
        apiService.deletePet(mascota.id)
    }
}

