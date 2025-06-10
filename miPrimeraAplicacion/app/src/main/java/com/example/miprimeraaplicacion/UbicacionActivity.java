package com.example.miprimeraaplicacion;

import android.Manifest;

import android.annotation.SuppressLint;

import android.content.ContentResolver;

import android.content.ContentValues;

import android.content.Intent;

import android.content.pm.PackageManager;

import android.location.Address;

import android.location.Geocoder;

import android.location.Location;

import android.net.Uri;

import android.os.Build;

import android.os.Bundle;

import android.os.Environment;

import android.provider.MediaStore;

import android.widget.Button;

import android.widget.TextView;

import android.widget.Toast;



import androidx.annotation.NonNull;

import androidx.appcompat.app.AppCompatActivity;

import androidx.core.app.ActivityCompat;

import androidx.core.content.ContextCompat;

import androidx.core.content.FileProvider;



import com.google.android.gms.location.FusedLocationProviderClient;

import com.google.android.gms.location.LocationServices;



import com.itextpdf.kernel.pdf.PdfWriter;

import com.itextpdf.kernel.pdf.PdfDocument;

import com.itextpdf.layout.Document;

import com.itextpdf.layout.element.Paragraph;

import com.itextpdf.layout.properties.TextAlignment;

import com.itextpdf.kernel.colors.ColorConstants;

import com.itextpdf.kernel.colors.DeviceRgb;



import java.io.File;

import java.io.FileNotFoundException;

import java.io.FileOutputStream;

import java.io.IOException;

import java.io.OutputStream;

import java.text.SimpleDateFormat;

import java.util.ArrayList;

import java.util.Date;

import java.util.List;

import java.util.Locale;



public class UbicacionActivity extends AppCompatActivity {



    private static final int REQUEST_LOCATION_PERMISSION = 1;

    private static final int REQUEST_WRITE_STORAGE = 112;



    private TextView tvUbicacion;

    private FusedLocationProviderClient fusedLocationClient;

    private Button btnGuardarMulta;



    private String usuarioLogueado;

    private String agenteOni, agentePuesto;

    private String conductorLicencia, conductorApellido1, conductorApellido2, conductorApellido3,

    conductorNombre1, conductorNombre2, conductorNombre3, conductorClaseLicencia;

    private String vehiculoTipoPlaca, vehiculoNumeroPlaca, vehiculoCodigoRuta, vehiculoClase,

    vehiculoMarca, vehiculoModelo, vehiculoColor;

    private boolean vehiculoPlacaExtranjera;

    private String fechaHoraInfraccion;

    private ArrayList<String> fotosEvidenciaPaths;

    private String multaMonto, multaCategoria;



    private String faltaCodigo;

    private String faltaClasificacion;

    private String observaciones;



    private boolean decomisoVehiculos, decomisoTarjetaCirculacion, decomisoLicencia,

    decomisoPlacas, decomisoPoliza, decomisoPermisosLinea;

    private boolean otrosConductorAusente, otrosSeNegoFirmar, otrosAparatoLaser,

    otrosPruebaAlcotest, otrosDestruyoEsquela, otrosVehiculoRemolcado;



    private String ubicacionDepartamento = "Desconocido";

    private String ubicacionMunicipio = "Desconocido";

    private String ubicacionCalle = "Desconocida";



    private DB_multas dbMultas;



    private static final int REQUEST_SHARE_PDF = 1001;



