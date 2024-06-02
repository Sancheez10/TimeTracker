package com.example.timetracker;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.Manifest;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private Button getLocationButton, bWorkStatus;
    private LocationManager locationManager;
    private Toolbar toolbar_main;
    private long startTime, endTime, elapsedTime;
    private TextView tvTimer, tvStatus, tvWelcome;
    private Location firstLocation = null;
    private DatabaseReference databaseRef;
    private boolean isWorking = false;
    private SharedPreferences sharedPreferences;
    private Handler handler = new Handler();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar_main = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar_main);

        tvTimer = findViewById(R.id.tvTimer);
        tvStatus = findViewById(R.id.tvStatusWork);
        tvWelcome = findViewById(R.id.tvWelcome);
        bWorkStatus = findViewById(R.id.bWork);
        getLocationButton = findViewById(R.id.getLocationButton);

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        // Inicializar SharedPreferences
        sharedPreferences = getSharedPreferences("user_prefs", Context.MODE_PRIVATE);

        // Obtener el correo electrónico del usuario desde SharedPreferences
        String email = sharedPreferences.getString("email", "Usuario");

        // Mostrar un mensaje de bienvenida
        tvWelcome.setText("Bienvenido, " + email);

        checkLocationSettings();

        databaseRef = FirebaseDatabase.getInstance().getReference("Timer");

        bWorkStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isWorking) {
                    startTime = System.currentTimeMillis();
                    isWorking = true;
                    tvStatus.setText("Trabajando");
                    bWorkStatus.setText("Detener");
                    saveEntryToDatabase(startTime);
                    startTimer();
                } else {
                    endTime = System.currentTimeMillis();
                    isWorking = false;
                    tvStatus.setText("Parado");
                    bWorkStatus.setText("Iniciar");
                    saveExitToDatabase(endTime);
                    stopTimer();
                    calculateAndDisplayTotalTime();
                }
            }
        });
    }

    private void startTimer() {
        handler.postDelayed(timerRunnable, 1000); // Actualizar cada segundo
    }

    // Método para detener el contador
    private void stopTimer() {
        handler.removeCallbacks(timerRunnable);
    }

    private Runnable timerRunnable = new Runnable() {
        @Override
        public void run() {
            elapsedTime = System.currentTimeMillis() - startTime;
            updateTimer(elapsedTime);
            handler.postDelayed(this, 1000);
        }
    };

    // Método para actualizar el texto del contador
    private void updateTimer(long timeInMillis) {
        int seconds = (int) (timeInMillis / 1000);
        int hours = seconds / 3600;
        int minutes = (seconds % 3600) / 60;
        seconds = seconds % 60;
        String timeFormatted = String.format(Locale.getDefault(), "%02d:%02d:%02d", hours, minutes, seconds);
        tvTimer.setText(timeFormatted);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar, menu);

        // Obtener la referencia al elemento del menú del panel de administrador
        MenuItem adminMenuItem = menu.findItem(R.id.action_Admin);

        // Obtener el UID del usuario actualmente autenticado
        String currentUserUid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Obtener la referencia al documento del usuario actual en Firestore
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference userRef = db.collection("workers").document(currentUserUid);

        // Obtener los datos del documento del usuario actual
        userRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                // Obtener el valor del campo "isAdmin" del documento
                boolean isAdmin = documentSnapshot.getBoolean("isAdmin");

                // Verificar si el usuario es administrador y mostrar u ocultar la opción del panel de administrador
                if (isAdmin) {
                    adminMenuItem.setVisible(true);
                } else {
                    adminMenuItem.setVisible(false);
                }
            }
        }).addOnFailureListener(e -> {
            // Manejar cualquier error al obtener los datos del usuario
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.mis_cuentas) {
            Intent intent = new Intent(this, Cuentas.class);
            startActivity(intent);
            return true;
        }
        else if (item.getItemId() == R.id.registro_laboral) {
            Intent intent = new Intent(this, RegistroLaboral.class);
            startActivity(intent);
            return true;
        } else if (item.getItemId() == R.id.anotaciones) {
            Intent intent = new Intent(this, AddAnotacionActivity.class);
            startActivity(intent);
            return true;
        } else if (item.getItemId() == R.id.calendario_laboral) {
            Intent intent = new Intent(this, Calendario.class);
            startActivity(intent);
            return true;
        } else if (item.getItemId() == R.id.horarios) {
            Intent intent = new Intent(this, Horarios.class);
            startActivity(intent);
            return true;
        } else if (item.getItemId() == R.id.configuracion) {
            Intent intent = new Intent(this, Configuracion.class);
            startActivity(intent);
            return true;
        } else if (item.getItemId() == R.id.ayuda) {
            Intent intent = new Intent(this, Ayuda.class);
            startActivity(intent);
            return true;
        } else if (item.getItemId() == R.id.proteccion_de_datos) {
            Intent intent = new Intent(this, ProteccionDeDatosActivity.class);
            startActivity(intent);
            return true;
        } else if (item.getItemId() == R.id.action_Admin) {
            Intent intent = new Intent(this, PanellAdministrador.class);
            startActivity(intent);
            return true;
        } else {
            // If we got here, the user's action was not recognized.
            // Invoke the superclass to handle it.
            return super.onOptionsItemSelected(item);
        }
    }

    public void bClickUbication(View v) {
        if (v == getLocationButton) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                checkLocationSettingsAndShowMessage();
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, new MyLocationListener());
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }
        }
    }

    private class MyLocationListener implements LocationListener {
        @Override
        public void onLocationChanged(Location location) {
            if (firstLocation == null) {
                firstLocation = location;
                double latitude = firstLocation.getLatitude();
                double longitude = firstLocation.getLongitude();
                TextView tvCoordinates = findViewById(R.id.getLocationButton);
                tvCoordinates.setText("Ubicación: " + latitude + ", " + longitude);
            }
        }

        @Override
        public void onProviderEnabled(String provider) {
        }

        @Override
        public void onProviderDisabled(String provider) {
        }
    }

    private void checkLocationSettings() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        boolean isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        if (!isGpsEnabled) {
            getLocationButton.setText("Sin ubicación");
        } else {
            getLocationButton.setText("Obtener ubicación");
        }
    }

    private void checkLocationSettingsAndShowMessage() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        boolean isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        if (!isGpsEnabled) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Ubicación desactivada")
                    .setMessage("Por favor, active la ubicación para obtener su ubicación.")
                    .setPositiveButton("Activar ubicación", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            startActivity(intent);
                        }
                    })
                    .setNegativeButton("Cancelar", null)
                    .create()
                    .show();
        }
    }

    private void saveEntryToDatabase(long entryTime) {
        String userId = sharedPreferences.getString("userId", "");
        String entryTimeStr = formatTime(entryTime);

        // Crear un nuevo nodo para el usuario en la base de datos
        DatabaseReference userRef = databaseRef.child("Timer").child(userId);

        // Guardar la hora de entrada del usuario
        userRef.child("entry_time").setValue(entryTimeStr);
    }


    private void saveExitToDatabase(long exitTime) {
        String userId = sharedPreferences.getString("userId", "");
        String exitTimeStr = formatTime(exitTime);

        // Obtener la referencia al nodo del usuario en la base de datos
        DatabaseReference userRef = databaseRef.child("Timer").child(userId);

        // Guardar la hora de salida del usuario
        userRef.child("exit_time").setValue(exitTimeStr);
    }


    private String formatTime(long timeInMillis) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        Date date = new Date(timeInMillis);
        return sdf.format(date);
    }

    private void calculateAndDisplayTotalTime() {
        long totalTime = endTime - startTime;
        int totalSeconds = (int) (totalTime / 1000);
        int hours = totalSeconds / 3600;
        int minutes = (totalSeconds % 3600) / 60;
        int seconds = totalSeconds % 60;

        String userId = sharedPreferences.getString("userId", "");

        // Obtener la referencia al nodo del usuario en la base de datos
        DatabaseReference userRef = databaseRef.child("Timer").child(userId);

        // Obtener el tiempo total trabajado anteriormente por el usuario
        String previousTotalTimeStr = sharedPreferences.getString("totalTimeWorked", "00:00:00");

        // Convertir el tiempo total trabajado anteriormente a horas, minutos y segundos
        String[] previousTotalTimeParts = previousTotalTimeStr.split(":");
        int previousHours = Integer.parseInt(previousTotalTimeParts[0]);
        int previousMinutes = Integer.parseInt(previousTotalTimeParts[1]);
        int previousSeconds = Integer.parseInt(previousTotalTimeParts[2]);

        // Calcular el nuevo tiempo total trabajado sumando el tiempo anterior y el actual
        int newTotalSeconds = (previousHours * 3600) + (previousMinutes * 60) + previousSeconds + totalSeconds;
        int newHours = newTotalSeconds / 3600;
        int newMinutes = (newTotalSeconds % 3600) / 60;
        int newSeconds = newTotalSeconds % 60;

        // Formatear el nuevo tiempo total trabajado como una cadena HH:mm:ss
        String newTotalTimeStr = String.format(Locale.getDefault(), "%02d:%02d:%02d", newHours, newMinutes, newSeconds);

        // Guardar el nuevo tiempo total trabajado en las preferencias
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("totalTimeWorked", newTotalTimeStr);
        editor.apply();

        // Actualizar el tiempo total trabajado en la base de datos
        userRef.child("total_hours_worked").setValue(newTotalTimeStr);

        // Actualizar el TextView con el nuevo tiempo total trabajado
        tvTimer.setText(newTotalTimeStr);
    }


}
