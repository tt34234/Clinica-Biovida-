package com.example.appclinicabiovida;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class menuPaciente extends AppCompatActivity implements View.OnClickListener {

    ImageView btnInicio,btnInfo,btnPerfil,btnCerrar;

    TextView tvPacienteIniaciadoSeccion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_menu_paciente);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        btnInicio = findViewById(R.id.btnInicio);
        btnInicio.setOnClickListener(this);
        btnInfo = findViewById(R.id.btnInfo);
        btnInfo.setOnClickListener(this);
        btnPerfil = findViewById(R.id.btnPerfil);
        btnPerfil.setOnClickListener(this);
        btnCerrar = findViewById(R.id.btnCerrar);
        btnCerrar.setOnClickListener(this);
        tvPacienteIniaciadoSeccion=findViewById(R.id.tvPacienteIniciadoSeccion);

        Intent intent = getIntent();
        String nombre = intent.getStringExtra("nombre");
        String apellido = intent.getStringExtra("apellido");

        if (nombre != null && apellido != null) {
            tvPacienteIniaciadoSeccion.setText("Bienvenido " + nombre + " " + apellido);
        } else {
            tvPacienteIniaciadoSeccion.setText("Bienvenido"); // Mensaje por defecto
        }



    }
    private void logoutUser() {
        Intent objIntent = new Intent(menuPaciente.this, inicioSeccionPaciente.class);
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

    public void sede(View v)
    {
        Intent objIntent = new Intent(menuPaciente.this,sedes.class);
        startActivity(objIntent);
        finish();

    }
    @Override
    public void onClick(View v) {

        if (v == btnInicio) {
            Intent objIntent = new Intent(menuPaciente.this, menuPaciente.class);
            startActivity(objIntent);
            finish();
        } else if (v == btnInfo) {
            Intent objIntent = new Intent(menuPaciente.this, Informacion_clinica.class);
            startActivity(objIntent);
            finish();
        }else if (v == btnPerfil) {
            Intent objIntent = new Intent(menuPaciente.this, perfil.class);
            startActivity(objIntent);
            finish();
        }else if (v == btnCerrar) {
            showLogoutConfirmationDialog();
        }
    }

    public void registrarcita(View v)
    {
           Intent objIntent = new Intent(menuPaciente.this,elegirEspecilidad.class);
           startActivity(objIntent);
           finish();

    }

    public void anularCita(View v)
    {
        Intent objIntent = new Intent(menuPaciente.this,AnularCita.class);
        startActivity(objIntent);
        finish();

    }

    public void mostrarCitasRegistradas(View v)
    {
        Intent objIntent = new Intent(menuPaciente.this,mostrarcitasPaciente.class);
        startActivity(objIntent);
        finish();

    }
}