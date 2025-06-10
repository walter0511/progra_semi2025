package com.example.miprimeraaplicacion;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.ArrayAdapter; // Importar ArrayAdapter (seguirá siendo necesario)
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList; // Necesario para crear listas dinámicas
import java.util.List;    // Necesario para crear listas dinámicas

public class DelVehiculoActivity extends AppCompatActivity {

    Spinner spinnerTipoPlaca;
    EditText editTextNumeroPlaca, editTextCodigoRuta;
    CheckBox checkExtranjera;
    EditText txtClase, txtMarca, txtModelo, txtColor;
    Button btnSiguiente;

    DB_vehiculos dbVehiculos;

    // Variables para almacenar todos los datos recibidos y propios
    private String usuarioLogueado;
    private String conductorLicencia, conductorApellido1, conductorApellido2, conductorApellido3,
            conductorNombre1, conductorNombre2, conductorNombre3, conductorClaseLicencia;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.del_vehiculo);

        // Inicializar base de datos
        dbVehiculos = new DB_vehiculos(this);

        // Referencias a los campos del layout (asegúrate que los IDs coincidan)
        spinnerTipoPlaca = findViewById(R.id.spinnerTipoPlaca);
        editTextNumeroPlaca = findViewById(R.id.editTextNumeroPlaca);
        editTextCodigoRuta = findViewById(R.id.editTextCodigoRuta);
        checkExtranjera = findViewById(R.id.checkExtranjera);

        txtClase = findViewById(R.id.txtClase);
        txtMarca = findViewById(R.id.txtMarca);
        txtModelo = findViewById(R.id.txtModelo);
        txtColor = findViewById(R.id.txtColor);

        btnSiguiente = findViewById(R.id.btnSiguiente);

        // --- INICIALIZACIÓN DEL SPINNER: Con una opción por defecto "Vacía" al inicio ---
        // Esto es crucial para evitar el NullPointerException si el usuario no busca nada
        // o si la búsqueda no encuentra un vehículo.
        List<String> initialSpinnerItems = new ArrayList<>();
        initialSpinnerItems.add("Seleccionar Tipo de Placa"); // Opción por defecto
        ArrayAdapter<String> initialAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, initialSpinnerItems);
        initialAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTipoPlaca.setAdapter(initialAdapter);
        // --- FIN DE INICIALIZACIÓN ---


        // RECIBIR: Datos del agente y conductor
        usuarioLogueado = getIntent().getStringExtra("usuario_logueado");
        conductorLicencia = getIntent().getStringExtra("conductor_licencia");
        conductorApellido1 = getIntent().getStringExtra("conductor_apellido1");
        conductorApellido2 = getIntent().getStringExtra("conductor_apellido2");
        conductorApellido3 = getIntent().getStringExtra("conductor_apellido3");
        conductorNombre1 = getIntent().getStringExtra("conductor_nombre1");
        conductorNombre2 = getIntent().getStringExtra("conductor_nombre2");
        conductorNombre3 = getIntent().getStringExtra("conductor_nombre3");
        conductorClaseLicencia = getIntent().getStringExtra("conductor_clase_licencia");

        // Opcional: una verificación básica para asegurar que se recibieron los datos principales
        if (usuarioLogueado == null || conductorLicencia == null) {
            Toast.makeText(this, "Error: Datos previos del agente o conductor no recibidos correctamente.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }


        // Buscar vehículo cuando el EditText de número de placa pierda foco
        editTextNumeroPlaca.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                String numeroPlaca = editTextNumeroPlaca.getText().toString().trim();
                if (!numeroPlaca.isEmpty()) {
                    buscarVehiculoPorPlaca(numeroPlaca);
                } else {
                    limpiarCampos(); // Limpia los campos si la placa está vacía al perder el foco
                    Toast.makeText(this, "Ingrese un número de placa para buscar.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Acción del botón Siguiente (navegar a la siguiente actividad)
        btnSiguiente.setOnClickListener(v -> {
            // Recolectar todos los datos del vehículo
            String tipoPlaca = spinnerTipoPlaca.getSelectedItem().toString();
            String numeroPlaca = editTextNumeroPlaca.getText().toString().trim();
            String codigoRuta = editTextCodigoRuta.getText().toString();
            boolean placaExtranjera = checkExtranjera.isChecked();
            String claseVehiculo = txtClase.getText().toString();
            String marca = txtMarca.getText().toString();
            String modelo = txtModelo.getText().toString();
            String color = txtColor.getText().toString();

            // Validación básica de campos obligatorios
            // Ahora también validamos que el tipo de placa no sea la opción por defecto
            if (numeroPlaca.isEmpty() || tipoPlaca.equals("Seleccionar Tipo de Placa") || claseVehiculo.isEmpty() || marca.isEmpty()) {
                Toast.makeText(this, "Por favor, complete al menos Placa, Tipo, Clase y Marca del vehículo.", Toast.LENGTH_SHORT).show();
                return;
            }


            // PASAR: Crear Intent y adjuntar todos los datos (recibidos + propios)
            Intent intent = new Intent(DelVehiculoActivity.this, falta.class);

            // Datos del Agente (recibidos)
            intent.putExtra("usuario_logueado", usuarioLogueado);

            // Datos del Conductor (recibidos)
            intent.putExtra("conductor_licencia", conductorLicencia);
            intent.putExtra("conductor_apellido1", conductorApellido1);
            intent.putExtra("conductor_apellido2", conductorApellido2);
            intent.putExtra("conductor_apellido3", conductorApellido3);
            intent.putExtra("conductor_nombre1", conductorNombre1);
            intent.putExtra("conductor_nombre2", conductorNombre2);
            intent.putExtra("conductor_nombre3", conductorNombre3);
            intent.putExtra("conductor_clase_licencia", conductorClaseLicencia);

            // Datos del Vehículo (propios de esta actividad)
            intent.putExtra("vehiculo_tipo_placa", tipoPlaca);
            intent.putExtra("vehiculo_numero_placa", numeroPlaca);
            intent.putExtra("vehiculo_codigo_ruta", codigoRuta);
            intent.putExtra("vehiculo_placa_extranjera", placaExtranjera); // Boolean
            intent.putExtra("vehiculo_clase", claseVehiculo);
            intent.putExtra("vehiculo_marca", marca);
            intent.putExtra("vehiculo_modelo", modelo);
            intent.putExtra("vehiculo_color", color);

            startActivity(intent);
        });
    }

    private void buscarVehiculoPorPlaca(String numeroPlaca) {
        Cursor cursor = dbVehiculos.buscarPorPlaca(numeroPlaca);
        if (cursor != null && cursor.moveToFirst()) {
            String tipoPlaca = cursor.getString(cursor.getColumnIndexOrThrow("tipo_placa"));
            String codigoRuta = cursor.getString(cursor.getColumnIndexOrThrow("codigo_ruta"));
            int placaExtranjeraInt = cursor.getInt(cursor.getColumnIndexOrThrow("placa_extranjera"));
            String clase = cursor.getString(cursor.getColumnIndexOrThrow("clase"));
            String marca = cursor.getString(cursor.getColumnIndexOrThrow("marca"));
            String modelo = cursor.getString(cursor.getColumnIndexOrThrow("modelo"));
            String color = cursor.getString(cursor.getColumnIndexOrThrow("color"));

            // --- AÑADIDO PARA EL SPINNER: Cargar el tipo de placa encontrado ---
            List<String> spinnerItems = new ArrayList<>();
            spinnerItems.add(tipoPlaca); // Agrega solo el tipo de placa encontrado
            ArrayAdapter<String> dynamicAdapter = new ArrayAdapter<>(this,
                    android.R.layout.simple_spinner_item, spinnerItems);
            dynamicAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerTipoPlaca.setAdapter(dynamicAdapter);
            // La selección siempre será el primer (y único) elemento en este caso
            spinnerTipoPlaca.setSelection(0);
            // --- FIN DE LO AÑADIDO ---

            editTextCodigoRuta.setText(codigoRuta != null ? codigoRuta : "");
            checkExtranjera.setChecked(placaExtranjeraInt == 1);

            txtClase.setText(clase);
            txtMarca.setText(marca);
            txtModelo.setText(modelo);
            txtColor.setText(color);

            cursor.close();
            Toast.makeText(this, "Vehículo encontrado.", Toast.LENGTH_SHORT).show();
        } else {
            limpiarCampos();
            Toast.makeText(this, "Vehículo no encontrado. Ingrese los datos manualmente.", Toast.LENGTH_SHORT).show();
        }
    }

    // Ya no necesitas setSpinnerValue si siempre se crea un nuevo adapter con un solo elemento
    // private void setSpinnerValue(Spinner spinner, String value) { ... }

    private void limpiarCampos() {
        // Limpiar los EditText como antes
        editTextNumeroPlaca.setText(""); // También limpia el número de placa
        editTextCodigoRuta.setText("");
        checkExtranjera.setChecked(false);
        txtClase.setText("");
        txtMarca.setText("");
        txtModelo.setText("");
        txtColor.setText("");

        // --- AÑADIDO PARA EL SPINNER: Resetear el spinner a su estado inicial "vacío" ---
        List<String> initialSpinnerItems = new ArrayList<>();
        initialSpinnerItems.add("Seleccionar Tipo de Placa"); // Opción por defecto
        ArrayAdapter<String> initialAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, initialSpinnerItems);
        initialAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTipoPlaca.setAdapter(initialAdapter);
        spinnerTipoPlaca.setSelection(0); // Asegura que se selecciona la opción por defecto
        // --- FIN DE LO AÑADIDO ---
    }
}
