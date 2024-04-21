package com.ieschabas.pmdm.walletapp.ui.tarjetaSIP

import android.util.Log
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ieschabas.pmdm.walletapp.R
import com.ieschabas.pmdm.walletapp.databinding.ItemTarjetaSipBinding
import com.ieschabas.pmdm.walletapp.model.tarjetas.Tarjeta
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class TarjetaSIPViewHolder(private val binding: ItemTarjetaSipBinding) : RecyclerView.ViewHolder(binding.root) {

    private val tvApellidosNombre: TextView = itemView.findViewById(R.id.tvApellidosNombreUsuario)
    private val tvNumeroSip: TextView = itemView.findViewById(R.id.tvNumeroSip)
    private val tvDigitoControl: TextView = itemView.findViewById(R.id.tvDigitoControl)
    private val tvCodigoIdentificacionTerritorial: TextView = itemView.findViewById(R.id.tvCodigoIdentificacionTerritorial)
    private val tvDatosIdentificacion: TextView = itemView.findViewById(R.id.tvDatosIdentificacion)
    private val tvCodigoSns: TextView = itemView.findViewById(R.id.tvCodigoSns)
    private val tvFechaEmision: TextView = itemView.findViewById(R.id.tvFechaEmisionUsuario)
    private val tvFechaCaducidad: TextView = itemView.findViewById(R.id.tvFechaCaducidadUsuario)
    private val tvTelefonoUrgencias: TextView = itemView.findViewById(R.id.tvTelefonoUrgencias)
    private val tvNumeroSeguridadSocial: TextView = itemView.findViewById(R.id.tvNumeroSeguridadSocial)
    private val tvCentroMedico: TextView = itemView.findViewById(R.id.tvCentroMedico)
    private val tvMedicoAsignado: TextView = itemView.findViewById(R.id.tvMedicoAsignadoUsuario)
    private val tvEnfermeraAsignada: TextView = itemView.findViewById(R.id.tvEnfermeraAsignadaUsuario)
    private val tvTelefonosUrgenciasCitaPrevia: TextView = itemView.findViewById(R.id.tvTelefonosUrgenciasCitaPrevia)


    fun bind(tarjetaSIP: Tarjeta.TarjetaSIP) {
        Log.d("TarjetaDNViewHolder", "Bind Tarjeta SIP: $tarjetaSIP")
        // Asigna los valores de la tarjeta de DNI a las vistas
        tvApellidosNombre.text = tarjetaSIP.apellidosNombre
        tvNumeroSip.text = tarjetaSIP.numeroSip
        tvDigitoControl.text = tarjetaSIP.digitoControl
        tvCodigoIdentificacionTerritorial.text = tarjetaSIP.codigoIdentificacionTerritorial
        tvDatosIdentificacion.text = tarjetaSIP.datosIdentificacion
        tvCodigoSns.text = tarjetaSIP.codigoSns
        tvTelefonoUrgencias.text = tarjetaSIP.telefonoUrgencias
        tvNumeroSeguridadSocial.text = tarjetaSIP.numeroSeguridadSocial
        tvCentroMedico.text = tarjetaSIP.centroMedico
        tvMedicoAsignado.text = tarjetaSIP.medicoAsignado
        tvEnfermeraAsignada.text = tarjetaSIP.enfermeraAsignada
        tvTelefonosUrgenciasCitaPrevia.text = tarjetaSIP.telefonosUrgenciasCitaPrevia


        // Parsea las fechas
        val fechaEmision = parseDate(tarjetaSIP.fechaEmision)
        val fechaCaducidad = parseDate(tarjetaSIP.fechaCaducidad)

        // Formatea las fechas
        tvFechaEmision.text = formatDate(fechaEmision)
        tvFechaCaducidad.text = formatDate(fechaCaducidad)

    }

    private fun parseDate(dateString: Date): Date? {
        return SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault()).parse(
            dateString.toString()
        )
    }

    private fun formatDate(date: Date?): String {
        if (date == null) {
            return ""
        }

        return SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(date)
    }
}
