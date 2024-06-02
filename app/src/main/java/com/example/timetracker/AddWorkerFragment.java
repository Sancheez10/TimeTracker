package com.example.timetracker;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import java.util.List;

public class AddWorkerFragment extends DialogFragment {

    private EditText etNombre, etApellidos, etEmail, etTelefono, etNIE_NIF;
    private Button btnGuardar;

    private FirebaseHelper firebaseHelper;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_add_worker_fragment, container, false);

        initializeViews(view);
        firebaseHelper = new FirebaseHelper();

        btnGuardar.setOnClickListener(v -> guardarTrabajador());

        return view;
    }

    private void initializeViews(View view) {
        etNombre = view.findViewById(R.id.etNombre);
        etApellidos = view.findViewById(R.id.etApellidos);
        etEmail = view.findViewById(R.id.etEmail);
        etTelefono = view.findViewById(R.id.etTelefono);
        etNIE_NIF = view.findViewById(R.id.etNIE_NIF);
        btnGuardar = view.findViewById(R.id.btnGuardar);
    }

    private void guardarTrabajador() {
        // Obtener los datos ingresados por el usuario
        String nombre = etNombre.getText().toString();
        String apellidos = etApellidos.getText().toString();
        String email = etEmail.getText().toString();
        String telefono = etTelefono.getText().toString();
        String nieNif = etNIE_NIF.getText().toString();

        // Validar los datos
        if (!validarDatos(nombre, apellidos, email, telefono, nieNif)) {
            return;
        }

        // Crear un nuevo objeto Worker con los datos ingresados
        Worker worker = new Worker(nombre, apellidos, email, telefono, nieNif);

        // Agregar el trabajador utilizando FirebaseHelper
        firebaseHelper.addWorker(worker, new FirebaseHelper.DataStatus() {
            @Override
            public void DataIsLoaded(List<?> data) {
                // No se usa aquí
            }

            @Override
            public void DataIsInserted() {
                // Éxito al guardar en Firebase
                dismiss(); // Cerrar la ventana flotante
                Toast.makeText(getContext(), "Trabajador guardado correctamente", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void DataIsUpdated() {
                // No se usa aquí
            }

            @Override
            public void DataIsDeleted() {
                // No se usa aquí
            }

            @Override
            public void onError(String errorMessage) {
                // Error al guardar en Firebase
                Toast.makeText(getContext(), "Error al guardar el trabajador: " + errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean validarDatos(String nombre, String apellidos, String email, String telefono, String nieNif) {
        if (nombre.isEmpty()) {
            etNombre.setError("Ingrese un nombre válido");
            return false;
        }
        if (apellidos.isEmpty()) {
            etApellidos.setError("Ingrese apellidos válidos");
            return false;
        }
        if (email.isEmpty()) {
            etEmail.setError("Ingrese un email válido");
            return false;
        }
        if (telefono.isEmpty()) {
            etTelefono.setError("Ingrese un teléfono válido");
            return false;
        }
        if (nieNif.isEmpty()) {
            etNIE_NIF.setError("Ingrese un NIE/NIF válido");
            return false;
        }
        return true;
    }
}
