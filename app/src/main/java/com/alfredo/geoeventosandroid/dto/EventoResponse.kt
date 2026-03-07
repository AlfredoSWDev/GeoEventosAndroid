package com.alfredo.geoeventosandroid.dto

data class EventoResponse(
    val eventId: Int? = null,
    val nombreEvento: String,
    val valorEvento: String,
    val lugarEvento: String,
    val vigenciaEvento: String? = null,
    val descripcionEvento: String? = null,
    val fotosEvento: String? = null,
    val latitud: Double,
    val longitud: Double
)
