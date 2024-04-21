package com.ieschabas.pmdm.walletapp.ui.tarjetaSIP

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
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
import com.ieschabas.pmdm.walletapp.ui.usuario.UsuarioViewModel
import com.ieschabas.pmdm.walletapp.ui.usuario.UsuarioViewModelFactory
import kotlinx.coroutines.launch
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class TarjetaSIPFragment(private val repository: TarjetasRepository) : Fragment() {

    constructor() : this(TarjetasRepository(TarjetasApi()))

    private lateinit var viewModel: TarjetaSIPViewModel
    private lateinit var tarjetaSIP: Tarjeta.TarjetaSIP

    private var _binding: FragmentTarjetaSipBinding? = null
    private val binding get() = _binding!!

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

            cargarTarjetaSIPUsuario(tarjetaSIP)

            // Configurar clic en la tarjeta para modificar
            binding.root.setOnClickListener {
                //abrirFormularioModificacion(tarjeta)
            }

            // Configurar clic largo en la tarjeta para eliminar
            binding.root.setOnLongClickListener {
                mostrarDialogoEliminar(id)
                true
            }
        }
    }

    private suspend fun obtenerTarjetaSIP(): Tarjeta.TarjetaSIP {
        val usuarioId = FirebaseAuth.getInstance().currentUser?.uid

        // Verificar si el usuario está autenticado
        usuarioId?.let {
            try {
                // Obtener la tarjeta SIP del repositorio
                val tarjetasSIP = repository.obtenerTarjetaSIPUsuario(it)
                // Si se obtiene al menos una tarjeta SIP, tomar la primera (asumiendo que solo hay una por usuario)
                if (tarjetasSIP.isNotEmpty()) {
                    return tarjetasSIP[0]
                } else {
                    // Si no se encuentra ninguna tarjeta SIP, devolver una tarjeta SIP vacía o lanzar una excepción según tu lógica
                    return Tarjeta.TarjetaSIP(
                        idUsuario = it,
                        numeroSip = "",
                        digitoControl = "",
                        codigoIdentificacionTerritorial = "",
                        datosIdentificacion = "",
                        codigoSns = "",
                        fechaEmision = Date(),
                        fechaCaducidad = Date(),
                        telefonoUrgencias = "",
                        numeroSeguridadSocial = "",
                        centroMedico = "",
                        medicoAsignado = "",
                        enfermeraAsignada = "",
                        telefonosUrgenciasCitaPrevia = "",
                        apellidosNombre = ""
                    )
                }
            } catch (e: Exception) {
                Log.e("TarjetaSIPFragment", "Error al obtener la tarjeta SIP del usuario: ${e.message}")
                // Lanzar una excepción si ocurre un error al obtener la tarjeta SIP
                throw e
            }
        }

        // Si el usuario no está autenticado, puedes devolver una tarjeta SIP vacía o lanzar una excepción según tu lógica
        return Tarjeta.TarjetaSIP(
            idUsuario = "",
            numeroSip = "",
            digitoControl = "",
            codigoIdentificacionTerritorial = "",
            datosIdentificacion = "",
            codigoSns = "",
            fechaEmision = Date(),
            fechaCaducidad = Date(),
            telefonoUrgencias = "",
            numeroSeguridadSocial = "",
            centroMedico = "",
            medicoAsignado = "",
            enfermeraAsignada = "",
            telefonosUrgenciasCitaPrevia = "",
            apellidosNombre = ""
        )
    }

    private fun cargarTarjetaSIPUsuario(tarjetaSIP: Tarjeta.TarjetaSIP) {
        val usuarioId = FirebaseAuth.getInstance().currentUser?.uid
        usuarioId?.let {
            Log.d("TarjetaSIPFragment", "ID de usuario: $it")
            viewModel.viewModelScope.launch {
                val tarjetasSIP = viewModel.cargarTarjetaSIPUsuario(it)
                // Verifica si se obtuvo una tarjeta SIP válida
                tarjetasSIP.let { tarjeta ->
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

    private fun formatDate(date: Date): String {
        // Define el formato deseado para la fecha
        val targetFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        // Formatea la fecha en el formato deseado
        return targetFormat.format(date)
    }

    private fun mostrarDialogoEliminar(id: Int) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Eliminar Tarjeta")
        builder.setMessage("¿Estás seguro de que deseas eliminar la Tarjeta SIP?")
        builder.setPositiveButton("Sí") { dialog, _ ->
            // Lógica para eliminar la tarjeta
            eliminarTarjeta(id)
            dialog.dismiss()
        }
        builder.setNegativeButton("No") { dialog, _ ->
            dialog.dismiss()
        }
        val dialog = builder.create()
        dialog.show()
    }

    private fun eliminarTarjeta(id: Int) {
        viewModel.eliminarTarjetaSIP(id)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
