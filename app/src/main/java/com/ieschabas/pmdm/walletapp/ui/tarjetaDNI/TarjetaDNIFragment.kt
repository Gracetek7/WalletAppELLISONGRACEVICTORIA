package com.ieschabas.pmdm.walletapp.ui.tarjetaDNI

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.ieschabas.pmdm.walletapp.R
import com.ieschabas.pmdm.walletapp.data.TarjetasApi
import com.ieschabas.pmdm.walletapp.data.TarjetasRepository
import com.ieschabas.pmdm.walletapp.databinding.FragmentTarjetaDniBinding
import com.ieschabas.pmdm.walletapp.model.tarjetas.Tarjeta
import com.squareup.picasso.Picasso
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class TarjetaDNIFragment(private val repository: TarjetasRepository) : Fragment() {

    constructor() : this(TarjetasRepository(TarjetasApi()))

    private lateinit var viewModel: TarjetaDNIViewModel
    private lateinit var tarjetaDNI: Tarjeta.TarjetaDNI

    private var _binding: FragmentTarjetaDniBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTarjetaDniBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val viewModelFactory = TarjetaDNIViewModelFactory(requireContext(), repository)
        viewModel = ViewModelProvider(this, viewModelFactory)[TarjetaDNIViewModel::class.java]

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Lanzar una coroutine para obtener la tarjeta DNI
        lifecycleScope.launch {
            // Inicializar tarjetaDNI dentro de la coroutine
            tarjetaDNI = obtenerTarjetaDNI()

            cargarTarjetaDNIUsuario(tarjetaDNI)

            // Configurar clic en la tarjeta para modificar
            binding.root.setOnClickListener {
                //abrirFormularioModificacion(tarjeta)
            }

            // Configurar clic largo en la tarjeta para eliminar
            binding.root.setOnLongClickListener {
                mostrarDialogoEliminar(id)
                true
            }
        }
    }

    private suspend fun obtenerTarjetaDNI(): Tarjeta.TarjetaDNI {
        val usuarioId = FirebaseAuth.getInstance().currentUser?.uid

        // Verificar si el usuario está autenticado
        usuarioId?.let {
            try {
                // Obtener la tarjeta DNI del repositorio
                val tarjetasDNI = repository.obtenerTarjetaDNIUsuario(it)
                // Si se obtiene al menos una tarjeta DNI, tomar la primera (asumiendo que solo hay una por usuario)
                if (tarjetasDNI.isNotEmpty()) {
                    return tarjetasDNI[0]
                } else {
                    // Si no se encuentra ninguna tarjeta DNI, devolver una tarjeta DNI vacía o lanzar una excepción según tu lógica
                    return Tarjeta.TarjetaDNI(
                        idUsuario = it,
                        numeroDocumento = "",
                        fechaNacimiento = Date(),
                        fechaExpedicion = Date(),
                        fechaCaducidad = Date(),
                        nombre = "",
                        apellidos = "",
                        sexo = Tarjeta.Sexo.MASCULINO, // O cualquier otro valor por defecto
                        nacionalidad = "",
                        lugarNacimiento = "",
                        domicilio = "",
                        fotografiaUrl = "",
                        firmaUrl = ""
                    )
                }
            } catch (e: Exception) {
                Log.e("TarjetaDNIFragment", "Error al obtener la tarjeta DNI del usuario: ${e.message}")
                // Lanzar una excepción si ocurre un error al obtener la tarjeta DNI
                throw e
            }
        }

        // Si el usuario no está autenticado, puedes devolver una tarjeta DNI vacía o lanzar una excepción según tu lógica
        return Tarjeta.TarjetaDNI(
            idUsuario = "",
            numeroDocumento = "",
            fechaNacimiento = Date(),
            fechaExpedicion = Date(),
            fechaCaducidad = Date(),
            nombre = "",
            apellidos = "",
            sexo = Tarjeta.Sexo.MASCULINO, // O cualquier otro valor por defecto
            nacionalidad = "",
            lugarNacimiento = "",
            domicilio = "",
            fotografiaUrl = "",
            firmaUrl = ""
        )
    }



    private fun cargarTarjetaDNIUsuario(tarjetaDNI: Tarjeta.TarjetaDNI) {
        val usuarioId = FirebaseAuth.getInstance().currentUser?.uid
        usuarioId?.let {
            Log.d("TarjetaDNIFragment", "ID de usuario: $it")
            viewModel.viewModelScope.launch {
                val tarjetasDNI = viewModel.cargarTarjetaDNIUsuario(it)
                // Verifica si se obtuvo una tarjeta DNI válida
                tarjetasDNI.let { tarjeta ->
                    _binding?.tvNumeroDocumento?.text = getString(
                        R.string.dni_template,
                        getString(R.string.dni),
                        tarjetaDNI.numeroDocumento
                    )
                    _binding?.tvNombre?.text = getString(
                        R.string.nombre_template,
                        getString(R.string.nombre),
                        tarjetaDNI.nombre
                    )
                    _binding?.tvApellidos?.text = getString(
                        R.string.apellidos_template,
                        getString(R.string.apellidos),
                        tarjetaDNI.apellidos
                    )
                    _binding?.tvSexo?.text =
                        getString(R.string.sexo_template, getString(R.string.sexo), tarjetaDNI.sexo)
                    _binding?.tvNacionalidad?.text = getString(
                        R.string.nacionalidad_template,
                        getString(R.string.nacionalidad),
                        tarjetaDNI.nacionalidad
                    )
                    _binding?.tvFechaNacimiento?.text = formatDate(tarjetaDNI.fechaNacimiento)
                    _binding?.tvFechaExpedicion?.text = formatDate(tarjetaDNI.fechaExpedicion)
                    _binding?.tvFechaCaducidad?.text = formatDate(tarjetaDNI.fechaCaducidad)
                    _binding?.tvLugarNacimiento?.text = getString(
                        R.string.lugar_nacimiento_template,
                        getString(R.string.lugar_de_nacimiento),
                        tarjetaDNI.lugarNacimiento
                    )
                    _binding?.tvDomicilio?.text = getString(
                        R.string.domicilio_template,
                        getString(R.string.domicilio),
                        tarjetaDNI.domicilio
                    )
                    Picasso.get()
                        .load(tarjetaDNI.fotografiaUrl)
                        //.placeholder(R.drawable.placeholder_image) // Recurso de imagen de marcador de posición mientras se carga la imagen
                        //.error(R.drawable.error_image) // Recurso de imagen de error si no se puede cargar la imagen
                        .into(_binding?.ivFotografia)

                    Picasso.get()
                        .load(tarjetaDNI.firmaUrl)
                        //.placeholder(R.drawable.placeholder_image) // Recurso de imagen de marcador de posición mientras se carga la imagen
                        //.error(R.drawable.error_image) // Recurso de imagen de error si no se puede cargar la imagen
                        .into(_binding?.ivFirma)
                }
            }
        }
    }

    private fun formatDate(date: Date): String {
        // Define el formato deseado para la fecha
        val targetFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        // Formatea la fecha en el formato deseado
        return targetFormat.format(date)
    }

    private fun mostrarDialogoEliminar(id: Int) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Eliminar Tarjeta")
        builder.setMessage("¿Estás seguro de que deseas eliminar la Tarjeta DNI?")
        builder.setPositiveButton("Sí") { dialog, _ ->
            // Lógica para eliminar la tarjeta
            eliminarTarjeta(id)
            dialog.dismiss()
        }
        builder.setNegativeButton("No") { dialog, _ ->
            dialog.dismiss()
        }
        val dialog = builder.create()
        dialog.show()
    }

    private fun eliminarTarjeta(id: Int) {
        viewModel.eliminarTarjetaDNI(id)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
