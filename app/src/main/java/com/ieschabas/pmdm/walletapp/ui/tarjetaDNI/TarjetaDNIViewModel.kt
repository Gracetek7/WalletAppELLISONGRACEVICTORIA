package com.ieschabas.pmdm.walletapp.ui.tarjetaDNI

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.ieschabas.pmdm.walletapp.data.TarjetasRepository
import com.ieschabas.pmdm.walletapp.model.tarjetas.Tarjeta
import kotlinx.coroutines.launch

class TarjetaDNIViewModel(
    private val context: Context,
    private val tarjetasRepository: TarjetasRepository,
) : ViewModel() {

    private val usuarioId: String = ""

    // LiveData para almacenar la lista de tarjetas DNI
    private val _tarjetasDNI = MutableLiveData<List<Tarjeta.TarjetaDNI?>>()
    val tarjetasDNI: MutableLiveData<List<Tarjeta.TarjetaDNI?>> get() = _tarjetasDNI

    // LiveData para manejar el estado de carga
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean>
        get() = _isLoading

    // LiveData para manejar los errores
    private val _error = MutableLiveData<String>()
    val error: LiveData<String>
        get() = _error

    private var currentId: Int? = null

    init {
        // Cargar las tarjetas DNI del usuario cuando se crea la instancia de la vista
        cargarTarjetaDNIUsuario(usuarioId)
    }

    fun cargarTarjetaDNIUsuario(idUsuario: String) {
        viewModelScope.launch {
            try {
                Log.d("TarjetaDNIViewModel", "Cargando tarjetas del usuario con ID: $idUsuario")
                val tarjetas = tarjetasRepository.obtenerTarjetaDNIUsuario(idUsuario)
                Log.d("TarjetaDNIViewModel", "Tarjetas DNI cargadas: $tarjetas")
                _tarjetasDNI.postValue(tarjetas)

                // Verificar si la lista de tarjetas DNI está vacía
                if (tarjetas.isEmpty()) {
                    // Manejar el caso en que el usuario no tenga tarjetas DNI cargadas
                    _error.postValue("El usuario no tiene tarjetas DNI cargadas")
                }
            } catch (e: Exception) {
                Log.e(
                    "TarjetaDNIViewModel",
                    "Error al cargar las tarjetas DNI del usuario: ${e.message}"
                )
                _error.postValue("Error al cargar las tarjetas DNI del usuario: ${e.message}")
            }
        }
    }

//    // Método para cargar las tarjetas DNI desde el repositorio de tarjetas
//    fun cargarTarjetaDNI(id: Int) {
//        viewModelScope.launch {
//            _isLoading.value = true
//            val tarjetasResponse = tarjetasRepository.getTarjetaDNI(id)
//            if (tarjetasResponse != null && tarjetasResponse.isSuccessful) {
//                _tarjetasDNI.postValue(listOf(tarjetasResponse.body()))
//            } else {
//                _error.postValue("Error al cargar las tarjetas DNI")
//            }
//            _isLoading.value = false
//        }
//    }

    // Método para modificar la tarjeta por el ID de la tarjeta
    fun modificarTarjetaDNI(tarjetaDNI: Tarjeta.TarjetaDNI) {
        viewModelScope.launch {
            val response = tarjetaDNI.id?.let { tarjetasRepository.modificarTarjetaDNI(it) }
            if (response?.isSuccessful == true) {
                // Tarjeta modificada exitosamente
                // Actualizar la lista de tarjetas en el ViewModel
                //val tarjetasDNI = tarjetasRepository.obtenerTarjetaDNIUsuario(idUsuario)
                _tarjetasDNI.value = listOf(tarjetaDNI)
            } else {
                Log.e("TarjetaDNIViewModel", "error al modificar la tarjeta dni")
            }
        }
    }

    // Método para eliminar la tarjeta por el ID de la tarjeta
    fun eliminarTarjetaDNI(tarjetaDNI: Tarjeta.TarjetaDNI) {
        viewModelScope.launch {
            try {
                val response = tarjetaDNI.id?.let { tarjetasRepository.eliminarTarjetaDNI(it) }
                if (response?.isSuccessful == true) {
                    // Tarjeta eliminada exitosamente
                    // Aquí también puedes actualizar la lista de tarjetas DNI si lo deseas
                } else {
                    Log.e("TarjetaDNIViewModel", "error al eliminar la tarjeta dni")
                }
            } catch (e: Exception) {
                Log.e("TarjetaDNIViewModel", "Error al eliminar la tarjeta dni: ${e.message}")
            }
        }
    }
}