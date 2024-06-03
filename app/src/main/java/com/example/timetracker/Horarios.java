package com.example.timetracker;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class Horarios extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_horarios);

        // Obtener referencias a las vistas
        TextView textViewHorarios = findViewById(R.id.textViewHorarios);
        Button closeButton = findViewById(R.id.close_button);

        // Definir los horarios
        String horarios = "Lunes a Viernes: 14:00 - 22:00\nSábado, Domingo y Festivos: Cerrado";

        // Mostrar los horarios en el TextView
        textViewHorarios.setText(horarios);

        // Cerrar la actividad al hacer clic en el botón
        closeButton.setOnClickListener(v -> finish());
    }
}
