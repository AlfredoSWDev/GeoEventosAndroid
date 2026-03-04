package com.alfredo.geoeventosandroid.model;

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.alfredo.geoeventosandroid.client.RetrofitClient
import com.alfredo.geoeventosandroid.dto.EventoResponse
import kotlinx.coroutines.launch

class EventosViewModel : ViewModel() {
    var eventos by mutableStateOf<List<EventoResponse>>(emptyList())
    private set

    var eventoSeleccionado by mutableStateOf<EventoResponse?>(null)  // ← nuevo
    private set

    init {
        cargarEventos()
    }


    fun cargarEventos(busqueda: String? = null) {
        viewModelScope.launch {
            try {
                eventos = RetrofitClient.instance.listarEventos(busqueda)
            } catch (e: Exception) {
                // Manejar error (ej. sin internet)
            }
        }
    }

    // Seleccionar evento al tocar el marcador
    fun seleccionarEvento(evento: EventoResponse) {         // ← nuevo
        eventoSeleccionado = evento
    }

    fun cerrarDetalle() {                                   // ← nuevo
        eventoSeleccionado = null
    }
}