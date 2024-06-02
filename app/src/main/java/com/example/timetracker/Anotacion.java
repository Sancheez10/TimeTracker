package com.example.timetracker;

import java.util.Date;

public class Anotacion {
    private String id;
    private String texto;
    private Date fechaHora;
    private String fileUrl; // URL del fichero adjunto
    private String createdBy; // Trabajador que la creó

    // Constructor vacío necesario para Firebase
    public Anotacion() {}

    public Anotacion(String id, String texto, Date fechaHora, String fileUrl, String createdBy) {
        this.id = id;
        this.texto = texto;
        this.fechaHora = fechaHora;
        this.fileUrl = fileUrl;
        this.createdBy = createdBy;
    }

    // Getters y setters

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTexto() {
        return texto;
    }

    public void setTexto(String texto) {
        this.texto = texto;
    }

    public Date getFechaHora() {
        return fechaHora;
    }

    public void setFechaHora(Date fechaHora) {
        this.fechaHora = fechaHora;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }
}
