package com.example.timetracker;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
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

        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        confirmPasswordEditText = findViewById(R.id.confirmPasswordEditText);
        loginButton = findViewById(R.id.loginButton);
        signUpButton = findViewById(R.id.signUpButton);

        firebaseAuth = FirebaseAuth.getInstance();
        databaseRef = FirebaseDatabase.getInstance().getReference("workers");

        sharedPreferences = getSharedPreferences("workers_pref", Context.MODE_PRIVATE);

        loginButton.setOnClickListener(v -> loginUser());
        signUpButton.setOnClickListener(v -> signUpUser());
    }

    private boolean isAllowedDomain(String email) {
        String domain = email.substring(email.indexOf("@") + 1);
        return allowedDomains.contains(domain);
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

    private void signUpUser() {
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
        String confirmPassword = confirmPasswordEditText.getText().toString().trim();

        if (!isValidEmail(email)) {
            emailEditText.setError("Correo electrónico no válido");
            return;
        }

        if (!isAllowedDomain(email)) {
            emailEditText.setError("El correo electrónico debe pertenecer a timetracker.com");
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
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private void saveUserData(String userId, String email, String password) {
        Worker user = new Worker(email, hashPassword(password));
        databaseRef.child(userId).setValue(user)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(AuthActivity.this, "Datos guardados", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(AuthActivity.this, "Error al guardar los datos: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
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

    private void redirectToMainActivity() {
        Intent intent = new Intent(AuthActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void authenticateUser(String email, String password) {

        databaseRef.orderByChild("email").equalTo(email).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                        Worker user = userSnapshot.getValue(Worker.class);
                        if (user != null && BCrypt.checkpw(password, user.getPassword())) {
                            saveUserDataInPreferences(userSnapshot.getKey(), email);
                            redirectToMainActivity();
                        } else {
                            Toast.makeText(AuthActivity.this, "Correo o contraseña incorrecta", Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {

                    Toast.makeText(AuthActivity.this, "El usuario no existe", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

                Toast.makeText(AuthActivity.this, "Error en la autenticación", Toast.LENGTH_SHORT).show();
            }
        });


}
}
