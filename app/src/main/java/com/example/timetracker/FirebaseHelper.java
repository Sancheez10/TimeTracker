package com.example.timetracker;

import android.net.Uri;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FirebaseHelper {
    private DatabaseReference databaseReference;
    private StorageReference storageReference;

    public FirebaseHelper() {
        databaseReference = FirebaseDatabase.getInstance().getReference();
        storageReference = FirebaseStorage.getInstance().getReference();
    }

    // 1. Agregar un trabajador
    public void addWorker(Worker worker, final DataStatus dataStatus) {
        String key = databaseReference.child("workers").push().getKey();
        worker.setId(key);
        databaseReference.child("workers").child(key).setValue(worker)
                .addOnSuccessListener(aVoid -> dataStatus.DataIsInserted())
                .addOnFailureListener(e -> dataStatus.onError(e.getMessage()));
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
                dataStatus.onError(databaseError.getMessage());
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
                dataStatus.onError(databaseError.getMessage());
            }
        });
    }

    // 5. Actualizar un trabajador
    public void updateWorker(Worker worker, final DataStatus dataStatus) {
        databaseReference.child("workers").child(worker.getId()).setValue(worker)
                .addOnSuccessListener(aVoid -> dataStatus.DataIsUpdated())
                .addOnFailureListener(e -> dataStatus.onError(e.getMessage()));
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

    // 9. Obtener trabajadores por nombre
    public void getWorkersByName(String name, final DataStatus dataStatus) {
        databaseReference.child("workers").orderByChild("nombre").startAt(name).endAt(name + "\uf8ff")
                .addListenerForSingleValueEvent(new ValueEventListener() {
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
                        dataStatus.onError(databaseError.getMessage());
                    }
                });
    }

    // Métodos para manejar anotaciones

    // 10. Agregar una anotación
    public void addAnotacion(Anotacion anotacion, final DataStatus dataStatus) {
        String key = databaseReference.child("anotaciones").push().getKey();
        anotacion.setId(key);
        databaseReference.child("anotaciones").child(key).setValue(anotacion)
                .addOnSuccessListener(aVoid -> dataStatus.DataIsInserted())
                .addOnFailureListener(e -> dataStatus.onError(e.getMessage()));
    }

    // 11. Obtener todas las anotaciones
    public void getAllAnotaciones(final DataStatus dataStatus) {
        databaseReference.child("anotaciones").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<Anotacion> anotaciones = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Anotacion anotacion = snapshot.getValue(Anotacion.class);
                    anotaciones.add(anotacion);
                }
                dataStatus.DataIsLoaded(anotaciones);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                dataStatus.onError(databaseError.getMessage());
            }
        });
    }

    // 12. Eliminar una anotación
    public void deleteAnotacion(String anotacionId, final DataStatus dataStatus) {
        databaseReference.child("anotaciones").child(anotacionId).removeValue()
                .addOnSuccessListener(aVoid -> dataStatus.DataIsDeleted())
                .addOnFailureListener(e -> dataStatus.onError(e.getMessage()));
    }

    // 13. Subir archivo a Firebase Storage
    public void uploadFile(Uri fileUri, final FileUploadCallback fileUploadCallback) {
        StorageReference fileRef = storageReference.child("anotaciones/" + fileUri.getLastPathSegment());
        fileRef.putFile(fileUri)
                .addOnSuccessListener(taskSnapshot -> fileRef.getDownloadUrl().addOnSuccessListener(uri -> fileUploadCallback.onFileUploaded(uri.toString())))
                .addOnFailureListener(e -> fileUploadCallback.onError(e.getMessage()));
    }

    // 14. Añadir un grupo
    public void addGroup(Map<String, Object> group, final DataStatus dataStatus) {
        String key = databaseReference.child("groups").push().getKey();
        databaseReference.child("groups").child(key).setValue(group)
                .addOnSuccessListener(aVoid -> dataStatus.DataIsInserted())
                .addOnFailureListener(e -> dataStatus.onError(e.getMessage()));
    }

    // 15. Obtener todos los grupos
    public void getAllGroups(final DataStatus dataStatus) {
        databaseReference.child("groups").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<Group> groups = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Group group = snapshot.getValue(Group.class);
                    groups.add(group);
                }
                dataStatus.DataIsLoaded(groups);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                dataStatus.onError(databaseError.getMessage());
            }
        });
    }

    // 16. Eliminar un grupo
    public void deleteGroup(String groupId, final DataStatus dataStatus) {
        databaseReference.child("groups").child(groupId).removeValue()
                .addOnSuccessListener(aVoid -> dataStatus.DataIsDeleted())
                .addOnFailureListener(e -> dataStatus.onError(e.getMessage()));
    }

    public interface DataStatus {
        void DataIsLoaded(List<?> data);

        void DataIsInserted();

        void DataIsUpdated();

        void DataIsDeleted();

        void onError(String errorMessage);
    }

    public interface FileUploadCallback {
        void onFileUploaded(String fileUrl);

        void onError(String errorMessage);
    }
}
