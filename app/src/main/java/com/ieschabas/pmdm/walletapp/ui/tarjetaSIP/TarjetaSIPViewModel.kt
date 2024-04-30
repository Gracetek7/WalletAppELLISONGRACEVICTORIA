package com.ieschabas.pmdm.walletapp.ui.tarjetaSIP

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ieschabas.pmdm.walletapp.data.TarjetasRepository
import com.ieschabas.pmdm.walletapp.model.Usuario
import com.ieschabas.pmdm.walletapp.model.tarjetas.Tarjeta
import kotlinx.coroutines.launch

class TarjetaSIPViewModel (private val context: Context, private val tarjetasRepository: TarjetasRepository) : ViewModel() {

    // LiveData para almacenar la tarjeta SIP del usuario
    private val _tarjetasSIP = MutableLiveData<Tarjeta.TarjetaSIP?>()
    val tarjetasSIP: MutableLiveData<Tarjeta.TarjetaSIP?>
        get() = _tarjetasSIP

    private val _usuarioActual = MutableLiveData<Usuario?>()
    val usuarioActual: MutableLiveData<Usuario?> get() = _usuarioActual

    // LiveData para manejar el estado de carga
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean>
        get() = _isLoading

    // LiveData para manejar los errores
    private val _error = MutableLiveData<String>()
    val error: LiveData<String>
        get() = _error

    fun cargarTarjetaSIPUsuario(idUsuario: String) {
        viewModelScope.launch {
            try {
                Log.d("TarjetaSIPViewModel", "Cargando tarjeta SIP del usuario con ID: $idUsuario")
                val tarjetasSIP = tarjetasRepository.obtenerTarjetaSIPUsuario(idUsuario)
                if (tarjetasSIP.isNotEmpty()) {
                    _tarjetasSIP.postValue(tarjetasSIP[0])
                } else {
                    _error.postValue("No se encontró ninguna tarjeta SIP para el usuario")
                }
                Log.d("TarjetaSIPViewModel", "Tarjeta SIP cargada: ${_tarjetasSIP.value}")
            } catch (e: Exception) {
                Log.e("TarjetaSIPViewModel", "Error al cargar la tarjeta SIP del usuario: ${e.message}")
                _error.postValue("Error al cargar la tarjeta SIP del usuario: ${e.message}")
            }
        }
    }
    fun modificarTarjetaSIP(tarjetaSIP: Tarjeta.TarjetaSIP) {
        viewModelScope.launch {
            val response = tarjetaSIP.id.let { tarjetasRepository.modificarTarjetaSIP(tarjetaSIP) }
            if (response?.isSuccessful == true) {
                _tarjetasSIP.value = tarjetaSIP
                Log.i("TarjetaSIPViewModel", "Tarjeta SIP modificada correctamente")
            } else {
                Log.e("TarjetaSIPViewModel", "Error al modificar la tarjeta SIP: ${response?.message()}")
            }
        }
    }

    // Método para eliminar la tarjeta por el ID de la tarjeta
    fun eliminarTarjetaSIP(tarjetaSIP: Tarjeta.TarjetaSIP) {
        viewModelScope.launch {
            try {
                val response = tarjetaSIP.id.let { tarjetasRepository.eliminarTarjetaSIP(it) }
                if (response?.isSuccessful == true) {
                    // Tarjeta eliminada exitosamente
                    // Aquí también puedes actualizar la lista de tarjetas DNI si lo deseas
                    //_tarjetasSIP.value = tarjetaSIP
                } else {
                    Log.e("TarjetaSIPViewModel", "error al eliminar la tarjeta sip")
                }
            } catch (e: Exception) {
                Log.e("TarjetaSIPViewModel", "Error al eliminar la tarjeta sip: ${e.message}")
            }
        }
    }
}