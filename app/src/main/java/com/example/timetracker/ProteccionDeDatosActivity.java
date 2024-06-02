package com.example.timetracker;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class ProteccionDeDatosActivity extends AppCompatActivity {

    private Button legalButton, privacyButton, closeButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_proteccion_de_datos);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Protección de Datos");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        legalButton = findViewById(R.id.legal_button);
        privacyButton = findViewById(R.id.privacy_button);
        closeButton = findViewById(R.id.close_button);

        legalButton.setOnClickListener(v -> {
            Intent intent = new Intent(ProteccionDeDatosActivity.this, LegalNoticeActivity.class);
            startActivity(intent);
        });

        privacyButton.setOnClickListener(v -> {
            Intent intent = new Intent(ProteccionDeDatosActivity.this, PrivacyPolicyActivity.class);
            startActivity(intent);
        });

        closeButton.setOnClickListener(v -> finish());
    }
}
