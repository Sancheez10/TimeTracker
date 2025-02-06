package com.example.timetracker;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.regex.Pattern;

public class Cuentas extends AppCompatActivity {
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    private EditText editTextPasswordOld, editTextPasswordNew;
    private Button buttonActualizar;

    // Expresión regular para validar la nueva contraseña (mínimo 6 caracteres, sin caracteres especiales)
    private static final Pattern PASSWORD_PATTERN = Pattern.compile("^[a-zA-Z0-9]{6,}$");

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cuentas);

        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Users");

        editTextPasswordOld = findViewById(R.id.editTextPasswordOld);
        editTextPasswordNew = findViewById(R.id.editTextPasswordNew);
        buttonActualizar = findViewById(R.id.buttonActualizar);

        buttonActualizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                actualizarContraseña();
            }
        });
    }

    private void actualizarContraseña() {
        String oldPassword = editTextPasswordOld.getText().toString().trim();
        String newPassword = editTextPasswordNew.getText().toString().trim();

        if (TextUtils.isEmpty(oldPassword) || oldPassword.length() < 6) {
            Toast.makeText(this, "La contraseña actual debe tener al menos 6 caracteres", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!PASSWORD_PATTERN.matcher(newPassword).matches()) {
            Toast.makeText(this, "La nueva contraseña debe tener al menos 6 caracteres y no contener caracteres especiales", Toast.LENGTH_LONG).show();
            return;
        }

        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user == null) {
            Toast.makeText(this, "No se ha iniciado sesión", Toast.LENGTH_SHORT).show();
            return;
        }

        // Reautenticación del usuario antes de cambiar la contraseña
        AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), oldPassword);
        user.reauthenticate(credential).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                user.updatePassword(newPassword).addOnCompleteListener(updateTask -> {
                    if (updateTask.isSuccessful()) {
                        // Actualizar la contraseña en Firebase Realtime Database
                        DatabaseReference usuarioRef = databaseReference.child(user.getUid());
                        usuarioRef.child("password").setValue(newPassword);

                        Toast.makeText(this, "Contraseña actualizada correctamente", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "Error al actualizar la contraseña", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                Toast.makeText(this, "Autenticación fallida: Verifique su contraseña actual", Toast.LENGTH_LONG).show();
            }
        });
    }
}
