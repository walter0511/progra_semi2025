package com.example.miprimeraaplicacion;
import android.os.AsyncTask;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class obtenerDatosServidor extends AsyncTask<Void, Void, String> {
    private lista_productos actividad;

    public obtenerDatosServidor(lista_productos actividad) {
        this.actividad = actividad;
    }

    @Override
    protected String doInBackground(Void... voids) {
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(utilidades.url_consulta);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setRequestProperty("Authorization", "Basic " + utilidades.credencialesCodificadas);

            InputStream inputStream = urlConnection.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder stringBuilder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
            }
            reader.close();
            return stringBuilder.toString();
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
    }

    @Override
    protected void onPostExecute(String result) {
        if (result.startsWith("Error:")) {
            Toast.makeText(actividad, result, Toast.LENGTH_LONG).show();
            actividad.cargarProductosLocales();
        } else {
            actividad.procesarRespuestaServidor(result);
        }
    }
}