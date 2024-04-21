package com.ieschabas.pmdm.walletapp.ui.tarjetaPermisoCirculacion

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.ieschabas.pmdm.walletapp.R
import com.ieschabas.pmdm.walletapp.databinding.FragmentTarjetaDniBinding
import com.ieschabas.pmdm.walletapp.databinding.ItemTarjetaPermisoBinding
import com.ieschabas.pmdm.walletapp.model.tarjetas.Tarjeta
import com.ieschabas.pmdm.walletapp.ui.tarjetaDNI.TarjetaDNIViewModel

class TarjetaPermisoCirculacionFragment : Fragment() {

    private lateinit var viewModel: TarjetaPermisoCirculacionViewModel

    private var _binding: ItemTarjetaPermisoBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = ItemTarjetaPermisoBinding.inflate(inflater, container, false)
        val root: View = binding.root

        viewModel = ViewModelProvider(this)[TarjetaPermisoCirculacionViewModel::class.java]

        // Configurar la barra de herramientas (Toolbar)
        val toolbar = view?.findViewById<Toolbar>(R.id.toolbar)
        (activity as AppCompatActivity).setSupportActionBar(toolbar)

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Inicializar vistas y mostrar información de la tarjeta
        //mostrarInformacionTarjeta(tarjeta)

        // Configurar clic en la tarjeta para modificar
        view.setOnClickListener {
            //abrirFormularioModificacion(tarjeta)
        }

        // Configurar clic largo en la tarjeta para eliminar
        view.setOnLongClickListener {
            //mostrarDialogoEliminar(id)
            true
        }
    }
}