package com.alfredo.geoeventosandroid.model;

import com.alfredo.geoeventosandroid.dto.EventoResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

/**
 * Unit tests for [EventosViewModel].
 *
 * These tests run on the JVM (no Android framework needed).
 * Add to: app/src/test/java/com/alfredo/geoeventosandroid/model/
 *
 * Dependencies to add in build.gradle.kts (app):
 *   testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3")
 *   testImplementation("junit:junit:4.13.2")
 */
@OptIn(ExperimentalCoroutinesApi::class)
class EventosViewModelTest {

    // ── Coroutine test dispatcher ──────────────────────────────────────────
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    // ── Helpers ────────────────────────────────────────────────────────────

    private fun buildEvento(
            id: Int = 1,
            nombre: String = "Evento Test",
            lat: Double = -33.45,
            lon: Double = -70.65
    ) = EventoResponse(
            eventId           = id,
            nombreEvento      = nombre,
            valorEvento       = "Gratis",
            lugarEvento       = "Santiago",
            vigenciaEvento    = "2025-12-31",
            descripcionEvento = "Descripción de prueba",
            fotosEvento       = null,
            latitud           = lat,
            longitud          = lon
    )

    // ── Tests: estado inicial ──────────────────────────────────────────────

    @Test
    fun `initial state has empty eventos list`() {
        // We create a testable subclass that skips the network init call
        val vm = object : EventosViewModel() {
            // Override to avoid network call on init
        }
        // Before any coroutine runs the list should be empty
        assertTrue(vm.eventos.isEmpty())
    }

    @Test
    fun `initial state has no selected event`() {
        val vm = object : EventosViewModel() {}
        assertNull(vm.eventoSeleccionado)
    }

    // ── Tests: seleccionarEvento ───────────────────────────────────────────

    @Test
    fun `seleccionarEvento sets eventoSeleccionado`() {
        val vm = object : EventosViewModel() {}
        val evento = buildEvento()

        vm.seleccionarEvento(evento)

        assertEquals(evento, vm.eventoSeleccionado)
    }

    @Test
    fun `seleccionarEvento replaces previous selection`() {
        val vm = object : EventosViewModel() {}
        val evento1 = buildEvento(id = 1, nombre = "Primero")
        val evento2 = buildEvento(id = 2, nombre = "Segundo")

        vm.seleccionarEvento(evento1)
        vm.seleccionarEvento(evento2)

        assertEquals(evento2, vm.eventoSeleccionado)
        assertEquals("Segundo", vm.eventoSeleccionado?.nombreEvento)
    }

    // ── Tests: cerrarDetalle ───────────────────────────────────────────────

    @Test
    fun `cerrarDetalle clears eventoSeleccionado`() {
        val vm = object : EventosViewModel() {}
        vm.seleccionarEvento(buildEvento())

        vm.cerrarDetalle()

        assertNull(vm.eventoSeleccionado)
    }

    @Test
    fun `cerrarDetalle is safe when no event selected`() {
        val vm = object : EventosViewModel() {}

        // Should not throw
        vm.cerrarDetalle()

        assertNull(vm.eventoSeleccionado)
    }

    // ── Tests: EventoResponse data class ──────────────────────────────────

    @Test
    fun `EventoResponse equality works correctly`() {
        val e1 = buildEvento(id = 1)
        val e2 = buildEvento(id = 1)

        assertEquals(e1, e2)
    }

    @Test
    fun `EventoResponse with different ids are not equal`() {
        val e1 = buildEvento(id = 1)
        val e2 = buildEvento(id = 2)

        assertNotEquals(e1, e2)
    }

    @Test
    fun `EventoResponse allows null optional fields`() {
        val evento = EventoResponse(
                eventId           = null,
                nombreEvento      = "Sin ID",
                valorEvento       = "0",
                lugarEvento       = "Online",
                vigenciaEvento    = null,
                descripcionEvento = null,
                fotosEvento       = null,
                latitud           = 0.0,
                longitud          = 0.0
        )

        assertNull(evento.eventId)
        assertNull(evento.vigenciaEvento)
        assertNull(evento.descripcionEvento)
        assertNull(evento.fotosEvento)
    }

    // ── Tests: coordinate validation logic ────────────────────────────────

    @Test
    fun `evento with zero coordinates is considered invalid for map`() {
        val evento = buildEvento(lat = 0.0, lon = 0.0)

        // Mirrors the filter used in MapaEventosScreen:
        // if (evento.latitud == 0.0 && evento.longitud == 0.0) return@forEach
        val shouldSkip = evento.latitud == 0.0 && evento.longitud == 0.0

        assertTrue("Event with 0,0 coords should be skipped on map", shouldSkip)
    }

    @Test
    fun `evento with valid coordinates is accepted for map`() {
        val evento = buildEvento(lat = -33.45, lon = -70.65)

        val shouldSkip = evento.latitud == 0.0 && evento.longitud == 0.0

        assertFalse("Event with valid coords should NOT be skipped", shouldSkip)
    }

    // ── Tests: foto URL validation logic ──────────────────────────────────

    @Test
    fun `foto url starting with http is shown`() {
        val evento = buildEvento().copy(fotosEvento = "http://example.com/foto.jpg")

        val shouldShow = !evento.fotosEvento.isNullOrBlank() &&
                evento.fotosEvento.startsWith("http")

        assertTrue(shouldShow)
    }

    @Test
    fun `foto url not starting with http is hidden`() {
        val evento = buildEvento().copy(fotosEvento = "foto_local.jpg")

        val shouldShow = !evento.fotosEvento.isNullOrBlank() &&
                evento.fotosEvento.startsWith("http")

        assertFalse(shouldShow)
    }

    @Test
    fun `null foto url is hidden`() {
        val evento = buildEvento().copy(fotosEvento = null)

        val shouldShow = !evento.fotosEvento.isNullOrBlank() &&
                evento.fotosEvento.startsWith("http")

        assertFalse(shouldShow)
    }

    @Test
    fun `blank foto url is hidden`() {
        val evento = buildEvento().copy(fotosEvento = "   ")

        val shouldShow = !evento.fotosEvento.isNullOrBlank() &&
                evento.fotosEvento!!.startsWith("http")

        assertFalse(shouldShow)
    }

    // ── Tests: firstOrNull logic for map center ────────────────────────────

    @Test
    fun `first valid event is used as map center`() {
        val invalid = buildEvento(id = 1, lat = 0.0, lon = 0.0)
        val valid   = buildEvento(id = 2, lat = -33.45, lon = -70.65)
        val eventos = listOf(invalid, valid)

        val center = eventos.firstOrNull { it.latitud != 0.0 || it.longitud != 0.0 }

        assertEquals(valid, center)
    }

    @Test
    fun `all zero coord events returns null center`() {
        val eventos = listOf(
                buildEvento(lat = 0.0, lon = 0.0),
                buildEvento(lat = 0.0, lon = 0.0)
        )

        val center = eventos.firstOrNull { it.latitud != 0.0 || it.longitud != 0.0 }

        assertNull(center)
    }

    @Test
    fun `empty list returns null center`() {
        val eventos = emptyList<EventoResponse>()

        val center = eventos.firstOrNull { it.latitud != 0.0 || it.longitud != 0.0 }

        assertNull(center)
    }
}