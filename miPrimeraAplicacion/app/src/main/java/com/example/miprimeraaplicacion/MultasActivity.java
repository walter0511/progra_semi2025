package com.example.miprimeraaplicacion;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import androidx.core.content.ContextCompat;
import android.widget.RadioButton; // Necesario para obtener el texto del RadioButton

public class MultasActivity extends AppCompatActivity {

    private EditText inputMonto;
    private RadioGroup radioGroupCategoria;
    private Button btnSiguiente; // Volvemos a btnSiguiente

    // Variables para almacenar todos los datos recibidos de DecomisosAutoridadOtrosActivity
    private String usuarioLogueado;
    private String agenteOni, agentePuesto;
    private String conductorLicencia, conductorApellido1, conductorApellido2, conductorApellido3,
            conductorNombre1, conductorNombre2, conductorNombre3, conductorClaseLicencia;
    private String vehiculoTipoPlaca, vehiculoNumeroPlaca, vehiculoCodigoRuta, vehiculoClase,
            vehiculoMarca, vehiculoModelo, vehiculoColor;
    private boolean vehiculoPlacaExtranjera;
    private String fechaHoraInfraccion;
    private ArrayList<String> fotosEvidenciaPaths;
    private String faltaCodigo, faltaClasificacion, observaciones;

    // --- NUEVAS VARIABLES PARA RECIBIR DATOS DE DECOMISOS Y OTROS ---
    private boolean decomisoVehiculos, decomisoTarjetaCirculacion, decomisoLicencia,
            decomisoPlacas, decomisoPoliza, decomisoPermisosLinea;
    private boolean otrosConductorAusente, otrosSeNegoFirmar, otrosAparatoLaser,
            otrosPruebaAlcotest, otrosDestruyoEsquela, otrosVehiculoRemolcado;
    // -----------------------------------------------------------------

    // Eliminamos la instancia de DB_multas aquí, ya que no se usará para guardar en esta actividad.
    // private DB_multas dbMultas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.de_multas); // Asegúrate que coincida con tu XML

        // Eliminamos la inicialización de DB_multas aquí.
        // dbMultas = new DB_multas(this);

        // 1. Obtener referencias de vistas
        inputMonto = findViewById(R.id.inputMonto);
        radioGroupCategoria = findViewById(R.id.radioGroupCategoria);
        btnSiguiente = findViewById(R.id.btnSiguiente);

        // RECIBIR: Todos los datos previos
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            // Datos del Agente
            usuarioLogueado = extras.getString("usuario_logueado");
            agenteOni = extras.getString("agente_oni");
            agentePuesto = extras.getString("agente_puesto");

            // Datos del Conductor
            conductorLicencia = extras.getString("conductor_licencia");
            conductorApellido1 = extras.getString("conductor_apellido1");
            conductorApellido2 = extras.getString("conductor_apellido2");
            conductorApellido3 = extras.getString("conductor_apellido3");
            conductorNombre1 = extras.getString("conductor_nombre1");
            conductorNombre2 = extras.getString("conductor_nombre2");
            conductorNombre3 = extras.getString("conductor_nombre3");
            conductorClaseLicencia = extras.getString("conductor_clase_licencia");

            // Datos del Vehículo
            vehiculoTipoPlaca = extras.getString("vehiculo_tipo_placa");
            vehiculoNumeroPlaca = extras.getString("vehiculo_numero_placa");
            vehiculoCodigoRuta = extras.getString("vehiculo_codigo_ruta");
            vehiculoPlacaExtranjera = extras.getBoolean("vehiculo_placa_extranjera", false);
            vehiculoClase = extras.getString("vehiculo_clase");
            vehiculoMarca = extras.getString("vehiculo_marca");
            vehiculoModelo = extras.getString("vehiculo_modelo");
            vehiculoColor = extras.getString("vehiculo_color");

            // Datos de la Falta (fecha/hora y fotos ya estaban, ahora añadimos los nuevos)
            fechaHoraInfraccion = extras.getString("fecha_hora_infraccion");
            fotosEvidenciaPaths = extras.getStringArrayList("fotos_evidencia_paths");
            faltaCodigo = extras.getString("falta_codigo");
            faltaClasificacion = extras.getString("falta_clasificacion");
            observaciones = extras.getString("observaciones");

