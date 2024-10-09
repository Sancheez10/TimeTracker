package com.example.timetracker;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class AddAnotacionActivity extends AppCompatActivity {

    private EditText etTextoAnotacion;
    private Button btnFechaHora, btnGuardarAnotacion, bhistorial;
    private TextView tvFechaHora;
    private Date fechaHoraSeleccionada; // Fecha seleccionada por el usuario
    private FirebaseHelper firebaseHelper;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_anotations_admin);

        etTextoAnotacion = findViewById(R.id.etTextoAnotacion);
        btnFechaHora = findViewById(R.id.btnFechaHora);
        btnGuardarAnotacion = findViewById(R.id.btnGuardarAnotacion);
        tvFechaHora = findViewById(R.id.tvFechaHora);
        bhistorial = findViewById(R.id.bHistorial);

        // Inicializar FirebaseHelper
        firebaseHelper = new FirebaseHelper();

        // Obtener SharedPreferences
        sharedPreferences = getSharedPreferences("workers_pref", MODE_PRIVATE);

        // Selección de fecha y hora
        btnFechaHora.setOnClickListener(v -> seleccionarFechaHora());

        // Guardar anotación
        btnGuardarAnotacion.setOnClickListener(v -> guardarAnotacion());

        // Ir al historial de anotaciones
        bhistorial.setOnClickListener(view -> clickHistorial());
    }

    /**
     * Método para mostrar los diálogos de selección de fecha y hora.
     */
    private void seleccionarFechaHora() {
        final Calendar calendar = Calendar.getInstance();

        // Mostrar DatePickerDialog para seleccionar la fecha
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            // Mostrar TimePickerDialog para seleccionar la hora
            TimePickerDialog timePickerDialog = new TimePickerDialog(this, (timeView, hourOfDay, minute) -> {
                calendar.set(year, month, dayOfMonth, hourOfDay, minute);
                fechaHoraSeleccionada = calendar.getTime(); // Guardar la fecha y hora seleccionada
                tvFechaHora.setText(fechaHoraSeleccionada.toString()); // Mostrar la fecha seleccionada
            }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true);
            timePickerDialog.show();
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    /**
     * Guarda la anotación tanto en Firebase como en SharedPreferences.
     */
    private void guardarAnotacion() {
        String texto = etTextoAnotacion.getText().toString();

        // Validar que el campo de texto no esté vacío
        if (texto.isEmpty()) {
            Toast.makeText(this, "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        // Validar que se haya seleccionado una fecha y hora
        if (fechaHoraSeleccionada == null) {
            Toast.makeText(this, "Por favor, seleccione una fecha y hora", Toast.LENGTH_SHORT).show();
            return;
        }

        // Obtener el correo del usuario desde SharedPreferences
        String createdBy = sharedPreferences.getString("email", "Desconocido");

        // Crear una nueva anotación
        Anotacion anotacion = new Anotacion(null, texto, fechaHoraSeleccionada, null, createdBy, false);

        // Guardar en Firebase
        guardarAnotacionEnFirebase(anotacion);

        // Guardar en SharedPreferences (opcional, por si deseas un almacenamiento local)
        guardarAnotacionEnSharedPreferences(anotacion);
    }

    /**
     * Guarda la anotación en Firebase.
     */
    private void guardarAnotacionEnFirebase(Anotacion anotacion) {
        firebaseHelper.addAnotacion(anotacion, new FirebaseHelper.DataStatus() {
            @Override
            public void DataIsLoaded(List<?> data) {
                // No usado aquí
            }

            @Override
            public void DataIsInserted() {
                Toast.makeText(AddAnotacionActivity.this, "Anotación guardada", Toast.LENGTH_SHORT).show();
                finish(); // Finaliza la actividad tras guardar la anotación
            }

            @Override
            public void DataIsUpdated() {
                // No usado aquí
            }

            @Override
            public void DataIsDeleted() {
                // No usado aquí
            }

            @Override
            public void onError(String errorMessage) {
                Toast.makeText(AddAnotacionActivity.this, "Error al guardar la anotación: " + errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Guarda la anotación en SharedPreferences para almacenamiento local.
     */
    private void guardarAnotacionEnSharedPreferences(Anotacion anotacion) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("anotacion_text", anotacion.getTexto());
        editor.putString("anotacion_created_by", anotacion.getCreatedBy());
        editor.apply();
    }

    /**
     * Ir a la actividad del historial de anotaciones.
     */
    public void clickHistorial() {
        Intent intent = new Intent(this, HistorialAnotacionesActivity.class);
        startActivity(intent);
    }
}
