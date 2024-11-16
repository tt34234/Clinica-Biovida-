package com.example.appclinicabiovida;

import java.sql.Time;
import java.util.Date;

public class Horario {

    private int idHorario;
    private int idDoctor;
    private Date fechaHorario;
    private Time horaInicio;
    private Time horaFin;
    private String estadoHorario;

    public Horario() {
    }

    public Horario(int idHorario, int idDoctor, Date fechaHorario, Time horaInicio, Time horaFin, String estadoHorario) {
        this.idHorario = idHorario;
        this.idDoctor = idDoctor;
        this.fechaHorario = fechaHorario;
        this.horaInicio = horaInicio;
        this.horaFin = horaFin;
        this.estadoHorario = estadoHorario;
    }

    public int getIdHorario() {
        return idHorario;
    }

    public void setIdHorario(int idHorario) {
        this.idHorario = idHorario;
    }

    public int getIdDoctor() {
        return idDoctor;
    }

    public void setIdDoctor(int idDoctor) {
        this.idDoctor = idDoctor;
    }

    public Date getFechaHorario() {
        return fechaHorario;
    }

    public void setFechaHorario(Date fechaHorario) {
        this.fechaHorario = fechaHorario;
    }

    public Time getHoraInicio() {
        return horaInicio;
    }

    public void setHoraInicio(Time horaInicio) {
        this.horaInicio = horaInicio;
    }

    public Time getHoraFin() {
        return horaFin;
    }

    public void setHoraFin(Time horaFin) {
        this.horaFin = horaFin;
    }

    public String getEstadoHorario() {
        return estadoHorario;
    }

    public void setEstadoHorario(String estadoHorario) {
        this.estadoHorario = estadoHorario;
    }
}
