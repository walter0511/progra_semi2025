package com.example.miprimeraaplicacion;

import android.content.ContentValues;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log; // Agrega esta importación para Logcat

// IMPORTACIONES DE FIREBASE REALTIME DATABASE (si eliges usarla aquí)
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

// IMPORTACIONES PARA HashMap y Map
import java.util.HashMap;
import java.util.Map;

public class DB_multas extends SQLiteOpenHelper {

    private static final String TAG = "DB_multas_SQLite"; // TAG para Logcat

    private static final String DATABASE_NAME = "multas_app.db";
    // *** CAMBIO CLAVE 1: INCREMENTAR LA VERSIÓN DE LA BASE DE DATOS ***
    // Esto es NECESARIO para que onUpgrade se ejecute y añada las nuevas columnas.
    // Hemos pasado de 2 a 3 porque ahora estamos añadiendo más columnas.
    private static final int DATABASE_VERSION = 3; // <<<< CAMBIADO DE 2 A 3

    // Nombre de la tabla
    private static final String TABLE_MULTAS = "multas";

    // Columnas de la tabla multas
    private static final String COLUMN_ID = "id_multa";
    private static final String COLUMN_FECHA_HORA = "fecha_hora"; // Formato TEXT (timestamp o string)
    private static final String COLUMN_TIPO_INFRACCION = "tipo_infraccion";
    private static final String COLUMN_MONTO = "monto";
    private static final String COLUMN_UBICACION_DEPARTAMENTO = "ubicacion_departamento";
    private static final String COLUMN_UBICACION_MUNICIPIO = "ubicacion_municipio";
    private static final String COLUMN_UBICACION_CALLE = "ubicacion_calle";
    private static final String COLUMN_EVIDENCIA_FOTOS_PATHS = "evidencia_fotos_paths";

    // Datos del Agente (referencia o copia de datos importantes)
    private static final String COLUMN_AGENTE_USUARIO = "agente_usuario";
    private static final String COLUMN_AGENTE_ONI = "agente_oni";
    private static final String COLUMN_AGENTE_PUESTO = "agente_puesto";

    // Datos del Conductor
    private static final String COLUMN_CONDUCTOR_LICENCIA = "conductor_licencia";
    private static final String COLUMN_CONDUCTOR_APELLIDO1 = "conductor_apellido1";
    private static final String COLUMN_CONDUCTOR_APELLIDO2 = "conductor_apellido2";
    private static final String COLUMN_CONDUCTOR_APELLIDO3 = "conductor_apellido3";
    private static final String COLUMN_CONDUCTOR_NOMBRE1 = "conductor_nombre1";
    private static final String COLUMN_CONDUCTOR_NOMBRE2 = "conductor_nombre2";
    private static final String COLUMN_CONDUCTOR_NOMBRE3 = "conductor_nombre3";
    private static final String COLUMN_CONDUCTOR_CLASE_LICENCIA = "conductor_clase_licencia";

    // Datos del Vehículo
    private static final String COLUMN_VEHICULO_TIPO_PLACA = "vehiculo_tipo_placa";
    private static final String COLUMN_VEHICULO_NUMERO_PLACA = "vehiculo_numero_placa";
    private static final String COLUMN_VEHICULO_CODIGO_RUTA = "vehiculo_codigo_ruta";
    private static final String COLUMN_VEHICULO_PLACA_EXTRANJERA = "vehiculo_placa_extranjera";
    private static final String COLUMN_VEHICULO_CLASE = "vehiculo_clase";
    private static final String COLUMN_VEHICULO_MARCA = "vehiculo_marca";
    private static final String COLUMN_VEHICULO_MODELO = "vehiculo_modelo";
    private static final String COLUMN_VEHICULO_COLOR = "vehiculo_color";

    // Columnas para los datos de la Falta
    private static final String COLUMN_FALTA_CODIGO = "falta_codigo";
    private static final String COLUMN_FALTA_CLASIFICACION = "falta_clasificacion";
    private static final String COLUMN_OBSERVACIONES = "observaciones";

