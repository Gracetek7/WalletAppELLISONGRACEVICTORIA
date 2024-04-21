package com.ieschabas.pmdm.walletapp.ui.tarjetaDNI

import android.util.Log
import com.ieschabas.pmdm.walletapp.model.tarjetas.Tarjeta
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ieschabas.pmdm.walletapp.databinding.ItemTarjetaDniBinding
import com.squareup.picasso.Picasso
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone


//NO LO TENGO QUE USAR SOLO ES PARA UNA LISTA DE TARJETAS
class TarjetaDNViewHolder(private val binding: ItemTarjetaDniBinding, private val listener: OnTarjetaClickListener) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(tarjetaDNI: Tarjeta.TarjetaDNI) {

        binding.tvNumeroDocumento.text = tarjetaDNI.numeroDocumento
        binding.tvNombre.text = tarjetaDNI.nombre
        binding.tvApellidos.text = tarjetaDNI.apellidos
        binding.tvSexo.text = tarjetaDNI.sexo.toString()
        binding.tvNacionalidad.text = tarjetaDNI.nacionalidad
        binding.tvLugarNacimiento.text = tarjetaDNI.lugarNacimiento
        binding.tvDomicilio.text = tarjetaDNI.domicilio

        // Parsea las fechas
        val fechaNacimiento = parseDate(tarjetaDNI.fechaNacimiento)
        val fechaExpedicion = parseDate(tarjetaDNI.fechaExpedicion)
        val fechaCaducidad = parseDate(tarjetaDNI.fechaCaducidad)

        // Formatea las fechas
        binding.tvFechaNacimiento.text = formatDate(fechaNacimiento)
        binding.tvFechaExpedicion.text = formatDate(fechaExpedicion)
        binding.tvFechaCaducidad.text = formatDate(fechaCaducidad)

        // Load the images using Picasso
        Log.d("TarjetaDNViewHolder", "URL de la fotografía: ${tarjetaDNI.fotografiaUrl}")
        Log.d("TarjetaDNViewHolder", "URL de la firma: ${tarjetaDNI.firmaUrl}")


        Picasso.get().load(tarjetaDNI.fotografiaUrl).into(binding.ivFotografia)
        Picasso.get().load(tarjetaDNI.firmaUrl).into(binding.ivFirma)


        // Configuración de clics
        binding.root.setOnClickListener {
            Log.d("TarjetaDNViewHolder", "Clic en la tarjeta")
            listener.onTarjetaClick(tarjetaDNI)
        }

        binding.root.setOnLongClickListener {
            Log.d("TarjetaDNViewHolder", "Clic largo en la tarjeta")
            listener.onTarjetaLongClick(tarjetaDNI, bindingAdapterPosition)
            true // Indica que el evento ha sido manejado correctamente
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