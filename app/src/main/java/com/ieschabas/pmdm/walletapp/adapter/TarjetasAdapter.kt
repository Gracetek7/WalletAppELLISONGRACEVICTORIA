package com.ieschabas.pmdm.walletapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ieschabas.pmdm.walletapp.databinding.ItemTarjetaDniBinding
import com.ieschabas.pmdm.walletapp.databinding.ItemTarjetaSipBinding
import com.ieschabas.pmdm.walletapp.model.tarjetas.Tarjeta
import com.ieschabas.pmdm.walletapp.ui.tarjetaDNI.TarjetaDNViewHolder
import com.ieschabas.pmdm.walletapp.ui.tarjetaSIP.TarjetaSIPViewHolder

class TarjetasAdapter(private val listener: TarjetaDNViewHolder.OnTarjetaClickListener) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var tarjetas = mutableListOf<Tarjeta>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TIPO_TARJETA_DNI -> {
                val binding = ItemTarjetaDniBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                TarjetaDNViewHolder(binding, listener)
            }
            TIPO_TARJETA_SIP -> {
                val binding = ItemTarjetaSipBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                TarjetaSIPViewHolder(binding)
            }
//            TIPO_TARJETA_PERMISO_CIRCULACION -> {
//                val binding = ItemTarjetaPermisoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
//                TarjetaPermisoCirculacionViewHolder(binding, listener)
//            }
            else -> throw IllegalArgumentException("Tipo de tarjeta desconocido")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val tarjeta = tarjetas[position]
        when (holder.itemViewType) {

            TIPO_TARJETA_DNI -> {
                val viewHolder = holder as TarjetaDNViewHolder
                viewHolder.bind(tarjeta as Tarjeta.TarjetaDNI)
            }

            TIPO_TARJETA_SIP -> {
                val viewHolder = holder as TarjetaSIPViewHolder
                viewHolder.bind(tarjeta as Tarjeta.TarjetaSIP)
            }
//            TIPO_TARJETA_PERMISO_CIRCULACION -> {
//                (holder as TarjetaPermisoCirculacionViewHolder).bind(tarjeta as Tarjeta.TarjetaPermisoCirculacion)
//            }
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
}





