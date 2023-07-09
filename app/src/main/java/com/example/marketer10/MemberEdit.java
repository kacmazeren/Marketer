package com.example.marketer10;

import static androidx.core.location.LocationManagerCompat.getCurrentLocation;
import android.Manifest;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MemberEdit extends AppCompatActivity {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    FusedLocationProviderClient fusedLocationProviderClient;
    EditText editTextName, editTextSurname, editTextPhone, editTextEmail, editTextAddress, editTextLocation;
    TextView memberNumberTextView;
    Button editButton, saveButton, deleteButton;
    DatabaseHelper databaseHelper;
    String email;
    ListView listViewMarkets;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member_edit);

        editTextName = findViewById(R.id.editTextName);
        editTextSurname = findViewById(R.id.editTextSurname);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPhone = findViewById(R.id.editTextPhone);
        editTextAddress = findViewById(R.id.editTextAddress);
        editTextLocation = findViewById(R.id.editTextLocation);
        memberNumberTextView = findViewById(R.id.editTextMemberNumber);
        editButton = findViewById(R.id.buttonEdit);
        saveButton = findViewById(R.id.buttonSave);
        deleteButton= findViewById(R.id.buttonDelete);
        databaseHelper = new DatabaseHelper(this);
        listViewMarkets = findViewById(R.id.listViewMarkets);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            // If permission is already granted, get current location
            getCurrentLocation();
        }

        Intent intent = getIntent();
        if (intent != null) {
            String memberData = intent.getStringExtra("memberData");
            String[] memberComponents = memberData.split(" - ");
            email = memberComponents[1];  // assuming email is at position 1

            // Set data to EditTexts
            editTextName.setText(memberComponents[2]);
            editTextSurname.setText(memberComponents[3]);
            editTextEmail.setText(email);
            editTextPhone.setText(memberComponents[4]);
            editTextAddress.setText(memberComponents[5]);
            memberNumberTextView.setText(memberComponents[7]);

            editTextName.setEnabled(false);
            editTextSurname.setEnabled(false);
            editTextEmail.setEnabled(false);
            editTextPhone.setEnabled(false);
            editTextAddress.setEnabled(false);
            memberNumberTextView.setEnabled(false);
        }

        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editTextName.setEnabled(true);
                editTextSurname.setEnabled(true);
                editTextEmail.setEnabled(true);
                editTextPhone.setEnabled(true);
                editTextAddress.setEnabled(true);
                saveButton.setVisibility(View.VISIBLE);
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String newEmail = editTextEmail.getText().toString();
                String newName = editTextName.getText().toString();
                String newSurname = editTextSurname.getText().toString();
                String newPhone = editTextPhone.getText().toString();
                String newAddress = editTextAddress.getText().toString();
                String memberNumber = memberNumberTextView.getText().toString();

                boolean isUpdateSuccessful = databaseHelper.updateMemberData(memberNumber,  newEmail, newName, newSurname, newPhone, newAddress);

                if (isUpdateSuccessful) {
                    Toast.makeText(MemberEdit.this, "Update successful", Toast.LENGTH_SHORT).show();
                    editTextName.setEnabled(false);
                    editTextSurname.setEnabled(false);
                    editTextEmail.setEnabled(false);
                    editTextPhone.setEnabled(false);
                    editTextAddress.setEnabled(false);
                    saveButton.setVisibility(View.GONE);
                } else {
                    Toast.makeText(MemberEdit.this, "Update failed", Toast.LENGTH_SHORT).show();
                }
            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String memberNumber = memberNumberTextView.getText().toString();

                boolean isDeleteSuccessful = databaseHelper.deleteMember(memberNumber);

                if (isDeleteSuccessful) {
                    Toast.makeText(MemberEdit.this, "Member deleted", Toast.LENGTH_SHORT).show();
                    // Here, you may want to redirect the user to another Activity or do other appropriate action
                } else {
                    Toast.makeText(MemberEdit.this, "Deletion failed", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }
    private void getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if (location != null) {
                        double latitude = location.getLatitude();
                        double longitude = location.getLongitude();
                        getNearbyMarkets(latitude, longitude, "AIzaSyBzp53E_d1EqzoAaR0StZR6m_uuQi07nU0");

                        Geocoder geocoder = new Geocoder(MemberEdit.this, Locale.getDefault());
                        try {
                            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
                            Address address = addresses.get(0);
                            String addressText = address.getAddressLine(0);
                            editTextLocation.setText(addressText);

                        } catch (IOException e) {
                            editTextLocation.setText("Could not get address");
                            e.printStackTrace();
                        }

                    } else {
                        editTextLocation.setText("Could not get location");
                    }
                }
            });
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    public void getNearbyMarkets(double userLatitude, double userLongitude, String apiKey) {
        // Initialize Retrofit
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://maps.googleapis.com/maps/api/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        // Create an instance of the Places API interface
        PlacesApi placesApi = retrofit.create(PlacesApi.class);

        // Make the request
        Call<PlacesResponse> call = placesApi.getNearbyPlaces(
                userLatitude + "," + userLongitude,
                2000,
                "supermarket",
                apiKey
        );

        call.enqueue(new Callback<PlacesResponse>() {
            @Override
            public void onResponse(Call<PlacesResponse> call, Response<PlacesResponse> response) {
                if (response.isSuccessful()) {
                    PlacesResponse placesResponse = response.body();

                    // Add markets to listViewMarkets.
                    List<Market> markets = placesResponse.getResults();

                    // Perform reverse geocoding for each market to get the address
                    Geocoder geocoder = new Geocoder(MemberEdit.this, Locale.getDefault());

                    for (Market market : markets) {
                        double latitude = market.getGeometry().getLocation().getLat();
                        double longitude = market.getGeometry().getLocation().getLng();

                        try {
                            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
                            if (addresses != null && !addresses.isEmpty()) {
                                Address address = addresses.get(0);
                                String marketAddress = address.getAddressLine(0);
                                market.setFormatted_address(marketAddress);

                                // Calculate distance between user location and market location
                                float[] results = new float[1];
                                Location.distanceBetween(userLatitude, userLongitude, latitude, longitude, results);
                                float distance = results[0];

                                // Set distance in Market object
                                market.setDistance(distance);
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    ArrayAdapter<Market> adapter = new ArrayAdapter<>(MemberEdit.this, android.R.layout.simple_list_item_1, markets);
                    listViewMarkets.setAdapter(adapter);
                }
            }

            @Override
            public void onFailure(Call<PlacesResponse> call, Throwable t) {
                // Handle failure
            }
        });
    }
}
