package com.example.appclinicabiovida;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Spinner;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    public void ingresarComoPaciente(View v)
    {
        Intent objintent= new Intent(MainActivity.this,inicioSeccionPaciente.class);
        startActivity(objintent);
        finish();

    }


    public void ingresarComoAdmin(View v)
    {
        Intent objIntent = new Intent(MainActivity.this,inicioSeccionAdministrador.class);
        startActivity(objIntent);
        finish();
    }

    public void mostrarGeneros(View v)
    {
        Spinner lstGenero= (Spinner) findViewById(R.id.spngeneropaciente);


    }
}