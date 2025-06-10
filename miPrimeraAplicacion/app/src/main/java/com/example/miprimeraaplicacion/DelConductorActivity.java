package com.example.miprimeraaplicacion;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast; // Añadir importación
import androidx.appcompat.app.AppCompatActivity;

public class DelConductorActivity extends AppCompatActivity {

    EditText licenciaInput, apellido1Input, apellido2Input, apellido3Input;
    EditText nombre1Input, nombre2Input, nombre3Input;
    CheckBox claseExtranjera, claseJuvenil, claseMotocicleta, claseParticular, clasePesadaT, clasePesadaL, claseLiviana;

    DB_conductores dbConductores;

    // Variable para almacenar el usuario logueado
    private String usuarioLogueado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.del_conductor);

        // Inicializar base de datos
        dbConductores = new DB_conductores(this);

        // Referencias a campos
        licenciaInput = findViewById(R.id.licenciaInput);
        apellido1Input = findViewById(R.id.apellido1Input);
        apellido2Input = findViewById(R.id.apellido2Input);
        apellido3Input = findViewById(R.id.apellido3Input);
        nombre1Input = findViewById(R.id.nombre1Input);
        nombre2Input = findViewById(R.id.nombre2Input);
        nombre3Input = findViewById(R.id.nombre3Input);

        claseExtranjera = findViewById(R.id.claseExtranjera);
        claseJuvenil = findViewById(R.id.claseJuvenil);
        claseMotocicleta = findViewById(R.id.claseMotocicleta);
        claseParticular = findViewById(R.id.claseParticular);
        clasePesadaT = findViewById(R.id.clasePesadaT);
        clasePesadaL = findViewById(R.id.clasePesadaL);
        claseLiviana = findViewById(R.id.claseLiviana);

        // RECIBIR: Obtener el usuario logueado de la actividad anterior
        usuarioLogueado = getIntent().getStringExtra("usuario_logueado");
        if (usuarioLogueado == null) {
            Toast.makeText(this, "Error: Usuario no recibido.", Toast.LENGTH_SHORT).show();
            finish(); // O manejar este error de alguna forma
            return;
        }

        // Buscar automáticamente cuando se pierda el foco del campo licencia
        licenciaInput.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                buscarConductor(licenciaInput.getText().toString().trim());
            }
        });

        // Configuración del botón Siguiente
        Button btnSiguiente = findViewById(R.id.btnSiguiente);
        btnSiguiente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Recolectar todos los datos del conductor
                String licencia = licenciaInput.getText().toString();
                String apellido1 = apellido1Input.getText().toString();
                String apellido2 = apellido2Input.getText().toString();
                String apellido3 = apellido3Input.getText().toString(); // Recoge este valor
                String nombre1 = nombre1Input.getText().toString();
                String nombre2 = nombre2Input.getText().toString();
                String nombre3 = nombre3Input.getText().toString(); // Recoge este valor

                String claseLicencia = "";
                if (claseExtranjera.isChecked()) claseLicencia = "Extranjera";
                else if (claseJuvenil.isChecked()) claseLicencia = "Juvenil";
                else if (claseMotocicleta.isChecked()) claseLicencia = "Motocicleta";
                else if (claseParticular.isChecked()) claseLicencia = "Particular";
                else if (clasePesadaT.isChecked()) claseLicencia = "Pesada-T";
                else if (clasePesadaL.isChecked()) claseLicencia = "Pesada-L";
                else if (claseLiviana.isChecked()) claseLicencia = "Liviana";
                else {
                    Toast.makeText(DelConductorActivity.this, "Por favor, seleccione una clase de licencia.", Toast.LENGTH_SHORT).show();
                    return; // Evita avanzar si no hay clase seleccionada
                }

                // PASAR: Crear Intent y adjuntar todos los datos
                Intent intent = new Intent(DelConductorActivity.this, DelVehiculoActivity.class);
                intent.putExtra("usuario_logueado", usuarioLogueado); // Agente
                intent.putExtra("conductor_licencia", licencia);
                intent.putExtra("conductor_apellido1", apellido1);
                intent.putExtra("conductor_apellido2", apellido2);
                intent.putExtra("conductor_apellido3", apellido3); // Pasa este valor
                intent.putExtra("conductor_nombre1", nombre1);
                intent.putExtra("conductor_nombre2", nombre2);
                intent.putExtra("conductor_nombre3", nombre3); // Pasa este valor
                intent.putExtra("conductor_clase_licencia", claseLicencia);
                startActivity(intent);
            }
        });
    }

    private void buscarConductor(String licencia) {
        Cursor cursor = dbConductores.buscarPorLicencia(licencia);
        if (cursor != null && cursor.moveToFirst()) { // Añadido null check
            apellido1Input.setText(cursor.getString(cursor.getColumnIndexOrThrow("apellido1")));
            apellido2Input.setText(cursor.getString(cursor.getColumnIndexOrThrow("apellido2")));
            //apellido3Input.setText(cursor.getString(cursor.getColumnIndexOrThrow("apellido3"))); // Si tu base de datos tiene este campo
            nombre1Input.setText(cursor.getString(cursor.getColumnIndexOrThrow("nombre1")));
            nombre2Input.setText(cursor.getString(cursor.getColumnIndexOrThrow("nombre2")));
            //nombre3Input.setText(cursor.getString(cursor.getColumnIndexOrThrow("nombre3"))); // Si tu base de datos tiene este campo

            String clase = cursor.getString(cursor.getColumnIndexOrThrow("clase"));
            actualizarClase(clase);
            cursor.close(); // ¡Importante cerrar el cursor!
        } else {
            // Limpia los campos si no se encuentra el conductor
            apellido1Input.setText("");
            apellido2Input.setText("");
            apellido3Input.setText("");
            nombre1Input.setText("");
            nombre2Input.setText("");
            nombre3Input.setText("");
            claseExtranjera.setChecked(false);
            claseJuvenil.setChecked(false);
            claseMotocicleta.setChecked(false);
            claseParticular.setChecked(false);
            clasePesadaT.setChecked(false);
            clasePesadaL.setChecked(false);
            claseLiviana.setChecked(false);
            Toast.makeText(this, "Conductor no encontrado.", Toast.LENGTH_SHORT).show();
        }
    }

    private void actualizarClase(String clase) {
        claseExtranjera.setChecked("Extranjera".equalsIgnoreCase(clase));
        claseJuvenil.setChecked("Juvenil".equalsIgnoreCase(clase));
        claseMotocicleta.setChecked("Motocicleta".equalsIgnoreCase(clase));
        claseParticular.setChecked("Particular".equalsIgnoreCase(clase));
        clasePesadaT.setChecked("Pesada-T".equalsIgnoreCase(clase));
        clasePesadaL.setChecked("Pesada-L".equalsIgnoreCase(clase));
        claseLiviana.setChecked("Liviana".equalsIgnoreCase(clase));
    }
}
