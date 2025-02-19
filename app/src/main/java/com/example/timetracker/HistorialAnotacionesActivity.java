package com.example.timetracker;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.app.AlertDialog;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class HistorialAnotacionesActivity extends AppCompatActivity {
    private ListView listView;
    private SearchView searchView;
    private FirebaseAuth mAuth;
    private FirebaseHelper firebaseHelper;

    private SharedPreferences sharedPreferences;
    private List<Anotacion> anotaciones;
    private List<String> anotacionesText;
    private ArrayAdapter<String> adapter;

    private static final String PREF_HIDDEN_ANNOTATIONS = "hidden_annotations_";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historial_anotaciones);

        mAuth = FirebaseAuth.getInstance();
        firebaseHelper = new FirebaseHelper();
        sharedPreferences = getSharedPreferences("workers_pref", Context.MODE_PRIVATE);

        listView = findViewById(R.id.listViewAnotaciones);
        searchView = findViewById(R.id.searchView);

        cargarAnotaciones();

        listView.setOnItemClickListener((parent, view, position, id) -> {
            Anotacion anotacionSeleccionada = anotaciones.get(position);
            manejarClicEnAnotacion(anotacionSeleccionada, position);
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filtrarAnotaciones(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filtrarAnotaciones(newText);
                return false;
            }
        });
    }

    private void manejarClicEnAnotacion(Anotacion anotacion, int position) {
        boolean isAdmin = sharedPreferences.getBoolean("isAdmin", false);
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser == null) return;

        if (isAdmin || anotacion.getCreatedBy().equals(currentUser.getEmail())) {
            confirmarEliminacion(anotacion);
        } else {
            ocultarAnotacionParaUsuario(anotacion.getId(), position);
        }
    }

    private void ocultarAnotacionParaUsuario(String anotacionId, int position) {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) return;

        String userEmail = currentUser.getEmail();
        String prefKey = PREF_HIDDEN_ANNOTATIONS + userEmail;

        Set<String> hiddenAnotations = sharedPreferences.getStringSet(prefKey, new HashSet<>());
        hiddenAnotations.add(anotacionId);
        sharedPreferences.edit().putStringSet(prefKey, hiddenAnotations).apply();

        anotaciones.remove(position);
        anotacionesText.remove(position);
        adapter.notifyDataSetChanged();
    }

    private void cargarAnotaciones() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "No se ha iniciado sesión.", Toast.LENGTH_SHORT).show();
            return;
        }

        String userEmail = currentUser.getEmail();
        String prefKey = PREF_HIDDEN_ANNOTATIONS + userEmail;
        Set<String> hiddenAnotations = sharedPreferences.getStringSet(prefKey, new HashSet<>());

        firebaseHelper.getAllAnotaciones(new FirebaseHelper.DataStatus() {
            @Override
            public void DataIsLoaded(List<?> data) {
                anotaciones = new ArrayList<>();
                anotacionesText = new ArrayList<>();

                for (Object obj : data) {
                    Anotacion anotacion = (Anotacion) obj;

                    if (!hiddenAnotations.contains(anotacion.getId())) {
                        anotaciones.add(anotacion);
                        anotacionesText.add("Texto: " + anotacion.getTexto() + " - Por: " + anotacion.getCreatedBy());
                    }
                }

                adapter = new ArrayAdapter<>(HistorialAnotacionesActivity.this,
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

    private void filtrarAnotaciones(String query) {
        List<String> anotacionesFiltradas = new ArrayList<>();
        for (Anotacion anotacion : anotaciones) {
            String textoCompleto = "Texto: " + anotacion.getTexto() + " - Por: " + anotacion.getCreatedBy();
            if (textoCompleto.toLowerCase().contains(query.toLowerCase())) {
                anotacionesFiltradas.add(textoCompleto);
            }
        }

        adapter.clear();
        adapter.addAll(anotacionesFiltradas);
        adapter.notifyDataSetChanged();
    }

    private void confirmarEliminacion(Anotacion anotacion) {
        new AlertDialog.Builder(this)
                .setTitle("Eliminar anotación")
                .setMessage("¿Estás seguro de que deseas eliminar esta anotación?")
                .setPositiveButton("Eliminar", (dialog, which) -> eliminarAnotacion(anotacion))
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void eliminarAnotacion(Anotacion anotacion) {
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
                cargarAnotaciones();
            }

            @Override
            public void onError(String errorMessage) {
                Toast.makeText(HistorialAnotacionesActivity.this, "Error al eliminar: " + errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
