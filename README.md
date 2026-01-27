# 🗺️ Google Maps SDK Android – Proyecto de Ejemplo

Este proyecto muestra cómo integrar **Google Maps en una aplicación Android**, centrado como guía paso a paso para alumnos. Está desarrollado con Java y Android Studio.

---

## 🎯 Objetivo del Proyecto

Aprender a:

- Configurar Google Maps en un proyecto Android
- Mostrar un mapa en una actividad
- Añadir un marcador personalizado
- Centrar la cámara en una localización concreta

---

## 📁 Estructura del Proyecto

```
GoogleMapsSDK/
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/es/medac/googlemapssdk/MainActivity.java
│   │   │   ├── res/layout/activity_main.xml
│   │   │   ├── AndroidManifest.xml
│   │   │   └── ...
├── build.gradle
├── settings.gradle
└── ...
```

---

## 🧱 Requisitos

- Android Studio Arctic Fox (o superior)
- Mínimo SDK: `21`
- Google Play Services activo
- Conexión a Internet
- Cuenta de Google para obtener la API Key de Google Maps

---

## 🚀 Pasos para Configurar el Proyecto

### 1. Clona el Repositorio

```bash
git clone https://github.com/juanGMedac/GoogleMapsSDK.git
cd GoogleMapsSDK
```

### 2. Abre el Proyecto en Android Studio

Archivo → Abrir → Selecciona la carpeta descargada.

---

### 3. Consigue tu API Key de Google Maps

1. Ve a [Google Cloud Console](https://console.cloud.google.com/)
2. Crea un proyecto nuevo o selecciona uno existente
3. Activa la API: **Maps SDK for Android**
4. Ve a “Credenciales” → Crea una nueva **API Key**
5. Copia la clave

---

### 4. Añade tu API Key en el Manifest

Edita `app/src/main/AndroidManifest.xml` y dentro de la etiqueta `<application>`, reemplaza:

```xml
<meta-data
    android:name="com.google.android.geo.API_KEY"
    android:value="AQUI_TU_API_KEY" />
```

---

### 5. Código Principal (`MainActivity.java`)

```java
@Override
public void onMapReady(GoogleMap googleMap) {
    mMap = googleMap;

    // Coordenadas del marcador
    LatLng medac = new LatLng(36.7213, -4.4214);

    // Añade el marcador
    mMap.addMarker(new MarkerOptions().position(medac).title("Medac Málaga"));

    // Mueve y hace zoom en la cámara
    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(medac, 15));
}
```

---

## 📌 Permisos y dependencias

### En `AndroidManifest.xml`:

```xml
<uses-permission android:name="android.permission.INTERNET"/>
```

### En `app/build.gradle`:

```gradle
dependencies {
    implementation 'com.google.android.gms:play-services-maps:18.1.0'
    implementation 'com.google.android.gms:play-services-location:21.0.1'
}
```

---

## 📷 Vista previa esperada

🗺️ Un mapa centrado en Málaga con un marcador llamado **“Medac Málaga”**.

---

## 🧪 Ideas de mejora (para alumnos)

- Pedir permisos de localización y mostrar tu ubicación
- Usar múltiples marcadores
- Implementar cambio de tipo de mapa (satélite, terreno, etc.)
- Detectar clics en el mapa
- Agregar botones de zoom o centrado

---

## 📘 Clases y Métodos en el Proyecto

A continuación, se detallan las clases y métodos utilizados para la integración de Google Maps:

| Elemento                           | Tipo         | Descripción                                                                 |
|------------------------------------|--------------|-----------------------------------------------------------------------------|
| `MainActivity`                     | Clase        | Actividad principal que muestra el mapa e implementa la interfaz `OnMapReadyCallback`. |
| `AppCompatActivity`               | Clase Base   | Clase base de actividades con compatibilidad hacia versiones antiguas de Android. |
| `OnMapReadyCallback`              | Interfaz     | Proporciona el método `onMapReady()` que se llama cuando el mapa está listo para ser usado. |
| `GoogleMap`                        | Clase        | Objeto que representa el mapa de Google; permite manipular marcadores, cámara, eventos, etc. |
| `SupportMapFragment`              | Clase        | Fragmento que contiene el mapa dentro de la interfaz. Se obtiene desde el XML o por código. |
| `CameraUpdateFactory`             | Clase Util   | Permite actualizar la cámara del mapa, hacer zoom, centrar una ubicación, etc. |
| `LatLng`                           | Clase        | Representa una coordenada geográfica (latitud y longitud). |
| `MarkerOptions`                   | Clase        | Sirve para crear un marcador personalizado en una posición determinada. |
| `ActivityMainBinding`             | Clase        | Clase generada automáticamente por Data Binding para acceder a las vistas. |
| `mMap`                             | Variable     | Instancia del mapa que se inicializa en `onMapReady()`. |
| `ubicacionSeleccionada`          | Variable     | Objeto `LatLng` que guarda una posición geográfica específica. |
| `onCreate(Bundle savedInstanceState)` | Método     | Inicializa la actividad, infla el layout, y obtiene el fragmento del mapa. |
| `onMapReady(GoogleMap googleMap)` | Método       | Método clave que se ejecuta cuando el mapa está cargado y listo para usarse. |

---

## 🧠 Recursos útiles

- [Guía oficial de Google Maps SDK](https://developers.google.com/maps/documentation/android-sdk)
- [Ejemplos oficiales de Google](https://github.com/googlemaps/android-samples)

---

## 🧾 Licencia

Este proyecto es solo para fines educativos.

---
