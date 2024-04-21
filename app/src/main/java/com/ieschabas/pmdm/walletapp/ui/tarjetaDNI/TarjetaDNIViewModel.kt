package com.ieschabas.pmdm.walletapp.ui.tarjetaDNI

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ieschabas.pmdm.walletapp.data.TarjetasRepository
import com.ieschabas.pmdm.walletapp.model.tarjetas.Tarjeta
import kotlinx.coroutines.launch

class TarjetaDNIViewModel(private val context: Context, private val tarjetasRepository: TarjetasRepository) : ViewModel() {

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

    fun cargarTarjetaDNIUsuario(idUsuario: String) {
        viewModelScope.launch {
            try {
                Log.d("TarjetaDNIViewModel", "Cargando tarjetas del usuario con ID: $idUsuario")
                val tarjetas = tarjetasRepository.obtenerTarjetaDNIUsuario(idUsuario)
                Log.d("TarjetaDNIViewModel", "Tarjetas DNI cargadas: $tarjetas")
                _tarjetasDNI.postValue(tarjetas)
            } catch (e: Exception) {
                Log.e(
                    "TarjetaDNIViewModel",
                    "Error al cargar las tarjetas DNI del usuario: ${e.message}"
                )
                _error.postValue("Error al cargar las tarjetas DNI del usuario: ${e.message}")
            }
        }
    }

    // Método para cargar las tarjetas DNI desde el repositorio de tarjetas
    fun cargarTarjetaDNI(id: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            val tarjetasResponse = tarjetasRepository.getTarjetaDNI(id)
            if (tarjetasResponse != null && tarjetasResponse.isSuccessful) {
                _tarjetasDNI.postValue(listOf(tarjetasResponse.body()))
            } else {
                _error.postValue("Error al cargar las tarjetas DNI")
            }
            _isLoading.value = false
        }
    }

    // Método para modificar la tarjeta por el ID de la tarjeta
    fun modificarTarjetaDNI(id: Int, tarjeta: Tarjeta.TarjetaDNI) {
        viewModelScope.launch {
            val tarjetaDNI = tarjetasDNI.value?.find { it?.id == id }
            if (tarjetaDNI != null) {
                val response = tarjetasRepository.modificarTarjetaDNI(id, tarjeta)
                if (response?.isSuccessful == true) {
                    // Tarjeta modificada exitosamente
                } else {
                    Log.e("TarjetaDNIViewModel", "error al modificar la tarjeta dni")
                }
            } else {
                // Manejar el caso en que no se encuentre la tarjeta
            }
        }
    }

    // Método para eliminar la tarjeta por el ID de la tarjeta
    fun eliminarTarjetaDNI(id: Int) {
        viewModelScope.launch {
            val tarjetaDNI = tarjetasDNI.value?.find { it?.id == id }
            if (tarjetaDNI != null) {
                val response = tarjetasRepository.eliminarTarjetaDNI(id)
                if (response?.isSuccessful == true) {
                    // Tarjeta eliminada exitosamente
                } else {
                    Log.e("TarjetaDNIViewModel", "error al eliminar la tarjeta dni")
                }
            } else {
                // Manejar el caso en que no se encuentre la tarjeta
            }
        }
    }
}