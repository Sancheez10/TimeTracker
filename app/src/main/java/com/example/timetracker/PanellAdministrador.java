package com.example.timetracker;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PanellAdministrador extends AppCompatActivity {

    private ImageButton ibAddUser, ibAddGroup, ibConfig, ibAyuda;

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
            popupMenu.setOnMenuItemClickListener(this::onAddUserMenuItemClick);

            // Mostrar el menú
            popupMenu.show();
        });

        ibAddGroup.setOnClickListener(view -> {
            Intent intent = new Intent(PanellAdministrador.this, AddGroup.class);
            startActivity(intent);
        });

        ibAyuda.setOnClickListener(view -> {
            Intent intent = new Intent(PanellAdministrador.this, Ayuda.class);
            startActivity(intent);
        });

        ibConfig.setOnClickListener(view -> {
            // Inflar el menú a partir del archivo XML
            PopupMenu popupMenu = new PopupMenu(this, ibConfig);
            popupMenu.getMenuInflater().inflate(R.menu.menu_config_admin, popupMenu.getMenu());

            // Configurar los listeners para las opciones del menú
            popupMenu.setOnMenuItemClickListener(item -> {
                // Manejar la selección de la opción del menú
                switch (item.getItemId()) {
                    case R.id.action_nombrarSubadmin:
                        // Código para la opción 1
                        Intent intent = new Intent(PanellAdministrador.this, NombrarSubadmin.class);
                        startActivity(intent);
                        return true;
                    case R.id.action_anotaciones:
                        // Código para la opción 2
                        Intent intent2 = new Intent(PanellAdministrador.this, Anotaciones.class);
                        startActivity(intent2);
                        return true;
                    case R.id.action_dispositivosRestricciones:
                        // Código para la opción 3
                        Intent intent3 = new Intent(PanellAdministrador.this, DispositivosRestricciones.class);
                        startActivity(intent3);
                        return true;
                    case R.id.action_etiquetas:
                        // Código para la opción 4
                        Intent intent4 = new Intent(PanellAdministrador.this, Etiquetas.class);
                        startActivity(intent4);
                        return true;
                    case R.id.action_exportar:
                        // Código para la opción 5
                        exportarDatosFirebase();
                        return true;
                    case R.id.action_API:
                        // Código para la opción 6
                        String url = "https://www.relojlaboral.com/wiki/index.php/P%C3%A1gina_principal";
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                        startActivity(browserIntent);
                        return true;
                    return true;
                    default:
                        return false;
                }
            });

            // Mostrar el menú
            popupMenu.show();
        });
    }

    private boolean onAddUserMenuItemClick(MenuItem item) {
        // Manejar la selección de la opción del menú
        if (item.getItemId() == R.id.action_addUser) {
            // Abrir la ventana flotante para agregar un nuevo usuario
            showAddUserFragment();
            return true;
        } else if (item.getItemId() == R.id.action_importUserFromJSON) {
            // Código para la opción 2 (importar usuarios desde JSON)
            importUsersFromJSON();
            return true;
        } else {
            return false;
        }
    }


    private void importUsersFromJSON() {
        try {
            // Leer el archivo JSON desde el sistema de archivos
            File file = new File(Environment.getExternalStorageDirectory(), "users.json");
            StringBuilder stringBuilder = new StringBuilder();
            BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line);
            }
            bufferedReader.close();

            // Parsear el JSON y obtener la lista de usuarios
            Gson gson = new Gson();
            User[] users = gson.fromJson(stringBuilder.toString(), User[].class);

            // Guardar los usuarios en la base de datos
            for (User user : users) {
                // Aquí debes agregar la lógica para guardar el usuario en la base de datos
            }

            Toast.makeText(this, "Usuarios importados correctamente", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            Log.e("ImportUsers", "Error al importar usuarios desde JSON", e);
            Toast.makeText(this, "Error al importar usuarios", Toast.LENGTH_SHORT).show();
        }
    }

    private void exportarDatosFirebase () {
        // Obtener referencia a la colección de "trabajadores" en Firebase
        CollectionReference trabajadoresRef = FirebaseFirestore.getInstance().collection("trabajadores");

        // Obtener todos los documentos de la colección
        trabajadoresRef.get()
                .addOnSuccessListener(querySnapshot -> {
                    // Crear una lista para almacenar los datos de los trabajadores
                    List<Map<String, Object>> trabajadores = new ArrayList<>();

                    // Recorrer los documentos y extraer los datos
                    for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                        Map<String, Object> trabajador = document.getData();
                        trabajadores.add(trabajador);
                    }

                    // Exportar los datos a un archivo CSV
                    exportarACSV(trabajadores);
                })
                .addOnFailureListener(e -> {
                    // Manejar el error en caso de que falle la consulta a Firebase
                    Log.e("Firebase", "Error al obtener los datos de los trabajadores", e);
                });
    }

    private void exportarACSV (List < Map < String, Object >> datos){
        try {
            // Crear un archivo CSV en el almacenamiento externo
            File file = new File(Environment.getExternalStorageDirectory(), "trabajadores.csv");
            FileWriter writer = new FileWriter(file);

            // Escribir los encabezados del CSV
            writer.write("Nombre,Apellido,Cargo,Correo,Teléfono\n");

            // Escribir los datos de los trabajadores en el archivo CSV
            for (Map<String, Object> trabajador : datos) {
                String nombre = trabajador.get("nombre").toString();
                String apellido = trabajador.get("apellido").toString();
                String cargo = trabajador.get("cargo").toString();
                String correo = trabajador.get("correo").toString();
                String telefono = trabajador.get("telefono").toString();
                writer.write(nombre + "," + apellido + "," + cargo + "," + correo + "," + telefono + "\n");
            }

            writer.flush();
            writer.close();

            // Mostrar un mensaje de éxito o abrir el archivo exportado
            Toast.makeText(this, "Datos exportados correctamente", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            // Manejar el error en caso de que falle la exportación a CSV
            Log.e("Firebase", "Error al exportar los datos a CSV", e);
        }
    }

    private void showAddUserFragment() {
        AddUserFragment addUserFragment = new AddUserFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, addUserFragment, "add_user_fragment")
                .addToBackStack(null)
                .commit();    }
}