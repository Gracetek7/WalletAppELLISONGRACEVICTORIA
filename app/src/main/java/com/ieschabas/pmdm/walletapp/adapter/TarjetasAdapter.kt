package com.ieschabas.pmdm.walletapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ieschabas.pmdm.walletapp.databinding.ItemTarjetaDniBinding
import com.ieschabas.pmdm.walletapp.databinding.ItemTarjetaSipBinding
import com.ieschabas.pmdm.walletapp.model.tarjetas.Tarjeta
import com.ieschabas.pmdm.walletapp.ui.tarjetaDNI.TarjetaDNViewHolder
import com.ieschabas.pmdm.walletapp.ui.tarjetaSIP.TarjetaSIPViewHolder

class TarjetasAdapter(private val listener: OnTarjetaClickListener) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var tarjetas = mutableListOf<Tarjeta>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TIPO_TARJETA_DNI -> {
                val binding = ItemTarjetaDniBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                TarjetaDNViewHolder(binding, listener) // Pasar el listener para Tarjeta DNI
            }
            TIPO_TARJETA_SIP -> {
                val binding = ItemTarjetaSipBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                TarjetaSIPViewHolder(binding, listener) // Pasar el listener para Tarjeta SIP
            }
            else -> throw IllegalArgumentException("Tipo de tarjeta no encontrada")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val tarjeta = tarjetas[position]
        val context = holder.itemView.context // Obtener el contexto del elemento de la vista

        when (holder.itemViewType) {
            TIPO_TARJETA_DNI -> {
                (holder as TarjetaDNViewHolder).bind(tarjeta as Tarjeta.TarjetaDNI, context)
            }
            TIPO_TARJETA_SIP -> {
                (holder as TarjetaSIPViewHolder).bind(tarjeta as Tarjeta.TarjetaSIP)
            }
        }
    }

    override fun getItemCount() = tarjetas.size

    override fun getItemViewType(position: Int): Int {
        return when (tarjetas[position]) {
            is Tarjeta.TarjetaDNI -> TIPO_TARJETA_DNI
            is Tarjeta.TarjetaSIP -> TIPO_TARJETA_SIP
            else -> throw IllegalArgumentException("Tipo de tarjeta no encontrada")
        }
    }

    companion object {
        private const val TIPO_TARJETA_DNI = 1
        private const val TIPO_TARJETA_SIP = 2
    }

    fun updateData(newData: List<Tarjeta>) {
        tarjetas.clear()
        tarjetas.addAll(newData)
        notifyDataSetChanged()
    }

    // Definir la interfaz del listener
    interface OnTarjetaClickListener {
        fun onTarjetaClick(tarjeta: Tarjeta)
        fun onTarjetaLongClick(tarjeta: Tarjeta, position: Int)
    }
}
