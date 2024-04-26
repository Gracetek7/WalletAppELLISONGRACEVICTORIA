package com.ieschabas.pmdm.walletapp.ui.usuario

import android.app.AlertDialog
import android.content.Context
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.ieschabas.pmdm.walletapp.R
import com.ieschabas.pmdm.walletapp.data.TarjetasRepository
import com.ieschabas.pmdm.walletapp.model.Usuario
import com.ieschabas.pmdm.walletapp.model.tarjetas.Tarjeta
import com.squareup.picasso.Picasso
import kotlinx.coroutines.launch
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

interface UsuarioFragmentListener {
    fun seleccionarFoto()
    fun seleccionarFirma()
}

class UsuarioViewModel(private val context: Context, private val tarjetasRepository: TarjetasRepository) : ViewModel() {


    private var fragmentListener: UsuarioFragmentListener? = null

    // Método para establecer el listener del fragmento
    fun setFragmentListener(listener: UsuarioFragmentListener) {
        fragmentListener = listener
    }

    // Método en el ViewModel que llama al método seleccionarFoto() del fragmento
    fun llamarSeleccionarFoto() {
        fragmentListener?.seleccionarFoto()
    }
    // Método en el ViewModel que llama al método seleccionarFirma() del fragmento
    fun llamarSeleccionarFirma() {
        fragmentListener?.seleccionarFirma()
    }

    private val _usuarioActual = MutableLiveData<Usuario?>()
    val usuarioActual: MutableLiveData<Usuario?> get() = _usuarioActual

    private val _tarjetasUsuario = MutableLiveData<List<Tarjeta>>()
    val tarjetasUsuario: LiveData<List<Tarjeta>> get() = _tarjetasUsuario


    // LiveData para almacenar la URI de la foto seleccionada
    private val _fotoSeleccionadaUrl = MutableLiveData<Uri?>()
    val fotoSeleccionadaUrl: LiveData<Uri?> get() = _fotoSeleccionadaUrl

    // LiveData para almacenar la URI de la firma seleccionada
    private val _firmaSeleccionadaUrl = MutableLiveData<Uri?>()
    val firmaSeleccionadaUrl: LiveData<Uri?> get() = _firmaSeleccionadaUrl


    // Método para manejar el resultado de la selección de la foto
    fun handleSeleccionFotoResult(uri: Uri?) {
        _fotoSeleccionadaUrl.value = uri
        Log.d("UsuarioViewModel", "URI de la foto seleccionada: $uri")
    }

    // Método para manejar el resultado de la selección de la firma
    fun handleSeleccionFirmaResult(uri: Uri?) {
        _firmaSeleccionadaUrl.value = uri
        Log.d("UsuarioViewModel", "URI de la firma seleccionada: $uri")
    }


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
        builder.setItems(opcionesTarjeta) { dialog, which ->
            val usuarioId = FirebaseAuth.getInstance().currentUser?.uid
            usuario.let { user ->
                when (which) {
                    0 -> mostrarDialogoCrearTarjetaDNI(user)
                    1 -> mostrarDialogoCrearTarjetaSIP()
                    2 -> mostrarDialogoCrearTarjetaPermiso()
                }
            }
        }

