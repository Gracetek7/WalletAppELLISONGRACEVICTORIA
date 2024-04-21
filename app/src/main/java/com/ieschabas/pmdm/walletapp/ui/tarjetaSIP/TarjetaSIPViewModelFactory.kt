package com.ieschabas.pmdm.walletapp.ui.tarjetaSIP

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ieschabas.pmdm.walletapp.data.TarjetasRepository

class TarjetaSIPViewModelFactory(private val context: Context, private val tarjetasRepository: TarjetasRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TarjetaSIPViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TarjetaSIPViewModel(context, tarjetasRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

