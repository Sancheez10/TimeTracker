package com.example.timetracker;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;

import org.mindrot.jbcrypt.BCrypt;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.RowId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class PanellAdministrador extends AppCompatActivity {

    private ImageButton ibAddUser, ibAddGroup, ibConfig, ibAyuda;
    private Button btnClose;
    private CollectionReference workersCollection;
    private Toolbar toolbar_panell;

    private FirebaseHelper firebaseHelper;

    private static final int REQUEST_CODE_PERMISSION = 123;
    private static final int REQUEST_CODE_CSV_FILE = 456;

    private DatabaseReference databaseReference;

    private ActivityResultLauncher<String> csvFileLauncher = registerForActivityResult(new ActivityResultContracts.GetContent(),
            uri -> {
                if (uri != null) {
                    processCSVFile(uri);
                }
            });

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
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference("workers");

        workersCollection = FirebaseFirestore.getInstance().collection("workers");

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
        // Obtener referencia a la colección de "workers" en Firestore


        // Solicitar permisos de lectura y escritura externa
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE_PERMISSION);

        // Lanzar el selector de archivos CSV
        csvFileLauncher.launch("*/*");
    }

    private void processCSVFile(Uri uri) {
        try {
            // Obtener el InputStream del archivo CSV a partir de la URI
            InputStream inputStream = getContentResolver().openInputStream(uri);

            // Inicializar un lector CSV
            CSVReader reader = new CSVReader(new InputStreamReader(inputStream));

            // Leer las líneas del archivo CSV
            String[] nextLine;
            while ((nextLine = reader.readNext()) != null) {


                // Verificar si la línea tiene la estructura esperada
                if (nextLine.length >= 3) {
                    // Obtener los campos de la línea CSV
                    String email = nextLine[0];
                    String password = nextLine[1];
                    String name = nextLine[2];

                    String hashedPassword = hashPassword(password);

                    Worker worker = new Worker(null,name,email,hashedPassword);

                    guardarTrabajador(worker);


                    // Agregar registro de log
                    Log.d("CSV_IMPORT", "Trabajador procesado: " + worker.getEmail());
                } else {
                    // Si la línea no tiene la estructura esperada, registrar un error
                    Log.e("CSV_IMPORT", "Error: La línea CSV no tiene la estructura esperada");
                }
            }

            // Cerrar el lector CSV
            reader.close();

            // Mostrar un mensaje de éxito
            Toast.makeText(this, "Trabajadores importados correctamente", Toast.LENGTH_SHORT).show();
        } catch (IOException | CsvValidationException e) {
            e.printStackTrace();
            // Manejar cualquier error que pueda ocurrir al leer el archivo CSV
            Toast.makeText(this, "Error al importar trabajadores desde CSV", Toast.LENGTH_SHORT).show();
        }
    }

    private String hashPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }


    private void guardarTrabajador(Worker worker) {
        String key = databaseReference.push().getKey();
        worker.setId(key);
        databaseReference.child(key).setValue(worker);
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
