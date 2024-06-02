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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddGroupFragment extends DialogFragment {

    private EditText etNombreGrupo;
    private Button btnGuardar;

    private FirebaseHelper firebaseHelper;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_add_group_fragment, container, false);

        etNombreGrupo = view.findViewById(R.id.etNombreGrupo);
        btnGuardar = view.findViewById(R.id.btnGuardar);

        firebaseHelper = new FirebaseHelper();

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

        // Agregar el grupo a Firebase usando FirebaseHelper
        firebaseHelper.addGroup(grupo, new FirebaseHelper.DataStatus() {
            @Override
            public void DataIsLoaded(List<?> data) {
                // No utilizado en este contexto
            }

            @Override
            public void DataIsInserted() {
                // Éxito al guardar en Firebase
                dismiss(); // Cerrar la ventana flotante
            }

            @Override
            public void DataIsUpdated() {
                // No utilizado en este contexto
            }

            @Override
            public void DataIsDeleted() {
                // No utilizado en este contexto
            }

            @Override
            public void onError(String errorMessage) {
                // Error al guardar en Firebase
                Toast.makeText(getContext(), "Error al guardar el grupo: " + errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
