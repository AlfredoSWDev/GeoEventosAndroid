package com.alfredo.geoeventosandroid.service

import com.alfredo.geoeventosandroid.dto.ImageResponse
import com.alfredo.geoeventosandroid.dto.EventoResponse
import okhttp3.MultipartBody
import retrofit2.http.*

interface EventoApiService {
    @GET("api/eventos")
    suspend fun listarEventos(@Query("q") query: String? = null): List<EventoResponse>

    @GET("api/eventos/{id}")
    suspend fun obtenerEvento(@Path("id") id: Int): EventoResponse

    @POST("api/eventos")
    suspend fun crearEvento(@Body evento: EventoResponse): EventoResponse

    @PUT("api/eventos/{id}")
    suspend fun actualizarEvento(@Path("id") id: Int, @Body evento: EventoResponse): EventoResponse

    @DELETE("api/eventos/{id}")
    suspend fun eliminarEvento(@Path("id") id: Int)

    // Subida de imagen
    @Multipart
    @POST("api/imagenes/subir")
    suspend fun subirImagen(@Part archivo: MultipartBody.Part): ImageResponse
}