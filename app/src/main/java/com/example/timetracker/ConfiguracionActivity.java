package com.example.timetracker;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.FirebaseAuthException;

public class ConfiguracionActivity extends AppCompatActivity {

    private CheckBox checkBoxExtraSecurity;
    private CheckBox checkBoxNotifications;
    private Button buttonModifyNotifications;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuracion);

        Toolbar toolbar = findViewById(R.id.toolbar_configuracion);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Configuración");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        checkBoxExtraSecurity = findViewById(R.id.checkbox_extra_security);
        checkBoxNotifications = findViewById(R.id.checkbox_notifications);
        buttonModifyNotifications = findViewById(R.id.button_modify_notifications);

        mAuth = FirebaseAuth.getInstance();

        // Handler for the extra security checkbox
        checkBoxExtraSecurity.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                enableExtraSecurity();
            } else {
                disableExtraSecurity();
            }
        });

        // Handler for the notifications checkbox
        checkBoxNotifications.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                // Enable notifications
                Toast.makeText(this, "Notificaciones habilitadas", Toast.LENGTH_SHORT).show();
            } else {
                // Disable notifications
                Toast.makeText(this, "Notificaciones deshabilitadas", Toast.LENGTH_SHORT).show();
            }
        });

        // Handler for the modify notifications button
        buttonModifyNotifications.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.setAction(Settings.ACTION_APP_NOTIFICATION_SETTINGS);
            intent.putExtra(Settings.EXTRA_APP_PACKAGE, getPackageName());
            startActivity(intent);
        });
    }

    private void enableExtraSecurity() {
        // Implement fingerprint or face recognition logic
        // Note: This is a placeholder for actual implementation
        Toast.makeText(this, "Seguridad extra habilitada", Toast.LENGTH_SHORT).show();
    }

    private void disableExtraSecurity() {
        // Implement logic to disable fingerprint or face recognition
        // Note: This is a placeholder for actual implementation
        Toast.makeText(this, "Seguridad extra deshabilitada", Toast.LENGTH_SHORT).show();
    }
}
