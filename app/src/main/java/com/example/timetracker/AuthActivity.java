package com.example.timetracker;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Arrays;
import java.util.List;

public class AuthActivity extends AppCompatActivity {

    private EditText emailEditText, passwordEditText, confirmPasswordEditText;
    private Button loginButton, signUpButton;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseRef;
    private SharedPreferences sharedPreferences;

    private final List<String> allowedDomains = Arrays.asList("timetracker.com");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        // Inicializar las vistas
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        confirmPasswordEditText = findViewById(R.id.confirmPasswordEditText);
        loginButton = findViewById(R.id.loginButton);
        signUpButton = findViewById(R.id.signUpButton);

        // Inicializar Firebase Authentication y la referencia a la base de datos
        firebaseAuth = FirebaseAuth.getInstance();
        databaseRef = FirebaseDatabase.getInstance().getReference("workers");

        // Inicializar SharedPreferences
        sharedPreferences = getSharedPreferences("workers_pref", Context.MODE_PRIVATE);

        // Configurar los listeners de los botones
        loginButton.setOnClickListener(v -> loginUser());
        signUpButton.setOnClickListener(v -> signUpUser());
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

        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Inicio de sesión exitoso
                            String userId = firebaseAuth.getCurrentUser().getUid();
                            saveUserDataInPreferences(userId, email);
                            redirectToMainActivity();
                        } else {
                            // Fallo en el inicio de sesión, muestra un mensaje de error
                            Toast.makeText(AuthActivity.this, "Fallo en el inicio de sesión: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void signUpUser() {
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
        String confirmPassword = confirmPasswordEditText.getText().toString().trim();

        if (!isValidEmail(email)) {
            emailEditText.setError("Correo electrónico no válido");
            return;
        }

        if (!password.equals(confirmPassword)) {
            confirmPasswordEditText.setError("Las contraseñas no coinciden");
            return;
        }

        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        String userId = firebaseAuth.getCurrentUser().getUid();
                        saveUserData(userId, email, password);
                        saveUserDataInPreferences(userId, email);
                        redirectToMainActivity();
                    } else {
                        Toast.makeText(AuthActivity.this, "Fallo en el registro: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private boolean isValidEmail(String email) {
        // Validar el formato del correo electrónico utilizando una expresión regular
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private boolean isAllowedDomain(String email) {
        String domain = email.substring(email.indexOf("@") + 1);
        return allowedDomains.contains(domain);
    }

    private String hashPassword(String password) {
        // Implementa aquí tu lógica de hash
        return password;  // Reemplaza esto con el hash real
    }

    private void saveUserData(String userId, String email, String password) {
        // Crear un objeto de usuario con los datos que deseas guardar
        Worker user = new Worker(email, hashPassword(password));

        // Guardar los datos del usuario en la base de datos
        databaseRef.child(userId).setValue(user)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Datos guardados correctamente
                        Toast.makeText(AuthActivity.this, "Datos guardados", Toast.LENGTH_SHORT).show();
                    } else {
                        // Error al guardar los datos
                        Toast.makeText(AuthActivity.this, "Error al guardar los datos: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }


    private void saveUserDataInPreferences(String userId, String email) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("userId", userId);
        editor.putString("email", email);
        editor.apply();
    }

    private void redirectToMainActivity() {
        Intent intent = new Intent(AuthActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
