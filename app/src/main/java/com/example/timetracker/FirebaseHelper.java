package com.example.timetracker;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class FirebaseHelper {
    private DatabaseReference databaseReference;

    public FirebaseHelper() {
        databaseReference = FirebaseDatabase.getInstance().getReference();
    }

    // 1. Agregar un trabajador
    public void addWorker(Worker worker) {
        String key = databaseReference.child("workers").push().getKey();
        worker.setId(key);
        databaseReference.child("workers").child(key).setValue(worker);
    }

    // 2. Agregar un registro horario
    public void addWorkLog(WorkLog workLog) {
        String key = databaseReference.child("workLogs").push().getKey();
        workLog.setId(key);
        databaseReference.child("workLogs").child(key).setValue(workLog);
    }

    // 3. Obtener todos los trabajadores
    public void getAllWorkers(final DataStatus dataStatus) {
        databaseReference.child("workers").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<Worker> workers = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Worker worker = snapshot.getValue(Worker.class);
                    workers.add(worker);
                }
                dataStatus.DataIsLoaded(workers);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                dataStatus.DataIsLoaded(null);
            }
        });
    }

    // 4. Obtener todos los registros horarios
    public void getAllWorkLogs(final DataStatus dataStatus) {
        databaseReference.child("workLogs").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<WorkLog> workLogs = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    WorkLog workLog = snapshot.getValue(WorkLog.class);
                    workLogs.add(workLog);
                }
                dataStatus.DataIsLoaded(workLogs);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                dataStatus.DataIsLoaded(null);
            }
        });
    }

    // 5. Actualizar un trabajador
    public void updateWorker(Worker worker) {
        databaseReference.child("workers").child(worker.getId()).setValue(worker);
    }

    // 6. Actualizar un registro horario
    public void updateWorkLog(WorkLog workLog) {
        databaseReference.child("workLogs").child(workLog.getId()).setValue(workLog);
    }

    // 7. Eliminar un trabajador
    public void deleteWorker(String workerId) {
        databaseReference.child("workers").child(workerId).removeValue();
    }

    // 8. Eliminar un registro horario
    public void deleteWorkLog(String workLogId) {
        databaseReference.child("workLogs").child(workLogId).removeValue();
    }

    // Interfaz para manejar la respuesta de Firebase
    public interface DataStatus {
        void DataIsLoaded(List<?> data);
    }
}

