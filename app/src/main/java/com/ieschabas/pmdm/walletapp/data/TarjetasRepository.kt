package com.ieschabas.pmdm.walletapp.data

import android.util.Log
import com.ieschabas.pmdm.walletapp.model.Usuario
import com.ieschabas.pmdm.walletapp.model.tarjetas.Tarjeta
import retrofit2.Response

class TarjetasRepository(tarjetasApi: TarjetasApi) {

    private val errorApi = "Error API"
    private val tarjetaService = tarjetasApi.retrofitService

    suspend fun syncUserData(userData: Map<String, String>): Response<Unit>? {
        return try {
            Log.d(errorApi, "Llamando a syncUserData")
            tarjetaService.syncUserData(userData)
        } catch (e: Exception) {
            Log.e(errorApi, "Error al sincronizar datos del usuario: ${e.message}")
            null
        }
    }

    suspend fun obtenerTarjetasUsuario(idUsuario: String): List<Tarjeta> {
        Log.d("TarjetasRepository", "ID de usuario recibido: $idUsuario")
        val tarjetas: MutableList<Tarjeta> = mutableListOf()

        try {
            Log.d("TarjetasRepository", "Haciendo el get de tarjetas a TarjetaService con el ID del usuario recibido: $idUsuario")

            // Obtener tarjeta DNI
            val responseDNI = tarjetaService.getTarjetaDNIUsuario(idUsuario)
            if (responseDNI.isSuccessful) {
                val tarjetaDNI = responseDNI.body()
                tarjetaDNI?.let { dni ->

                    Log.d("TarjetasRepository", "ID de usuario recibido en el let: $idUsuario")
                    tarjetas.add(dni)

                } ?: Log.e(errorApi, "La respuesta del servidor para la tarjeta DNI es nula")
            } else {
                val errorMessage = responseDNI.errorBody()?.string() ?: responseDNI.message() ?: "Response nula"
                Log.e(errorApi, "Error al obtener la tarjeta DNI del usuario: $errorMessage")
            }

            // Obtener tarjeta SIP
            val responseSIP = tarjetaService.getTarjetaSIPUsuario(idUsuario)
            if (responseSIP.isSuccessful) {
                val tarjetaSIP = responseSIP.body()
                tarjetaSIP?.let { sip ->
                    tarjetas.add(sip)
                } ?: Log.e(errorApi, "La respuesta del servidor para la tarjeta SIP es nula")
            } else if (responseSIP.code() != 404) {
                Log.e(errorApi, "Error al obtener la tarjeta SIP del usuario: ${responseSIP.message()}")
            }

            // Obtener tarjeta Permiso de Circulación
            val responsePermiso = tarjetaService.getPermisoCirculacionUsuario(idUsuario)
            if (responsePermiso.isSuccessful) {
                val tarjetaPermiso = responsePermiso.body()
                tarjetaPermiso?.let { permiso ->
                    tarjetas.add(permiso)
                } ?: Log.e(errorApi, "La respuesta del servidor para el Permiso de Circulación es nula")
            } else if (responsePermiso.code() != 404) {
                Log.e(errorApi, "Error al obtener el Permiso de Circulación del usuario: ${responsePermiso.message()}")
            }

        } catch (e: Exception) {
            Log.e(errorApi, "Error al obtener las tarjetas del usuario: ${e.message}")
            throw e
        }

        Log.d("TarjetasRepository", "Tarjetas devueltas: $tarjetas")
        return tarjetas
    }

    suspend fun getUsuario(id: String): Response<Usuario>? {
        try {
            return tarjetaService.getUsuario(id)
        } catch (e: Exception) {
            Log.e(errorApi, "Error en getUsuario: ${e.message}")
        }
        return null
    }


    suspend fun modificarUsuario(id: String, usuario: Usuario): Response<Void>? {
        try {
            return tarjetaService.modificarUsuario(id, usuario)
        } catch (e: Exception) {
            Log.e(errorApi, "Error en modificar Usuario: ${e.message}")
        }
        return null
    }

    suspend fun eliminarUsuario(id: String): Response<Void>? {
        try {
            return tarjetaService.eliminarUsuario(id)
        } catch (e: Exception) {
            Log.e(errorApi, "Error en eliminar Usuario: ${e.message}")
        }
        return null
    }

    suspend fun obtenerTarjetaDNIUsuario(idUsuario: String): List<Tarjeta.TarjetaDNI> {
        Log.d("TarjetasRepository", "ID de usuario recibido: $idUsuario")
        val tarjetas: MutableList<Tarjeta.TarjetaDNI> = mutableListOf()
        try {
            // Obtener tarjeta SIP
            val responseSIP = tarjetaService.getTarjetaDNIUsuario(idUsuario)
            if (responseSIP.isSuccessful) {
                val tarjetaDNI = responseSIP.body()
                tarjetaDNI?.let { dni ->
                    tarjetas.add(dni)
                } ?: Log.e(errorApi, "La respuesta del servidor para la tarjeta DNI es nula")
            } else if (responseSIP.code() != 404) {
                Log.e(errorApi, "Error al obtener la tarjeta DNI del usuario: ${responseSIP.message()}")
            }

        } catch (e: Exception) {
            Log.e(errorApi, "Error al obtener las tarjetas DNI del usuario: ${e.message}")
        }
        return tarjetas
    }


