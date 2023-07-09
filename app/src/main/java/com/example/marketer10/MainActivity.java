package com.example.marketer10;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private Button accountButton,adminButton ;
    private EditText searchBox;

    private MarketDBHelper dbHelper;
    private SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize the accountButton
        accountButton = findViewById(R.id.accountButton);

        // Set a click listener for the accountButton
        accountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Open a new page/activity to get the user's email
                Intent intent = new Intent(MainActivity.this, EmailActivity.class);
                startActivityForResult(intent, 1);
            }
        });

        adminButton = findViewById(R.id.adminButton);
        adminButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AdminActivity.class);
                startActivityForResult(intent, 1);
            }
        });
        searchBox = findViewById(R.id.searchBox);

        searchBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Run task to find nearby markets in the background
                getCurrentLocation();
            }
        });
        requestLocationPermissions();
    }

    class FindNearbyMarketsTask extends AsyncTask<Void, Void, List<Market>> {
        private double latitude;
        private double longitude;

        public FindNearbyMarketsTask(double latitude, double longitude) {
            this.latitude = latitude;
            this.longitude = longitude;
        }
            @Override
            protected List<Market> doInBackground(Void... voids) {
                // This is where you would get the user's location and
                // send a request to the API to find the nearby markets.
                // Since I don't know how your API works, I can't provide specific code for this.
                // The result should be a list of markets.
                List<Market> markets = new ArrayList<>();
                return markets;
            }

            protected void onPostExecute(List<Market> markets) {
                super.onPostExecute(markets);

                // ...

                for (Market market : markets) {
                    ContentValues values = new ContentValues();
                    values.put(MarketDBHelper.COLUMN_NAME, market.getName());
                    values.put(MarketDBHelper.COLUMN_ADDRESS, market.getFormatted_address());
                    values.put(MarketDBHelper.COLUMN_LATITUDE, market.getGeometry().getLocation().getLat());
                    values.put(MarketDBHelper.COLUMN_LONGITUDE, market.getGeometry().getLocation().getLng());

                    long newRowId = db.insert(MarketDBHelper.TABLE_MARKETS, null, values);
                    Log.d("MainActivity", "Inserted market with row id: " + newRowId);
                }
            }

    }
    private void requestLocationPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            // If permission is already granted, get current location
            getCurrentLocation();
        }
    }
    private void getCurrentLocation() {
        FusedLocationProviderClient fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if (location != null) {
                        double latitude = location.getLatitude();
                        double longitude = location.getLongitude();
                        Log.d("MainActivity", "Latitude: " + latitude + ", Longitude: " + longitude);
                        // Here, you can run your AsyncTask with the user's current location
                        new FindNearbyMarketsTask(latitude, longitude).execute();
                    } else {
                        Log.d("MainActivity", "Could not get location");
                    }
                }
            });
        } else {
            requestLocationPermissions();
        }
    }

}
