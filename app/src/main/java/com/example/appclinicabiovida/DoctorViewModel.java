package com.example.appclinicabiovida;

public class DoctorViewModel {
    private Doctor doctor;
    private String nombreEspecialidad;

    public DoctorViewModel(Doctor doctor, String nombreEspecialidad) {
        this.doctor = doctor;
        this.nombreEspecialidad = nombreEspecialidad;
    }

    public Doctor getDoctor() {
        return doctor;
    }

    public String getNombreEspecialidad() {
        return nombreEspecialidad;
    }
}
