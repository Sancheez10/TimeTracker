package com.example.timetracker;

import java.util.Date;

public class Anotacion {
    private String id; // ID del documento en Firestore
    private String texto; // Texto de la anotación
    private Date fechaHora; // Fecha y hora de la anotación
    private String fileUrl; // URL del archivo adjunto, si lo hay
    private String createdBy; // Usuario que creó la anotación
    private boolean viewed; // Indicador de si la anotación ha sido vista


    // Constructor vacío necesario para Firebase
    public Anotacion() {}

    public Anotacion(String id, String texto, Date fechaHora, String fileUrl, String createdBy, boolean isViewed) {
        this.id = id;
        this.texto = texto;
        this.fechaHora = fechaHora;
        this.fileUrl = fileUrl;
        this.createdBy = createdBy;
        this.viewed = isViewed;
    }

    public Anotacion(String id, String texto, Date fechaHora, String createdBy, boolean isViewed) {
        this.id = id;
        this.texto = texto;
        this.fechaHora = fechaHora;
        this.createdBy = createdBy;
        this.viewed = isViewed;
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
        return viewed; // Mantén este método si prefieres llamarlo 'isViewed' en tu código
    }

    public void setViewed(boolean viewed) {
        this.viewed = viewed;
    }

}
