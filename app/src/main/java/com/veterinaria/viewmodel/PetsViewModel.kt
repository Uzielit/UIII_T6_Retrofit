package com.veterinaria.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.veterinaria.data.model.Mascota
import com.veterinaria.data.repository.MascotaRepository

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch


class PetsViewModel (private val repository: MascotaRepository): ViewModel() {

    private val _petsUiState = MutableStateFlow<List<Mascota>>(emptyList())
    // 2. Estado público inmutable para que la UI observe
    val petsUiState: StateFlow<List<Mascota>> = _petsUiState.asStateFlow()

    // 3. Estado para manejar la Carga (Loading)
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // 4. Estado para manejar Errores de red
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    /**
     * El bloque init se llama cuando el ViewModel se crea.
     * Es el lugar perfecto para cargar los datos iniciales.
     */
    init {
        loadPets()
    }

    /**
     * Función que llama a la API (a través del repositorio)
     * para obtener las mascotas y actualizar los estados.
     */
    fun loadPets() {
        // Lanzamos una corutina en el scope del ViewModel
        viewModelScope.launch {

            _error.value = null     // Limpiamos errores previos
            try {
                // ¡Llamada a la API! Esta es la suspend fun
                val listaDeMascotas = repository.getAllPets()
                _petsUiState.value = listaDeMascotas // Actualizamos el estado con la lista
            } catch (e: Exception) {
                // Si algo falla (ej. sin internet), capturamos el error
                e.printStackTrace()
                _error.value = "Error al cargar mascotas: ${e.message}"
            }
            _isLoading.value = false // Terminamos de cargar
        }
    }



}
class PetViewModelFactory(
    private val repository: MascotaRepository,
    private val context: Context
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        // Tu factory está perfecto, no necesita cambios.
        if (modelClass.isAssignableFrom(PetsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PetsViewModel(repository) as T
        }

        // (Sería bueno añadir aquí tus otros ViewModels,
        // como AddViewModel y EditPetsViewModel, para tener un factory central)

        throw IllegalArgumentException("Unknown ViewModel class")
    }
}


/*
    private val _pets = MutableStateFlow<List<Mascota>>(emptyList())


    val petsUiState = _pets.asStateFlow()

    init {

    }

    fun fetchPets() {
        viewModelScope.launch {
            _pets.value = repository.getAllPets()
        }
    }
class PetViewModel(private val repository: PetRepository) : ViewModel() {

    // Usamos MutableStateFlow para poder actualizar la lista manualmente
    private val _petsUiState = MutableStateFlow<List<Pet>>(emptyList())
    val petsUiState = _petsUiState.asStateFlow() // La UI observa esto

    // init se llama cuando el ViewModel se crea
    init {
        fetchPets() // Cargamos las mascotas al iniciar
    }

    // Función para obtener las mascotas
    fun fetchPets() {
        viewModelScope.launch {
            _petsUiState.value = repository.getPets()
        }
    }

    // Función para añadir una nueva mascota
    fun addNewPet(name: String, species: String, breed: String, imageUri: Uri?) {
        viewModelScope.launch {
            repository.insertPet(name, species, breed, imageUri)
            fetchPets() // Refrescar la lista
        }
    }
}

class PetViewModelFactory(
    private val repository: PetRepository,
    private val context: Context // <-- AÑADIR CONTEXT
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PetViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            // Pasamos el repositorio (que ya tiene el context)
            return PetViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
 */