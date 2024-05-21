package com.ieschabas.pmdm.walletapp.ui.tarjetaPermisoCirculacion

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ieschabas.pmdm.walletapp.data.TarjetasRepository
import com.ieschabas.pmdm.walletapp.model.tarjetas.Tarjeta

class TarjetaPermisoCirculacionViewModel (private val tarjetasRepository: TarjetasRepository) : ViewModel() {

    // LiveData para almacenar la lista de tarjetas DNI
    private val _tarjetaPermisoCirculacion =
        MutableLiveData<List<Tarjeta.TarjetaPermisoCirculacion?>>()
    val tarjetasDNI: MutableLiveData<List<Tarjeta.TarjetaPermisoCirculacion?>> get() = _tarjetaPermisoCirculacion

    // LiveData para manejar el estado de carga
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean>
        get() = _isLoading

    // LiveData para manejar los errores
    private val _error = MutableLiveData<String>()
    val error: LiveData<String>
        get() = _error

}