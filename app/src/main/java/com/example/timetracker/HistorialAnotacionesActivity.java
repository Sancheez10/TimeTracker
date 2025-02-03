package com.example.timetracker;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

public class HistorialAnotacionesActivity extends AppCompatActivity {
    private ListView listView;
    private FirebaseAuth mAuth;
    private FirebaseHelper firebaseHelper;

    private SharedPreferences sharedPreferences;

    private List<Anotacion> anotaciones; // Lista de objetos Anotacion
    private List<String> anotacionesText; // Lista de textos para mostrar en el ListView

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historial_anotaciones);

        // Inicializar FirebaseAuth y FirebaseHelper
        mAuth = FirebaseAuth.getInstance();
        firebaseHelper = new FirebaseHelper();

        // Inicializar ListView
        listView = findViewById(R.id.listViewAnotaciones);

        // Cargar anotaciones y mostrarlas
        cargarAnotaciones();

        // Configurar clic en elementos del ListView
        listView.setOnItemClickListener((parent, view, position, id) -> {
            Anotacion anotacionSeleccionada = anotaciones.get(position);
            eliminarAnotacionSiEsPropia(anotacionSeleccionada);
        });
    }

    private void cargarAnotaciones() {
        // Obtener usuario actual
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "No se ha iniciado sesión.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Cargar anotaciones desde Firebase
        firebaseHelper.getAllAnotaciones(new FirebaseHelper.DataStatus() {
            @Override
            public void DataIsLoaded(List<?> data) {
                anotaciones = new ArrayList<>();
                anotacionesText = new ArrayList<>();

                for (Object obj : data) {
                    Anotacion anotacion = (Anotacion) obj;

                    // Agregar texto de la anotación con el creador
                    anotaciones.add(anotacion);
                    anotacionesText.add("Texto: " + anotacion.getTexto() + " - Por: " + anotacion.getCreatedBy());
                }

                // Mostrar las anotaciones en el ListView
                ArrayAdapter<String> adapter = new ArrayAdapter<>(HistorialAnotacionesActivity.this,
                        android.R.layout.simple_list_item_1, anotacionesText);
                listView.setAdapter(adapter);
            }

            @Override
            public void DataIsInserted() {}

            @Override
            public void DataIsUpdated() {}

            @Override
            public void DataIsDeleted() {}

            @Override
            public void onError(String errorMessage) {
                Toast.makeText(HistorialAnotacionesActivity.this, "Error al cargar anotaciones: " + errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void eliminarAnotacionSiEsPropia(Anotacion anotacion) {
        // Verificar si el usuario actual es el creador de la anotación
        sharedPreferences = getSharedPreferences("workers_pref", Context.MODE_PRIVATE);
        boolean isAdmin = sharedPreferences.getBoolean("isAdmin", false);
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null && anotacion.getCreatedBy().equals(currentUser.getEmail())) {
            // Eliminar la anotación
            firebaseHelper.deleteAnotacion(anotacion.getId(), new FirebaseHelper.DataStatus() {
                @Override
                public void DataIsLoaded(List<?> data) {}

                @Override
                public void DataIsInserted() {}

                @Override
                public void DataIsUpdated() {}

                @Override
                public void DataIsDeleted() {
                    Toast.makeText(HistorialAnotacionesActivity.this, "Anotación eliminada", Toast.LENGTH_SHORT).show();
                    cargarAnotaciones(); // Recargar las anotaciones después de eliminar
                }

                @Override
                public void onError(String errorMessage) {
                    Toast.makeText(HistorialAnotacionesActivity.this, "Error al eliminar: " + errorMessage, Toast.LENGTH_SHORT).show();
                }
            });
        } else if(isAdmin) {
            firebaseHelper.deleteAnotacion(anotacion.getId(), new FirebaseHelper.DataStatus() {
                @Override
                public void DataIsLoaded(List<?> data) {}

                @Override
                public void DataIsInserted() {}

                @Override
                public void DataIsUpdated() {}

                @Override
                public void DataIsDeleted() {
                    Toast.makeText(HistorialAnotacionesActivity.this, "Anotación eliminada", Toast.LENGTH_SHORT).show();
                    cargarAnotaciones(); // Recargar las anotaciones después de eliminar
                }

                @Override
                public void onError(String errorMessage) {
                    Toast.makeText(HistorialAnotacionesActivity.this, "Error al eliminar: " + errorMessage, Toast.LENGTH_SHORT).show();
                }
            });

        }
        else{
            Toast.makeText(this, "No puedes eliminar esta anotación.", Toast.LENGTH_SHORT).show();
        }
    }
}
