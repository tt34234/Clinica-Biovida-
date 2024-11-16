package com.example.appclinicabiovida;

public class Sede {

    int id_sede;
    String nombre_sede;
    String direccion_sede;

    public Sede() {
    }

    public Sede(int id_sede, String nombre_sede, String direccion_sede) {
        this.id_sede = id_sede;
        this.nombre_sede = nombre_sede;
        this.direccion_sede = direccion_sede;
    }

    public int getId_sede() {
        return id_sede;
    }

    public void setId_sede(int id_sede) {
        this.id_sede = id_sede;
    }

    public String getNombre_sede() {
        return nombre_sede;
    }

    public void setNombre_sede(String nombre_sede) {
        this.nombre_sede = nombre_sede;
    }

    public String getDireccion_sede() {
        return direccion_sede;
    }

    public void setDireccion_sede(String direccion_sede) {
        this.direccion_sede = direccion_sede;
    }
}
