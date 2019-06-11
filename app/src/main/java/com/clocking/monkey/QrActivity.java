package com.clocking.monkey;


import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class QrActivity extends AppCompatActivity {



    private Button buttonScan;
    private final int PERMISSIONS_REQUEST_CAMERA = 1;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.qr_activity);

        initPermissions();
        initUI();


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,}, 1000);
        } else {
            locationStart();
        }
    }
    public void setLocation ( final Location location){

        if (location.getLatitude() != 0.0 && location.getLongitude() != 0.0) {
            try {
                Geocoder geocoder = new Geocoder(this, Locale.getDefault());
                List<Address> list = geocoder.getFromLocation(
                        location.getLatitude(), location.getLongitude(), 1);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        buttonScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(QrActivity.this, ScanActivity.class);
                startActivity(intent);


                //location
                float metros = 10;
                float[] distance = new float[1];
                Location.distanceBetween(38.094259, -3.631208, location.getLatitude(), location.getLongitude(), distance);

                if (distance[0] / metros > 1) {
                    Toast.makeText(getApplicationContext(), "Estas fuera del area para poder fichar. Area: " + metros + " m",
                            Toast.LENGTH_LONG).show();
                } else {

                    Toast.makeText(getApplicationContext(), "¡¡Has fichado correctamente!!." + "\n" + "Estas dentro del rango de: " + metros + " m",
                            Toast.LENGTH_LONG).show();


                }
            }
        });
    }

            private void initUI () {
                buttonScan = findViewById(R.id.button_scan_qr);
            }


            private void initPermissions() {
                if (ActivityCompat.checkSelfPermission(QrActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) ;
                        requestPermissions(new String[]{Manifest.permission.CAMERA}, PERMISSIONS_REQUEST_CAMERA);
                    }
                    return;
                }
            }


            private void locationStart () {
                LocationManager mlocManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                QrActivity.Localizacion Local = new QrActivity.Localizacion();
                Local.setQrActivity(this);


                final boolean gpsEnabled = mlocManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
                if (!gpsEnabled) {
                    Intent settingsIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(settingsIntent);
                }
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,}, 1000);
                    return;
                }

                mlocManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, (LocationListener) Local);
                mlocManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, (LocationListener) Local);

            }

            public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
                if (requestCode == 1000) {
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        locationStart();
                        return;
                    }
                }
            }

            public class Localizacion implements LocationListener {
                QrActivity qrActivity;

                public QrActivity getMainActivity() {
                    return qrActivity;
                }

                public void setQrActivity(QrActivity gps) {
                    this.qrActivity = gps;
                }

                @Override
                public void onLocationChanged(Location location) {

                    location.getLatitude();
                    location.getLongitude();

                }

                @Override
                public void onProviderEnabled(String provider) {
                    Log.e("mensaje", "GPS activado");

                }

                @Override
                public void onProviderDisabled(String provider) {
                    Log.e("mensaje", "GPS desactivado");

                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {

                    switch (status) {
                        case LocationProvider.AVAILABLE:
                            Log.d("debug", "LocationProvider.AVAILABLE");
                            break;
                        case LocationProvider.OUT_OF_SERVICE:
                            Log.d("debug", "LocationProvider.OUT_OF_SERVICE");
                            break;
                        case LocationProvider.TEMPORARILY_UNAVAILABLE:
                            Log.d("debug", "LocationProvider.TEMPORARILY_UNAVAILABLE");
                            break;
                    }

                }


            }
}







