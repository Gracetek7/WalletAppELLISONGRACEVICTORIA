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
import com.ieschabas.pmdm.walletapp.ui.tarjetaDNI.TarjetaDNViewHolder

class UsuarioFragment(private var tarjetasRepository: TarjetasRepository) : Fragment(), TarjetaDNViewHolder.OnTarjetaClickListener {
    constructor() : this(TarjetasRepository(TarjetasApi()))

    private lateinit var viewModel: UsuarioViewModel
    private lateinit var tarjetasAdapter: TarjetasAdapter

    private var _binding: FragmentUsuarioBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUsuarioBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView = binding.recyclerView
        val crearTarjetaButton = binding.btnAgregarTarjeta

        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Inicializar el adaptador de tarjetas
        tarjetasAdapter = TarjetasAdapter(this)
        recyclerView.adapter = tarjetasAdapter  // Configurar el adaptador en el RecyclerView

        val viewModelFactory = UsuarioViewModelFactory(requireContext(), tarjetasRepository)
        viewModel = ViewModelProvider(this, viewModelFactory)[UsuarioViewModel::class.java]

        // Observar las tarjetas del usuario después de cargarlas
        viewModel.tarjetasUsuario.observe(viewLifecycleOwner) { tarjetas ->
            tarjetas?.let {

                for (tarjeta in tarjetas) {
                    Log.d("UsuarioFragment", "Tarjeta: $tarjeta")
                }

                // Actualizar el adaptador con las nuevas tarjetas
                tarjetasAdapter.updateData(tarjetas.toMutableList())

                // Agregar un mensaje de depuración para verificar el número de tarjetas recibidas
                Log.d("UsuarioFragment", "Número de tarjetas: ${tarjetas.size}")

                // Mostrar el formulario para crear nuevas tarjetas
                //viewModel.mostrarFormularioCrearTarjetas(tarjetas.firstOrNull() ?: Usuario())
            }
        }

        // Observar los errores
        viewModel.error.observe(viewLifecycleOwner) { errorMessage ->
            Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show()
        }

        cargarUsuarioActual() // Llamar al método para cargar el usuario actual y sus tarjetas asociadas
    }

    override fun onTarjetaClick(tarjeta: Tarjeta.TarjetaDNI) {
        // Aquí colocas la lógica que deseas realizar cuando se hace clic en una tarjeta DNI
        // Por ejemplo, podrías abrir una nueva pantalla o mostrar información adicional sobre la tarjeta.
    }

    override fun onTarjetaLongClick(tarjeta: Tarjeta.TarjetaDNI, position: Int) {
        TODO("Not yet implemented")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun cargarUsuarioActual() {
        val usuarioId = FirebaseAuth.getInstance().currentUser?.uid
        usuarioId?.let { id ->
            viewModel.cargarTarjetasUsuario(id)
            viewModel.tarjetasUsuario.observe(viewLifecycleOwner) { tarjetas ->
                tarjetas?.let {
                    tarjetasAdapter.updateData(tarjetas.toMutableList())
                }
            }
        }
    }
}
