package com.example.timetracker;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class HorariosActivity extends AppCompatActivity {

    private static final String TAG = "HorariosActivity";
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_horarios);

        db = FirebaseFirestore.getInstance();

        RecyclerView recyclerView = findViewById(R.id.recyclerViewHorarios);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        loadHorarios(recyclerView);
    }

    private void loadHorarios(RecyclerView recyclerView) {
        db.collection("horarios")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            List<Horario> horarioList = new ArrayList<>();
                            for (DocumentSnapshot document : task.getResult()) {
                                String workerId = document.getString("workerId");
                                String entrada = document.getString("entrada");
                                String salida = document.getString("salida");
                                horarioList.add(new Horario(workerId, entrada, salida));
                            }
                            HorariosAdapter adapter = new HorariosAdapter(horarioList);
                            recyclerView.setAdapter(adapter);
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });
    }
}
