package com.example.miprimeraaplicacion;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log; // Importar Log para depuración
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class ActivityLoginUsuario extends AppCompatActivity { // Considera renombrar esta clase a 'ConsultaUsuarioActivity'

    private static final String TAG = "ActivityLoginUsuario"; // Para mensajes de Log

    // Declaración de variables de la UI
    private EditText editTextConsulta;
    private RadioGroup radioGroupConsulta;
    private RadioButton radioLicencia, radioPlaca;
    private Button btnLoginUsuario; // Coincide con el ID en el XML
    private ImageButton btnRegresar;
    private ProgressBar progressBar;
    private TextView tvNoResults;
    private RecyclerView recyclerViewMultas;

    // Variables para el RecyclerView
    private MultaAdapter multaAdapter;
    private List<Map<String, Object>> multasList;

    // Referencia a Firebase Realtime Database
    private DatabaseReference mDatabase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_usuario);

        // 1. Obtener referencias a los elementos de la UI
        editTextConsulta = findViewById(R.id.editTextConsulta);
        radioGroupConsulta = findViewById(R.id.radioGroupConsulta);
        radioLicencia = findViewById(R.id.radioLicencia);
        radioPlaca = findViewById(R.id.radioPlaca);
        btnLoginUsuario = findViewById(R.id.btnLoginUsuario); // ¡CORRECTO! Coincide con el XML
        btnRegresar = findViewById(R.id.btnRegresar);
        progressBar = findViewById(R.id.progressBar);
        tvNoResults = findViewById(R.id.tvNoResults);
        recyclerViewMultas = findViewById(R.id.recyclerViewMultas);

        // 2. Inicializar Firebase Realtime Database
        mDatabase = FirebaseDatabase.getInstance().getReference("multas"); // Asegúrate de que "multas" es el nodo raíz de tus multas

        // 3. Configurar RecyclerView
        multasList = new ArrayList<>();
        multaAdapter = new MultaAdapter(multasList);
        recyclerViewMultas.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewMultas.setAdapter(multaAdapter);

        // 4. Configurar listeners de eventos
        btnLoginUsuario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                consultarMultas();
            }
        });

        btnRegresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed(); // Regresar a la actividad anterior
            }
        });

        // Configurar el tipo de teclado y hint según la selección de RadioButton
        radioGroupConsulta.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.radioLicencia) {
                    editTextConsulta.setHint("Ingrese número de licencia");
                    // InputType para texto con mayúsculas, sin sugerencias de autocorrección
                    editTextConsulta.setInputType(android.text.InputType.TYPE_CLASS_TEXT |
                            android.text.InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS |
                            android.text.InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
                } else if (checkedId == R.id.radioPlaca) {
                    editTextConsulta.setHint("Ingrese número de placa");
                    // InputType para texto con mayúsculas, sin sugerencias de autocorrección
                    editTextConsulta.setInputType(android.text.InputType.TYPE_CLASS_TEXT |
                            android.text.InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS |
                            android.text.InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
                }
                editTextConsulta.setText(""); // Limpiar el campo al cambiar el tipo de búsqueda
            }
        });

        // Establecer el tipo de input inicial al cargar la actividad (por defecto, Licencia)
        editTextConsulta.setInputType(android.text.InputType.TYPE_CLASS_TEXT |
                android.text.InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS |
                android.text.InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
    }

    /**
     * Método para consultar las multas en Firebase Realtime Database
     * según el número de licencia o placa ingresado.
     */
    private void consultarMultas() {
        String searchValue = editTextConsulta.getText().toString().trim();
        if (searchValue.isEmpty()) {
            editTextConsulta.setError("Este campo no puede estar vacío.");
            return;
        }

        // Mostrar ProgressBar y limpiar resultados anteriores
        progressBar.setVisibility(View.VISIBLE);
        tvNoResults.setVisibility(View.GONE);
        multasList.clear();
        multaAdapter.notifyDataSetChanged();

        String fieldToQuery; // Campo en Firebase por el cual se realizará la consulta
        if (radioLicencia.isChecked()) {
            fieldToQuery = "conductor_licencia"; // Asegúrate de que este sea el nombre exacto del campo en Firebase
            Log.d(TAG, "Consultando por Licencia: " + searchValue);
        } else { // radioPlaca.isChecked()
            fieldToQuery = "vehiculo_numero_placa"; // Asegúrate de que este sea el nombre exacto del campo en Firebase
            Log.d(TAG, "Consultando por Placa: " + searchValue);
        }

        // Realiza la consulta en Firebase Realtime Database
        Query query = mDatabase.orderByChild(fieldToQuery).equalTo(searchValue);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                progressBar.setVisibility(View.GONE); // Ocultar ProgressBar

                if (snapshot.exists()) {
                    for (DataSnapshot multaSnapshot : snapshot.getChildren()) {
                        // Firebase Realtime Database devuelve los datos como Map<String, Object>
                        Map<String, Object> multa = (Map<String, Object>) multaSnapshot.getValue();
                        if (multa != null) {
                            multasList.add(multa);
                            Log.d(TAG, "Multa encontrada: " + multa.get("fecha_hora"));
                        }
                    }
                    // Opcional: Ordenar multas por fecha (del más reciente al más antiguo)
                    Collections.sort(multasList, new Comparator<Map<String, Object>>() {
                        @Override
                        public int compare(Map<String, Object> m1, Map<String, Object> m2) {
                            String fechaHora1 = (String) m1.get("fecha_hora");
                            String fechaHora2 = (String) m2.get("fecha_hora");

                            // Manejo de casos donde la fecha sea nula o no un String
                            if (fechaHora1 == null && fechaHora2 == null) return 0;
                            if (fechaHora1 == null) return 1; // nulo va al final
                            if (fechaHora2 == null) return -1; // nulo va al final

                            // Asumiendo formato de fecha y hora que permita comparación lexicográfica directa
                            // Ejemplo: "yyyy-MM-dd HH:mm:ss"
                            return fechaHora2.compareTo(fechaHora1); // Del más reciente al más antiguo
                        }
                    });
                    multaAdapter.notifyDataSetChanged(); // Notificar al adaptador que hay nuevos datos
                } else {
                    tvNoResults.setVisibility(View.VISIBLE); // Mostrar mensaje de "no resultados"
                    Log.d(TAG, "No se encontraron multas para: " + searchValue);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressBar.setVisibility(View.GONE); // Ocultar ProgressBar
                Toast.makeText(ActivityLoginUsuario.this, "Error al consultar: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                tvNoResults.setVisibility(View.VISIBLE); // Mostrar mensaje de "no resultados"
                Log.e(TAG, "Error en la consulta de Firebase: " + error.getMessage(), error.toException());
            }
        });
    }

    /**
     * Adaptador para el RecyclerView que muestra las multas.
     * Utiliza un Map<String, Object> para representar cada multa.
     */
    private class MultaAdapter extends RecyclerView.Adapter<MultaAdapter.MultaViewHolder> {

        private List<Map<String, Object>> multas;

        public MultaAdapter(List<Map<String, Object>> multas) {
            this.multas = multas;
        }

        @NonNull
        @Override
        public MultaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_multa_historial, parent, false);
            return new MultaViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull MultaViewHolder holder, int position) {
            Map<String, Object> multa = multas.get(position);

            // Asegúrate de que los nombres de los campos (claves del Map) coincidan con los que usas en Firebase
            holder.tvMultaFecha.setText("Fecha: " + multa.get("fecha_hora"));
            holder.tvMultaTipo.setText("Tipo: " + multa.get("tipo_infraccion"));

            // Manejo del monto que puede ser Double, Long o String en Firebase
            Object montoObj = multa.get("monto");
            if (montoObj instanceof Number) {
                holder.tvMultaMonto.setText("Monto: $" + String.format("%.2f", ((Number) montoObj).doubleValue()));
            } else if (montoObj != null) {
                try {
                    holder.tvMultaMonto.setText("Monto: $" + String.format("%.2f", Double.parseDouble(montoObj.toString())));
                } catch (NumberFormatException e) {
                    holder.tvMultaMonto.setText("Monto: N/D (Formato incorrecto)");
                    Log.e(TAG, "Error al parsear monto: " + montoObj, e);
                }
            } else {
                holder.tvMultaMonto.setText("Monto: N/D");
            }

            // Obtener la URL de descarga del PDF desde los datos de la multa
            String pdfUrl = (String) multa.get("pdf_download_url");
            if (pdfUrl != null && !pdfUrl.isEmpty()) {
                holder.btnDescargarPdf.setVisibility(View.VISIBLE);
                holder.btnDescargarPdf.setOnClickListener(v -> {
                    try {
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse(pdfUrl));
                        // Asegurarse de que el dispositivo tenga una aplicación para abrir PDFs
                        if (intent.resolveActivity(holder.itemView.getContext().getPackageManager()) != null) {
                            holder.itemView.getContext().startActivity(intent);
                        } else {
                            Toast.makeText(holder.itemView.getContext(), "No se encontró una aplicación para abrir PDFs.", Toast.LENGTH_LONG).show();
                            Log.w(TAG, "No hay aplicación disponible para abrir URL: " + pdfUrl);
                        }
                    } catch (Exception e) {
                        Toast.makeText(holder.itemView.getContext(), "Error al abrir el PDF.", Toast.LENGTH_LONG).show();
                        Log.e(TAG, "Error al intentar abrir URL del PDF: " + pdfUrl, e);
                    }
                });
            } else {
                holder.btnDescargarPdf.setVisibility(View.GONE);
                Log.d(TAG, "URL de PDF no disponible para esta multa: " + multa.get("fecha_hora"));
            }
        }

        @Override
        public int getItemCount() {
            return multas.size();
        }

        public class MultaViewHolder extends RecyclerView.ViewHolder {
            TextView tvMultaFecha, tvMultaTipo, tvMultaMonto;
            Button btnDescargarPdf;

            public MultaViewHolder(@NonNull View itemView) {
                super(itemView);
                tvMultaFecha = itemView.findViewById(R.id.tvMultaFecha);
                tvMultaTipo = itemView.findViewById(R.id.tvMultaTipo);
                tvMultaMonto = itemView.findViewById(R.id.tvMultaMonto);
                btnDescargarPdf = itemView.findViewById(R.id.btnDescargarPdf);
            }
        }
    }
}
