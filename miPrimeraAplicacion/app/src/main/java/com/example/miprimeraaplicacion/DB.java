package com.example.miprimeraaplicacion;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DB extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "walter";
    private static final int DATABASE_VERSION = 1;
    private static final String SQL_CREATE_TABLE =
            "CREATE TABLE productos (idProducto TEXT PRIMARY KEY, nombre TEXT, " +
                    "imagenUrl TEXT, precio REAL, costo REAL, stock INTEGER)";

    public DB(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS productos");
        onCreate(db);
    }

    public String administrar_productos(String accion, String[] datos) {
        SQLiteDatabase db = getWritableDatabase();
        try {
            switch (accion) {
                case "nuevo":
                    // Antes de insertar, verificar si existe
                    Cursor cursor = db.rawQuery("SELECT idProducto FROM productos WHERE idProducto = ?", new String[]{datos[0]});
                    if (cursor.moveToFirst()) {
                        // Ya existe: actualizar
                        db.execSQL("UPDATE productos SET nombre=?, imagenUrl=?, precio=?, costo=?, stock=? WHERE idProducto=?",
                                new String[]{datos[1], datos[2], datos[3], datos[4], datos[5], datos[0]});
                    } else {
                        // No existe: insertar
                        db.execSQL("INSERT INTO productos VALUES (?,?,?,?,?,?)", datos);
                    }
                    cursor.close();
                    break;
                case "modificar":
                    db.execSQL("UPDATE productos SET nombre=?, imagenUrl=?, precio=?, costo=?, stock=? WHERE idProducto=?",
                            new String[]{datos[1], datos[2], datos[3], datos[4], datos[5], datos[0]});
                    break;
                case "eliminar":
                    db.execSQL("DELETE FROM productos WHERE idProducto=?", new String[]{datos[0]});
                    break;
            }
            return "ok";
        } catch (Exception e) {
            return e.getMessage();
        } finally {
            db.close();
        }
    }

    public Cursor lista_productos() {
        return getReadableDatabase().rawQuery("SELECT * FROM productos", null);
    }
}