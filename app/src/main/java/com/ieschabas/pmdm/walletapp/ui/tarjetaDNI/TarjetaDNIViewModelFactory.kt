package com.ieschabas.pmdm.walletapp.ui.tarjetaDNI

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.FirebaseAuth
import com.ieschabas.pmdm.walletapp.data.TarjetasRepository

class TarjetaDNIViewModelFactory(private val context: Context, private val tarjetasRepository: TarjetasRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TarjetaDNIViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TarjetaDNIViewModel(context, tarjetasRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

