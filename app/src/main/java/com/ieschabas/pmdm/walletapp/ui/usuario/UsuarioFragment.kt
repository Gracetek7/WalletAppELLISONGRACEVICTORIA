package com.ieschabas.pmdm.walletapp.ui.usuario

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.ieschabas.pmdm.walletapp.adapter.TarjetasAdapter
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.ieschabas.pmdm.walletapp.data.TarjetasApi
import com.ieschabas.pmdm.walletapp.data.TarjetasRepository
import com.ieschabas.pmdm.walletapp.databinding.FragmentUsuarioBinding
import com.ieschabas.pmdm.walletapp.model.tarjetas.Tarjeta

class UsuarioFragment(private var tarjetasRepository: TarjetasRepository) : Fragment(), TarjetasAdapter.OnTarjetaClickListener {

    constructor() : this(TarjetasRepository(TarjetasApi()))

    private lateinit var viewModel: UsuarioViewModel
    private lateinit var tarjetasAdapter: TarjetasAdapter

    private var _binding: FragmentUsuarioBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentUsuarioBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView = binding.recyclerView

        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Inicializar el adaptador de tarjetas
        tarjetasAdapter = TarjetasAdapter(this) // Pasar una instancia de UsuarioFragment como listener
        recyclerView.adapter = tarjetasAdapter  // Configurar el adaptador en el RecyclerView

        val viewModelFactory = UsuarioViewModelFactory(requireContext(), tarjetasRepository)
        viewModel = ViewModelProvider(this, viewModelFactory)[UsuarioViewModel::class.java]

        // Observar las tarjetas del usuario después de cargarlas
        viewModel.tarjetasUsuario.observe(viewLifecycleOwner) { tarjetas ->
            tarjetas?.let {
                // Actualizar el adaptador con las nuevas tarjetas
                tarjetasAdapter.updateData(tarjetas)
                // Agregar un mensaje de depuración para verificar el número de tarjetas recibidas
                Log.d("UsuarioFragment", "Número de tarjetas: ${tarjetas.size}")
            }
        }

        // Observar los errores
        viewModel.error.observe(viewLifecycleOwner) { errorMessage ->
            Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show()
        }

        cargarUsuarioActual() // Llamar al método para cargar el usuario actual y sus tarjetas asociadas
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onTarjetaClick(tarjeta: Tarjeta) {
        // Aquí colocas la lógica que deseas realizar cuando se hace clic en una tarjeta
        // Por ejemplo, podrías abrir una nueva pantalla o mostrar información adicional sobre la tarjeta.
    }

    override fun onTarjetaLongClick(tarjeta: Tarjeta, position: Int) {
        // Aquí colocas la lógica que deseas realizar cuando se hace un clic largo en una tarjeta
        // Por ejemplo, podrías mostrar un menú contextual o realizar una acción específica.
    }

    private fun cargarUsuarioActual() {
        val usuarioId = FirebaseAuth.getInstance().currentUser?.uid
        usuarioId?.let { id ->
            viewModel.cargarTarjetasUsuario(id)
        }
    }
}

