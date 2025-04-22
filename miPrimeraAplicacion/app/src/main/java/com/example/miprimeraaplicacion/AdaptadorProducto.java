package com.example.miprimeraaplicacion;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class AdaptadorProducto extends ArrayAdapter<productos> {

    private Context context;
    private ArrayList<productos> productosList;


    public AdaptadorProducto(Context context, ArrayList<productos> alProductos) {
        super(context, 0, alProductos);
        this.context = context;
        this.productosList = alProductos;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(android.R.layout.simple_list_item_1, parent, false);
        }


        productos producto = productosList.get(position);


        TextView textView = convertView.findViewById(android.R.id.text1);
        textView.setText(producto.getNombre() + " - Ganancia: " + producto.getGanancia());

        return convertView;
    }
}