package com.example.appclinicabiovida;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.Spanned;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.textfield.TextInputEditText;

public class inicioSeccionAdministrador extends AppCompatActivity implements View.OnClickListener {

    ImageView btnregresarISA;
    TextInputEditText tietingresardniadmin, tietingresarpassadmin;


    private static final String ADMIN_DNI = "12345678";
    private static final String ADMIN_PASSWORD = "admin123";
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_inicio_seccion_administrador);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        btnregresarISA = findViewById(R.id.btnRegresarISA);
        btnregresarISA.setOnClickListener(this);

        tietingresardniadmin=findViewById(R.id.tietIngresardniadmin);
        tietingresarpassadmin=findViewById(R.id.tietIngresarpassadmin);
        // Filtro para solo permitir números y limitar la longitud a 8 dígitos en el DNI
        InputFilter soloNumeros = new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                for (int i = start; i < end; i++) {
                    if (!Character.isDigit(source.charAt(i))) {
                        return ""; // Rechaza cualquier caracter que no sea un dígito
                    }
                }
                return null;
            }
        };

        // Aplicar el filtro de solo números y limitar a 8 dígitos en el campo de DNI
        tietingresardniadmin.setFilters(new InputFilter[]{soloNumeros, new InputFilter.LengthFilter(8)});

    }

    @Override
    public void onClick(View v) {

        if (v==btnregresarISA) {
            Intent objIntent = new Intent(inicioSeccionAdministrador.this, MainActivity.class);

            startActivity(objIntent);

            finish();
        }
    }
    private boolean validarCamposVacios(String dni, String password) {
        if (dni.isEmpty()) {
            Toast.makeText(this, "Ingrese DNI", Toast.LENGTH_SHORT).show();
            return false;
        } else if (password.isEmpty()) {
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
    public void iniciarseccionAdmin(View view) {
        String dni = tietingresardniadmin.getText().toString().trim();
        String password = tietingresarpassadmin.getText().toString().trim();

        if (validarCamposVacios(dni, password) && validardni(dni) && validarPassword(password)) {
            if (dni.equals(ADMIN_DNI) && password.equals(ADMIN_PASSWORD)) {
                Toast.makeText(this, "Iniciaste Sección Correctamente", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(inicioSeccionAdministrador.this, gestionarcitas_administador.class);
                startActivity(intent);
                finish();
            } else {
                // Mostrar mensaje de error
                Toast.makeText(this, "DNI o contraseña incorrectos", Toast.LENGTH_SHORT).show();
            }
        }

    }
}