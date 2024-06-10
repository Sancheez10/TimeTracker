package com.example.timetracker;

public class Worker {
    private String nieNif;
    private String id;
    private String name;
    private String surname;
    private String password;
    private String email;
    private String phone;
    private String city;
    private long checkInTime;  // Hora de entrada en milisegundos
    private long checkOutTime; // Hora de salida en milisegundos
    private double totalDailyHours; // Horas totales diarias
    private boolean isAdmin; // Campo booleano para indicar si es subadministrador

    // Constructor con todos los campos
    public Worker(String id, String name, String surname, String email, String phone, String city,
                  long checkInTime, long checkOutTime, boolean isAdmin, String password, String nieNif) {
        this.id = id;
        this.name = name;
        this.surname = surname;
        this.email = email;
        this.phone = phone;
        this.city = city;
        this.checkInTime = checkInTime;
        this.checkOutTime = checkOutTime;
        this.totalDailyHours = calculateTotalDailyHours(checkInTime, checkOutTime);
        this.isAdmin = isAdmin;
        this.password = password;
        this.nieNif = nieNif;

    }

    public Worker(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public Worker(String nombre, String apellidos, String email, String telefono, String nieNif) {
        this.name = nombre;
        this.surname = apellidos;
        this.email = email;
        this.phone = telefono;
        this.nieNif = nieNif;
    }

    public Worker(String id,String nombre, String email, String password) {
        this.id = id;
        this.name = nombre;
        this.email = email;
        this.password = password;
    }

    // Getters y setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getSurname() { return surname; }
    public void setSurname(String surname) { this.surname = surname; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }
    public long getCheckInTime() { return checkInTime; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getNieNif() { return nieNif; }
    public void setNieNif(String nieNif) { this.nieNif = nieNif; }
    public void setCheckInTime(long checkInTime) {
        this.checkInTime = checkInTime;
        updateTotalDailyHours();
    }
    public long getCheckOutTime() { return checkOutTime; }
    public void setCheckOutTime(long checkOutTime) {
        this.checkOutTime = checkOutTime;
        updateTotalDailyHours();
    }
    public double getTotalDailyHours() { return totalDailyHours; }
    public void setTotalDailyHours(double totalDailyHours) { this.totalDailyHours = totalDailyHours; }
    public boolean isAdmin() { return isAdmin; }
    public void setAdmin(boolean admin) { isAdmin = admin; }

    // Método para calcular las horas totales diarias
    private double calculateTotalDailyHours(long checkInTime, long checkOutTime) {
        if (checkInTime == 0 || checkOutTime == 0) {
            return 0;
        }
        long difference = checkOutTime - checkInTime;
        return difference / (1000.0 * 60 * 60); // Convertir milisegundos a horas
    }

    // Método para actualizar las horas totales diarias
    private void updateTotalDailyHours() {
        this.totalDailyHours = calculateTotalDailyHours(this.checkInTime, this.checkOutTime);
    }
}