    @Override

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.departamento);



        tvUbicacion = findViewById(R.id.tvUbicacion);

        btnGuardarMulta = findViewById(R.id.btnGuardarMulta);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);



        dbMultas = new DB_multas(this);



        Bundle extras = getIntent().getExtras();

        if (extras != null) {

            usuarioLogueado = extras.getString("usuario_logueado");

            agenteOni = extras.getString("agente_oni");

            agentePuesto = extras.getString("agente_puesto");

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

            multaMonto = extras.getString("multa_monto");

            multaCategoria = extras.getString("multa_categoria");

            faltaCodigo = extras.getString("falta_codigo");

            faltaClasificacion = extras.getString("falta_clasificacion");

            observaciones = extras.getString("observaciones");

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

        }



        if (usuarioLogueado == null || agenteOni == null || agentePuesto == null ||

                conductorLicencia == null || conductorApellido1 == null || conductorNombre1 == null || conductorClaseLicencia == null ||

                vehiculoTipoPlaca == null || vehiculoNumeroPlaca == null || vehiculoClase == null ||

                fechaHoraInfraccion == null || multaMonto == null || multaCategoria == null ||

                faltaCodigo == null || faltaClasificacion == null || observaciones == null) {

            Toast.makeText(this, "Error crítico: Faltan datos esenciales para guardar la multa. Asegúrese de que todos los campos previos fueron llenados.", Toast.LENGTH_LONG).show();

            finish();

            return;

        }



        pedirPermisoUbicacion();



        btnGuardarMulta.setOnClickListener(v -> {

            guardarMultaEnBaseDeDatos();

        });

    }



    private void pedirPermisoUbicacion() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)

                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,

                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},

                    REQUEST_LOCATION_PERMISSION);

        } else {

            obtenerUbicacion();

        }

    }



    @SuppressLint("MissingPermission")

    private void obtenerUbicacion() {

        fusedLocationClient.getLastLocation().addOnSuccessListener(location -> {

            if (location != null) {

                obtenerDireccion(location);

            } else {

                tvUbicacion.setText("No se pudo obtener la ubicación. Intente de nuevo.");

                Toast.makeText(this, "No se pudo obtener la ubicación.", Toast.LENGTH_SHORT).show();

            }

        }).addOnFailureListener(e -> {

            tvUbicacion.setText("Error al obtener la ubicación: " + e.getMessage());

            Toast.makeText(this, "Error al obtener la ubicación: " + e.getMessage(), Toast.LENGTH_LONG).show();

        });

    }



    private void obtenerDireccion(Location location) {

        Geocoder geocoder = new Geocoder(this, Locale.getDefault());

        try {

            List<Address> listaDirecciones = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);

            if (!listaDirecciones.isEmpty()) {

                Address direccion = listaDirecciones.get(0);



                ubicacionCalle = direccion.getThoroughfare() != null ? direccion.getThoroughfare() : "Desconocida";

                ubicacionMunicipio = direccion.getLocality() != null ? direccion.getLocality() : "Desconocido";

                ubicacionDepartamento = direccion.getAdminArea() != null ? direccion.getAdminArea() : "Desconocido";



                String resultado = "Departamento: " + ubicacionDepartamento + "\n"

                        + "Municipio: " + ubicacionMunicipio + "\n"

                        + "Calle: " + ubicacionCalle;



                tvUbicacion.setText(resultado);

                Toast.makeText(this, "Ubicación obtenida correctamente.", Toast.LENGTH_SHORT).show();



            } else {

                tvUbicacion.setText("No se encontró dirección para la ubicación.");

                Toast.makeText(this, "No se encontró dirección para la ubicación.", Toast.LENGTH_SHORT).show();

            }

        } catch (IOException e) {

            tvUbicacion.setText("Error al obtener dirección: " + e.getMessage());

            Toast.makeText(this, "Error al obtener dirección: " + e.getMessage(), Toast.LENGTH_LONG).show();

            e.printStackTrace();

        }

    }



    @Override

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);



        if (requestCode == REQUEST_LOCATION_PERMISSION) {

            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                obtenerUbicacion();

            } else {

                Toast.makeText(this, "Permiso de ubicación denegado. No se podrá guardar la ubicación de la multa.", Toast.LENGTH_LONG).show();

            }

        } else if (requestCode == REQUEST_WRITE_STORAGE) {

            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                generateAndSharePdf();

            } else {

                Toast.makeText(this, "Permiso de almacenamiento denegado. No se puede generar o compartir el PDF de la multa.", Toast.LENGTH_LONG).show();

            }

        }

    }



    private void guardarMultaEnBaseDeDatos() {

        String fotosPathsString = "";

        if (fotosEvidenciaPaths != null && !fotosEvidenciaPaths.isEmpty()) {

            fotosPathsString = String.join(";", fotosEvidenciaPaths);

        }

        int vehiculoPlacaExtranjeraInt = vehiculoPlacaExtranjera ? 1 : 0;

        long idMulta = dbMultas.insertarMulta(

                usuarioLogueado,

                agenteOni,

                agentePuesto,

                conductorLicencia,

                conductorApellido1,

                conductorApellido2,

                conductorApellido3,

                conductorNombre1,

                conductorNombre2,

                conductorNombre3,

                conductorClaseLicencia,

                vehiculoTipoPlaca,

                vehiculoNumeroPlaca,

                vehiculoCodigoRuta,

                vehiculoPlacaExtranjeraInt,

                vehiculoClase,

                vehiculoMarca,

                vehiculoModelo,

                vehiculoColor,

                fechaHoraInfraccion,

                fotosPathsString,

                multaMonto,

                multaCategoria,

                ubicacionDepartamento,

                ubicacionMunicipio,

                ubicacionCalle,

                faltaCodigo,

                faltaClasificacion,

                observaciones,

                decomisoVehiculos,

                decomisoTarjetaCirculacion,

                decomisoLicencia,

                decomisoPlacas,

                decomisoPoliza,

                decomisoPermisosLinea,

                otrosConductorAusente,

                otrosSeNegoFirmar,

                otrosAparatoLaser,

                otrosPruebaAlcotest,

                otrosDestruyoEsquela,

                otrosVehiculoRemolcado

        );

        if (idMulta != -1) {

            Toast.makeText(this, "Multa guardada exitosamente con ID: " + idMulta, Toast.LENGTH_LONG).show();

            generateAndSharePdf();

        } else {

            Toast.makeText(this, "Error al guardar la multa.", Toast.LENGTH_LONG).show();

        }

    }



    private void generateAndSharePdf() {

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());

        String pdfFileName = "MULTA_" + (vehiculoNumeroPlaca != null ? vehiculoNumeroPlaca : "SIN_PLACA") + "_" + timeStamp + ".pdf";



        Uri pdfUri = null;

        File pdfFile = null;



        try {

            File cachePath = new File(getCacheDir(), "shared_pdfs");

            if (!cachePath.exists()) {

                cachePath.mkdirs();

            }

            pdfFile = new File(cachePath, pdfFileName);

            OutputStream outputStream = new FileOutputStream(pdfFile);



            PdfWriter writer = new PdfWriter(outputStream);

            PdfDocument pdf = new PdfDocument(writer);

            Document document = new Document(pdf);



            DeviceRgb darkGray = new DeviceRgb(64, 64, 64);



            document.add(new Paragraph("REPORTE DE MULTA")

                    .setFontColor(ColorConstants.BLUE)

                    .setFontSize(18)

                    .setBold()

                    .setTextAlignment(TextAlignment.CENTER));



            document.add(new Paragraph("Fecha de Generación: " + new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault()).format(new Date()))

                    .setFontSize(10)

                    .setFontColor(ColorConstants.BLACK));

            document.add(new Paragraph("\n"));



            document.add(new Paragraph("--- DATOS DEL AGENTE ---")

                    .setFontColor(darkGray)

                    .setFontSize(14)

                    .setBold());

            document.add(new Paragraph("Usuario Logueado: " + (usuarioLogueado != null ? usuarioLogueado : "N/A"))

                    .setFontSize(10).setFontColor(ColorConstants.BLACK));

            document.add(new Paragraph("ONI: " + (agenteOni != null ? agenteOni : "N/A"))

                    .setFontSize(10).setFontColor(ColorConstants.BLACK));

            document.add(new Paragraph("Puesto: " + (agentePuesto != null ? agentePuesto : "N/A"))

                    .setFontSize(10).setFontColor(ColorConstants.BLACK));

            document.add(new Paragraph("\n"));



            document.add(new Paragraph("--- DATOS DEL CONDUCTOR ---")

                    .setFontColor(darkGray)

                    .setFontSize(14)

                    .setBold());

            document.add(new Paragraph("Licencia: " + (conductorLicencia != null ? conductorLicencia : "N/A"))

                    .setFontSize(10).setFontColor(ColorConstants.BLACK));

            document.add(new Paragraph("Nombres: " + (conductorNombre1 != null ? conductorNombre1 : "") + " " + (conductorNombre2 != null ? conductorNombre2 : "") + " " + (conductorNombre3 != null ? conductorNombre3 : ""))

                    .setFontSize(10).setFontColor(ColorConstants.BLACK));

            document.add(new Paragraph("Apellidos: " + (conductorApellido1 != null ? conductorApellido1 : "") + " " + (conductorApellido2 != null ? conductorApellido2 : "") + " " + (conductorApellido3 != null ? conductorApellido3 : ""))

                    .setFontSize(10).setFontColor(ColorConstants.BLACK));

            document.add(new Paragraph("Clase Licencia: " + (conductorClaseLicencia != null ? conductorClaseLicencia : "N/A"))

                    .setFontSize(10).setFontColor(ColorConstants.BLACK));

            document.add(new Paragraph("\n"));



            document.add(new Paragraph("--- DATOS DEL VEHÍCULO ---")

                    .setFontColor(darkGray)

                    .setFontSize(14)

                    .setBold());

            document.add(new Paragraph("Tipo de Placa: " + (vehiculoTipoPlaca != null ? vehiculoTipoPlaca : "N/A"))

                    .setFontSize(10).setFontColor(ColorConstants.BLACK));

            document.add(new Paragraph("Número de Placa: " + (vehiculoNumeroPlaca != null ? vehiculoNumeroPlaca : "N/A"))

                    .setFontSize(10).setFontColor(ColorConstants.BLACK));

            document.add(new Paragraph("Código de Ruta: " + (vehiculoCodigoRuta != null ? vehiculoCodigoRuta : "N/A"))

                    .setFontSize(10).setFontColor(ColorConstants.BLACK));

            document.add(new Paragraph("Placa Extranjera: " + (vehiculoPlacaExtranjera ? "Sí" : "No"))

                    .setFontSize(10).setFontColor(ColorConstants.BLACK));

            document.add(new Paragraph("Clase: " + (vehiculoClase != null ? vehiculoClase : "N/A"))

                    .setFontSize(10).setFontColor(ColorConstants.BLACK));

            document.add(new Paragraph("Marca: " + (vehiculoMarca != null ? vehiculoMarca : "N/A"))

                    .setFontSize(10).setFontColor(ColorConstants.BLACK));

            document.add(new Paragraph("Modelo: " + (vehiculoModelo != null ? vehiculoModelo : "N/A"))

                    .setFontSize(10).setFontColor(ColorConstants.BLACK));

            document.add(new Paragraph("Color: " + (vehiculoColor != null ? vehiculoColor : "N/A"))

                    .setFontSize(10).setFontColor(ColorConstants.BLACK));

            document.add(new Paragraph("\n"));



            document.add(new Paragraph("--- DATOS DE LA FALTA Y MULTA ---")

                    .setFontColor(darkGray)

                    .setFontSize(14)

                    .setBold());

            document.add(new Paragraph("Fecha y Hora Infracción: " + (fechaHoraInfraccion != null ? fechaHoraInfraccion : "N/A"))

                    .setFontSize(10).setFontColor(ColorConstants.BLACK));

            document.add(new Paragraph("Código de Falta: " + (faltaCodigo != null ? faltaCodigo : "N/A"))

                    .setFontSize(10).setFontColor(ColorConstants.BLACK));

            document.add(new Paragraph("Clasificación: " + (faltaClasificacion != null ? faltaClasificacion : "N/A"))

                    .setFontSize(10).setFontColor(ColorConstants.BLACK));

            document.add(new Paragraph("Observaciones: " + (observaciones != null ? observaciones : "N/A"))

                    .setFontSize(10).setFontColor(ColorConstants.BLACK));

            document.add(new Paragraph("Monto de la Multa: " + (multaMonto != null ? multaMonto : "N/A"))

                    .setFontSize(10).setFontColor(ColorConstants.BLACK));

            document.add(new Paragraph("Categoría de la Multa: " + (multaCategoria != null ? multaCategoria : "N/A"))

                    .setFontSize(10).setFontColor(ColorConstants.BLACK));



            if (fotosEvidenciaPaths != null && !fotosEvidenciaPaths.isEmpty()) {

                document.add(new Paragraph("Rutas de Fotos de Evidencia:")

                        .setFontSize(10)

                        .setFontColor(ColorConstants.BLACK));

                for (String path : fotosEvidenciaPaths) {

                    document.add(new Paragraph("- " + path)

                            .setFontSize(8)

                            .setFontColor(ColorConstants.BLACK));

                }

            } else {

                document.add(new Paragraph("Fotos de Evidencia: Ninguna")

                        .setFontSize(10)

                        .setFontColor(ColorConstants.BLACK));

            }

            document.add(new Paragraph("\n"));



            document.add(new Paragraph("--- DECOMISOS ---")

                    .setFontColor(darkGray)

                    .setFontSize(14)

                    .setBold());

            boolean hasDecomisos = false;

            if (decomisoVehiculos) { document.add(new Paragraph("- Vehículos").setFontSize(10).setFontColor(ColorConstants.BLACK)); hasDecomisos = true; }

            if (decomisoTarjetaCirculacion) { document.add(new Paragraph("- Tarjeta de Circulación").setFontSize(10).setFontColor(ColorConstants.BLACK)); hasDecomisos = true; }

            if (decomisoLicencia) { document.add(new Paragraph("- Licencia").setFontSize(10).setFontColor(ColorConstants.BLACK)); hasDecomisos = true; }

            if (decomisoPlacas) { document.add(new Paragraph("- Placas").setFontSize(10).setFontColor(ColorConstants.BLACK)); hasDecomisos = true; }

            if (decomisoPoliza) { document.add(new Paragraph("- Póliza").setFontSize(10).setFontColor(ColorConstants.BLACK)); hasDecomisos = true; }

            if (decomisoPermisosLinea) { document.add(new Paragraph("- Permisos de Línea").setFontSize(10).setFontColor(ColorConstants.BLACK)); hasDecomisos = true; }

            if (!hasDecomisos) { document.add(new Paragraph("Ninguno").setFontSize(10).setFontColor(ColorConstants.BLACK)); }

            document.add(new Paragraph("\n"));



            document.add(new Paragraph("--- OTROS DETALLES ---")

                    .setFontColor(darkGray)

                    .setFontSize(14)

                    .setBold());

            boolean hasOtros = false;

            if (otrosConductorAusente) { document.add(new Paragraph("- Conductor Ausente").setFontSize(10).setFontColor(ColorConstants.BLACK)); hasOtros = true; }

            if (otrosSeNegoFirmar) { document.add(new Paragraph("- Se Negó a Firmar").setFontSize(10).setFontColor(ColorConstants.BLACK)); hasOtros = true; }

            if (otrosAparatoLaser) { document.add(new Paragraph("- Uso de Aparato Láser").setFontSize(10).setFontColor(ColorConstants.BLACK)); hasOtros = true; }

            if (otrosPruebaAlcotest) { document.add(new Paragraph("- Prueba de Alcotest Realizada").setFontSize(10).setFontColor(ColorConstants.BLACK)); hasOtros = true; }

            if (otrosDestruyoEsquela) { document.add(new Paragraph("- Destruyó Esquela").setFontSize(10).setFontColor(ColorConstants.BLACK)); hasOtros = true; }

            if (otrosVehiculoRemolcado) { document.add(new Paragraph("- Vehículo Remolcado").setFontSize(10).setFontColor(ColorConstants.BLACK)); hasOtros = true; }

            if (!hasOtros) { document.add(new Paragraph("Ninguno").setFontSize(10).setFontColor(ColorConstants.BLACK)); }

            document.add(new Paragraph("\n"));



            document.add(new Paragraph("--- UBICACIÓN ---")

                    .setFontColor(darkGray)

                    .setFontSize(14)

                    .setBold());

            document.add(new Paragraph("Departamento: " + (ubicacionDepartamento != null ? ubicacionDepartamento : "N/A"))

                    .setFontSize(10).setFontColor(ColorConstants.BLACK));

            document.add(new Paragraph("Municipio: " + (ubicacionMunicipio != null ? ubicacionMunicipio : "N/A"))

                    .setFontSize(10).setFontColor(ColorConstants.BLACK));

            document.add(new Paragraph("Calle/Dirección: " + (ubicacionCalle != null ? ubicacionCalle : "N/A"))

                    .setFontSize(10).setFontColor(ColorConstants.BLACK));

            document.add(new Paragraph("\n"));



            document.close();

            outputStream.close();



            pdfUri = FileProvider.getUriForFile(this, getApplicationContext().getPackageName() + ".provider", pdfFile);



            sharePdf(pdfUri);



        } catch (IOException e) {

            e.printStackTrace();

            Toast.makeText(this, "Error al generar o compartir el PDF: " + e.getMessage(), Toast.LENGTH_LONG).show();

        }

    }



