package com.example.timetracker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.SearchView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class RegistroLaboral extends AppCompatActivity {

    private RecyclerView recyclerViewFichajes;
    private RegistroLaboralAdapter adapter;
    private List<Fichaje> fichajeList;
    private SearchView searchView;
    private boolean isAdmin;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private SharedPreferences sharedPreferences;
    private String currentUserUid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro_laboral);

        Toolbar toolbar = findViewById(R.id.toolbar_registro_laboral);
        setSupportActionBar(toolbar);

        recyclerViewFichajes = findViewById(R.id.recyclerViewFichajes);
        recyclerViewFichajes.setLayoutManager(new LinearLayoutManager(this));
        sharedPreferences = getSharedPreferences("workers_pref", Context.MODE_PRIVATE);


        searchView = findViewById(R.id.searchView);

        Button btnBack = findViewById(R.id.btn_back);
        btnBack.setOnClickListener(view -> finish());

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Inicializar la lista y el adaptador
        fichajeList = new ArrayList<>();
        adapter = new RegistroLaboralAdapter(fichajeList);
        recyclerViewFichajes.setAdapter(adapter);

        // Verificar si el usuario es administrador
        currentUserUid = sharedPreferences.getString("userId", "Usuario");

        db.collection("workers").document(currentUserUid).get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                isAdmin = documentSnapshot.getBoolean("isAdmin");
                if (isAdmin) {
                    searchView.setVisibility(View.VISIBLE);
                    setupAdminSearch();
                } else {
                    loadUserFichajes(currentUserUid);
                }
            }
        }).addOnFailureListener(e -> {
            Toast.makeText(this, "Error al obtener la información del usuario", Toast.LENGTH_SHORT).show();
        });
    }

    private void loadUserFichajes(String userId) {
        db.collection("Timer")
                .document("Timer")
                .collection(currentUserUid)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Fichaje fichaje = new Fichaje(
                                    document.getString("date"),
                                    document.getString("location"),
                                    document.getString("checkInTime"),
                                    document.getString("checkOutTime")
                            );
                            fichajeList.add(fichaje);
                        }
                        adapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(this, "Error al cargar los fichajes", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void setupAdminSearch() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchFichajes(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchFichajes(newText);
                return false;
            }
        });

        // Cargar todos los fichajes inicialmente para el administrador
        db.collection("Timer")
                .document("Timer")
                .collection(currentUserUid)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Fichaje fichaje = new Fichaje(
                                    document.getString("date"),
                                    document.getString("location"),
                                    document.getString("checkInTime"),
                                    document.getString("checkOutTime"),
                                    document.getString("userId")
                            );
                            fichajeList.add(fichaje);
                        }
                        adapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(this, "Error al cargar los fichajes", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void searchFichajes(String query) {
        db.collection("Timer")
                .document("Timer")
                .collection(currentUserUid)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        fichajeList.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Fichaje fichaje = new Fichaje(
                                    document.getString("date"),
                                    document.getString("location"),
                                    document.getString("checkInTime"),
                                    document.getString("checkOutTime"),
                                    document.getString("userId")
                            );
                            fichajeList.add(fichaje);
                        }
                        adapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(this, "Error al buscar los fichajes", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
