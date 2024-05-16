package com.example.timetracker;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Save extends AppCompatActivity {
    private EditText etName,etId,etEmail,etPoblacion,etApellido;

    private TextView tvShow;
    private Button bSend,bRead;
    private DatabaseReference rootDatabase;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.prueba);

        bSend = findViewById(R.id.btn);
        bRead = findViewById(R.id.btnRead);

        etName = findViewById(R.id.inputText);
        etId = findViewById(R.id.inputId);
        etEmail = findViewById(R.id.inputEmail);
        etApellido = findViewById(R.id.inputApellido);
        etPoblacion = findViewById(R.id.inputPoblacio);


        tvShow = findViewById(R.id.tvRead);
        rootDatabase = FirebaseDatabase.getInstance().getReference().child("Users");

        bSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String data = etName.getText().toString();
                int id = Integer.parseInt(etId.getText().toString());
                String apellido = etApellido.getText().toString();
                String email = etEmail.getText().toString();
                String poblacion = etPoblacion.getText().toString();

                Person person = new Person();
                person.setEmpleadoId(id);
                person.setNombre(data);
                person.setApellido(apellido);
                person.setEmail(email);
                person.setPoblacion(poblacion);

                rootDatabase.setValue(person).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(Save.this, "Se ha añadido el usuario", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        bRead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rootDatabase.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()){
                            String data = snapshot.getValue().toString();
                            tvShow.setText(data);

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });



    }
}
