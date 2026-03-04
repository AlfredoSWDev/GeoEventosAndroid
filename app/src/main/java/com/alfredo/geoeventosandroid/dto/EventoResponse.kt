package com.alfredo.geoeventosandroid.dto

data class EventoResponse(
    val eventId: Int? = null,
    val nombreEvento: String,
    val valorEvento: String,
    val lugarEvento: String,
    val vigenciaEvento: String?,
    val descripcionEvento: String?,
    val fotosEvento: String?,
    val latitud: Double,  // <-- Nuevo
    val longitud: Double  // <-- Nuevo
)
