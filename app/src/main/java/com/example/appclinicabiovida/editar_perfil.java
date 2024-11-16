package com.example.appclinicabiovida;


import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.content.DialogInterface;
import androidx.appcompat.app.AlertDialog;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class editar_perfil extends AppCompatActivity implements View.OnClickListener{

    ImageView btnInicio, btnInfo, btnPerfil, btnCerrar;
    EditText ed1, ed2, ed3, ed4, ed5, ed6;
    Button btnGuardar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_perfil);

        // Configuración de Edge-to-Edge
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Inicialización de los elementos de la interfaz
        btnInicio = findViewById(R.id.btnInicio);
        btnInfo = findViewById(R.id.btnInfo);
        btnPerfil = findViewById(R.id.btnPerfil);
        btnCerrar = findViewById(R.id.btnCerrar);
        btnGuardar = findViewById(R.id.btnGuardar);

        ed1 = findViewById(R.id.dni);
        ed2 = findViewById(R.id.nombre);
        ed3 = findViewById(R.id.apellido);
        ed4 = findViewById(R.id.telefono);
        ed5 = findViewById(R.id.genero);
        ed6 = findViewById(R.id.contraseña);

        // Configuración de listeners para botones de navegación
        btnInicio.setOnClickListener(this);
        btnInfo.setOnClickListener(this);
        btnPerfil.setOnClickListener(this);
        btnCerrar.setOnClickListener(this);

        // Obtener el DNI desde SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("preDatosCita", MODE_PRIVATE);
        String dni = sharedPreferences.getString("dniPaciente", null);
        Log.d("DNI", "DNI recibido: " + dni);

        // Verificación del DNI
        if (dni == null || dni.isEmpty()) {
            Toast.makeText(this, "El DNI no está disponible", Toast.LENGTH_SHORT).show();
            return;
        }

        // Llamada a mostrardatos() para cargar la información del paciente
        mostrardatos(dni);

        // Listener para el botón Guardar
        btnGuardar.setOnClickListener(v -> Update());
    }


    private void mostrardatos(String dni) {
        String url = "http://192.168.0.199:80/pacientee/mostrar_.php";

        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Cargando datos...");
        progressDialog.show();

        StringRequest request = new StringRequest(Request.Method.POST, url,
                response -> {
                    progressDialog.dismiss();
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        String success = jsonObject.getString("exito");

                        if (success.equals("1")) {
                            JSONArray jsonArray = jsonObject.getJSONArray("datos");
                            JSONObject object = jsonArray.getJSONObject(0);

                            ed1.setText(object.getString("dni"));
                            ed2.setText(object.getString("nombre"));
                            ed3.setText(object.getString("apellido"));
                            ed4.setText(object.getString("telefono"));
                            ed5.setText(object.getString("genero"));
                            ed6.setText(object.getString("passwrd"));
                        } else {
                            Toast.makeText(editar_perfil.this, "No se encontraron datos", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(editar_perfil.this, "Error en los datos", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    progressDialog.dismiss();
                    Toast.makeText(editar_perfil.this, "Error de conexión", Toast.LENGTH_SHORT).show();
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("dni_paciente", dni);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(request);
    }


    public void Update() {
        String p_dni = ed1.getText().toString().trim();
        String p_nombre = ed2.getText().toString().trim();
        String p_apellido = ed3.getText().toString().trim();
        String p_telefono = ed4.getText().toString().trim();
        String p_genero = ed5.getText().toString().trim();
        String p_contra = ed6.getText().toString().trim();

        if (p_dni.isEmpty() || p_nombre.isEmpty() || p_apellido.isEmpty() || p_telefono.isEmpty() || p_genero.isEmpty() || p_contra.isEmpty()) {
            Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Actualizando...");
        progressDialog.show();

        String url = "http://192.168.0.199:80/pacientee/actualizar_.php";
        StringRequest request = new StringRequest(Request.Method.POST, url,
                response -> {
                    progressDialog.dismiss();
                    Toast.makeText(editar_perfil.this, "Actualización exitosa", Toast.LENGTH_SHORT).show();

                    // Crear un Intent para devolver los datos modificados
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("dni", p_dni);
                    resultIntent.putExtra("nombre", p_nombre);
                    resultIntent.putExtra("apellido", p_apellido);
                    resultIntent.putExtra("telefono", p_telefono);
                    resultIntent.putExtra("genero", p_genero);
                    resultIntent.putExtra("contraseña", p_contra);

                    // Devolver los datos a la actividad perfil
                    setResult(RESULT_OK, resultIntent);
                    finish(); // Finaliza esta actividad
                },
                error -> {
                    progressDialog.dismiss();
                    Toast.makeText(editar_perfil.this, "Error de conexión", Toast.LENGTH_SHORT).show();
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("dni_paciente", p_dni);
                params.put("nombre_paciente", p_nombre);
                params.put("apellido_paciente", p_apellido);
                params.put("telefono_paciente", p_telefono);
                params.put("genero_paciente", p_genero);
                params.put("password_paciente", p_contra);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(request);
    }

    @Override
    public void onClick(View v) {
        Intent intent = null;
        if (v == btnInicio) {
            intent = new Intent(this, menuPaciente.class);
        } else if (v == btnInfo) {
            intent = new Intent(this, Informacion_clinica.class);
        } else if (v == btnPerfil) {
            intent = new Intent(this, editar_perfil.class);
        } else if (v == btnCerrar) {
            showLogoutConfirmationDialog();
            return;
        }
        if (intent != null) {
            startActivity(intent);
            finish();
        }
    }

    private void logoutUser() {
        Intent intent = new Intent(this, inicioSeccionPaciente.class);
        startActivity(intent);
        finish();
    }

    private void showLogoutConfirmationDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Cerrar sesión")
                .setMessage("¿Estás seguro de que quieres cerrar sesión?")
                .setPositiveButton("Sí", (dialog, which) -> logoutUser())
                .setNegativeButton("Cancelar", (dialog, which) -> dialog.dismiss())
                .create()
                .show();
    }

}