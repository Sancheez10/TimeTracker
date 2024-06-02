package com.example.timetracker;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class Calendario extends AppCompatActivity {

    private Spinner spinner;
    private CalendarView calendarView;
    private Button bVolver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        Toolbar toolbar = findViewById(R.id.toolbar3);
        setSupportActionBar(toolbar);

        spinner = findViewById(R.id.spinner);
        calendarView = findViewById(R.id.calendarView);
        bVolver = findViewById(R.id.bVolver);

        // Obtener el año actual
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);

        // Generar una lista de años disponibles: el año actual y los dos siguientes
        List<String> years = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            years.add(String.valueOf(currentYear + i));
        }

        // Crear un adaptador para el Spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, years);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        // Listener para el Spinner que actualiza el año seleccionado en el CalendarView
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedYear = parent.getItemAtPosition(position).toString();
                int year = Integer.parseInt(selectedYear);
                // Obtener el mes y día actual para no cambiarlos
                Calendar currentCalendar = Calendar.getInstance();
                int month = currentCalendar.get(Calendar.MONTH);
                int dayOfMonth = currentCalendar.get(Calendar.DAY_OF_MONTH);
                // Establecer la fecha en el CalendarView
                Calendar selectedCalendar = Calendar.getInstance();
                selectedCalendar.set(year, month, dayOfMonth);
                calendarView.setDate(selectedCalendar.getTimeInMillis());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // No es necesario implementar nada aquí para este caso
            }
        });

        bVolver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}


