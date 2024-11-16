package com.example.appclinicabiovida;

public class Especialidad {

    int id_especialidad;
    String nombre_especialidad;
    String urlImagen_especialidad;

    public Especialidad() {

    }

    public Especialidad(int id_especialidad, String nombre_especialidad, String urlImagen_especialidad) {
        this.id_especialidad = id_especialidad;
        this.nombre_especialidad = nombre_especialidad;
        this.urlImagen_especialidad = urlImagen_especialidad;

    }

    public int getId_especialidad() {
        return id_especialidad;
    }

    public void setId_especialidad(int id_especialidad) {
        this.id_especialidad = id_especialidad;
    }

    public String getNombre_especialidad() {
        return nombre_especialidad;
    }

    public void setNombre_especialidad(String nombre_especialidad) {
        this.nombre_especialidad = nombre_especialidad;
    }

    public String getUrlImagen_especialidad() {
        return urlImagen_especialidad;
    }

    public void setUrlImagen_especialidad(String urlImagen_especialidad) {
        this.urlImagen_especialidad = urlImagen_especialidad;
    }
}
