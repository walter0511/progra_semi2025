package com.example.miprimeraaplicacion;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class EditarProductoActivity extends AppCompatActivity {
    private EditText txtNombre, txtPrecio, txtCosto, txtStock;
    private ImageView imgProducto;
    private Button btnGuardar;
    private DB db;
    private String accion = "nuevo";
    private String idProducto;
    private String urlCompletaFoto = "";
    private utilidades utls;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activiti_editar_producto);

        db = new DB(this);
        utls = new utilidades();

        // Inicializar vistas
        txtNombre = findViewById(R.id.txtNombreProducto);
        txtPrecio = findViewById(R.id.txtPrecio);
        txtCosto = findViewById(R.id.txtCosto);
        txtStock = findViewById(R.id.txtStock);
        imgProducto = findViewById(R.id.imgProducto);
        btnGuardar = findViewById(R.id.btnGuardarProducto);

        // Obtener parámetros
        accion = getIntent().getStringExtra("accion");
        if (accion.equals("modificar")) {
            idProducto = getIntent().getStringExtra("idProducto");
            cargarDatosProducto();
        } else {
            idProducto = utls.generarUnicoId();
        }

        // Configurar listeners
        imgProducto.setOnClickListener(v -> tomarFoto());
        btnGuardar.setOnClickListener(v -> guardarProducto());
    }

    private void cargarDatosProducto() {
        Cursor cursor = db.lista_productos();
        if (cursor.moveToFirst()) {
            do {
                if (cursor.getString(0).equals(idProducto)) {
                    txtNombre.setText(cursor.getString(1));
                    urlCompletaFoto = cursor.getString(2);
                    txtPrecio.setText(String.valueOf(cursor.getDouble(3)));
                    txtCosto.setText(String.valueOf(cursor.getDouble(4)));
                    txtStock.setText(String.valueOf(cursor.getInt(5)));

                    // Cargar imagen
                    if (urlCompletaFoto != null && !urlCompletaFoto.isEmpty()) {
                        Bitmap bitmap = BitmapFactory.decodeFile(urlCompletaFoto);
                        if (bitmap != null) {
                            imgProducto.setImageBitmap(bitmap);
                        }
                    }
                    break;
                }
            } while (cursor.moveToNext());
        }
        cursor.close();
    }

    private void tomarFoto() {
        try {
            File fotoProducto = crearImagenProducto();
            if (fotoProducto != null) {
                Uri uriFotoProducto = FileProvider.getUriForFile(
                        this,
                        "com.example.miprimeraaplicacion.fileprovider",
                        fotoProducto
                );
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, uriFotoProducto);
                startActivityForResult(intent, 1);
            }
        } catch (Exception e) {
            Toast.makeText(this, "Error al tomar foto: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private File crearImagenProducto() throws Exception {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String fileName = "IMG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(fileName, ".jpg", storageDir);
        urlCompletaFoto = image.getAbsolutePath();
        return image;
    }

    private void guardarProducto() {
        try {
            String nombre = txtNombre.getText().toString();
            double precio = Double.parseDouble(txtPrecio.getText().toString());
            double costo = Double.parseDouble(txtCosto.getText().toString());
            int stock = Integer.parseInt(txtStock.getText().toString());

            String[] datos = {
                    idProducto,
                    nombre,
                    urlCompletaFoto,
                    String.valueOf(precio),
                    String.valueOf(costo),
                    String.valueOf(stock)
            };

            // Guardar en SQLite
            String resultado = db.administrar_productos(accion, datos);

            if (resultado.equals("ok")) {
                // Si hay conexión a internet, guardar en CouchDB
                if (new detectarInternet(this).hayConexionInternet()) {
                    JSONObject jsonProducto = new JSONObject();
                    jsonProducto.put("_id", idProducto);
                    jsonProducto.put("nombre", nombre);
                    jsonProducto.put("imagenUrl", urlCompletaFoto);
                    jsonProducto.put("precio", precio);
                    jsonProducto.put("costo", costo);
                    jsonProducto.put("stock", stock);
                    jsonProducto.put("tipo", "producto");

                    new enviarDatosServidor(this).execute(
                            jsonProducto.toString(),
                            accion.equals("nuevo") ? "POST" : "PUT",
                            utilidades.url_mto + (accion.equals("modificar") ? "/" + idProducto : "")
                    ).get();
                }

                Toast.makeText(this, "Producto guardado", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Error: " + resultado, Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            try {
                Bitmap bitmap = BitmapFactory.decodeFile(urlCompletaFoto);
                imgProducto.setImageBitmap(bitmap);
            } catch (Exception e) {
                Toast.makeText(this, "Error al cargar imagen", Toast.LENGTH_SHORT).show();
            }
        }
    }
}