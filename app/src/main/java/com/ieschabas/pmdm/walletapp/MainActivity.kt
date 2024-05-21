package com.ieschabas.pmdm.walletapp

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.ieschabas.pmdm.walletapp.data.TarjetasApi
import com.ieschabas.pmdm.walletapp.data.TarjetasRepository
import com.ieschabas.pmdm.walletapp.databinding.ActivityMainBinding
import com.ieschabas.pmdm.walletapp.ui.usuario.UsuarioViewModel
import com.ieschabas.pmdm.walletapp.ui.usuario.UsuarioViewModelFactory
import kotlinx.coroutines.launch
class MainActivity : AppCompatActivity() {
    private lateinit var tarjetasRepository: TarjetasRepository
    private lateinit var viewModel: UsuarioViewModel
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.appBarMain.toolbar)

        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_content_main)

        // Menú lateral con las opciones de navegar hacía el menú principal(home), hacía tarjeta DNI y tarjta SIP
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_usuario, R.id.nav_tarjeta_dni, R.id.nav_tarjeta_sip
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        // Inicializa tarjetasRepository
        tarjetasRepository = TarjetasRepository(TarjetasApi())

        val usuarioId = obtenerIdUsuario()

        usuarioId?.let { id ->
            // ViewModelProvider Factory personalizadao para crear la instancia de UsuarioViewModel
            val viewModelFactory = UsuarioViewModelFactory(this, tarjetasRepository)
            viewModel =
                ViewModelProvider(this, viewModelFactory)[UsuarioViewModel::class.java]
            lifecycleScope.launch {
                viewModel.cargarUsuarioActual(id)
            }

            // Completa la carga del usuario actual y sus tarjetas
            viewModel.usuarioActual.observe(this) { usuario ->
                usuario?.let {
                    Log.d("MainActivity", "Usuario cargado: $it")
                    // Si se cargó correctamente el usuario, navegará hacía UsuarioFragment
                    val navController = findNavController(R.id.nav_host_fragment_content_main)
                    // Navega a UsuarioFragment
                    navController.navigate(R.id.nav_usuario)
                }
            }

        } ?: run {
            mostrarError("Error, al obtener el ID del usuario")
        }
    }

    // Obtiene el id del Usuario
    private fun obtenerIdUsuario(): String? {
        // Devuelve el ID de usuario del Firebase Authentication, o null si no se ha iniciado sesión
        return FirebaseAuth.getInstance().currentUser?.uid
    }

    // Muestra errores
    private fun mostrarError(mensaje: String) {
        Toast.makeText(this, mensaje, Toast.LENGTH_SHORT).show()
    }

    // Menu controller
    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}
