package com.example.timetracker;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class ContactTechSupportActivity extends AppCompatActivity {

    private EditText etTitle, etMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_tech_support);

        Toolbar toolbar = findViewById(R.id.toolbar_contact_tech_support);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Contactar con el Servicio Técnico");
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
                // Aquí puedes manejar el envío del mensaje, por ejemplo, enviándolo a un servidor o a un correo electrónico
                Toast.makeText(ContactTechSupportActivity.this, "Mensaje enviado:\n" + title + "\n" + message, Toast.LENGTH_SHORT).show();
            }
        });

        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
