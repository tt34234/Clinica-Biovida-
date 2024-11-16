package com.example.appclinicabiovida;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import android.widget.ArrayAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class editarcitas_administrador extends AppCompatActivity {
    Button b1;
    ImageView b2;
    Spinner ed6;
    TextView ed0, ed1, ed2, ed3, ed4, ed5, ed7;
    private int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editarcitas_administrador);

        ed0 = findViewById(R.id.paciente1);
        ed1 = findViewById(R.id.dniPaciente);
        ed2 = findViewById(R.id.sintomasCita);
        ed5 = findViewById(R.id.motivocancelacionCita);
        ed6 = findViewById(R.id.estado1);
        ed7 = findViewById(R.id.fecha);

        b1 = findViewById(R.id.bt1);
        b2 = findViewById(R.id.bt2);

        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), horario_administrador.class);
                startActivity(i);
                finish();
            }
        });


        // Cargar el array de opciones para el Spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.status_options, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ed6.setAdapter(adapter);

        Intent i = getIntent();
        position = i.getExtras().getInt("position");

        // Cargar los datos de la cita
        ed0.setText(gestionarcitas_administador.listaUsuarios.get(position).getCodigoCita());
        ed1.setText(gestionarcitas_administador.listaUsuarios.get(position).getDniPaciente());
        ed2.setText(gestionarcitas_administador.listaUsuarios.get(position).getSintomas());
        ed5.setText(gestionarcitas_administador.listaUsuarios.get(position).getMotivocancelacionCita());
        String idHorario = gestionarcitas_administador.listaUsuarios.get(position).getIdHorario();

        // Obtener el estado de la base de datos (de la cita actual)
        String estado = gestionarcitas_administador.listaUsuarios.get(position).getEstadoCita();
        // Establecer el valor del estado en el Spinner
        int spinnerPosition = adapter.getPosition(estado);
        ed6.setSelection(spinnerPosition);

        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Update();
            }
        });

        // URL para obtener los horarios sin id_doctor
        String url = "http://192.168.0.199/horario/mostrarfecha.php?id_horario=" + idHorario;

        // Realizar la solicitud GET
        StringRequest request = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Log para mostrar la respuesta del servidor
                        Log.d("ServerResponse", "Respuesta del servidor: " + response);

                        try {
                            // Aquí asumimos que el servidor devuelve un JSON con la fecha del horario
                            JSONObject jsonResponse = new JSONObject(response);
                            JSONArray horariosArray = jsonResponse.getJSONArray("datos");

                            // Aquí accedemos al primer horario, ya que esperamos solo uno
                            if (horariosArray.length() > 0) {
                                JSONObject horario = horariosArray.getJSONObject(0);  // Accede al primer horario
                                String fechaHorario = horario.getString("fecha_horario");

                                // Log para verificar la fecha
                                Log.d("Fecha", "Fecha del horario: " + fechaHorario);

                                // Mostrar la fecha en el TextView (como lo necesitas)
                                ed7.setText(fechaHorario);  // Asegúrate de que `ed7` es el TextView donde deseas mostrar la fecha
                            } else {
                                Toast.makeText(editarcitas_administrador.this, "No se encontraron horarios para este ID", Toast.LENGTH_SHORT).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(editarcitas_administrador.this, "Error al obtener la fecha", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Error", "Error al obtener la fecha: " + error.getMessage());
                        Toast.makeText(editarcitas_administrador.this, "Error al obtener la fecha", Toast.LENGTH_SHORT).show();
                    }
                });

        // Crear y añadir la solicitud a la cola
        RequestQueue requestQueue = Volley.newRequestQueue(editarcitas_administrador.this);
        requestQueue.add(request);
    }

    public void Update() {
        // Obtén los valores de los campos
        String p_paciente = ed0.getText().toString().trim();
        String p_dni = ed1.getText().toString().trim();
        String p_sintomas = ed2.getText().toString().trim();
        String p_motivo = ed5.getText().toString().trim();
        String p_estado = ed6.getSelectedItem().toString().trim();
        String p_idH = ed7.getText().toString().trim();

        // Agregar logs para verificar los valores antes de la solicitud
        Log.d("Update", "codigoCita: " + p_paciente);
        Log.d("Update", "dniPaciente: " + p_dni);
        Log.d("Update", "sintomasCita: " + p_sintomas);
        Log.d("Update", "estadoCita: " + p_motivo);
        Log.d("Update", "motivocancelacionCita: " + p_estado);
        Log.d("Update", "idHorario: " + p_idH);

        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Actualizando...");
        progressDialog.show();

        // Realiza la solicitud POST
        StringRequest request = new StringRequest(Request.Method.POST, "http://192.168.0.199:80/cita/actualizar_.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Verifica la respuesta del servidor
                        Log.d("Update", "Respuesta: " + response);
                        Toast.makeText(editarcitas_administrador.this, "Actualizado correctamente", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                        startActivity(new Intent(getApplicationContext(), gestionarcitas_administador.class));
                        finish();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // Maneja el error y muestra el mensaje
                Log.e("Update", "Error: " + error.getMessage());
                Toast.makeText(editarcitas_administrador.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("codigoCita", p_paciente);
                params.put("dniPaciente", p_dni);
                params.put("sintomasCita", p_sintomas);
                params.put("estadoCita", p_estado);
                params.put("motivocancelacionCita", p_motivo);
                params.put("idHorario", p_idH);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(editarcitas_administrador.this);
        requestQueue.add(request);
    }
}