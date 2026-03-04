# GeoEventos Android

Cliente móvil para la plataforma **GeoEventos**, desarrollado en Android con Kotlin y Jetpack Compose. Permite a los usuarios visualizar eventos geolocalizados en un mapa interactivo y consultar su detalle.

> Este repositorio contiene únicamente el cliente Android. La lógica de negocio y el acceso a datos residen en [GeoEventosAPI](https://github.com/tu-usuario/GeoEventosAPI).

---

## ¿Qué es GeoEventos?

GeoEventos es una plataforma que permite a empresas locales **publicar, promocionar y gestionar eventos** en un mapa interactivo, de forma similar a Google Maps. El modelo de negocio es **B2B (Business to Business)**: las empresas pagan por publicar sus eventos, mientras que los usuarios finales acceden a la información de forma totalmente **gratuita**.

Este cliente Android representa la cara visible para los usuarios finales: una app donde pueden explorar eventos cercanos, ver su ubicación en el mapa y consultar los detalles de cada uno.

---

## Stack Tecnológico

| Tecnología | Uso |
|------------|-----|
| Kotlin | Lenguaje principal |
| Jetpack Compose | Interfaz declarativa |
| OSMDroid + OpenStreetMap | Mapa interactivo (sin API key, 100% gratuito) |
| Retrofit 2 | Cliente HTTP para consumir la API REST |
| Gson | Deserialización de respuestas JSON |
| Coil Compose | Carga de imágenes desde URL |
| ViewModel + StateFlow | Gestión de estado |
| Coroutines | Llamadas asíncronas a la API |

---

## Estructura del Proyecto

```
app/src/main/java/com/alfredo/geoeventosandroid/
├── MainActivity.kt                  # Punto de entrada, configura OSMDroid
├── MapView.kt                       # Composable del mapa con marcadores
│
├── client/
│   └── RetrofitClient.kt            # Instancia singleton de Retrofit
│
├── dto/
│   ├── EventoResponse.kt            # DTO de evento (incluye latitud/longitud)
│   └── ImageResponse.kt             # DTO de respuesta de subida de imagen
│
├── model/
│   └── EventosViewModel.kt          # Estado de la UI y llamadas a la API
│
├── service/
│   └── EventoApiService.kt          # Interfaz de endpoints Retrofit
│
└── ui/
    └── theme/
        ├── Color.kt
        ├── Theme.kt
        └── Type.kt
```

---

## Funcionalidades

- **Mapa interactivo** con OpenStreetMap sin necesidad de API key
- **Marcadores** por cada evento con coordenadas registradas
- **Panel de detalle** al tocar un marcador: nombre, lugar, valor, vigencia, descripción y foto
- **Búsqueda** de eventos por nombre o lugar en tiempo real
- **Carga de imágenes** desde URL (ImgBB) en el panel de detalle

---

## Endpoints que Consume

Todos los endpoints apuntan a [GeoEventosAPI](https://github.com/AlfredoSWDev/GeoEventosAPI).

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| `GET` | `/api/eventos` | Listar todos los eventos |
| `GET` | `/api/eventos?q={texto}` | Buscar eventos por nombre o lugar |
| `GET` | `/api/eventos/{id}` | Obtener detalle de un evento |
| `POST` | `/api/eventos` | Crear nuevo evento |
| `PUT` | `/api/eventos/{id}` | Actualizar evento |
| `DELETE` | `/api/eventos/{id}` | Eliminar evento |
| `POST` | `/api/imagenes/subir` | Subir imagen a ImgBB |

### DTO de Evento

```kotlin
data class EventoResponse(
    val eventId:           Int?    = null,
    val nombreEvento:      String,
    val valorEvento:       String,
    val lugarEvento:       String,
    val vigenciaEvento:    String?,
    val descripcionEvento: String?,
    val fotosEvento:       String?,
    val latitud:           Double,
    val longitud:          Double
)
```

---

## Cómo Correr el Proyecto

### Requisitos

- Android Studio Hedgehog o superior
- Android SDK 28+
- [GeoEventosAPI](https://github.com/AlfredoSWDev/GeoEventosAPI) corriendo localmente

### Pasos

**1. Clonar el repositorio:**
```bash
git clone https://github.com/tu-usuario/GeoEventosAndroid.git
```

**2. Abrir en Android Studio:**
- File → Open → selecciona la carpeta del proyecto

**3. Verificar la URL de la API en `RetrofitClient.kt`:**
```kotlin
// Para emulador Android
private const val BASE_URL = "http://10.0.2.2:8080/"

// Para dispositivo físico (usa la IP de tu máquina en la red local)
// private const val BASE_URL = "http://192.168.x.x:8080/"
```

**4. Levantar la API:**

Antes de correr la app asegúrate de que GeoEventosAPI esté corriendo:

[GeoEventosAPI](https://github.com/AlfredoSWDev/GeoEventosAPI)


**5. Correr la app:**
- Conecta un emulador o dispositivo físico
- Click en ▶️ Run en Android Studio

---

## Configuración de Red

| Entorno | URL base |
|---------|----------|
| Emulador Android | `http://10.0.2.2:8080/` |
| Dispositivo físico | `http://192.168.x.x:8080/` |
| Producción (futuro) | `https://api.geoeventos.com/` |

> La app tiene `android:usesCleartextTraffic="true"` habilitado para desarrollo local. En producción se debe usar HTTPS y desactivar esta opción.

---

## Permisos Requeridos

```xml
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
```

---

## Roadmap

- [ ] Crear y editar eventos desde la app móvil
- [ ] Filtrar eventos por categoría y distancia
- [ ] Notificaciones push para eventos cercanos
- [ ] Modo offline con caché de eventos
- [ ] Autenticación de usuarios
- [ ] Despliegue en Google Play Store

---

## Repositorios del Proyecto

| Repositorio                                                    | Descripción |
|----------------------------------------------------------------|-------------|
| [GeoEventosAPI](https://github.com/AlfredoSWDev/GeoEventosAPI) | API REST Spring Boot |
| [GeoEventosGUI](https://github.com/AlfredoSWDev/GeoEventosGUI)   | Cliente de escritorio Swing |
| **GeoEventosAndroid**                                          | Este repositorio — cliente móvil Android |
