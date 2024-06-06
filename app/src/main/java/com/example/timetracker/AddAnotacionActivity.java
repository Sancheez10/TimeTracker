package com.example.timetracker;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class AddAnotacionActivity extends AppCompatActivity {

    private EditText etAnotacion;
    private Button bEnviar, bHistorial;
    private FirebaseFirestore db;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anotaciones);

        Toolbar toolbar = findViewById(R.id.toolbar4);
        setSupportActionBar(toolbar);

        etAnotacion = findViewById(R.id.etAnotacion);
        bEnviar = findViewById(R.id.bEnviar);
        bHistorial = findViewById(R.id.bHistorial);

        db = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser();
    }

    public void enviarAnotacion(View view) {
        String textoAnotacion = etAnotacion.getText().toString();
        if (!textoAnotacion.isEmpty() && currentUser != null) {
            String userId = currentUser.getUid();
            String anotacionId = db.collection("anotaciones_users")
                    .document(userId)
                    .collection("anotaciones")
                    .document().getId();

            Date fechaHoraActual = new Date();
            String createdBy = currentUser.getEmail();

            Map<String, Object> anotacionData = new HashMap<>();
            anotacionData.put("texto", textoAnotacion);
            anotacionData.put("fechaHora", fechaHoraActual);
            anotacionData.put("createdBy", createdBy);

            db.collection("anotaciones_users")
                    .document(userId)
                    .collection("anotaciones")
                    .document(anotacionId)
                    .set(anotacionData)
                    .addOnSuccessListener(documentReference -> {
                        Toast.makeText(AddAnotacionActivity.this, "Anotación guardada", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(AddAnotacionActivity.this, "Error al guardar la anotación", Toast.LENGTH_SHORT).show();
                    });
        } else {
            Toast.makeText(AddAnotacionActivity.this, "Por favor, ingresa una anotación", Toast.LENGTH_SHORT).show();
        }
        etAnotacion.setText("");
    }

    public void abrirHistorial(View view) {
        Intent intent = new Intent(this, HistorialAnotacionesActivity.class);
        startActivity(intent);
    }
}
