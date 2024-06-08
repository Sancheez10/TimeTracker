package com.example.timetracker;

public class Fichaje {
    private String date;
    private String location;
    private String checkInTime;
    private String checkOutTime;
    private String userId; // Solo para uso administrativo

    public Fichaje(String date, String location, String checkInTime, String checkOutTime) {
        this.date = date;
        this.location = location;
        this.checkInTime = checkInTime;
        this.checkOutTime = checkOutTime;
    }

    public Fichaje(String date, String location, String checkInTime, String checkOutTime, String userId) {
        this.date = date;
        this.location = location;
        this.checkInTime = checkInTime;
        this.checkOutTime = checkOutTime;
        this.userId = userId;
    }

    public String getDate() {
        return date;
    }

    public String getLocation() {
        return location;
    }

    public String getCheckInTime() {
        return checkInTime;
    }

    public String getCheckOutTime() {
        return checkOutTime;
    }

    public String getUserId() {
        return userId;
    }
}
