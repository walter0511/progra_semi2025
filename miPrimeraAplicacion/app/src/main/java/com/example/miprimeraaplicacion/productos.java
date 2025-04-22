package com.example.miprimeraaplicacion;

public class productos {

    private String nombre;
    private String urlFoto;
    private double precio;
    private double costo;
    private double ganancia;


    public productos(String nombre, String urlFoto, double precio, double costo) {
        this.nombre = nombre;
        this.urlFoto = urlFoto;
        this.precio = precio;
        this.costo = costo;
        this.ganancia = calcularGanancia();
    }


    private double calcularGanancia() {
        return this.precio - this.costo;
    }


    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getUrlFoto() {
        return urlFoto;
    }

    public void setUrlFoto(String urlFoto) {
        this.urlFoto = urlFoto;
    }

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
        this.ganancia = calcularGanancia();
    }

    public double getCosto() {
        return costo;
    }

    public void setCosto(double costo) {
        this.costo = costo;
        this.ganancia = calcularGanancia();
    }

    public double getGanancia() {
        return ganancia;
    }


    @Override
    public String toString() {
        return "Producto: " + nombre + "\n" +
                "Precio: " + precio + "\n" +
                "Costo: " + costo + "\n" +
                "Ganancia: " + ganancia + "\n" +
                "Foto: " + urlFoto;
    }
}
