package com.example.appclinicabiovida;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONArray;
import org.json.JSONObject;
import android.util.Log;

public class detalleCita extends AppCompatActivity {

    // Declaramos las variables para los TextView y EditText
    private TextView txvCodigoCita, txvDniPaciente, txvpaciente, txvEstadoCita, txvFechaCita, txvHoraInicio,
            txvHoraFin, txvDoctor, txvEspecialidad, txvSede;
    private EditText etmlSintomas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_cita);

        // Inicializamos las vistas
        txvCodigoCita = findViewById(R.id.txvcodigocita);
        txvDniPaciente = findViewById(R.id.txvdnipaciente);
        txvEstadoCita = findViewById(R.id.txvestadocita);
        txvFechaCita = findViewById(R.id.txvfechacita);
        txvHoraInicio = findViewById(R.id.txvhorainicio);
        txvHoraFin = findViewById(R.id.txvhorafin);
        txvDoctor = findViewById(R.id.txvdoctor);
        txvEspecialidad = findViewById(R.id.txvespecialidad);
        txvSede = findViewById(R.id.txvsede);
        etmlSintomas = findViewById(R.id.etmlsintomas);
        txvpaciente = findViewById(R.id.txvpaciente);

        // Obtenemos el codigoCita desde la intención
        String codigoCita = getIntent().getStringExtra("codigoCita");

        // Si el codigoCita no es nulo, hacemos la solicitud para obtener los datos
        if (codigoCita != null) {
            cargarDetalleCita(codigoCita);
        }
    }

    private void cargarDetalleCita(String codigoCita) {
        // URL de tu PHP que obtiene los detalles de la cita
        String url = "http://192.168.0.199:80/cita/mostrar_citaxcodigo.php?codigo_cita=" + codigoCita;

        // Usamos Volley para hacer la solicitud
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            // Log para verificar la respuesta recibida
                            Log.d("DetalleCita", "Respuesta del servidor: " + response);

                            // Parseamos la respuesta JSON
                            JSONObject jsonResponse = new JSONObject(response);
                            JSONArray citasArray = jsonResponse.getJSONArray("citas");

                            if (citasArray.length() > 0) {
                                JSONObject cita = citasArray.getJSONObject(0);

                                // Cargamos los datos en los TextView y EditText
                                txvCodigoCita.setText("Código de Cita: " + cita.getString("codigo_cita"));
                                txvDniPaciente.setText("DNI del paciente: " + cita.getString("dni"));
                                txvpaciente.setText("Paciente: " + cita.getString("nombre_paciente") + " " + cita.getString("apellido_paciente"));
                                txvEstadoCita.setText("Estado de la cita: " + cita.getString("estado_cita"));
                                txvFechaCita.setText("Fecha: " + cita.getString("fecha_horario"));
                                txvHoraInicio.setText("Hora inicio: " + cita.getString("horaInicio_horario"));
                                txvHoraFin.setText("Hora fin: " + cita.getString("horaFin_horario"));
                                txvDoctor.setText("Doctor: " + cita.getString("nombre_doctor") + " " + cita.getString("apellido_doctor"));
                                txvEspecialidad.setText("Especialidad: " + cita.getString("nombre_especialidad"));
                                txvSede.setText("Sede: " + cita.getString("nombre_sede"));
                                etmlSintomas.setText(cita.getString("sintomas"));
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                },
                error -> {
                    // Aquí puedes manejar cualquier error
                    Log.e("DetalleCita", "Error en la solicitud: " + error.getMessage());
                    error.printStackTrace();
                });

        // Añadimos la solicitud a la cola de Volley
        Volley.newRequestQueue(this).add(stringRequest);
    }

    public void btnRegresarAnularCita(View v) {
        Intent objIntent = new Intent(detalleCita.this, AnularCita.class);
        startActivity(objIntent);
        finish();
    }
}