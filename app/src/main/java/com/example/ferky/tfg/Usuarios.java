package com.example.ferky.tfg;

public class Usuarios {
    String usuarioid;
    String nombre;
    String apellido;
    String telefono;
    Integer tipo;

    public Usuarios(String usuarioid, String nombre, String apellido, String telefono,Integer tipo) {
        this.usuarioid = usuarioid;
        this.nombre = nombre;
        this.apellido = apellido;
        this.telefono = telefono;
        this.tipo = tipo;
    }

    public String getUsuarioid() {
        return usuarioid;
    }

    public void setUsuarioid(String usuarioid) {
        this.usuarioid = usuarioid;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public Integer getTipo() { return tipo; }

    public void setTipo(Integer tipo) { this.tipo = tipo; }



}
