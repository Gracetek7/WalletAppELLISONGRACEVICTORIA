package com.ieschabas.pmdm.walletapp.ui.tarjetaSIP

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.widget.EditText
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ieschabas.pmdm.walletapp.R
import com.ieschabas.pmdm.walletapp.data.TarjetasRepository
import com.ieschabas.pmdm.walletapp.model.Usuario
import com.ieschabas.pmdm.walletapp.model.tarjetas.Tarjeta
import kotlinx.coroutines.launch
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class TarjetaSIPViewModel (private val context: Context, private val tarjetasRepository: TarjetasRepository) : ViewModel() {

    // MutableLiveData para almacenar la tarjeta SIP del usuario
    // el valor de los datos almacenados dentro de este puede ser modificado
    private val _tarjetasSIP = MutableLiveData<Tarjeta.TarjetaSIP?>()
    val tarjetasSIP: MutableLiveData<Tarjeta.TarjetaSIP?> get() = _tarjetasSIP

    private val _usuarioActual = MutableLiveData<Usuario?>()
    val usuarioActual: MutableLiveData<Usuario?> get() = _usuarioActual

    private val _tarjetasUsuario = MutableLiveData<List<Tarjeta>>()
    val tarjetasUsuario: LiveData<List<Tarjeta>> get() = _tarjetasUsuario

    // LiveData para manejar el estado de carga
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    // LiveData para manejar los errores
    private val _error = MutableLiveData<String>()
    val error: LiveData<String> get() = _error

    // Método para cargar la Tarjeta SIP del usuario
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

    // Método para modificar la tarjeta SIP
    fun modificarTarjetaSIP(id: Int, tarjetaSIP: Tarjeta.TarjetaSIP) {
        viewModelScope.launch {
            Log.d("TarjetaSIPViewModel", "Tarjeta SIP id: $id")
            val response = tarjetaSIP.id.let { tarjetasRepository.modificarTarjetaSIP(id, tarjetaSIP) }
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
                    // Si la tarjeta se eliminó correctamente
                    // Actualiza la lista de tarjetas SIP
                    //_tarjetasSIP.value = tarjetaSIP
                } else {
                    Log.e("TarjetaSIPViewModel", "error al eliminar la tarjeta sip")
                }
            } catch (e: Exception) {
                Log.e("TarjetaSIPViewModel", "Error al eliminar la tarjeta sip: ${e.message}")
            }
        }
    }



    // Dialógo para crear nueva Tarjeta SIP
    fun mostrarDialogoCrearTarjetaSIP(usuario: String, tarjetaSIP: Tarjeta.TarjetaSIP? = null) {
        Log.d("UsuarioViewModel", "en el metodo de mostrar Dialogo crear tarjeta SIP")

        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialogo_crear_tarjeta_sip, null)

        val editTextApellidosNombre = dialogView.findViewById<EditText>(R.id.tvApellidosNombreUsuario)
        val editTextNumeroSip = dialogView.findViewById<EditText>(R.id.tvNumeroSip)
        val editTextDigitoControl = dialogView.findViewById<EditText>(R.id.tvDigitoControl)
        val editTextCodigoIdentificacionTerritorial = dialogView.findViewById<EditText>(R.id.tvCodigoIdentificacionTerritorial)
        val editTextDatosIdentificacion = dialogView.findViewById<EditText>(R.id.tvDatosIdentificacion)
        val editTextCodigoSns = dialogView.findViewById<EditText>(R.id.tvCodigoSns)
        val editTextFechaEmision = dialogView.findViewById<EditText>(R.id.tvFechaEmisionUsuario)
        val editTextFechaCaducidad = dialogView.findViewById<EditText>(R.id.editTextFechaCaducidad)
        val editTextTelefonoUrgencias = dialogView.findViewById<EditText>(R.id.tvTelefonoUrgencias)
        val editTextNumeroSeguridadSocial = dialogView.findViewById<EditText>(R.id.tvNumeroSeguridadSocial)
        val editTextCentroMedico = dialogView.findViewById<EditText>(R.id.tvCentroMedico)
        val editTextMedicoAsignado = dialogView.findViewById<EditText>(R.id.tvMedicoAsignadoUsuario)
        val editTextEnfermeraAsignada = dialogView.findViewById<EditText>(R.id.tvEnfermeraAsignadaUsuario)
        val editTextTelefonosUrgenciasCitaPrevia = dialogView.findViewById<EditText>(R.id.tvTelefonosUrgenciasCitaPrevia)

        // Formato a mostrar al usuario
        val formatoMostrar = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

        // Click Listeners para mostrar las fechas en formato deseado
        editTextFechaEmision.setOnClickListener {
            mostrarDatePickerDialog(editTextFechaEmision, formatoMostrar)
        }

        editTextFechaCaducidad.setOnClickListener {
            mostrarDatePickerDialog(editTextFechaCaducidad, formatoMostrar)
        }

        val alertDialog = AlertDialog.Builder(context
        )
            .setView(dialogView)
            // Al pulsar el boton de crear se comprueban las fechas introducidas y se crea la tarjeta SIP
            .setPositiveButton("Crear") { dialog, _ ->
                val apellidosNombre = editTextApellidosNombre.text.toString().uppercase()
                val numeroSip = editTextNumeroSip.text.toString()
                val digitoControl = editTextDigitoControl.text.toString().uppercase()
                val codigoIdentificacionTerritorial = editTextCodigoIdentificacionTerritorial.text.toString()
                val datosIdentificacion = editTextDatosIdentificacion.text.toString()
                val codigoSns = editTextCodigoSns.text.toString()
                val fechaEmisionStr = editTextFechaEmision.text.toString()
                val fechaCaducidadStr = editTextFechaCaducidad.text.toString()
                val telefonoUrgencias = editTextTelefonoUrgencias.text.toString()
                val numeroSeguridadSocial = editTextNumeroSeguridadSocial.text.toString()
                val centroMedico = editTextCentroMedico.text.toString().uppercase()
                val medicoAsignado = editTextMedicoAsignado.text.toString().uppercase()
                val enfermeraAsignada = editTextEnfermeraAsignada.text.toString().uppercase()
                val telefonosUrgenciasCitaPrevia = editTextTelefonosUrgenciasCitaPrevia.text.toString()

                // Formato a enviar a
                val formatoInput = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

                val fechaEmision: Date? = formatoInput.parse((fechaEmisionStr))
                val fechaCaducidad: Date? = formatoInput.parse((fechaCaducidadStr))

                if (fechaEmision != null && fechaCaducidad != null) {
                    // Si se han ingresado fechas válidas sigue con la creación de la tarjeta SIP
                    viewModelScope.launch {
                        try {
                            crearTarjetaSIP(
                                usuario,
                                apellidosNombre,
                                numeroSip,
                                digitoControl,
                                codigoIdentificacionTerritorial,
                                datosIdentificacion,
                                codigoSns,
                                fechaEmision,
                                fechaCaducidad,
                                telefonoUrgencias,
                                numeroSeguridadSocial,
                                centroMedico,
                                medicoAsignado,
                                enfermeraAsignada,
                                telefonosUrgenciasCitaPrevia
                            )
                        } catch (e: Exception) {
                            _error.postValue("Error al crear la tarjeta SIP: ${e.message}")
                        }
                    }
                } else {
                    // Muestra error si las fechas ingresadas no son válidas
                    _error.postValue("Por favor, ingrese fechas válidas en formato dd/MM/yyyy.")
                }
                dialog.dismiss()
            }

            .setNegativeButton("Cancelar") { dialog, _ ->
                dialog.dismiss()
            }
            .create()

        alertDialog.show()
    }

    suspend fun crearTarjetaSIP(
        usuario: String,
        apellidosNomnre: String,
        numeroSip: String,
        digitoControl: String,
        codigoIdentificacionTerritorial: String,
        datosIdentificacion: String,
        codigoSns: String,
        fechaEmision: Date,
        fechaCaducidad: Date,
        telefonoUrgencias: String,
        numeroSeguridadSocial: String,
        centroMedico: String,
        medicoAsignado: String,
        enfermeraAsignada: String,
        telefonosUrgenciasCitaPrevia: String
    ) {
        try {
            // Crea nueva tarjeta SIP con los datos proporcionados por el usuario
            val nuevaTarjeta = Tarjeta.TarjetaSIP (
                idUsuario = usuario,
                apellidosNombre = apellidosNomnre,
                numeroSip = numeroSip,
                digitoControl = digitoControl,
                codigoIdentificacionTerritorial = codigoIdentificacionTerritorial,
                datosIdentificacion = datosIdentificacion,
                codigoSns = codigoSns,
                fechaEmision = fechaEmision,
                fechaCaducidad = fechaCaducidad,
                telefonoUrgencias = telefonoUrgencias,
                numeroSeguridadSocial = numeroSeguridadSocial,
                centroMedico = centroMedico,
                medicoAsignado = medicoAsignado,
                enfermeraAsignada = enfermeraAsignada,
                telefonosUrgenciasCitaPrevia = telefonosUrgenciasCitaPrevia)

            // Llama al método en el repositorio para insertar la nueva tarjeta DNI del usuario
            crearTarjetaSIPDesdeObjeto(nuevaTarjeta)
        } catch (e: ParseException) {
            _error.postValue("Error al parsear la fecha: ${e.message}" + fechaEmision + fechaCaducidad)
        }
    }

    // Muestra un diálogo para las fechas
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

    // Método para insertar la nueva tarjeta SIP del usuario en el repositorio
    suspend fun crearTarjetaSIPDesdeObjeto(nuevaTarjeta: Tarjeta.TarjetaSIP) {
        _isLoading.postValue(true)
        try {
            val response = tarjetasRepository.insertarTarjetaSIP(nuevaTarjeta)
            if (response != null && response.isSuccessful) {
                cargarTarjetasUsuario(nuevaTarjeta.idUsuario)
                Log.i("TAG", "Se ha creado la tarjeta SIP correctamente.")
            } else {
                _error.postValue("Error al crear la tarjeta SIP")
                Log.d("Error UsuarioViewModel", "Error al crear la tarjeta SIP")
            }
        } catch (e: Exception) {
            _error.postValue("Error al crear la tarjeta SIP: ${e.message}")
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
}