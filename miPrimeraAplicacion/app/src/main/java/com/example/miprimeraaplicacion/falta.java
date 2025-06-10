package com.example.miprimeraaplicacion;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TimePicker;
import androidx.appcompat.app.AppCompatActivity;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.widget.RadioGroup;
import android.widget.RadioButton;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.io.IOException;

public class falta extends AppCompatActivity {

    private EditText etFechaDecomiso;
    private TimePicker timePickerDecomiso;
    private Calendar calendar = Calendar.getInstance();
    private static final int REQUEST_CAMERA_PERMISSION = 100;
    private static final int REQUEST_IMAGE_CAPTURE = 101;
    private ArrayList<String> fotosGuardadasPaths = new ArrayList<>();

    private ImageView imgEvidencia;
    private LinearLayout layoutFotos;

    private EditText etCodigoFalta;
    private RadioGroup radioGroupClasificacion;
    private EditText etObservacionesDecomiso; // <<< ¡CORREGIDO! Usando el ID del layout

    // Variables para almacenar todos los datos recibidos de DelVehiculoActivity
    private String usuarioLogueado;
    private String conductorLicencia, conductorApellido1, conductorApellido2, conductorApellido3,
            conductorNombre1, conductorNombre2, conductorNombre3, conductorClaseLicencia;
    private String vehiculoTipoPlaca, vehiculoNumeroPlaca, vehiculoCodigoRuta, vehiculoClase,
            vehiculoMarca, vehiculoModelo, vehiculoColor;
    private boolean vehiculoPlacaExtranjera;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.falta);

        // Inicializar vistas del layout
        timePickerDecomiso = findViewById(R.id.timePickerDecomiso);
        etFechaDecomiso = findViewById(R.id.etFechaDecomiso);
        imgEvidencia = findViewById(R.id.imgEvidencia);
        layoutFotos = findViewById(R.id.layoutFotos);

        etCodigoFalta = findViewById(R.id.etCodigoFalta);
        radioGroupClasificacion = findViewById(R.id.radioGroupClasificacion);
        // <<< ¡CORREGIDO! Inicializar con el ID correcto del layout
        etObservacionesDecomiso = findViewById(R.id.etObservacionesDecomiso);

        // RECIBIR: Todos los datos del agente, conductor y vehículo
        usuarioLogueado = getIntent().getStringExtra("usuario_logueado");
        conductorLicencia = getIntent().getStringExtra("conductor_licencia");
        conductorApellido1 = getIntent().getStringExtra("conductor_apellido1");
        conductorApellido2 = getIntent().getStringExtra("conductor_apellido2");
        conductorApellido3 = getIntent().getStringExtra("conductor_apellido3");
        conductorNombre1 = getIntent().getStringExtra("conductor_nombre1");
        conductorNombre2 = getIntent().getStringExtra("conductor_nombre2");
        conductorNombre3 = getIntent().getStringExtra("conductor_nombre3");
        conductorClaseLicencia = getIntent().getStringExtra("conductor_clase_licencia");

        vehiculoTipoPlaca = getIntent().getStringExtra("vehiculo_tipo_placa");
        vehiculoNumeroPlaca = getIntent().getStringExtra("vehiculo_numero_placa");
        vehiculoCodigoRuta = getIntent().getStringExtra("vehiculo_codigo_ruta");
        vehiculoPlacaExtranjera = getIntent().getBooleanExtra("vehiculo_placa_extranjera", false);
        vehiculoClase = getIntent().getStringExtra("vehiculo_clase");
        vehiculoMarca = getIntent().getStringExtra("vehiculo_marca");
        vehiculoModelo = getIntent().getStringExtra("vehiculo_modelo");
        vehiculoColor = getIntent().getStringExtra("vehiculo_color");

        // Opcional: una verificación básica para asegurar que se recibieron los datos principales
        if (usuarioLogueado == null || conductorLicencia == null || vehiculoNumeroPlaca == null) {
            Toast.makeText(this, "Error: Datos previos incompletos. Regresando.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        // Establecer hora actual por defecto
        int currentHour = calendar.get(Calendar.HOUR_OF_DAY);
        int currentMinute = calendar.get(Calendar.MINUTE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            timePickerDecomiso.setHour(currentHour);
            timePickerDecomiso.setMinute(currentMinute);
        } else {
            timePickerDecomiso.setCurrentHour(currentHour);
            timePickerDecomiso.setCurrentMinute(currentMinute);
        }

        setupDatePicker();

        Button btnSiguiente = findViewById(R.id.btnSiguiente);
        btnSiguiente.setOnClickListener(v -> {
            // Recolectar fecha y hora actuales de esta actividad
            String fecha = etFechaDecomiso.getText().toString();
            int hour = 0;
            int minute = 0;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                hour = timePickerDecomiso.getHour();
                minute = timePickerDecomiso.getMinute();
            } else {
                hour = timePickerDecomiso.getCurrentHour();
                minute = timePickerDecomiso.getCurrentMinute();
            }
            String hora = String.format(Locale.getDefault(), "%02d:%02d", hour, minute);
            String fechaHoraInfraccion = fecha + " " + hora;

            // Recolectar los datos de Código de Falta y Clasificación
            String codigoFalta = etCodigoFalta.getText().toString().trim();
            String clasificacionFalta = "";
            int selectedRadioButtonId = radioGroupClasificacion.getCheckedRadioButtonId();
            if (selectedRadioButtonId != -1) {
                RadioButton selectedRadioButton = findViewById(selectedRadioButtonId);
                clasificacionFalta = selectedRadioButton.getText().toString();
            } else {
                Toast.makeText(this, "Por favor, seleccione una Clasificación para la falta.", Toast.LENGTH_SHORT).show();
                return;
            }

            // <<< ¡CORREGIDO! Recolectar el texto del EditText correcto
            String observaciones = etObservacionesDecomiso.getText().toString().trim();

            // Validación básica de campos obligatorios
            if (codigoFalta.isEmpty() || fecha.isEmpty() || clasificacionFalta.isEmpty()) {
                Toast.makeText(this, "Por favor, complete el Código de la Falta, la Fecha y seleccione una Clasificación.", Toast.LENGTH_SHORT).show();
                return;
            }


            // PASAR: Crear Intent y adjuntar TODOS los datos acumulados
            Intent intent = new Intent(falta.this, DecomisosAutoridadOtrosActivity.class);

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

            // Datos del Vehículo (recibidos)
            intent.putExtra("vehiculo_tipo_placa", vehiculoTipoPlaca);
            intent.putExtra("vehiculo_numero_placa", vehiculoNumeroPlaca);
            intent.putExtra("vehiculo_codigo_ruta", vehiculoCodigoRuta);
            intent.putExtra("vehiculo_placa_extranjera", vehiculoPlacaExtranjera);
            intent.putExtra("vehiculo_clase", vehiculoClase);
            intent.putExtra("vehiculo_marca", vehiculoMarca);
            intent.putExtra("vehiculo_modelo", vehiculoModelo);
            intent.putExtra("vehiculo_color", vehiculoColor);

            // Datos de la Falta (propios de esta actividad)
            intent.putExtra("fecha_hora_infraccion", fechaHoraInfraccion);
            intent.putStringArrayListExtra("fotos_evidencia_paths", fotosGuardadasPaths);
            intent.putExtra("falta_codigo", codigoFalta);
            intent.putExtra("falta_clasificacion", clasificacionFalta);
            intent.putExtra("observaciones", observaciones); // <<< ¡CORREGIDO! Pasando las observaciones

            startActivity(intent);
        });

        // Configuración para tomar foto con imgEvidencia
        imgEvidencia.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
            } else {
                abrirCamara();
            }
        });
    }

    private void setupDatePicker() {
        DatePickerDialog.OnDateSetListener dateSetListener = (view, year, month, dayOfMonth) -> {
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, month);
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateDateLabel();
        };

        etFechaDecomiso.setOnClickListener(v ->
                new DatePickerDialog(
                        this,
                        dateSetListener,
                        calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH)
                ).show()
        );
        updateDateLabel();
    }

    private void updateDateLabel() {
        String dateFormat = "dd/MM/yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat, Locale.getDefault());
        etFechaDecomiso.setText(sdf.format(calendar.getTime()));
    }

    private void abrirCamara() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
        } else {
            Toast.makeText(this, "No se encontró una aplicación de cámara.", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK && data != null) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");

            if (imageBitmap == null) {
                Toast.makeText(this, "Error: La imagen capturada está vacía o es inválida.", Toast.LENGTH_SHORT).show();
                return;
            }

            ImageView nuevaFoto = new ImageView(this);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(200, 200);
            params.setMargins(8, 0, 8, 0);
            nuevaFoto.setLayoutParams(params);
            nuevaFoto.setScaleType(ImageView.ScaleType.CENTER_CROP);
            nuevaFoto.setImageBitmap(imageBitmap);

            try {
                File directory = new File(getFilesDir(), "multa_evidencia");
                if (!directory.exists()) {
                    boolean created = directory.mkdirs();
                    if (!created) {
                        Toast.makeText(this, "Error crítico: No se pudo crear el directorio de evidencia.", Toast.LENGTH_LONG).show();
                        return;
                    }
                }

                String fileName = "IMG_" + System.currentTimeMillis() + ".jpg";
                File file = new File(directory, fileName);

                FileOutputStream fos = new FileOutputStream(file);
                imageBitmap.compress(Bitmap.CompressFormat.JPEG, 90, fos);
                fos.flush();
                fos.close();

                layoutFotos.addView(nuevaFoto);
                fotosGuardadasPaths.add(file.getAbsolutePath());
                Toast.makeText(this, "Foto guardada: " + file.getName(), Toast.LENGTH_SHORT).show();

            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "Error al guardar la foto: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        } else if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_CANCELED) {
            Toast.makeText(this, "Captura de imagen cancelada.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                abrirCamara();
            } else {
                Toast.makeText(this, "Permiso de cámara denegado. No se podrá tomar evidencia fotográfica.", Toast.LENGTH_LONG).show();
            }
        }
    }
}
