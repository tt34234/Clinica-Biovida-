package com.example.appclinicabiovida;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;

public class Mapaest extends AppCompatActivity implements OnMapReadyCallback {
    GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_mapa);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

        // Ubicaciones en Surco, San Borja, Lima y San Isidro
        LatLng surco = new LatLng(-12.1375, -76.9912);
        LatLng sanBorja = new LatLng(-12.1064, -76.9818);
        LatLng lima = new LatLng(-12.0464, -77.0428);
        LatLng sanIsidro = new LatLng(-12.0970, -77.0369);

        // Agregar marcadores en cada ubicación
        mMap.addMarker(new MarkerOptions().position(surco).title("Surco"));
        mMap.addMarker(new MarkerOptions().position(sanBorja).title("San Borja"));
        mMap.addMarker(new MarkerOptions().position(lima).title("Lima"));
        mMap.addMarker(new MarkerOptions().position(sanIsidro).title("San Isidro"));

        // Mover la cámara para que muestre todas las ubicaciones
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        builder.include(surco);
        builder.include(sanBorja);
        builder.include(lima);
        builder.include(sanIsidro);
        LatLngBounds bounds = builder.build();

        // Ajustar el zoom para que todas las ubicaciones estén dentro de la vista
        int padding = 100; // espacio de padding en los bordes del mapa
        mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, padding));
    }

}
