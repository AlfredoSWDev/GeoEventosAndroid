package com.alfredo.geoeventosandroid.service

import com.alfredo.geoeventosandroid.dto.EventoResponse
import kotlinx.coroutines.runBlocking
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.RecordedRequest
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Tests para [EventoApiService].
 *
 * Cubre:
 *  1. Anotaciones correctas (@GET, @POST, @PUT, @DELETE, @Multipart)
 *     → verificadas indirectamente: si Retrofit las lee mal, la request no llega.
 *  2. Respuestas HTTP simuladas con MockWebServer.
 *
 * Ubicación: app/src/test/java/com/alfredo/geoeventosandroid/service/EventoApiServiceTest.kt
 *
 * Dependencia nueva en libs.versions.toml:
 *   [versions]  mockwebserver = "4.12.0"
 *   [libraries] mockwebserver = { group = "com.squareup.okhttp3", name = "mockwebserver", version.ref = "mockwebserver" }
 *
 *   build.gradle.kts → testImplementation(libs.mockwebserver)
 */
class EventoApiServiceTest {

    private lateinit var server: MockWebServer
    private lateinit var api: EventoApiService

    @Before
    fun setUp() {
        server = MockWebServer()
        server.start()
        api = Retrofit.Builder()
            .baseUrl(server.url("/"))
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(EventoApiService::class.java)
    }

    @After
    fun tearDown() = server.shutdown()

    // ── Helpers ────────────────────────────────────────────────────────────

    private fun eventoJson(id: Int = 1, nombre: String = "Festival") = """
        {
          "eventId": $id, "nombreEvento": "$nombre",
          "valorEvento": "Gratis", "lugarEvento": "Santiago",
          "vigenciaEvento": "2025-12-31", "descripcionEvento": "Desc",
          "fotosEvento": "https://example.com/foto.jpg",
          "latitud": -33.45, "longitud": -70.65
        }
    """.trimIndent()

    private fun enqueue(body: String, code: Int = 200) = server.enqueue(
        MockResponse().setResponseCode(code)
            .setHeader("Content-Type", "application/json")
            .setBody(body)
    )

    private fun lastRequest(): RecordedRequest = server.takeRequest()

    private fun dummyEvento() = EventoResponse(
        nombreEvento = "Test", valorEvento = "Gratis",
        lugarEvento = "Online", latitud = 0.0, longitud = 0.0
    )

    // ══════════════════════════════════════════════════════════════════════
    // 1. Anotaciones — método HTTP y path correcto
    // ══════════════════════════════════════════════════════════════════════

    @Test
    fun `listarEventos uses GET`() = runBlocking {
        enqueue("[]")
        api.listarEventos()
        assertEquals("GET", lastRequest().method)
    }

    @Test
    fun `listarEventos path is api-eventos`() = runBlocking {
        enqueue("[]")
        api.listarEventos()
        assertTrue(lastRequest().path!!.startsWith("/api/eventos"))
    }

    @Test
    fun `obtenerEvento uses GET`() = runBlocking {
        enqueue(eventoJson(id = 1))
        api.obtenerEvento(1)
        assertEquals("GET", lastRequest().method)
    }

    @Test
    fun `obtenerEvento path contains id`() = runBlocking {
        enqueue(eventoJson(id = 7))
        api.obtenerEvento(7)
        assertTrue(lastRequest().path!!.contains("/api/eventos/7"))
    }

    @Test
    fun `crearEvento uses POST`() = runBlocking {
        enqueue(eventoJson())
        api.crearEvento(dummyEvento())
        assertEquals("POST", lastRequest().method)
    }

    @Test
    fun `crearEvento path is api-eventos`() = runBlocking {
        enqueue(eventoJson())
        api.crearEvento(dummyEvento())
        assertEquals("/api/eventos", lastRequest().path)
    }

    @Test
    fun `actualizarEvento uses PUT`() = runBlocking {
        enqueue(eventoJson())
        api.actualizarEvento(3, dummyEvento())
        assertEquals("PUT", lastRequest().method)
    }

    @Test
    fun `actualizarEvento path contains id`() = runBlocking {
        enqueue(eventoJson(id = 3))
        api.actualizarEvento(3, dummyEvento())
        assertTrue(lastRequest().path!!.contains("/api/eventos/3"))
    }

    @Test
    fun `eliminarEvento uses DELETE`() = runBlocking {
        server.enqueue(MockResponse().setResponseCode(204))
        api.eliminarEvento(1)
        assertEquals("DELETE", lastRequest().method)
    }

    @Test
    fun `eliminarEvento path contains id`() = runBlocking {
        server.enqueue(MockResponse().setResponseCode(204))
        api.eliminarEvento(5)
        assertTrue(lastRequest().path!!.contains("/api/eventos/5"))
    }

    @Test
    fun `subirImagen uses POST`() = runBlocking {
        enqueue("""{"success":true,"url":"https://x.com/f.jpg","mensaje":"OK"}""")
        val part = okhttp3.MultipartBody.Part.createFormData(
            "archivo", "test.jpg",
            byteArrayOf(1, 2, 3).toRequestBody("image/jpeg")
        )
        api.subirImagen(part)
        assertEquals("POST", lastRequest().method)
    }

