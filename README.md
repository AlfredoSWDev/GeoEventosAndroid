**GeoEventosAndroid**

Este módulo es parte del proyecto GeoEventos, un sistema de gestión de eventos en tiempo real. La app móvil Android permite a los usuarios explorar eventos cercanos, ver su ubicación en el mapa y consultar los detalles de cada uno.

**Funcionalidades**

* **Mapa interactivo** con OpenStreetMap sin necesidad de API key
* **Marcadores** por cada evento con coordenadas registradas
* **Panel de detalle** al tocar un marcador: nombre, lugar, valor, vigencia, descripción y foto
* **Búsqueda** de eventos por nombre o lugar en tiempo real
* **Carga de imágenes** desde URL (ImgBB) en el panel de detalle

**EndPoints que Consume**

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

**Cómo Correr el Proyecto**

### Requisitos

* Android Studio Hedgehog o superior
* Android SDK 28+
* [GeoEventosAPI](https://github.com/AlfredoSWDev/GeoEventosAPI) corriendo localmente

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
```

**4. Levantar la API:**

Antes de correr la app asegúrate de que GeoEventosAPI esté corriendo:

[GeoEventosAPI](https://github.com/AlfredoSWDev/GeoEventosAPI)

**5. Correr la app:**
- Conecta un emulador o dispositivo físico
- Click en ▶️ Run en Android Studio

**Permisos Requeridos**

```xml
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
```

**Roadmap**

- [ ] Crear y editar eventos desde la app móvil
- [ ] Filtrar eventos por categoría y distancia
- [ ] Notificaciones push para eventos cercanos
- [ ] Modo offline con caché de eventos
- [ ] Autenticación de usuarios
- [ ] Despliegue en Google Play Store

