package com.ieschabas.pmdm.walletapp.ui.tarjetaSIP

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ieschabas.pmdm.walletapp.data.TarjetasRepository
import com.ieschabas.pmdm.walletapp.model.tarjetas.Tarjeta
import kotlinx.coroutines.launch

class TarjetaSIPViewModel (private val tarjetasRepository: TarjetasRepository) : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is gallery Fragment"
    }
    val text: LiveData<String> = _text

    // LiveData para almacenar la lista de tarjetas DNI
    private val _tarjetasSIP = MutableLiveData<List<Tarjeta.TarjetaSIP?>>()
    val tarjetasSIP: LiveData<List<Tarjeta.TarjetaSIP?>>
        get() = _tarjetasSIP

    // LiveData para manejar el estado de carga
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean>
        get() = _isLoading

    // LiveData para manejar los errores
    private val _error = MutableLiveData<String>()
    val error: LiveData<String>
        get() = _error

    private var currentId: Int? = null

    fun setId(id: Int) {
        currentId = id
        cargarTarjetaSIP(id)
    }

    init {
        // If the id is available at this point, call setId
        if (currentId != null) {
            cargarTarjetaSIP(currentId!!)
        }
    }

    // Método para cargar las tarjetas DNI desde el repositorio de tarjetas
    fun cargarTarjetaSIP(id: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            val tarjetasResponse = tarjetasRepository.getTarjetaSIP(id)
            if (tarjetasResponse != null && tarjetasResponse.isSuccessful) {
                _tarjetasSIP.postValue(listOf(tarjetasResponse.body()))
            } else {
                _error.postValue("Error al cargar las tarjetas DNI")
            }
            _isLoading.value = false
        }
    }

    // Método para modificar la tarjeta por el ID de la tarjeta
    fun modificarTarjetaSIP(id: Int, tarjeta: Tarjeta.TarjetaDNI) {
        viewModelScope.launch {
            val tarjetaDNI = tarjetasSIP.value?.find { it?.id == id }
            if (tarjetaDNI != null) {
                val response = tarjetasRepository.modificarTarjetaDNI(id, tarjeta)
                if (response?.isSuccessful == true) {
                    // Tarjeta modificada exitosamente
                } else {
                    _error.postValue("Error al modificar las tarjeta SIP")
                }
            } else {
                _error.postValue("Error al encontrar la tarjeta DNI")
                _isLoading.value = false
            }
        }
    }

    // Método para eliminar la tarjeta por el ID de la tarjeta
    fun eliminarTarjetaSIP(id: Int) {
        viewModelScope.launch {
            val tarjetaDNI = tarjetasSIP.value?.find { it?.id == id }
            if (tarjetaDNI != null) {
                val response = tarjetasRepository.eliminarTarjetaDNI(id)
                if (response?.isSuccessful == true) {
                    // Tarjeta eliminada exitosamente
                } else {
                    _error.postValue("Error al eliminar la tarjeta SIP")             }
            } else {
                _error.postValue("Error al encontrar la tarjeta SIP")
                _isLoading.value = false
            }
        }
    }
}