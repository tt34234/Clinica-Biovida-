package com.example.appclinicabiovida;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.NetworkResponse;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class AnularCita extends AppCompatActivity {

    private RecyclerView recyclerViewCitas;
    private CitasAnularAdapter adaptador;
    private RequestQueue requestQueue;
    private ArrayList<CitaCompleta> listaCitas;
    private SharedPreferences sharedPreferences;

    // Crear un formato de fecha para mostrar
    SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anularcita);
        // Obtener los elementos de la interfaz

        recyclerViewCitas = findViewById(R.id.lstCitasAnular);
        recyclerViewCitas.setLayoutManager(new LinearLayoutManager(this));

        listaCitas = new ArrayList<>();
        adaptador = new CitasAnularAdapter(listaCitas);
        recyclerViewCitas.setAdapter(adaptador);

        requestQueue = Volley.newRequestQueue(this);

        sharedPreferences = getSharedPreferences("preDatosCita", MODE_PRIVATE);
        String dniPaciente = sharedPreferences.getString("dniPaciente", "");

        Log.d("URL", "Solicitando URL: " + "http://192.168.0.199:80/cita/obtener_citas_dni.php?dniPaciente=" + dniPaciente);
        cargarCitas(dniPaciente);
    }

    private void cargarCitas(String dniPaciente) {
        String url = "http://192.168.0.199:80/cita/obtener_citas_dni.php?dniPaciente=" + dniPaciente;
        JsonObjectRequest request = new JsonObjectRequest(url, null,
                response -> {
                    try {
                        Log.d("RespuestaServidor", "Respuesta: " + response.toString());
                        // Verificar si el resultado tiene éxito
                        String exito = response.getString("exito");
                        if (exito.equals("1")) {
                            // Procesar las citas recibidas
                            JSONArray jsonArray = response.getJSONArray("citas");
                            listaCitas.clear();  // Limpiar lista antes de agregar nuevas citas

                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);

                                // Crear objeto CitaCompleta a partir de la respuesta JSON
                                String codigoCita = jsonObject.getString("codigo_cita");
                                String fechaHorario = jsonObject.getString("fecha_horario");
                                String nombreSede = jsonObject.getString("nombre_sede");
                                String nombreEspecialidad = jsonObject.getString("nombre_especialidad");
                                String nombreDoctor = jsonObject.getString("nombre_doctor");
                                String apellidoDoctor = jsonObject.getString("apellido_doctor");
                                // Convertir la fecha a formato Date
                                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                                Date fecha = dateFormat.parse(fechaHorario);

                                // Agregar la cita a la lista
                                listaCitas.add(new CitaCompleta(codigoCita, fecha,  nombreDoctor, apellidoDoctor,nombreSede,  nombreEspecialidad));
                            }

                            // Notificar al adaptador que los datos han cambiado
                            adaptador.notifyDataSetChanged();
                        } else {
                            // Mostrar mensaje de error si no se encontraron citas
                            String mensaje = response.getString("mensaje");
                            Toast.makeText(AnularCita.this, mensaje, Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException | ParseException e) {
                        e.printStackTrace();
                        Toast.makeText(AnularCita.this, "Error al procesar los datos", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    Log.e("ErrorVolley", "Error al cargar las citas: " + error.toString());
                    Toast.makeText(AnularCita.this, "Error al cargar citas", Toast.LENGTH_SHORT).show();
                }) {
            @Override
            protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
                // Mostrar el cuerpo de la respuesta completa en caso de error
                Log.e("VolleyResponse", "Error de respuesta: " + new String(response.data));
                return super.parseNetworkResponse(response);
            }
        };

        requestQueue.add(request);
    }

    private void anularCita(String codigoCita) {
        EditText editTextMotivo = findViewById(R.id.etmMotivoCancelacion);
        String motivoCancelacion = editTextMotivo.getText().toString().trim();

        // Validar que se ingrese un motivo antes de continuar
        if (motivoCancelacion.isEmpty()) {
            Toast.makeText(this, "Por favor, ingrese el motivo de la cancelación", Toast.LENGTH_SHORT).show();
            return;
        }
        String url = "http://192.168.0.199:80/cita/anular_cita.php";
        JSONObject parametros = new JSONObject();
        try {
            parametros.put("codigoCita", codigoCita);
            parametros.put("motivoCancelacion", motivoCancelacion);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest request = new JsonObjectRequest(
                com.android.volley.Request.Method.POST,
                url,
                parametros,
                response -> {
                    try {
                        String mensaje = response.getString("mensaje");
                        Toast.makeText(this, mensaje, Toast.LENGTH_SHORT).show();

                        // Eliminar la cita de la lista si se anuló correctamente
                        listaCitas.removeIf(cita -> cita.getCodigoCita().equals(codigoCita));
                        adaptador.notifyDataSetChanged();
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Error al procesar la respuesta", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    Log.e("Volley", "Error en la solicitud: " + error.toString());
                    Toast.makeText(this, "Error al anular la cita", Toast.LENGTH_SHORT).show();
                }
        );

        requestQueue.add(request);
    }





    private class CitasAnularAdapter extends RecyclerView.Adapter<CitasAnularAdapter.CitaViewHolder> {

        private final ArrayList<CitaCompleta> listaCitas;

        public CitasAnularAdapter(ArrayList<CitaCompleta> listaCitas) {
            this.listaCitas = listaCitas;
        }

        @Override
        public CitaViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_citas_opcionanular, parent, false);
            return new CitaViewHolder(view);
        }

        @Override
        public void onBindViewHolder(CitaViewHolder holder, int position) {
            CitaCompleta cita = listaCitas.get(position);
            holder.bindData(cita);
        }

        @Override
        public int getItemCount() {
            return listaCitas.size();
        }

        public class CitaViewHolder extends RecyclerView.ViewHolder {

            TextView tvCodigoCita, tvFechaCita, tvNombreDoctor, tvEspecialidadCita, tvSedeCita;
            ImageView imageCita;
            Button btnAnular,  btnImagenDetalleCita;

            public CitaViewHolder(View itemView) {
                super(itemView);

                imageCita = itemView.findViewById(R.id.listImageCitaAnular);
                tvCodigoCita = itemView.findViewById(R.id.codigoCita);
                tvFechaCita = itemView.findViewById(R.id.fechaCita);
                tvNombreDoctor = itemView.findViewById(R.id.nombredoctorcita);
                tvEspecialidadCita = itemView.findViewById(R.id.especialidadCita);
                tvSedeCita = itemView.findViewById(R.id.sedeCita);
                btnAnular = itemView.findViewById(R.id.btnAnularCita);
            }

            public void bindData(CitaCompleta cita) {
                tvCodigoCita.setText("Código: " + cita.getCodigoCita());

                // Formatear la fecha antes de mostrarla
                String fechaFormateada = outputFormat.format(cita.getFechaHorario());
                tvFechaCita.setText("Fecha: " + fechaFormateada);

                tvNombreDoctor.setText("Doctor: " + cita.getNombreDoctor() +" "+cita.getApellidoDoctor());
                tvEspecialidadCita.setText("Especialidad: " + cita.getNombreEspecialidad());
                tvSedeCita.setText("Sede: " + cita.getNombreSede());

                btnAnular.setOnClickListener(v -> {
                    anularCita(cita.getCodigoCita());
                });


                ImageButton btnDetalleCita = itemView.findViewById(R.id.btnImagenDetalleCita);
                btnDetalleCita.setOnClickListener(view -> {
                    // Aquí defines qué sucede al hacer clic en el botón
                    Intent intent = new Intent(view.getContext(), detalleCita.class);
                    intent.putExtra("codigoCita", cita.getCodigoCita());
                    view.getContext().startActivity(intent);
                });


            }
        }
    }
}
