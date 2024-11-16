package com.example.appclinicabiovida;

public class Cita {
    String codigoCita;
    String dniPaciente,sintomas;
    String estadoCita,motivocancelacionCita;
    String idHorario;
    private int idDoctor;

    public Cita(String codigoCita, String dniPaciente, String sintomas, String estadoCita, String idHorario, String motivocancelacionCita) {
        this.codigoCita = codigoCita;
        this.dniPaciente = dniPaciente;
        this.sintomas = sintomas;
        this.estadoCita = estadoCita;
        this.idHorario = idHorario;
        this.motivocancelacionCita = motivocancelacionCita;
    }

    public String getCodigoCita() {
        return codigoCita;
    }

    public void setCodigoCita(String codigoCita) {
        this.codigoCita = codigoCita;
    }

    public String getDniPaciente() {
        return dniPaciente;
    }

    public void setDniPaciente(String dniPaciente) {
        this.dniPaciente = dniPaciente;
    }

    public String getSintomas() {
        return sintomas;
    }

    public void setSintomas(String sintomasCita) {
        this.sintomas = sintomasCita;
    }

    public String getEstadoCita() {
        return estadoCita;
    }

    public void setEstadoCita(String estadoCita) {
        this.estadoCita = estadoCita;
    }

    public String getMotivocancelacionCita() {
        return motivocancelacionCita;
    }

    public void setMotivocancelacionCita(String motivocancelacionCita) {
        this.motivocancelacionCita = motivocancelacionCita;
    }

    public String getIdHorario() {
        return idHorario;
    }

    public void setIdHorario(String idHorario) {
        this.idHorario = idHorario;
    }
    public int getIdDoctor() {
        return idDoctor;
    }

    // Setter para el idDoctor
    public void setIdDoctor(int idDoctor) {
        this.idDoctor = idDoctor;
    }
}