            // --- RECIBIR LOS NUEVOS DATOS DE DECOMISOS Y OTROS ---
            decomisoVehiculos = extras.getBoolean("decomiso_vehiculos", false);
            decomisoTarjetaCirculacion = extras.getBoolean("decomiso_tarjeta_circulacion", false);
            decomisoLicencia = extras.getBoolean("decomiso_licencia", false);
            decomisoPlacas = extras.getBoolean("decomiso_placas", false);
            decomisoPoliza = extras.getBoolean("decomiso_poliza", false);
            decomisoPermisosLinea = extras.getBoolean("decomiso_permisos_linea", false);

            otrosConductorAusente = extras.getBoolean("otros_conductor_ausente", false);
            otrosSeNegoFirmar = extras.getBoolean("otros_se_nego_firmar", false);
            otrosAparatoLaser = extras.getBoolean("otros_aparato_laser", false);
            otrosPruebaAlcotest = extras.getBoolean("otros_prueba_alcotest", false);
            otrosDestruyoEsquela = extras.getBoolean("otros_destruyo_esquela", false);
            otrosVehiculoRemolcado = extras.getBoolean("otros_vehiculo_remolcado", false);
            // -----------------------------------------------------------------

            // Validación básica: asegura que los datos principales llegaron
            if (usuarioLogueado == null || conductorLicencia == null || vehiculoNumeroPlaca == null ||
                    fechaHoraInfraccion == null || agenteOni == null || faltaCodigo == null ||
                    faltaClasificacion == null || observaciones == null) {
                Toast.makeText(this, "Error: Datos previos incompletos en MultasActivity. No se puede continuar.", Toast.LENGTH_LONG).show();
                finish();
                return;
            }
        } else {
            Toast.makeText(this, "Error: No se recibieron datos en MultasActivity. No se puede continuar.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        // 2. Configurar listener para cambios en RadioGroup
        radioGroupCategoria.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.radioLeve) {
                    setMontoAutomatico(inputMonto, "50.00");
                } else if (checkedId == R.id.radioGrave) {
                    setMontoAutomatico(inputMonto, "100.00");
                } else if (checkedId == R.id.radioMuyGrave) {
                    setMontoAutomatico(inputMonto, "150.00");
                } else if (checkedId == R.id.radioOtra) {
                    setMontoEditable(inputMonto);
                }
            }
        });

        // 3. Configurar botón Siguiente
        btnSiguiente.setOnClickListener(v -> {
            // Recolectar datos de la multa
            String montoMulta = inputMonto.getText().toString().trim();
            String categoriaMulta = "";
            int selectedId = radioGroupCategoria.getCheckedRadioButtonId();

            if (selectedId == -1) {
                Toast.makeText(this, "Por favor, seleccione una categoría de multa.", Toast.LENGTH_SHORT).show();
                return;
            } else {
                RadioButton selectedRadioButton = findViewById(selectedId);
                categoriaMulta = selectedRadioButton.getText().toString(); // Obtener el texto del RadioButton seleccionado
            }

            if (selectedId == R.id.radioOtra) {
                if (montoMulta.isEmpty()) {
                    Toast.makeText(this, "Por favor, ingrese el monto de la multa para la categoría 'Otra'.", Toast.LENGTH_SHORT).show();
                    return;
                }
                // Opcional: Validar que el monto sea un número válido si la categoría es "Otra"
                try {
                    Double.parseDouble(montoMulta);
                } catch (NumberFormatException e) {
                    Toast.makeText(this, "Monto de multa inválido. Ingrese un valor numérico.", Toast.LENGTH_SHORT).show();
                    return;
                }
            } else if (montoMulta.isEmpty()) { // Validar monto para categorías predefinidas si el EditText está vacío
                Toast.makeText(this, "El monto de la multa no puede estar vacío.", Toast.LENGTH_SHORT).show();
                return;
            }


            // PASAR: Crear Intent y adjuntar TODOS los datos acumulados a UbicacionActivity
            Intent intent = new Intent(MultasActivity.this, UbicacionActivity.class);

            // Datos del Agente (recibidos y confirmados aquí)
            intent.putExtra("usuario_logueado", usuarioLogueado);
            intent.putExtra("agente_oni", agenteOni);
            intent.putExtra("agente_puesto", agentePuesto);

            // Datos del Conductor (recibidos)
            intent.putExtra("conductor_licencia", conductorLicencia);
            intent.putExtra("conductor_apellido1", conductorApellido1);
            intent.putExtra("conductor_apellido2", conductorApellido2);
            intent.putExtra("conductor_apellido3", conductorApellido3);
            intent.putExtra("conductor_nombre1", conductorNombre1);
            intent.putExtra("conductor_nombre2", conductorNombre2);
            intent.putExtra("conductor_nombre3", conductorNombre3);
            intent.putExtra("conductor_clase_licencia", conductorClaseLicencia);

            // Datos del Vehículo (recibidos)
            intent.putExtra("vehiculo_tipo_placa", vehiculoTipoPlaca);
            intent.putExtra("vehiculo_numero_placa", vehiculoNumeroPlaca);
            intent.putExtra("vehiculo_codigo_ruta", vehiculoCodigoRuta);
            intent.putExtra("vehiculo_placa_extranjera", vehiculoPlacaExtranjera);
            intent.putExtra("vehiculo_clase", vehiculoClase);
            intent.putExtra("vehiculo_marca", vehiculoMarca);
            intent.putExtra("vehiculo_modelo", vehiculoModelo);
            intent.putExtra("vehiculo_color", vehiculoColor);

            // Datos de la Falta (recibidos de la actividad anterior)
            intent.putExtra("fecha_hora_infraccion", fechaHoraInfraccion);
            intent.putStringArrayListExtra("fotos_evidencia_paths", fotosEvidenciaPaths);
            intent.putExtra("falta_codigo", faltaCodigo);
            intent.putExtra("falta_clasificacion", faltaClasificacion);
            intent.putExtra("observaciones", observaciones);

            // --- PASAR LOS NUEVOS DATOS DE DECOMISOS Y OTROS A LA SIGUIENTE ACTIVIDAD (UbicacionActivity) ---
            intent.putExtra("decomiso_vehiculos", decomisoVehiculos);
            intent.putExtra("decomiso_tarjeta_circulacion", decomisoTarjetaCirculacion);
            intent.putExtra("decomiso_licencia", decomisoLicencia);
            intent.putExtra("decomiso_placas", decomisoPlacas);
            intent.putExtra("decomiso_poliza", decomisoPoliza);
            intent.putExtra("decomiso_permisos_linea", decomisoPermisosLinea);

            intent.putExtra("otros_conductor_ausente", otrosConductorAusente);
            intent.putExtra("otros_se_nego_firmar", otrosSeNegoFirmar);
            intent.putExtra("otros_aparato_laser", otrosAparatoLaser);
            intent.putExtra("otros_prueba_alcotest", otrosPruebaAlcotest);
            intent.putExtra("otros_destruyo_esquela", otrosDestruyoEsquela);
            intent.putExtra("otros_vehiculo_remolcado", otrosVehiculoRemolcado);
            // --------------------------------------------------------------------------------------------------

            // Datos de la Multa (propios de esta actividad, que se pasarán a UbicacionActivity)
            intent.putExtra("multa_monto", montoMulta);
            intent.putExtra("multa_categoria", categoriaMulta);

            startActivity(intent);
        });
    }

    private void setMontoAutomatico(EditText editText, String monto) {
        editText.setText(monto);
        editText.setFocusable(false);
        editText.setClickable(false);
        editText.setBackgroundColor(ContextCompat.getColor(this, android.R.color.darker_gray));
    }

    private void setMontoEditable(EditText editText) {
        editText.setText("");
        editText.setHint("Ingrese monto");
        editText.setFocusableInTouchMode(true);
        editText.setClickable(true);
        // Asegúrate de que R.drawable.edit_text_bg exista y sea un recurso válido para el fondo
        //editText.setBackgroundResource(R.drawable.edit_text_bg); // Comenta o elimina si no tienes este recurso
        editText.setBackgroundColor(ContextCompat.getColor(this, android.R.color.white)); // Puedes usar un color blanco o un color por defecto
    }
}
