package com.ieschabas.pmdm.walletapp.ui.usuario

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
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
import androidx.core.content.ContextCompat
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

    private var isSelectingPhoto: Boolean = true

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            // Permission granted, launch the image picker
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            Log.d("UsuarioFragment", "lanzando la actividad seleccionar imagen")
            imagePickerLauncher.launch(intent)
        } else {
            // Permission denied, show a message to the user
            Toast.makeText(requireContext(), "Permiso denegado", Toast.LENGTH_SHORT).show()
        }
    }
    // Resultado de la selección de la imagen desde la galería
    private val imagePickerLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            Log.d("UsuarioFragment", "Resultado de la selección de imagen: RESULT_OK")
            val data: Intent? = result.data
            if (data != null) {
                val selectedImageUri: Uri? = data.data
                if (selectedImageUri != null) {
                    Log.d("UsuarioFragment", "URI de la imagen seleccionada: $selectedImageUri")
                    tarjetaDNIViewModel.handleSeleccionImagenResult(isSelectingPhoto, selectedImageUri)
                } else {
                    Log.d("UsuarioFragment", "Error: No se pudo obtener la URI de la imagen seleccionada")
                }
            }
        } else {
            Log.d("UsuarioFragment", "Error, se canceló la selección de la imagen")
        }
    }

    private inner class FragmentListener : TarjetaDNIFragmentListener {
        // Método para seleccionar foto
        override fun seleccionarFoto() {
            // Llamar al método para seleccionar foto en el Fragment
            seleccionarImagen(true)
        }
        // Método para seleccionar firma
        override fun seleccionarFirma() {
            // Llamar al método para seleccionar firma en el Fragment
            seleccionarImagen(false)
        }
    }

    private val fragmentListener = FragmentListener()

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

        tarjetaDNIViewModel.setFragmentListener(fragmentListener)

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

        usuarioViewModel.tarjetasUsuario.observe(viewLifecycleOwner) { tarjetas ->
            tarjetas?.let {
                tarjetasAdapter.updateData(tarjetas)
                Log.d("UsuarioFragment", "Número de tarjetas: ${tarjetas.size}")
            }
        }

        usuarioViewModel.error.observe(viewLifecycleOwner) { errorMessage ->
            Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show()
        }

        // Carga al usuario actual
        cargarUsuarioActual()

        // Boton click listener para escuchar el click producido del usuario,
        // llamando al metodo de mostrar dialogo de crear Tarjetas
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

    // Carga al usuario actual con sus tarjetas
    private fun cargarUsuarioActual() {
        val usuarioId = FirebaseAuth.getInstance().currentUser?.uid
        usuarioId?.let { id ->
            usuarioViewModel.cargarTarjetasUsuario(id)
        }
    }

    // Función para solicitar permiso de almacenamiento externo
    private fun requestExternalStoragePermission() {
        // Solicitar permiso al usuario
        requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
    }

    fun seleccionarImagen(isSelectingPhoto: Boolean) {
        // Lógica para seleccionar la imagen
        this.isSelectingPhoto = isSelectingPhoto
        val imageType = if (isSelectingPhoto) "foto" else "firma"
        Log.d("UsuarioFragment", "Iniciando selección de $imageType")

        // Verifica si el permiso para leer el almacenamiento externo ha sido concedido
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.READ_MEDIA_IMAGES
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            // Permiso aceptado
            lanzarActividadSeleccionImagen()
        } else {
            // Permiso no aceptado, solicitar permiso
            Log.d("UsuarioFragment", "Permiso de almacenamiento externo no aceptado.")
            requestExternalStoragePermission()
        }
    }

    private fun lanzarActividadSeleccionImagen() {
        val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
            type = "image/*"
        }
        Log.d("UsuarioFragment", "Lanzando la actividad de selección de imagen")
        imagePickerLauncher.launch(intent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
