package com.example.timetracker;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class NombrarSubadmin extends AppCompatActivity {

    private ListView lvSubAdmins;
    private EditText etSearch;
    private RadioGroup rgOptions;
    private CheckBox cbOption;
    private Button btnClose, btnGuardar;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nombrar_subadmin);

        // Inicializar Firestore
        db = FirebaseFirestore.getInstance();

        // Inicializar vistas
        etSearch = findViewById(R.id.etSearch);
        lvSubAdmins = findViewById(R.id.lvSubAdmins);
        etSearch = findViewById(R.id.etSearch);
        rgOptions = findViewById(R.id.rgOptions);
        cbOption = findViewById(R.id.cbOption);
        btnClose = findViewById(R.id.btnClose);
        btnGuardar = findViewById(R.id.btnGuardar);

        db = FirebaseFirestore.getInstance();


        // Configurar ListView
        List<String> subAdminsList = new ArrayList<>();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, subAdminsList);
        lvSubAdmins.setAdapter(adapter);

        // Obtener los primeros 5 trabajadores de la base de datos y mostrarlos en ListView
        obtenerPrimerosTrabajadores(adapter);

        // Método para obtener los trabajadores por nombre
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String searchTerm = s.toString();
                obtenerTrabajadoresPorNombre(adapter, searchTerm);
            }

            @Override
            public void afterTextChanged(Editable s) {}

        });

        btnGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                guardarTrabajadoresSeleccionados();
            }
        });

    }

    private void obtenerPrimerosTrabajadores(ArrayAdapter<String> adapter) {
        db.collection("trabajadores")
                .orderBy("nombre")
                .limit(5)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                // Obtener el nombre del trabajador y añadirlo a la lista
                                String nombre = document.getString("nombre");
                                adapter.add(nombre);
                            }
                            adapter.notifyDataSetChanged();
                        } else {
                            Toast.makeText(NombrarSubadmin.this, "Error al obtener los trabajadores", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void obtenerTrabajadoresPorNombre(ArrayAdapter<String> adapter, String searchTerm) {
        db.collection("trabajadores")
                .orderBy("nombre")
                .startAt(searchTerm)
                .endAt(searchTerm + "\uf8ff")
                .limit(5)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        adapter.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            // Obtener el nombre del trabajador y añadirlo a la lista
                            String nombre = document.getString("nombre");
                            adapter.add(nombre);
                        }
                        adapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(NombrarSubadmin.this, "Error al obtener los trabajadores", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void guardarTrabajadoresSeleccionados() {
        for (int i = 0; i < lvSubAdmins.getCount(); i++) {
            if (lvSubAdmins.isItemChecked(i)) {
                String nombreTrabajador = (String) lvSubAdmins.getItemAtPosition(i);
                marcarTrabajadorComoSubAdmin(nombreTrabajador);
            }
        }
    }

    private void marcarTrabajadorComoSubAdmin(String nombreTrabajador) {
        db.collection("trabajadores")
                .whereEqualTo("nombre", nombreTrabajador)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            // Obtener el ID del trabajador
                            String idTrabajador = document.getId();

                            // Marcar al trabajador como subadministrador
                            db.collection("trabajadores")
                                    .document(idTrabajador)
                                    .update("isSubAdmin", true)
                                    .addOnCompleteListener(task1 -> {
                                        if (task1.isSuccessful()) {
                                            Toast.makeText(NombrarSubadmin.this, "Trabajador marcado como subadministrador: " + nombreTrabajador, Toast.LENGTH_SHORT).show();
                                        } else {
                                            Toast.makeText(NombrarSubadmin.this, "Error al marcar al trabajador como subadministrador", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    } else {
                        Toast.makeText(NombrarSubadmin.this, "Error al obtener los trabajadores", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
