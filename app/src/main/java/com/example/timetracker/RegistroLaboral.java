package com.example.timetracker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.SearchView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class RegistroLaboral extends AppCompatActivity {

    private RecyclerView recyclerViewFichajes;
    private RegistroLaboralAdapter adapter;
    private List<Fichaje> fichajeList, fichajeListFull;
    private SearchView searchView;
    private boolean isAdmin;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private String currentUserUid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro_laboral);

        Toolbar toolbar = findViewById(R.id.toolbar_registro_laboral);
        setSupportActionBar(toolbar);

        recyclerViewFichajes = findViewById(R.id.recyclerViewFichajes);
        recyclerViewFichajes.setLayoutManager(new LinearLayoutManager(this));
        searchView = findViewById(R.id.searchView);
        Button btnBack = findViewById(R.id.btn_back);
        btnBack.setOnClickListener(view -> finish());

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Obtener el UID del usuario autenticado
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            currentUserUid = currentUser.getUid();
            Toast.makeText(this, "Usuario autenticado: " + currentUserUid, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Error: No hay usuario autenticado", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        // Inicializar listas y adaptador
        fichajeList = new ArrayList<>();
        fichajeListFull = new ArrayList<>();
        adapter = new RegistroLaboralAdapter(fichajeList);
        recyclerViewFichajes.setAdapter(adapter);

        // Verificar si es administrador
        db.collection("workers").document(currentUserUid).get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                isAdmin = documentSnapshot.getBoolean("isAdmin");
                if (isAdmin) {
                    searchView.setVisibility(View.VISIBLE);
                    loadAllFichajes();
                } else {
                    loadUserFichajes(currentUserUid);
                }
            }
        }).addOnFailureListener(e ->
                Toast.makeText(this, "Error al obtener la información del usuario: " + e.getMessage(), Toast.LENGTH_SHORT).show()
        );

        // Configurar búsqueda en tiempo real
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filterFichajes(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterFichajes(newText);
                return false;
            }
        });
    }

    private void loadUserFichajes(String userId) {
        db.collection("Timer").document("Timer").collection(userId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        fichajeList.clear();
                        fichajeListFull.clear();

                        if (task.getResult().isEmpty()) {
                            Toast.makeText(this, "No hay registros para este usuario", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        for (QueryDocumentSnapshot fechaDoc : task.getResult()) {
                            String fecha = fechaDoc.getId(); // El ID del documento es la fecha (ej: "2025-02-03")

                            Fichaje fichaje = new Fichaje(
                                    fechaDoc.getString("entry_date"),
                                    fechaDoc.getString("entry_address"),
                                    fechaDoc.getString("entry_time"),
                                    fechaDoc.getString("exit_time"),
                                    fechaDoc.getString("total_hours_worked")
                            );

                            fichajeList.add(fichaje);
                            fichajeListFull.add(fichaje);
                        }

                        adapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(this, "Error al cargar los fichajes: " + task.getException(), Toast.LENGTH_SHORT).show();
                    }
                });
    }


    private void loadAllFichajes() {
        db.collection("Timer").document("Timer").collection(currentUserUid)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        fichajeList.clear();
                        fichajeListFull.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Fichaje fichaje = new Fichaje(
                                    document.getString("date"),
                                    document.getString("location"),
                                    document.getString("checkInTime"),
                                    document.getString("checkOutTime"),
                                    document.getString("userId")
                            );
                            fichajeList.add(fichaje);
                            fichajeListFull.add(fichaje);
                        }
                        adapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(this, "Error al cargar los fichajes", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void filterFichajes(String query) {
        List<Fichaje> filteredList = new ArrayList<>();
        for (Fichaje fichaje : fichajeListFull) {
            if (fichaje.getDate().contains(query) || fichaje.getLocation().toLowerCase().contains(query.toLowerCase())) {
                filteredList.add(fichaje);
            }
        }
        fichajeList.clear();
        fichajeList.addAll(filteredList);
        adapter.notifyDataSetChanged();
    }
}
