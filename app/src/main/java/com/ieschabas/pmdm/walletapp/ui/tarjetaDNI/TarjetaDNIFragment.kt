package com.ieschabas.pmdm.walletapp.ui.tarjetaDNI

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
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
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class TarjetaDNIFragment(private val repository: TarjetasRepository) : Fragment() {

    constructor() : this(TarjetasRepository(TarjetasApi()))

    private lateinit var viewModel: TarjetaDNIViewModel
    private var tarjetaDNI: Tarjeta.TarjetaDNI? = null

    private var _binding: FragmentTarjetaDniBinding? = null
    private val binding get() = _binding!!

    val usuarioId = FirebaseAuth.getInstance().currentUser?.uid

    private var dialog: AlertDialog? = null

    private inner class FragmentListener : TarjetaDNIFragmentListener {
        // Método para seleccionar una foto
        override fun seleccionarFoto() {
            // Llamar al método para seleccionar una foto en el Fragment
            seleccionarImagen(true)
        }
        // Método para seleccionar una firma
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
        _binding = FragmentTarjetaDniBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val viewModelFactory = TarjetaDNIViewModelFactory(requireContext(), repository)
        viewModel = ViewModelProvider(this, viewModelFactory)[TarjetaDNIViewModel::class.java]

        viewModel.setFragmentListener(fragmentListener)

        return root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Coroutine para obtener la tarjeta DNI
        lifecycleScope.launch {
            // Inicializar tarjetaDNI dentro de la coroutine
            tarjetaDNI = obtenerTarjetaDNI()

            // Carga la tarjeta DNI del usuario
            tarjetaDNI?.let { cargarTarjetaDNIUsuario(it) }

            // Configurar clic en la tarjeta para modificar
            binding.root.setOnClickListener {
                mostrarDialogoModificar(tarjetaDNI!!)
            }

            // Configurar clic largo en la tarjeta para eliminar
            binding.root.setOnLongClickListener {
                mostrarDialogoEliminar(tarjetaDNI!!)
                true
            }
            // Boton modificar tarjeta DNI
            binding.modificarButton.setOnClickListener {
                tarjetaDNI?.let {
                    mostrarDialogoModificar(it)
                }
            }

            // Boton eliminar tarjeta DNI
            binding.eliminarButton.setOnClickListener {
                tarjetaDNI?.let {
                    mostrarDialogoEliminar(it)
                }
            }
        }
    }
    // Dentro de la función donde obtienes la imagen seleccionada
    private fun guardarImagenEnBaseDeDatos(uri: Uri) {
        // Convertir la imagen a un Bitmap
        val bitmap: Bitmap = MediaStore.Images.Media.getBitmap(requireContext().contentResolver, uri)

        // Convertir el Bitmap a una cadena Base64
        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
        val imageBytes: ByteArray = outputStream.toByteArray()
        val imageBase64: String = Base64.encodeToString(imageBytes, Base64.DEFAULT)

        // Guardar la cadena Base64 en la base de datos
        // Aquí necesitarás llamar a tu método para guardar en la base de datos MySQL
        guardarImagenEnBaseDeDatosMySQL(imageBase64)
    }

    // Función para guardar la imagen en la base de datos MySQL
    private fun guardarImagenEnBaseDeDatosMySQL(imageBase64: String) {
        // Aquí deberías llamar a tu función para guardar en la base de datos MySQL
        // Puedes usar Retrofit, Room, o cualquier otra forma de interactuar con tu base de datos
        // Por ejemplo, si estás utilizando Retrofit, enviarías la cadena Base64 a través de una solicitud POST
        // a tu API que luego la insertaría en la base de datos
    }


    companion object {
        private const val READ_EXTERNAL_STORAGE = 123
        private const val SELECT_IMAGE_REQUEST_CODE = 456
    }

    private var isSelectingPhoto: Boolean = true

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            // Permission granted, launch the image picker
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            imagePickerLauncher.launch(intent)
        } else {
            // Permission denied, show a message to the user
            Toast.makeText(requireContext(), "Permission denied", Toast.LENGTH_SHORT).show()
        }
    }

    // Función para solicitar permiso de almacenamiento externo
    private fun requestExternalStoragePermission() {
        // Solicitar permiso al usuario
        requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
    }
    private fun seleccionarImagen(isSelectingPhoto: Boolean) {
        // Lógica para seleccionar la imagen
        this.isSelectingPhoto = isSelectingPhoto
        val imageType = if (isSelectingPhoto) "foto" else "firma"
        Log.d("TarjetaDNIFragment", "Iniciando selección de $imageType")

        // Verificar si el permiso para leer el almacenamiento externo ha sido concedido
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.READ_MEDIA_IMAGES
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            // Permiso concedido, proceder con la selección de imagen
            lanzarActividadSeleccionImagen()
        } else {
            // Permiso no concedido, solicitar permiso
            Log.d("TarjetaDNIFragment", "Permiso de almacenamiento externo no concedido. Solicitando permiso...")
            requestExternalStoragePermission()
        }
    }

    // Función para lanzar la actividad de selección de imagen una vez que se conceda el permiso
    private fun lanzarActividadSeleccionImagen() {
        val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
            type = "image/*"
        }
        Log.d("TarjetaDNIFragment", "Lanzando actividad de selección de imagen...")
        imagePickerLauncher.launch(intent)
    }

    // Resultado de la selección de la imagen desde la galería
    private val imagePickerLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            Log.d("TarjetaDNIFragment", "Resultado de la selección de imagen: RESULT_OK")
            val data: Intent? = result.data
            if (data != null) {
                val selectedImageUri: Uri? = data.data
                if (selectedImageUri != null) {
                    Log.d("TarjetaDNIFragment", "URI de la imagen seleccionada: $selectedImageUri")
                    viewModel.handleSeleccionImagenResult(isSelectingPhoto, selectedImageUri)
                } else {
                    Log.d("TarjetaDNIFragment", "Error: No se pudo obtener la URI de la imagen seleccionada")
                }
            }
        } else {
            Log.d("TarjetaDNIFragment", "Error, se canceló la selección de la imagen")
        }
    }

    // Función para mostrar la imagen seleccionada en el ImageView correspondiente
    private fun mostrarImagenSeleccionada(uri: Uri?, isSelectingPhoto: Boolean) {
        if (isSelectingPhoto) {
            // Mostrar imagen seleccionada en el ImageView correspondiente para la foto
            binding.ivFotografia.setImageURI(uri)
        } else {
            // Mostrar imagen seleccionada en el ImageView correspondiente para la firma
            binding.ivFirma.setImageURI(uri)
        }
    }

    private suspend fun obtenerTarjetaDNI(): Tarjeta.TarjetaDNI? {
        // Verificar si el usuario está autenticado
        usuarioId?.let {
            try {
                // Obtener la tarjeta DNI del repositorio
                val tarjetasDNI = repository.obtenerTarjetaDNIUsuario(it)
                // Si se obtiene al menos una tarjeta DNI, tomar la primera (asumiendo que solo hay una por usuario)
                if (tarjetasDNI.isNotEmpty() && tarjetasDNI[0].numeroDocumento.isNotEmpty()) {
                    return tarjetasDNI[0]
                } else {
                    // Si no se encuentra ninguna tarjeta DNI válida, devolver una tarjeta DNI vacía o lanzar una excepción según tu lógica
                    return null
                }
            } catch (e: Exception) {
                Log.e("TarjetaDNIFragment", "Error al obtener la tarjeta DNI del usuario: ${e.message}")
                // Lanzar una excepción si ocurre un error al obtener la tarjeta DNI
                throw e
            }
        }
        return null
    }

    private fun cargarTarjetaDNIUsuario(tarjetaDNI: Tarjeta.TarjetaDNI) {
        usuarioId?.let {
            Log.d("TarjetaDNIFragment", "ID de usuario: $it")
            viewModel.viewModelScope.launch {
                val tarjetasDNI = viewModel.cargarTarjetaDNIUsuario(it)
                // Verifica si se obtuvo una tarjeta DNI válida
                tarjetasDNI.let {
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


                    if (tarjetaDNI.fotografiaUrl.isNotEmpty()) {
                        Picasso.get()
                            .load(tarjetaDNI.fotografiaUrl)
                            //.placeholder(R.drawable.placeholder_image) // Recurso de imagen de marcador de posición mientras se carga la imagen
                            //.error(R.drawable.error_image) // Recurso de imagen de error si no se puede cargar la imagen
                            .into(_binding?.ivFotografia)
                    } else {
                        // Manejar el caso en el que la URL de la imagen esté vacía
                    }

                    if (tarjetaDNI.firmaUrl.isNotEmpty()) {
                        Picasso.get()
                            .load(tarjetaDNI.firmaUrl)
                            //.placeholder(R.drawable.placeholder_image) // Recurso de imagen de marcador de posición mientras se carga la imagen
                            //.error(R.drawable.error_image) // Recurso de imagen de error si no se puede cargar la imagen
                            .into(_binding?.ivFirma)
                    } else {
                        // Manejar el caso en el que la URL de la firma esté vacía
                    }
                }
            }
        }
    }

    private fun formatDate(date: Date): String {
        // Define the desired format for the date
        val targetFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        // Format the date in the desired format
        return targetFormat.format(date)
    }

    private fun parseDate(dateString: String): Date? {
        // Define the format of the date string
        val sourceFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        // Parse the date string into a Date object
        return sourceFormat.parse(dateString)
    }

    // Método para mostrar el diálogo de modificación
    private fun mostrarDialogoModificar(tarjetaDNI: Tarjeta.TarjetaDNI) {
        val viewModelFactory = TarjetaDNIViewModelFactory(requireContext(), repository)
        val viewModel = ViewModelProvider(this, viewModelFactory).get(TarjetaDNIViewModel::class.java)

        // Llamar al método para mostrar el diálogo de creación, pasando los datos de la tarjeta actual
        viewModel.mostrarDialogoCrearTarjetaDNI(usuarioId!!, tarjetaDNI)
    }
