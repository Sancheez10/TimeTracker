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

public class AddGroupFragment extends DialogFragment {

    private EditText etNombreGrupo;
    private Button btnGuardar;

    private FirebaseFirestore db;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_add_group_fragment, container, false);

        etNombreGrupo = view.findViewById(R.id.etNombreGrupo);
        btnGuardar = view.findViewById(R.id.btnGuardar);

        db = FirebaseFirestore.getInstance();

        btnGuardar.setOnClickListener(v -> guardarGrupo());

        return view;
    }

    private void guardarGrupo() {
        // Obtener el nombre del grupo ingresado por el usuario
        String nombreGrupo = etNombreGrupo.getText().toString();

        // Verificar si se ingresó un nombre de grupo válido
        if (nombreGrupo.isEmpty()) {
            // Si el nombre del grupo está vacío, mostrar un mensaje de error y salir del método
            etNombreGrupo.setError("Ingrese un nombre de grupo válido");
            return;
        }

        // Crear un mapa con los datos del grupo
        Map<String, Object> grupo = new HashMap<>();
        grupo.put("nombre", nombreGrupo);

        // Agregar el grupo a Firestore
        db.collection("grupos")
                .add(grupo)
                .addOnSuccessListener(documentReference -> {
                    // Éxito al guardar en Firestore
                    dismiss(); // Cerrar la ventana flotante
                })
                .addOnFailureListener(e -> {
                    // Error al guardar en Firestore
                    String errorMessage = "Error al guardar el grupo: " + e.getMessage();
                    Toast.makeText(getContext(), errorMessage, Toast.LENGTH_SHORT).show();
                });
    }
}
