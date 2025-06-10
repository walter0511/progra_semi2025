package com.example.miprimeraaplicacion;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.google.firebase.database.DatabaseReference; // Importar
import com.google.firebase.database.FirebaseDatabase; // Importar

public class DB_vehiculos extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "vehiculos.db";
    private static final int DATABASE_VERSION = 2;  // Cambiar versión para forzar upgrade
    private DatabaseReference mDatabase; // Añade esta línea

    public DB_vehiculos(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        mDatabase = FirebaseDatabase.getInstance().getReference("vehiculos"); // Inicializa la referencia de la DB de Firebase
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE = "CREATE TABLE vehiculos (" +
                "tipo_placa TEXT," +
                "numero_placa TEXT PRIMARY KEY," +
                "codigo_ruta TEXT," +
                "placa_extranjera INTEGER," +
                "clase TEXT," +
                "marca TEXT," +
                "modelo TEXT," +
                "color TEXT" +
                ")";
        db.execSQL(CREATE_TABLE);

        // Inserciones de datos existentes (mantenlas para la DB local)
        insertInitialVehiculoData(db); // Refactoriza la inserción de datos iniciales
    }

    private void insertInitialVehiculoData(SQLiteDatabase db) {
        ContentValues v1 = new ContentValues();
        v1.put("tipo_placa", "Particular");
        v1.put("numero_placa", "P1234");
        v1.putNull("codigo_ruta");
        v1.put("placa_extranjera", 0);
        v1.put("clase", "Automóvil");
        v1.put("marca", "Toyota");
        v1.put("modelo", "2020");
        v1.put("color", "Rojo");
        db.insert("vehiculos", null, v1);
        uploadVehiculoToFirebase(v1); // Subir a Firebase

        ContentValues v2 = new ContentValues();
        v2.put("tipo_placa", "Transporte");
        v2.put("numero_placa", "AB1234");
        v2.put("codigo_ruta", "Ruta 15");
        v2.put("placa_extranjera", 0);
        v2.put("clase", "Bus");
        v2.put("marca", "Mercedes");
        v2.put("modelo", "2018");
        v2.put("color", "Azul");
        db.insert("vehiculos", null, v2);
        uploadVehiculoToFirebase(v2); // Subir a Firebase

        ContentValues v3 = new ContentValues();
        v3.put("tipo_placa", "Particular");
        v3.put("numero_placa", "X9999");
        v3.putNull("codigo_ruta");
        v3.put("placa_extranjera", 1);
        v3.put("clase", "Automóvil");
        v3.put("marca", "Honda");
        v3.put("modelo", "2019");
        v3.put("color", "Blanco");
        db.insert("vehiculos", null, v3);
        uploadVehiculoToFirebase(v3); // Subir a Firebase
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS vehiculos");
        onCreate(db);
    }

    public Cursor buscarPorPlaca(String numeroPlaca) {
        SQLiteDatabase db = this.getReadableDatabase();
        String placaUpper = numeroPlaca.toUpperCase();
        String query = "SELECT * FROM vehiculos WHERE UPPER(numero_placa) = ?";
        return db.rawQuery(query, new String[]{placaUpper});
    }

    // Nuevo método para subir un vehículo a Firebase
    public void uploadVehiculoToFirebase(ContentValues vehiculoData) {
        String numeroPlaca = vehiculoData.getAsString("numero_placa");
        if (numeroPlaca != null) {
            mDatabase.child(numeroPlaca).setValue(vehiculoData)
                    .addOnSuccessListener(aVoid -> {
                        // Opcional: Registrar éxito
                        System.out.println("Vehículo " + numeroPlaca + " subido a Firebase.");
                    })
                    .addOnFailureListener(e -> {
                        // Opcional: Registrar error
                        System.err.println("Error subiendo vehículo " + numeroPlaca + " a Firebase: " + e.getMessage());
                    });
        }
    }
}

