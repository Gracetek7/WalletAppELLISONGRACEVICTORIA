package com.ieschabas.pmdm.walletapp.ui.tarjetaDNI

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Context
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.widget.EditText
import android.widget.ImageView
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ieschabas.pmdm.walletapp.R
import com.ieschabas.pmdm.walletapp.data.TarjetasRepository
import com.ieschabas.pmdm.walletapp.model.tarjetas.Tarjeta
import com.squareup.picasso.Picasso
import kotlinx.coroutines.launch
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
interface TarjetaDNIFragmentListener {
    // Método para seleccionar la foto
    fun seleccionarFoto()

    // Método para seleccionar la firma
    fun seleccionarFirma()
}

class TarjetaDNIViewModel(private val context: Context, private val tarjetasRepository: TarjetasRepository, ) : ViewModel() {

    private val _tarjetasUsuario = MutableLiveData<List<Tarjeta>>()
    val tarjetasUsuario: LiveData<List<Tarjeta>> get() = _tarjetasUsuario

    private var fragmentListener: TarjetaDNIFragmentListener? = null
    // Método para establecer el listener del fragmento
    fun setFragmentListener(listener: TarjetaDNIFragmentListener) {
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
    fun handleSeleccionFoto(selectedImageUri: Uri) {
        _fotoSeleccionadaUrl.value = selectedImageUri
    }

    fun handleSeleccionFirma(selectedImageUri: Uri) {
        _firmaSeleccionadaUrl.value = selectedImageUri
    }

    private val usuarioId: String = ""

    // LiveData para almacenar la lista de tarjetas DNI
    private val _tarjetasDNI = MutableLiveData<List<Tarjeta.TarjetaDNI?>>()
    val tarjetasDNI: MutableLiveData<List<Tarjeta.TarjetaDNI?>> get() = _tarjetasDNI

    // LiveData para almacenar la URI de la foto seleccionada
    private val _fotoSeleccionadaUrl = MutableLiveData<Uri?>()
    val fotoSeleccionadaUrl: LiveData<Uri?> get() = _fotoSeleccionadaUrl

    // LiveData para almacenar la URI de la firma seleccionada
    private val _firmaSeleccionadaUrl = MutableLiveData<Uri?>()
    val firmaSeleccionadaUrl: LiveData<Uri?> get() = _firmaSeleccionadaUrl


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

    // Método para modificar la tarjeta por el ID de la tarjeta
    fun modificarTarjetaDNI(tarjetaDNI: Tarjeta.TarjetaDNI) {
        viewModelScope.launch {
            val response = tarjetaDNI.id?.let { tarjetasRepository.modificarTarjetaDNI(it) }
            if (response?.isSuccessful == true) {
                // Tarjeta modificada exitosamente, actualizar la lista de tarjetas en el ViewModel
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

                } else {
                    Log.e("TarjetaDNIViewModel", "error al eliminar la tarjeta dni")
                }
            } catch (e: Exception) {
                Log.e("TarjetaDNIViewModel", "Error al eliminar la tarjeta dni: ${e.message}")
            }
        }
    }

