package com.example.miprimeraaplicacion;

import android.app.Application;
import com.google.firebase.FirebaseApp; // Asegúrate de tener esta importación
import com.google.firebase.database.FirebaseDatabase; // Asegúrate de tener esta importación

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseApp.initializeApp(this); // Inicializa Firebase
        // Opcional: Habilita las capacidades sin conexión (persistencia de datos)
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }
}

