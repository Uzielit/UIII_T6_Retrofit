package com.veterinaria.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.veterinaria.data.model.Mascota

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import android.net.Uri
import com.veterinaria.data.repository.MascotaRepository
import java.text.SimpleDateFormat
import java.util.Locale

import java.io.File
import java.io.FileOutputStream
import java.util.UUID

class AddViewModel(private val repository: MascotaRepository): ViewModel() {


    private val _nombre = MutableStateFlow("")
    val nombre = _nombre.asStateFlow()

    private val _especie = MutableStateFlow("")
    val especie = _especie.asStateFlow()

    private val _imageUrl = MutableStateFlow<Uri?>(null)
    val imageUrl = _imageUrl.asStateFlow()



    private val _fechaNacimientoStr = MutableStateFlow("")
    val fechaNacimientoStr = _fechaNacimientoStr.asStateFlow()

    private val _vacunado = MutableStateFlow(false)
    val vacunado = _vacunado.asStateFlow()
    private val _isSaving = MutableStateFlow(false)
    val isSaving = _isSaving.asStateFlow()

    // Para mostrar un mensaje de error si algo falla
    private val _saveError = MutableStateFlow<String?>(null)
    val saveError = _saveError.asStateFlow()


    fun onNombreChange(text: String) { _nombre.value = text }
    fun onEspecieChange(text: String) { _especie.value = text }
    fun onImageUrlChange(uri: Uri?) { _imageUrl.value = uri }
    fun onFechaNacimientoChange(text: String) { _fechaNacimientoStr.value = text }
    fun onVacunadoChange(isChecked: Boolean) { _vacunado.value = isChecked }

    fun savePet(onSaveComplete: (success: Boolean) -> Unit) {
        val nombre = _nombre.value
        val especie = _especie.value
        val fechaStr = _fechaNacimientoStr.value
        val vacunado = _vacunado.value
        val imagenUri = _imageUrl.value


        if (nombre.isBlank() || especie.isBlank() || imagenUri == null) {
            _saveError.value = "Nombre, especie e imagen son obligatorios."
            onSaveComplete(false)
            return
        }

        viewModelScope.launch {

            _isSaving.value = true
            _saveError.value = null

            try {

                val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                val fechaLong = try {
                    formatter.parse(fechaStr)?.time ?: System.currentTimeMillis()
                } catch (e: Exception) { System.currentTimeMillis() }

                //  Crear el objeto Mascota
                val nuevaMascota = Mascota(
                    id = 0,
                    nombre = nombre,
                    especie = especie,
                    imageUrl = null,
                    fechaNacimiento = fechaLong,
                    vacunado = vacunado
                )


                // Le pasamos el objeto de datos Y la Uri de la imagen por separado.
                repository.insertPet(nuevaMascota, imagenUri)

                _isSaving.value = false
                resetForm()
                onSaveComplete(true)

            } catch (e: Exception) {
                // 5. Error
                e.printStackTrace()
                _saveError.value = "Error al guardar: ${e.message}"
                _isSaving.value = false
                onSaveComplete(false)
            }
        }
    }

    private fun resetForm() {
        _nombre.value = ""
        _especie.value = ""
        _imageUrl.value = null
        _fechaNacimientoStr.value = ""
        _vacunado.value = false
    }
    /*
    private fun saveImageToInternalStorage(context: Context, uri: Uri): String {
        val inputStream = context.contentResolver.openInputStream(uri)

        val imagesDir = File(context.filesDir, "images")
        if (!imagesDir.exists()) {
            imagesDir.mkdir()
        }

        val fileName = "${UUID.randomUUID()}.jpg"
        val file = File(imagesDir, fileName)

        inputStream.use { input ->
            FileOutputStream(file).use { output ->
                input?.copyTo(output)
            }
        }
        return file.absolutePath
    }
     */


}
