package com.example.timetracker;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class LegalNoticeActivity extends AppCompatActivity {

    private Button closeButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_legal_notice);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Aviso Legal");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        closeButton = findViewById(R.id.close_button);

        closeButton.setOnClickListener(v -> finish());
    }
}
