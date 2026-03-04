package com.alfredo.geoeventosandroid

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.alfredo.geoeventosandroid.model.EventosViewModel
import com.alfredo.geoeventosandroid.ui.theme.GeoEventosAndroidTheme
import org.osmdroid.config.Configuration

class MainActivity : ComponentActivity() {

    private lateinit var mapView: org.osmdroid.views.MapView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Configuration.getInstance().load(
            this, getSharedPreferences("osm_prefs", MODE_PRIVATE)
        )
        Configuration.getInstance().userAgentValue = packageName

        enableEdgeToEdge()
        setContent {
            GeoEventosAndroidTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    PantallaMapaEventos()
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        Configuration.getInstance().load(
            this, getSharedPreferences("osm_prefs", MODE_PRIVATE)
        )
    }

    override fun onPause() {
        super.onPause()
    }
}

@Composable
fun PantallaMapaEventos(vm: EventosViewModel = viewModel()) {

    var searchQuery by remember { mutableStateOf("") }

    Box(modifier = Modifier.fillMaxSize()) {

        // ── 1. Mapa de fondo ──────────────────────────────────────────────
        MapaEventosScreen(
            eventos  = vm.eventos,
            onEventoClick = { evento -> vm.seleccionarEvento(evento) }
        )

        // ── 2. Barra de búsqueda (arriba) ────────────────────────────────
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .statusBarsPadding()
                .align(Alignment.TopCenter),
            shape = RoundedCornerShape(24.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            TextField(
                value         = searchQuery,
                onValueChange = {
                    searchQuery = it
                    vm.cargarEventos(it.ifBlank { null })
                },
                modifier    = Modifier.fillMaxWidth(),
                placeholder = { Text("Buscar eventos...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                colors      = TextFieldDefaults.colors(
                    focusedContainerColor   = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedIndicatorColor   = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                singleLine = true
            )
        }

        // ── 3. Panel de detalle (abajo) ───────────────────────────────────
        vm.eventoSeleccionado?.let { evento ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .padding(16.dp),
                shape     = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {

                    // Botón cerrar
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment     = Alignment.CenterVertically
                    ) {
                        Text(
                            text       = evento.nombreEvento,
                            fontSize   = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        IconButton(onClick = { vm.cerrarDetalle() }) {
                            Icon(Icons.Default.Close, contentDescription = "Cerrar")
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Foto del evento
                    if (!evento.fotosEvento.isNullOrBlank() &&
                        evento.fotosEvento.startsWith("http")) {
                        AsyncImage(
                            model             = evento.fotosEvento,
                            contentDescription = "Foto del evento",
                            modifier          = Modifier
                                .fillMaxWidth()
                                .height(140.dp)
                                .clip(RoundedCornerShape(8.dp)),
                            contentScale      = ContentScale.Crop
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    // Datos del evento
                    FilaDetalle(label = "📍 Lugar",    valor = evento.lugarEvento)
                    FilaDetalle(label = "💰 Valor",    valor = evento.valorEvento)
                    FilaDetalle(label = "📅 Vigencia", valor = evento.vigenciaEvento ?: "—")
                    FilaDetalle(label = "📝 Descripción", valor = evento.descripcionEvento ?: "—")
                }
            }
        }
    }
}

@Composable
fun FilaDetalle(label: String, valor: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp)
    ) {
        Text(
            text       = label,
            fontWeight = FontWeight.SemiBold,
            fontSize   = 13.sp,
            modifier   = Modifier.width(110.dp)
        )
        Text(
            text     = valor,
            fontSize = 13.sp,
            color    = Color.DarkGray
        )
    }
}