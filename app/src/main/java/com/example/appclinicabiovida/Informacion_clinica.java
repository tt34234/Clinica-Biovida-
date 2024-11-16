package com.example.appclinicabiovida;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class Informacion_clinica extends AppCompatActivity implements View.OnClickListener{
    ImageView btnInicio,btnInfo,btnPerfil,btnCerrar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_informacion_clinica);
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
    }
    private void logoutUser() {
        Intent objIntent = new Intent(Informacion_clinica.this, inicioSeccionPaciente.class);
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
    @Override
    public void onClick(View v) {
        if (v == btnInicio) {
            Intent objIntent = new Intent(Informacion_clinica.this, menuPaciente.class);
            startActivity(objIntent);
            finish();
        } else if (v == btnInfo) {
            Intent objIntent = new Intent(Informacion_clinica.this, Informacion_clinica.class);
            startActivity(objIntent);
            finish();
        }else if (v == btnPerfil) {
            Intent objIntent = new Intent(Informacion_clinica.this, perfil.class);
            startActivity(objIntent);
            finish();
        }else if (v == btnCerrar) {
            showLogoutConfirmationDialog();

        }
    }
}