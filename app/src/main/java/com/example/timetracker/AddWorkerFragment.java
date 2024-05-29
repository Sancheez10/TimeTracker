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

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class AddWorkerFragment extends DialogFragment {

    private EditText etNombre, etApellidos, etEmail, etTelefono, etNIE_NIF;
    private Button btnGuardar;

    private FirebaseFirestore db;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_add_worker_fragment, container, false);

        etNombre = view.findViewById(R.id.etNombre);
        etApellidos = view.findViewById(R.id.etApellidos);
        etEmail = view.findViewById(R.id.etEmail);
        etTelefono = view.findViewById(R.id.etTelefono);
        etNIE_NIF = view.findViewById(R.id.etNIE_NIF);
        btnGuardar = view.findViewById(R.id.btnGuardar);

        db = FirebaseFirestore.getInstance();

        btnGuardar.setOnClickListener(v -> guardarTrabajador());

        return view;
    }

    private void guardarTrabajador() {
        // Obtener los datos ingresados por el usuario
        String nombre = etNombre.getText().toString();
        String apellidos = etApellidos.getText().toString();
        String email = etEmail.getText().toString();
        String telefono = etTelefono.getText().toString();
        String nieNif = etNIE_NIF.getText().toString();

        // Crear un mapa con los datos del trabajador
        Map<String, Object> trabajador = new HashMap<>();
        trabajador.put("nombre", nombre);
        trabajador.put("apellidos", apellidos);
        trabajador.put("email", email);
        trabajador.put("telefono", telefono);
        trabajador.put("nie_nif", nieNif);

        // Agregar el trabajador a Firestore
        db.collection("trabajadores").add(trabajador).addOnSuccessListener(documentReference -> {
            // Éxito al guardar en Firestore
            dismiss(); // Cerrar la ventana flotante
        }).addOnFailureListener(e -> {
            // Error al guardar en Firestore
            // Acciones a realizar si la operación falla
            String errorMessage = "Error al guardar el trabajador: " + e.getMessage();
            Toast.makeText(getContext(), errorMessage, Toast.LENGTH_SHORT).show();
        });
    }
}
