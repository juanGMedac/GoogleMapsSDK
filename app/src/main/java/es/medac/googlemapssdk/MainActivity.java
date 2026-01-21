package es.medac.googlemapssdk;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil; // Importación clave

import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

// Importamos la clase generada automáticamente por Data Binding
import es.medac.googlemapssdk.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private LatLng ubicacionSeleccionada;

    // Referencia al binding (reemplaza a todas las variables de vistas)
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 1. Inflar el layout usando DataBindingUtil
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        // 2. Inicializar el Mapa
        // Aunque usamos Binding, para el FragmentManager seguimos usando findFragmentById
        // apuntando al ID del contenedor en el XML.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapFragment);

        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        // 3. Configurar listeners usando el objeto 'binding'
        // Ya no hace falta findViewById, accedemos directamente por el ID del XML
        binding.btnGuardar.setOnClickListener(v -> guardarUbicacion());
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        LatLng madrid = new LatLng(40.416775, -3.703790);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(madrid, 10));

        mMap.setOnMapClickListener(latLng -> {
            ubicacionSeleccionada = latLng;
            mMap.clear();
            mMap.addMarker(new MarkerOptions().position(latLng).title("Selección"));
            mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));

            // Actualizar UI mediante Binding
            actualizarUI(latLng);
        });
    }

    private void actualizarUI(LatLng latLng) {
        // Acceso directo y seguro a las vistas
        binding.tvLatitud.setText("Lat: " + latLng.latitude);
        binding.tvLongitud.setText("Long: " + latLng.longitude);
        binding.btnGuardar.setEnabled(true);
    }

    private void guardarUbicacion() {
        if (ubicacionSeleccionada != null) {
            String mensaje = "Guardado: " +
                    ubicacionSeleccionada.latitude + ", " +
                    ubicacionSeleccionada.longitude;
            Toast.makeText(this, mensaje, Toast.LENGTH_SHORT).show();
        }
    }
}