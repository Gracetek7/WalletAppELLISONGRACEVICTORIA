package com.ieschabas.pmdm.walletapp.ui.usuario

import android.app.AlertDialog
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.ieschabas.pmdm.walletapp.adapter.TarjetasAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.google.firebase.auth.FirebaseAuth
import com.ieschabas.pmdm.walletapp.R
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

    private inner class FragmentListener : UsuarioFragmentListener {
        // Implementación del método para seleccionar una foto
        override fun seleccionarFoto() {
            // Llamar al método para seleccionar una foto en el Fragment
            seleccionarImagen(true)
        }

        // Implementación del método para seleccionar una firma
        override fun seleccionarFirma() {
            // Llamar al método para seleccionar una firma en el Fragment
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
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView = binding.recyclerView

        val btnAgregarTarjeta = binding.btnAgregarTarjeta

        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Inicializar el adaptador de tarjetas
        tarjetasAdapter = TarjetasAdapter(this) // Pasar una instancia de UsuarioFragment como listener
        recyclerView.adapter = tarjetasAdapter  // Configurar el adaptador en el RecyclerView

        val viewModelFactory = UsuarioViewModelFactory(requireContext(), tarjetasRepository)
        viewModel = ViewModelProvider(this, viewModelFactory)[UsuarioViewModel::class.java]

        viewModel.setFragmentListener(fragmentListener)

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

        btnAgregarTarjeta.setOnClickListener {
            Log.d("UsuarioFragment", "Botón Agregar Tarjeta clickeado")
            // Obtener el ID del usuario actual
            val usuarioId = FirebaseAuth.getInstance().currentUser?.uid
            usuarioId?.let { id ->
                // Si se obtiene el ID del usuario, cargar sus tarjetas asociadas
                viewModel.mostrarFormularioCrearTarjetas(usuarioId)
                // No es necesario obtener el objeto Usuario aquí, ya que solo necesitas el ID para cargar las tarjetas.
            } ?: run {
                Log.e("UsuarioFragment", "No se pudo obtener el ID del usuario actual")
            }
        }
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

    // Método para mostrar la imagen seleccionada en un diálogo
    private fun mostrarImagenesSeleccionadas(uriFoto: Uri?, uriFirma: Uri?) {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialogo_crear_tarjeta_dni, null)
        val imageViewFoto = dialogView.findViewById<ImageView>(R.id.btnSeleccionarFoto)
        val imageViewFirma = dialogView.findViewById<ImageView>(R.id.btnSeleccionarFirma)

        // Configura las imágenes seleccionadas en los ImageViews del diálogo
        uriFoto?.let {
            imageViewFoto.setImageURI(uriFoto)
        }
        uriFirma?.let {
            imageViewFirma.setImageURI(uriFirma)
        }

        // Crea y muestra el diálogo
        AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .setPositiveButton("Aceptar") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private var isSelectingPhoto: Boolean = true


    private fun seleccionarImagen(isSelectingPhoto: Boolean) {
        // Lógica para seleccionar la imagen
        this.isSelectingPhoto = isSelectingPhoto
        val imageType = if (isSelectingPhoto) "foto" else "firma"
        Log.d("UsuarioFragment", "Iniciando selección de $imageType")
        // Lanzar la actividad para seleccionar la imagen
        imagePickerLauncher.launch("image/*")
    }

    private val imagePickerLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            // Check which type of image is being selected and handle accordingly
            if (viewModel.fotoSeleccionadaUrl.value == null) {
                viewModel.handleSeleccionFotoResult(uri)
            } else if (viewModel.firmaSeleccionadaUrl.value == null) {
                viewModel.handleSeleccionFirmaResult(uri)
            } else {
                Log.d("UsuarioFragment", "Both images already selected")
            }

            // Check if both images have been selected
            if (viewModel.fotoSeleccionadaUrl.value != null && viewModel.firmaSeleccionadaUrl.value != null) {
                // If both images have been selected, show them
                viewModel.fotoSeleccionadaUrl.value?.let { uriFoto ->
                    viewModel.firmaSeleccionadaUrl.value?.let { uriFirma ->
                        mostrarImagenesSeleccionadas(uriFoto, uriFirma)
                    }
                }
            }
        } else {
            Log.d("UsuarioFragment", "Error: Se ha cancelado la selección de la imagen")
        }
    }

}