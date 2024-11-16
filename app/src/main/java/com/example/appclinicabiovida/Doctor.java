package com.example.appclinicabiovida;

public class Doctor {
    private int id_doctor;
    private int id_especialidad; // Foreign Key (relacionado con Especialidad)
    private int id_sede; // Foreign Key (relacionado con Sede)
    private String nombre_doctor;
    private String apellido_doctor;
    private String estado_doctor; // Estado del doctor (activo, inactivo)

    public Doctor() {
    }

    public Doctor(int id_doctor, int id_especialidad, int id_sede, String nombre_doctor, String apellido_doctor, String estado_doctor) {
        this.id_doctor = id_doctor;
        this.id_especialidad = id_especialidad;
        this.id_sede = id_sede;
        this.nombre_doctor = nombre_doctor;
        this.apellido_doctor = apellido_doctor;
        this.estado_doctor = estado_doctor;
    }

    public int getId_doctor() {
        return id_doctor;
    }

    public void setId_doctor(int id_doctor) {
        this.id_doctor = id_doctor;
    }

    public int getId_especialidad() {
        return id_especialidad;
    }

    public void setId_especialidad(int id_especialidad) {
        this.id_especialidad = id_especialidad;
    }

    public int getId_sede() {
        return id_sede;
    }

    public void setId_sede(int id_sede) {
        this.id_sede = id_sede;
    }

    public String getNombre_doctor() {
        return nombre_doctor;
    }

    public void setNombre_doctor(String nombre_doctor) {
        this.nombre_doctor = nombre_doctor;
    }

    public String getApellido_doctor() {
        return apellido_doctor;
    }

    public void setApellido_doctor(String apellido_doctor) {
        this.apellido_doctor = apellido_doctor;
    }

    public String getEstado_doctor() {
        return estado_doctor;
    }

    public void setEstado_doctor(String estado) {
        this.estado_doctor = estado;
    }
}