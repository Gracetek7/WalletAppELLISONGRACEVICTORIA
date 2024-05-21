package com.ieschabas.pmdm.walletapp.ui.usuario

import android.app.AlertDialog
import android.content.Context
import android.util.Log
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.privacysandbox.tools.core.model.Method
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.firebase.messaging.FirebaseMessaging
import com.ieschabas.pmdm.walletapp.data.TarjetasRepository
import com.ieschabas.pmdm.walletapp.model.Usuario
import com.ieschabas.pmdm.walletapp.model.tarjetas.Tarjeta
import com.ieschabas.pmdm.walletapp.ui.tarjetaDNI.TarjetaDNIViewModel
import com.ieschabas.pmdm.walletapp.ui.tarjetaSIP.TarjetaSIPViewModel
import kotlinx.coroutines.launch
import okhttp3.Request
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

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

    // Recoge al usuario actual y llama al método de cargarTarjetasUsuarios con las tarjetas asociadas al usuario.
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
                        cargarTarjetasUsuario(idUsuario)
                        return
                    } else {
                        _error.postValue("Usuario no encontrado")
                    }
                } else {
                    _error.postValue("Error, al cargar el usuario: ${usuarioResponse.message()}")
                }
            }
        } catch (e: Exception) {
            _error.postValue("Error al cargar el usuario: ${e.message}")
        } finally {
            _isLoading.value = false
        }
    }

    // Carga las tarjetas asociadas del usuario
    fun cargarTarjetasUsuario(idUsuario: String) {
        viewModelScope.launch {
            try {
                Log.d("UsuarioViewModel", "Cargando las tarjetas del usuario con ID: $idUsuario")
                val tarjetas = tarjetasRepository.obtenerTarjetasUsuario(idUsuario)
                Log.d("UsuarioViewModel", "Total tarjetas cargadas: $tarjetas")
                _tarjetasUsuario.postValue(tarjetas)
            } catch (e: Exception) {
                Log.e("UsuarioViewModel", "Error, al cargar las tarjetas del usuario: ${e.message}")
                _error.postValue("Error, al cargar las tarjetas del usuario: ${e.message}")
            }
        }
    }

    // Muestra el diálogo para crear nueva tarjeta (DNI, SIP, Permiso de Circulación, Otro)
    fun mostrarFormularioCrearTarjetas(usuario: String, listener: (Boolean) -> Unit) {
        val opcionesTarjeta = arrayOf("DNI", "SIP", "Permiso de Circulación", "Otro")

        val builder = AlertDialog.Builder(context)
        builder.setTitle("Crear Tarjeta")
        builder.setItems(opcionesTarjeta) { _, which ->
            usuario.let { user ->
                when (which) {
                    // En caso de pulsar la primera opción abre el dialogo de Tarjeta DNI
                    0 -> {
                        listener(true)
                        tarjetaDNIViewModel.mostrarDialogoCrearTarjetaDNI(user)
                    }
                    // En caso de pulsar la segunda opción abre el dialogo de Tarjeta SIP
                    1 -> {
                        tarjetaSIPViewModel.mostrarDialogoCrearTarjetaSIP(user)
                    }
                    // En caso de pulsar la tercera opción abre el dialogo de Tarjeta PermisoCirculacion
//                    2 -> {
//                        tarjetaPermisoCirculacionViewModel.mostrarDialogoCrearTarjetaPermisoCirculacion(user)
//                    }
                    else -> {
                        listener(false)
                    }
                }
            }
        }

        val dialog = builder.create()
        dialog.show()
    }

    // Método que actualiza usuario actual
    suspend fun actualizarUsuarioActual(usuarioActualizado: Usuario) {
        _isLoading.value = true
        try {
            val response =
                tarjetasRepository.modificarUsuario(usuarioActualizado.id, usuarioActualizado)
            if (response != null && response.isSuccessful) {
                _usuarioActual.postValue(usuarioActualizado)
            } else {
                _error.postValue("Error, al actualizar el usuario")
            }
        } catch (e: Exception) {
            _error.postValue("Error, al actualizar el usuario: ${e.message}")
        } finally {
            _isLoading.value = false
        }
    }

    // Método que elimina usuario actual
    suspend fun eliminarUsuarioActual(idUsuario: String) {
        _isLoading.value = true
        try {
            val response = tarjetasRepository.eliminarUsuario(idUsuario)
            if (response != null && response.isSuccessful) {
                _usuarioActual.postValue(null)
            } else {
                _error.postValue("Error, al eliminar el usuario")
            }
        } catch (e: Exception) {
            _error.postValue("Error, al eliminar el usuario: ${e.message}")
        } finally {
            _isLoading.value = false
        }
    }
    // Método que envia una notificación push al iniciar sesión al usuario
    // con un mensaje indicando que se ha iniciado la sesión en WalletApp
    fun enviarNotificacionAlServidor() {
        // Obtiene el token del dispositivo actual
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val token = task.result

                // Crea el cuerpo de la notificación
                val notification = JSONObject().apply {
                    put("to", token)
                    put("data", JSONObject().put("message", "Se ha iniciado la sesión en WalletApp"))
                }

                // Configurar la conexión HTTP
                val url = URL("https://fcm.googleapis.com/fcm/send")
                (url.openConnection() as? HttpURLConnection)?.run {
                    requestMethod = "POST"
                    setRequestProperty("Content-Type", "application/json")
                    setRequestProperty("Authorization", "key=YOUR_SERVER_KEY")

                    doOutput = true
                    outputStream?.use { os ->
                        os.write(notification.toString().toByteArray())
                        os.flush()
                    }
                    // Lee la respuesta del servidor
                    inputStream?.use { response ->
                        // Lee la respuesta del servidor
                        val responseData = response.bufferedReader().use { it.readText() }
                        Log.d("UsuarioFragment", "Respuesta del servidor: $responseData")
                    }

                    disconnect()
                } ?: Log.e("UsuarioFragment", "Error, no se pudo abrir la conexión HTTP")
            } else {
                Log.e("UsuarioFragment", "Error, al obtener el token de FCM: ${task.exception}")
            }
        }
    }

    sealed class Resultado<out T> {
        data class Success<T>(val data: T) : Resultado<T>()
        data class Error(val message: String) : Resultado<Nothing>()
    }
}