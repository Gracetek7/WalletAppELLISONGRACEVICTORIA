package com.ieschabas.pmdm.walletapp.ui.usuario

import android.app.AlertDialog
import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ieschabas.pmdm.walletapp.data.TarjetasRepository
import com.ieschabas.pmdm.walletapp.model.Usuario
import com.ieschabas.pmdm.walletapp.model.tarjetas.Tarjeta
import com.ieschabas.pmdm.walletapp.ui.tarjetaDNI.TarjetaDNIViewModel
import com.ieschabas.pmdm.walletapp.ui.tarjetaSIP.TarjetaSIPViewModel
import kotlinx.coroutines.launch

class UsuarioViewModel(private val context: Context, private val tarjetasRepository: TarjetasRepository) : ViewModel() {

    private var tarjetaDNIViewModel: TarjetaDNIViewModel = TarjetaDNIViewModel(context, tarjetasRepository)
    private var tarjetaSIPViewModel: TarjetaSIPViewModel = TarjetaSIPViewModel(context, tarjetasRepository)

    private val _usuarioActual = MutableLiveData<Usuario?>()
    val usuarioActual: MutableLiveData<Usuario?> get() = _usuarioActual

    private val _tarjetasUsuario = MutableLiveData<List<Tarjeta>>()
    val tarjetasUsuario: LiveData<List<Tarjeta>> get() = _tarjetasUsuario

    private val _isLoading = MutableLiveData<Boolean>()
    private val _error = MutableLiveData<String>()

    val error: LiveData<String> get() = _error

    suspend fun cargarUsuarioActual(idUsuario: String) {
        _isLoading.value = true
        try {
            Log.d("UsuarioViewModel", "Cargando usuario con ID: $idUsuario")
            val usuarioResponse = tarjetasRepository.getUsuario(idUsuario)
            if (usuarioResponse != null) {
                if (usuarioResponse.isSuccessful) {
                    val usuario = usuarioResponse.body()
                    if (usuario != null) {
                        _usuarioActual.postValue(usuario)
                        Log.d("UsuarioViewModel", "Usuario cargado: $usuario")
                        cargarTarjetasUsuario(idUsuario) // Cargar también las tarjetas del usuario
                        return // Asegura que no se ejecute más código después de cargar las tarjetas
                    } else {
                        _error.postValue("Usuario no encontrado")
                    }
                } else {
                    _error.postValue("Error al cargar el usuario: ${usuarioResponse.message()}")
                }
            }
        } catch (e: Exception) {
            _error.postValue("Error al cargar el usuario: ${e.message}")
        } finally {
            _isLoading.value = false
        }
    }

    fun cargarTarjetasUsuario(idUsuario: String) {
        viewModelScope.launch {
            try {
                Log.d("UsuarioViewModel", "Cargando tarjetas del usuario con ID: $idUsuario")
                val tarjetas = tarjetasRepository.obtenerTarjetasUsuario(idUsuario)
                Log.d("UsuarioViewModel", "Tarjetas cargadas: $tarjetas")
                _tarjetasUsuario.postValue(tarjetas)
            } catch (e: Exception) {
                Log.e("UsuarioViewModel", "Error al cargar las tarjetas del usuario: ${e.message}")
                _error.postValue("Error al cargar las tarjetas del usuario: ${e.message}")
            }
        }
    }

    fun mostrarFormularioCrearTarjetas(usuario: String) {
        Log.d("UsuarioViewModel", "UsuarioViewModel, en el metodo de mostrar formulario crear tarjetas")
        val opcionesTarjeta = arrayOf("DNI", "SIP", "Permiso de Circulación")

        val builder = AlertDialog.Builder(context)
        builder.setTitle("Crear Tarjeta")
        builder.setItems(opcionesTarjeta) { _, which ->
            usuario.let { user ->
                when (which) {
                    0 -> tarjetaDNIViewModel.mostrarDialogoCrearTarjetaDNI(user)
                    1 -> tarjetaSIPViewModel.mostrarDialogoCrearTarjetaSIP(user)
                    //2 -> tarjetaPermisoCirculacionViewModel.mostrarDialogoCrearTarjetaPermiso(user)
                }
            }
        }

        val dialog = builder.create()
        dialog.show()
    }

    suspend fun actualizarUsuarioActual(usuarioActualizado: Usuario) {
        _isLoading.value = true
        try {
            val response =
                tarjetasRepository.modificarUsuario(usuarioActualizado.id, usuarioActualizado)
            if (response != null && response.isSuccessful) {
                _usuarioActual.postValue(usuarioActualizado)
            } else {
                _error.postValue("Error al actualizar el usuario")
            }
        } catch (e: Exception) {
            _error.postValue("Error al actualizar el usuario: ${e.message}")
        } finally {
            _isLoading.value = false
        }
    }

    suspend fun eliminarUsuarioActual(idUsuario: String) {
        _isLoading.value = true
        try {
            val response = tarjetasRepository.eliminarUsuario(idUsuario)
            if (response != null && response.isSuccessful) {
                _usuarioActual.postValue(null)
            } else {
                _error.postValue("Error al eliminar el usuario")
            }
        } catch (e: Exception) {
            _error.postValue("Error al eliminar el usuario: ${e.message}")
        } finally {
            _isLoading.value = false
        }
    }

    sealed class Resultado<out T> {
        data class Success<T>(val data: T) : Resultado<T>()
        data class Error(val message: String) : Resultado<Nothing>()
    }
}