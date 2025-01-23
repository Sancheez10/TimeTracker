package com.example.timetracker;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class AddAnotacionAdminActivity extends AppCompatActivity {

    private EditText etTextoAnotacion;
    private Button btnGuardarAnotacion, bhistorial;
    private SharedPreferences sharedPreferences;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_anotations_admin);

        etTextoAnotacion = findViewById(R.id.etTextoAnotacion);
        btnGuardarAnotacion = findViewById(R.id.btnGuardarAnotacion);
        bhistorial = findViewById(R.id.bHistorial);

        // Obtener SharedPreferences
        sharedPreferences = getSharedPreferences("workers_pref", MODE_PRIVATE);

        // Inicializar FirebaseAuth
        mAuth = FirebaseAuth.getInstance();

        // Guardar anotación
        btnGuardarAnotacion.setOnClickListener(v -> guardarAnotacion());

        // Ir al historial de anotaciones
        bhistorial.setOnClickListener(view -> clickHistorial());
    }

    /**
     * Guarda la anotación en SharedPreferences y Firebase.
     */
    private void guardarAnotacion() {
        String texto = etTextoAnotacion.getText().toString();

        // Validar que el campo de texto no esté vacío
        if (texto.isEmpty()) {
            Toast.makeText(this, "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        // Obtener el correo del usuario desde SharedPreferences
        String createdBy = sharedPreferences.getString("email", "Desconocido");

        // Crear la anotación como un string con formato: texto - creado por
        String anotacion = "Texto: " + texto + " - Por: " + createdBy;

        // Guardar anotación en SharedPreferences
        guardarAnotacionEnSharedPreferences(anotacion);

        // Obtener el usuario actual de Firebase
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            // Usar el nombre del usuario o el email si el nombre no está disponible
            createdBy = currentUser.getDisplayName() != null ? currentUser.getDisplayName() : currentUser.getEmail();
        }

        // Crear el objeto Anotacion para Firebase
        Anotacion anotacionFirebase = new Anotacion(null, texto, null, null, createdBy, false);

        // Guardar la anotación en Firebase
        guardarAnotacionEnFirebase(anotacionFirebase);
    }



    /**
     * Guarda la anotación en Firebase.
     */
    private void guardarAnotacionEnFirebase(Anotacion anotacion) {
        FirebaseHelper firebaseHelper = new FirebaseHelper();
        firebaseHelper.addAnotacion(anotacion, new FirebaseHelper.DataStatus() {
            @Override
            public void DataIsLoaded(List<?> data) {}

            @Override
            public void DataIsInserted() {
                Toast.makeText(AddAnotacionAdminActivity.this, "Anotación guardada en Firebase", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void DataIsUpdated() {}

            @Override
            public void DataIsDeleted() {}

            @Override
            public void onError(String errorMessage) {
                Toast.makeText(AddAnotacionAdminActivity.this, "Error al guardar la anotación: " + errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Guarda la anotación en SharedPreferences.
     */
    private void guardarAnotacionEnSharedPreferences(String anotacion) {
        // Obtener las anotaciones previamente guardadas (si hay alguna)
        String anotacionesGuardadas = sharedPreferences.getString("anotaciones", "");

        // Si ya hay anotaciones guardadas, agregamos la nueva
        if (!anotacionesGuardadas.isEmpty()) {
            anotacionesGuardadas += "|" + anotacion; // Usamos "|" como delimitador
        } else {
            anotacionesGuardadas = anotacion; // Si no hay anotaciones, simplemente guardamos la primera
        }

        // Guardar las anotaciones concatenadas
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("anotaciones", anotacionesGuardadas);
        editor.apply();

        Toast.makeText(AddAnotacionAdminActivity.this, "Anotación guardada en SharedPreferences", Toast.LENGTH_SHORT).show();
    }

    /**
     * Ir a la actividad del historial de anotaciones.
     */
    public void clickHistorial() {
        Intent intent = new Intent(this, HistorialAnotacionesActivity.class);
        startActivity(intent);
    }
}