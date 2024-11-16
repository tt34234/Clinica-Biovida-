package com.example.appclinicabiovida;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Date;
import java.sql.Time;
import java.util.ArrayList;


public class elegirHorario extends AppCompatActivity {

    private RecyclerView recyclerViewHorarios;
    private AdaptadorHorario adaptadorHorario;
    private RequestQueue requestQueue;
    private int idDoctor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_elegir_horario);

        recyclerViewHorarios = findViewById(R.id.lst1);
        requestQueue = Volley.newRequestQueue(this);

        // Recibe el id_doctor pasado desde la clase anterior
        idDoctor = getIntent().getIntExtra("id_doctor", -1);

        // Configura RecyclerView y Adaptador
        recyclerViewHorarios.setLayoutManager(new LinearLayoutManager(this));
        adaptadorHorario = new AdaptadorHorario(new ArrayList<>());
        recyclerViewHorarios.setAdapter(adaptadorHorario);

        // Carga los horarios para el doctor seleccionado
        cargarHorarios(idDoctor);
    }

    private void cargarHorarios(int idDoctor) {
        String url = "http://192.168.0.199:80/horario/mostrar_horarios.php?id_doctor=" + idDoctor;
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        JSONArray jsonArray = response.getJSONArray("datos");
                        ArrayList<Horario> listaHorarios = new ArrayList<>();

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject objeto = jsonArray.getJSONObject(i);

                            int idHorario = objeto.getInt("id_horario");
                            String fechaHorarioStr = objeto.getString("fecha_horario");
                            String horaInicioStr = objeto.getString("horaInicio_horario");
                            String horaFinStr = objeto.getString("horaFin_horario");
                            String estadoHorario = objeto.getString("estado_horario");

                            // Convierte las cadenas a los tipos Date y Time
                            Date fechaHorario = Date.valueOf(fechaHorarioStr);
                            Time horaInicio = Time.valueOf(horaInicioStr);
                            Time horaFin = Time.valueOf(horaFinStr);

                            Horario horario = new Horario(idHorario, idDoctor, fechaHorario, horaInicio, horaFin, estadoHorario);
                            listaHorarios.add(horario);
                        }

                        adaptadorHorario.actualizarLista(listaHorarios);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> {
                    // Manejo de error
                });
        requestQueue.add(request);
    }


    private class AdaptadorHorario extends RecyclerView.Adapter<AdaptadorHorario.HorarioViewHolder> {
        private ArrayList<Horario> listaHorarios;

        public AdaptadorHorario(ArrayList<Horario> listaHorarios) {
            this.listaHorarios = listaHorarios;
        }

        @NonNull
        @Override
        public HorarioViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new HorarioViewHolder(getLayoutInflater().inflate(R.layout.list_horarios1, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull HorarioViewHolder holder, int position) {
            holder.imprimir(position);
        }

        @Override
        public int getItemCount() {
            return listaHorarios.size();
        }

        public void actualizarLista(ArrayList<Horario> nuevaLista) {
            this.listaHorarios.clear();
            this.listaHorarios.addAll(nuevaLista);
            notifyDataSetChanged();
        }

        public class HorarioViewHolder extends RecyclerView.ViewHolder {
            TextView tvFechaHorario, tvHoraInicio, tvHoraFin, tvEstadoHorario;
            ImageView ivHorario;
            Button btnSeleccionarHorario;

            public HorarioViewHolder(@NonNull View itemView) {
                super(itemView);
                tvFechaHorario = itemView.findViewById(R.id.fechaHorario);
                tvHoraInicio = itemView.findViewById(R.id.horaInicio);
                tvHoraFin = itemView.findViewById(R.id.horaFin);
                tvEstadoHorario = itemView.findViewById(R.id.estadoHorario);
                ivHorario = itemView.findViewById(R.id.listImageHorario);
                btnSeleccionarHorario = itemView.findViewById(R.id.btnSeleccionarHorario);
            }

            public void imprimir(int position) {
                Horario horario = listaHorarios.get(position);

                // Asigna los valores a los elementos de la interfaz
                tvFechaHorario.setText("Fecha: " + horario.getFechaHorario().toString());
                tvHoraInicio.setText("Hora Inicio: " + horario.getHoraInicio().toString());
                tvHoraFin.setText("Hora Fin: " + horario.getHoraFin().toString());
                tvEstadoHorario.setText("Estado: " + horario.getEstadoHorario());



                // Verificar el estado del horario
                if (horario.getEstadoHorario().equals("ocupado")) {
                    // Si el horario está ocupado, desactivar el botón y cambiar su color
                    btnSeleccionarHorario.setText("Horario Ocupado");
                    btnSeleccionarHorario.setBackgroundColor(getResources().getColor(R.color.red)); // Usa el color rojo del archivo colors.xml
                    btnSeleccionarHorario.setEnabled(false); // Deshabilita el botón
                } else {
                    // Si el horario está disponible, habilitar el botón y cambiar su color
                    btnSeleccionarHorario.setText("Seleccionar Horario");
                    btnSeleccionarHorario.setBackgroundColor(getResources().getColor(R.color.green)); // Usa el color verde del archivo colors.xml
                    btnSeleccionarHorario.setEnabled(true); // Habilita el botón
                }

                // Manejador de eventos para el botón Seleccionar Horario
                btnSeleccionarHorario.setOnClickListener(v -> {
                    if (horario.getEstadoHorario().equals("ocupado")) {
                        // Mostrar mensaje si el horario está ocupado
                        Toast.makeText(elegirHorario.this, "Este horario está ocupado", Toast.LENGTH_SHORT).show();
                    } else {
                        // Guardar en SharedPreferences si el horario está disponible
                        SharedPreferences sharedPreferences = elegirHorario.this.getSharedPreferences("preDatosCita", MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();

                        // Guardar fecha, horas y el id del horario seleccionado
                        editor.putString("fechaHorario", horario.getFechaHorario().toString());
                        editor.putString("horaInicio", horario.getHoraInicio().toString());
                        editor.putString("horaFin", horario.getHoraFin().toString());
                        editor.putInt("idHorario", horario.getIdHorario());

                        // Aplicar los cambios
                        editor.apply();

                        // Confirmación de selección
                        Toast.makeText(elegirHorario.this, "Horario seleccionado: " + horario.getIdHorario(), Toast.LENGTH_SHORT).show();

                        // Crear el Intent para abrir la actividad ingresarMotivoConfirmacionCita
                        Intent intent = new Intent(elegirHorario.this, ingresarMotivoConfirmacionCita.class);
                        // Iniciar la actividad
                        startActivity(intent);
                    }
                }
                );
            }
        }
    }
}

