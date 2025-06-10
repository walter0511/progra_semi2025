package com.example.miprimeraaplicacion;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.google.firebase.database.DatabaseReference; // Importar
import com.google.firebase.database.FirebaseDatabase; // Importar

public class DB_conductores extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "conductores.db";
    private static final int DATABASE_VERSION = 1;
    private DatabaseReference mDatabase; // Añade esta línea

    public DB_conductores(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        mDatabase = FirebaseDatabase.getInstance().getReference("conductores"); // Inicializa la referencia de la DB de Firebase
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE = "CREATE TABLE conductores (" +
                "licencia TEXT PRIMARY KEY," +
                "apellido1 TEXT," +
                "apellido2 TEXT," +
                "nombre1 TEXT," +
                "nombre2 TEXT," +
                "clase TEXT" +
                ")";
        db.execSQL(CREATE_TABLE);

        // Inserciones de datos existentes (mantenlas para la DB local)
        insertInitialConductorData(db); // Refactoriza la inserción de datos iniciales
    }

    private void insertInitialConductorData(SQLiteDatabase db) {
        ContentValues values = new ContentValues();
        values.put("licencia", "A12345678");
        values.put("apellido1", "Gómez");
        values.put("apellido2", "Martínez");
        values.put("nombre1", "Carlos");
        values.put("nombre2", "Alberto");
        values.put("clase", "Particular");
        db.insert("conductores", null, values);
        uploadConductorToFirebase(values); // Subir a Firebase

        ContentValues values2 = new ContentValues();
        values2.put("licencia", "B98765432");
        values2.put("apellido1", "Ramírez");
        values2.put("apellido2", "López");
        values2.put("nombre1", "Andrea");
        values2.put("nombre2", "Sofía");
        values2.put("clase", "Liviana");
        db.insert("conductores", null, values2);
        uploadConductorToFirebase(values2); // Subir a Firebase

        ContentValues values3 = new ContentValues();
        values3.put("licencia", "C24681357");
        values3.put("apellido1", "Torres");
        values3.put("apellido2", "Herrera");
        values3.put("nombre1", "Luis");
        values3.put("nombre2", "Miguel");
        values3.put("clase", "Motocicleta");
        db.insert("conductores", null, values3);
        uploadConductorToFirebase(values3); // Subir a Firebase

        ContentValues values4 = new ContentValues();
        values4.put("licencia", "D13579246");
        values4.put("apellido1", "Díaz");
        values4.put("apellido2", "Morales");
        values4.put("nombre1", "Mariana");
        values4.put("nombre2", "Isabel");
        values4.put("clase", "Juvenil");
        db.insert("conductores", null, values4);
        uploadConductorToFirebase(values4); // Subir a Firebase

        ContentValues values5 = new ContentValues();
        values5.put("licencia", "E11223344");
        values5.put("apellido1", "Castillo");
        values5.put("apellido2", "Méndez");
        values5.put("nombre1", "Kevin");
        values5.put("nombre2", "Josué");
        values5.put("clase", "Pesada-L");
        db.insert("conductores", null, values5);
        uploadConductorToFirebase(values5); // Subir a Firebase

        ContentValues values6 = new ContentValues();
        values6.put("licencia", "F55667788");
        values6.put("apellido1", "Chávez");
        values6.put("apellido2", "Rivas");
        values6.put("nombre1", "Paola");
        values6.put("nombre2", "Fernanda");
        values6.put("clase", "Extranjera");
        db.insert("conductores", null, values6);
        uploadConductorToFirebase(values6); // Subir a Firebase
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS conductores");
        onCreate(db);
    }

    public Cursor buscarPorLicencia(String licencia) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query("conductores", null, "licencia = ?", new String[]{licencia}, null, null, null);
    }

    // Nuevo método para subir un conductor a Firebase
    public void uploadConductorToFirebase(ContentValues conductorData) {
        String licencia = conductorData.getAsString("licencia");
        if (licencia != null) {
            mDatabase.child(licencia).setValue(conductorData)
                    .addOnSuccessListener(aVoid -> {
                        // Opcional: Registrar éxito
                        System.out.println("Conductor " + licencia + " subido a Firebase.");
                    })
                    .addOnFailureListener(e -> {
                        // Opcional: Registrar error
                        System.err.println("Error subiendo conductor " + licencia + " a Firebase: " + e.getMessage());
                    });
        }
    }
}

