package com.ieschabas.pmdm.walletapp.data

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class TarjetasApi {

    val retrofitService: TarjetasService = makeRetrofitService()
    // Conexion Retrofit

    private fun makeRetrofitService(): TarjetasService {
        return Retrofit.Builder()
            .baseUrl("http://10.0.2.2:8080/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(TarjetasService::class.java)
    }

}