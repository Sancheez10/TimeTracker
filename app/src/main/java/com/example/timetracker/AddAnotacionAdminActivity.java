package com.example.timetracker;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class AddAnotacionAdminActivity extends AppCompatActivity {

    private EditText etTextoAnotacion;
    private Button btnFechaHora, btnAdjuntarArchivo, btnGuardarAnotacion;
    private TextView tvFechaHora;
    private Date fechaHoraSeleccionada;
    private Uri archivoAdjuntoUri;

    private FirebaseHelper firebaseHelper;

    // Launcher para seleccionar archivo
    private final ActivityResultLauncher<Intent> filePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    archivoAdjuntoUri = result.getData().getData();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_anotations_admin);

        etTextoAnotacion = findViewById(R.id.etTextoAnotacion);
        btnFechaHora = findViewById(R.id.btnFechaHora);
        btnAdjuntarArchivo = findViewById(R.id.btnAdjuntarArchivo);
        btnGuardarAnotacion = findViewById(R.id.btnGuardarAnotacion);
        tvFechaHora = findViewById(R.id.tvFechaHora);

        firebaseHelper = new FirebaseHelper();

        btnFechaHora.setOnClickListener(v -> seleccionarFechaHora());
        btnAdjuntarArchivo.setOnClickListener(v -> seleccionarArchivo());
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

    private void seleccionarArchivo() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        filePickerLauncher.launch(intent);
    }

    private void guardarAnotacion() {
        String texto = etTextoAnotacion.getText().toString();
        if (texto.isEmpty() || fechaHoraSeleccionada == null) {
            Toast.makeText(this, "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        String createdBy = "Nombre del Trabajador"; // Debes obtener el nombre del trabajador logueado

        Anotacion anotacion = new Anotacion(null, texto, fechaHoraSeleccionada, null, createdBy);

        // Si se ha seleccionado un archivo, subirlo a Firebase Storage
        if (archivoAdjuntoUri != null) {
            firebaseHelper.uploadFile(archivoAdjuntoUri, new FirebaseHelper.FileUploadCallback() {
                @Override
                public void onFileUploaded(String fileUrl) {
                    anotacion.setFileUrl(fileUrl);
                    firebaseHelper.addAnotacion(anotacion, new FirebaseHelper.DataStatus() {
                        @Override
                        public void DataIsLoaded(List<?> data) {}

                        @Override
                        public void DataIsInserted() {
                            Toast.makeText(AddAnotacionAdminActivity.this, "Anotación guardada", Toast.LENGTH_SHORT).show();
                            finish();
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

                @Override
                public void onError(String errorMessage) {
                    Toast.makeText(AddAnotacionAdminActivity.this, "Error al subir el archivo: " + errorMessage, Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            firebaseHelper.addAnotacion(anotacion, new FirebaseHelper.DataStatus() {
                @Override
                public void DataIsLoaded(List<?> data) {}

                @Override
                public void DataIsInserted() {
                    Toast.makeText(AddAnotacionAdminActivity.this, "Anotación guardada", Toast.LENGTH_SHORT).show();
                    finish();
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
    }
}
