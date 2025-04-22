package com.example.miprimeraaplicacion;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DB extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "walter";
    private static final int DATABASE_VERSION = 1;


    private static final String SQLdb = "CREATE TABLE steven  (" +
            "idproducto TEXT PRIMARY KEY, " +
            "nombre TEXT, " +
            "precio TEXT, " +
            "costo TEXT, " +
            "ganancias TEXT, " +
            "urlFoto TEXT)";

    public DB(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQLdb);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Aquí puedes manejar las actualizaciones de base de datos si es necesario
        db.execSQL("DROP TABLE IF EXISTS ListaProductos");
        onCreate(db);
    }

    public String administrar_productos(String accion, String[] datos) {
        SQLiteDatabase db = null;
        try {
            db = this.getWritableDatabase();
            String mensaje = "ok";
            ContentValues values = new ContentValues();

            switch (accion) {
                case "nuevo":
                    values.put("idproducto", datos[0]);
                    values.put("nombre", datos[1]);
                    values.put("precio", datos[2]);
                    values.put("costo", datos[3]);
                    values.put("ganancias", datos[4]);
                    values.put("urlFoto", datos[5]);
                    db.insertOrThrow("steven", null, values);
                    break;
                case "modificar":
                    values.put("nombre", datos[1]);
                    values.put("precio", datos[2]);
                    values.put("costo", datos[3]);
                    values.put("ganancias", datos[4]);
                    values.put("urlFoto", datos[5]);
                    db.update("steven", values, "idproducto = ?", new String[]{datos[0]});
                    break;
                case "eliminar":
                    db.delete("steven", "idproducto = ?", new String[]{datos[0]});
                    break;
                default:
                    return "Acción no reconocida";
            }
            db.setTransactionSuccessful();
            return mensaje;
        } catch (SQLException e) {
            return "Error: " + e.getMessage();
        } finally {
            if (db != null) {
                db.endTransaction();
                db.close();
            }
        }
    }

    public Cursor lista_steven() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM steven", null);
    }
}