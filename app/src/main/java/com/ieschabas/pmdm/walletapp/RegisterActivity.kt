package com.ieschabas.pmdm.walletapp

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.firebase.auth.FirebaseAuth
import com.ieschabas.pmdm.walletapp.data.TarjetasApi
import com.ieschabas.pmdm.walletapp.data.TarjetasRepository
import kotlinx.coroutines.launch
import org.json.JSONObject

class RegisterActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var tarjetasRepository: TarjetasRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registro)

        auth = FirebaseAuth.getInstance()
        tarjetasRepository = TarjetasRepository(TarjetasApi())

        val etEmail = findViewById<EditText>(R.id.etCorreo)
        val etPassword = findViewById<EditText>(R.id.etContrasena)
        val etNombre = findViewById<EditText>(R.id.etNombre)
        val etApellidos = findViewById<EditText>(R.id.etApellidos)
        val btnRegister = findViewById<Button>(R.id.btnRegistrar)

        btnRegister.setOnClickListener {
            val email = etEmail.text.toString()
            val password = etPassword.text.toString()
            val nombre = etNombre.text.toString()
            val apellidos = etApellidos.text.toString()

            if (email.isEmpty() || password.isEmpty() || nombre.isEmpty() || apellidos.isEmpty()) {
                Toast.makeText(this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            registerUser(email, password, nombre, apellidos)
        }
    }

    private fun registerUser(
        email: String,
        password: String,
        nombre: String,
        apellidos: String
    ) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val firebaseUser = auth.currentUser
                    val firebaseUserId = firebaseUser?.uid

                    if (firebaseUserId != null) {
                        val userData = mapOf(
                            "id" to firebaseUserId,
                            "nombre" to nombre,
                            "apellidos" to apellidos,
                            "correoElectronico" to email,
                            "contrasena" to password
                        )
                        lifecycleScope.launch {
                            syncUserData(userData)
                        }
                    } else {
                        Toast.makeText(this, "Error al obtener el ID de usuario de Firebase", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this, "Error al registrar en Firebase: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }
    // Sincroniza el usuario/a registrado usando Firebase con la base de datos de MySQL
    private suspend fun syncUserData(userData: Map<String, String>) {
        try {
            val response = tarjetasRepository.syncUserData(userData)
            if (response?.isSuccessful == true) {
                Log.d("RegisterActivity", "Datos del usuario sincronizados correctamente")
                runOnUiThread {
                    Toast.makeText(applicationContext, "Usuario registrado correctamente en la base de datos", Toast.LENGTH_SHORT).show()
                }
            } else {
                Log.e("RegisterActivity", "Error al sincronizar datos del usuario: ${response?.message()}")
                runOnUiThread {
                    Toast.makeText(applicationContext, "Error al sincronizar datos del usuario", Toast.LENGTH_SHORT).show()
                }
            }
        } catch (e: Exception) {
            Log.e("RegisterActivity", "Excepción al sincronizar datos del usuario: ${e.message}")
            runOnUiThread {
                Toast.makeText(applicationContext, "Excepción al sincronizar datos del usuario: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}