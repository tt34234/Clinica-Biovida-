package com.example.appclinicabiovida;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;




import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
public class perfil extends AppCompatActivity implements View.OnClickListener{

        // Declaración de los TextViews
        TextView tvDni, tvNombre, tvApellido, tvTelefono, tvGenero, tvContrasena;
        Button btnEditar;
        ImageView btnInicio,btnInfo,btnPerfil,btnCerrar;
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_perfil);

            // Inicializar los TextViews
            tvDni = findViewById(R.id.tvDni);
            tvNombre = findViewById(R.id.tvNombre);
            tvApellido = findViewById(R.id.tvApellido);
            tvTelefono = findViewById(R.id.tvTelefono);
            tvGenero = findViewById(R.id.tvGenero);
            tvContrasena = findViewById(R.id.tvContrasena);

            // Obtener el DNI desde SharedPreferences
            SharedPreferences sharedPreferences = getSharedPreferences("preDatosCita", MODE_PRIVATE);
            String dni = sharedPreferences.getString("dniPaciente", "");

            // Si el DNI está disponible, cargar los datos desde el servidor
            if (!dni.isEmpty()) {
                mostrarDatos(dni);
            }

            // Configurar el botón "Editar"
            btnEditar = findViewById(R.id.btnEditar);
            btnEditar.setOnClickListener(v -> {
                // Cuando se hace clic en "Editar", abrir la actividad editar_perfil
                Intent intent = new Intent(perfil.this, editar_perfil.class);
                startActivityForResult(intent, 1); // Usamos startActivityForResult para obtener los datos modificados
            });
            btnInicio = findViewById(R.id.btnInicio);
            btnInicio.setOnClickListener(this);
            btnInfo = findViewById(R.id.btnInfo);
            btnInfo.setOnClickListener(this);
            btnPerfil = findViewById(R.id.btnPerfil);
            btnPerfil.setOnClickListener(this);
            btnCerrar = findViewById(R.id.btnCerrar);
            btnCerrar.setOnClickListener(this);
        }
    private void logoutUser() {
        Intent objIntent = new Intent(perfil.this, inicioSeccionPaciente.class);
        startActivity(objIntent);
        finish(); // Esto cerrará la actividad actual
    }
    private void showLogoutConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Cerrar sesión");
        builder.setMessage("¿Estás seguro de que quieres cerrar sesión?");

        builder.setPositiveButton("Sí", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Lógica para cerrar sesión
                logoutUser();
            }
        });

        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

        // Método para obtener los datos del paciente desde el servidor
        private void mostrarDatos(String dni) {
            String url = "http://192.168.0.199:80/pacientee/mostrar_.php"; // URL del servidor

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

                                // Actualizar los TextViews con los datos recibidos
                                tvDni.setText(object.getString("dni"));
                                tvNombre.setText(object.getString("nombre"));
                                tvApellido.setText(object.getString("apellido"));
                                tvTelefono.setText(object.getString("telefono"));
                                tvGenero.setText(object.getString("genero"));
                                tvContrasena.setText(object.getString("passwrd"));
                            } else {
                                Toast.makeText(perfil.this, "No se encontraron datos", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(perfil.this, "Error al obtener los datos", Toast.LENGTH_SHORT).show();
                        }
                    },
                    error -> {
                        progressDialog.dismiss();
                        Toast.makeText(perfil.this, "Error de conexión", Toast.LENGTH_SHORT).show();
                    }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    params.put("dni_paciente", dni);  // Enviar el DNI del paciente al servidor
                    return params;
                }
            };

            RequestQueue requestQueue = Volley.newRequestQueue(this);
            requestQueue.add(request);
        }

        // Método para manejar la respuesta de editar_perfil
        @Override
        protected void onActivityResult(int requestCode, int resultCode, Intent data) {
            super.onActivityResult(requestCode, resultCode, data);
            if (requestCode == 1 && resultCode == RESULT_OK) {
                // Recuperar los datos modificados desde el Intent
                String dni = data.getStringExtra("dni");
                String nombre = data.getStringExtra("nombre");
                String apellido = data.getStringExtra("apellido");
                String telefono = data.getStringExtra("telefono");
                String genero = data.getStringExtra("genero");
                String contrasena = data.getStringExtra("psswrd");

                // Mostrar los datos actualizados
                tvDni.setText(dni);
                tvNombre.setText(nombre);
                tvApellido.setText(apellido);
                tvTelefono.setText(telefono);
                tvGenero.setText(genero);
                tvContrasena.setText(contrasena);

                // Guardar los datos modificados en SharedPreferences
                SharedPreferences sharedPreferences = getSharedPreferences("preDatosCita", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("dniPaciente", dni);
                editor.putString("nombrePaciente", nombre);
                editor.putString("apellidoPaciente", apellido);
                editor.putString("telefono", telefono);
                editor.putString("genero", genero);
                editor.putString("passwrd", contrasena);
                editor.apply();

                // También podemos refrescar los datos desde el servidor
                mostrarDatos(dni);
            }

        }
    @Override
    public void onClick(View v) {
        if (v == btnInicio) {
            Intent objIntent = new Intent(perfil.this, menuPaciente.class);
            startActivity(objIntent);
            finish();
        } else if (v == btnInfo) {
            Intent objIntent = new Intent(perfil.this, Informacion_clinica.class);
            startActivity(objIntent);
            finish();
        }else if (v == btnPerfil) {
            Intent objIntent = new Intent(perfil.this, perfil.class);
            startActivity(objIntent);
            finish();
        }else if (v == btnCerrar) {
            showLogoutConfirmationDialog();
        }
    }
    }

