package com.ieschabas.pmdm.walletapp.ui.usuario

import android.app.Activity
import android.content.Intent
import android.net.Uri
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
import com.ieschabas.pmdm.walletapp.ui.tarjetaDNI.TarjetaDNIFragmentListener
import com.ieschabas.pmdm.walletapp.ui.tarjetaDNI.TarjetaDNIViewModel
import com.ieschabas.pmdm.walletapp.ui.tarjetaDNI.TarjetaDNIViewModelFactory

class UsuarioFragment(private var tarjetasRepository: TarjetasRepository) : Fragment(), TarjetasAdapter.OnTarjetaClickListener {

    constructor() : this(TarjetasRepository(TarjetasApi()))

    private lateinit var usuarioViewModel: UsuarioViewModel
    private lateinit var tarjetaDNIViewModel: TarjetaDNIViewModel
    private lateinit var tarjetasAdapter: TarjetasAdapter
    private lateinit var navController: NavController

    private var _binding: FragmentUsuarioBinding? = null
    private val binding get() = _binding!!

    private var isSelectingPhoto: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUsuarioBinding.inflate(inflater, container, false)

        val viewModelFactory = UsuarioViewModelFactory(requireContext(), tarjetasRepository)
        usuarioViewModel = ViewModelProvider(this, viewModelFactory)[UsuarioViewModel::class.java]


        val tarjetaDNIViewModelFactory = TarjetaDNIViewModelFactory(requireContext(), tarjetasRepository)
        tarjetaDNIViewModel = ViewModelProvider(this, tarjetaDNIViewModelFactory)[TarjetaDNIViewModel::class.java]

        val tarjetaDNIFragmentListener = object : TarjetaDNIFragmentListener {
            override fun seleccionarFoto() {
                seleccionarImagen(true)
            }

            override fun seleccionarFirma() {
                seleccionarImagen(false)
            }
        }
        tarjetaDNIViewModel.setFragmentListener(tarjetaDNIFragmentListener)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = Navigation.findNavController(view)

        val recyclerView = binding.recyclerView
        val btnAgregarTarjeta = binding.btnAgregarTarjeta

        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        tarjetasAdapter = TarjetasAdapter(this)
        recyclerView.adapter = tarjetasAdapter

        val usuarioViewModelFactory = UsuarioViewModelFactory(requireContext(), tarjetasRepository)
        usuarioViewModel = ViewModelProvider(this, usuarioViewModelFactory)[UsuarioViewModel::class.java]

        usuarioViewModel.tarjetasUsuario.observe(viewLifecycleOwner) { tarjetas ->
            tarjetas?.let {
                tarjetasAdapter.updateData(tarjetas)
                Log.d("UsuarioFragment", "Número de tarjetas: ${tarjetas.size}")
            }
        }

        usuarioViewModel.error.observe(viewLifecycleOwner) { errorMessage ->
            Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show()
        }
        cargarUsuarioActual()

        btnAgregarTarjeta.setOnClickListener {
            Log.d("UsuarioFragment", "Botón Agregar Tarjeta clickeado")
            val usuarioId = FirebaseAuth.getInstance().currentUser?.uid
            usuarioId?.let { id ->
                usuarioViewModel.mostrarFormularioCrearTarjetas(id) { isDNISelected ->
                    if (!isDNISelected) {
                        tarjetaDNIViewModel.mostrarDialogoCrearTarjetaDNI(id)
                    }
                }
            } ?: run {
                Log.e("UsuarioFragment", "No se pudo obtener el ID del usuario actual")
            }
        }

    }

    override fun onTarjetaClick(tarjeta: Tarjeta) {
        // Mostrar las tarjetas en pantalla completa al hacer click
    }

    override fun onTarjetaLongClick(tarjeta: Tarjeta, position: Int) {
        // Imprimir la tarjeta en formato pdf
    }

    private fun cargarUsuarioActual() {
        val usuarioId = FirebaseAuth.getInstance().currentUser?.uid
        usuarioId?.let { id ->
            usuarioViewModel.cargarTarjetasUsuario(id)
        }
    }

    private fun seleccionarImagen(isSelectingPhoto: Boolean) {
        this.isSelectingPhoto = isSelectingPhoto
        val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
            type = "image/*"
        }
        imagePickerLauncher.launch(intent)
    }

    private val imagePickerLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data
            val selectedImageUri: Uri? = data?.data
            selectedImageUri?.let {
                // Pasa la URI seleccionada al ViewModel correspondiente
                if (isSelectingPhoto) {
                    tarjetaDNIViewModel.handleSeleccionFoto(selectedImageUri)
                } else {
                    tarjetaDNIViewModel.handleSeleccionFirma(selectedImageUri)
                }
            }
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

interface TarjetaDNIFragmentListener {
    fun seleccionarFoto()
    fun seleccionarFirma()
}
