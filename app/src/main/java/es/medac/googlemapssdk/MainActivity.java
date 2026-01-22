package es.medac.googlemapssdk;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;

import es.medac.googlemapssdk.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private LatLng ubicacionSeleccionada;
    private ActivityMainBinding binding;

    // CLIENTE DE LOCALIZACIÓN DE GOOGLE (Nuevo)
    private FusedLocationProviderClient fusedLocationClient;

    // Código de petición de permisos (puede ser cualquier número entero)
    private static final int CODIGO_PETICION_PERMISOS = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        // 1. Inicializar el cliente de localización
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // 2. Cargar el mapa
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapFragment);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        binding.btnGuardar.setOnClickListener(v -> guardarUbicacion());
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        // Configurar el listener de clicks (igual que antes)
        mMap.setOnMapClickListener(this::ponerMarcador);

        // 3. VERIFICAR PERMISOS Y OBTENER UBICACIÓN
        activarMiUbicacion();
    }

    /**
     * Método que gestiona los permisos y activa el GPS
     */
    private void activarMiUbicacion() {
        // Comprobamos si tenemos permiso de localización fina (GPS preciso)
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {

            // SI TENEMOS PERMISO:
            // 1. Activamos el botón azul de "Mi Ubicación" en el mapa
            mMap.setMyLocationEnabled(true);

            // 2. Pedimos la última ubicación conocida para mover la cámara
            obtenerUltimaUbicacionConocida();

        } else {
            // SI NO TENEMOS PERMISO: Lo pedimos al usuario
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    CODIGO_PETICION_PERMISOS
            );
        }
    }

    /**
     * Método asíncrono para obtener la latitud/longitud actual
     */
    private void obtenerUltimaUbicacionConocida() {
        try {
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            // ¡OJO! location puede ser null si el GPS estaba apagado o no se ha usado recientemente
                            if (location != null) {
                                LatLng ubicacionActual = new LatLng(location.getLatitude(), location.getLongitude());

                                // Movemos la cámara allí con Zoom 15
                                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(ubicacionActual, 15));
                            } else {
                                Toast.makeText(MainActivity.this, "No se pudo obtener la ubicación actual", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        } catch (SecurityException e) {
            // Este catch es necesario aunque ya hayamos comprobado permisos
            e.printStackTrace();
        }
    }

    /**
     * Escuchamos la respuesta del usuario al diálogo de permisos
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == CODIGO_PETICION_PERMISOS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // El usuario aceptó: Volvemos a intentar activar la ubicación
                activarMiUbicacion();
            } else {
                Toast.makeText(this, "Permiso denegado. No podemos mostrar tu ubicación", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void ponerMarcador(LatLng latLng) {
        ubicacionSeleccionada = latLng;
        mMap.clear();
        mMap.addMarker(new MarkerOptions().position(latLng).title("Selección"));
        mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));

        // Actualizamos UI con DataBinding
        binding.tvLatitud.setText("Lat: " + latLng.latitude);
        binding.tvLongitud.setText("Long: " + latLng.longitude);
        binding.btnGuardar.setEnabled(true);
    }

    private void guardarUbicacion() {
        if (ubicacionSeleccionada != null) {
            Toast.makeText(this, "Guardado: " + ubicacionSeleccionada.latitude, Toast.LENGTH_SHORT).show();
        }
    }
}