package com.example.miprimeraaplicacion;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class enviarDatosServidor extends AsyncTask<String, Void, String> {
    private Context context;

    public enviarDatosServidor(Context context) {
        this.context = context;
    }

    @Override
    protected String doInBackground(String... params) {
        String jsonDatos = params[0];
        String metodo = params[1];
        String urlString = params[2];

        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(urlString);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod(metodo);
            urlConnection.setRequestProperty("Content-Type", "application/json");
            urlConnection.setRequestProperty("Accept", "application/json");
            urlConnection.setRequestProperty("Authorization", "Basic " + utilidades.credencialesCodificadas);
            urlConnection.setDoOutput(true);
            urlConnection.setDoInput(true);

            // Escribir datos
            OutputStream os = urlConnection.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
            writer.write(jsonDatos);
            writer.flush();
            writer.close();
            os.close();

            // Leer respuesta
            int responseCode = urlConnection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK ||
                    responseCode == HttpURLConnection.HTTP_CREATED) {
                InputStream is = urlConnection.getInputStream();
                BufferedReader br = new BufferedReader(new InputStreamReader(is));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    sb.append(line);
                }
                br.close();
                return sb.toString();
            } else {
                return "Error: " + responseCode + " " + urlConnection.getResponseMessage();
            }
        } catch (Exception e) {
            return "Exception: " + e.getMessage();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
    }

    @Override
    protected void onPostExecute(String result) {
        try {
            JSONObject json = new JSONObject(result);
            if (json.has("ok") && json.getBoolean("ok")) {
                Toast.makeText(context, "Sincronizado con servidor", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, "Error en servidor: " + result, Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Toast.makeText(context, "Error: " + result, Toast.LENGTH_LONG).show();
        }
    }
}