// --- Métodos MODIFICADOS/AÑADIDOS para compartir y cerrar sesión ---



    private void sharePdf(Uri pdfUri) {

        try {

            if (pdfUri == null) {

                Toast.makeText(this, "Error: No se pudo generar el PDF para compartir.", Toast.LENGTH_LONG).show();

                redirigirAlLoginYCerrarSesion();

                return;

            }



            Intent emailIntent = new Intent(Intent.ACTION_SEND);

            emailIntent.setType("application/pdf");

            emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{""});

            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Reporte de Multa");

            emailIntent.putExtra(Intent.EXTRA_TEXT, "Adjunto el reporte de la multa generada.");

            emailIntent.putExtra(Intent.EXTRA_STREAM, pdfUri);

            emailIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);



            startActivityForResult(Intent.createChooser(emailIntent, "Enviar PDF de multa vía..."), REQUEST_SHARE_PDF);



        } catch (Exception e) {

            Toast.makeText(this, "No se encontró una aplicación para enviar correos o compartir el PDF. " + e.getMessage(), Toast.LENGTH_LONG).show();

            e.printStackTrace();

            redirigirAlLoginYCerrarSesion();

        }

    }



    @Override

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);



        if (requestCode == REQUEST_SHARE_PDF) {

            Toast.makeText(this, "Interacción de compartir PDF finalizada. Cerrando sesión...", Toast.LENGTH_SHORT).show();

            redirigirAlLoginYCerrarSesion();

        }

    }



    private void redirigirAlLoginYCerrarSesion() {

        Intent intent = new Intent(this, SeleccionLoginActivity.class);

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);

        startActivity(intent);

        finish();

    }



// --- Fin de métodos MODIFICADOS/AÑADIDOS ---



    @Override

    protected void onResume() {

        super.onResume();

    }

}
