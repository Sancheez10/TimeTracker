package com.example.timetracker;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class HistorialAnotacionesActivity extends AppCompatActivity {

    private ListView lvAnotaciones;
    private Button btnAtras, btnRefrescar, btnMarcarComoVistas;
    private ArrayAdapter<String> adapter;
    private List<String> anotacionesList;
    private FirebaseFirestore db;
    private SharedPreferences sharedPreferences;
    private String userEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historial_anotaciones);

        // Configuración de la Toolbar
        Toolbar toolbar = findViewById(R.id.toolbarHistorial);
        setSupportActionBar(toolbar);

        // Inicialización de las vistas
        lvAnotaciones = findViewById(R.id.listViewAnotaciones);
        btnAtras = findViewById(R.id.btnAtras);
        btnRefrescar = findViewById(R.id.btnRefrescar);
        btnMarcarComoVistas = findViewById(R.id.btnMarcarComoVistas);

        // Inicialización de las listas
        anotacionesList = new ArrayList<>();

        // Configuración del adaptador
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, anotacionesList);
        lvAnotaciones.setAdapter(adapter);

        // Inicialización de Firebase Firestore
        db = FirebaseFirestore.getInstance();

        // Obtener SharedPreferences para obtener el correo del usuario
        sharedPreferences = getSharedPreferences("workers_pref", MODE_PRIVATE);
        userEmail = sharedPreferences.getString("email", "Desconocido");

        // Listener para el botón "Atrás"
        btnAtras.setOnClickListener(v -> finish());

        // Listener para el botón "Refrescar"
        btnRefrescar.setOnClickListener(v -> cargarAnotaciones());

        // Cargar las anotaciones al iniciar la actividad
        cargarAnotaciones();
    }

    /**
     * Método para cargar las anotaciones desde Firebase Firestore.
     */
    private void cargarAnotaciones() {
        // Limpiar la lista antes de cargar nuevos datos
        anotacionesList.clear();

        // Verificar si se obtuvo el correo del usuario
        if (userEmail.equals("Desconocido")) {
            Toast.makeText(this, "Error al obtener el correo del usuario", Toast.LENGTH_SHORT).show();
            return;
        }

        // Realizar la consulta a Firebase Firestore para obtener las anotaciones del usuario actual
        db.collection("anotaciones_users")
                .document(userEmail)
                .collection("anotaciones")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Recorrer las anotaciones y agregarlas a la lista
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Anotacion anotacion = document.toObject(Anotacion.class);
                            anotacionesList.add(anotacion.getTexto() + "\nFecha: " + anotacion.getFechaHora());
                        }

                        // Notificar al adaptador que los datos han cambiado
                        adapter.notifyDataSetChanged();

                        if (anotacionesList.isEmpty()) {
                            Toast.makeText(this, "No hay anotaciones para mostrar", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(this, "Error al cargar anotaciones", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
