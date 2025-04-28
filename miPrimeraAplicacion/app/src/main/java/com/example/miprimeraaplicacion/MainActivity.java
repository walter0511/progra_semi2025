package com.example.miprimeraaplicacion;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    private EditText txtNombre, txtPrecio, txtCosto, txtStock;
    private ImageView imgProducto;
    private Button btnGuardar;
    private FloatingActionButton fabLista;
    private DB db;
    private String accion = "nuevo", idProducto = "", urlCompletaFoto = " ";
    private utilidades utls;
    private detectarInternet di;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_productos);

        utls = new utilidades();
        db = new DB(this);
        di = new detectarInternet(this);

        txtNombre = findViewById(R.id.txtNombreProducto);
        txtPrecio = findViewById(R.id.txtPrecio);
        txtCosto = findViewById(R.id.txtCosto);
        txtStock = findViewById(R.id.txtStock);
        imgProducto = findViewById(R.id.imgProducto);
        btnGuardar = findViewById(R.id.btnGuardarProducto);
        fabLista = findViewById(R.id.fabListaProductos);

        btnGuardar.setOnClickListener(v -> guardarProducto());
        fabLista.setOnClickListener(v -> abrirLista());
        imgProducto.setOnClickListener(v -> tomarFoto());

        // Manejar acción (nuevo o modificar)
        Intent intent = getIntent();
        accion = intent.getStringExtra("accion");
        if (accion == null) accion = "nuevo";

        if (accion.equals("modificar")) {
            idProducto = intent.getStringExtra("idProducto");
            if (idProducto != null) {
                cargarDatosProducto();
            }
        } else {
            idProducto = utls.generarUnicoId();
        }
    }

    private void cargarDatosProducto() {
        try (Cursor cursor = db.lista_productos()) {
            boolean encontrado = false;
            if (cursor.moveToFirst()) {
                do {
                    if (cursor.getString(0).equals(idProducto)) {
                        txtNombre.setText(cursor.getString(1));
                        txtPrecio.setText(String.valueOf(cursor.getDouble(3)));
                        txtCosto.setText(String.valueOf(cursor.getDouble(4)));
                        txtStock.setText(String.valueOf(cursor.getInt(5)));
                        urlCompletaFoto = cursor.getString(2);

                        if (urlCompletaFoto != null && !urlCompletaFoto.isEmpty()) {
                            Bitmap bitmap = BitmapFactory.decodeFile(urlCompletaFoto);
                            if (bitmap != null) {
                                imgProducto.setImageBitmap(bitmap);
                            }
                        }
                        encontrado = true;
                        break;
                    }
                } while (cursor.moveToNext());
            }
            if (!encontrado) {
                Toast.makeText(this, "Producto no encontrado", Toast.LENGTH_SHORT).show();
                finish();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Error al cargar producto: " + e.getMessage(),
                    Toast.LENGTH_LONG).show();
        }
    }

    private void tomarFoto() {
        try {
            File fotoProducto = crearImagenProducto();
            if (fotoProducto != null) {
                Uri uriFotoProducto = FileProvider.getUriForFile(this,
                        "com.example.miprimeraaplicacion.fileprovider", fotoProducto);
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, uriFotoProducto);
                startActivityForResult(intent, 1);
            }
        } catch (Exception e) {
            Toast.makeText(this, "Error al tomar foto: " + e.getMessage(),
                    Toast.LENGTH_LONG).show();
        }
    }

    private File crearImagenProducto() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);
        urlCompletaFoto = image.getAbsolutePath();
        return image;
    }

    private void abrirLista() {
        startActivity(new Intent(this, lista_productos.class));
        finish();
    }

    private void guardarProducto() {
        try {
            // Validar campos
            String nombre = txtNombre.getText().toString().trim();
            if (nombre.isEmpty()) {
                Toast.makeText(this, "Ingrese el nombre del producto", Toast.LENGTH_SHORT).show();
                return;
            }

            double precio = Double.parseDouble(txtPrecio.getText().toString());
            double costo = Double.parseDouble(txtCosto.getText().toString());
            int stock = Integer.parseInt(txtStock.getText().toString());

            if (precio <= 0 || costo <= 0 || stock < 0) {
                Toast.makeText(this, "Valores inválidos", Toast.LENGTH_SHORT).show();
                return;
            }

            // Guardar en SQLite
            String[] datos = {
                    idProducto,
                    nombre,
                    urlCompletaFoto != null ? urlCompletaFoto : "",
                    String.valueOf(precio),
                    String.valueOf(costo),
                    String.valueOf(stock)
            };

            String resultado = db.administrar_productos(accion, datos);
            if (!resultado.equals("ok")) {
                Toast.makeText(this, "Error SQLite: " + resultado, Toast.LENGTH_LONG).show();
                return;
            }

            // Si hay internet, sincronizar con CouchDB
            if (di.hayConexionInternet()) {
                JSONObject jsonProducto = new JSONObject();
                jsonProducto.put("_id", idProducto);
                jsonProducto.put("idProducto", idProducto);
                jsonProducto.put("nombre", nombre);
                jsonProducto.put("imagenUrl", urlCompletaFoto != null ? urlCompletaFoto : "");
                jsonProducto.put("precio", precio);
                jsonProducto.put("costo", costo);
                jsonProducto.put("stock", stock);

                String url = accion.equals("nuevo") ?
                        utilidades.url_mto :
                        utilidades.url_mto + "/" + idProducto;

                new enviarDatosServidor(this).execute(
                        jsonProducto.toString(),
                        accion.equals("nuevo") ? "POST" : "PUT",
                        url
                );
            }

            Toast.makeText(this, "Producto guardado", Toast.LENGTH_SHORT).show();
            abrirLista();
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