    // Dialógo para crear nueva Tarjeta DNI
    fun mostrarDialogoCrearTarjetaDNI(usuario: String, tarjetaDNI: Tarjeta.TarjetaDNI? = null) {
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

        val formatoMostrar = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

        editTextFechaNacimiento.setOnClickListener {
            mostrarDatePickerDialog(editTextFechaNacimiento, formatoMostrar)
        }

        editTextFechaExpedicion.setOnClickListener {
            mostrarDatePickerDialog(editTextFechaExpedicion, formatoMostrar)
        }

        editTextFechaCaducidad.setOnClickListener {
            mostrarDatePickerDialog(editTextFechaCaducidad, formatoMostrar)
        }

        val alertDialog = AlertDialog.Builder(context)
            .setView(dialogView)
            .setPositiveButton("Crear") { dialog, _ ->
                val numeroDocumento = editTextNumeroDocumento.text.toString()
                val fechaNacimientoStr = editTextFechaNacimiento.text.toString()
                val fechaExpedicionStr = editTextFechaExpedicion.text.toString()
                val fechaCaducidadStr = editTextFechaCaducidad.text.toString()
                val nombre = editTextNombre.text.toString().uppercase()
                val apellidos = editTextApellidos.text.toString().uppercase()
                val nacionalidad = editTextNacionalidad.text.toString().uppercase()
                val lugarNacimiento = editTextLugarNacimiento.text.toString().uppercase()
                val domicilio = editTextDomicilio.text.toString().uppercase()

                val formatoInput = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

                val fechaNacimiento: Date? = formatoInput.parse(fechaNacimientoStr)
                val fechaExpedicion: Date? = formatoInput.parse(fechaExpedicionStr)
                val fechaCaducidad: Date? = formatoInput.parse(fechaCaducidadStr)

                val fotografiaUrl = fotoSeleccionadaUrl.value // Obtiene la URI de la foto seleccionada
                val firmaUrl = firmaSeleccionadaUrl.value // Obtiene la URI de la firma seleccionada

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
                dialog.dismiss()
            }

            .setNegativeButton("Cancelar") { dialog, _ ->
                dialog.dismiss()
            }
            .create()

        // Botón para seleccionar la foto
        buttonFotografiaUrl.setOnClickListener {
            // Llamar al método para seleccionar la foto
            llamarSeleccionarFoto()
        }

        // Botón para seleccionar la firma
        buttonTextFirmaUrl.setOnClickListener {
            // Llamar al método para seleccionar la firma
            llamarSeleccionarFirma()
        }

        // Observa los cambios en la URI de la foto seleccionada
        _fotoSeleccionadaUrl.observeForever { fotoUri: Uri? ->
            fotoUri?.let { uri ->
                // Carga la imagen seleccionada en el ImageView usando con Picasso
                Picasso.get().load(uri).into(buttonFotografiaUrl)
            }
        }

        // Observa los cambios en la URI de la firma seleccionada
        _firmaSeleccionadaUrl.observeForever { firmaUri: Uri? ->
            firmaUri?.let { uri ->
                // Carga la imagen seleccionada en el ImageView usando con Picasso
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
    // Método para insertar la nueva tarjeta DNI del usuario en el repositorio
    suspend fun crearTarjetaDNIDesdeObjeto(nuevaTarjeta: Tarjeta.TarjetaDNI) {
        _isLoading.postValue(true)
        try {
            val response = tarjetasRepository.insertarTarjetaDNI(nuevaTarjeta)
            if (response != null && response.isSuccessful) {
                cargarTarjetasUsuario(nuevaTarjeta.idUsuario)
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

    // Carga la lista de tarjetas del usuario asociado
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

    // Muestra un diálogo para seleccionar las fechas
    private fun mostrarDatePickerDialog(editText: EditText, formato: SimpleDateFormat) {
        val calendar = Calendar.getInstance()
        val datePickerDialog = DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                calendar.set(year, month, dayOfMonth)
                val fechaFormateada = formato.format(calendar.time)
                editText.setText(fechaFormateada)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.show()
    }

    // Método para obtener el sexo del usuario
    private fun obtenerSexoDesdeUsuario(usuario: String): Tarjeta.Sexo {
        return Tarjeta.Sexo.MASCULINO
    }

    // Calcula la fecha de caducidad de tarjeta DNI
    private fun calcularFechaCaducidad(fechaExpedicion: Date): Date {
        val calendar = Calendar.getInstance()
        calendar.time = fechaExpedicion
        calendar.add(Calendar.YEAR, 10)
        return calendar.time
    }

    // Método para manejar el resultado de la selección de la foto
    fun handleSeleccionImagenResult(isSelectingPhoto: Boolean, uri: Uri) {
        if (isSelectingPhoto) {
            _fotoSeleccionadaUrl.value = uri
            fragmentListener?.seleccionarFoto()
        } else {
            _firmaSeleccionadaUrl.value = uri
            fragmentListener?.seleccionarFirma()
        }
    }
}