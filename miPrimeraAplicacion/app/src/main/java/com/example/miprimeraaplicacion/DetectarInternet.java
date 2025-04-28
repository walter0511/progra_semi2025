package com.example.miprimeraaplicacion;

import  android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class detectarInternet {
    private Context context;

    public detectarInternet(Context context) {
        this.context = context;
    }

    public boolean hayConexionInternet() {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }
}