package com.ieschabas.pmdm.walletapp.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "usuario")
data class Usuario(
    @PrimaryKey
    val id: String,
    val nombre: String,
    val apellidos: String,
    @ColumnInfo(name = "correo_electronico")
    val correo_electronico: String,
    val contrasena: String
)