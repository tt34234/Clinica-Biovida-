package com.example.appclinicabiovida;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.Spanned;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
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
import com.google.android.material.textfield.TextInputEditText;

import java.util.HashMap;
import java.util.Map;

public class registrarPaciente extends AppCompatActivity implements View.OnClickListener {
    ImageView btnRegresarRP;
    TextInputEditText tietNombrepaciente, tietApellidopaciente, tietTelefonopaciente, tietDnipaciente, tietPasswordpaciente;
    Spinner spnGeneropaciente;
    Button btnRegistrarpaciente;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_registrar_paciente);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        btnRegresarRP = findViewById(R.id.btnRegresarRP);
        btnRegresarRP.setOnClickListener(this);
        tietNombrepaciente=findViewById(R.id.tietnombrepaciente);
        tietApellidopaciente=findViewById(R.id.tietapellidopaciente);
        tietTelefonopaciente=findViewById(R.id.tiettelefonopaciente);
        tietDnipaciente=findViewById(R.id.tietdnipaciente);
        tietPasswordpaciente=findViewById(R.id.tietpasswordpaciente);
        spnGeneropaciente=findViewById(R.id.spngeneropaciente);


        btnRegistrarpaciente=findViewById(R.id.btnRegistrarPaciente);


        // Filtro para solo permitir letras y espacios en nombre y apellido
        InputFilter soloLetras = new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                for (int i = start; i < end; i++) {
                    if (!Character.isLetter(source.charAt(i)) && !Character.isSpaceChar(source.charAt(i))) {
                        return "";  // Rechaza caracteres que no sean letras o espacios
                    }
                }
                return null;
            }
        };

        // Filtro para solo permitir números en teléfono y DNI
        InputFilter soloNumeros = new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                for (int i = start; i < end; i++) {
                    if (!Character.isDigit(source.charAt(i))) {
                        return "";  // Rechaza caracteres que no sean dígitos
                    }
                }
                return null;
            }
        };

        // Aplicar filtros de solo letras a los campos de nombre y apellido
        tietNombrepaciente.setFilters(new InputFilter[]{soloLetras});
        tietApellidopaciente.setFilters(new InputFilter[]{soloLetras});

        // Aplicar filtros de solo números y longitud máxima a los campos de teléfono y DNI
        tietTelefonopaciente.setFilters(new InputFilter[]{soloNumeros, new InputFilter.LengthFilter(9)});
        tietDnipaciente.setFilters(new InputFilter[]{soloNumeros, new InputFilter.LengthFilter(8)});
    }

    @Override
    public void onClick(View v) {

        if(v==btnRegresarRP)
        {
           Intent objIntent = new Intent(registrarPaciente.this,inicioSeccionPaciente.class);
           startActivity(objIntent);

           finish();
        }
    }


    public void limpiar() {
        tietNombrepaciente.setText("");
        tietApellidopaciente.setText("");
        tietTelefonopaciente.setText("");
        tietDnipaciente.setText("");
        tietPasswordpaciente.setText("");

        tietNombrepaciente.requestFocus();
    }
    private boolean validarCamposVacios(String nombre, String apellido, String telefono, String genero, String dni, String password) {
        if (nombre.isEmpty()) {
            Toast.makeText(this, "Ingrese nombre", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (apellido.isEmpty()) {
            Toast.makeText(this, "Ingrese apellido", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (telefono.isEmpty()) {
            Toast.makeText(this, "Ingrese teléfono", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (genero.isEmpty()) {
            Toast.makeText(this, "Seleccione género", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (dni.isEmpty()) {
            Toast.makeText(this, "Ingrese DNI", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (password.isEmpty()) {
            Toast.makeText(this, "Ingrese contraseña", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
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
    public boolean validarTelefono(String telefono)
    {
        if (telefono.length() != 9) {
            Toast.makeText(this, "El numero de telefono debe tener 9 digitos", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    public void registrar_paciente(View view) {
        try {
            String nombre_paciente = tietNombrepaciente.getText().toString().trim();
            String apellido_paciente = tietApellidopaciente.getText().toString().trim();
            String telefono_paciente = tietTelefonopaciente.getText().toString().trim();
            String genero_paciente = spnGeneropaciente.getSelectedItem().toString();
            String dni_paciente = tietDnipaciente.getText().toString();
            String password_paciente = tietPasswordpaciente.getText().toString();



            if (validarCamposVacios(nombre_paciente, apellido_paciente, telefono_paciente, genero_paciente, dni_paciente, password_paciente) && validarPassword(password_paciente) && validardni(dni_paciente) && validarTelefono(telefono_paciente)) {
                ProgressDialog progressDialog = new ProgressDialog(this);
                progressDialog.setMessage("Cargando...");
                progressDialog.show();

                StringRequest request = new StringRequest(Request.Method.POST, "http://192.168.0.199:80/paciente/insertar_.php",
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                progressDialog.dismiss();

                                // Verificar si el DNI ya está registrado
                                if (response.trim().equals("DNI ya registrado")) {
                                    Toast.makeText(registrarPaciente.this, "El DNI ya está registrado", Toast.LENGTH_SHORT).show();
                                } else if (response.trim().equals("Datos insertados correctamente")) {
                                    Toast.makeText(registrarPaciente.this, "Registrado correctamente", Toast.LENGTH_SHORT).show();
                                    limpiar();  // Limpiar los campos si el registro fue exitoso
                                } else {
                                    Toast.makeText(registrarPaciente.this, "Error al registrar", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();
                        Toast.makeText(registrarPaciente.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }) {
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String, String> params = new HashMap<>();
                        params.put("nombre_paciente", nombre_paciente);
                        params.put("apellido_paciente", apellido_paciente);
                        params.put("telefono_paciente", telefono_paciente);
                        params.put("genero_paciente", genero_paciente);
                        params.put("dni_paciente", dni_paciente);
                        params.put("password_paciente", password_paciente);
                        return params;
                    }
                };

                RequestQueue requestQueue = Volley.newRequestQueue(registrarPaciente.this);
                requestQueue.add(request);
            }

        } catch (Exception ex) {
            Toast.makeText(this, "Verificar datos a ingresar", Toast.LENGTH_SHORT).show();
        }
    }
}


