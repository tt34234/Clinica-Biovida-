package com.example.appclinicabiovida;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.Spanned;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class inicioSeccionPaciente extends AppCompatActivity implements View.OnClickListener {
    TextView txvRegistrarpaciente;
    TextInputEditText tietingresardnipaciente, tietingresarpasspaciente;
    Button btnIniciarSeccionPaciente;
    ImageView btnRegresarISP;
    SharedPreferences preDatosCita;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicio_seccion_paciente);

        tietingresardnipaciente = findViewById(R.id.tietIngresardnipaciente);
        tietingresarpasspaciente = findViewById(R.id.tietIngresarpasspaciente);
        txvRegistrarpaciente = findViewById(R.id.txvRegistrarPaciente);
        btnIniciarSeccionPaciente = findViewById(R.id.btnIngresarPaciente);
        preDatosCita = getSharedPreferences("preDatosCita", MODE_PRIVATE);
        btnIniciarSeccionPaciente.setOnClickListener(this);
        txvRegistrarpaciente.setOnClickListener(this);
        btnRegresarISP = findViewById(R.id.btnRegresarISP);
        btnRegresarISP.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == btnIniciarSeccionPaciente) {
            iniciarseccionPaciente();
        }
        if (v==btnRegresarISP)
        {
            Intent objIntent= new Intent(inicioSeccionPaciente.this,MainActivity.class);
            startActivity(objIntent);
            finish();
        }
        if (v==txvRegistrarpaciente)
        {
            Intent objIntent= new Intent(inicioSeccionPaciente.this,registrarPaciente.class);
            startActivity(objIntent);
            finish();
        }
    }

    public boolean validardni(String dni)
    {
        if (dni.length() != 8) {
            Toast.makeText(this, "El dni debe tener 8 digitos", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    public boolean validarPassword(String password)
    {
        if (password.length() < 8) {
            Toast.makeText(this, "La contraseña debe tener al menos 8 digitos", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void iniciarseccionPaciente()
    {
        String dni_paciente = tietingresardnipaciente.getText().toString().trim();
        String password_paciente = tietingresarpasspaciente.getText().toString().trim();

        if (dni_paciente.isEmpty() || password_paciente.isEmpty()) {
            Toast.makeText(this, "Ingrese todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        // Validar DNI y contraseña
        if (!validardni(dni_paciente)) {
            return;  // Si el DNI no es válido, no continuar con la solicitud
        }

        if (!validarPassword(password_paciente)) {
            return;  // Si la contraseña no es válida, no continuar con la solicitud
        }

        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Cargando...");
        progressDialog.show();

        StringRequest request = new StringRequest(Request.Method.POST, "http://192.168.0.199:80/pacientee/login_.php",
                response -> {
                    progressDialog.dismiss();
                    try {
                        JSONObject jsonResponse = new JSONObject(response);
                        String status = jsonResponse.getString("status");

                        if (status.equalsIgnoreCase("login exitoso")) {
                            // Obtener nombre y apellido desde la respuesta del servidor
                            String nombre = jsonResponse.getString("nombre");
                            String apellido = jsonResponse.getString("apellido");

                            // Guardar dni y otros datos en SharedPreferences si lo necesitas
                            /*SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString("dni_paciente", dni_paciente);
                            editor.putString("nombre", nombre);  // Guardar nombre
                            editor.putString("apellido", apellido);

                            editor.apply();*/
                            SharedPreferences.Editor editor = preDatosCita.edit();
                            editor.putString("nombrePaciente", nombre);
                            editor.putString("apellidoPaciente", apellido);
                            editor.putString("dniPaciente",dni_paciente);  // Asegúrate de que 'dniPaciente' sea String
                            editor.apply();
                            // Enviar los datos al menuPaciente
                            Intent intent = new Intent(inicioSeccionPaciente.this, menuPaciente.class);
                            intent.putExtra("nombre", nombre);
                            intent.putExtra("apellido", apellido);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(inicioSeccionPaciente.this, "DNI o contraseña incorrectos", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(inicioSeccionPaciente.this, "Error en la respuesta del servidor", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    progressDialog.dismiss();
                    Toast.makeText(inicioSeccionPaciente.this, "Error de conexión", Toast.LENGTH_SHORT).show();
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("dni_paciente", dni_paciente);
                params.put("password_paciente", password_paciente);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(request);
    }
}



