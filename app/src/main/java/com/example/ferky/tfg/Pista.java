package com.example.ferky.tfg;

public class Pista {


    private String id, duracion,localizacion,nombre,precio,tipo;

    public Pista(String duracion, String localizacion, String nombre, String tipo, String precio) {
        this.duracion = duracion;
        this.localizacion = localizacion;
        this.nombre = nombre;
        this.tipo = tipo;
        this.precio = precio;
    }
    public Pista() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDuracion() {
        return duracion;
    }

    public void setDuracion(String duracion) {
        this.duracion = duracion;
    }

    public String getLocalizacion() {
        return localizacion;
    }

    public void setLocalizacion(String localizacion) {
        this.localizacion = localizacion;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getPrecio() {
        return precio;
    }

    public void setPrecio(String precio) {
        this.precio = precio;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

}
