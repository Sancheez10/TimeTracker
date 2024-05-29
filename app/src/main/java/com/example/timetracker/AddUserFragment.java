package com.example.timetracker;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class AddUserFragment extends Fragment {
    private EditText etNombre, etApellido, etCorreo, etTelefono;
    private Button btnGuardar;

    // Referencia a la base de datos de Firebase
    private DatabaseReference databaseRef;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_add_user_fragment, container, false);

        etNombre = view.findViewById(R.id.etNombre);
        etApellido = view.findViewById(R.id.etApellido);
        etCorreo = view.findViewById(R.id.etCorreo);
        etTelefono = view.findViewById(R.id.etTelefono);
        btnGuardar = view.findViewById(R.id.btnGuardar);

        // Obtener referencia a la base de datos de Firebase
        databaseRef = FirebaseDatabase.getInstance().getReference("users");

        btnGuardar.setOnClickListener(v -> {
            // Obtener los datos del usuario
            String nombre = etNombre.getText().toString();
            String apellido = etApellido.getText().toString();
            String correo = etCorreo.getText().toString();
            String telefono = etTelefono.getText().toString();

            // Crear un mapa con los datos del usuario
            Map<String, Object> userMap = new HashMap<>();
            userMap.put("nombre", nombre);
            userMap.put("apellido", apellido);
            userMap.put("correo", correo);
            userMap.put("telefono", telefono);

            // Generar un ID único para el usuario
            String userId = databaseRef.child("users").push().getKey();

            // Guardar el usuario en la base de datos
            databaseRef.child("users").child(userId).setValue(userMap)
                    .addOnSuccessListener(aVoid -> {
                        // Mostrar un mensaje de éxito
                        Toast.makeText(getContext(), "Usuario guardado correctamente", Toast.LENGTH_SHORT).show();

                        // Limpiar los campos de entrada
                        etNombre.setText("");
                        etApellido.setText("");
                        etCorreo.setText("");
                        etTelefono.setText("");
                    })
                    .addOnFailureListener(e -> {
                        // Mostrar un mensaje de error
                        Toast.makeText(getContext(), "Error al guardar el usuario", Toast.LENGTH_SHORT).show();
                        Log.e("AddUserFragment", "Error al guardar el usuario: " + e.getMessage());
                    });
        });

        return view;
    }
}
