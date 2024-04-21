package com.ieschabas.pmdm.walletapp.ui.tarjetaPermisoCirculacion

import androidx.recyclerview.widget.RecyclerView
import com.ieschabas.pmdm.walletapp.databinding.FragmentTarjetaDniBinding
import com.ieschabas.pmdm.walletapp.databinding.FragmentTarjetaSipBinding
import com.ieschabas.pmdm.walletapp.databinding.ItemTarjetaDniBinding
import com.ieschabas.pmdm.walletapp.databinding.ItemTarjetaPermisoBinding
import com.ieschabas.pmdm.walletapp.databinding.ItemTarjetaSipBinding
import com.ieschabas.pmdm.walletapp.model.tarjetas.Tarjeta
import com.ieschabas.pmdm.walletapp.ui.tarjetaDNI.TarjetaDNViewHolder

class TarjetaPermisoCirculacionViewHolder(private val binding: ItemTarjetaPermisoBinding, private val listener: TarjetaDNViewHolder.OnTarjetaClickListener) :
    RecyclerView.ViewHolder(binding.root) {
    fun bind(tarjetaPermisoCirculacion: Tarjeta.TarjetaPermisoCirculacion) {
        binding.tarjetaPermisoCirculacion.text = tarjetaPermisoCirculacion.nombre

    }
    interface OnTarjetaClickListener {
        fun onTarjetaClick(tarjetaPermisoCirculacion: Tarjeta.TarjetaPermisoCirculacion)
        fun onTarjetaLongClick(tarjetaPermisoCirculacion: Tarjeta.TarjetaPermisoCirculacion, position: Int)
    }

}

