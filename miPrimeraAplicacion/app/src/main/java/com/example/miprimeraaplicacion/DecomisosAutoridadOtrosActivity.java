package com.example.miprimeraaplicacion;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;

public class DecomisosAutoridadOtrosActivity extends AppCompatActivity {

    EditText etONI, etPuesto;
    CheckBox cbBA, cbEJ, cbYSU;

    // --- NUEVOS CHECKBOXES DE ELEMENTOS DECOMISADOS Y OTROS ---
    CheckBox cbVehiculos, cbTarjetaCirculacion, cbLicencia, cbPlacas, cbPoliza, cbPermisosLinea;
    CheckBox cbConductorAusente, cbSeNegoFirmar, cbAparatoLaser, cbPruebaAlcotest, cbDestruyoEsquela, cbVehiculoRemolcado;
    // -----------------------------------------------------------

    Button btnSiguiente;

    // Variables para almacenar todos los datos recibidos
    private String usuarioLogueado;
    private String conductorLicencia, conductorApellido1, conductorApellido2, conductorApellido3,
            conductorNombre1, conductorNombre2, conductorNombre3, conductorClaseLicencia;
    private String vehiculoTipoPlaca, vehiculoNumeroPlaca, vehiculoCodigoRuta, vehiculoClase,
            vehiculoMarca, vehiculoModelo, vehiculoColor;
    private boolean vehiculoPlacaExtranjera;
    private String fechaHoraInfraccion;
    private ArrayList<String> fotosEvidenciaPaths;

    // --- VARIABLES DE DATOS DE LA FALTA RECIBIDOS ---
    private String faltaCodigo;
    private String faltaClasificacion;
    private String observaciones;

