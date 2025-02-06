package com.example.timetracker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SearchView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class RegistroLaboral extends AppCompatActivity {

    private RecyclerView recyclerViewFichajes;
    private RegistroLaboralAdapter adapter;
    private List<String> fichajeList, fichajeListFull;
    private SearchView searchView;
    private boolean isAdmin;
    private FirebaseAuth mAuth;
    private DatabaseReference dbRef;
    private String currentUserUid;

    private SharedPreferences sharedPreferences;

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

        sharedPreferences = getSharedPreferences("workers_pref", Context.MODE_PRIVATE);
        String email = sharedPreferences.getString("email", "Usuario");


        mAuth = FirebaseAuth.getInstance();
        dbRef = FirebaseDatabase.getInstance().getReference();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            currentUserUid = currentUser.getUid();
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

        boolean isAdmin = sharedPreferences.getBoolean("isAdmin", false);

        if (isAdmin) {
            searchView.setVisibility(View.VISIBLE);
            loadAllFichajes();
        } else {
            searchView.setVisibility(View.VISIBLE);
            loadUserFichajes(currentUserUid);
        }

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
        dbRef.child("Timer").child("Timer").child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                fichajeList.clear();
                fichajeListFull.clear();

                if (!snapshot.exists()) {
                    Log.d("Firebase", "No hay datos en Timer para el usuario: " + userId);
                    Toast.makeText(RegistroLaboral.this, "No hay registros para este usuario", Toast.LENGTH_SHORT).show();
                    return;
                }

                for (DataSnapshot fichajeSnap : snapshot.getChildren()) {
                    String fecha = fichajeSnap.getKey();  // Clave es la fecha (ej. "2025-02-04")
                    String entryTime = fichajeSnap.child("entry_time").getValue(String.class);
                    String entryAddress = fichajeSnap.child("entry_address").getValue(String.class);
                    String exitTime = fichajeSnap.child("exit_time").getValue(String.class);
                    String totalHours = fichajeSnap.child("total_hours_worked").getValue(String.class);

                    if (totalHours != null && !totalHours.contains("-")) { // Evitar valores incorrectos
                        String entry = "Fecha: " + fecha +
                                "\nDirección: " + entryAddress +
                                "\nEntrada: " + entryTime +
                                "\nSalida: " + exitTime +
                                "\nHoras trabajadas: " + totalHours + " \n";
                        fichajeList.add(entry);
                        fichajeListFull.add(entry);
                    }
                }

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.e("Firebase", "Error al obtener fichajes", error.toException());
                Toast.makeText(RegistroLaboral.this, "Error al cargar los fichajes", Toast.LENGTH_SHORT).show();
            }
        });
    }





    private void loadAllFichajes() {
        dbRef.child("Timer").child("Timer").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                fichajeList.clear();
                fichajeListFull.clear();

                if (!snapshot.exists()) {
                    Log.d("Firebase", "No hay datos en Timer");
                    return;
                }

                for (DataSnapshot userSnap : snapshot.getChildren()) {
                    String userId = userSnap.getKey(); // UID del usuario

                    // Obtener el nombre del usuario antes de cargar los fichajes
                    dbRef.child("workers").child(userId).child("email").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot nameSnapshot) {
                            String userName = nameSnapshot.getValue(String.class);
                            if (userName == null) userName = "Usuario desconocido";

                            for (DataSnapshot fichajeSnap : userSnap.getChildren()) {
                                String fecha = fichajeSnap.getKey(); // Fecha del fichaje
                                String entryAddress = fichajeSnap.child("entry_address").getValue(String.class);
                                String entryTime = fichajeSnap.child("entry_time").getValue(String.class);
                                String exitTime = fichajeSnap.child("exit_time").getValue(String.class);
                                String totalHours = fichajeSnap.child("total_hours_worked").getValue(String.class);

                                if (totalHours != null && !totalHours.contains("-")) { // Filtrar valores inválidos
                                    String entry = "Usuario: " + userName +  // Aquí ponemos el nombre en vez del UID
                                            "\nDirección: " + entryAddress +
                                            "\nFecha: " + fecha +
                                            "\nEntrada: " + entryTime +
                                            "\nSalida: " + exitTime +
                                            "\nHoras trabajadas: " + totalHours + "\n";
                                    fichajeList.add(entry);
                                    fichajeListFull.add(entry);
                                }
                            }

                            adapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onCancelled(DatabaseError error) {
                            Log.e("Firebase", "Error al obtener nombre del usuario", error.toException());
                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.e("Firebase", "Error al obtener fichajes", error.toException());
                Toast.makeText(RegistroLaboral.this, "Error al cargar los fichajes", Toast.LENGTH_SHORT).show();
            }
        });
    }




    private void filterFichajes(String query) {
        List<String> filteredList = new ArrayList<>();
        for (String fichaje : fichajeListFull) {
            if (fichaje.contains(query)) {
                filteredList.add(fichaje);
            }
        }
        fichajeList.clear();
        fichajeList.addAll(filteredList);
        adapter.notifyDataSetChanged();
    }
}
