package com.example.timetracker;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;

import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.Manifest;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private Button getLocationButton, bWorkStatus;
    private LocationManager locationManager;
    private Toolbar toolbar_main;
    private long startTime, endTime;
    private TextView tvTimer, tvStatus;
    private Location firstLocation = null;
    private DatabaseReference databaseRef;
    private boolean isWorking = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar_main = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar_main);

        tvTimer = findViewById(R.id.tvTimer);
        tvStatus = findViewById(R.id.tvStatusWork);
        bWorkStatus = findViewById(R.id.bWork);
        getLocationButton = findViewById(R.id.getLocationButton);

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

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
                } else {
                    endTime = System.currentTimeMillis();
                    isWorking = false;
                    tvStatus.setText("Parado");
                    bWorkStatus.setText("Iniciar");
                    saveExitToDatabase(endTime);
                    calculateAndDisplayTotalTime();
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar, menu);
        return true;
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
        String entryTimeStr = formatTime(entryTime);
        databaseRef.child(entryTimeStr).child("entry_time").setValue(entryTimeStr);
    }

    private void saveExitToDatabase(long exitTime) {
        String exitTimeStr = formatTime(exitTime);
        databaseRef.child(exitTimeStr).child("exit_time").setValue(exitTimeStr);
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
        tvTimer.setText(String.format("%02d:%02d:%02d", hours, minutes, seconds));
    }

}
