package com.alfredo.geoeventosandroid

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.alfredo.geoeventosandroid.dto.EventoResponse
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.CustomZoomButtonsController
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

@Composable
fun MapaEventosScreen(
    eventos: List<EventoResponse>,
    onEventoClick: (EventoResponse) -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = androidx.lifecycle.compose.LocalLifecycleOwner.current

    val mapView = remember {
        MapView(context).apply {
            setTileSource(TileSourceFactory.MAPNIK)
            setMultiTouchControls(true)
            zoomController.setVisibility(CustomZoomButtonsController.Visibility.NEVER)
            controller.setZoom(12.0)
            isTilesScaledToDpi = true
            setUseDataConnection(true)
            minZoomLevel = 4.0
            maxZoomLevel = 19.0
        }
    }

    // ── Manejar ciclo de vida del mapa ─────────────────────────────────────
    DisposableEffect(lifecycleOwner) {
        val observer = androidx.lifecycle.LifecycleEventObserver { _, event ->
            when (event) {
                androidx.lifecycle.Lifecycle.Event.ON_RESUME -> mapView.onResume()
                androidx.lifecycle.Lifecycle.Event.ON_PAUSE  -> mapView.onPause()
                else -> {}
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            mapView.onDetach()
        }
    }

    AndroidView(
        factory = { mapView },
        modifier = Modifier.fillMaxSize(),
        update = { view ->
            view.overlays.clear()

            eventos.forEach { evento ->
                if (evento.latitud == 0.0 && evento.longitud == 0.0) return@forEach

                val point = GeoPoint(evento.latitud, evento.longitud)

                val marker = Marker(view).apply {
                    position = point
                    title    = evento.nombreEvento
                    snippet  = evento.lugarEvento
                    setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                    setOnMarkerClickListener { _, _ ->
                        onEventoClick(evento)
                        true
                    }
                }
                view.overlays.add(marker)
            }

            if (eventos.isNotEmpty()) {
                val primero = eventos.firstOrNull {
                    it.latitud != 0.0 || it.longitud != 0.0
                }
                primero?.let {
                    view.controller.setCenter(GeoPoint(it.latitud, it.longitud))
                }
            }

            view.invalidate()
        }
    )
}