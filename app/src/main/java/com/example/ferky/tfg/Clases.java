package com.example.ferky.tfg;

public class Clases {
    String claseid,seccion,asignatura,tema;

    public Clases(String claseid, String seccion, String asignatura, String tema) {
        this.claseid = claseid;
        this.seccion = seccion;
        this.asignatura = asignatura;
        this.tema = tema;
    }


    public String getClaseid() {
        return claseid;
    }

    public String getSeccion() {
        return seccion;
    }

    public String getAsignatura() {
        return asignatura;
    }

    public String getTema() {
        return tema;
    }
}
