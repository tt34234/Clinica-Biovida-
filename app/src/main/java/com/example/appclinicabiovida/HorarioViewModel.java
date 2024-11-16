package com.example.appclinicabiovida;

public class HorarioViewModel {
    private Horario horario;
    private String nombreFecha;

    public HorarioViewModel(Horario horario, String nombreFecha) {
        this.horario = horario;
        this.nombreFecha = nombreFecha;
    }

    public Horario getHorario() {
        return horario;
    }

    public String getNombreFecha() {
        return nombreFecha;
    }
}
