package com.example.miprimeraaplicacion;
public class productos {
    private String idProducto, nombre, imagenUrl;
    private double precio, costo;
    private int stock;

    public productos(String idProducto, String nombre, String imagenUrl, double precio, double costo, int stock) {
        this.idProducto = idProducto;
        this.nombre = nombre;
        this.imagenUrl = imagenUrl;
        this.precio = precio;
        this.costo = costo;
        this.stock = stock;
    }

    // Getters
    public String getIdProducto() { return idProducto; }
    public String getNombre() { return nombre; }
    public String getImagenUrl() { return imagenUrl; }
    public double getPrecio() { return precio; }
    public double getCosto() { return costo; }
    public int getStock() { return stock; }

    // Cálculos
    public double getGanancia() { return precio - costo; }
    public double getPorcentajeGanancia() {
        return costo != 0 ? ((precio - costo) / costo) * 100 : 0;
    }
}