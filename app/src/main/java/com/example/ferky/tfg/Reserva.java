package com.example.ferky.tfg;

public class Reserva {
    private String duracion;
    private String fecha;
    private String fecha_pista;
    private String pista;
    private String inicio;
    private String usuario;
    private String precio;
    private String id;
    private String horainicio;




    public Reserva(String duracion, String fecha, String fecha_pista, String pista, String inicio, String usuario, String precio, String horainicio) {
        this.duracion = duracion;
        this.fecha = fecha;
        this.fecha_pista = fecha_pista;
        this.pista = pista;
        this.inicio = inicio;
        this.usuario = usuario;
        this.precio = precio;
        this.horainicio = horainicio;
    }
    public Reserva() {
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

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getFecha_pista() {
        return fecha_pista;
    }

    public void setFecha_pista(String fecha_pista) {
        this.fecha_pista = fecha_pista;
    }

    public String getPista() {
        return pista;
    }

    public void setPista(String pista) {
        this.pista = pista;
    }

    public String getInicio() {
        return inicio;
    }

    public void setInicio(String inicio) {
        this.inicio = inicio;
    }

    public String getUsuario() { return usuario; }

    public void setUsuario(String usuario) { this.usuario = usuario; }

    public String getPrecio() {
        return precio;
    }

    public void setPrecio(String precio) {
        this.precio = precio;
    }

    public String getHorainicio() {
        return horainicio;
    }

    public void setHorainicio(String horainicio) {
        this.horainicio = horainicio;
    }


}
