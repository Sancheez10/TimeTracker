package com.example.timetracker;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        Toolbar toolbar = findViewById(R.id.toolbar_about);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Acerca de");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Button closeButton = findViewById(R.id.close_button);
        closeButton.setOnClickListener(v -> finish());
    }
}