    //metodo actualizar

    suspend fun getTarjetaDNI(id: Int): Response<Tarjeta.TarjetaDNI>? {
        try {
            return tarjetaService.getTarjetaDNI(id)
        } catch (e: Exception) {
            Log.e(errorApi, "Error en getTarjetaDNI: ${e.message}")
        }
        return null
    }

    suspend fun insertarTarjetaDNI(tarjetaDNI: Tarjeta.TarjetaDNI): Response<Void>? {
        try {
            return tarjetaService.insertarTarjetaDNI(tarjetaDNI)
        } catch (e: Exception) {
            Log.e(errorApi, "Error en insertarTarjeta DNI: ${e.message}")
        }
        return null
    }

    suspend fun modificarTarjetaDNI(id: Int): Response<Void>? {
        try {
            return tarjetaService.modificarTarjetaDNI(id)
        } catch (e: Exception) {
            Log.e(errorApi, "Error en modificarTarjeta DNI: ${e.message}")
        }
        return null
    }

    suspend fun eliminarTarjetaDNI(id: Int): Response<Void>? {
        try {
            return tarjetaService.eliminarTarjetaDNI(id)
        } catch (e: Exception) {
            Log.e(errorApi, "Error al eliminar Tarjeta DNI: ${e.message}")
        }
        return null
    }

    suspend fun obtenerTarjetaSIPUsuario(idUsuario: String): List<Tarjeta.TarjetaSIP> {
        Log.d("TarjetasRepository", "ID de usuario recibido: $idUsuario")
        val tarjetas: MutableList<Tarjeta.TarjetaSIP> = mutableListOf()
        try {
            // Obtener tarjeta SIP
            val responseSIP = tarjetaService.getTarjetaSIPUsuario(idUsuario)
            if (responseSIP.isSuccessful) {
                val tarjetaSIP = responseSIP.body()
                tarjetaSIP?.let { sip ->
                    tarjetas.add(sip)
                } ?: Log.e(errorApi, "La respuesta del servidor para la tarjeta SIP es nula")
            } else if (responseSIP.code() != 404) {
                Log.e(errorApi, "Error al obtener la tarjeta SIP del usuario: ${responseSIP.message()}")
            }

        } catch (e: Exception) {
            Log.e(errorApi, "Error al obtener las tarjetas SIP del usuario: ${e.message}")
        }
        return tarjetas
    }


    suspend fun getTarjetaSIP(id: Int): Response<Tarjeta.TarjetaSIP>? {
        try {
            return tarjetaService.getTarjetaSIP(id)
        } catch (e: Exception) {
            Log.e(errorApi, "Error en getTarjetaSIP: ${e.message}")
        }
        return null
    }

    suspend fun insertarTarjetaSIP(tarjetaSIP: Tarjeta.TarjetaSIP): Response<Void>? {
        try {
            return tarjetaService.insertarTarjetaSIP(tarjetaSIP)
        } catch (e: Exception) {
            Log.e(errorApi, "Error al insertar Tarjeta SIP: ${e.message}")
        }
        return null
    }

    suspend fun modificarTarjetaSIP(id: Int,tarjetaSIP: Tarjeta.TarjetaSIP): Response<Void>? {
        try {
            return tarjetaService.modificarTarjetaSIP(id,tarjetaSIP)
        } catch (e: Exception) {
            Log.e(errorApi, "Error al modificar Tarjeta SIP: ${e.message}")
        }
        return null
    }

    suspend fun eliminarTarjetaSIP(id: Int): Response<Void>? {
        try {
            return tarjetaService.eliminarTarjetaSIP(id)
        } catch (e: Exception) {
            Log.e(errorApi, "Error al eliminar Tarjeta SIP: ${e.message}")
        }
        return null
    }


    suspend fun getPermisoCirculacion(id: String): Response<Tarjeta.TarjetaPermisoCirculacion>? {
        try {
            return tarjetaService.getPermisoCirculacion(id)
        } catch (e: Exception) {
            Log.e(errorApi, "Error en getPermisoCirculacion: ${e.message}")
        }
        return null
    }

    suspend fun insertarPermisoCirculacion(permisoCirculacion: Tarjeta.TarjetaPermisoCirculacion): Response<Void>? {
        try {
            return tarjetaService.insertarPermisoCirculacion(permisoCirculacion)
        } catch (e: Exception) {
            Log.e(errorApi, "Error en insertarPermisoCirculacion: ${e.message}")
        }
        return null
    }

    suspend fun modificarPermisoCirculacion(id: String, permisoCirculacion: Tarjeta.TarjetaPermisoCirculacion): Response<Void>? {
        try {
            return tarjetaService.modificarPermisoCirculacion(id, permisoCirculacion)
        } catch (e: Exception) {
            Log.e(errorApi, "Error en modificarPermisoCirculacion: ${e.message}")
        }
        return null
    }

    suspend fun eliminarPermisoCirculacion(id: String): Response<Void>? {
        try {
            return tarjetaService.eliminarPermisoCirculacion(id)
        } catch (e: Exception) {
            Log.e(errorApi, "Error en eliminarPermisoCirculacion: ${e.message}")
        }
        return null
    }
}
