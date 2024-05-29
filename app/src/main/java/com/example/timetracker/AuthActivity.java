
package com.example.timetracker;

import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
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


public class AuthActivity extends AppCompatActivity {

    private EditText emailEditText, passwordEditText;
    private Button loginButton, signUpButton;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        // Inicializar las vistas
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        loginButton = findViewById(R.id.loginButton);
        signUpButton = findViewById(R.id.signUpButton);

        // Inicializar Firebase Authentication
        firebaseAuth = FirebaseAuth.getInstance();

        databaseRef = FirebaseDatabase.getInstance().getReference("Users");
        // Configurar los listeners de los botones
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUser();
            }
        });

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Aquí puedes agregar la lógica para redirigir al usuario a la pantalla de registro
            }
        });
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
                            saveUserData(userId, email, password);
                            Toast.makeText(AuthActivity.this, "Inicio de sesión exitoso", Toast.LENGTH_SHORT).show();
                        } else {
                            // Fallo en el inicio de sesión, muestra un mensaje de error
                            Toast.makeText(AuthActivity.this, "Fallo en el inicio de sesión: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void saveUserData(String userId, String email, String password) {
        // Crear un objeto de usuario con los datos que deseas guardar
        Person user = new Person(email, password);

        // Guardar los datos del usuario en la base de datos
        databaseRef.child("users").child(userId).setValue(user)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            // Datos guardados correctamente
                            Toast.makeText(AuthActivity.this, "Datos guardados", Toast.LENGTH_SHORT).show();
                        } else {
                            // Error al guardar los datos
                            Toast.makeText(AuthActivity.this, "Error al guardar los datos: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }




}

