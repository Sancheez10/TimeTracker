package com.example.timetracker;

import java.util.Date;

public class Anotacion {
    private String id; // ID del documento en Firestore
    private String texto; // Texto de la anotación
    private Date fechaHora; // Fecha y hora de la anotación
    private String fileUrl; // URL del archivo adjunto, si lo hay
    private String createdBy; // Usuario que creó la anotación
    private boolean isViewed; // Indicador de si la anotación ha sido vista

    // Constructor vacío necesario para Firebase
    public Anotacion() {}

    public Anotacion(String id, String texto, Date fechaHora, String fileUrl, String createdBy, boolean isViewed) {
        this.id = id;
        this.texto = texto;
        this.fechaHora = fechaHora;
        this.fileUrl = fileUrl;
        this.createdBy = createdBy;
        this.isViewed = isViewed;
    }

    public Anotacion(String id, String texto, String createdBy,  boolean isViewed) {
        this.id = id;
        this.texto = texto;
        this.createdBy = createdBy;
        this.isViewed = isViewed;
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

    public boolean isViewed() {
        return isViewed;
    }

    public void setViewed(boolean viewed) {
        isViewed = viewed;
    }
}
