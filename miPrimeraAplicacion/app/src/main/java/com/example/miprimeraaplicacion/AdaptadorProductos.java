package com.example.miprimeraaplicacion;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class AdaptadorProductos extends BaseAdapter {
    private Context context;
    private ArrayList<productos> alProductos;
    private LayoutInflater inflater;

    public AdaptadorProductos(Context context, ArrayList<productos> alProductos) {
        this.context = context;
        this.alProductos = alProductos;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() { return alProductos.size(); }

    @Override
    public Object getItem(int position) { return alProductos.get(position); }

    @Override
    public long getItemId(int position) { return position; }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View itemView = convertView;
        if (itemView == null) {
            itemView = inflater.inflate(R.layout.item_producto, parent, false);
        }

        productos miProducto = alProductos.get(position);

        try {
            TextView tempVal = itemView.findViewById(R.id.lblNombreProducto);
            tempVal.setText(miProducto.getNombre());

            tempVal = itemView.findViewById(R.id.lblPrecio);
            tempVal.setText(String.format("Precio: $%.2f", miProducto.getPrecio()));

            tempVal = itemView.findViewById(R.id.lblCosto);
            tempVal.setText(String.format("Costo: $%.2f", miProducto.getCosto()));

            tempVal = itemView.findViewById(R.id.lblGanancia);
            tempVal.setText(String.format("Ganancia: $%.2f (%.2f%%)",
                    miProducto.getGanancia(), miProducto.getPorcentajeGanancia()));

            tempVal = itemView.findViewById(R.id.lblStock);
            tempVal.setText("Stock: " + miProducto.getStock());

            ImageView img = itemView.findViewById(R.id.imgProducto);
            if (miProducto.getImagenUrl() != null && !miProducto.getImagenUrl().isEmpty()) {
                Bitmap bitmap = BitmapFactory.decodeFile(miProducto.getImagenUrl());
                if (bitmap != null) {
                    img.setImageBitmap(bitmap);
                }
            }
        } catch (Exception e) {
            Toast.makeText(context, "Error al mostrar producto: " + e.getMessage(),
                    Toast.LENGTH_SHORT).show();
        }

        return itemView;
    }

}