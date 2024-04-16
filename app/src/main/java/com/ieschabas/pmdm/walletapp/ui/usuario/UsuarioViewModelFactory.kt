package com.ieschabas.pmdm.walletapp.ui.usuario

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ieschabas.pmdm.walletapp.data.TarjetasRepository


class UsuarioViewModelFactory(private val context: Context, private val tarjetasRepository: TarjetasRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(UsuarioViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return UsuarioViewModel(context, tarjetasRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
