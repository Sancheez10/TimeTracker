package com.example.timetracker;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class NombrarSubadmin extends AppCompatActivity {

    private ListView lvSubAdmins;
    private EditText etSearch;
    private RadioGroup rgOptions;
    private CheckBox cbOption;
    private Button btnClose, btnGuardar;
    private FirebaseHelper firebaseHelper;
    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nombrar_subadmin);

        // Inicializar FirebaseHelper
        firebaseHelper = new FirebaseHelper();

        // Inicializar vistas
        etSearch = findViewById(R.id.etSearch);
        lvSubAdmins = findViewById(R.id.lvSubAdmins);
        rgOptions = findViewById(R.id.rgOptions);
        cbOption = findViewById(R.id.cbOption);
        btnClose = findViewById(R.id.btnClose);
        btnGuardar = findViewById(R.id.btnGuardar);

        // Configurar ListView
        List<String> subAdminsList = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, subAdminsList);
        lvSubAdmins.setAdapter(adapter);

        // Obtener los primeros 5 trabajadores de la base de datos y mostrarlos en ListView
        obtenerPrimerosTrabajadores();

        // Método para obtener los trabajadores por nombre
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String searchTerm = s.toString();
                obtenerTrabajadoresPorNombre(searchTerm);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        btnGuardar.setOnClickListener(v -> guardarTrabajadoresSeleccionados());
        btnClose.setOnClickListener(v -> finish());
    }

    private void obtenerPrimerosTrabajadores() {
        firebaseHelper.getAllWorkers(new FirebaseHelper.DataStatus() {
            @Override
            public void DataIsLoaded(List<?> data) {
                adapter.clear();
                for (Object obj : data) {
                    Worker worker = (Worker) obj;
                    adapter.add(worker.getName());
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void DataIsInserted() {}

            @Override
            public void DataIsUpdated() {}

            @Override
            public void DataIsDeleted() {}

            @Override
            public void onError(String errorMessage) {
                Toast.makeText(NombrarSubadmin.this, "Error al obtener los trabajadores: " + errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void obtenerTrabajadoresPorNombre(String searchTerm) {
        firebaseHelper.getWorkersByName(searchTerm, new FirebaseHelper.DataStatus() {
            @Override
            public void DataIsLoaded(List<?> data) {
                adapter.clear();
                for (Object obj : data) {
                    Worker worker = (Worker) obj;
                    adapter.add(worker.getName());
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void DataIsInserted() {}

            @Override
            public void DataIsUpdated() {}

            @Override
            public void DataIsDeleted() {}

            @Override
            public void onError(String errorMessage) {
                Toast.makeText(NombrarSubadmin.this, "Error al obtener los trabajadores: " + errorMessage, Toast.LENGTH_SHORT).show();
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
        firebaseHelper.getAllWorkers(new FirebaseHelper.DataStatus() {
            @Override
            public void DataIsLoaded(List<?> data) {
                for (Object obj : data) {
                    Worker worker = (Worker) obj;
                    if (worker.getName().equals(nombreTrabajador)) {
                        worker.setAdmin(true);
                        firebaseHelper.updateWorker(worker, new FirebaseHelper.DataStatus() {
                            @Override
                            public void DataIsLoaded(List<?> data) {}

                            @Override
                            public void DataIsInserted() {}

                            @Override
                            public void DataIsUpdated() {
                                Toast.makeText(NombrarSubadmin.this, "Trabajador marcado como subadministrador: " + nombreTrabajador, Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void DataIsDeleted() {}

                            @Override
                            public void onError(String errorMessage) {
                                Toast.makeText(NombrarSubadmin.this, "Error al marcar al trabajador como subadministrador: " + errorMessage, Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            }

            @Override
            public void DataIsInserted() {}

            @Override
            public void DataIsUpdated() {}

            @Override
            public void DataIsDeleted() {}

            @Override
            public void onError(String errorMessage) {
                Toast.makeText(NombrarSubadmin.this, "Error al obtener los trabajadores: " + errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
