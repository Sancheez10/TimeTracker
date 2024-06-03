package com.example.timetracker;

import android.os.Bundle;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class HistorialAnotacionesActivity extends AppCompatActivity implements AnnotationsAdapter.OnDeleteClickListener {

    private ListView lvAnotaciones;
    private FirebaseFirestore db;
    private List<Anotacion> anotaciones;
    private AnnotationsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historial_anotaciones);

        Toolbar toolbar = findViewById(R.id.toolbar5);
        setSupportActionBar(toolbar);

        lvAnotaciones = findViewById(R.id.lvAnotaciones);
        db = FirebaseFirestore.getInstance();
        anotaciones = new ArrayList<>();

        adapter = new AnnotationsAdapter(this, R.layout.item_anotacion, anotaciones, this);
        lvAnotaciones.setAdapter(adapter);

        cargarAnotaciones();
    }

    private void cargarAnotaciones() {
        db.collection("anotaciones").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    Anotacion anotacion = document.toObject(Anotacion.class);
                    anotacion.setId(document.getId());
                    anotaciones.add(anotacion);
                }
                adapter.notifyDataSetChanged();
            } else {
                Toast.makeText(HistorialAnotacionesActivity.this, "Error al obtener las anotaciones", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onDeleteClick(Anotacion anotacion) {
        db.collection("anotaciones").document(anotacion.getId()).delete().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(HistorialAnotacionesActivity.this, "Anotación eliminada", Toast.LENGTH_SHORT).show();
                anotaciones.remove(anotacion);
                adapter.notifyDataSetChanged();
            } else {
                Toast.makeText(HistorialAnotacionesActivity.this, "Error al eliminar la anotación", Toast.LENGTH_SHORT).show();
            }
        });
    }
}


