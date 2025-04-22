package com.example.miprimeraaplicacion;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    FloatingActionButton fab;
    Button btn;
    TextView tempVal;
    DB db;
    String accion = "nuevo", idproducto = "", id = "", rev = "";
    ImageView img;
    String urlCompletaFoto = "";
    Intent tomarFotoIntent;
    utilidades utls;
    DetectarInternet di;

    @SuppressLint({"MissingInflatedId", "WrongViewCast"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        utls = new utilidades();
        img = findViewById(R.id.imgFotoAdaptador);
        db = new DB(this);
        btn = findViewById(R.id.btnGuardarProducto);
        btn.setOnClickListener(view -> guardarProducto());

        fab = findViewById(R.id.ltsProductos);
        fab.setOnClickListener(view -> abrirVentana());

        mostrarDatos();
        tomarFoto();
    }

    private void mostrarDatos() {
        try {
            Bundle parametros = getIntent().getExtras();
            accion = parametros.getString("accion");
            if (accion.equals("modificar")) {
                JSONObject datos = new JSONObject(parametros.getString("producto"));
                id = datos.getString("_id");
                rev = datos.getString("_rev");
                idproducto = datos.getString("idproducto");

                ((TextView) findViewById(R.id.txtNombre)).setText(datos.getString("nombre"));
                ((TextView) findViewById(R.id.txtPrecio)).setText(datos.getString("precio"));
                ((TextView) findViewById(R.id.txtCosto)).setText(datos.getString("costo"));
                ((TextView) findViewById(R.id.txtGanancias)).setText(datos.getString("ganancias"));

                urlCompletaFoto = datos.getString("urlFoto");
                img.setImageURI(Uri.parse(urlCompletaFoto));
            } else {
                idproducto = utls.generarUnicoId();
            }
        } catch (Exception e) {
            mostrarMsg("Error: " + e.getMessage());
        }
    }

    private void tomarFoto() {
        img.setOnClickListener(view -> {
            tomarFotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            File fotoProducto;
            try {
                fotoProducto = crearImagenProducto();
                if (fotoProducto != null) {
                    Uri uriFotoProducto = FileProvider.getUriForFile(MainActivity.this,
                            "com.example.miprimeraaplicacion.fileprovider", fotoProducto);
                    tomarFotoIntent.putExtra(MediaStore.EXTRA_OUTPUT, uriFotoProducto);
                    startActivityForResult(tomarFotoIntent, 1);
                } else {
                    mostrarMsg("No se pudo crear la imagen.");
                }
            } catch (Exception e) {
                mostrarMsg("Error: " + e.getMessage());
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if (requestCode == 1 && resultCode == RESULT_OK) {
                img.setImageURI(Uri.parse(urlCompletaFoto));
            } else {
                mostrarMsg("No se tomó la foto.");
            }
        } catch (Exception e) {
            mostrarMsg("Error: " + e.getMessage());
        }
    }

    private File crearImagenProducto() throws Exception {
        String fechaHoraMs = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()),
                fileName = "imagen_" + fechaHoraMs + "_";
        File dirAlmacenamiento = getExternalFilesDir(Environment.DIRECTORY_DCIM);
        if (!dirAlmacenamiento.exists()) {
            dirAlmacenamiento.mkdir();
        }
        File image = File.createTempFile(fileName, ".jpg", dirAlmacenamiento);
        urlCompletaFoto = image.getAbsolutePath();
        return image;
    }

    private void mostrarMsg(String msg) {
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
    }

    private void abrirVentana() {
        Intent intent = new Intent(this, ListaProductos.class);
        startActivity(intent);
    }

    private void guardarProducto() {
        try {
            String nombre = ((TextView) findViewById(R.id.txtNombre)).getText().toString();
            String precio = ((TextView) findViewById(R.id.txtPrecio)).getText().toString();
            String costo = ((TextView) findViewById(R.id.txtCosto)).getText().toString();
            String ganancias = ((TextView) findViewById(R.id.txtGanancias)).getText().toString();

            JSONObject datosProducto = new JSONObject();
            if (accion.equals("modificar")) {
                datosProducto.put("_id", id);
                datosProducto.put("_rev", rev);
            }
            datosProducto.put("idproducto", idproducto);
            datosProducto.put("nombre", nombre);
            datosProducto.put("precio", precio);
            datosProducto.put("costo", costo);
            datosProducto.put("ganancias", ganancias);
            datosProducto.put("urlFoto", urlCompletaFoto);

            di = new DetectarInternet(this);
            if (di.hayConexionInternet()) {
                enviarDatosServidor objEnviarDatos = new enviarDatosServidor(this);
                String respuesta = objEnviarDatos.execute(datosProducto.toString(), "POST", utilidades.url_mto).get();
                JSONObject respuestaJSON = new JSONObject(respuesta);
                if (respuestaJSON.getBoolean("ok")) {
                    id = respuestaJSON.getString("id");
                    rev = respuestaJSON.getString("rev");
                } else {
                    mostrarMsg("Error al guardar en CouchDB: " + respuestaJSON.getString("msg"));
                }
            }

            String[] datosLocales = {idproducto, nombre, precio, costo, ganancias, urlCompletaFoto};
            db.administrar_productos(accion, datosLocales);

            Toast.makeText(getApplicationContext(), "Registro guardado con éxito en ambas bases de datos.", Toast.LENGTH_LONG).show();
            abrirVentana();
        } catch (Exception e) {
            mostrarMsg("Error al guardar: " + e.getMessage());
        }
    }
}