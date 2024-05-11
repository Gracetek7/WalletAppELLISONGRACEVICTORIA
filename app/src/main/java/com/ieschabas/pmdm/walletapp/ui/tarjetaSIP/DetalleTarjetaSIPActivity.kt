package com.ieschabas.pmdm.walletapp.ui.tarjetaSIP

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.ieschabas.pmdm.walletapp.databinding.ActivityDetalleTarjetaSipBinding
import com.ieschabas.pmdm.walletapp.databinding.ItemTarjetaSipBinding
import com.ieschabas.pmdm.walletapp.model.tarjetas.Tarjeta
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale



//class DetalleTarjetaSIPActivity : AppCompatActivity() {
//    private lateinit var tarjetaViewModel: TarjetaSIPViewModel
//    private val binding: Ac
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        binding = ActivityDetalleTarjetaSipBinding.inflate(layoutInflater)
//        setContentView(binding.root)
//
//        // Inicializa el ViewModel
//        tarjetaViewModel = ViewModelProvider(this)[TarjetaSIPViewModel::class.java]
//
//        // Observa la tarjeta seleccionada y actualiza la interfaz de usuario en consecuencia
//        tarjetaViewModel.tarjetaSeleccionada.observe(this) { tarjeta ->
//            tarjeta?.let {
//                bind(it)
//            }
//        }
//    }
//}
//
//    private fun bind(tarjetaSIP: Tarjeta.TarjetaSIP) {
//        // Aquí realizas la asignación de los valores de la tarjeta SIP a las vistas
//
//        val tvApellidosNombre: TextView = binding.tvApellidosNombreUsuario
//        val tvNumeroSip: TextView = binding.tvNumeroSip
//        val tvDigitoControl: TextView = binding.tvDigitoControl
//        val tvCodigoIdentificacionTerritorial: TextView = binding.tvCodigoIdentificacionTerritorial
//        val tvDatosIdentificacion: TextView = binding.tvDatosIdentificacion
//        val tvCodigoSns: TextView = binding.tvCodigoSns
//        val tvFechaEmision: TextView = binding.tvFechaEmisionUsuario
//        val tvFechaCaducidad: TextView = binding.tvFechaEmisionUsuario
//        val tvTelefonoUrgencias: TextView = binding.tvTelefonoUrgencias
//        val tvNumeroSeguridadSocial: TextView = binding.tvNumeroSeguridadSocial
//        val tvCentroMedico: TextView = binding.tvCentroMedico
//        val tvMedicoAsignado: TextView = binding.tvMedicoAsignadoUsuario
//        val tvEnfermeraAsignada: TextView = binding.tvEnfermeraAsignadaUsuario
//        val tvTelefonosUrgenciasCitaPrevia: TextView = binding.tvTelefonosUrgenciasCitaPrevia
//
//        // Asigna los valores de la tarjeta SIP a las vistas
//        tvApellidosNombre.text = tarjetaSIP.apellidosNombre
//        tvNumeroSip.text = tarjetaSIP.numeroSip
//        tvDigitoControl.text = tarjetaSIP.digitoControl
//        tvCodigoIdentificacionTerritorial.text = tarjetaSIP.codigoIdentificacionTerritorial
//        tvDatosIdentificacion.text = tarjetaSIP.datosIdentificacion
//        tvCodigoSns.text = tarjetaSIP.codigoSns
//        tvTelefonoUrgencias.text = tarjetaSIP.telefonoUrgencias
//        tvNumeroSeguridadSocial.text = tarjetaSIP.numeroSeguridadSocial
//        tvCentroMedico.text = tarjetaSIP.centroMedico
//        tvMedicoAsignado.text = tarjetaSIP.medicoAsignado
//        tvEnfermeraAsignada.text = tarjetaSIP.enfermeraAsignada
//        tvTelefonosUrgenciasCitaPrevia.text = tarjetaSIP.telefonosUrgenciasCitaPrevia
//
//        // Parsea las fechas
//        val fechaEmision = tarjetaSIP.fechaEmision
//        val fechaCaducidad = tarjetaSIP.fechaCaducidad
//
//        // Formatea las fechas
//        tvFechaEmision.text = formatDate(fechaEmision)
//        tvFechaCaducidad.text = formatDate(fechaCaducidad)
//    }
//
//    private fun formatDate(date: Date?): String {
//        return date?.let {
//            SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(it)
//        } ?: ""
//    }
//}