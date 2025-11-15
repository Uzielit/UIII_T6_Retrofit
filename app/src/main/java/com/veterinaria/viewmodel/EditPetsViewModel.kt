package com.veterinaria.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.veterinaria.data.model.Mascota

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import android.net.Uri
import com.veterinaria.data.repository.MascotaRepository

import java.io.File
import java.io.FileOutputStream
import java.util.UUID


class EditPetsViewModel(
    private val repository: MascotaRepository,
    private val petId: Int
) : ViewModel() {

    private val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

    private val _nombre = MutableStateFlow("")
    val nombre = _nombre.asStateFlow()

    private val _especie = MutableStateFlow("")
    val especie = _especie.asStateFlow()

    private val _imageUrl = MutableStateFlow<String?>(null)
    val imageUrl = _imageUrl.asStateFlow()

    private val _fechaNacimientoStr = MutableStateFlow("")
    val fechaNacimientoStr = _fechaNacimientoStr.asStateFlow()

    private val _vacunado = MutableStateFlow(false)
    val vacunado = _vacunado.asStateFlow()

    private val _nuevaImagenUri = MutableStateFlow<Uri?>(null)
    val nuevaImagenUri = _nuevaImagenUri.asStateFlow()

    // --- Estados de UI ---
    private val _isLoading = MutableStateFlow(true)
    val isLoading = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    private var mascotaOriginal: Mascota? = null

    init {
        loadPetData()
    }


    private fun loadPetData() {
        viewModelScope.launch {
            _isLoading.value = true
            try {

                val mascota = repository.getPetById(petId)
                if (mascota == null) {
                    _error.value = "No se pudo encontrar la mascota."
                } else {
                    mascotaOriginal = mascota
                    _nombre.value = mascota.nombre
                    _especie.value = mascota.especie
                    _imageUrl.value = mascota.imageUrl // URL Remota
                    _fechaNacimientoStr.value = formatter.format(Date(mascota.fechaNacimiento))
                    _vacunado.value = mascota.vacunado
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _error.value = "Error al cargar: ${e.message}"
            }
            _isLoading.value = false
        }
    }

    fun onNombreChange(text: String) { _nombre.value = text }
    fun onEspecieChange(text: String) { _especie.value = text }
    fun onFechaNacimientoChange(text: String) { _fechaNacimientoStr.value = text }
    fun onVacunadoChange(isChecked: Boolean) { _vacunado.value = isChecked }


    fun onNewImageSelected(uri: Uri?) {
        _nuevaImagenUri.value = uri
    }

    fun updatePet(onUpdateComplete: (success: Boolean) -> Unit) {
        val mascotaBase = mascotaOriginal ?: return

        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val fechaLong = try {
                    formatter.parse(_fechaNacimientoStr.value)?.time ?: System.currentTimeMillis()
                } catch (e: Exception) { System.currentTimeMillis() }

                val mascotaActualizada = Mascota(
                    id = mascotaBase.id,
                    nombre = _nombre.value,
                    especie = _especie.value,
                    imageUrl = mascotaBase.imageUrl,
                    fechaNacimiento = fechaLong,
                    vacunado = _vacunado.value
                )

                repository.updatePet(mascotaActualizada, _nuevaImagenUri.value)

                _isLoading.value = false
                onUpdateComplete(true)

            } catch (e: Exception) {
                e.printStackTrace()
                _error.value = "Error al actualizar: ${e.message}"
                _isLoading.value = false
                onUpdateComplete(false)
            }
        }
    }

    fun deletePet(onDeleteComplete: (success: Boolean) -> Unit) {
        val mascotaParaBorrar = mascotaOriginal ?: return

        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                repository.deletePet(mascotaParaBorrar)

                _isLoading.value = false
                onDeleteComplete(true)

            } catch (e: Exception) {
                e.printStackTrace()
                _error.value = "Error al eliminar: ${e.message}"
                _isLoading.value = false
                onDeleteComplete(false)
            }
        }
    }


 /*
    private fun saveImageToInternalStorage(context: Context, uri: Uri): String {
        val inputStream = context.contentResolver.openInputStream(uri)
        val imagesDir = File(context.filesDir, "images")
        if (!imagesDir.exists()) { imagesDir.mkdir() }
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