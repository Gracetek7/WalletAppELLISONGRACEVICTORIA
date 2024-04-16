package com.ieschabas.pmdm.walletapp.ui.tarjetaSIP

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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

class TarjetaSIPFragment : Fragment() {

    private lateinit var tarjeta: Tarjeta.TarjetaSIP
    private lateinit var viewModel: TarjetaSIPViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProvider(this).get(TarjetaSIPViewModel::class.java)

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

    private fun abrirFormularioModificacion(tarjeta: Tarjeta.TarjetaSIP) {
        TODO("Not yet implemented")
    }

    private fun mostrarInformacionTarjeta() {
        viewModel.cargarTarjetaSIP(id)
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
        viewModel.eliminarTarjetaSIP(id)
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