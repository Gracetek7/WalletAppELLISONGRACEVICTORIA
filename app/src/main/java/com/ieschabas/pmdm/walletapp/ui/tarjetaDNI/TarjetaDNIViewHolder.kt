package com.ieschabas.pmdm.walletapp.ui.tarjetaDNI

import android.util.Log
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.ieschabas.pmdm.walletapp.model.tarjetas.Tarjeta
import java.text.SimpleDateFormat
import java.util.*
import android.widget.ImageView
import android.widget.TextView
import com.ieschabas.pmdm.walletapp.R
import com.squareup.picasso.Picasso
class TarjetaDNViewHolder(itemView: View, private val listener: OnTarjetaClickListener) : RecyclerView.ViewHolder(itemView) {

    private val ivFotografia: ImageView = itemView.findViewById(R.id.ivFotografia)
    private val tvNumeroDocumento: TextView = itemView.findViewById(R.id.tvNumeroDocumento)
    private val tvNombre: TextView = itemView.findViewById(R.id.tvNombre)
    private val tvApellidos: TextView = itemView.findViewById(R.id.tvApellidos)
    private val tvSexo: TextView = itemView.findViewById(R.id.tvSexo)
    private val tvNacionalidad: TextView = itemView.findViewById(R.id.tvNacionalidad)
    private val tvFechaNacimiento: TextView = itemView.findViewById(R.id.tvFechaNacimiento)
    private val tvFechaExpedicion: TextView = itemView.findViewById(R.id.tvFechaExpedicion)
    private val tvFechaCaducidad: TextView = itemView.findViewById(R.id.tvFechaCaducidad)
    private val tvLugarNacimiento: TextView = itemView.findViewById(R.id.tvLugarNacimiento)
    private val tvDomicilio: TextView = itemView.findViewById(R.id.tvDomicilio)
    private val ivFirma: ImageView = itemView.findViewById(R.id.ivFirma)

    fun bind(tarjetaDNI: Tarjeta.TarjetaDNI) {
        Log.d("TarjetaDNViewHolder", "Bind Tarjeta DNI: $tarjetaDNI")
        // Asigna los valores de la tarjeta de DNI a las vistas
        tvNumeroDocumento.text = tarjetaDNI.numeroDocumento
        tvNombre.text = tarjetaDNI.nombre
        tvApellidos.text = tarjetaDNI.apellidos
        tvSexo.text = tarjetaDNI.sexo.toString()
        tvNacionalidad.text = tarjetaDNI.nacionalidad
        tvLugarNacimiento.text = tarjetaDNI.lugarNacimiento
        tvDomicilio.text = tarjetaDNI.domicilio

        // Parsea las fechas
        val fechaNacimiento = parseDate(tarjetaDNI.fechaNacimiento)
        val fechaExpedicion = parseDate(tarjetaDNI.fechaExpedicion)
        val fechaCaducidad = parseDate(tarjetaDNI.fechaCaducidad)

        // Formatea las fechas
        tvFechaNacimiento.text = formatDate(fechaNacimiento)
        tvFechaExpedicion.text = formatDate(fechaExpedicion)
        tvFechaCaducidad.text = formatDate(fechaCaducidad)

        // Carga las imágenes
        Picasso.get().load(tarjetaDNI.fotografiaUrl).into(ivFotografia)
        Picasso.get().load(tarjetaDNI.firmaUrl).into(ivFirma)

        // Configuración de clics
        itemView.setOnClickListener {
            listener.onTarjetaClick(tarjetaDNI)
        }

        itemView.setOnLongClickListener {
            listener.onTarjetaLongClick(tarjetaDNI, bindingAdapterPosition)
            true // Indicamos que el evento ha sido manejado correctamente
        }
    }

    private fun parseDate(dateString: Date): Date? {
        return SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.getDefault()).parse(dateString.toString())
    }


    private fun formatDate(date: Date?): String {
        if (date == null) {
            return ""
        }

        return SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(date)
    }

    interface OnTarjetaClickListener {
        fun onTarjetaClick(tarjeta: Tarjeta.TarjetaDNI)
        fun onTarjetaLongClick(tarjeta: Tarjeta.TarjetaDNI, position: Int)
    }

}
