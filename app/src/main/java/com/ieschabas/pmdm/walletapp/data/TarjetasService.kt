package com.ieschabas.pmdm.walletapp.data


import com.ieschabas.pmdm.walletapp.model.Usuario
import com.ieschabas.pmdm.walletapp.model.tarjetas.Tarjeta
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface TarjetasService {

    // Usuario
    @POST("syncUserData")
    suspend fun syncUserData(
        @Body userData: Map<String, String>
    ): Response<Unit>

    @GET("GETusuario/{id}")
    suspend fun getUsuario(@Path("id") id: String): Response<Usuario>

    @PUT("PUTusuario/{id}")
    suspend fun modificarUsuario(@Path("id") id: String, @Body usuario: Usuario): Response<Void>

    @DELETE("DELETEusuario/{id}")
    suspend fun eliminarUsuario(@Path("id") id: String): Response<Void>

    // Rutas para recoger las tarjetas asociadas con el ID del usuario
    @GET("/GETtarjetaDNIUsuario/{idUsuario}")
    suspend fun getTarjetaDNIUsuario(@Path("idUsuario") idUsuario: String): Response<Tarjeta.TarjetaDNI>

    @GET("GETtarjetaSIPUsuario/{idUsuario}")
    suspend fun getTarjetaSIPUsuario(@Path("idUsuario") idUsuario: String): Response<Tarjeta.TarjetaSIP>

    @GET("GETpermisoCirculacionUsuario/{idUsuario}")
    suspend fun getPermisoCirculacionUsuario(@Path("idUsuario") idUsuario: String): Response<Tarjeta.TarjetaPermisoCirculacion>

    // Tarjeta DNI
    @GET("GETtarjetaDNI/{id}")
    suspend fun getTarjetaDNI(@Path("id") id: Int): Response<Tarjeta.TarjetaDNI>

    @POST("POSTtarjetaDNI")
    suspend fun insertarTarjetaDNI(@Body tarjetaDNI: Tarjeta.TarjetaDNI): Response<Void>

    @PUT("PUTtarjetaDNI/{id}")
    suspend fun modificarTarjetaDNI(@Path("id") id: Int): Response<Void>

    @DELETE("DELETEtarjetaDNI/{id}")
    suspend fun eliminarTarjetaDNI(@Path("id") id: Int): Response<Void>

    // Tarjeta SIP
    @GET("GETtarjetaSIP/{id}")
    suspend fun getTarjetaSIP(@Path("id") id: Int): Response<Tarjeta.TarjetaSIP>

    @POST("POSTtarjetaSIP")
    suspend fun insertarTarjetaSIP(@Body tarjetaSIP: Tarjeta.TarjetaSIP): Response<Void>

    @PUT("PUTtarjetaSIP/{id}")
    suspend fun modificarTarjetaSIP(@Path("id") id: Int, @Body tarjetaSIP: Tarjeta.TarjetaSIP): Response<Void>

    @DELETE("DELETEtarjetaSIP/{id}")
    suspend fun eliminarTarjetaSIP(@Path("id") id: Int): Response<Void>

    // Tarjeta Permiso de Circulación
    @GET("GETpermisoCirculacion/{id}")
    suspend fun getPermisoCirculacion(@Path("id") id: String): Response<Tarjeta.TarjetaPermisoCirculacion>

    @POST("POSTpermisoCirculacion")
    suspend fun insertarPermisoCirculacion(@Body permisoCirculacion: Tarjeta.TarjetaPermisoCirculacion): Response<Void>

    @PUT("PUTpermisoCirculacion/{id}")
    suspend fun modificarPermisoCirculacion(@Path("id") id: String, @Body permisoCirculacion: Tarjeta.TarjetaPermisoCirculacion): Response<Void>

    @DELETE("DELETEpermisoCirculacion/{id}")
    suspend fun eliminarPermisoCirculacion(@Path("id") id: String): Response<Void>
}
