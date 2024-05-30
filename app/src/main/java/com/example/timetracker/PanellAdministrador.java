package com.example.timetracker;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PanellAdministrador extends AppCompatActivity {

    private ImageButton ibAddUser, ibAddGroup, ibConfig, ibAyuda;
    private CollectionReference workersCollection;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_panell_administrador);

        ibAyuda = findViewById(R.id.ibAyuda);
        ibConfig = findViewById(R.id.ibConfig);
        ibAddGroup = findViewById(R.id.ibAddGroup);
        ibAddUser = findViewById(R.id.ibAddUser);

        ibAddUser.setOnClickListener(view -> {
            // Inflar el menú a partir del archivo XML
            PopupMenu popupMenu = new PopupMenu(this, ibAddUser);
            popupMenu.getMenuInflater().inflate(R.menu.menu_adduser, popupMenu.getMenu());

            // Configurar los listeners para las opciones del menú
            popupMenu.setOnMenuItemClickListener(item -> {
                // Manejar la selección de la opción del menú
                if (item.getItemId() == R.id.action_importUserFromJSON) {
                    importWorkersFromJSON();
                    return true;
                } else if (item.getItemId() == R.id.action_addUser) {
                    abrirVentanaFlotanteAddWorker();
                    return true;
                }
                return false;
            });

            // Mostrar el menú
            popupMenu.show();
        });

        ibAddGroup.setOnClickListener(view -> {
            // Código para abrir una ventana flotante y pedir el nombre del grupo
            abrirVentanaFlotanteAddGroup();
        });

        ibAyuda.setOnClickListener(view -> {
            // Código para abrir la actividad de Ayuda
            abrirActividadAyuda();
        });

        ibConfig.setOnClickListener(view -> {
            // Código para abrir el menú de configuración del administrador
            abrirMenuConfiguracionAdmin();
        });
    }

    private void importWorkersFromJSON() {
        // Código para importar trabajadores desde JSON
        // Leer el archivo JSON desde el almacenamiento externo
        try {
            File file = new File(getExternalFilesDir(null), "workers.json");
            BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
            StringBuilder stringBuilder = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line);
            }
            bufferedReader.close();

            // Convertir el JSON a una lista de trabajadores
            Gson gson = new Gson();
            Type type = new TypeToken<List<Map<String, Object>>>() {
            }.getType();
            List<Map<String, Object>> workersList = gson.fromJson(stringBuilder.toString(), type);

            // Guardar los trabajadores en Firestore
            for (Map<String, Object> worker : workersList) {
                workersCollection.add(worker).addOnSuccessListener(documentReference -> Log.d("IMPORT", "Trabajador importado exitosamente")).addOnFailureListener(e -> Log.e("IMPORT", "Error al importar trabajador", e));
            }
            Toast.makeText(this, "Trabajadores importados correctamente", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error al importar trabajadores desde JSON", Toast.LENGTH_SHORT).show();
        }
    }

    private void abrirVentanaFlotanteAddWorker() {
        // Código para abrir una ventana flotante para añadir un trabajador
        AddWorkerFragment addWorkerFragment = new AddWorkerFragment();
        addWorkerFragment.show(getSupportFragmentManager(), "add_worker_fragment");
    }

    private void abrirVentanaFlotanteAddGroup() {
        // Código para abrir una ventana flotante y pedir el nombre del grupo
        AddGroupFragment addGroupFragment = new AddGroupFragment();
        addGroupFragment.show(getSupportFragmentManager(), "add_group_fragment");
    }

    private void abrirActividadAyuda() {
        // Código para abrir la actividad de Ayuda
        Intent intent = new Intent(PanellAdministrador.this, Ayuda.class);
        startActivity(intent);
    }

    private void abrirMenuConfiguracionAdmin() {
        // Inflar el menú a partir del archivo XML
        PopupMenu popupMenu = new PopupMenu(this, ibConfig);
        popupMenu.getMenuInflater().inflate(R.menu.menu_config_admin, popupMenu.getMenu());

        // Configurar los listeners para las opciones del menú
        popupMenu.setOnMenuItemClickListener(item -> {
            // Manejar la selección de la opción del menú
            if (item.getItemId() == R.id.action_nombrarSubadmin) {
                abrirActividadNombrarSubadmin();
                return true;
            } else if (item.getItemId() == R.id.action_anotaciones) {
                abrirActividadAnotaciones();
                return true;
            } else if (item.getItemId() == R.id.action_dispositivosRestricciones) {
                abrirActividadDispositivosRestricciones();
                return true;
            } else if (item.getItemId() == R.id.action_etiquetas) {
                abrirActividadEtiquetas();
                return true;
            } else if (item.getItemId() == R.id.action_exportar) {
                exportWorkersToJSON();
                return true;
            } else if (item.getItemId() == R.id.action_API) {
                abrirPaginaWebAPI();
                return true;
            }
            return false;
        });

        // Mostrar el menú
        popupMenu.show();
    }

    private void abrirActividadNombrarSubadmin() {
        Intent intent = new Intent(PanellAdministrador.this, NombrarSubadmin.class);
        startActivity(intent);
    }

    private void abrirActividadAnotaciones() {
        Intent intent = new Intent(PanellAdministrador.this, Anotaciones.class);
        startActivity(intent);
    }

    private void abrirActividadDispositivosRestricciones() {
        Intent intent = new Intent(PanellAdministrador.this, DispositivosRestricciones.class);
        startActivity(intent);
    }

    private void abrirActividadEtiquetas() {
        Intent intent = new Intent(PanellAdministrador.this, Etiquetas.class);
        startActivity(intent);
    }

    private void exportWorkersToJSON() {
        // Código para exportar datos a Firebase
        // Obtener referencia a la colección de "trabajadores" en Firestore
        workersCollection = FirebaseFirestore.getInstance().collection("trabajadores");

        // Obtener todos los documentos de la colección
        workersCollection.get().addOnSuccessListener(querySnapshot -> {
            // Crear una lista para almacenar los datos de los trabajadores
            List<Map<String, Object>> workersList = new ArrayList<>();

            // Recorrer los documentos y extraer los datos
            for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                Map<String, Object> workerData = document.getData();
                workersList.add(workerData);
            }

            // Convertir la lista de trabajadores a JSON
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            String json = gson.toJson(workersList);

            // Guardar el JSON en un archivo
            try {
                File file = new File(getExternalFilesDir(null), "workers.json");
                FileWriter writer = new FileWriter(file);
                writer.write(json);
                writer.close();
                Toast.makeText(this, "Trabajadores exportados correctamente", Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "Error al exportar trabajadores", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e -> {
            e.printStackTrace();
            Toast.makeText(this, "Error al obtener trabajadores de Firestore", Toast.LENGTH_SHORT).show();
        });
    }

    private void abrirPaginaWebAPI() {
        String url = "https://www.relojlaboral.com/wiki/index.php/P%C3%A1gina_principal";
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(browserIntent);
    }
}