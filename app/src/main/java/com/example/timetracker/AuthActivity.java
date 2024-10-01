package com.example.timetracker;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.mindrot.jbcrypt.BCrypt;

import java.util.Arrays;
import java.util.List;

public class AuthActivity extends AppCompatActivity {
    private EditText emailEditText, passwordEditText;
    private Button loginButton;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseRef;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        loginButton = findViewById(R.id.loginButton);

        firebaseAuth = FirebaseAuth.getInstance();
        databaseRef = FirebaseDatabase.getInstance().getReference("workers");

        sharedPreferences = getSharedPreferences("workers_pref", Context.MODE_PRIVATE);

        loginButton.setOnClickListener(v -> loginUser());

    }

    private void loginUser() {
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            emailEditText.setError("Ingresa un correo electrónico");
            return;
        }

        if (TextUtils.isEmpty(password)) {
            passwordEditText.setError("Ingresa una contraseña");
            return;
        }

        authenticateUser(email, password);
    }

    private void authenticateUser(String email, String password) {
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // El usuario ha iniciado sesión correctamente
                        String userId = firebaseAuth.getCurrentUser().getUid();

                        // Llamamos a saveUserData para asegurarnos de que el usuario se guarda en la base de datos
                        saveUserData(userId, email, password);

                        // Redirigir a la MainActivity
                        redirectToMainActivity();
                    } else {
                        // Si el correo o contraseña es incorrecto o el usuario no existe
                        Toast.makeText(AuthActivity.this, "Correo o contraseña incorrecta", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void saveUserData(String userId, String email, String password) {
        // Guardar el usuario en Firebase Database bajo su UID si no está ya guardado
        databaseRef.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                    // Si el usuario no existe en la base de datos, guardarlo
                    Worker user = new Worker(email, hashPassword(password));
                    databaseRef.child(userId).setValue(user)
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    Toast.makeText(AuthActivity.this, "Datos guardados", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(AuthActivity.this, "Error al guardar los datos: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                } else {
                    // Si ya existe, obtener su estado de administrador
                    Boolean isAdmin = dataSnapshot.child("admin").getValue(Boolean.class);
                    saveAdminStatusInPreferences(isAdmin != null ? isAdmin : false);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("AuthActivity", "Error al obtener datos de usuario: " + databaseError.getMessage());
            }
        });

        saveUserDataInPreferences(userId, email);  // Guardar en SharedPreferences
    }

    private String hashPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }

    private void saveUserDataInPreferences(String userId, String email) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("userId", userId);
        editor.putString("email", email);
        editor.apply();
    }

    private void saveAdminStatusInPreferences(boolean isAdmin) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("isAdmin", isAdmin);
        editor.apply();
    }

    private void redirectToMainActivity() {
        Intent intent = new Intent(AuthActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
