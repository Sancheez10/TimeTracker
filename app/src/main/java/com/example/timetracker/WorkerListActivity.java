package com.example.timetracker;

import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class WorkerListActivity extends AppCompatActivity {

    private DatabaseReference db;
    private ListView listViewWorkers;
    private ArrayAdapter<String> adapter;
    private List<String> workerList;
    private Button btnBack;
    private SearchView searchView;

    private static final String TAG = "WorkerListActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_worker_list);

        Log.d(TAG, "onCreate: starting");

        Toolbar toolbar = findViewById(R.id.toolbar_worker_list);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Lista de Trabajadores");

        listViewWorkers = findViewById(R.id.listViewWorkers);
        searchView = findViewById(R.id.searchView);
        btnBack = findViewById(R.id.btn_back);

        workerList = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, workerList);
        listViewWorkers.setAdapter(adapter);

        db = FirebaseDatabase.getInstance().getReference().child("workers");

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
        Log.d(TAG, "loadWorkers: querying database");
        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                workerList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String email = snapshot.child("email").getValue(String.class);
                    if (email != null) {
                        workerList.add(email);
                        Log.d(TAG, "Added email: " + email);
                    } else {
                        Log.d(TAG, "Email is null for snapshot " + snapshot.getKey());
                    }
                }
                Log.d(TAG, "Total workers added: " + workerList.size());
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "loadWorkers:onCancelled", databaseError.toException());
                Toast.makeText(WorkerListActivity.this, "Error al cargar los trabajadores", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
