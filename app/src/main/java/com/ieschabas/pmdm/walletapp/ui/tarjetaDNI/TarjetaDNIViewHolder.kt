package com.ieschabas.pmdm.walletapp.ui.tarjetaDNI

import android.content.Context
import android.util.Log
import com.ieschabas.pmdm.walletapp.model.tarjetas.Tarjeta
import androidx.recyclerview.widget.RecyclerView
import com.ieschabas.pmdm.walletapp.R
import com.ieschabas.pmdm.walletapp.adapter.TarjetasAdapter
import com.ieschabas.pmdm.walletapp.databinding.ItemTarjetaDniBinding
import com.squareup.picasso.Picasso
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

// Mostrar tarjeta DNI en recyclerView de usuario fragment
class TarjetaDNViewHolder(private val binding: ItemTarjetaDniBinding, private val listener: TarjetasAdapter.OnTarjetaClickListener) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(tarjetaDNI: Tarjeta.TarjetaDNI, context: Context) {
        val resources = context.resources

        binding.tvNumeroDocumento.text = resources.getString(R.string.dni_template, resources.getString(R.string.dni), tarjetaDNI.numeroDocumento)
        binding.tvNombre.text = resources.getString(R.string.nombre_template, resources.getString(R.string.nombre), tarjetaDNI.nombre)
        binding.tvApellidos.text = resources.getString(R.string.apellidos_template, resources.getString(R.string.apellidos), tarjetaDNI.apellidos)
        binding.tvSexo.text = resources.getString(R.string.sexo_template, resources.getString(R.string.sexo), tarjetaDNI.sexo)
        binding.tvNacionalidad.text = resources.getString(R.string.nacionalidad_template, resources.getString(R.string.nacionalidad), tarjetaDNI.nacionalidad)
        binding.tvFechaNacimiento.text = formatDate(tarjetaDNI.fechaNacimiento)
        binding.tvFechaExpedicion.text = formatDate(tarjetaDNI.fechaExpedicion)
        binding.tvFechaCaducidad.text = formatDate(tarjetaDNI.fechaCaducidad)
        binding.tvLugarNacimiento.text = resources.getString(R.string.lugar_nacimiento_template, resources.getString(R.string.lugar_de_nacimiento), tarjetaDNI.lugarNacimiento)
        binding.tvDomicilio.text = resources.getString(R.string.domicilio_template, resources.getString(R.string.domicilio), tarjetaDNI.domicilio)

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

    private fun formatDate(date: Date?): String {
        if (date == null) {
            return ""
        }

        // Define el patrón de formato de fecha adecuado para la fecha recibida
        val dateFormat = SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy", Locale.getDefault())

        // Establece la zona horaria GMT para asegurar la consistencia en el formato
        dateFormat.timeZone = TimeZone.getTimeZone("GMT")

        // Formatea la fecha
        return dateFormat.format(date)
    }
}