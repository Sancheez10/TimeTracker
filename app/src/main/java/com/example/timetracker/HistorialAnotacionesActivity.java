package com.example.timetracker;

import android.os.Bundle;

import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class HistorialAnotacionesActivity extends AppCompatActivity {

    private ListView lvAnotaciones;
    private Button btnAtras, btnRefrescar, btnMarcarComoVistas;
    private ArrayAdapter<String> adapter;
    private List<String> anotacionesList;
    private List<String> anotacionesIds;

    private FirebaseFirestore db;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historial_anotaciones);

        // Configuración de la Toolbar
        Toolbar toolbar = findViewById(R.id.toolbarHistorial);
        setSupportActionBar(toolbar);

        // Inicialización de Firebase
        db = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        // Inicialización de las vistas
        lvAnotaciones = findViewById(R.id.listViewAnotaciones);
        btnAtras = findViewById(R.id.btnAtras);
        btnRefrescar = findViewById(R.id.btnRefrescar);
        btnMarcarComoVistas = findViewById(R.id.btnMarcarComoVistas);

        // Inicialización de las listas
        anotacionesList = new ArrayList<>();
        anotacionesIds = new ArrayList<>();

        // Configuración del adaptador
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, anotacionesList);
        lvAnotaciones.setAdapter(adapter);

        // Listener para el botón "Atrás"
        btnAtras.setOnClickListener(v -> finish());

        // Listener para el botón "Refrescar"
        btnRefrescar.setOnClickListener(v -> cargarAnotaciones());

        // Listener para el botón "Marcar todas como vistas"
        btnMarcarComoVistas.setOnClickListener(v -> marcarTodasComoVistas());

        // Listener para cada item del ListView para permitir la eliminación individual
        lvAnotaciones.setOnItemClickListener((parent, view, position, id) -> eliminarAnotacion(position));

        // Cargar las anotaciones al iniciar la actividad
        cargarAnotaciones();
    }

    private void cargarAnotaciones() {
        // Obtener el UID del usuario actual
        String userId = firebaseAuth.getCurrentUser().getUid();

        // Limpiar la lista antes de cargar nuevos datos
        anotacionesList.clear();
        anotacionesIds.clear();

        // Consultar Firestore para obtener las anotaciones del usuario
        db.collection("anotaciones_users")
                .document(userId)
                .collection("anotaciones")
                .whereEqualTo("isViewed", false)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            // Obtener el texto y la ID de la anotación
                            String texto = document.getString("texto");
                            String id = document.getId();

                            // Añadir la anotación y su ID a las listas
                            anotacionesList.add(texto);
                            anotacionesIds.add(id);
                        }
                        // Notificar al adaptador que los datos han cambiado
                        adapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(this, "Error al cargar las anotaciones", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void marcarTodasComoVistas() {
        // Obtener el UID del usuario actual
        String userId = firebaseAuth.getCurrentUser().getUid();

        // Actualizar cada anotación para marcarla como vista
        for (String id : anotacionesIds) {
            db.collection("anotaciones_users")
                    .document(userId)
                    .collection("anotaciones")
                    .document(id)
                    .update("isViewed", true)
                    .addOnSuccessListener(aVoid -> {
                        // Opción: mostrar un mensaje de éxito
                    })
                    .addOnFailureListener(e -> Toast.makeText(this, "Error al marcar la anotación como vista", Toast.LENGTH_SHORT).show());
        }

        // Limpiar la lista local de anotaciones y notificar al adaptador
        anotacionesList.clear();
        anotacionesIds.clear();
        adapter.notifyDataSetChanged();
        Toast.makeText(this, "Todas las anotaciones han sido marcadas como vistas", Toast.LENGTH_SHORT).show();
    }

    private void eliminarAnotacion(int position) {
        // Obtener el UID del usuario actual
        String userId = firebaseAuth.getCurrentUser().getUid();

        // Obtener la ID de la anotación seleccionada
        String id = anotacionesIds.get(position);

        // Eliminar la anotación de Firestore
        db.collection("anotaciones_users")
                .document(userId)
                .collection("anotaciones")
                .document(id)
                .update("isViewed", true)
                .addOnSuccessListener(aVoid -> {
                    // Eliminar la anotación de la lista local y actualizar el adaptador
                    anotacionesList.remove(position);
                    anotacionesIds.remove(position);
                    adapter.notifyDataSetChanged();
                    Toast.makeText(this, "Anotación marcada como vista", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Error al marcar la anotación como vista", Toast.LENGTH_SHORT).show());
    }
}