    DB_agentes dbAgentes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.decomisos_deautoridad_otros);

        etONI = findViewById(R.id.etONI);
        etPuesto = findViewById(R.id.etPuestoPolicial);
        cbBA = findViewById(R.id.cbBA);
        cbEJ = findViewById(R.id.cbEJ);
        cbYSU = findViewById(R.id.cbYSU);

        // --- INICIALIZAR NUEVOS CHECKBOXES ---
        cbVehiculos = findViewById(R.id.cbVehiculos);
        cbTarjetaCirculacion = findViewById(R.id.cbTarjetaCirculacion);
        cbLicencia = findViewById(R.id.cbLicencia);
        cbPlacas = findViewById(R.id.cbPlacas);
        cbPoliza = findViewById(R.id.cbPoliza);
        cbPermisosLinea = findViewById(R.id.cbPermisosLinea);

        cbConductorAusente = findViewById(R.id.cbConductorAusente);
        cbSeNegoFirmar = findViewById(R.id.cbSeNegoFirmar);
        cbAparatoLaser = findViewById(R.id.cbAparatoLaser);
        cbPruebaAlcotest = findViewById(R.id.cbPruebaAlcotest);
        cbDestruyoEsquela = findViewById(R.id.cbDestruyoEsquela);
        cbVehiculoRemolcado = findViewById(R.id.cbVehiculoRemolcado);
        // ------------------------------------

        btnSiguiente = findViewById(R.id.btnSiguiente);

        dbAgentes = new DB_agentes(this);

        // RECIBIR: Todos los datos previos usando el Bundle
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            usuarioLogueado = extras.getString("usuario_logueado");
            conductorLicencia = extras.getString("conductor_licencia");
            conductorApellido1 = extras.getString("conductor_apellido1");
            conductorApellido2 = extras.getString("conductor_apellido2");
            conductorApellido3 = extras.getString("conductor_apellido3");
            conductorNombre1 = extras.getString("conductor_nombre1");
            conductorNombre2 = extras.getString("conductor_nombre2");
            conductorNombre3 = extras.getString("conductor_nombre3");
            conductorClaseLicencia = extras.getString("conductor_clase_licencia");
            vehiculoTipoPlaca = extras.getString("vehiculo_tipo_placa");
            vehiculoNumeroPlaca = extras.getString("vehiculo_numero_placa");
            vehiculoCodigoRuta = extras.getString("vehiculo_codigo_ruta");
            vehiculoPlacaExtranjera = extras.getBoolean("vehiculo_placa_extranjera", false);
            vehiculoClase = extras.getString("vehiculo_clase");
            vehiculoMarca = extras.getString("vehiculo_marca");
            vehiculoModelo = extras.getString("vehiculo_modelo");
            vehiculoColor = extras.getString("vehiculo_color");
            fechaHoraInfraccion = extras.getString("fecha_hora_infraccion");
            fotosEvidenciaPaths = extras.getStringArrayList("fotos_evidencia_paths");

            faltaCodigo = extras.getString("falta_codigo");
            faltaClasificacion = extras.getString("falta_clasificacion");
            observaciones = extras.getString("observaciones");
        }

        // Validación básica: asegura que los datos principales llegaron
        if (usuarioLogueado == null || conductorLicencia == null || vehiculoNumeroPlaca == null ||
                fechaHoraInfraccion == null || faltaCodigo == null || faltaClasificacion == null ||
                observaciones == null) {
            Toast.makeText(this, "Error: Datos previos incompletos en DecomisosAutoridadOtrosActivity.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Cargar datos del agente logueado
        Cursor cursor = dbAgentes.obtenerDatosAgente(usuarioLogueado);

        if (cursor != null && cursor.moveToFirst()) {
            etONI.setText(cursor.getString(cursor.getColumnIndexOrThrow("oni")));
            etPuesto.setText(cursor.getString(cursor.getColumnIndexOrThrow("puesto_policial")));
            cbBA.setChecked(cursor.getInt(cursor.getColumnIndexOrThrow("es_ba")) == 1);
            cbEJ.setChecked(cursor.getInt(cursor.getColumnIndexOrThrow("es_ej")) == 1);
            cbYSU.setChecked(cursor.getInt(cursor.getColumnIndexOrThrow("es_ysu")) == 1);

            // Deshabilitar campos del agente ya que se cargan automáticamente
            etONI.setEnabled(false);
            etPuesto.setEnabled(false);
            cbBA.setEnabled(false);
            cbEJ.setEnabled(false);
            cbYSU.setEnabled(false);
            cursor.close();
        } else {
            Toast.makeText(this, "No se encontraron datos para el agente logueado.", Toast.LENGTH_SHORT).show();
        }

        // Configurar el botón Siguiente
        btnSiguiente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String agenteOni = etONI.getText().toString();
                String agentePuesto = etPuesto.getText().toString();

                Intent intent = new Intent(DecomisosAutoridadOtrosActivity.this, MultasActivity.class);

                // --- PASAR TODOS LOS DATOS ACUMULADOS A MultasActivity ---

                // Datos del Agente
                intent.putExtra("usuario_logueado", usuarioLogueado);
                intent.putExtra("agente_oni", agenteOni);
                intent.putExtra("agente_puesto", agentePuesto);

                // Datos del Conductor
                intent.putExtra("conductor_licencia", conductorLicencia);
                intent.putExtra("conductor_apellido1", conductorApellido1);
                intent.putExtra("conductor_apellido2", conductorApellido2);
                intent.putExtra("conductor_apellido3", conductorApellido3);
                intent.putExtra("conductor_nombre1", conductorNombre1);
                intent.putExtra("conductor_nombre2", conductorNombre2);
                intent.putExtra("conductor_nombre3", conductorNombre3);
                intent.putExtra("conductor_clase_licencia", conductorClaseLicencia);

                // Datos del Vehículo
                intent.putExtra("vehiculo_tipo_placa", vehiculoTipoPlaca);
                intent.putExtra("vehiculo_numero_placa", vehiculoNumeroPlaca);
                intent.putExtra("vehiculo_codigo_ruta", vehiculoCodigoRuta);
                intent.putExtra("vehiculo_placa_extranjera", vehiculoPlacaExtranjera);
                intent.putExtra("vehiculo_clase", vehiculoClase);
                intent.putExtra("vehiculo_marca", vehiculoMarca);
                intent.putExtra("vehiculo_modelo", vehiculoModelo);
                intent.putExtra("vehiculo_color", vehiculoColor);

                // Datos de la Falta (Fecha/Hora y Fotos)
                intent.putExtra("fecha_hora_infraccion", fechaHoraInfraccion);
                intent.putStringArrayListExtra("fotos_evidencia_paths", fotosEvidenciaPaths);

                // Datos de la Falta (Código, Clasificación y Observaciones)
                intent.putExtra("falta_codigo", faltaCodigo);
                intent.putExtra("falta_clasificacion", faltaClasificacion);
                intent.putExtra("observaciones", observaciones);

                // --- PASAR LOS ESTADOS DE LOS CHECKBOXES DE DECOMISOS Y OTROS ---
                intent.putExtra("decomiso_vehiculos", cbVehiculos.isChecked());
                intent.putExtra("decomiso_tarjeta_circulacion", cbTarjetaCirculacion.isChecked());
                intent.putExtra("decomiso_licencia", cbLicencia.isChecked());
                intent.putExtra("decomiso_placas", cbPlacas.isChecked());
                intent.putExtra("decomiso_poliza", cbPoliza.isChecked());
                intent.putExtra("decomiso_permisos_linea", cbPermisosLinea.isChecked());

                intent.putExtra("otros_conductor_ausente", cbConductorAusente.isChecked());
                intent.putExtra("otros_se_nego_firmar", cbSeNegoFirmar.isChecked());
                intent.putExtra("otros_aparato_laser", cbAparatoLaser.isChecked());
                intent.putExtra("otros_prueba_alcotest", cbPruebaAlcotest.isChecked());
                intent.putExtra("otros_destruyo_esquela", cbDestruyoEsquela.isChecked());
                intent.putExtra("otros_vehiculo_remolcado", cbVehiculoRemolcado.isChecked());
                // -----------------------------------------------------------------

                startActivity(intent);
            }
        });
    }
}