    // *** NUEVAS CONSTANTES PARA LOS DECOMISOS ***
    private static final String COLUMN_DECOMISO_VEHICULOS = "decomiso_vehiculos";
    private static final String COLUMN_DECOMISO_TARJETA_CIRCULACION = "decomiso_tarjeta_circulacion";
    private static final String COLUMN_DECOMISO_LICENCIA = "decomiso_licencia";
    private static final String COLUMN_DECOMISO_PLACAS = "decomiso_placas";
    private static final String COLUMN_DECOMISO_POLIZA = "decomiso_poliza";
    private static final String COLUMN_DECOMISO_PERMISOS_LINEA = "decomiso_permisos_linea";

    // *** NUEVAS CONSTANTES PARA OTROS ***
    private static final String COLUMN_OTROS_CONDUCTOR_AUSENTE = "otros_conductor_ausente";
    private static final String COLUMN_OTROS_SE_NEGO_FIRMAR = "otros_se_nego_firmar";
    private static final String COLUMN_OTROS_APARATO_LASER = "otros_aparato_laser";
    private static final String COLUMN_OTROS_PRUEBA_ALCOTEST = "otros_prueba_alcotest";
    private static final String COLUMN_OTROS_DESTRUYO_ESQUELA = "otros_destruyo_esquela";
    private static final String COLUMN_OTROS_VEHICULO_REMOLCADO = "otros_vehiculo_remolcado";


    // Sentencia SQL para crear la tabla de multas (con todas las columnas)
    // Es importante que esta cadena de CREATE TABLE contenga TODAS las columnas definidas arriba.
    private static final String CREATE_TABLE_MULTAS_SQL =
            "CREATE TABLE " + TABLE_MULTAS + "(" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    COLUMN_FECHA_HORA + " TEXT NOT NULL," +
                    COLUMN_TIPO_INFRACCION + " TEXT NOT NULL," +
                    COLUMN_MONTO + " REAL NOT NULL," +
                    COLUMN_UBICACION_DEPARTAMENTO + " TEXT," +
                    COLUMN_UBICACION_MUNICIPIO + " TEXT," +
                    COLUMN_UBICACION_CALLE + " TEXT," +
                    COLUMN_EVIDENCIA_FOTOS_PATHS + " TEXT," +
                    COLUMN_AGENTE_USUARIO + " TEXT NOT NULL," +
                    COLUMN_AGENTE_ONI + " TEXT," +
                    COLUMN_AGENTE_PUESTO + " TEXT," +
                    COLUMN_CONDUCTOR_LICENCIA + " TEXT," +
                    COLUMN_CONDUCTOR_APELLIDO1 + " TEXT," +
                    COLUMN_CONDUCTOR_APELLIDO2 + " TEXT," +
                    COLUMN_CONDUCTOR_APELLIDO3 + " TEXT," +
                    COLUMN_CONDUCTOR_NOMBRE1 + " TEXT," +
                    COLUMN_CONDUCTOR_NOMBRE2 + " TEXT," +
                    COLUMN_CONDUCTOR_NOMBRE3 + " TEXT," +
                    COLUMN_CONDUCTOR_CLASE_LICENCIA + " TEXT," +
                    COLUMN_VEHICULO_TIPO_PLACA + " TEXT," +
                    COLUMN_VEHICULO_NUMERO_PLACA + " TEXT," +
                    COLUMN_VEHICULO_CODIGO_RUTA + " TEXT," +
                    COLUMN_VEHICULO_PLACA_EXTRANJERA + " INTEGER," +
                    COLUMN_VEHICULO_CLASE + " TEXT," +
                    COLUMN_VEHICULO_MARCA + " TEXT," +
                    COLUMN_VEHICULO_MODELO + " TEXT," +
                    COLUMN_VEHICULO_COLOR + " TEXT," +
                    COLUMN_FALTA_CODIGO + " TEXT," +
                    COLUMN_FALTA_CLASIFICACION + " TEXT," +
                    COLUMN_OBSERVACIONES + " TEXT," +
                    COLUMN_DECOMISO_VEHICULOS + " INTEGER DEFAULT 0," +
                    COLUMN_DECOMISO_TARJETA_CIRCULACION + " INTEGER DEFAULT 0," +
                    COLUMN_DECOMISO_LICENCIA + " INTEGER DEFAULT 0," +
                    COLUMN_DECOMISO_PLACAS + " INTEGER DEFAULT 0," +
                    COLUMN_DECOMISO_POLIZA + " INTEGER DEFAULT 0," +
                    COLUMN_DECOMISO_PERMISOS_LINEA + " INTEGER DEFAULT 0," +
                    COLUMN_OTROS_CONDUCTOR_AUSENTE + " INTEGER DEFAULT 0," +
                    COLUMN_OTROS_SE_NEGO_FIRMAR + " INTEGER DEFAULT 0," +
                    COLUMN_OTROS_APARATO_LASER + " INTEGER DEFAULT 0," +
                    COLUMN_OTROS_PRUEBA_ALCOTEST + " INTEGER DEFAULT 0," +
                    COLUMN_OTROS_DESTRUYO_ESQUELA + " INTEGER DEFAULT 0," +
                    COLUMN_OTROS_VEHICULO_REMOLCADO + " INTEGER DEFAULT 0" +
                    ")";

