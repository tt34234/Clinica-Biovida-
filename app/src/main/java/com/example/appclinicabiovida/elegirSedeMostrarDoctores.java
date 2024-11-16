package com.example.appclinicabiovida;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
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
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;




public class elegirSedeMostrarDoctores extends AppCompatActivity {

    private RecyclerView recyclerView;
    private AdaptadorDoctor adaptadorDoctor;
    private RequestQueue requestQueue;
    private Spinner spinnerSedes;
    private ArrayList<Doctor> listaDoctores;
    private ArrayList<Sede> listaSedes; // Lista de objetos Sede
    private String sedeSeleccionada;
    String nombreEspecialidad;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_elegir_sede_mostrar_doctores);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Recibir el id_especialidad pasado desde elegirEspecialidad
        int idEspecialidad = getIntent().getIntExtra("id_especialidad", -1);
       nombreEspecialidad = getIntent().getStringExtra("nombre_especialidad"); // Obtener el nombre de la especialidad
        recyclerView = findViewById(R.id.lst1);
        spinnerSedes = findViewById(R.id.spnElegirsedecita);

        listaDoctores = new ArrayList<>();
        listaSedes = new ArrayList<>();
        requestQueue = Volley.newRequestQueue(this);

        // Configurar RecyclerView y Adaptador
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        adaptadorDoctor = new AdaptadorDoctor(new ArrayList<>());
        recyclerView.setAdapter(adaptadorDoctor);


        // Cargar datos de doctores y sedes con el filtro de especialidad
        cargarDoctores(idEspecialidad); // Pasa el idEspecialidad para filtrar
        cargarSedes();

        // Configurar Spinner para filtrar por sede
        spinnerSedes.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                sedeSeleccionada = listaSedes.get(position).getNombre_sede();
                adaptadorDoctor.filtrarPorSede(sedeSeleccionada); // Filtrar lista de doctores según la sede seleccionada
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void cargarDoctores(int idEspecialidad) {
        String url = "http://192.168.0.199:80/doctor/mostrar_doctores.php";
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        JSONArray jsonArray = response.getJSONArray("datos");

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject objeto = jsonArray.getJSONObject(i);
                            int id_doctor = objeto.getInt("id_doctor");
                            int id_especialidad = objeto.getInt("id_especialidad");
                            int id_sede = objeto.getInt("id_sede");
                            String nombre_doctor = objeto.getString("nombre_doctor");
                            String apellido_doctor = objeto.getString("apellido_doctor");
                            String estado_doctor = objeto.getString("estado_doctor");

                            // Filtrar doctores por id_especialidad
                            if (id_especialidad == idEspecialidad) {
                                Doctor doctor = new Doctor(id_doctor, id_especialidad, id_sede, nombre_doctor, apellido_doctor, estado_doctor);
                                listaDoctores.add(doctor);
                            }
                        }
                        adaptadorDoctor.actualizarLista(listaDoctores);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> {
                    // Manejo de error
                });
        requestQueue.add(request);
    }
    private void cargarSedes() {
        String url = "http://192.168.0.199:80/sede/mostrar_sedes.php";
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        JSONArray jsonArray = response.getJSONArray("datos");
                        listaSedes.add(new Sede(0, "Todas las sedes", ""));
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject objeto = jsonArray.getJSONObject(i);
                            int id_sede = objeto.getInt("id_sede");
                            String nombre_sede = objeto.getString("nombre_sede");
                            String direccion_sede = objeto.getString("direccion_sede");

                            Sede sede = new Sede(id_sede, nombre_sede, direccion_sede);
                            listaSedes.add(sede);
                        }

                        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, obtenerNombresSedes());
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinnerSedes.setAdapter(adapter);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> {
                    // Manejo de error
                });
        requestQueue.add(request);
    }

    private ArrayList<String> obtenerNombresSedes() {
        ArrayList<String> nombresSedes = new ArrayList<>();
        for (Sede sede : listaSedes) {
            nombresSedes.add(sede.getNombre_sede());
        }
        return nombresSedes;
    }

    // Método para obtener el nombre de la sede con el id
    private String obtenerNombreSedePorId(int idSede) {
        for (Sede sede : listaSedes) {
            if (sede.getId_sede() == idSede) {
                return sede.getNombre_sede();
            }
        }
        return "Sede desconocida";  // En caso de que no se encuentre
    }

    private class AdaptadorDoctor extends RecyclerView.Adapter<AdaptadorDoctor.DoctorViewHolder> {
        private ArrayList<Doctor> listaCompleta;
        private ArrayList<Doctor> listaDoctoresFiltrados;

        public AdaptadorDoctor(ArrayList<Doctor> listaDoctores) {
            this.listaCompleta = new ArrayList<>(listaDoctores);
            this.listaDoctoresFiltrados = new ArrayList<>(listaDoctores);
        }

        @NonNull
        @Override
        public DoctorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new DoctorViewHolder(getLayoutInflater().inflate(R.layout.list_doctores, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull DoctorViewHolder holder, int position) {
            holder.imprimir(position);
            holder.cardView.setOnClickListener(v -> {
                Doctor doctorSeleccionado = listaDoctoresFiltrados.get(position);
                int idDoctor = doctorSeleccionado.getId_doctor();
                String nombreDoctor = doctorSeleccionado.getNombre_doctor();
                String apellidoDoctor = doctorSeleccionado.getApellido_doctor();
                String nombreSede = obtenerNombreSedePorId(doctorSeleccionado.getId_sede());

                // Guardar los datos en SharedPreferences
                SharedPreferences sharedPreferences = v.getContext().getSharedPreferences("preDatosCita", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("nombreDoctor", nombreDoctor);
                editor.putString("apellidoDoctor", apellidoDoctor);
                editor.putString("nombreSede", nombreSede);
                editor.apply();

                // Crear un Intent para abrir la clase elegirHorario y pasar el id_doctor
                Intent intent = new Intent(holder.itemView.getContext(), elegirHorario.class);
                intent.putExtra("id_doctor", idDoctor);
                v.getContext().startActivity(intent);
            });
        }

        @Override
        public int getItemCount() {
            return listaDoctoresFiltrados.size();
        }

        public void filtrarPorSede(String sede) {
            listaDoctoresFiltrados.clear();
            if (sede.equals("Todas las sedes")) {
                listaDoctoresFiltrados.addAll(listaCompleta);
            } else {
                for (Doctor doctor : listaCompleta) {
                    if (doctor.getId_sede() == obtenerIdSedePorNombre(sede)) {
                        listaDoctoresFiltrados.add(doctor);
                    }
                }
            }
            notifyDataSetChanged();
        }

        private int obtenerIdSedePorNombre(String nombreSede) {
            for (Sede sede : listaSedes) {
                if (sede.getNombre_sede().equals(nombreSede)) {
                    return sede.getId_sede();
                }
            }
            return -1; // Si no se encuentra
        }

        public void actualizarLista(ArrayList<Doctor> nuevaLista) {
            this.listaCompleta.clear();
            this.listaCompleta.addAll(nuevaLista);
            this.listaDoctoresFiltrados.clear();
            this.listaDoctoresFiltrados.addAll(nuevaLista);
            notifyDataSetChanged();
        }

        public class DoctorViewHolder extends RecyclerView.ViewHolder {
            TextView tvNombreDoctor, tvApellidoDoctor, tvEstadoDoctor, tvSedeDoctor, tvEspecialidadDoctor;
            ImageView ivFotoDoctor;
            public CardView cardView;

            public DoctorViewHolder(@NonNull View itemView) {
                super(itemView);
                tvNombreDoctor = itemView.findViewById(R.id.nombreDoctor);
                tvApellidoDoctor = itemView.findViewById(R.id.apellidoDoctor);
                tvEstadoDoctor = itemView.findViewById(R.id.estadoDoctor);
                tvSedeDoctor = itemView.findViewById(R.id.sedeDoctor);
                tvEspecialidadDoctor=itemView.findViewById(R.id.especialidadDoctor);
                ivFotoDoctor = itemView.findViewById(R.id.listImageDoctor);
                cardView = itemView.findViewById(R.id.main_container);
            }

            public void imprimir(int position) {
                Doctor doctor = listaDoctoresFiltrados.get(position);
                tvNombreDoctor.setText(doctor.getNombre_doctor());
                tvApellidoDoctor.setText(doctor.getApellido_doctor());
                tvEstadoDoctor.setText("Estado: "+doctor.getEstado_doctor());
                tvSedeDoctor.setText("Sede: "+obtenerNombreSedePorId(doctor.getId_sede()));
                tvEspecialidadDoctor.setText("Especialidad: "+nombreEspecialidad);

            }
        }
    }
}
