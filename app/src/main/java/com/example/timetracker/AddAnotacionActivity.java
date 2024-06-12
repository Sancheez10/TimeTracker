package com.example.timetracker;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class AddAnotacionActivity extends AppCompatActivity {

    private EditText etTextoAnotacion;
    private Button btnFechaHora, btnGuardarAnotacion;
    private TextView tvFechaHora;
    private Date fechaHoraSeleccionada;
    private FirebaseHelper firebaseHelper;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_anotations_admin);

        etTextoAnotacion = findViewById(R.id.etTextoAnotacion);
        btnFechaHora = findViewById(R.id.btnFechaHora);
        btnGuardarAnotacion = findViewById(R.id.btnGuardarAnotacion);
        tvFechaHora = findViewById(R.id.tvFechaHora);

        firebaseHelper = new FirebaseHelper();
        mAuth = FirebaseAuth.getInstance();

        btnFechaHora.setOnClickListener(v -> seleccionarFechaHora());
        btnGuardarAnotacion.setOnClickListener(v -> guardarAnotacion());
    }

    private void seleccionarFechaHora() {
        final Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            TimePickerDialog timePickerDialog = new TimePickerDialog(this, (timeView, hourOfDay, minute) -> {
                calendar.set(year, month, dayOfMonth, hourOfDay, minute);
                fechaHoraSeleccionada = calendar.getTime();
                tvFechaHora.setText(fechaHoraSeleccionada.toString());
            }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true);
            timePickerDialog.show();
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    private void guardarAnotacion() {
        String texto = etTextoAnotacion.getText().toString();
        if (texto.isEmpty() || fechaHoraSeleccionada == null) {
            Toast.makeText(this, "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseUser currentUser = mAuth.getCurrentUser();
        String createdBy = "Desconocido";

        if (currentUser != null) {
            // Usar el nombre del usuario o el email si el nombre no está disponible
            createdBy = currentUser.getDisplayName() != null ? currentUser.getDisplayName() : currentUser.getEmail();
        }

        Anotacion anotacion = new Anotacion(null, texto, fechaHoraSeleccionada, null, createdBy, false);
        guardarAnotacionEnFirebase(anotacion);
    }

    private void guardarAnotacionEnFirebase(Anotacion anotacion) {
        firebaseHelper.addAnotacion(anotacion, new FirebaseHelper.DataStatus() {
            @Override
            public void DataIsLoaded(List<?> data) {}

            @Override
            public void DataIsInserted() {
                Toast.makeText(AddAnotacionActivity.this, "Anotación guardada", Toast.LENGTH_SHORT).show();
                finish();
            }

            @Override
            public void DataIsUpdated() {}

            @Override
            public void DataIsDeleted() {}

            @Override
            public void onError(String errorMessage) {
                Toast.makeText(AddAnotacionActivity.this, "Error al guardar la anotación: " + errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