    // *** DECLARACIÓN E INICIALIZACIÓN DE FIREBASE REALTIME DATABASE ***
    // Si decides NO usar Realtime Database en esta clase, ELIMINA estas líneas.
    private DatabaseReference mDatabase;

    public DB_multas(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        // Inicializa la referencia de la DB de Firebase
        // Esto solo es necesario si mantienes la lógica de Realtime Database aquí.
        mDatabase = FirebaseDatabase.getInstance().getReference("multas");
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_MULTAS_SQL); // Usa la constante con el nombre completo
        Log.d(TAG, "Tabla de multas creada en SQLite.");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(TAG, "Actualizando la base de datos de la versión " + oldVersion + " a " + newVersion + ", lo que destruirá todos los datos antiguos para recrear.");
        // Una estrategia común para migraciones simples o cuando no te importa perder datos
        // es simplemente eliminar la tabla y volver a crearla.
        // ADVERTENCIA: Esto borrará todos los datos existentes. Si necesitas conservar datos,
        // tendrías que implementar sentencias ALTER TABLE para añadir las columnas una por una.
        // Dado que has incrementado la versión a 3 y has añadido los ALTER TABLE, vamos a usarlos.

        // Primero, asegurémonos de que las columnas anteriores se hayan añadido si la versión es < 2
        if (oldVersion < 2) {
            Log.d(TAG, "Realizando upgrade de DB_multas de versión " + oldVersion + " a 2.");
            db.execSQL("ALTER TABLE " + TABLE_MULTAS + " ADD COLUMN " + COLUMN_FALTA_CODIGO + " TEXT");
            db.execSQL("ALTER TABLE " + TABLE_MULTAS + " ADD COLUMN " + COLUMN_FALTA_CLASIFICACION + " TEXT");
            db.execSQL("ALTER TABLE " + TABLE_MULTAS + " ADD COLUMN " + COLUMN_OBSERVACIONES + " TEXT");
        }
        // Luego, si la versión antigua es menor que 3, añade las nuevas columnas
        if (oldVersion < 3) { // <<< NUEVO BLOQUE para las columnas de decomisos y otros
            Log.d(TAG, "Realizando upgrade de DB_multas de versión " + oldVersion + " a 3. Añadiendo nuevas columnas.");
            db.execSQL("ALTER TABLE " + TABLE_MULTAS + " ADD COLUMN " + COLUMN_DECOMISO_VEHICULOS + " INTEGER DEFAULT 0");
            db.execSQL("ALTER TABLE " + TABLE_MULTAS + " ADD COLUMN " + COLUMN_DECOMISO_TARJETA_CIRCULACION + " INTEGER DEFAULT 0");
            db.execSQL("ALTER TABLE " + TABLE_MULTAS + " ADD COLUMN " + COLUMN_DECOMISO_LICENCIA + " INTEGER DEFAULT 0");
            db.execSQL("ALTER TABLE " + TABLE_MULTAS + " ADD COLUMN " + COLUMN_DECOMISO_PLACAS + " INTEGER DEFAULT 0");
            db.execSQL("ALTER TABLE " + TABLE_MULTAS + " ADD COLUMN " + COLUMN_DECOMISO_POLIZA + " INTEGER DEFAULT 0");
            db.execSQL("ALTER TABLE " + TABLE_MULTAS + " ADD COLUMN " + COLUMN_DECOMISO_PERMISOS_LINEA + " INTEGER DEFAULT 0");

            db.execSQL("ALTER TABLE " + TABLE_MULTAS + " ADD COLUMN " + COLUMN_OTROS_CONDUCTOR_AUSENTE + " INTEGER DEFAULT 0");
            db.execSQL("ALTER TABLE " + TABLE_MULTAS + " ADD COLUMN " + COLUMN_OTROS_SE_NEGO_FIRMAR + " INTEGER DEFAULT 0");
            db.execSQL("ALTER TABLE " + TABLE_MULTAS + " ADD COLUMN " + COLUMN_OTROS_APARATO_LASER + " INTEGER DEFAULT 0");
            db.execSQL("ALTER TABLE " + TABLE_MULTAS + " ADD COLUMN " + COLUMN_OTROS_PRUEBA_ALCOTEST + " INTEGER DEFAULT 0");
            db.execSQL("ALTER TABLE " + TABLE_MULTAS + " ADD COLUMN " + COLUMN_OTROS_DESTRUYO_ESQUELA + " INTEGER DEFAULT 0");
            db.execSQL("ALTER TABLE " + TABLE_MULTAS + " ADD COLUMN " + COLUMN_OTROS_VEHICULO_REMOLCADO + " INTEGER DEFAULT 0");
        }
        // Si hay un salto mayor de versión, o si simplemente quieres reiniciar la tabla si la actualización es compleja
        // Puedes descomentar las siguientes dos líneas, pero ten en cuenta que BORRARÁ TODOS LOS DATOS.
        // db.execSQL("DROP TABLE IF EXISTS " + TABLE_MULTAS);
        // onCreate(db);
    }

    /**
     * Inserta una nueva multa en la base de datos con todos los detalles.
     * @return El ID de la nueva fila insertada, o -1 si hubo un error.
     */
    public long insertarMulta(
            String usuarioLogueado, String agenteOni, String agentePuesto,
            String conductorLicencia, String conductorApellido1, String conductorApellido2, String conductorApellido3,
            String conductorNombre1, String conductorNombre2, String conductorNombre3, String conductorClaseLicencia,
            String vehiculoTipoPlaca, String vehiculoNumeroPlaca, String vehiculoCodigoRuta, int vehiculoPlacaExtranjera,
            String vehiculoClase, String vehiculoMarca, String vehiculoModelo, String vehiculoColor,
            String fechaHoraInfraccion, String fotosEvidenciaPaths, String multaMonto, String multaCategoria,
            String ubicacionDepartamento, String ubicacionMunicipio, String ubicacionCalle,
            String faltaCodigo, String faltaClasificacion, String observaciones,
            // *** NUEVOS PARÁMETROS PARA DECOMISOS Y OTROS (booleanos) ***
            boolean decomisoVehiculos, boolean decomisoTarjetaCirculacion, boolean decomisoLicencia,
            boolean decomisoPlacas, boolean decomisoPoliza, boolean decomisoPermisosLinea,
            boolean otrosConductorAusente, boolean otrosSeNegoFirmar, boolean otrosAparatoLaser,
            boolean otrosPruebaAlcotest, boolean otrosDestruyoEsquela, boolean otrosVehiculoRemolcado) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        // Datos de la Multa principal
        values.put(COLUMN_FECHA_HORA, fechaHoraInfraccion);
        values.put(COLUMN_TIPO_INFRACCION, multaCategoria);
        values.put(COLUMN_MONTO, Double.parseDouble(multaMonto));

        // Datos de Ubicación
        values.put(COLUMN_UBICACION_DEPARTAMENTO, ubicacionDepartamento);
        values.put(COLUMN_UBICACION_MUNICIPIO, ubicacionMunicipio);
        values.put(COLUMN_UBICACION_CALLE, ubicacionCalle);
        values.put(COLUMN_EVIDENCIA_FOTOS_PATHS, fotosEvidenciaPaths);

        // Datos del Agente
        values.put(COLUMN_AGENTE_USUARIO, usuarioLogueado);
        values.put(COLUMN_AGENTE_ONI, agenteOni);
        values.put(COLUMN_AGENTE_PUESTO, agentePuesto);

        // Datos del Conductor
        values.put(COLUMN_CONDUCTOR_LICENCIA, conductorLicencia);
        values.put(COLUMN_CONDUCTOR_APELLIDO1, conductorApellido1);
        values.put(COLUMN_CONDUCTOR_APELLIDO2, conductorApellido2);
        values.put(COLUMN_CONDUCTOR_APELLIDO3, conductorApellido3);
        values.put(COLUMN_CONDUCTOR_NOMBRE1, conductorNombre1);
        values.put(COLUMN_CONDUCTOR_NOMBRE2, conductorNombre2);
        values.put(COLUMN_CONDUCTOR_NOMBRE3, conductorNombre3);
        values.put(COLUMN_CONDUCTOR_CLASE_LICENCIA, conductorClaseLicencia);

        // Datos del Vehículo
        values.put(COLUMN_VEHICULO_TIPO_PLACA, vehiculoTipoPlaca);
        values.put(COLUMN_VEHICULO_NUMERO_PLACA, vehiculoNumeroPlaca);
        values.put(COLUMN_VEHICULO_CODIGO_RUTA, vehiculoCodigoRuta);
        values.put(COLUMN_VEHICULO_PLACA_EXTRANJERA, vehiculoPlacaExtranjera);
        values.put(COLUMN_VEHICULO_CLASE, vehiculoClase);
        values.put(COLUMN_VEHICULO_MARCA, vehiculoMarca);
        values.put(COLUMN_VEHICULO_MODELO, vehiculoModelo);
        values.put(COLUMN_VEHICULO_COLOR, vehiculoColor);

        // Datos de la Falta
        values.put(COLUMN_FALTA_CODIGO, faltaCodigo);
        values.put(COLUMN_FALTA_CLASIFICACION, faltaClasificacion);
        values.put(COLUMN_OBSERVACIONES, observaciones);

        // *** AÑADIR LOS NUEVOS VALORES DE DECOMISOS A ContentValues ***
        // Convertimos el booleano a un entero (1 para true, 0 para false)
        values.put(COLUMN_DECOMISO_VEHICULOS, decomisoVehiculos ? 1 : 0);
        values.put(COLUMN_DECOMISO_TARJETA_CIRCULACION, decomisoTarjetaCirculacion ? 1 : 0);
        values.put(COLUMN_DECOMISO_LICENCIA, decomisoLicencia ? 1 : 0);
        values.put(COLUMN_DECOMISO_PLACAS, decomisoPlacas ? 1 : 0);
        values.put(COLUMN_DECOMISO_POLIZA, decomisoPoliza ? 1 : 0);
        values.put(COLUMN_DECOMISO_PERMISOS_LINEA, decomisoPermisosLinea ? 1 : 0);

        // *** AÑADIR LOS NUEVOS VALORES DE OTROS A ContentValues ***
        values.put(COLUMN_OTROS_CONDUCTOR_AUSENTE, otrosConductorAusente ? 1 : 0);
        values.put(COLUMN_OTROS_SE_NEGO_FIRMAR, otrosSeNegoFirmar ? 1 : 0);
        values.put(COLUMN_OTROS_APARATO_LASER, otrosAparatoLaser ? 1 : 0);
        values.put(COLUMN_OTROS_PRUEBA_ALCOTEST, otrosPruebaAlcotest ? 1 : 0);
        values.put(COLUMN_OTROS_DESTRUYO_ESQUELA, otrosDestruyoEsquela ? 1 : 0);
        values.put(COLUMN_OTROS_VEHICULO_REMOLCADO, otrosVehiculoRemolcado ? 1 : 0);


        long id = db.insert(TABLE_MULTAS, null, values);
        db.close();

        // Subir a Firebase después de una inserción exitosa en SQLite
        if (id != -1) {
            uploadMultaToFirebase(values);
        }

        return id;
    }

    public Cursor obtenerTodasLasMultas() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(TABLE_MULTAS, null, null, null, null, null, COLUMN_ID + " DESC");
    }

    // Nuevo método para subir una multa a Firebase
    public void uploadMultaToFirebase(ContentValues multaData) {
        // Firebase Realtime Database no soporta directamente ContentValues.
        // Necesitas convertirlo a un Map<String, Object> o a un objeto Java personalizado.
        Map<String, Object> firebaseMultaData = new HashMap<>();
        for (String key : multaData.keySet()) {
            firebaseMultaData.put(key, multaData.get(key));
        }

        // Usa una clave única para cada multa, por ejemplo, timestamp o ID de SQLite
        // Para simplificar, usaremos un push() que genera un ID único
        mDatabase.push().setValue(firebaseMultaData)
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "Multa subida a Firebase exitosamente.");
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error subiendo multa a Firebase: " + e.getMessage(), e); // Incluye la excepción en el log
                });
    }
}