    @Test
    fun `subirImagen path is api-imagenes-subir`() = runBlocking {
        enqueue("""{"success":true,"url":"https://x.com/f.jpg","mensaje":"OK"}""")
        val part = okhttp3.MultipartBody.Part.createFormData(
            "archivo", "test.jpg",
            byteArrayOf(1, 2, 3).toRequestBody("image/jpeg")
        )
        api.subirImagen(part)
        assertEquals("/api/imagenes/subir", lastRequest().path)
    }

    @Test
    fun `subirImagen sends multipart content-type`() = runBlocking {
        enqueue("""{"success":true,"url":"https://x.com/f.jpg","mensaje":"OK"}""")
        val part = okhttp3.MultipartBody.Part.createFormData(
            "archivo", "test.jpg",
            byteArrayOf(1, 2, 3).toRequestBody("image/jpeg")
        )
        api.subirImagen(part)
        val ct = lastRequest().getHeader("Content-Type") ?: ""
        assertTrue(ct.contains("multipart/form-data"))
    }

    // ══════════════════════════════════════════════════════════════════════
    // 2. MockWebServer — respuestas simuladas
    // ══════════════════════════════════════════════════════════════════════

    @Test
    fun `listarEventos parses list correctly`() = runBlocking {
        enqueue("[${eventoJson(1, "Jazz")}, ${eventoJson(2, "Rock")}]")
        val result = api.listarEventos()
        assertEquals(2, result.size)
        assertEquals("Jazz", result[0].nombreEvento)
        assertEquals("Rock", result[1].nombreEvento)
    }

    @Test
    fun `listarEventos returns empty list`() = runBlocking {
        enqueue("[]")
        assertTrue(api.listarEventos().isEmpty())
    }

    @Test
    fun `listarEventos sends query param when provided`() = runBlocking {
        enqueue("[]")
        api.listarEventos("jazz")
        assertTrue(lastRequest().path!!.contains("q=jazz"))
    }

    @Test
    fun `listarEventos omits query param when null`() = runBlocking {
        enqueue("[]")
        api.listarEventos(null)
        assertFalse(lastRequest().path!!.contains("q="))
    }

    @Test
    fun `obtenerEvento parses response correctly`() = runBlocking {
        enqueue(eventoJson(id = 42, nombre = "Concierto"))
        val result = api.obtenerEvento(42)
        assertEquals(42, result.eventId)
        assertEquals("Concierto", result.nombreEvento)
        assertEquals(-33.45, result.latitud, 0.0001)
        assertEquals(-70.65, result.longitud, 0.0001)
    }

    @Test
    fun `crearEvento returns evento with assigned id`() = runBlocking {
        enqueue(eventoJson(id = 99, nombre = "Nuevo"))
        val result = api.crearEvento(dummyEvento())
        assertEquals(99, result.eventId)
        assertEquals("Nuevo", result.nombreEvento)
    }

    @Test
    fun `actualizarEvento returns updated evento`() = runBlocking {
        enqueue(eventoJson(id = 3, nombre = "Actualizado"))
        val result = api.actualizarEvento(3, dummyEvento())
        assertEquals("Actualizado", result.nombreEvento)
    }

    @Test
    fun `listarEventos throws HttpException on 500`() = runBlocking {
        server.enqueue(MockResponse().setResponseCode(500))
        try {
            api.listarEventos()
            fail("Debería lanzar HttpException")
        } catch (e: HttpException) {
            assertEquals(500, e.code())
        }
    }

    @Test
    fun `obtenerEvento throws HttpException on 404`() = runBlocking {
        server.enqueue(MockResponse().setResponseCode(404))
        try {
            api.obtenerEvento(999)
            fail("Debería lanzar HttpException")
        } catch (e: HttpException) {
            assertEquals(404, e.code())
        }
    }

    @Test
    fun `subirImagen returns success response`() = runBlocking {
        enqueue("""{"success":true,"url":"https://cdn.com/foto.jpg","mensaje":"Subida OK"}""")
        val part = okhttp3.MultipartBody.Part.createFormData(
            "archivo", "foto.jpg",
            byteArrayOf(1, 2, 3).toRequestBody("image/jpeg")
        )
        val result = api.subirImagen(part)
        assertTrue(result.success)
        assertEquals("https://cdn.com/foto.jpg", result.url)
    }

    @Test
    fun `subirImagen returns failure response`() = runBlocking {
        enqueue("""{"success":false,"url":"","mensaje":"Formato no soportado"}""")
        val part = okhttp3.MultipartBody.Part.createFormData(
            "archivo", "foto.bmp",
            byteArrayOf(1).toRequestBody("image/bmp")
        )
        val result = api.subirImagen(part)
        assertFalse(result.success)
        assertEquals("Formato no soportado", result.mensaje)
    }

    // extensión privada para no repetir boilerplate
    private fun ByteArray.toRequestBody(contentType: String) =
        okhttp3.RequestBody.create(contentType.toMediaType(), this)

    private fun String.toMediaType() =
        toMediaTypeOrNull()!!
}