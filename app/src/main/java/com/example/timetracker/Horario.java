package com.example.timetracker;

public class Horario {
    private String workerId;
    private String entrada;
    private String salida;

    public Horario(String workerId, String entrada, String salida) {
        this.workerId = workerId;
        this.entrada = entrada;
        this.salida = salida;
    }

    public String getWorkerId() {
        return workerId;
    }

    public String getEntrada() {
        return entrada;
    }

    public String getSalida() {
        return salida;
    }
}
