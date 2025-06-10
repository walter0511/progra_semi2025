package com.example.miprimeraaplicacion;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DB_agentes extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "agentes_pnc.db";
    private static final int DATABASE_VERSION = 1;

    // Tabla de agentes
    private static final String TABLE_AGENTES = "agentes";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_USUARIO = "usuario";
    private static final String COLUMN_CONTRASENA = "contrasena";
    private static final String COLUMN_ONI = "oni";
    private static final String COLUMN_PUESTO = "puesto_policial";
    private static final String COLUMN_ES_BA = "es_ba";
    private static final String COLUMN_ES_EJ = "es_ej";
    private static final String COLUMN_ES_YSU = "es_ysu";

    private static final String CREATE_TABLE_AGENTES =
            "CREATE TABLE " + TABLE_AGENTES + "(" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    COLUMN_USUARIO + " TEXT UNIQUE NOT NULL," +
                    COLUMN_CONTRASENA + " TEXT NOT NULL," +
                    COLUMN_ONI + " TEXT," +
                    COLUMN_PUESTO + " TEXT," +
                    COLUMN_ES_BA + " INTEGER," +
                    COLUMN_ES_EJ + " INTEGER," +
                    COLUMN_ES_YSU + " INTEGER" +
                    ")";

    public DB_agentes(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_AGENTES);

        insertarAgente(db, "javier", "javier123", "123456", "San Salvador", 1, 0, 1);
        insertarAgente(db, "walter", "walter123", "789101", "Soyapango", 0, 1, 0);
        insertarAgente(db, "yomi", "yomi123", "112233", "Santa Ana", 1, 1, 0);
        insertarAgente(db, "steven", "steven123", "445566", "Usulután", 0, 0, 1);
    }

    private void insertarAgente(SQLiteDatabase db, String usuario, String contrasena,
                                String oni, String puesto, int es_ba, int es_ej, int es_ysu) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_USUARIO, usuario);
        values.put(COLUMN_CONTRASENA, contrasena);
        values.put(COLUMN_ONI, oni);
        values.put(COLUMN_PUESTO, puesto);
        values.put(COLUMN_ES_BA, es_ba);
        values.put(COLUMN_ES_EJ, es_ej);
        values.put(COLUMN_ES_YSU, es_ysu);
        db.insert(TABLE_AGENTES, null, values);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_AGENTES);
        onCreate(db);
    }

    public boolean verificarAgente(String usuario, String contrasena) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columnas = {COLUMN_ID};
        String seleccion = COLUMN_USUARIO + " = ? AND " + COLUMN_CONTRASENA + " = ?";
        String[] args = {usuario, contrasena};

        Cursor cursor = db.query(TABLE_AGENTES, columnas, seleccion, args, null, null, null);
        boolean existe = cursor.getCount() > 0;
        cursor.close();
        db.close();
        return existe;
    }

    // Obtener datos de un agente por su usuario
    public Cursor obtenerDatosAgente(String usuario) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(TABLE_AGENTES, null, COLUMN_USUARIO + " = ?", new String[]{usuario}, null, null, null);
    }
}
