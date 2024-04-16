package com.ieschabas.pmdm.walletapp.ui.tarjetaDNI

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.ieschabas.pmdm.walletapp.R
import com.ieschabas.pmdm.walletapp.model.tarjetas.Tarjeta
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class TarjetaDNIFragment : Fragment() {

    private lateinit var tarjeta: Tarjeta.TarjetaDNI
    private lateinit var viewModel: TarjetaDNIViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProvider(this).get(TarjetaDNIViewModel::class.java)

        // Configurar la barra de herramientas (Toolbar)
        val toolbar = view?.findViewById<Toolbar>(R.id.toolbar)
        (activity as AppCompatActivity).setSupportActionBar(toolbar)

        // Configurar el botón de hamburguesa en la barra de herramientas
        (activity as AppCompatActivity).supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.ic_menu_usuario) // Establecer el icono de hamburguesa
        }

        return inflater.inflate(R.layout.fragment_tarjeta_dni, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //viewModel.cargarTarjetaDNI(id)

        // Inicializar vistas y mostrar información de la tarjeta
        mostrarInformacionTarjeta()

        // Configurar clic en la tarjeta para modificar
        view.setOnClickListener {
            abrirFormularioModificacion(tarjeta)
        }

        // Configurar clic largo en la tarjeta para eliminar
        view.setOnLongClickListener {
            mostrarDialogoEliminar(id)
            true // Indicar que el evento ha sido manejado correctamente
        }
    }

    private fun mostrarInformacionTarjeta() {
        viewModel.cargarTarjetaDNI(id)
    }

    private fun abrirFormularioModificacion(tarjeta: Tarjeta.TarjetaDNI) {
        val dialogView =
            LayoutInflater.from(requireContext()).inflate(R.layout.dialogo_crear_tarjeta_dni, null)

        // Obtener referencias a las vistas del diálogo
        val editTextNumeroDocumento =
            dialogView.findViewById<EditText>(R.id.editTextNumeroDocumento)
        val editTextFechaNacimiento =
            dialogView.findViewById<EditText>(R.id.editTextFechaNacimiento)
        val editTextFechaExpedicion =
            dialogView.findViewById<EditText>(R.id.editTextFechaExpedicion)
        val editTextFechaCaducidad = dialogView.findViewById<EditText>(R.id.editTextFechaCaducidad)
        val editTextNombre = dialogView.findViewById<EditText>(R.id.editTextNombre)
        val editTextApellidos = dialogView.findViewById<EditText>(R.id.editTextApellidos)
        val spinnerSexo = dialogView.findViewById<Spinner>(R.id.spinnerSexo)
        val editTextNacionalidad = dialogView.findViewById<EditText>(R.id.editTextNacionalidad)
        val editTextLugarNacimiento =
            dialogView.findViewById<EditText>(R.id.editTextLugarNacimiento)
        val editTextDomicilio = dialogView.findViewById<EditText>(R.id.editTextDomicilio)
        val buttonFotografiaUrl = dialogView.findViewById<Button>(R.id.btnSeleccionarFoto)
        val buttonTextFirmaUrl = dialogView.findViewById<Button>(R.id.btnFirma)

        // Asignar los valores de la tarjeta actual a los campos de texto del diálogo
        editTextNumeroDocumento.setText(tarjeta.numeroDocumento)
        editTextFechaNacimiento.setText(formatDate(tarjeta.fechaNacimiento))
        editTextFechaExpedicion.setText(formatDate(tarjeta.fechaExpedicion))
        editTextFechaCaducidad.setText(formatDate(tarjeta.fechaCaducidad))
        editTextNombre.setText(tarjeta.nombre)
        editTextApellidos.setText(tarjeta.apellidos)
        spinnerSexo.setSelection(getIndex(spinnerSexo, tarjeta.sexo))
        editTextNacionalidad.setText(tarjeta.nacionalidad)
        editTextLugarNacimiento.setText(tarjeta.lugarNacimiento)
        editTextDomicilio.setText(tarjeta.domicilio)
        buttonFotografiaUrl.text = tarjeta.fotografiaUrl
        buttonTextFirmaUrl.text = tarjeta.firmaUrl

        val alertDialog = AlertDialog.Builder(requireContext())
            .setTitle("Modificar Tarjeta DNI")
            .setView(dialogView)
            .setPositiveButton("Guardar") { dialog, _ ->
                // Obtener los nuevos valores de los campos de texto
                val nuevoNumeroDocumento = editTextNumeroDocumento.text.toString()
                val nuevoNombre = editTextNombre.text.toString()
                val nuevoApellidos = editTextApellidos.text.toString()
                val nuevoSexo = spinnerSexo.selectedItem as Tarjeta.Sexo
                val nuevaNacionalidad = editTextNacionalidad.text.toString()
                val nuevoLugarNacimiento = editTextLugarNacimiento.text.toString()
                val nuevoDomicilio = editTextDomicilio.text.toString()
                val nuevaFotografiaUrl = buttonFotografiaUrl.text.toString()
                val nuevaFirmaUrl = buttonTextFirmaUrl.text.toString()

                val nuevaFechaNacimiento = editTextFechaNacimiento.text.toString()
                val nuevaFechaExpedicion = editTextFechaExpedicion.text.toString()
                val nuevaFechaCaducidad = editTextFechaCaducidad.text.toString()

                // Crear nueva tarjeta con los valores modificados
                val tarjetaModificada = Tarjeta.TarjetaDNI(
                    tarjeta.id,
                    tarjeta.idUsuario,
                    nuevoNumeroDocumento,
                    parseDate(nuevaFechaNacimiento),
                    parseDate(nuevaFechaExpedicion),
                    parseDate(nuevaFechaCaducidad),
                    nuevoNombre,
                    nuevoApellidos,
                    nuevoSexo,
                    nuevaNacionalidad,
                    nuevoLugarNacimiento,
                    nuevoDomicilio,
                    nuevaFotografiaUrl,
                    nuevaFirmaUrl
                )

                // Llamar al método de modificar tarjeta del ViewModel
                viewModel.modificarTarjetaDNI(tarjeta.id!!, tarjetaModificada)

                dialog.dismiss()
            }
            .setNegativeButton("Cancelar") { dialog, _ ->
                dialog.dismiss()
            }
            .create()

        alertDialog.show()
    }

    // Función auxiliar para obtener el índice de un elemento en un Spinner
    private fun getIndex(spinner: Spinner, value: Tarjeta.Sexo): Int {
        for (i in 0 until spinner.count) {
            val selectedItem = spinner.getItemAtPosition(i) as Tarjeta.Sexo
            if (selectedItem == value) {
                return i
            }
        }
        return 0
    }

    private fun mostrarDialogoEliminar(id: Int) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Eliminar Tarjeta")
        builder.setMessage("¿Estás seguro de que deseas eliminar esta tarjeta? ('Si'/'No')")
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

    private fun parseDate(dateString: String): Date {
        return try {
            SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).parse(dateString)!!
        } catch (e: ParseException) {
            e.printStackTrace()
            Date() // Devuelve una fecha por defecto en caso de error
        }
    }

    private fun formatDate(date: Date): String {
        return SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(date)
    }
}