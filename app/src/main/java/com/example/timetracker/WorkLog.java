package com.example.timetracker;

public class WorkLog {
        private String id;
        private String workerId;
        private long checkInTime;  // Hora de entrada en milisegundos
        private long checkOutTime; // Hora de salida en milisegundos
        private double totalHours; // Horas totales trabajadas en este registro

        // Constructor vacío necesario para Firebase
        public WorkLog() {}

        // Constructor con todos los campos
        public WorkLog(String id, String workerId, long checkInTime, long checkOutTime) {
            this.id = id;
            this.workerId = workerId;
            this.checkInTime = checkInTime;
            this.checkOutTime = checkOutTime;
            this.totalHours = calculateTotalHours(checkInTime, checkOutTime);
        }

        // Getters y setters
        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
        public String getWorkerId() { return workerId; }
        public void setWorkerId(String workerId) { this.workerId = workerId; }
        public long getCheckInTime() { return checkInTime; }
        public void setCheckInTime(long checkInTime) {
            this.checkInTime = checkInTime;
            updateTotalHours();
        }
        public long getCheckOutTime() { return checkOutTime; }
        public void setCheckOutTime(long checkOutTime) {
            this.checkOutTime = checkOutTime;
            updateTotalHours();
        }
        public double getTotalHours() { return totalHours; }
        public void setTotalHours(double totalHours) { this.totalHours = totalHours; }

        // Método para calcular las horas totales trabajadas
        private double calculateTotalHours(long checkInTime, long checkOutTime) {
            if (checkInTime == 0 || checkOutTime == 0) {
                return 0;
            }
            long difference = checkOutTime - checkInTime;
            return difference / (1000.0 * 60 * 60); // Convertir milisegundos a horas
        }

        // Método para actualizar las horas totales trabajadas
        private void updateTotalHours() {
            this.totalHours = calculateTotalHours(this.checkInTime, this.checkOutTime);
        }
    }
