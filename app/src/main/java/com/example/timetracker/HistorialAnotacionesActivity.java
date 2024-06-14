

package com.example.timetracker;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class HistorialAnotacionesActivity extends AppCompatActivity {

    private ListView lvAnotaciones;
    private Button btnAtras, btnRefrescar, btnMarcarComoVistas;
    private ArrayAdapter<String> adapter;
    private List<String> anotacionesList;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historial_anotaciones);

        // Configuración de la Toolbar
        Toolbar toolbar = findViewById(R.id.toolbarHistorial);
        setSupportActionBar(toolbar);

        // Inicialización de las vistas
        lvAnotaciones = findViewById(R.id.listViewAnotaciones);
        btnAtras = findViewById(R.id.btnAtras);
        btnRefrescar = findViewById(R.id.btnRefrescar);
        btnMarcarComoVistas = findViewById(R.id.btnMarcarComoVistas);

        // Inicialización de las listas
        anotacionesList = new ArrayList<>();

        // Configuración del adaptador
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, anotacionesList);
        lvAnotaciones.setAdapter(adapter);

        // Obtener SharedPreferences
        sharedPreferences = getSharedPreferences("anotaciones_prefs", MODE_PRIVATE);

        // Listener para el botón "Atrás"
        btnAtras.setOnClickListener(v -> finish());

        // Listener para el botón "Refrescar"
        btnRefrescar.setOnClickListener(v -> cargarAnotaciones());

        // Cargar las anotaciones al iniciar la actividad
        cargarAnotaciones();
    }

    private void cargarAnotaciones() {
        // Limpiar la lista antes de cargar nuevos datos
        anotacionesList.clear();

        // Obtener todas las anotaciones guardadas en SharedPreferences
        Map<String, ?> allEntries = sharedPreferences.getAll();
        for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
            String textoAnotacion = entry.getValue().toString();
            anotacionesList.add(textoAnotacion);
        }

        // Notificar al adaptador que los datos han cambiado
        adapter.notifyDataSetChanged();

        if (anotacionesList.isEmpty()) {
            Toast.makeText(this, "No hay anotaciones para mostrar", Toast.LENGTH_SHORT).show();
        }
    }


}