        val dialog = builder.create()
        dialog.show()
    }

    private fun mostrarDialogoCrearTarjetaDNI(usuario: String) {
        Log.d("UsuarioViewModel", "en el metodo de mostrar Dialogo crear tarjeta DNI")

        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialogo_crear_tarjeta_dni, null)

        val editTextNumeroDocumento = dialogView.findViewById<EditText>(R.id.editTextNumeroDocumento)
        val editTextFechaNacimiento = dialogView.findViewById<EditText>(R.id.editTextFechaNacimiento)
        val editTextFechaExpedicion = dialogView.findViewById<EditText>(R.id.editTextFechaExpedicion)
        val editTextFechaCaducidad = dialogView.findViewById<EditText>(R.id.editTextFechaCaducidad)
        val editTextNombre = dialogView.findViewById<EditText>(R.id.editTextNombre)
        val editTextApellidos = dialogView.findViewById<EditText>(R.id.editTextApellidos)
        val editTextNacionalidad = dialogView.findViewById<EditText>(R.id.editTextNacionalidad)
        val editTextLugarNacimiento = dialogView.findViewById<EditText>(R.id.editTextLugarNacimiento)
        val editTextDomicilio = dialogView.findViewById<EditText>(R.id.editTextDomicilio)
        val buttonFotografiaUrl = dialogView.findViewById<ImageView>(R.id.btnSeleccionarFoto)
        val buttonTextFirmaUrl = dialogView.findViewById<ImageView>(R.id.btnSeleccionarFirma)

        val alertDialog = AlertDialog.Builder(context)
            .setTitle("Crear Tarjeta DNI")
            .setView(dialogView)
            .setPositiveButton("Crear") { dialog, _ ->
                val numeroDocumento = editTextNumeroDocumento.text.toString()
                val fechaNacimientoStr = editTextFechaNacimiento.text.toString()
                val fechaExpedicionStr = editTextFechaExpedicion.text.toString()
                val fechaCaducidadStr = editTextFechaCaducidad.text.toString()
                val nombre = editTextNombre.text.toString()
                val apellidos = editTextApellidos.text.toString()
                val nacionalidad = editTextNacionalidad.text.toString()
                val lugarNacimiento = editTextLugarNacimiento.text.toString()
                val domicilio = editTextDomicilio.text.toString()

                val formatoInput = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

                val fechaNacimiento: Date? = formatoInput.parse(fechaNacimientoStr)

                val fechaExpedicion: Date? = formatoInput.parse(fechaExpedicionStr)

                val fechaCaducidad: Date? = formatoInput.parse(fechaCaducidadStr)

                val fotografiaUrl = fotoSeleccionadaUrl.value // Obtener la URI de la foto seleccionada
                val firmaUrl = firmaSeleccionadaUrl.value // Obtener la URI de la firma seleccionada

                if (fotografiaUrl != null && firmaUrl != null) {
                    // Si se han seleccionado tanto la foto como la firma
                    viewModelScope.launch {
                        try {
                            if (fechaNacimiento != null) {
                                if (fechaExpedicion != null) {
                                    crearTarjetaDNI(
                                        numeroDocumento,
                                        fechaNacimiento,
                                        fechaExpedicion,
                                        fechaCaducidad!!,
                                        nombre,
                                        apellidos,
                                        usuario,
                                        nacionalidad,
                                        lugarNacimiento,
                                        domicilio,
                                        fotografiaUrl.toString(),
                                        firmaUrl.toString()
                                    )
                                }
                            }
                        } catch (e: Exception) {
                            _error.postValue("Error al crear la tarjeta DNI: ${e.message}")
                        }
                    }
                } else {
                    // Si falta seleccionar alguna de las imágenes, mostrar un mensaje de error
                    _error.postValue("Por favor, seleccione tanto la foto como la firma.")
                }
//                } else {
//                    // Mostrar mensaje de error si las fechas ingresadas no son válidas
//                    _error.postValue("Por favor, ingrese fechas válidas en formato dd/MM/yyyy.")
//                }
                dialog.dismiss()
            }

            .setNegativeButton("Cancelar") { dialog, _ ->
                dialog.dismiss()
            }
            .create()

        // Configurar el botón para seleccionar la foto
        buttonFotografiaUrl.setOnClickListener {
            // Llamar al método para seleccionar la foto
            llamarSeleccionarFoto()
        }

        // Configurar el botón para seleccionar la firma
        buttonTextFirmaUrl.setOnClickListener {
            // Llamar al método para seleccionar la firma
            llamarSeleccionarFirma()
        }

        // Observar los cambios en la URI de la foto seleccionada
        _fotoSeleccionadaUrl.observeForever { fotoUri: Uri? ->
            fotoUri?.let { uri ->
                // Cargar la imagen seleccionada en el ImageView correspondiente con Picasso
                Picasso.get().load(uri).into(buttonFotografiaUrl)
            }
        }

        // Observar los cambios en la URI de la firma seleccionada
        _firmaSeleccionadaUrl.observeForever { firmaUri: Uri? ->
            firmaUri?.let { uri ->
                // Cargar la imagen seleccionada en el ImageView correspondiente con Picasso
                Picasso.get().load(uri).into(buttonTextFirmaUrl)
            }
        }

        alertDialog.show()
    }

    suspend fun crearTarjetaDNI(
        numeroDocumento: String,
        fechaNacimiento: Date,
        fechaExpedicion: Date,
        fechaCaducidad: Date,
        nombre: String,
        apellidos: String,
        usuario: String,
        nacionalidad: String,
        lugarNacimiento: String,
        domicilio: String,
        fotografiaUrl: String,
        firmaUrl: String
    ) {
        try {
            // Crear la nueva tarjeta DNI con los datos proporcionados
              val nuevaTarjeta = Tarjeta.TarjetaDNI(
                idUsuario = usuario,
                numeroDocumento = numeroDocumento,
                fechaNacimiento = fechaNacimiento,
                fechaExpedicion = fechaExpedicion,
                fechaCaducidad = fechaCaducidad,
                nombre = nombre,
                apellidos = apellidos,
                sexo = obtenerSexoDesdeUsuario(usuario),
                nacionalidad = nacionalidad,
                lugarNacimiento = lugarNacimiento,
                domicilio = domicilio,
                fotografiaUrl = fotografiaUrl,
                firmaUrl = firmaUrl
            )

            // Llama al método en el repositorio para insertar la nueva tarjeta DNI
            crearTarjetaDNIDesdeObjeto(nuevaTarjeta)
        } catch (e: ParseException) {
            _error.postValue("Error al parsear la fecha: ${e.message}" +fechaNacimiento+ fechaExpedicion)
        }
    }

    suspend fun crearTarjetaDNIDesdeObjeto(nuevaTarjeta: Tarjeta.TarjetaDNI) {
        _isLoading.postValue(true)
        try {
            val response = tarjetasRepository.insertarTarjetaDNI(nuevaTarjeta)
            if (response != null && response.isSuccessful) {
                Log.i("TAG", "Se ha creado la tarjeta DNI correctamente.")
            } else {
                _error.postValue("Error al crear la tarjeta DNI")
                Log.d("Error UsuarioViewModel", "Error al crear la tarjeta DNI")
            }
        } catch (e: Exception) {
            _error.postValue("Error al crear la tarjeta DNI: ${e.message}")
        } finally {
            _isLoading.postValue(false)
        }
    }

    private fun obtenerSexoDesdeUsuario(usuario: String): Tarjeta.Sexo {
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