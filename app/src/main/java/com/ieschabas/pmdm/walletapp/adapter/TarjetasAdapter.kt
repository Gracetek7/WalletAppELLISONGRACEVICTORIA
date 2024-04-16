package com.ieschabas.pmdm.walletapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ieschabas.pmdm.walletapp.R
import com.ieschabas.pmdm.walletapp.model.tarjetas.Tarjeta
import com.ieschabas.pmdm.walletapp.ui.tarjetaDNI.TarjetaDNViewHolder
import com.ieschabas.pmdm.walletapp.ui.tarjetaPermisoCirculacion.TarjetaPermisoCirculacionViewHolder
import com.ieschabas.pmdm.walletapp.ui.tarjetaSIP.TarjetaSIPViewHolder
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class TarjetasAdapter(private val listener: TarjetaDNViewHolder.OnTarjetaClickListener) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val tarjetas = mutableListOf<Tarjeta>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TIPO_TARJETA_DNI -> {
                val itemView = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_tarjeta_dni, parent, false)
                TarjetaDNViewHolder(itemView, listener)
            }

            TIPO_TARJETA_SIP -> {
                val itemView = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_tarjeta_sip, parent, false)
                TarjetaSIPViewHolder(itemView)
            }

            TIPO_TARJETA_PERMISO_CIRCULACION -> {
                val itemView = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_tarjeta_permiso, parent, false)
                TarjetaPermisoCirculacionViewHolder(itemView)
            }

            else -> throw IllegalArgumentException("Tipo de tarjeta desconocido")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val tarjeta = tarjetas[position]
        when (holder.itemViewType) {
            TIPO_TARJETA_DNI -> {
                (holder as TarjetaDNViewHolder).bind(tarjeta as Tarjeta.TarjetaDNI)
            }

            TIPO_TARJETA_SIP -> {
                (holder as TarjetaSIPViewHolder).bind(tarjeta as Tarjeta.TarjetaSIP)
            }

            TIPO_TARJETA_PERMISO_CIRCULACION -> {
                (holder as TarjetaPermisoCirculacionViewHolder).bind(tarjeta as Tarjeta.TarjetaPermisoCirculacion)
            }
        }
    }

    override fun getItemCount() = tarjetas.size

    override fun getItemViewType(position: Int): Int {
        return when (tarjetas[position]) {
            is Tarjeta.TarjetaDNI -> TIPO_TARJETA_DNI
            is Tarjeta.TarjetaSIP -> TIPO_TARJETA_SIP
            is Tarjeta.TarjetaPermisoCirculacion -> TIPO_TARJETA_PERMISO_CIRCULACION
        }
    }

    companion object {
        private const val TIPO_TARJETA_DNI = 1
        private const val TIPO_TARJETA_SIP = 2
        private const val TIPO_TARJETA_PERMISO_CIRCULACION = 3
    }

    fun updateData(newData: List<Tarjeta>) {
        tarjetas.clear()
        tarjetas.addAll(newData)
        notifyDataSetChanged()
    }



//    private fun parseDate(dateString: Date): Date? {
//        return SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.getDefault()).parse(dateString.toString())
//    }
//
//
//    private fun formatDate(date: Date?): String {
//        if (date == null) {
//            return ""
//        }
//
//        return SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(date)
//    }
}



