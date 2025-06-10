package com.example.miprimeraaplicacion;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class ActivityLoginAgente extends AppCompatActivity {
    private DB_agentes dbAgentes;
    private EditText editTextUsuario, editTextContrasena;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_agente);

        // Inicializar base de datos
        dbAgentes = new DB_agentes(this);

        // Obtener referencias de vistas
        editTextUsuario = findViewById(R.id.editTextUsuario);
        editTextContrasena = findViewById(R.id.editTextContrasena);
        Button btnLogin = findViewById(R.id.btnLoginAgente);
        ImageButton btnRegresar = findViewById(R.id.btnRegresar);

        // Configurar botón de login
        btnLogin.setOnClickListener(v -> {
            String usuario = editTextUsuario.getText().toString().trim();
            String contrasena = editTextContrasena.getText().toString().trim();

            if (usuario.isEmpty() || contrasena.isEmpty()) {
                mostrarMensaje("Ingrese usuario y contraseña");
            } else {
                if (dbAgentes.verificarAgente(usuario, contrasena)) {
                    iniciarAplicacion(usuario);  // Pasamos el usuario
                } else {
                    mostrarMensaje("Credenciales incorrectas");
                }
            }
        });

        // Configurar botón de regresar
        btnRegresar.setOnClickListener(v -> finish());
    }

    // Método para iniciar la aplicación y enviar el nombre del usuario
    private void iniciarAplicacion(String usuario) {
        Intent intent = new Intent(this, DelConductorActivity.class);

        intent.putExtra("usuario_logueado", usuario); // Enviamos el usuario
        startActivity(intent);
        finish();
    }

    private void mostrarMensaje(String mensaje) {
        Toast.makeText(this, mensaje, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        dbAgentes.close();
        super.onDestroy();
    }
}