//
//    private fun mostrarDialogoModificar(tarjetaDNI: Tarjeta.TarjetaDNI) {
//        val builder = AlertDialog.Builder(requireContext())
//        val inflater = requireActivity().layoutInflater
//        val dialogLayout = inflater.inflate(R.layout.dialogo_crear_tarjeta_dni, null)
//
//        val etNumeroDocumento = dialogLayout.findViewById<EditText>(R.id.editTextNumeroDocumento)
//        val etNombre = dialogLayout.findViewById<EditText>(R.id.editTextNombre)
//        val etApellidos = dialogLayout.findViewById<EditText>(R.id.editTextApellidos)
//        val etNacionalidad = dialogLayout.findViewById<EditText>(R.id.editTextNacionalidad)
//        val etFechaNacimiento = dialogLayout.findViewById<EditText>(R.id.editTextFechaNacimiento)
//        val etFechaExpedicion = dialogLayout.findViewById<EditText>(R.id.editTextFechaExpedicion)
//        val etFechaCaducidad = dialogLayout.findViewById<EditText>(R.id.editTextFechaCaducidad)
//        val etLugarNacimiento = dialogLayout.findViewById<EditText>(R.id.editTextLugarNacimiento)
//        val etDomicilio = dialogLayout.findViewById<EditText>(R.id.editTextDomicilio)
//
//        // Establecer los valores actuales de la tarjeta DNI en los TextView
//        etNumeroDocumento.setText(tarjetaDNI.numeroDocumento)
//        etNombre.setText(tarjetaDNI.nombre)
//        etApellidos.setText(tarjetaDNI.apellidos)
//        etNacionalidad.setText(tarjetaDNI.nacionalidad)
//        etFechaNacimiento.setText(formatDate(tarjetaDNI.fechaNacimiento))
//        etFechaExpedicion.setText(formatDate(tarjetaDNI.fechaExpedicion))
//        etFechaCaducidad.setText(formatDate(tarjetaDNI.fechaCaducidad))
//        etLugarNacimiento.setText(tarjetaDNI.lugarNacimiento)
//        etDomicilio.setText(tarjetaDNI.domicilio)
//
//        builder.setView(dialogLayout)
//            .setPositiveButton("Guardar") { dialogInterface, _ ->
//                // Obtener los nuevos valores de los TextView
//                val nuevoNumeroDocumento = etNumeroDocumento.text.toString()
//                val nuevoNombre = etNombre.text.toString()
//                val nuevoApellidos = etApellidos.text.toString()
//                val nuevaNacionalidad = etNacionalidad.text.toString()
//
//                val nuevaFechaNacimiento =  parseDate(etFechaNacimiento.text.toString())
//                val nuevaFechaExpedicion = parseDate(etFechaExpedicion.text.toString())
//                val nuevaFechaCaducidad = parseDate(etFechaCaducidad.text.toString())
//                val nuevoLugarNacimiento = etLugarNacimiento.text.toString()
//                val nuevoDomicilio = etDomicilio.text.toString()
//
//                // Actualiza la tarjeta DNI con los nuevos valores
//                tarjetaDNI.numeroDocumento = nuevoNumeroDocumento
//                tarjetaDNI.nombre = nuevoNombre
//                tarjetaDNI.apellidos = nuevoApellidos
//                tarjetaDNI.nacionalidad = nuevaNacionalidad
//                if (nuevaFechaNacimiento != null) {
//                    tarjetaDNI.fechaNacimiento = nuevaFechaNacimiento
//                }
//                if (nuevaFechaExpedicion != null) {
//                    tarjetaDNI.fechaExpedicion = nuevaFechaExpedicion
//                }
//                if (nuevaFechaCaducidad != null) {
//                    tarjetaDNI.fechaCaducidad = nuevaFechaCaducidad
//                }
//                tarjetaDNI.lugarNacimiento = nuevoLugarNacimiento
//                tarjetaDNI.domicilio = nuevoDomicilio
//
//                val usuario = FirebaseAuth.getInstance().currentUser?.uid
//                usuarioId?.let { id ->
//                    // Incluir el ID de usuario en los datos a actualizar
//                    val updatedTarjetaDNI = tarjetaDNI.copy(
//                        idUsuario = usuario!!,
//                        numeroDocumento = nuevoNumeroDocumento,
//                        nombre = nuevoNombre,
//                        apellidos = nuevoApellidos,
//                        sexo = obtenerSexoDesdeUsuario(usuario),
//                        nacionalidad = nuevaNacionalidad,
//                        fechaNacimiento = nuevaFechaNacimiento!!,
//                        fechaExpedicion = nuevaFechaExpedicion!!,
//                        fechaCaducidad = nuevaFechaCaducidad!!,
//                        lugarNacimiento = nuevoLugarNacimiento,
//                        domicilio = nuevoDomicilio
//                    )
//
//                    // Actualizar la tarjeta DNI con los nuevos valores
//                    viewModel.modificarTarjetaDNI(updatedTarjetaDNI)
//
//                    // Actualizar los campos de la interfaz de usuario con los nuevos valores
//                    cargarTarjetaDNIUsuario(updatedTarjetaDNI)
//                }
//            }
//            .setNegativeButton("Cancelar") { dialogInterface, _ ->
//                dialogInterface.cancel()
//            }
//            .show()
//    }
//
//    private fun obtenerSexoDesdeUsuario(usuario: String): Tarjeta.Sexo {
//        return Tarjeta.Sexo.MASCULINO
//    }

    // Diálogo para eliminar la tarjeta DNI
    private fun mostrarDialogoEliminar(tarjetaDNI: Tarjeta.TarjetaDNI) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Eliminar Tarjeta")
        builder.setMessage("¿Estás seguro de que deseas eliminar la Tarjeta DNI?")
        builder.setPositiveButton("Sí") { dialog, _ ->
            // Lógica para eliminar la tarjeta
            eliminarTarjeta(tarjetaDNI)
            dialog.dismiss()
        }
        builder.setNegativeButton("No") { dialog, _ ->
            dialog.dismiss()
        }
        val dialog = builder.create()
        dialog.show()
    }

    private fun eliminarTarjeta(tarjetaDNI: Tarjeta.TarjetaDNI) {
        // Elimina la tarjeta DNI
        viewModel.eliminarTarjetaDNI(tarjetaDNI)
        Toast.makeText(requireContext(), "Tarjeta DNI Eliminada con éxito", Toast.LENGTH_SHORT).show()
        limpiarCampos()
    }

    private fun limpiarCampos() {
        _binding?.tvNumeroDocumento?.text = ""
        _binding?.tvNombre?.text = ""
        _binding?.tvApellidos?.text = ""
        _binding?.tvSexo?.text = ""
        _binding?.tvNacionalidad?.text = ""
        _binding?.tvFechaNacimiento?.text = ""
        _binding?.tvFechaExpedicion?.text = ""
        _binding?.tvFechaCaducidad?.text = ""
        _binding?.tvLugarNacimiento?.text = ""
        _binding?.tvDomicilio?.text = ""
        _binding?.ivFotografia?.setImageDrawable(null)
        _binding?.ivFirma?.setImageDrawable(null)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
