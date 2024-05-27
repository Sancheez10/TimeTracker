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

import java.io.File;
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
            popupMenu.setOnMenuItemClickListener(item -> {
                // Manejar la selección de la opción del menú
                switch (item.getItemId()) {
                    case R.id.action_addUser:
                        // Abrir la ventana flotante para agregar un nuevo usuario
                        showAddUserFragment();
                        return true;
                    case R.id.action_importUserFromJSON:
                        // Código para la opción 2 (importar usuarios desde JSON)
                        importUsersFromJSON();
                        return true;
                    default:
                        return false;
                }
            });
        }

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

        private void showAddUserFragment () {
            // Crear una instancia del fragmento AddUserFragment
            AddUserFragment addUserFragment = new AddUserFragment();

            // Mostrar el fragmento en la actividad
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.fragment_container, addUserFragment)
                    .commit();
        }
    }
}