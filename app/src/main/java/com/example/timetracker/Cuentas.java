package com.example.timetracker;

import android.os.Bundle;
import android.text.TextUtils;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Cuentas extends AppCompatActivity {
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cuentas);

        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Users"); // Asegúrate de que esta referencia sea correcta
    }

    public void actualizarInformacionCuenta(String userId,  String nuevoEmail, String nuevaContraseña) {
        // Verificar si el usuario está autenticado
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null && !TextUtils.isEmpty(userId)) {
            // Actualizar la información en Firebase Realtime Database
            DatabaseReference usuarioRef = databaseReference.child(userId);
            if (!TextUtils.isEmpty(nuevoEmail)) {
                user.updateEmail(nuevoEmail); // Actualizar el email en Firebase Authentication
                usuarioRef.child("email").setValue(nuevoEmail);
            }
            if (!TextUtils.isEmpty(nuevaContraseña)) {
                usuarioRef.child("password").setValue(nuevaContraseña);
            }
        }
    }
}

