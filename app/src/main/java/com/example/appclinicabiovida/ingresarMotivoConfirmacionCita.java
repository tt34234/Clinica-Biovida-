package com.example.appclinicabiovida;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class ingresarMotivoConfirmacionCita extends AppCompatActivity {

    private EditText etmIngresarSintomas;
    private String codigoCita, dniPaciente, estadoCita;
    private int idHorario; // Cambié esta línea a int
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_ingresar_motivo_confirmacion_cita);

        // Acceder a los SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("preDatosCita", MODE_PRIVATE);

        // Obtener los datos guardados en SharedPreferences
        String nombrePaciente = sharedPreferences.getString("nombrePaciente", "Nombre del paciente no disponible");
        String apellidoPaciente = sharedPreferences.getString("apellidoPaciente", "Apellido del paciente no disponible");
        String nombreEspecialidad = sharedPreferences.getString("nombreEspecialidad", "Especialidad no disponible");
        String nombreDoctor = sharedPreferences.getString("nombreDoctor", "Nombre del doctor no disponible");
        String apellidoDoctor = sharedPreferences.getString("apellidoDoctor", "Apellido del doctor no disponible");
        String fechaHorario = sharedPreferences.getString("fechaHorario", "Fecha no disponible");
        String horaInicio = sharedPreferences.getString("horaInicio", "Hora de inicio no disponible");
        String horaFin = sharedPreferences.getString("horaFin", "Hora de fin no disponible");
        String nombreSede = sharedPreferences.getString("nombreSede", "Sede no disponible");


        dniPaciente = sharedPreferences.getString("dniPaciente", "");
        idHorario = sharedPreferences.getInt("idHorario", -1);  // Aquí ya no es necesario declarar la variable como String
        etmIngresarSintomas = findViewById(R.id.etmIngresarSintomas);

        // Asignar los valores a los TextView correspondientes
        TextView nombrePacienteTextView = findViewById(R.id.nombre_paciente);
        TextView apellidoPacienteTextView = findViewById(R.id.apellido_paciente);
        TextView nombreEspecialidadTextView = findViewById(R.id.nombre_especialidad);
        TextView nombreDoctorTextView = findViewById(R.id.nombre_doctor);
        TextView apellidoDoctorTextView = findViewById(R.id.apellido_doctor);
        TextView fechaHorarioTextView = findViewById(R.id.fecha_horario);
        TextView horaInicioTextView = findViewById(R.id.horaInicio_horario);
        TextView horaFinTextView = findViewById(R.id.horaFin_horario);
        TextView nombreSedeTextView = findViewById(R.id.nombre_sede);

        // Establecer los textos en los TextView
        nombrePacienteTextView.setText(nombrePaciente);
        apellidoPacienteTextView.setText(apellidoPaciente);
        nombreEspecialidadTextView.setText(nombreEspecialidad);
        nombreDoctorTextView.setText(nombreDoctor);
        apellidoDoctorTextView.setText(apellidoDoctor);
        fechaHorarioTextView.setText(fechaHorario);
        horaInicioTextView.setText(horaInicio);
        horaFinTextView.setText(horaFin);
        nombreSedeTextView.setText(nombreSede);

        // Configuración para aplicar los márgenes en la interfaz
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        // Generar código de cita único y establecer estado inicial
        codigoCita = generarCodigoCita();
        estadoCita = "programada";


    }

        // Método para generar un código de cita aleatorio
        private String generarCodigoCita() {
            String caracteres = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
            StringBuilder codigo = new StringBuilder();
            Random random = new Random();

            for (int i = 0; i < 8; i++) {
                int index = random.nextInt(caracteres.length());
                codigo.append(caracteres.charAt(index));
            }

            return codigo.toString();
        }


    // Método llamado cuando se presiona el botón "Confirmar Cita"
    public void registrarCita(View view) {
        String sintomas = etmIngresarSintomas.getText().toString().trim();

        // URL del archivo PHP en el servidor
        String url = "http://192.168.0.199:80/cita/insertar_cita.php";

        // Enviar datos al servidor usando Volley
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (response.contains("Datos insertados correctamente")) {
                            // Llamada para actualizar el estado del horario a "ocupado"
                            actualizarEstadoHorario();
                            mostrarDialog("Cita registrada exitosamente", "Código de cita: " + codigoCita);
                        } else if (response.contains("Codigo de cita generado repetido")) {
                            codigoCita = generarCodigoCita();
                            registrarCita(view);  // Reintentar con un nuevo código
                        } else {
                            Toast.makeText(ingresarMotivoConfirmacionCita.this, "Error: " + response, Toast.LENGTH_LONG).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(ingresarMotivoConfirmacionCita.this, "Error al conectar con el servidor", Toast.LENGTH_LONG).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("codigoCita", codigoCita);
                params.put("dniPaciente", dniPaciente);
                params.put("sintomasCita", sintomas);
                params.put("estadoCita", estadoCita);
                params.put("idHorario", String.valueOf(idHorario)); // Convertir idHorario a String
                return params;
            }
        };

        // Agregar la solicitud a la cola de Volley
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    // Método para actualizar el estado del horario a "ocupado"
    private void actualizarEstadoHorario() {
        // URL del archivo PHP que actualizará el estado del horario
        String urlActualizarEstado = "http://192.168.0.199:80/horario/actualizar_estado_horario.php";

        // Enviar datos al servidor usando Volley
        StringRequest stringRequest = new StringRequest(Request.Method.POST, urlActualizarEstado,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (response.contains("Estado de horario actualizado a ocupado")) {

                            Toast.makeText(ingresarMotivoConfirmacionCita.this, "Se actualizo el estado del horario a ocupado", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(ingresarMotivoConfirmacionCita.this, "Error al actualizar estado del horario", Toast.LENGTH_LONG).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(ingresarMotivoConfirmacionCita.this, "Error al conectar con el servidor", Toast.LENGTH_LONG).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("idHorario", String.valueOf(idHorario)); // Enviar el idHorario para actualizar
                return params;
            }
        };

        // Agregar la solicitud a la cola de Volley
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }


    // Método para mostrar un AlertDialog con el mensaje y el código generado
    private void mostrarDialog(String titulo, String mensaje) {
        new AlertDialog.Builder(ingresarMotivoConfirmacionCita.this)
                .setTitle(titulo)
                .setMessage(mensaje)
                .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Aquí puedes agregar cualquier acción al hacer clic en el botón "Aceptar", si lo deseas.
                    }
                })
                .show();
    }
}






