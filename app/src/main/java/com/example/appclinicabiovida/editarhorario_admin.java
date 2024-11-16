package com.example.appclinicabiovida;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Date;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Calendar;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.app.DatePickerDialog;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import androidx.annotation.NonNull;
public class editarhorario_admin extends AppCompatActivity {
    ImageView b1;
    private RecyclerView recyclerViewHorarios;
    private AdaptadorHorario adaptadorHorario;
    private HashSet<Long> fechasOcupadas = new HashSet<>();
    private RequestQueue requestQueue;
    private int idDoctor;
    private String formatearFecha(String fecha) {
        String[] partes = fecha.split("/");  // Asume que la fecha está en formato d/m/yyyy
        return partes[2] + "-" + partes[1] + "-" + partes[0];  // Devuelve en formato yyyy-mm-dd
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editarhorario_admin);

        recyclerViewHorarios = findViewById(R.id.lst1);
        requestQueue = Volley.newRequestQueue(this);

        // Recibe el id_doctor pasado desde la clase anterior
        idDoctor = getIntent().getIntExtra("id_doctor", -1);

        // Configura RecyclerView y Adaptador
        recyclerViewHorarios.setLayoutManager(new LinearLayoutManager(this));
        adaptadorHorario = new AdaptadorHorario(new ArrayList<>());
        recyclerViewHorarios.setAdapter(adaptadorHorario);
        b1=findViewById(R.id.bt1);
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Intent i = new Intent(getApplicationContext(), gestionarcitas_administador.class);
                startActivity(i);
                finish();
            }
        });
        // Carga los horarios para el doctor seleccionado
        cargarHorarios(idDoctor);
    }

    private void cargarHorarios(int idDoctor) {
        String url = "http://192.168.0.199:80/horario/mostrar_horarios.php?id_doctor=" + idDoctor;
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        Log.d("Respuesta", response.toString());
                        JSONArray jsonArray = response.getJSONArray("datos");
                        ArrayList<Horario> listaHorarios = new ArrayList<>();

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject objeto = jsonArray.getJSONObject(i);
                            int idHorario = objeto.getInt("id_horario");
                            String fechaHorarioStr = objeto.getString("fecha_horario");
                            String horaInicioStr = objeto.getString("horaInicio_horario");
                            String horaFinStr = objeto.getString("horaFin_horario");
                            String estadoHorario = objeto.getString("estado_horario");

                            // Convierte fecha y horas
                            java.sql.Date fechaHorario = Date.valueOf(fechaHorarioStr);
                            Time horaInicio = Time.valueOf(horaInicioStr);
                            Time horaFin = Time.valueOf(horaFinStr);

                            // Guarda la fecha en 'fechasOcupadas' en milisegundos
                            fechasOcupadas.add(fechaHorario.getTime());

                            // Crea un nuevo objeto Horario y agrega a la lista
                            Horario horario = new Horario(idHorario, idDoctor, fechaHorario, horaInicio, horaFin, estadoHorario);
                            listaHorarios.add(horario);
                        }

                        // Actualiza el adaptador con la lista de horarios cargados
                        adaptadorHorario.actualizarLista(listaHorarios);
                    } catch (JSONException e) {
                        Log.e("Error", "Error en el procesamiento del JSON: " + e.getMessage());
                        e.printStackTrace();
                    }
                },
                error -> Log.e("Error", "Error al cargar los horarios: " + error.getMessage())
        );

        // Agrega la solicitud a la cola de peticiones
        requestQueue.add(request);
    }
    private void actualizarHorasHorario(int idHorario, String nuevaHoraInicio, String nuevaHoraFin) {
        String url = "http://192.168.0.199:80/horario/actualizar_horas.php";

        HashMap<String, String> parametros = new HashMap<>();
        parametros.put("idHorario", String.valueOf(idHorario));
        parametros.put("nuevaHoraInicio", nuevaHoraInicio);
        parametros.put("nuevaHoraFin", nuevaHoraFin);

        StringRequest request = new StringRequest(Request.Method.POST, url,
                response -> {
                    Toast.makeText(editarhorario_admin.this, response, Toast.LENGTH_SHORT).show();
                },
                error -> {
                    Log.e("Error", error.toString());
                    Toast.makeText(editarhorario_admin.this, "Error en la conexión", Toast.LENGTH_SHORT).show();
                }) {
            @Override
            protected Map<String, String> getParams() {
                return parametros;
            }
        };

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
            return new HorarioViewHolder(getLayoutInflater().inflate(R.layout.list_horarios, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull HorarioViewHolder holder, int position) {
            Horario horario = listaHorarios.get(position);
            holder.imprimir(horario);
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
            Button btnEditarFecha,btnEditarHora;
            Horario horarioActual;

            public HorarioViewHolder(@NonNull View itemView) {
                super(itemView);
                tvFechaHorario = itemView.findViewById(R.id.fechaHorario);
                tvHoraInicio = itemView.findViewById(R.id.horaInicio);
                tvHoraFin = itemView.findViewById(R.id.horaFin);
                tvEstadoHorario = itemView.findViewById(R.id.estadoHorario);
                ivHorario = itemView.findViewById(R.id.listImageHorario);
                btnEditarFecha = itemView.findViewById(R.id.btnEditarfecha);
                btnEditarHora = itemView.findViewById(R.id.btnEditarHora);


                // Configura el OnClickListener para abrir el calendario solo para editar la fecha
                btnEditarFecha.setOnClickListener(this::abrirCalendario);
                btnEditarHora.setOnClickListener(this::abrirDialogoHora);
            }

            private void abrirCalendario(View view) {
                if (horarioActual != null) {
                    Calendar cal = Calendar.getInstance();
                    cal.setTimeInMillis(horarioActual.getFechaHorario().getTime());

                    int anio = cal.get(Calendar.YEAR);
                    int mes = cal.get(Calendar.MONTH);
                    int dia = cal.get(Calendar.DAY_OF_MONTH);

                    DatePickerDialog dpd = new DatePickerDialog(editarhorario_admin.this, (view1, year, month, dayOfMonth) -> {
                        String nuevaFecha = year + "-" + (month + 1) + "-" + dayOfMonth;
                        tvFechaHorario.setText("Fecha: " + nuevaFecha);
                        horarioActual.setFechaHorario(Date.valueOf(nuevaFecha)); // Actualiza la fecha en el objeto horarioActual
                    }, anio, mes, dia);

                    dpd.show();
                }
            }
            private void abrirDialogoHora(View view) {
                // Extrae la hora y los minutos de la hora de inicio actual del horario
                Calendar cInicio = Calendar.getInstance();
                cInicio.setTime(horarioActual.getHoraInicio());
                int horaInicio = cInicio.get(Calendar.HOUR_OF_DAY);
                int minInicio = cInicio.get(Calendar.MINUTE);

                // Abre un diálogo para seleccionar la hora de inicio, con la hora inicial establecida a la actual
                TimePickerDialog tpdInicio = new TimePickerDialog(editarhorario_admin.this, (timePicker, hourOfDay, minute) -> {
                    String nuevaHoraInicio = String.format("%02d:%02d:00", hourOfDay, minute); // Formato con ceros a la izquierda
                    tvHoraInicio.setText("Hora Inicio: " + nuevaHoraInicio);
                    horarioActual.setHoraInicio(Time.valueOf(nuevaHoraInicio));

                    // Extrae la hora y los minutos de la hora de fin actual del horario
                    Calendar cFin = Calendar.getInstance();
                    cFin.setTime(horarioActual.getHoraFin());
                    int horaFin = cFin.get(Calendar.HOUR_OF_DAY);
                    int minFin = cFin.get(Calendar.MINUTE);

                    // Luego de seleccionar la hora de inicio, abre el diálogo para seleccionar la hora de fin con la hora inicial establecida a la actual
                    TimePickerDialog tpdFin = new TimePickerDialog(editarhorario_admin.this, (timePickerFin, hourOfDayFin, minuteFin) -> {
                        String nuevaHoraFin = String.format("%02d:%02d:00", hourOfDayFin, minuteFin); // Formato con ceros a la izquierda
                        tvHoraFin.setText("Hora Fin: " + nuevaHoraFin);
                        horarioActual.setHoraFin(Time.valueOf(nuevaHoraFin));

                        // Después de obtener las horas, llama al método para actualizar en el servidor
                        actualizarHorario(horarioActual.getIdHorario(), horarioActual.getFechaHorario().toString(), nuevaHoraInicio, nuevaHoraFin);
                    }, horaFin, minFin, true); // <-- 'true' indica que el formato es de 24 horas

                    tpdFin.show();
                }, horaInicio, minInicio, true); // <-- 'true' indica que el formato es de 24 horas

                tpdInicio.show();
            }

            private void actualizarHorario(int idHorario, String nuevaFecha, String nuevaHoraInicio, String nuevaHoraFin) {
                String url = "http://192.168.0.199:80/horario/actualizar_fecha.php";

                HashMap<String, String> parametros = new HashMap<>();
                parametros.put("idHorario", String.valueOf(idHorario));
                parametros.put("nuevaFecha", nuevaFecha);
                parametros.put("nuevaHoraInicio", nuevaHoraInicio);
                parametros.put("nuevaHoraFin", nuevaHoraFin);

                StringRequest request = new StringRequest(Request.Method.POST, url,
                        response -> {
                            // Mostrar la respuesta del servidor
                            Toast.makeText(editarhorario_admin.this, "Horario actualizado correctamente", Toast.LENGTH_SHORT).show();
                        },
                        error -> {
                            // Manejar errores
                            Toast.makeText(editarhorario_admin.this, "Error en la conexión", Toast.LENGTH_SHORT).show();
                        }) {
                    @Override
                    protected Map<String, String> getParams() {
                        return parametros;
                    }
                };

                // Agregar la solicitud a la cola
                requestQueue.add(request);
            }

            public void imprimir(Horario horario) {
                this.horarioActual = horario; // Almacena el horario actual para usarlo en el calendario y selector de hora
                tvFechaHorario.setText("Fecha: " + horario.getFechaHorario().toString());
                tvHoraInicio.setText("Hora Inicio: " + horario.getHoraInicio().toString());
                tvHoraFin.setText("Hora Fin: " + horario.getHoraFin().toString());
                tvEstadoHorario.setText("Estado: " + horario.getEstadoHorario());
            }
        }
    }
}


