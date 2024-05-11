package com.ieschabas.pmdm.walletapp.ui.tarjetaSIP

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.net.ParseException
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.ieschabas.pmdm.walletapp.R
import com.ieschabas.pmdm.walletapp.data.TarjetasApi
import com.ieschabas.pmdm.walletapp.data.TarjetasRepository
import com.ieschabas.pmdm.walletapp.databinding.FragmentTarjetaSipBinding
import com.ieschabas.pmdm.walletapp.model.tarjetas.Tarjeta
import com.ieschabas.pmdm.walletapp.ui.tarjetaDNI.TarjetaDNIViewModel
import com.ieschabas.pmdm.walletapp.ui.tarjetaDNI.TarjetaDNIViewModelFactory
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.GregorianCalendar
import java.util.Locale

class TarjetaSIPFragment(private val repository: TarjetasRepository) : Fragment() {
    constructor() : this(TarjetasRepository(TarjetasApi()))

    private lateinit var viewModel: TarjetaSIPViewModel
    private var tarjetaSIP: Tarjeta.TarjetaSIP? = null

    private var _binding: FragmentTarjetaSipBinding? = null
    private val binding get() = _binding!!

    private val usuarioId = FirebaseAuth.getInstance().currentUser?.uid

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTarjetaSipBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val viewModelFactory = TarjetaSIPViewModelFactory(requireContext(), repository)
        viewModel = ViewModelProvider(this, viewModelFactory)[TarjetaSIPViewModel::class.java]

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Lanzar una coroutine para obtener la tarjeta SIP
        lifecycleScope.launch {
            // Inicializar tarjetaSIP dentro de la coroutine
            tarjetaSIP = obtenerTarjetaSIP()

            tarjetaSIP?.let { cargarTarjetaSIPUsuario(it) }

            // Configurar clic en la tarjeta para modificar
            binding.root.setOnClickListener {
                mostrarDialogoModificar(tarjetaSIP!!)
            }

            // Configurar clic largo en la tarjeta para eliminar
            binding.root.setOnLongClickListener {
                mostrarDialogoEliminar(tarjetaSIP!!)
                true
            }
            // Boton modificar tarjeta SIP
            binding.modificarButton.setOnClickListener {
                tarjetaSIP?.let {
                    mostrarDialogoModificar(tarjetaSIP!!)
                }
            }

            // Boton eliminar tarjeta SIP
            binding.eliminarButton.setOnClickListener {
                tarjetaSIP?.let {
                    mostrarDialogoEliminar(it)
                }
            }
        }
    }

    private suspend fun obtenerTarjetaSIP(): Tarjeta.TarjetaSIP? {
        // Verificar si el usuario está autenticado
        usuarioId?.let {
            try {
                // Obtener la tarjeta DNI del repositorio
                val tarjetasSIP = repository.obtenerTarjetaSIPUsuario(it)
                // Si se obtiene al menos una tarjeta DNI, tomar la primera (asumiendo que solo hay una por usuario)
                if (tarjetasSIP.isNotEmpty() && tarjetasSIP[0].numeroSip.isNotEmpty()) {
                    return tarjetasSIP[0]
                } else {
                    // Si no se encuentra ninguna tarjeta DNI válida, devolver una tarjeta DNI vacía o lanzar una excepción según tu lógica
                    return null
                }
            } catch (e: Exception) {
                Log.e("TarjetaDNIFragment", "Error al obtener la tarjeta DNI del usuario: ${e.message}")
                // Lanzar una excepción si ocurre un error al obtener la tarjeta DNI
                throw e
            }
        }
        // Si el usuario no está autenticado, puedes devolver una tarjeta DNI vacía o lanzar una excepción según tu lógica
        return null
    }

    private fun cargarTarjetaSIPUsuario(tarjetaSIP: Tarjeta.TarjetaSIP) {
        val usuarioId = FirebaseAuth.getInstance().currentUser?.uid
        usuarioId?.let {
            Log.d("TarjetaSIPFragment", "ID de usuario: $it")
            viewModel.viewModelScope.launch {
                val tarjetasSIP = viewModel.cargarTarjetaSIPUsuario(it)
                // Verifica si se obtuvo una tarjeta SIP válida
                tarjetasSIP.let {
                    _binding?.tvNumeroSip?.text = getString(
                        R.string.numero_sip_template,
                        tarjetaSIP.numeroSip
                    )
                    _binding?.tvDigitoControl?.text = getString(
                        R.string.digito_template,
                        tarjetaSIP.digitoControl
                    )
                    _binding?.tvCodigoIdentificacionTerritorial?.text = getString(
                        R.string.codigo_identificacion_territorial_template,
                        tarjetaSIP.codigoIdentificacionTerritorial
                    )
                    _binding?.tvDatosIdentificacion?.text = getString(
                        R.string.datos_identificacion_template,
                        tarjetaSIP.datosIdentificacion
                    )
                    _binding?.tvCodigoSns?.text = getString(
                        R.string.codigo_sns_template,
                        tarjetaSIP.codigoSns
                    )
                    _binding?.tvFechaEmisionUsuario?.text = formatDate(tarjetaSIP.fechaEmision)
                    _binding?.tvFechaCaducidadUsuario?.text = formatDate(tarjetaSIP.fechaCaducidad)
                    _binding?.tvTelefonoUrgencias?.text = getString(
                        R.string.telefono_urgencias_template,
                        tarjetaSIP.telefonoUrgencias
                    )
                    _binding?.tvNumeroSeguridadSocial?.text = getString(
                        R.string.numero_seguridad_social_template,
                        tarjetaSIP.numeroSeguridadSocial
                    )
                    _binding?.tvCentroMedico?.text = getString(
                        R.string.centro_medico_template,
                        tarjetaSIP.centroMedico
                    )
                    _binding?.tvMedicoAsignadoUsuario?.text = getString(
                        R.string.medico_asignado_template,
                        tarjetaSIP.medicoAsignado
                    )
                    _binding?.tvEnfermeraAsignadaUsuario?.text = getString(
                        R.string.enfermera_asignada_template,
                        tarjetaSIP.enfermeraAsignada
                    )
                    _binding?.tvTelefonosUrgenciasCitaPrevia?.text = getString(
                        R.string.telefono_urgencias_cita_previa_template,
                        tarjetaSIP.telefonosUrgenciasCitaPrevia
                    )
                    _binding?.tvApellidosNombreUsuario?.text = getString(
                        R.string.apellidos_nombre_template,
                        tarjetaSIP.apellidosNombre
                    )
                }
            }
        }
    }

    // Declarar una variable para almacenar la fecha seleccionada por el usuario
    private var fechaEmisionSeleccionada: Date? = Date()
    private var fechaCaducidadSeleccionada: Date? = Date()

    private fun mostrarDialogoModificar(tarjetaSIP: Tarjeta.TarjetaSIP) {
        val builder = AlertDialog.Builder(requireContext())
        val inflater = requireActivity().layoutInflater
        val dialogLayout = inflater.inflate(R.layout.dialogo_crear_tarjeta_sip, null)

        val etNumeroSip = dialogLayout.findViewById<EditText>(R.id.tvNumeroSip)
        val etDigitoControl = dialogLayout.findViewById<EditText>(R.id.tvDigitoControl)
        val etCodigoIdentificacionTerritorial = dialogLayout.findViewById<EditText>(R.id.tvCodigoIdentificacionTerritorial)
        val etDatosIdentificacion = dialogLayout.findViewById<EditText>(R.id.tvDatosIdentificacion)
        val etCodigoSns = dialogLayout.findViewById<EditText>(R.id.tvCodigoSns)
        val etFechaEmision = dialogLayout.findViewById<EditText>(R.id.tvFechaEmisionUsuario)
        val etFechaCaducidad = dialogLayout.findViewById<EditText>(R.id.editTextFechaCaducidad)
        val etTelefonoUrgencias = dialogLayout.findViewById<EditText>(R.id.tvTelefonoUrgencias)
        val etNumeroSeguridadSocial = dialogLayout.findViewById<EditText>(R.id.tvNumeroSeguridadSocial)
        val etCentroMedico = dialogLayout.findViewById<EditText>(R.id.tvCentroMedico)
        val etMedicoAsignado = dialogLayout.findViewById<EditText>(R.id.tvMedicoAsignadoUsuario)
        val etEnfermeraAsignada = dialogLayout.findViewById<EditText>(R.id.tvEnfermeraAsignadaUsuario)
        val etTelefonosUrgenciasCitaPrevia = dialogLayout.findViewById<EditText>(R.id.tvTelefonosUrgenciasCitaPrevia)
        val etApellidosNombre = dialogLayout.findViewById<EditText>(R.id.tvApellidosNombreUsuario)

        val formatoMostrar = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val formatoEnviar = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val formatoParser = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

        etFechaEmision.setOnClickListener {
            mostrarDatePickerDialog(etFechaEmision, formatoMostrar, "emision")
        }

        etFechaCaducidad.setOnClickListener {
            mostrarDatePickerDialog(etFechaCaducidad, formatoMostrar, "caducidad")
        }

        etNumeroSip.setText(tarjetaSIP.numeroSip)
        etDigitoControl.setText(tarjetaSIP.digitoControl)
        etCodigoIdentificacionTerritorial.setText(tarjetaSIP.codigoIdentificacionTerritorial)
        etDatosIdentificacion.setText(tarjetaSIP.datosIdentificacion)
        etCodigoSns.setText(tarjetaSIP.codigoSns)
        etFechaEmision.setText(formatDate(tarjetaSIP.fechaEmision))
        etFechaCaducidad.setText(formatDate(tarjetaSIP.fechaCaducidad))
        etTelefonoUrgencias.setText(tarjetaSIP.telefonoUrgencias)
        etNumeroSeguridadSocial.setText(tarjetaSIP.numeroSeguridadSocial)
        etCentroMedico.setText(tarjetaSIP.centroMedico)
        etMedicoAsignado.setText(tarjetaSIP.medicoAsignado)
        etEnfermeraAsignada.setText(tarjetaSIP.enfermeraAsignada)
        etTelefonosUrgenciasCitaPrevia.setText(tarjetaSIP.telefonosUrgenciasCitaPrevia)
        etApellidosNombre.setText(tarjetaSIP.apellidosNombre)

        builder.setView(dialogLayout)
            .setPositiveButton("Guardar") { _, _ ->

                val nuevoNumeroSip = etNumeroSip.text.toString()
                val nuevoDigitoControl = etDigitoControl.text.toString()
                val nuevoCodigoIdentificacionTerritorial = etCodigoIdentificacionTerritorial.text.toString()
                val nuevosDatosIdentificacion = etDatosIdentificacion.text.toString()
                val nuevoCodigoSns = etCodigoSns.text.toString()

                val fechaEmisionString = if (fechaEmisionSeleccionada != null) formatoEnviar.format(fechaEmisionSeleccionada!!) else ""
                val fechaCaducidadString = if (fechaCaducidadSeleccionada != null) formatoEnviar.format(fechaCaducidadSeleccionada!!) else ""
                Log.d("TAG", "fechaEmisionString: $fechaEmisionString")
                Log.d("TAG", "fechaCaducidadString: $fechaCaducidadString")

                val nuevoTelefonoUrgencias = etTelefonoUrgencias.text.toString()
                val nuevoNumeroSeguridadSocial = etNumeroSeguridadSocial.text.toString()
                val nuevoCentroMedico = etCentroMedico.text.toString()
                val nuevoMedicoAsignado = etMedicoAsignado.text.toString()
                val nuevaEnfermeraAsignada = etEnfermeraAsignada.text.toString()
                val nuevosTelefonosUrgenciasCitaPrevia = etTelefonosUrgenciasCitaPrevia.text.toString()
                val nuevosApellidosNombre = etApellidosNombre.text.toString()

                tarjetaSIP.id.let { id ->
                    try {
                        // Convert the String values back to Date objects
                        val fechaEmision = if (fechaEmisionString.isNotEmpty()) {
                            try {
                                formatoParser.parse(fechaEmisionString)
                            } catch (e: ParseException) {Log.e("Error", "Error al parsear la fecha de emisión: ${e.message}")
                                null
                            }
                        } else {
                            null
                        }

                        val fechaCaducidad = if (fechaCaducidadString.isNotEmpty()) {
                            try {
                                formatoParser.parse(fechaCaducidadString)
                            } catch (e: ParseException) {
                                Log.e("Error", "Error al parsear la fecha de caducidad: ${e.message}")
                                null
                            }
                        } else {
                            null
                        }
                        // Create a new TarjetaSIP object with the updated values
                        val nuevaTarjetaSIP = Tarjeta.TarjetaSIP(
                            id = tarjetaSIP.id,
                            idUsuario = tarjetaSIP.idUsuario,
                            numeroSip = nuevoNumeroSip,
                            digitoControl = nuevoDigitoControl,
                            codigoIdentificacionTerritorial = nuevoCodigoIdentificacionTerritorial,
                            datosIdentificacion = nuevosDatosIdentificacion,
                            codigoSns = nuevoCodigoSns,
                            fechaEmision = fechaEmision!!,
                            fechaCaducidad = fechaCaducidad!!,
                            telefonoUrgencias = nuevoTelefonoUrgencias,
                            numeroSeguridadSocial = nuevoNumeroSeguridadSocial,
                            centroMedico = nuevoCentroMedico,
                            medicoAsignado = nuevoMedicoAsignado,
                            enfermeraAsignada = nuevaEnfermeraAsignada,
                            telefonosUrgenciasCitaPrevia = nuevosTelefonosUrgenciasCitaPrevia,
                            apellidosNombre = nuevosApellidosNombre
                        )

                        // Modify the tarjetaSIP in the database with the updated values
                        viewModel.modificarTarjetaSIP(id, nuevaTarjetaSIP)
                        Log.d("TarjetaSIPFragment", "Tarjeta SIP modificada correctamente")

                    } catch (e: Exception) {
                        Log.e("Error", "No se pudo parsear una o ambas fechas")
                    }
                }
            }
            .setNegativeButton("Cancelar") { _, _ ->
                // Dismiss the dialog when the user clicks on the Cancel button
            }

        val dialog = builder.create()
        dialog.show()
    }

    private fun formatDateForServer(date: Date?): String {
        return if (date != null) {
            SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(date)
        } else {
            ""
        }
    }

    private fun formatDate(date: Date?): String {
        if (date == null) {
            return ""
        }

        return SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(date)
    }

    // Declarar una variable para almacenar la fecha seleccionada por el usuario
    private var fechaSeleccionada: Date? = Date()

    private val cal = Calendar.getInstance()

    // En la función mostrarDatePickerDialog, actualiza la fecha según el tipo de fecha
    private fun mostrarDatePickerDialog(textView: EditText, formatoMostrar: SimpleDateFormat, tipo: String) {
        val datePickerDialog = context?.let {
            DatePickerDialog(
                it,
                { _, year, month, dayOfMonth ->
                    val fechaSeleccionada = GregorianCalendar(year, month, dayOfMonth).time

                    if (tipo == "emision") {
                        fechaEmisionSeleccionada = fechaSeleccionada
                    } else if (tipo == "caducidad") {
                        fechaCaducidadSeleccionada = fechaSeleccionada
                    }

                    val dateString = formatoMostrar.format(fechaSeleccionada)
                    textView.setText(dateString)
                },
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)
            )
        }
        datePickerDialog!!.show()

        // Add error handling to log an error message if fechaEmisionSeleccionada is null
        if (fechaEmisionSeleccionada == null) {
            Log.e("Error", "fechaEmisionSeleccionada is null")
        }
    }

    private fun mostrarDialogoEliminar(tarjetaSIP: Tarjeta.TarjetaSIP) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Eliminar Tarjeta")
        builder.setMessage("¿Estás seguro de que deseas eliminar la Tarjeta SIP?")
        builder.setPositiveButton("Sí") { dialog, _ ->
            // Lógica para eliminar la tarjeta
            eliminarTarjeta(tarjetaSIP)
            dialog.dismiss()
        }
        builder.setNegativeButton("No") { dialog, _ ->
            dialog.dismiss()
        }
        val dialog = builder.create()
        dialog.show()
    }

    private fun eliminarTarjeta(tarjetaSIP: Tarjeta.TarjetaSIP) {
        viewModel.eliminarTarjetaSIP(tarjetaSIP)
        Toast.makeText(requireContext(), "TarjetaSIPFragment DNI en metodo de eliminar.", Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
