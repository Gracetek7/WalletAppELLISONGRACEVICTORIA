package com.ieschabas.pmdm.walletapp

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProvider
import com.ieschabas.pmdm.walletapp.data.TarjetasApi
import com.ieschabas.pmdm.walletapp.data.TarjetasRepository
import com.ieschabas.pmdm.walletapp.ui.tarjetaDNI.TarjetaDNIFragment
import com.ieschabas.pmdm.walletapp.ui.usuario.UsuarioFragment
import com.ieschabas.pmdm.walletapp.ui.usuario.UsuarioViewModel
import com.ieschabas.pmdm.walletapp.ui.usuario.UsuarioViewModelFactory

class MainActivity : AppCompatActivity() {
    private lateinit var tarjetasRepository: TarjetasRepository
    private lateinit var viewModel: UsuarioViewModel
    private lateinit var usuarioFragment: UsuarioFragment
    //hehehe
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        tarjetasRepository = TarjetasRepository(TarjetasApi())

        setContentView(R.layout.activity_main)

        usuarioFragment = UsuarioFragment(tarjetasRepository)

        val viewModelFactory = UsuarioViewModelFactory(applicationContext, tarjetasRepository)
        viewModel = ViewModelProvider(this, viewModelFactory)[UsuarioViewModel::class.java]

        // Observar cuando se complete la carga del usuario actual y sus tarjetas
        viewModel.usuarioActual.observe(this) { usuario ->
            usuario?.let {
                Log.d("MainActivity", "Usuario cargado: $it")
                supportFragmentManager.beginTransaction()
                    .replace(R.id.container, usuarioFragment)
                    .commit()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.activity_main_drawer, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                // Abrir el drawer cuando se hace clic en el botón de inicio
                val drawerLayout = findViewById<DrawerLayout>(R.id.drawer_layout)
                drawerLayout.openDrawer(GravityCompat.START)
                true
            }

            R.id.nav_usuario -> {
                // Navega a la pantalla de Usuario
                Log.d("MainActivity", "Click en Home")
                supportFragmentManager.beginTransaction()
                    .replace(R.id.container, usuarioFragment)
                    .commit()
                true
            }

            R.id.nav_tarjeta_dni -> {
                // Navega a la pantalla de Tarjeta DNI
                Log.d("MainActivity", "Click en Tarjeta DNI")
                val tarjetaDNIFragment = TarjetaDNIFragment()
                supportFragmentManager.beginTransaction()
                    .replace(R.id.container, tarjetaDNIFragment)
                    .commit()
                true
            }

            R.id.nav_tarjeta_sip -> {
                Log.d("MainActivity", "Click en Tarjeta SIP")
                // Navega a la pantalla de Tarjeta SIP
                val tarjetaSIPFragment = TarjetaDNIFragment()
                supportFragmentManager.beginTransaction()
                    .replace(R.id.container, tarjetaSIPFragment)
                    .commit()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }
}
