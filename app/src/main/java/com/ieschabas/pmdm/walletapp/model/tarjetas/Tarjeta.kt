package com.ieschabas.pmdm.walletapp.model.tarjetas

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import java.io.Serializable
import java.util.Date


sealed class Tarjeta {

    @Entity(tableName = "tarjeta_dni")
    data class TarjetaDNI(
        @PrimaryKey(autoGenerate = true)
        val id: Int? = null,
        @SerializedName("id_usuario")
        val idUsuario: String,
        @SerializedName("numero_documento")
        var numeroDocumento: String,
        @SerializedName("fecha_nacimiento")
        var fechaNacimiento: Date,
        @SerializedName("fecha_expedicion")
        var fechaExpedicion: Date,
        @SerializedName("fecha_caducidad")
        var fechaCaducidad: Date,
        @SerializedName("nombre")
        var nombre: String,
        @SerializedName("apellidos")
        var apellidos: String,
        @SerializedName("sexo")
        var sexo: Sexo,
        @SerializedName("nacionalidad")
        var nacionalidad: String,
        @SerializedName("lugar_nacimiento")
        var lugarNacimiento: String,
        @SerializedName("domicilio")
        var domicilio: String,
        @SerializedName("fotografia_url")
        val fotografiaUrl: String,
        @SerializedName("firma_url")
        val firmaUrl: String
    ) : Tarjeta()

    @Entity(tableName = "tarjeta_sip")
    data class TarjetaSIP(
        @PrimaryKey(autoGenerate = true)
        val id: Int = 0,
        @SerializedName("id_usuario")
        val idUsuario: String,
        @SerializedName("numero_sip")
        var numeroSip: String,
        @SerializedName("digito_control")
        var digitoControl: String,
        @SerializedName("codigo_identificacion_territorial")
        var codigoIdentificacionTerritorial: String,
        @SerializedName("datos_identificacion")
        var datosIdentificacion: String,
        @SerializedName("codigo_sns")
        val codigoSns: String,
        @SerializedName("fecha_emision")
        val fechaEmision: Date,
        @SerializedName("fecha_caducidad")
        val fechaCaducidad: Date,
        @SerializedName("telefono_urgencias")
        val telefonoUrgencias: String,
        @SerializedName("numero_seguridad_social")
        val numeroSeguridadSocial: String,
        @SerializedName("centro_medico")
        val centroMedico: String,
        @SerializedName("medico_asignado")
        val medicoAsignado: String,
        @SerializedName("enfermera_asignada")
        val enfermeraAsignada: String,
        @SerializedName("telefonos_urgencias_cita_previa")
        val telefonosUrgenciasCitaPrevia: String,
        @SerializedName("apellidos_nombre")
        val apellidosNombre: String
    ) : Tarjeta()

    @Entity(tableName = "tarjeta_permiso_circulacion")
    data class TarjetaPermisoCirculacion(
        @PrimaryKey(autoGenerate = true)
        val id: Int = 0,
        @SerializedName("id_usuario")
        val idUsuario: String,
        @SerializedName("fecha_matriculacion")
        val fechaMatriculacion: Date,
        @SerializedName("apellido_razon_social")
        val apellidoRazonSocial: String,
        val nombre: String,
        val domicilio: String,
        @SerializedName("marca_vehiculo")
        val marcaVehiculo: String,
        @SerializedName("tipo_variante_version")
        val tipoVarianteVersion: String,
        @SerializedName("denominacion_comercial")
        val denominacionComercial: String,
        @SerializedName("servicio_destinado")
        val servicioDestinado: String,
        @SerializedName("numero_identificacion")
        val numeroIdentificacion: String,
        @SerializedName("masa_maxima")
        val masaMaxima: Int,
        @SerializedName("numero_homologacion")
        val numeroHomologacion: String,
        val cilindrada: Int,
        @SerializedName("potencia_neta_maxima")
        val potenciaNetaMaxima: Int,
        @SerializedName("tipo_combustible")
        val tipoCombustible: String,
        @SerializedName("num_plazas_asiento")
        val numPlazasAsiento: Int,
        @SerializedName("num_plazas_pie")
        val numPlazasPie: Int,
        @SerializedName("relacion_potencia_peso")
        val relacionPotenciaPeso: Float? = null
    ): Tarjeta()

    enum class Sexo {
        MASCULINO,
        FEMENINO
    }
}