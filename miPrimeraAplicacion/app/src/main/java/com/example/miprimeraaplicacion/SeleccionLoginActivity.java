package com.example.miprimeraaplicacion;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class SeleccionLoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seleccion_login);

        // Referencias a los botones
        Button btnLoginUsuario = findViewById(R.id.btnAccesoUsuario);
        Button btnLoginAgente = findViewById(R.id.btnAccesoPolicia);

        // Listener para el botón de "Ingresar como Usuario"
        btnLoginUsuario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Intent para la actividad de login de usuario
                Intent intent = new Intent(SeleccionLoginActivity.this, ActivityLoginUsuario.class);
                startActivity(intent);
            }
        });

        // Listener para el botón de "Ingresar como Agente de Policía"
        btnLoginAgente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Intent para la actividad de login de agente de policía
                Intent intent = new Intent(SeleccionLoginActivity.this, ActivityLoginAgente.class);
                startActivity(intent);
            }
        });
    }
}
