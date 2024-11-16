package com.example.appclinicabiovida;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;


import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.appclinicabiovida.databinding.ActivityElegirEspecialidadBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class elegirEspecilidad extends AppCompatActivity  {

    private ActivityElegirEspecialidadBinding binding;
    public static ArrayList<Especialidad> listaEspecialidades;
    private RequestQueue requestQueue;
    private RecyclerView recyclerView;
    private AdaptadorEspecialidad adaptadorEspecialidad;

    private int id_especialidad;
    private String nombre_especialidad, urlImagen;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_elegir_especialidad);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        binding = ActivityElegirEspecialidadBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        recyclerView = findViewById(R.id.lst1);
        listaEspecialidades=new ArrayList<>();
        requestQueue= Volley.newRequestQueue(this);


        // Configurar RecyclerView y Adaptador
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        adaptadorEspecialidad = new AdaptadorEspecialidad(new ArrayList<>()); // Adaptador con lista vacía
        recyclerView.setAdapter(adaptadorEspecialidad);

        //cargar especialidades
        cargarEspecialidad();

        // Configurar el TextWatcher para la búsqueda
        binding.tiedIngresarespecialidad.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adaptadorEspecialidad.filtrar(s.toString()); // Llama al método de filtrado
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }


    private void cargarEspecialidad()
    {
        String url ="http://192.168.0.199/especialidad/mostrar_especialidades.php";
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        JSONArray jsonArray = response.getJSONArray("datos");

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject objeto = jsonArray.getJSONObject(i);
                            id_especialidad = objeto.getInt("id_especialidad");
                            nombre_especialidad = objeto.getString("nombre_especialidad");
                            urlImagen = objeto.getString("urlImagen_especialidad");

                            Especialidad especialidad = new Especialidad(id_especialidad, nombre_especialidad, urlImagen);
                            listaEspecialidades.add(especialidad);

                        }
                        adaptadorEspecialidad.actualizarLista(listaEspecialidades);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> {
                    // Manejo de error
                });
        requestQueue.add(request);
    }
    private class AdaptadorEspecialidad extends RecyclerView.Adapter<AdaptadorEspecialidad.EspecialidadViewHolder> {
        private ArrayList<Especialidad> listaCompleta; // Lista original
        private ArrayList<Especialidad> listaEspecialidadesFiltradas; // Lista filtrada

        public AdaptadorEspecialidad(ArrayList<Especialidad> listaEspecialidades) {
            this.listaCompleta = new ArrayList<>(listaEspecialidades);
            this.listaEspecialidadesFiltradas = new ArrayList<>(listaEspecialidades);
        }

        @NonNull
        @Override
        public EspecialidadViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new EspecialidadViewHolder(getLayoutInflater().inflate(R.layout.list_especialidades, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull AdaptadorEspecialidad.EspecialidadViewHolder holder, int position) {
            holder.imprimir(position);
            holder.cardView.setOnClickListener(v -> {
                // Obtener el id_especialidad de la especialidad seleccionada
                int idEspecialidadSeleccionada = listaEspecialidadesFiltradas.get(position).getId_especialidad();
                String nombreEspecialidadSeleccionada = listaEspecialidadesFiltradas.get(position).getNombre_especialidad();

                SharedPreferences preDatosCita = getSharedPreferences("preDatosCita", MODE_PRIVATE);
                SharedPreferences.Editor editor = preDatosCita.edit();
                editor.putString("nombreEspecialidad", nombreEspecialidadSeleccionada);
                editor.apply();
                // Crear un Intent para abrir elegirSedeMostrarDoctores
                Intent intent = new Intent(v.getContext(), elegirSedeMostrarDoctores.class);

                // Pasar el id_especialidad y nombre_especialidad a la siguiente actividad
                intent.putExtra("id_especialidad", idEspecialidadSeleccionada);
                intent.putExtra("nombre_especialidad", nombreEspecialidadSeleccionada);
                // Iniciar la actividad
                v.getContext().startActivity(intent);
            });
        }

        @Override
        public int getItemCount() {
            return listaEspecialidadesFiltradas.size();
        }

        public void filtrar(String texto) {
            listaEspecialidadesFiltradas.clear();
            if (texto.isEmpty()) {
                listaEspecialidadesFiltradas.addAll(listaCompleta);
            } else {
                for (Especialidad especialidad : listaCompleta) {
                    if (especialidad.getNombre_especialidad().toLowerCase().contains(texto.toLowerCase())) {
                        listaEspecialidadesFiltradas.add(especialidad);
                    }
                }
            }
            notifyDataSetChanged();
        }

        public void actualizarLista(ArrayList<Especialidad> nuevaLista) {
            this.listaCompleta.clear();
            this.listaCompleta.addAll(nuevaLista);
            this.listaEspecialidadesFiltradas.clear();
            this.listaEspecialidadesFiltradas.addAll(nuevaLista);
            notifyDataSetChanged();
        }

        public class EspecialidadViewHolder extends RecyclerView.ViewHolder {
            TextView tvNombreEspecialidad;
            ImageView ivFotoEspecialidad;
            public CardView cardView;

            public EspecialidadViewHolder(@NonNull View itemView) {
                super(itemView);
                tvNombreEspecialidad = itemView.findViewById(R.id.apellidoDoctor);
                ivFotoEspecialidad = itemView.findViewById(R.id.listImage);
                cardView = itemView.findViewById(R.id.main_container);
            }

            public void imprimir(int position) {
                Especialidad especialidad = listaEspecialidadesFiltradas.get(position);
                tvNombreEspecialidad.setText(especialidad.getNombre_especialidad());
                recuperarImagen(especialidad.getUrlImagen_especialidad(), ivFotoEspecialidad);
            }

            public void recuperarImagen(String urlImagen, ImageView imageView) {
                ImageRequest imageRequest = new ImageRequest(urlImagen,
                        response -> imageView.setImageBitmap(response),
                        0, 0, null, null,
                        error -> {});
                requestQueue.add(imageRequest);
            }
        }
    }


}