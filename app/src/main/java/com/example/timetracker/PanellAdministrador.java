package com.example.timetracker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PanellAdministrador extends AppCompatActivity {

    private ImageButton ibAddUser, ibAddGroup, ibConfig, ibAyuda;
    private Button btnClose;
    private CollectionReference workersCollection;
    private Toolbar toolbar_panell;
    private ListView workerListView;
    private List<String> workerNames;

    private List<Worker> workerList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_panell_administrador);

        toolbar_panell = findViewById(R.id.toolbar2);
        setSupportActionBar(toolbar_panell);

        ibAyuda = findViewById(R.id.ibAyuda);
        ibConfig = findViewById(R.id.ibConfig);
        ibAddGroup = findViewById(R.id.ibAddGroup);
        ibAddUser = findViewById(R.id.ibAddUser);
        btnClose = findViewById(R.id.close_button);
        workerListView = findViewById(R.id.workerListView);

        workerNames = new ArrayList<>();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, workerNames);
        workerListView.setAdapter(adapter);

        // Inicializar la colección de trabajadores en Firestore
        workersCollection = FirebaseFirestore.getInstance().collection("workers");


        workersCollection.get().addOnSuccessListener(queryDocumentSnapshots -> {
            for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                Log.d("AAAAAA", "docuement" + document);
                Worker worker = document.toObject(Worker.class);
                if (worker != null) {
                    workerList.add(worker);
                    // Agregar el nombre del trabajador a la lista workerNames
                    workerNames.add(worker.getName()); // Suponiendo que el objeto Worker tiene un método getName() que devuelve el nombre
                }
            }
            adapter.notifyDataSetChanged(); // Notificar al adaptador que los datos han cambiado
        }).addOnFailureListener(e -> {
            // Manejar el error
        });




        ibAddUser.setOnClickListener(view -> {
            // Inflar el menú a partir del archivo XML
            PopupMenu popupMenu = new PopupMenu(this, ibAddUser);
            popupMenu.getMenuInflater().inflate(R.menu.menu_adduser, popupMenu.getMenu());

            // Configurar los listeners para las opciones del menú
            popupMenu.setOnMenuItemClickListener(item -> {
                // Manejar la selección de la opción del menú
                if (item.getItemId() == R.id.action_importUserFromCSV) {
                    importWorkersFromCSV(); // Actualizado para importar desde CSV
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
            abrirDialogoAyuda();
        });

        ibConfig.setOnClickListener(view -> {
            // Código para abrir el menú de configuración del administrador
            abrirMenuConfiguracionAdmin();
        });

        btnClose.setOnClickListener(v -> finish());
    }

    private void importWorkersFromCSV() {
        // Código para importar trabajadores desde CSV
        try {
            File file = new File(getExternalFilesDir(null), "workers.csv");
            CSVReader reader = new CSVReader(new FileReader(file));
            String[] nextLine;
            while ((nextLine = reader.readNext()) != null) {
                // Suponiendo que el CSV tiene los campos: id, name, surname, email, phone, city, isAdmin, password, nieNif
                Map<String, Object> worker = new HashMap<>();
                worker.put("id", nextLine[0]);
                worker.put("name", nextLine[1]);
                worker.put("surname", nextLine[2]);
                worker.put("email", nextLine[3]);
                worker.put("phone", nextLine[4]);
                worker.put("city", nextLine[5]);
                worker.put("isAdmin", Boolean.parseBoolean(nextLine[6]));
                worker.put("password", hashPassword(nextLine[7])); // Hashed password
                worker.put("nieNif", nextLine[8]);

                workersCollection.add(worker).addOnSuccessListener(documentReference ->
                        Log.d("IMPORT", "Trabajador importado exitosamente")
                ).addOnFailureListener(e ->
                        Log.e("IMPORT", "Error al importar trabajador", e)
                );
            }
            reader.close();
            Toast.makeText(this, "Trabajadores importados correctamente", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error al importar trabajadores desde CSV", Toast.LENGTH_SHORT).show();
        } catch (CsvValidationException e) {
            throw new RuntimeException(e);
        }
    }

    private String hashPassword(String password) {
        try {
            // Crear una instancia de MessageDigest con el algoritmo SHA-256
            MessageDigest digest = MessageDigest.getInstance("SHA-256");

            // Aplicar el hash a la contraseña
            byte[] encodedHash = digest.digest(password.getBytes());

            // Convertir el byte array a un string hexadecimal
            StringBuilder hexString = new StringBuilder();
            for (byte b : encodedHash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error al generar el hash de la contraseña", e);
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

    private void abrirDialogoAyuda() {
        AyudaAdmin ayudaAdmin = new AyudaAdmin();
        ayudaAdmin.show(getSupportFragmentManager(), "ayuda_admin_dialog");
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
        Intent intent = new Intent(PanellAdministrador.this, AddAnotacionAdminActivity.class);
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
        workersCollection = FirebaseFirestore.getInstance().collection("workers");

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
