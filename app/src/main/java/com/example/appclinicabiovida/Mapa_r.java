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
import com.google.android.gms.maps.model.MarkerOptions;

public class Mapa_r extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMapClickListener, GoogleMap.OnMapLongClickListener {
    Button b1;
    EditText txtLatitud, txtLongitud;
    GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_mapa_r);

        txtLatitud = findViewById(R.id.txtLatitud);
        txtLongitud = findViewById(R.id.txtLongitud);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        b1 = (Button) findViewById(R.id.btna);

        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.putExtra("p_latitud", txtLatitud.getText().toString());
                intent.putExtra("p_longitud", txtLongitud.getText().toString());
                startActivity(intent);
            }
        });

    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        this.mMap.setOnMapClickListener(this);
        this.mMap.setOnMapLongClickListener(this);
        //Ubicaciones en Surco, San Borja, Lima y San Isidro
        LatLng surco = new LatLng(-12.1375, -76.9912);
        mMap.addMarker(new MarkerOptions().position(surco).title("UPN-Los Olivos"));
        LatLng sanBorja = new LatLng(-12.1064, -76.9818);
        mMap.addMarker(new MarkerOptions().position(sanBorja).title("UPN-Los Olivos"));
        LatLng lima = new LatLng(-12.0464, -77.0428);
        mMap.addMarker(new MarkerOptions().position(lima).title("UPN-Los Olivos"));
        LatLng sanIsidro = new LatLng(-12.0970,-77.0369);
        mMap.addMarker(new MarkerOptions().position(sanIsidro).title("UPN-Los Olivos"));
        LatLng upn = new LatLng(-11.9592924724,-77.06795856356);
        mMap.addMarker(new MarkerOptions().position(upn).title("UPN-Los Olivos"));

        mMap.moveCamera(CameraUpdateFactory.newLatLng(surco));
    }

    @Override
    public void onMapClick(@NonNull LatLng latLng) {
        txtLatitud.setText(String.valueOf(latLng.latitude));
        txtLongitud.setText(String.valueOf(latLng.longitude));

        mMap.clear();
        LatLng UPN = new LatLng(latLng.latitude,latLng.longitude);
        mMap.addMarker(new MarkerOptions().position(UPN).title(""));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(UPN));
    }

    @Override
    public void onMapLongClick(@NonNull LatLng latLng) {
        txtLatitud.setText(String.valueOf(latLng.latitude));
        txtLongitud.setText(String.valueOf(latLng.longitude));

        mMap.clear();
        LatLng UPN = new LatLng(latLng.latitude,latLng.longitude);
        mMap.addMarker(new MarkerOptions().position(UPN).title(""));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(UPN));
    }
}