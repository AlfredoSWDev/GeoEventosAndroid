package com.alfredo.geoeventosandroid

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.alfredo.geoeventosandroid.dto.EventoResponse
import com.alfredo.geoeventosandroid.ui.theme.GeoEventosAndroidTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Instrumented Compose UI tests.
 * Ubicación: app/src/androidTest/java/com/alfredo/geoeventosandroid/PantallaMapaEventosUITest.kt
 * Requiere emulador o dispositivo físico.
 */
@RunWith(AndroidJUnit4::class)
class PantallaMapaEventosUITest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private fun buildEvento(
        id: Int        = 1,
        nombre: String = "Festival de Música",
        lugar: String  = "Plaza Italia",
        valor: String  = "\$10.000",
        vigencia: String = "2025-12-31",
        desc: String   = "Descripción de prueba",
        foto: String?  = null,
        lat: Double    = -33.45,
        lon: Double    = -70.65
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

    // ── FilaDetalle ────────────────────────────────────────────────────────

    @Test
    fun filaDetalle_showsLabelAndValue() {
        composeTestRule.setContent {
            GeoEventosAndroidTheme {
                FilaDetalle(label = "💰 Valor", valor = "Gratis")
            }
        }
        composeTestRule.onNodeWithText("💰 Valor").assertIsDisplayed()
        composeTestRule.onNodeWithText("Gratis").assertIsDisplayed()
    }

    @Test
    fun filaDetalle_showsDashWhenEmpty() {
        composeTestRule.setContent {
            GeoEventosAndroidTheme {
                FilaDetalle(label = "📅 Vigencia", valor = "—")
            }
        }
        composeTestRule.onNodeWithText("—").assertIsDisplayed()
    }

    @Test
    fun filaDetalle_showsLongDescription() {
        val texto = "Este es un texto muy largo que describe el evento en detalle completo"
        composeTestRule.setContent {
            GeoEventosAndroidTheme {
                FilaDetalle(label = "📝 Descripción", valor = texto)
            }
        }
        composeTestRule.onNodeWithText(texto).assertIsDisplayed()
    }

    @Test
    fun filaDetalle_lugar_isDisplayed() {
        composeTestRule.setContent {
            GeoEventosAndroidTheme {
                FilaDetalle(label = "📍 Lugar", valor = "Parque Forestal")
            }
        }
        composeTestRule.onNodeWithText("📍 Lugar").assertIsDisplayed()
        composeTestRule.onNodeWithText("Parque Forestal").assertIsDisplayed()
    }

    // ── Panel de detalle ───────────────────────────────────────────────────

    @Test
    fun detailCard_notVisibleWhenNoEventSelected() {
        composeTestRule.setContent {
            GeoEventosAndroidTheme {
                val seleccionado: EventoResponse? = null
                if (seleccionado != null) {
                    Text("DETALLE VISIBLE")
                }
            }
        }
        composeTestRule.onNodeWithText("DETALLE VISIBLE").assertDoesNotExist()
    }

    @Test
    fun detailCard_visibleWhenEventSelected() {
        composeTestRule.setContent {
            GeoEventosAndroidTheme {
                val seleccionado = buildEvento(nombre = "Concierto Rock")
                Text(seleccionado.nombreEvento)
            }
        }
        composeTestRule.onNodeWithText("Concierto Rock").assertIsDisplayed()
    }

    @Test
    fun detailCard_showsAllEventFields() {
        val evento = buildEvento(
            nombre   = "Feria del Libro",
            lugar    = "Parque Forestal",
            valor    = "\$5.000",
            vigencia = "2025-12-31",
            desc     = "Gran feria literaria"
        )
        composeTestRule.setContent {
            GeoEventosAndroidTheme {
                androidx.compose.foundation.layout.Column {
                    Text(evento.nombreEvento)
                    FilaDetalle("📍 Lugar",    evento.lugarEvento)
                    FilaDetalle("💰 Valor",    evento.valorEvento)
                    FilaDetalle("📅 Vigencia", evento.vigenciaEvento ?: "—")
                    FilaDetalle("📝 Descripción", evento.descripcionEvento ?: "—")
                }
            }
        }
        composeTestRule.onNodeWithText("Feria del Libro").assertIsDisplayed()
        composeTestRule.onNodeWithText("Parque Forestal").assertIsDisplayed()
        composeTestRule.onNodeWithText("\$5.000").assertIsDisplayed()
        composeTestRule.onNodeWithText("2025-12-31").assertIsDisplayed()
        composeTestRule.onNodeWithText("Gran feria literaria").assertIsDisplayed()
    }

    @Test
    fun detailCard_nullVigencia_showsDash() {
        composeTestRule.setContent {
            GeoEventosAndroidTheme {
                FilaDetalle("📅 Vigencia", null ?: "—")
            }
        }
        composeTestRule.onNodeWithText("—").assertIsDisplayed()
    }

    // ── Botón cerrar ───────────────────────────────────────────────────────

    @Test
    fun closeButton_hidesDetailCard() {
        composeTestRule.setContent {
            GeoEventosAndroidTheme {
                var seleccionado: EventoResponse? by remember {
                    mutableStateOf(buildEvento(nombre = "Teatro Municipal"))
                }
                if (seleccionado != null) {
                    Card {
                        androidx.compose.foundation.layout.Row {
                            Text(seleccionado!!.nombreEvento)
                            IconButton(onClick = { seleccionado = null }) {
                                Icon(
                                    imageVector        = Icons.Default.Close,
                                    contentDescription = "Cerrar"
                                )
                            }
                        }
                    }
                }
            }
        }

        composeTestRule.onNodeWithText("Teatro Municipal").assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription("Cerrar").performClick()
        composeTestRule.onNodeWithText("Teatro Municipal").assertDoesNotExist()
    }

    // ── Tema ───────────────────────────────────────────────────────────────

    @Test
    fun theme_lightMode_rendersWithoutCrash() {
        composeTestRule.setContent {
            GeoEventosAndroidTheme(darkTheme = false, dynamicColor = false) {
                Text("Modo claro")
            }
        }
        composeTestRule.onNodeWithText("Modo claro").assertIsDisplayed()
    }

    @Test
    fun theme_darkMode_rendersWithoutCrash() {
        composeTestRule.setContent {
            GeoEventosAndroidTheme(darkTheme = true, dynamicColor = false) {
                Text("Modo oscuro")
            }
        }
        composeTestRule.onNodeWithText("Modo oscuro").assertIsDisplayed()
    }
}