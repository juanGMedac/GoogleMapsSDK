package es.medac.googlemapssdk;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.CancellationTokenSource;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import es.medac.googlemapssdk.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private LatLng ubicacionSeleccionada;
    private String direccionGuardada = "";

    private ActivityMainBinding binding;
    private FusedLocationProviderClient fusedLocationClient;
    private static final int CODIGO_PETICION_PERMISOS = 100;

    // CONSTANTES DE ZOOM
    // Nivel 1: Mundo entero
    // Nivel 5: Continente/País
    // Nivel 10: Ciudad
    // Nivel 15: Calles
    // Nivel 20: Edificios
    private static final float ZOOM_CALLE = 16.0f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

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

        // --- NUEVO: CONFIGURACIÓN DEL ZOOM Y UI ---
        // 1. Habilitar los botones de Zoom (+ y -) en la esquina inferior derecha
        mMap.getUiSettings().setZoomControlsEnabled(true);

        // 2. Habilitar gestos de Zoom (pellizcar pantalla) - Suele estar activo por defecto
        mMap.getUiSettings().setZoomGesturesEnabled(true);

        // 3. Habilitar brújula (aparece al rotar el mapa)
        mMap.getUiSettings().setCompassEnabled(true);

        mMap.setOnMapClickListener(this::ponerMarcador);

        gestionarPermisosYUbicacion();
    }

    private void gestionarPermisosYUbicacion() {
        boolean tienePermisoFino = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
        boolean tienePermisoCoarse = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;

        if (tienePermisoFino || tienePermisoCoarse) {
            activarFuncionesUbicacion();
        } else {
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                    },
                    CODIGO_PETICION_PERMISOS
            );
        }
    }

    private void activarFuncionesUbicacion() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        mMap.setMyLocationEnabled(true);
        // Habilitar botón nativo de "Ir a mi ubicación" (esquina superior derecha)
        mMap.getUiSettings().setMyLocationButtonEnabled(true);

        obtenerUbicacionActual();
    }

    private void obtenerUbicacionActual() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, new CancellationTokenSource().getToken())
                .addOnSuccessListener(this, location -> {
                    if (location != null) {
                        LatLng ubicacionActual = new LatLng(location.getLatitude(), location.getLongitude());

                        // MOVIMIENTO DE CÁMARA CON ZOOM
                        // Usamos la constante ZOOM_CALLE (16.0f)
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(ubicacionActual, ZOOM_CALLE));

                        obtenerDireccion(ubicacionActual);
                    } else {
                        Toast.makeText(MainActivity.this, "No se pudo obtener la ubicación actual.", Toast.LENGTH_LONG).show();
                    }
                });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == CODIGO_PETICION_PERMISOS) {
            if (grantResults.length > 0) {
                boolean permisoConcedido = false;
                for (int resultado : grantResults) {
                    if (resultado == PackageManager.PERMISSION_GRANTED) {
                        permisoConcedido = true;
                        break;
                    }
                }
                if (permisoConcedido) {
                    activarFuncionesUbicacion();
                } else {
                    Toast.makeText(this, "Se necesita permiso para mostrar tu posición", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void ponerMarcador(LatLng latLng) {
        ubicacionSeleccionada = latLng;
        direccionGuardada = "Cargando dirección...";

        mMap.clear();
        mMap.addMarker(new MarkerOptions().position(latLng).title("Selección"));

        // AL PULSAR EN EL MAPA:
        // Opción A: Mantener el zoom actual del usuario (más cómodo)
        mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));

        // Opción B: Forzar un zoom específico (NO es tan cómodo, pero descomentar para probar)
        // mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, ZOOM_CALLE));

        binding.tvLatitud.setText("Lat: " + latLng.latitude);
        binding.tvLongitud.setText("Long: " + latLng.longitude);
        binding.btnGuardar.setEnabled(true);

        obtenerDireccion(latLng);
    }

    private void obtenerDireccion(LatLng latLng) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1, addresses -> {
                if (!addresses.isEmpty()) {
                    guardarDireccionEnVariable(addresses.get(0));
                }
            });
        } else {
            new Thread(() -> {
                try {
                    List<Address> addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
                    if (!addresses.isEmpty()) {
                        runOnUiThread(() -> guardarDireccionEnVariable(addresses.get(0)));
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();
        }
    }

    private void guardarDireccionEnVariable(Address address) {
        direccionGuardada = address.getAddressLine(0);
    }

    private void guardarUbicacion() {
        if (ubicacionSeleccionada != null) {
            Toast.makeText(this, "Guardado en BBDD: \n" + direccionGuardada, Toast.LENGTH_LONG).show();
        }
    }
}