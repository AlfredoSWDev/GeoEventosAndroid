# GeoEventosAndroid

Cliente móvil del ecosistema **GeoEventos** — plataforma B2B de gestión de eventos geolocalizados.

App Android nativa construida con **Kotlin + Jetpack Compose + OSMDroid**, que permite explorar eventos cercanos en un mapa interactivo y consultar sus detalles completos.

---

## Stack

| Capa | Tecnología |
|------|-----------|
| Lenguaje | Kotlin |
| UI | Jetpack Compose |
| Mapa | OSMDroid + OpenStreetMap |
| HTTP | Retrofit |
| Imágenes | Coil (carga desde URL ImgBB) |
| Tests | JUnit 4 + MockK + MockWebServer + Compose UI Tests |

---

## Funcionalidades

* **Mapa interactivo** con OpenStreetMap — sin API key necesaria
* **Marcadores** por cada evento con sus coordenadas registradas
* **Panel de detalle** al tocar un marcador: nombre, lugar, valor, vigencia, descripción y foto
* **Búsqueda en tiempo real** de eventos por nombre o lugar
* **Carga de imágenes** desde URL (ImgBB) en el panel de detalle

---

## Endpoints que consume

Todos los endpoints apuntan a [GeoEventosAPI](https://github.com/AlfredoSWDev/GeoEventosAPI).

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| `GET` | `/api/eventos` | Listar todos los eventos |
| `GET` | `/api/eventos?q={texto}` | Buscar por nombre o lugar |
| `GET` | `/api/eventos/{id}` | Obtener detalle de un evento |
| `POST` | `/api/eventos` | Crear nuevo evento |
| `PUT` | `/api/eventos/{id}` | Actualizar evento |
| `DELETE` | `/api/eventos/{id}` | Eliminar evento |
| `POST` | `/api/imagenes/subir` | Subir imagen a ImgBB |

---

## Cómo correr el proyecto

### Requisitos

* Android Studio Hedgehog o superior
* Android SDK 28+
* [GeoEventosAPI](https://github.com/AlfredoSWDev/GeoEventosAPI) corriendo localmente (o apuntando a producción)

### Pasos

**1. Clonar el repositorio:**
```bash
git clone https://github.com/AlfredoSWDev/GeoEventosAndroid.git
```

**2. Abrir en Android Studio:**
- File → Open → selecciona la carpeta del proyecto

**3. Verificar la URL de la API en `RetrofitClient.kt`:**
```kotlin
// Para emulador Android
private const val BASE_URL = "http://10.0.2.2:8080/"

// Para dispositivo físico (usa la IP de tu máquina en la red local)
// private const val BASE_URL = "http://192.168.x.x:8080/"

// Para producción
// private const val BASE_URL = "https://geoeventosapi.onrender.com/"
```

**4. Correr la app:**
- Conecta un emulador o dispositivo físico
- Click en ▶️ Run en Android Studio

---

## Permisos requeridos

```xml
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
```

---

## Tests

Suite multicapa de tests automatizados:

```bash
./gradlew test
```

| Capa | Herramientas |
|------|-------------|
| Unitarios | JUnit 4 + MockK |
| Integración HTTP | MockWebServer |
| UI | Compose UI Tests |

---

## Roadmap

- [x] Mapa interactivo con OSMDroid
- [x] Panel de detalle al tocar marcador
- [x] Búsqueda en tiempo real
- [x] Carga de imágenes desde URL (ImgBB)
- [x] Suite de tests (JUnit 4 + MockK + Compose UI)
- [ ] Crear y editar eventos desde la app
- [ ] Filtrar eventos por categoría y distancia
- [ ] Notificaciones push para eventos cercanos
- [ ] Modo offline con caché de eventos
- [ ] Autenticación de usuarios
- [ ] Despliegue en Google Play Store

---

## Parte del ecosistema GeoEventos

| Repositorio | Descripción |
|-------------|-------------|
| [GeoEventosAPI](https://github.com/AlfredoSWDev/GeoEventosAPI) | Spring Boot 4 + Java 21 + PostgreSQL |
| [GeoEventosWeb](https://github.com/AlfredoSWDev/GeoEventosWeb) | Kotlin/Wasm + Compose for Web |
| **GeoEventosAndroid** | Kotlin + Jetpack Compose + OSMDroid ← aquí |
| [GeoEventosDesktop](https://github.com/AlfredoSWDev/GeoEventosDesktop) | Java 23 + Swing + JavaFX |
| [GeoEventosDB](https://github.com/AlfredoSWDev/GeoEventosDB) | Schema y migraciones PostgreSQL |

---

## Autor

**Alfredo Sanchez** — [@AlfredoSWDev](https://github.com/AlfredoSWDev)

📺 Stream de desarrollo en [Twitch](https://twitch.tv/AlfredoSWDev) · [YouTube](https://youtube.com/@AlfredoSWDev)
