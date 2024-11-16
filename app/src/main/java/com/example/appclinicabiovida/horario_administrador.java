package com.example.appclinicabiovida;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class horario_administrador extends AppCompatActivity {
    ImageView b1;
    private RecyclerView recyclerView;
    private horario_administrador.AdaptadorDoctor adaptadorDoctor;
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
        setContentView(R.layout.activity_horario_administrador);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        nombreEspecialidad = getIntent().getStringExtra("nombre_especialidad");
        recyclerView = findViewById(R.id.lst1);
        spinnerSedes = findViewById(R.id.spnElegirsedecita2);
        b1=findViewById(R.id.bt1);
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Intent i = new Intent(getApplicationContext(), gestionarcitas_administador.class);
                startActivity(i);
                finish();
            }
        });
        listaDoctores = new ArrayList<>();
        listaSedes = new ArrayList<>();
        requestQueue = Volley.newRequestQueue(this);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        adaptadorDoctor = new horario_administrador.AdaptadorDoctor(new ArrayList<>());
        recyclerView.setAdapter(adaptadorDoctor);
        cargarEspecialidades();
        cargarDoctores(); // Llamada sin parámetros para cargar todos los doctores
        cargarSedes();

        spinnerSedes.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                sedeSeleccionada = listaSedes.get(position).getNombre_sede();
                adaptadorDoctor.filtrarPorSede(sedeSeleccionada);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }
    private HashMap<Integer, Especialidad> especialidadesMap = new HashMap<>();

    private void cargarEspecialidades() {
        String url = "http://192.168.0.199:80/especialidad/mostrar_especialidades.php";
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        JSONArray jsonArray = response.getJSONArray("datos");

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject objeto = jsonArray.getJSONObject(i);
                            int id_especialidad = objeto.getInt("id_especialidad");
                            String nombre_especialidad = objeto.getString("nombre_especialidad");
                            String urlImagen_especialidad = objeto.getString("urlImagen_especialidad");

                            Especialidad especialidad = new Especialidad(id_especialidad, nombre_especialidad, urlImagen_especialidad);
                            especialidadesMap.put(id_especialidad, especialidad);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> {
                    // Manejo de error
                });
        requestQueue.add(request);
    }

    private void cargarDoctores() {
        String url = "http://192.168.0.199:80/doctor/mostrar_doctores.php";
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        JSONArray jsonArray = response.getJSONArray("datos");

                        List<DoctorViewModel> listaDoctorViewModel = new ArrayList<>();

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject objeto = jsonArray.getJSONObject(i);
                            int id_doctor = objeto.getInt("id_doctor");
                            int id_especialidad = objeto.getInt("id_especialidad");
                            int id_sede = objeto.getInt("id_sede");
                            String nombre_doctor = objeto.getString("nombre_doctor");
                            String apellido_doctor = objeto.getString("apellido_doctor");
                            String estado_doctor = objeto.getString("estado_doctor");

                            // Crear el objeto Doctor
                            Doctor doctor = new Doctor(id_doctor, id_especialidad, id_sede, nombre_doctor, apellido_doctor, estado_doctor);

                            // Obtener el nombre de la especialidad usando el id_especialidad
                            String nombre_especialidad = especialidadesMap.containsKey(id_especialidad)
                                    ? especialidadesMap.get(id_especialidad).getNombre_especialidad()
                                    : "Especialidad desconocida";

                            // Crear el DoctorViewModel
                            DoctorViewModel doctorViewModel = new DoctorViewModel(doctor, nombre_especialidad);
                            listaDoctorViewModel.add(doctorViewModel);
                        }
                        // Actualizar el adaptador con la lista de DoctorViewModel
                        adaptadorDoctor.actualizarLista(listaDoctorViewModel);
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

    public class AdaptadorDoctor extends RecyclerView.Adapter<AdaptadorDoctor.DoctorViewHolder> {
        private List<DoctorViewModel> lista;
        private ArrayList<DoctorViewModel> listaDoctoresFiltrados;

        public AdaptadorDoctor(ArrayList<DoctorViewModel> listaDoctores) {
            this.lista = listaDoctores;
            this.listaDoctoresFiltrados = new ArrayList<>(listaDoctores);
        }

        @NonNull
        @Override
        public DoctorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new DoctorViewHolder(getLayoutInflater().inflate(R.layout.list_doctores_editar, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull DoctorViewHolder holder, int position) {
            DoctorViewModel doctorViewModel = listaDoctoresFiltrados.get(position);
            holder.imprimir(doctorViewModel);  // Pasa el objeto doctorViewModel completo

        }


        @Override
        public int getItemCount() {
            return listaDoctoresFiltrados.size();
        }

        public void filtrarPorSede(String sede) {
            listaDoctoresFiltrados.clear();
            if (sede.equals("Todas las sedes")) {
                listaDoctoresFiltrados.addAll(lista);
            } else {
                for (DoctorViewModel doctorViewModel : lista) {
                    Doctor doctor = doctorViewModel.getDoctor();
                    if (doctor.getId_sede() == obtenerIdSedePorNombre(sede)) {
                        listaDoctoresFiltrados.add(doctorViewModel);
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

        public void actualizarLista(List<DoctorViewModel> listaDoctorViewModel) {
            this.lista = listaDoctorViewModel;
            this.listaDoctoresFiltrados = new ArrayList<>(listaDoctorViewModel);
            notifyDataSetChanged();
        }

        public class DoctorViewHolder extends RecyclerView.ViewHolder {
            TextView tvNombreDoctor, tvApellidoDoctor, tvEstadoDoctor, tvSedeDoctor, tvEspecialidadDoctor;
            ImageView ivFotoDoctor, tvlistar;
            Button btnCambiarEstado;
            public CardView cardView;

            public DoctorViewHolder(@NonNull View itemView) {
                super(itemView);
                tvNombreDoctor = itemView.findViewById(R.id.nombreDoctor);
                tvApellidoDoctor = itemView.findViewById(R.id.apellidoDoctor);
                tvEstadoDoctor = itemView.findViewById(R.id.estadoDoctor);
                tvSedeDoctor = itemView.findViewById(R.id.sedeDoctor);
                tvEspecialidadDoctor = itemView.findViewById(R.id.especialidadDoctor);
                ivFotoDoctor = itemView.findViewById(R.id.listImageDoctor);
                cardView = itemView.findViewById(R.id.main_container);
                tvlistar = itemView.findViewById(R.id.editar);
                btnCambiarEstado = itemView.findViewById(R.id.btncambiarestado);

                btnCambiarEstado.setOnClickListener(v -> {
                    Doctor doctor = listaDoctoresFiltrados.get(getAdapterPosition()).getDoctor();
                    String nuevoEstado = doctor.getEstado_doctor().equals("Activo") ? "Inactivo" : "Activo";
                    actualizarEstadoDoctor(doctor.getId_doctor(), nuevoEstado);
                });
                // Configurar el OnClickListener en la imagen de editar
                tvlistar.setOnClickListener(v -> {
                    // Obtener la información que necesites, por ejemplo el ID del doctor
                    Doctor doctor = listaDoctoresFiltrados.get(getAdapterPosition()).getDoctor();

                    // Crear un Intent para redirigir a la actividad de edición de citas
                    Intent intent = new Intent(itemView.getContext(), editarhorario_admin.class);
                    // Pasar datos a la nueva actividad si es necesario
                    intent.putExtra("id_doctor", doctor.getId_doctor()); // Ejemplo de pasar datos
                    itemView.getContext().startActivity(intent);
                });
            }
            public void actualizarEstadoDoctor(int idDoctor, String nuevoEstado) {
                if (idDoctor == 0 || nuevoEstado == null) {
                    Toast.makeText(horario_administrador.this, "Error: Parámetros inválidos", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Verificar que los parámetros son correctos
                Log.d("ActualizarEstado", "id_doctor: " + idDoctor + ", nuevoEstado: " + nuevoEstado);

                String url = "http://192.168.0.199:80/doctor/actualizar_doctores.php";
                JSONObject parametros = new JSONObject();
                try {
                    parametros.put("id_doctor", idDoctor);
                    parametros.put("estado_doctor", nuevoEstado);  // Enviar el nuevo estado (Activo/Inactivo)

                    // Verificar el contenido del JSON que se va a enviar
                    Log.d("JSON Request", parametros.toString());
                } catch (JSONException e) {
                    Log.e("Volley", "Error al construir JSON para enviar", e);
                    return;
                }

                StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                        response -> {
                            try {
                                Log.d("Volley", "Respuesta recibida: " + response);
                                JSONObject jsonResponse = new JSONObject(response);
                                boolean success = jsonResponse.getBoolean("success");
                                String message = jsonResponse.getString("message");

                                if (success) {
                                    Toast.makeText(horario_administrador.this, message, Toast.LENGTH_SHORT).show();

                                    // Actualizar el estado del doctor localmente en la lista
                                    for (DoctorViewModel doctorViewModel : listaDoctoresFiltrados) {
                                        Doctor doctor = doctorViewModel.getDoctor();
                                        if (doctor.getId_doctor() == idDoctor) {
                                            doctor.setEstado_doctor(nuevoEstado);  // Actualizar el estado
                                            break;
                                        }
                                    }

                                    // Notificar al adaptador que se ha actualizado el estado
                                    adaptadorDoctor.notifyDataSetChanged();
                                } else {
                                    Toast.makeText(horario_administrador.this, "Error: " + message, Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException e) {
                                Log.e("Volley", "Error al procesar JSON en la respuesta", e);
                            }
                        },
                        error -> {
                            Log.e("Volley", "Error en la solicitud", error);
                            Toast.makeText(horario_administrador.this, "Error en la solicitud de actualización", Toast.LENGTH_SHORT).show();
                        }) {
                    @Override
                    public byte[] getBody() throws AuthFailureError {
                        return parametros.toString().getBytes();
                    }

                    @Override
                    public String getBodyContentType() {
                        return "application/json; charset=utf-8";
                    }
                };

                if (requestQueue == null) {
                    requestQueue = Volley.newRequestQueue(horario_administrador.this);
                }
                requestQueue.add(stringRequest);
            }




            public void imprimir(DoctorViewModel doctorViewModel) {
                Doctor doctor = doctorViewModel.getDoctor();
                tvNombreDoctor.setText(doctor.getNombre_doctor());
                tvApellidoDoctor.setText(doctor.getApellido_doctor());
                tvEstadoDoctor.setText("Estado: " + doctor.getEstado_doctor());
                tvSedeDoctor.setText("Sede: " + obtenerNombreSedePorId(doctor.getId_sede()));
                tvEspecialidadDoctor.setText("Especialidad: " + doctorViewModel.getNombreEspecialidad());
            }
        }}
}
