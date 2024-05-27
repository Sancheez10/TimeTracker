package com.example.timetracker;

import java.util.Date;

public class Person {

    private int empleadoId;
    private String nombre;
    private String apellido;
    private String email;
    private Date horaEntrada;
    private Date horaSalida;
    private int horasTrabajadas;
    private String poblacion;

    public Person(int empleadoId, String nombre, String apellido, String email, Date horaEntrada, Date horaSalida, int horasTrabajadas, String poblacion) {
        this.empleadoId = empleadoId;
        this.nombre = nombre;
        this.apellido = apellido;
        this.email = email;
        this.horaEntrada = horaEntrada;
        this.horaSalida = horaSalida;
        this.horasTrabajadas = horasTrabajadas;
        this.poblacion = poblacion;
    }

    public Person() {
    }

    public int getEmpleadoId() {
        return empleadoId;
    }

    public void setEmpleadoId(int empleadoId) {
        this.empleadoId = empleadoId;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Date getHoraEntrada() {
        return horaEntrada;
    }

    public void setHoraEntrada(Date horaEntrada) {
        this.horaEntrada = horaEntrada;
    }

    public Date getHoraSalida() {
        return horaSalida;
    }

    public void setHoraSalida(Date horaSalida) {
        this.horaSalida = horaSalida;
    }

    public int getHorasTrabajadas() {
        return horasTrabajadas;
    }

    public void setHorasTrabajadas(int horasTrabajadas) {
        this.horasTrabajadas = horasTrabajadas;
    }

    public String getPoblacion() {
        return poblacion;
    }

    public void setPoblacion(String poblacion) {
        this.poblacion = poblacion;
    }
}
