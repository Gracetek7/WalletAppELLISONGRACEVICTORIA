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
import androidx.activity.result.contract.ActivityResultContracts
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.google.firebase.auth.FirebaseAuth
import com.ieschabas.pmdm.walletapp.data.TarjetasApi
import com.ieschabas.pmdm.walletapp.data.TarjetasRepository
import com.ieschabas.pmdm.walletapp.databinding.FragmentUsuarioBinding
import com.ieschabas.pmdm.walletapp.model.tarjetas.Tarjeta
import com.ieschabas.pmdm.walletapp.ui.tarjetaDNI.TarjetaDNIViewModel

interface UsuarioFragmentListener {
    fun seleccionarFoto()
    fun seleccionarFirma()
}


class UsuarioFragment(private var tarjetasRepository: TarjetasRepository) : Fragment(), TarjetasAdapter.OnTarjetaClickListener {

    // Método que pide el permiso al usuario para las notificaciones push
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { isGranted: Boolean ->
        if (isGranted) {
            enviarNotificacionAlServidor()
        } else {
            // TODO: Informar al usuario que tu aplicación no mostrará notificaciones.
        }
    }

    constructor() : this(TarjetasRepository(TarjetasApi()))

    private lateinit var viewModel: UsuarioViewModel
    private lateinit var tarjetasAdapter: TarjetasAdapter
    private lateinit var navController: NavController
    private lateinit var tarjetaDNIViewModel: TarjetaDNIViewModel


    private var _binding: FragmentUsuarioBinding? = null
    private val binding get() = _binding!!


    private var usuarioFragmentListener: UsuarioFragmentListener? = null

    // Lógica para abrir la galería cuando sea necesario seleccionar una foto o una firma
    private fun abrirGaleriaParaSeleccionarFoto() {
        usuarioFragmentListener?.seleccionarFoto()
    }

    private fun abrirGaleriaParaSeleccionarFirma() {
        usuarioFragmentListener?.seleccionarFirma()
    }

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

        navController = Navigation.findNavController(view)

        val recyclerView = binding.recyclerView

        val btnAgregarTarjeta = binding.btnAgregarTarjeta

        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Inicializa el adaptador de tarjetas
        tarjetasAdapter = TarjetasAdapter(this) // Pasa la instancia de UsuarioFragment como listener
        recyclerView.adapter = tarjetasAdapter  // Configura el adaptador en el RecyclerView

        val viewModelFactory = UsuarioViewModelFactory(requireContext(), tarjetasRepository)
        viewModel = ViewModelProvider(this, viewModelFactory)[UsuarioViewModel::class.java]

        viewModel.tarjetasUsuario.observe(viewLifecycleOwner) { tarjetas ->
            tarjetas?.let {
                // Actualizar el adaptador con las nuevas tarjetas
                tarjetasAdapter.updateData(tarjetas)
                Log.d("UsuarioFragment", "Número de tarjetas: ${tarjetas.size}")
            }
        }

        // Observa los errores
        viewModel.error.observe(viewLifecycleOwner) { errorMessage ->
            Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show()
        }
        cargarUsuarioActual() // Carga el usuario actual y sus tarjetas asociadas

        // Botón para crear nueva Tarjeta (DNI/SIP/Permiso Circulacion/Otro)
        btnAgregarTarjeta.setOnClickListener {
            Log.d("UsuarioFragment", "Botón Agregar Tarjeta clickeado")
            // Obtener el ID del usuario actual
            val usuarioId = FirebaseAuth.getInstance().currentUser?.uid
            usuarioId?.let {
                // Si se obtiene el ID del usuario, carga las tarjetas asociadas del usuario
                viewModel.mostrarFormularioCrearTarjetas(usuarioId)
            } ?: run {
                Log.e("UsuarioFragment", "No se pudo obtener el ID del usuario actual")
            }
        }

        //tarjetaDNIViewModel.setFragmentListener(this)

    }

    // Método que envia la notificacion push al servidor Firebase Cloud Messaging
    private fun enviarNotificacionAlServidor() {
        // Notificación push al servidor usando Firebase Cloud Messaging
        viewModel.enviarNotificacionAlServidor()
    }

    // Método para manejar el click en la tarjeta
    override fun onTarjetaClick(tarjeta: Tarjeta) {
        // Mostrar las tarjetas en pantalla completa al hacer click
    }
    // Método para manejar el click largo en la tarjeta
    override fun onTarjetaLongClick(tarjeta: Tarjeta, position: Int) {
        // Imprimir la tarjeta en formato pdf
    }
    // Método para cargar al usuario actual por su id
    private fun cargarUsuarioActual() {
        val usuarioId = FirebaseAuth.getInstance().currentUser?.uid
        usuarioId?.let { id ->
            viewModel.cargarTarjetasUsuario(id)
        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}