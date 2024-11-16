package com.example.appclinicabiovida;

import java.util.Date;

public class CitaCompleta {

    private String codigoCita;
    private Date fechaHorario;
    private String nombreDoctor;
    private String apellidoDoctor;
    private String nombreSede;

    private String nombreEspecialidad;






    public CitaCompleta() {
    }

    public CitaCompleta(String codigoCita, Date fechaHorario, String nombreDoctor, String apellidoDoctor, String nombreSede, String nombreEspecialidad) {
        this.codigoCita = codigoCita;
        this.fechaHorario = fechaHorario;
        this.nombreDoctor = nombreDoctor;
        this.apellidoDoctor=apellidoDoctor;
        this.nombreSede = nombreSede;
        this.nombreEspecialidad = nombreEspecialidad;

    }

    public String getCodigoCita() {
        return codigoCita;
    }

    public void setCodigoCita(String codigoCita) {
        this.codigoCita = codigoCita;
    }

    public Date getFechaHorario() {
        return fechaHorario;
    }

    public void setFechaHorario(Date fechaHorario) {
        this.fechaHorario = fechaHorario;
    }

    public String getNombreDoctor() {
        return nombreDoctor;
    }

    public void setNombreDoctor(String nombreDoctor) {
        this.nombreDoctor = nombreDoctor;
    }



    public String getApellidoDoctor() {
        return apellidoDoctor;
    }

    public void setApellidoDoctor(String apellidoDoctor) {
        this.apellidoDoctor = apellidoDoctor;
    }


    public String getNombreSede() {
        return nombreSede;
    }

    public void setNombreSede(String nombreSede) {
        this.nombreSede = nombreSede;
    }

    public String getNombreEspecialidad() {
        return nombreEspecialidad;
    }

    public void setNombreEspecialidad(String nombreEspecialidad) {
        this.nombreEspecialidad = nombreEspecialidad;
    }
}
