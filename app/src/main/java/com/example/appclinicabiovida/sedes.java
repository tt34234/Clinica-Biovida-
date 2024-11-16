package com.example.appclinicabiovida;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class sedes extends AppCompatActivity implements OnMapReadyCallback {
    GoogleMap mMap;
    private ArrayList<Sede> listaSedes;
    private RequestQueue requestQueue;
    private RecyclerView lst1;
    private sedes.AdaptadorSede adaptadorSede;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sedes);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);}
        lst1 = findViewById(R.id.lst1);
        listaSedes = new ArrayList<>();
        requestQueue = Volley.newRequestQueue(this);
        cargarSedes();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        lst1.setLayoutManager(linearLayoutManager);
        adaptadorSede = new AdaptadorSede(listaSedes);
        lst1.setAdapter(adaptadorSede);
    }

    private void cargarSedes() {
        String url = "http://192.168.0.199:80/sede/mostrar_sedes.php";
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        JSONArray jsonArray = response.getJSONArray("datos");

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject objeto = jsonArray.getJSONObject(i);
                            int id_sede = objeto.getInt("id_sede");
                            String nombre_sede = objeto.getString("nombre_sede");
                            String direccion_sede = objeto.getString("direccion_sede");

                            Sede sede = new Sede(id_sede, nombre_sede, direccion_sede);
                            listaSedes.add(sede);
                        }
                        adaptadorSede.notifyDataSetChanged();  // Notificar cambios al adaptador
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> {
                    // Manejo de error
                    error.printStackTrace();
                });
        requestQueue.add(request);
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

        // Habilitar controles de zoom
        mMap.getUiSettings().setZoomControlsEnabled(true);

        // Ubicaciones en Surco, San Borja, Lima y San Isidro
        LatLng surco = new LatLng(-12.0564, -77.0341);
        LatLng sanBorja = new LatLng(-12.1046, -76.9932);
        LatLng lima = new LatLng(-12.1129, -76.9736);
        LatLng sanIsidro = new LatLng(-12.1012,-77.0329);

        // Agregar marcadores en cada ubicación
        mMap.addMarker(new MarkerOptions().position(surco).title("Av. Inca Garcilaso de la Vega 1420, Lima"));
        mMap.addMarker(new MarkerOptions().position(sanBorja).title("Av. Guardia Civil 421-433, San Borja"));
        mMap.addMarker(new MarkerOptions().position(lima).title("Av. El Polo 461, Santiago de Surco"));
        mMap.addMarker(new MarkerOptions().position(sanIsidro).title("Av. Paseo de la República N° 3058, San Isidro"));

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

    public class AdaptadorSede extends RecyclerView.Adapter<AdaptadorSede.AdaptadorSedeHolder> {

        private ArrayList<Sede> listaSedes;

        public AdaptadorSede(ArrayList<Sede> listaSedes) {
            this.listaSedes = listaSedes;
        }

        @NonNull
        @Override
        public AdaptadorSedeHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_sede, parent, false);
            return new AdaptadorSedeHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull AdaptadorSedeHolder holder, int position) {
            Sede sede = listaSedes.get(position);
            holder.nombreSede.setText(sede.getNombre_sede());
            holder.direccionSede.setText(sede.getDireccion_sede());

            // Aquí puedes agregar un listener para cuando se haga clic en una sede
            holder.itemView.setOnClickListener(v -> {
                // Lógica para mostrar la sede en el mapa o realizar alguna acción
            });
        }

        @Override
        public int getItemCount() {
            return listaSedes.size();
        }

        public class AdaptadorSedeHolder extends RecyclerView.ViewHolder {
            TextView nombreSede, direccionSede;

            public AdaptadorSedeHolder(@NonNull View itemView) {
                super(itemView);
                nombreSede = itemView.findViewById(R.id.nombre);
                direccionSede = itemView.findViewById(R.id.direccion);
            }
}
}
}