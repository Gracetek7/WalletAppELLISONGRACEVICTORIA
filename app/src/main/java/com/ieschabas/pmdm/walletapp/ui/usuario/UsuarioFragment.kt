package com.ieschabas.pmdm.walletapp.ui.usuario

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ieschabas.pmdm.walletapp.R
import com.ieschabas.pmdm.walletapp.adapter.TarjetasAdapter
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ComponentActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.ieschabas.pmdm.walletapp.data.TarjetasApi
import com.ieschabas.pmdm.walletapp.data.TarjetasRepository
import com.ieschabas.pmdm.walletapp.model.tarjetas.Tarjeta
import com.ieschabas.pmdm.walletapp.ui.tarjetaDNI.TarjetaDNIFragment
import com.ieschabas.pmdm.walletapp.ui.tarjetaDNI.TarjetaDNViewHolder
import kotlinx.coroutines.launch
class UsuarioFragment(private var tarjetasRepository: TarjetasRepository) : Fragment(), TarjetaDNViewHolder.OnTarjetaClickListener {

    private lateinit var viewModel: UsuarioViewModel
    private lateinit var tarjetasAdapter: TarjetasAdapter

    constructor() : this(TarjetasRepository(TarjetasApi()))

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflar el diseño del fragmento
        val view = inflater.inflate(R.layout.fragment_usuario, container, false)

        // Configurar la barra de herramientas (Toolbar)
        val toolbar = view.findViewById<Toolbar>(R.id.toolbar)
        (activity as AppCompatActivity).setSupportActionBar(toolbar)

        // Configurar el botón de hamburguesa en la barra de herramientas
        (activity as AppCompatActivity).supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.ic_menu_usuario) // Establecer el icono de hamburguesa
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("UsuarioFragment", "Hilo actual: ${Thread.currentThread().name}")

        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView)
        val crearTarjetaButton = view.findViewById<Button>(R.id.btnAgregarTarjeta)

        // Asignar el LayoutManager antes de crear el adaptador y asignarlo al RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        val viewModelFactory = UsuarioViewModelFactory(requireContext(), tarjetasRepository)
        viewModel = ViewModelProvider(this, viewModelFactory)[UsuarioViewModel::class.java]

        // Initialize the adapter with an empty list
        tarjetasAdapter = TarjetasAdapter(this)
        recyclerView.adapter = tarjetasAdapter

        // Observe the tarjetasUsuario LiveData in the ViewModel
        viewModel.tarjetasUsuario.observe(viewLifecycleOwner) { tarjetas ->
            // Agregar un mensaje de depuración para verificar si las tarjetas se están recibiendo correctamente
            Log.d("UsuarioFragment", "Tarjetas recibidas: $tarjetas")

            // Verificar si las tarjetas DNI están presentes en la lista
            val tarjetasDNI = tarjetas.filterIsInstance<Tarjeta.TarjetaDNI>()
            Log.d("UsuarioFragment", "Tarjetas DNI recibidas: $tarjetasDNI")

            // Update the adapter with the new data
            tarjetasAdapter.updateData(tarjetas)

            // Agregar un mensaje de depuración para verificar el número de tarjetas recibidas
            Log.d("UsuarioFragment", "Número de tarjetas: ${tarjetas.size}")
        }

        // Observar errores y mostrar mensajes de toast
        viewModel.error.observe(viewLifecycleOwner) { errorMessage ->
            Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show()
        }

        // Configurar el botón para mostrar el formulario de creación de tarjetas
        crearTarjetaButton.setOnClickListener {
            viewModel.usuarioActual.value?.let { usuario ->
                viewModel.mostrarFormularioCrearTarjetas(usuario)
            }
        }
        // Carga al usuario con sus tarjetas
        cargarUsuarioActual()
    }

    // Al hacer un click se llama al metodo
    override fun onTarjetaClick(tarjeta: Tarjeta.TarjetaDNI) {
        // For example, expand the card or show a detailed view

        //Expandir la tarjeta al hacer click a pantalla completa
    }

    // Al hacer un click largo se llama al metodo
    override fun onTarjetaLongClick(tarjeta: Tarjeta.TarjetaDNI, position: Int) {
        // For example, show a delete confirmation dialog

        //Complementar con algo
    }

    private fun cargarUsuarioActual() {
        // Recoge el ID autogenerado por Firebase del usuario
        val usuario = FirebaseAuth.getInstance().currentUser
        val usuarioId = usuario?.uid
        usuarioId?.let {
            Log.d("UsuarioFragment", "ID de usuario: $it")
            viewModel.viewModelScope.launch {
                // Carga al usuario actual con sus tarjetas
                viewModel.cargarUsuarioActual(it)
                Log.d("UsuarioFragment", "Cargando las tarjetas del usuario: $it")
            }
        }
    }
}
