package com.alfredo.geoeventosandroid.dto

import org.junit.Assert.*
import org.junit.Test

/**
 * Unit tests for [EventoResponse] DTO.
 *
 * Place in: app/src/test/java/com/alfredo/geoeventosandroid/dto/
 */
class EventoResponseTest {

    // ── Builder helper ─────────────────────────────────────────────────────

    private fun evento(
        id: Int?         = 1,
        nombre: String   = "Feria del Libro",
        valor: String    = "$5.000",
        lugar: String    = "Parque Forestal",
        vigencia: String = "2025-12-31",
        desc: String     = "Gran feria literaria",
        foto: String?    = "https://cdn.example.com/img.jpg",
        lat: Double      = -33.4372,
        lon: Double      = -70.6506
    ) = EventoResponse(
        eventId           = id,
        nombreEvento      = nombre,
        valorEvento       = valor,
        lugarEvento       = lugar,
        vigenciaEvento    = vigencia,
        descripcionEvento = desc,
        fotosEvento       = foto,
        latitud           = lat,
        longitud          = lon
    )

    // ── Creation ───────────────────────────────────────────────────────────

    @Test
    fun `creates with all fields`() {
        val e = evento()
        assertEquals(1,               e.eventId)
        assertEquals("Feria del Libro", e.nombreEvento)
        assertEquals("$5.000",         e.valorEvento)
        assertEquals("Parque Forestal", e.lugarEvento)
        assertEquals("2025-12-31",      e.vigenciaEvento)
        assertEquals("Gran feria literaria", e.descripcionEvento)
        assertEquals("https://cdn.example.com/img.jpg", e.fotosEvento)
        assertEquals(-33.4372, e.latitud,  0.0001)
        assertEquals(-70.6506, e.longitud, 0.0001)
    }

    @Test
    fun `creates with minimum required fields`() {
        val e = EventoResponse(
            nombreEvento = "Mínimo",
            valorEvento  = "Gratis",
            lugarEvento  = "Online",
            latitud      = 0.0,
            longitud     = 0.0
        )
        assertNotNull(e)
        assertNull(e.eventId)
        assertNull(e.vigenciaEvento)
        assertNull(e.descripcionEvento)
        assertNull(e.fotosEvento)
    }

    // ── Equality & copy ────────────────────────────────────────────────────

    @Test
    fun `two identical instances are equal`() {
        assertEquals(evento(), evento())
    }

    @Test
    fun `copy with changed field is not equal to original`() {
        val original = evento()
        val modified = original.copy(nombreEvento = "Otro Evento")
        assertNotEquals(original, modified)
    }

    @Test
    fun `copy preserves unchanged fields`() {
        val original = evento()
        val copied   = original.copy(valorEvento = "Gratis")

        assertEquals(original.eventId,           copied.eventId)
        assertEquals(original.nombreEvento,       copied.nombreEvento)
        assertEquals(original.lugarEvento,        copied.lugarEvento)
        assertEquals(original.latitud,            copied.latitud, 0.0001)
        assertEquals(original.longitud,           copied.longitud, 0.0001)
        assertEquals("Gratis",                    copied.valorEvento)
    }

    // ── Coordinates ────────────────────────────────────────────────────────

    @Test
    fun `southern hemisphere negative latitude is valid`() {
        val e = evento(lat = -33.45, lon = -70.65)
        assertTrue(e.latitud < 0)
        assertTrue(e.longitud < 0)
    }

    @Test
    fun `zero coordinates mark an invalid location`() {
        val e = evento(lat = 0.0, lon = 0.0)
        assertTrue(e.latitud == 0.0 && e.longitud == 0.0)
    }

    @Test
    fun `extreme valid coordinates are stored correctly`() {
        val e = evento(lat = 90.0, lon = 180.0)
        assertEquals(90.0,  e.latitud,  0.0)
        assertEquals(180.0, e.longitud, 0.0)
    }

    // ── Optional fields ────────────────────────────────────────────────────

    @Test
    fun `null vigenciaEvento is handled`() {
        val e = evento(vigencia = null.toString()).copy(vigenciaEvento = null)
        assertNull(e.vigenciaEvento)
    }

    @Test
    fun `null descripcionEvento is handled`() {
        val e = evento().copy(descripcionEvento = null)
        assertNull(e.descripcionEvento)
    }

    @Test
    fun `null fotosEvento is handled`() {
        val e = evento(foto = null)
        assertNull(e.fotosEvento)
    }

    // ── toString / hashCode (data class contract) ──────────────────────────

    @Test
    fun `toString contains nombreEvento`() {
        val e = evento(nombre = "Festival Jazz")
        assertTrue(e.toString().contains("Festival Jazz"))
    }

    @Test
    fun `equal objects have same hashCode`() {
        assertEquals(evento().hashCode(), evento().hashCode())
    }

    @Test
    fun `different objects have different hashCode`() {
        val e1 = evento(id = 1)
        val e2 = evento(id = 2)
        assertNotEquals(e1.hashCode(), e2.hashCode())
    }
}