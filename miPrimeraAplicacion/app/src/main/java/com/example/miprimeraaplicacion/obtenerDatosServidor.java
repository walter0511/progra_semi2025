package com.example.miprimeraaplicacion;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class obtenerDatosServidor extends AsyncTask<Void, Void, String> {

    Context context;
    OnDatosActualizados listener;

    public obtenerDatosServidor(Context context, OnDatosActualizados listener) {
        this.context = context;
        this.listener = listener;
    }

    @Override
    protected String doInBackground(Void... voids) {
        StringBuilder result = new StringBuilder();
        try {
            URL url = new URL(utilidades.url_consulta); // Asegurate de que esta URL esté bien definida en tu clase utilidades
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String linea;
            while ((linea = reader.readLine()) != null) {
                result.append(linea);
            }
            reader.close();
        } catch (Exception e) {
            return "ERROR: " + e.getMessage();
        }
        return result.toString();
    }

    @Override
    protected void onPostExecute(String result) {
        if (result.startsWith("ERROR: ")) {
            Toast.makeText(context, result, Toast.LENGTH_LONG).show();
        } else {
            listener.alActualizar(result);
        }
    }

    public interface OnDatosActualizados {
        void alActualizar(String respuesta);
    }
}