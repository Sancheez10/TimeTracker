package com.example.timetracker;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;

import android.content.Intent;
import android.view.View;
import androidx.appcompat.widget.Toolbar;

public class Ayuda extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ayuda);

        Toolbar toolbar = findViewById(R.id.toolbar_help);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Ayuda");

        findViewById(R.id.btn_wiki_manual).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Abre la Wikipedia en el navegador
                String url = "https://es.wikipedia.org/wiki/Control_horario";
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(url));
                startActivity(intent);
            }
        });

        findViewById(R.id.btn_contact).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showContactOptions();
            }
        });

        findViewById(R.id.btn_support).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Ayuda.this, SupportActivity.class);
                startActivity(intent);
            }
        });

        findViewById(R.id.btn_about).setOnClickListener(v -> {
            Intent intent = new Intent(Ayuda.this, AboutActivity.class);
            startActivity(intent);
        });

        findViewById(R.id.close_button).setOnClickListener(v -> {
            finish(); // Cierra la actividad actual
        });
    }

    private void showContactOptions() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Contacto")
                .setItems(new String[]{"Contactar a la empresa", "Contactar con el servicio técnico"}, (dialog, which) -> {
                    switch (which) {
                        case 0:
                            // Contactar a la empresa
                            Intent intentCompany = new Intent(Ayuda.this, ContactCompanyActivity.class);
                            startActivity(intentCompany);
                            break;
                        case 1:
                            // Contactar con el servicio técnico
                            Intent intentTechSupport = new Intent(Ayuda.this, ContactTechSupportActivity.class);
                            startActivity(intentTechSupport);
                            break;
                    }
                });
        builder.create().show();
    }
}
