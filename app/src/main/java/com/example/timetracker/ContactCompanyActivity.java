package com.example.timetracker;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class ContactCompanyActivity extends AppCompatActivity {

    private EditText etTitle, etMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_company);

        Toolbar toolbar = findViewById(R.id.toolbar_contact_company);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Contactar a la Empresa");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        etTitle = findViewById(R.id.et_title);
        etMessage = findViewById(R.id.et_message);
        Button btnSend = findViewById(R.id.btn_send);
        Button btnClose = findViewById(R.id.close_button);

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = etTitle.getText().toString();
                String message = etMessage.getText().toString();
                sendEmail("tu_correo_empresa@ejemplo.com", title, message);
            }
        });

        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void sendEmail(String to, String subject, String message) {
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                "mailto", to, null));
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
        emailIntent.putExtra(Intent.EXTRA_TEXT, message);
        try {
            startActivity(Intent.createChooser(emailIntent, "Enviar correo..."));
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(ContactCompanyActivity.this, "No hay clientes de correo instalados.", Toast.LENGTH_SHORT).show();
        }
    }
}
