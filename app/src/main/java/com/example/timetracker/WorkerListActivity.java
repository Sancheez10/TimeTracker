package com.example.timetracker;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class WorkerListActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private ListView listViewWorkers;
    private ArrayAdapter<String> adapter;
    private List<String> workerList;
    private Button btnBack;
    private SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_worker_list);

        Toolbar toolbar = findViewById(R.id.toolbar_worker_list);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Lista de Trabajadores");

        listViewWorkers = findViewById(R.id.listViewWorkers);
        searchView = findViewById(R.id.searchView);
        btnBack = findViewById(R.id.btn_back);

        workerList = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, workerList);
        listViewWorkers.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();

        loadWorkers();

        // Configurar el Listener para el botón "Volver"
        btnBack.setOnClickListener(v -> onBackPressed());

        // Configurar el Listener para el SearchView
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                return false;
            }
        });
    }

    private void loadWorkers() {
        db.collection("workers")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String worker = "ID: " + document.getString("id") +
                                    "\nName: " + document.getString("name") +
                                    "\nSurname: " + document.getString("surname");
                            workerList.add(worker);
                        }
                        adapter.notifyDataSetChanged();
                    } else {
                        // Manejar el fallo
                        Toast.makeText(WorkerListActivity.this, "Error al cargar los trabajadores", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
