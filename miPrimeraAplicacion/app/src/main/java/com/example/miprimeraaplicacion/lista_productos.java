package com.example.miprimeraaplicacion;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class lista_productos extends AppCompatActivity {
    private ListView ltsProductos;
    private EditText txtBuscarProducto;
    private ArrayList<productos> alProductos = new ArrayList<>();
    private ArrayList<productos> alProductosCopia = new ArrayList<>();
    private AdaptadorProductos adapter;
    private DB db;
    private detectarInternet di;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_productos);

        txtBuscarProducto = findViewById(R.id.txtBuscarProducto);
        ltsProductos = findViewById(R.id.ltsProductos);
        FloatingActionButton fabAgregarProducto = findViewById(R.id.fabAgregarProducto);

        db = new DB(this);
        di = new detectarInternet(this);
        adapter = new AdaptadorProductos(this, alProductos);
        ltsProductos.setAdapter(adapter);

        // Botón de nuevo producto
        fabAgregarProducto.setOnClickListener(v -> {
            Intent intent = new Intent(lista_productos.this, MainActivity.class);
            intent.putExtra("accion", "nuevo");
            startActivity(intent);
        });

        // Click normal: modificar producto
        ltsProductos.setOnItemClickListener((parent, view, position, id) -> {
            productos productoSeleccionado = alProductos.get(position);
            Intent intent = new Intent(lista_productos.this, MainActivity.class);
            intent.putExtra("accion", "modificar");
            intent.putExtra("idProducto", productoSeleccionado.getIdProducto());
            startActivity(intent);
        });

        // Long click: eliminar producto
        ltsProductos.setOnItemLongClickListener((parent, view, position, id) -> {
            productos productoSeleccionado = alProductos.get(position);

            new androidx.appcompat.app.AlertDialog.Builder(lista_productos.this)
                    .setTitle("Eliminar Producto")
                    .setMessage("¿Estás seguro de eliminar el producto " + productoSeleccionado.getNombre() + "?")
                    .setPositiveButton("Sí", (dialog, which) -> {
                        eliminarProducto(productoSeleccionado.getIdProducto());
                    })
                    .setNegativeButton("No", null)
                    .show();

            return true;
        });

        buscarProductos();
        cargarProductos();
    }

    private void cargarProductos() {
        alProductos.clear();
        alProductosCopia.clear();

        if (di.hayConexionInternet()) {
            new obtenerDatosServidor(this).execute();
        } else {
            cargarProductosLocales();
        }
    }

    public void cargarProductosLocales() {
        try (Cursor cursor = db.lista_productos()) {
            if (cursor.moveToFirst()) {
                do {
                    productos producto = new productos(
                            cursor.getString(0),
                            cursor.getString(1),
                            cursor.getString(2),
                            cursor.getDouble(3),
                            cursor.getDouble(4),
                            cursor.getInt(5)
                    );
                    alProductos.add(producto);
                } while (cursor.moveToNext());
                alProductosCopia.addAll(alProductos);
                adapter.notifyDataSetChanged();
            } else {
                Toast.makeText(this, "No hay productos locales", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Error al cargar productos: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    public void procesarRespuestaServidor(String respuesta) {
        try {
            JSONObject jsonObject = new JSONObject(respuesta);
            JSONArray rows = jsonObject.getJSONArray("rows");

            alProductos.clear();
            for (int i = 0; i < rows.length(); i++) {
                JSONObject item = rows.getJSONObject(i).getJSONObject("value");
                productos producto = new productos(
                        item.getString("idProducto"),
                        item.getString("nombre"),
                        item.getString("imagenUrl"),
                        item.getDouble("precio"),
                        item.getDouble("costo"),
                        item.getInt("stock")
                );
                alProductos.add(producto);

                // Sincronizar con SQLite local
                String[] datos = {
                        producto.getIdProducto(),
                        producto.getNombre(),
                        producto.getImagenUrl(),
                        String.valueOf(producto.getPrecio()),
                        String.valueOf(producto.getCosto()),
                        String.valueOf(producto.getStock())
                };
                db.administrar_productos("nuevo", datos);
            }
            alProductosCopia.addAll(alProductos);
            adapter.notifyDataSetChanged();
        } catch (Exception e) {
            Toast.makeText(this, "Error al procesar respuesta: " + e.getMessage(), Toast.LENGTH_LONG).show();
            cargarProductosLocales();
        }
    }

    private void buscarProductos() {
        txtBuscarProducto.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String texto = s.toString().toLowerCase().trim();
                alProductos.clear();

                if (texto.isEmpty()) {
                    alProductos.addAll(alProductosCopia);
                } else {
                    for (productos p : alProductosCopia) {
                        if (p.getNombre().toLowerCase().contains(texto)) {
                            alProductos.add(p);
                        }
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void eliminarProducto(String idProducto) {
        String[] datos = {idProducto};
        String respuesta = db.administrar_productos("eliminar", datos);

        if ("ok".equals(respuesta)) {
            Toast.makeText(this, "Producto eliminado", Toast.LENGTH_SHORT).show();
            cargarProductos();
        } else {
            Toast.makeText(this, "Error al eliminar producto: " + respuesta, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        cargarProductos();
    }
}