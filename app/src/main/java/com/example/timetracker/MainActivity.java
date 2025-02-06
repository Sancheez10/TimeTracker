package com.example.timetracker;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
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

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
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
    private boolean hasEntryTime = false;

    private Button bFinishWork;



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
        sharedPreferences = getSharedPreferences("workers_pref", Context.MODE_PRIVATE);

        // Obtener el correo electrónico del usuario desde SharedPreferences
        String email = sharedPreferences.getString("email", "Usuario");

        // Mostrar un mensaje de bienvenida
        tvWelcome.setText("Bienvenido, " + email);

        checkLocationSettings();

        bFinishWork = findViewById(R.id.bFinishWork); // Vincular el botón
        bFinishWork.setVisibility(View.INVISIBLE);

        databaseRef = FirebaseDatabase.getInstance().getReference("Timer");
        tvTimer.setText("00:00:00");

        bWorkStatus.setVisibility(View.INVISIBLE);
        checkLocationPermissionAndRequestUpdates();

        bWorkStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bClickWorkButton(v); // Llama al método bClickWorkButton en el clic del botón
            }
        });

    }

    private void startTimer() {
        handler.postDelayed(timerRunnable, 1000); // Actualizar cada segundo
    }

    public void bClickWorkButton(View view) {
        if (!isWorking) {
            startTime = System.currentTimeMillis();
            isWorking = true;
            tvStatus.setText("Trabajando");
            bWorkStatus.setText("Detener");

            // Hacer visible el botón de "Finalizar Trabajo"
            bFinishWork.setVisibility(View.VISIBLE);

            String address = ((TextView)findViewById(R.id.getLocationButton)).getText().toString();
            if (!hasEntryTime) {
                saveEntryToDatabase(startTime, address);
                hasEntryTime = true;
            }
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

    public void finishWork(View view) {
        isWorking = false;
        tvStatus.setText("Trabajo finalizado");
        bWorkStatus.setText("Iniciar nuevo día");

        saveExitToDatabase(System.currentTimeMillis());
        stopTimer();
        calculateAndDisplayTotalTime();

        // Reiniciar variables para el nuevo día
        hasEntryTime = false;
        startTime = 0;
        endTime = 0;
        elapsedTime = 0;

        // Reiniciar UI
        tvTimer.setText("00:00:00");

        // Reiniciar el total de horas trabajadas en SharedPreferences
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("totalTimeWorked", "00:00:00");
        editor.apply();
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

        MenuItem adminMenuItem = menu.findItem(R.id.action_Admin);


        boolean isAdmin = sharedPreferences.getBoolean("isAdmin", false);

        if (isAdmin) {
            adminMenuItem.setVisible(true);
        } else {
            adminMenuItem.setVisible(false);
        }

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
            Intent intent = new Intent(this, ConfiguracionActivity.class);
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
        } else if (item.getItemId() == R.id.action_logout) {
            logout();
            return true;
        } else if (item.getItemId() == R.id.action_worker_list) {
            Intent intent = new Intent(this, WorkerListActivity.class);
            startActivity(intent);
            return true;
        } else {
            // If we got here, the user's action was not recognized.
            // Invoke the superclass to handle it.
            return super.onOptionsItemSelected(item);
        }
    }

    private void logout() {
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(MainActivity.this, AuthActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
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

    private void checkLocationPermissionAndRequestUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            // Si el permiso está concedido, comenzar a escuchar las actualizaciones de ubicación
            checkLocationSettingsAndShowMessage();
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, new MyLocationListener());
        } else {
            // Si el permiso no está concedido, solicitarlo al usuario
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }
    }

    private class MyLocationListener implements LocationListener {
        @Override
        public void onLocationChanged(Location location) {
            if (firstLocation == null) {
                firstLocation = location;

                // Obtener la dirección completa utilizando la geocodificación inversa
                Geocoder geocoder = new Geocoder(MainActivity.this, Locale.getDefault());
                try {
                    double latitude = firstLocation.getLatitude();
                    double longitude = firstLocation.getLongitude();
                    List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
                    if (addresses != null && addresses.size() > 0) {
                        Address address = addresses.get(0);
                        StringBuilder addressStringBuilder = new StringBuilder();

                        // Construir la dirección completa a partir de los diferentes componentes
                        for (int i = 0; i <= address.getMaxAddressLineIndex(); i++) {
                            addressStringBuilder.append(address.getAddressLine(i)).append(", ");
                        }

                        String completeAddress = addressStringBuilder.toString();
                        TextView tvCoordinates = findViewById(R.id.getLocationButton);
                        tvCoordinates.setText("Ubicación: " + completeAddress);

                        // Guardar la ubicación en la base de datos
                        saveEntryToDatabase(System.currentTimeMillis(), completeAddress);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

                // Mostrar el botón de iniciar trabajo si la ubicación se obtiene correctamente
                bWorkStatus.setVisibility(View.VISIBLE);
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



    private String formatDate(long timeInMillis) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Date date = new Date(timeInMillis);
        return sdf.format(date);
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

    private void saveEntryToDatabase(long entryTime, String address) {
        String userId = sharedPreferences.getString("userId", "");
        String entryTimeStr = formatTime(entryTime);
        String entryDateStr = formatDate(entryTime); // Obtener la fecha

        // Crear un nuevo nodo usando la fecha como identificador
        DatabaseReference userRef = databaseRef.child("Timer").child(userId).child(entryDateStr);

        // Guardar la hora y fecha de entrada del usuario
        userRef.child("entry_time").setValue(entryTimeStr);
        userRef.child("entry_date").setValue(entryDateStr);
        userRef.child("entry_address").setValue(address);
    }


    private void saveExitToDatabase(long exitTime) {
        String userId = sharedPreferences.getString("userId", "");
        String exitTimeStr = formatTime(exitTime);
        String entryDateStr = formatDate(exitTime); // Obtener la fecha actual

        // Referencia al nodo específico del día
        DatabaseReference userRef = databaseRef.child("Timer").child(userId).child(entryDateStr);

        // Guardar la hora de salida del usuario
        userRef.child("exit_time").setValue(exitTimeStr);
    }



    private String formatTime(long timeInMillis) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        Date date = new Date(timeInMillis);
        return sdf.format(date);
    }

    private void calculateAndDisplayTotalTime() {
        long sessionTime = endTime - startTime;

        // Obtener el tiempo total previo guardado en SharedPreferences
        String totalTimeStr = sharedPreferences.getString("totalTimeWorked", "00:00:00");
        long previousTotalTime = parseTimeToMilliseconds(totalTimeStr);

        // Sumar el tiempo nuevo trabajado al total previo
        long updatedTotalTime = previousTotalTime + sessionTime;

        // Convertir a formato HH:mm:ss
        int totalSeconds = (int) (updatedTotalTime / 1000);
        int hours = totalSeconds / 3600;
        int minutes = (totalSeconds % 3600) / 60;
        int seconds = totalSeconds % 60;
        String newTotalTimeStr = String.format(Locale.getDefault(), "%02d:%02d:%02d", hours, minutes, seconds);

        // Guardar el tiempo total actualizado en la base de datos
        String userId = sharedPreferences.getString("userId", "");
        String entryDateStr = formatDate(System.currentTimeMillis()); // Usar la fecha actual
        DatabaseReference userRef = databaseRef.child("Timer").child(userId).child(entryDateStr);
        userRef.child("total_hours_worked").setValue(newTotalTimeStr);

        // Guardar el tiempo total actualizado en SharedPreferences
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("totalTimeWorked", newTotalTimeStr);
        editor.apply();

        // Actualizar la UI
        tvTimer.setText(newTotalTimeStr);
    }

    // Método auxiliar para convertir HH:mm:ss a milisegundos
    private long parseTimeToMilliseconds(String time) {
        String[] parts = time.split(":");
        if (parts.length == 3) {
            int hours = Integer.parseInt(parts[0]);
            int minutes = Integer.parseInt(parts[1]);
            int seconds = Integer.parseInt(parts[2]);
            return (hours * 3600 + minutes * 60 + seconds) * 1000L;
        }
        return 0;
    }



}
