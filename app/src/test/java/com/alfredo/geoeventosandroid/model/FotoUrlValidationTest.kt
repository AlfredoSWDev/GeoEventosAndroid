package com.alfredo.geoeventosandroid.model

import com.alfredo.geoeventosandroid.dto.EventoResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

/**
 * Tests para la lógica de validación de URL de foto en el ViewModel / UI.
 *
 * La condición evaluada es la misma que usa PantallaMapaEventos para
 * decidir si renderiza AsyncImage:
 *   !evento.fotosEvento.isNullOrBlank() && evento.fotosEvento.startsWith("http")
 *
 * Ubicación: app/src/test/java/com/alfredo/geoeventosandroid/model/FotoUrlValidationTest.kt
 *
 * ── Por qué se necesita setMain ──────────────────────────────────────────────
 * Los 3 tests de integración instancian EventosViewModel directamente.
 * Su bloque init llama a cargarEventos() → viewModelScope.launch → Dispatchers.Main.
 * En JVM puro no existe el MainLooper de Android, así que hay que sustituirlo
 * con un dispatcher de test ANTES de crear el ViewModel, y restaurarlo al final.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class FotoUrlValidationTest {

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    // ── Helper ─────────────────────────────────────────────────────────────

    private fun evento(foto: String?) = EventoResponse(
        nombreEvento = "Test", valorEvento = "Gratis",
        lugarEvento  = "Santiago", latitud = -33.45, longitud = -70.65,
        fotosEvento  = foto
    )

    /** Replica exacta de la condición en PantallaMapaEventos */
    private fun debeMostrarFoto(evento: EventoResponse): Boolean =
        !evento.fotosEvento.isNullOrBlank() && evento.fotosEvento!!.startsWith("http")

    // ── URLs válidas ───────────────────────────────────────────────────────

    @Test
    fun `url https es mostrada`() {
        assertTrue(debeMostrarFoto(evento("https://cdn.example.com/foto.jpg")))
    }

    @Test
    fun `url http es mostrada`() {
        assertTrue(debeMostrarFoto(evento("http://cdn.example.com/foto.jpg")))
    }

    @Test
    fun `url https con path largo es mostrada`() {
        assertTrue(debeMostrarFoto(evento("https://storage.googleapis.com/bucket/eventos/2025/foto.png")))
    }

    @Test
    fun `url https con query params es mostrada`() {
        assertTrue(debeMostrarFoto(evento("https://cdn.example.com/foto.jpg?w=300&h=200")))
    }

    // ── URLs inválidas ─────────────────────────────────────────────────────

    @Test
    fun `url null no es mostrada`() {
        assertFalse(debeMostrarFoto(evento(null)))
    }

    @Test
    fun `url blank no es mostrada`() {
        assertFalse(debeMostrarFoto(evento("   ")))
    }

    @Test
    fun `url vacia no es mostrada`() {
        assertFalse(debeMostrarFoto(evento("")))
    }

    @Test
    fun `url relativa no es mostrada`() {
        assertFalse(debeMostrarFoto(evento("uploads/foto.jpg")))
    }

    @Test
    fun `url sin protocolo no es mostrada`() {
        assertFalse(debeMostrarFoto(evento("cdn.example.com/foto.jpg")))
    }

    @Test
    fun `url ftp no es mostrada`() {
        assertFalse(debeMostrarFoto(evento("ftp://cdn.example.com/foto.jpg")))
    }

    @Test
    fun `url solo con espacios no es mostrada`() {
        assertFalse(debeMostrarFoto(evento("     ")))
    }

    // ── Integración con EventosViewModel ──────────────────────────────────
    // setMain ya está activo desde @Before, por lo que viewModelScope
    // puede crearse sin el MainLooper de Android.

    @Test
    fun `evento seleccionado con foto valida muestra imagen`() {
        val vm = object : EventosViewModel() {}
        vm.seleccionarEvento(evento("https://cdn.example.com/foto.jpg"))

        val foto = vm.eventoSeleccionado?.fotosEvento
        assertTrue(!foto.isNullOrBlank() && foto!!.startsWith("http"))
    }

    @Test
    fun `evento seleccionado sin foto no muestra imagen`() {
        val vm = object : EventosViewModel() {}
        vm.seleccionarEvento(evento(null))

        val foto = vm.eventoSeleccionado?.fotosEvento
        assertFalse(!foto.isNullOrBlank() && foto != null && foto.startsWith("http"))
    }

    @Test
    fun `cerrar detalle limpia foto seleccionada`() {
        val vm = object : EventosViewModel() {}
        vm.seleccionarEvento(evento("https://cdn.example.com/foto.jpg"))
        vm.cerrarDetalle()

        assertNull(vm.eventoSeleccionado?.fotosEvento)
    }
}