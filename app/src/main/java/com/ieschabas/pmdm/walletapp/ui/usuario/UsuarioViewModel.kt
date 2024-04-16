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
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Date

class UsuarioViewModel(private val context: Context, private val tarjetasRepository: TarjetasRepository) : ViewModel() {

    private val _usuarioActual = MutableLiveData<Usuario?>()
    val usuarioActual: MutableLiveData<Usuario?> get() = _usuarioActual

    private val _tarjetasUsuario = MutableLiveData<List<Tarjeta>>()
    val tarjetasUsuario: LiveData<List<Tarjeta>> get() = _tarjetasUsuario


    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

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
                        cargarTarjetasUsuario(idUsuario)
                        //mostrarFormularioCrearTarjetas(usuario)
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


    fun mostrarFormularioCrearTarjetas(usuario: Usuario) {
        val opcionesTarjeta = arrayOf("DNI", "SIP", "Permiso de Circulación")

        val builder = AlertDialog.Builder(context)
        builder.setTitle("Crear Tarjeta")
        builder.setItems(opcionesTarjeta) { dialog, which ->
            val usuario = usuarioActual.value
            usuario?.let { user ->
                when (which) {
                   // 0 -> mostrarDialogoCrearTarjetaDNI(user)
                    1 -> mostrarDialogoCrearTarjetaSIP()
                    2 -> mostrarDialogoCrearTarjetaPermiso()
                }
            }
        }

        val dialog = builder.create()
        dialog.show()
    }

//    private fun mostrarDialogoCrearTarjetaDNI(usuario: Usuario) {
//        val dialogView =
//            LayoutInflater.from(context).inflate(R.layout.dialogo_crear_tarjeta_dni, null)
//
//        val editTextNumeroDocumento =
//            dialogView.findViewById<EditText>(R.id.editTextNumeroDocumento)
//        val editTextFechaNacimiento =
//            dialogView.findViewById<EditText>(R.id.editTextFechaNacimiento)
//        val editTextFechaExpedicion =
//            dialogView.findViewById<EditText>(R.id.editTextFechaExpedicion)
//        val editTextNombre = dialogView.findViewById<EditText>(R.id.editTextNombre)
//        val editTextApellidos = dialogView.findViewById<EditText>(R.id.editTextApellidos)
//        val editTextNacionalidad = dialogView.findViewById<EditText>(R.id.editTextNacionalidad)
//        val editTextLugarNacimiento =
//            dialogView.findViewById<EditText>(R.id.editTextLugarNacimiento)
//        val editTextDomicilio = dialogView.findViewById<EditText>(R.id.editTextDomicilio)
//        val buttonFotografiaUrl = dialogView.findViewById<Button>(R.id.btnSeleccionarFoto)
//        val buttonTextFirmaUrl = dialogView.findViewById<Button>(R.id.btnFirma)
//
//        AlertDialog.Builder(context)
//            .setTitle("Crear Tarjeta DNI")
//            .setView(dialogView)
//            .setPositiveButton("Crear") { dialog, _ ->
//                val numeroDocumento = editTextNumeroDocumento.text.toString()
//                val fechaNacimiento = editTextFechaNacimiento.text.toString()
//                val fechaExpedicion = editTextFechaExpedicion.text.toString()
//                val nombre = editTextNombre.text.toString()
//                val apellidos = editTextApellidos.text.toString()
//                val nacionalidad = editTextNacionalidad.text.toString()
//                val lugarNacimiento = editTextLugarNacimiento.text.toString()
//                val domicilio = editTextDomicilio.text.toString()
//                val fotografiaUrl = buttonFotografiaUrl.text.toString()
//                val firmaUrl = buttonTextFirmaUrl.text.toString()
//
//
//                viewModelScope.launch {
//                    crearTarjetaDNI(
//                        numeroDocumento,
//                        fechaNacimiento,
//                        fechaExpedicion,
//                        nombre,
//                        apellidos,
//                        usuario,
//                        nacionalidad,
//                        lugarNacimiento,
//                        domicilio,
//                        fotografiaUrl,
//                        firmaUrl
//                    )
//                }
//
//                dialog.dismiss()
//            }
//            .setNegativeButton("Cancelar") { dialog, _ ->
//                dialog.dismiss()
//            }
//            .show()
//    }

    private fun seleccionarFoto() {
        // Aquí puedes abrir la galería o iniciar la cámara para que el usuario seleccione o tome una foto
    }

    private fun capturarFirma() {
        // Aquí puedes abrir una vista para que el usuario dibuje su firma y la captures
    }

//    suspend fun crearTarjetaDNI(
//        numeroDocumento: String,
//        fechaNacimiento: String,
//        fechaExpedicion: String,
//        nombre: String,
//        apellidos: String,
//        usuario: Usuario,
//        nacionalidad: String,
//        lugarNacimiento: String,
//        domicilio: String,
//        fotografiaUrl: String,
//        firmaUrl: String
//    ) {
//        try {
//            // Construir un objeto Date a partir de las cadenas de fecha proporcionadas
//            val fechaNacimientoDate =
//                SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).parse(fechaNacimiento)
//            val fechaExpedicionDate =
//                SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).parse(fechaExpedicion)
//
//            // Crear la nueva tarjeta DNI con los datos proporcionados
//            val nuevaTarjeta = Tarjeta.TarjetaDNI(
//                idUsuario = usuario.id,
//                numeroDocumento = numeroDocumento,
//                fechaNacimiento = fechaNacimientoDate!!,
//                fechaExpedicion = fechaExpedicionDate!!,
//                fechaCaducidad = calcularFechaCaducidad(fechaExpedicionDate),
//                nombre = nombre,
//                apellidos = apellidos,
//                sexo = obtenerSexoDesdeUsuario(usuario),
//                nacionalidad = nacionalidad,
//                lugarNacimiento = lugarNacimiento,
//                domicilio = domicilio,
//                fotografiaUrl = fotografiaUrl,
//                firmaUrl = firmaUrl
//            )
//
//            // Llama al método en el repositorio para insertar la nueva tarjeta DNI
//            crearTarjetaDNI(nuevaTarjeta)
//        } catch (e: ParseException) {
//            _error.postValue("Error al parsear la fecha: ${e.message}")
//        }
//    }

    suspend fun crearTarjetaDNI(nuevaTarjeta: Tarjeta.TarjetaDNI) {
        _isLoading.value = true
        try {
            val response = tarjetasRepository.insertarTarjetaDNI(nuevaTarjeta)
            if (response != null && response.isSuccessful) {
                Log.i("TAG", "Se ha creado la tarjeta DNI correctamente.")
            } else {
                _error.postValue("Error al actualizar el usuario")
            }
        } catch (e: Exception) {
            _error.postValue("Error al actualizar el usuario: ${e.message}")
        } finally {
            _isLoading.value = false
        }
    }

    private fun obtenerSexoDesdeUsuario(usuario: Usuario): Tarjeta.Sexo {
        return Tarjeta.Sexo.MASCULINO
    }


    private fun calcularFechaCaducidad(fechaExpedicion: Date): Date {
        val calendar = Calendar.getInstance()
        calendar.time = fechaExpedicion
        calendar.add(Calendar.YEAR, 10)
        return calendar.time
    }


    // Muestra el diálogo para la Tarjeta SIP
    private fun mostrarDialogoCrearTarjetaSIP() {
        // Implementación para crear tarjeta SIP
    }

    // Muestra el diálogo para el Permiso de Circulación
    private fun mostrarDialogoCrearTarjetaPermiso() {
        // Implementación para crear tarjeta Permiso de Circulación
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