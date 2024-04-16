package com.ieschabas.pmdm.walletapp.model.tarjetas

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import java.util.Date


sealed class Tarjeta {

    @Entity(tableName = "tarjeta_dni")
    data class TarjetaDNI(
        @PrimaryKey(autoGenerate = true)
        val id: Int? = null,
        @SerializedName("id_usuario")
        val idUsuario: String,
        @SerializedName("numero_documento")
        val numeroDocumento: String,
        @SerializedName("fecha_nacimiento")
        var fechaNacimiento: Date,
        @SerializedName("fecha_expedicion")
        var fechaExpedicion: Date,
        @SerializedName("fecha_caducidad")
        var fechaCaducidad: Date,
        @SerializedName("nombre")
        val nombre: String,
        @SerializedName("apellidos")
        val apellidos: String,
        @SerializedName("sexo")
        val sexo: Sexo,
        @SerializedName("nacionalidad")
        val nacionalidad: String,
        @SerializedName("lugar_nacimiento")
        val lugarNacimiento: String,
        @SerializedName("domicilio")
        val domicilio: String,
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
        val numeroSip: String,
        @SerializedName("digito_control")
        val digitoControl: String,
        @SerializedName("codigo_identificacion_territorial")
        val codigoIdentificacionTerritorial: String,
        @SerializedName("datos_identificacion")
        val datosIdentificacion: String,
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
        @ColumnInfo(name = "id_usuario")
        val idUsuario: String,
        @ColumnInfo(name = "fecha_matriculacion")
        val fechaMatriculacion: Date,
        @ColumnInfo(name = "apellido_razon_social")
        val apellidoRazonSocial: String,
        val nombre: String,
        val domicilio: String,
        @ColumnInfo(name = "marca_vehiculo")
        val marcaVehiculo: String,
        @ColumnInfo(name = "tipo_variante_version")
        val tipoVarianteVersion: String,
        @ColumnInfo(name = "denominacion_comercial")
        val denominacionComercial: String,
        @ColumnInfo(name = "servicio_destinado")
        val servicioDestinado: String,
        @ColumnInfo(name = "numero_identificacion")
        val numeroIdentificacion: String,
        @ColumnInfo(name = "masa_maxima")
        val masaMaxima: Int,
        @ColumnInfo(name = "numero_homologacion")
        val numeroHomologacion: String,
        val cilindrada: Int,
        @ColumnInfo(name = "potencia_neta_maxima")
        val potenciaNetaMaxima: Int,
        @ColumnInfo(name = "tipo_combustible")
        val tipoCombustible: String,
        @ColumnInfo(name = "num_plazas_asiento")
        val numPlazasAsiento: Int,
        @ColumnInfo(name = "num_plazas_pie")
        val numPlazasPie: Int,
        @ColumnInfo(name = "relacion_potencia_peso")
        val relacionPotenciaPeso: Float? = null
    ): Tarjeta()

    enum class Sexo {
        MASCULINO,
        FEMENINO
    }
}