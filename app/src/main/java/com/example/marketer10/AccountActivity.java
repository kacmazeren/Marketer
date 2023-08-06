package com.example.marketer10;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;

import android.location.Location;

import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AccountActivity extends AppCompatActivity implements OnMapReadyCallback {


    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    FusedLocationProviderClient fusedLocationProviderClient;
    EditText editTextName, editTextSurname, editTextPhone, editTextEmail, editTextAddress,editTextOldPassword, editTextNewPassword, editTextConfirmPassword;
    TextView memberNumberTextView;
    Button editButton, saveButton, buttonSavePassword, adminButton, markerTescoButton, shoppingButton, saveListButton, statisticButton;
    private ListView listViewTesco, listViewDunnes, listViewLidl;
    private ArrayList<String> groceryListTesco, groceryListDunnes, groceryListLidl;
    private ArrayAdapter<String> adapterTesco, adapterDunnes, adapterLidl;
    private GoogleMap mMap;
    private ArrayList<String> groceryList;
    private ArrayAdapter<String> adapter;
    private HashMap<String, Marker> markers = new HashMap<>();
    public double calculateTotalTesco() {
        double totalTescoPrice = 0.0;
        for (String item : groceryListTesco) {
            String[] parts = item.split(",");
            String priceWithDollar = parts[2]; // Second part is Price
            String priceWithoutDollar = priceWithDollar.replace("€", "");
            double price = Double.parseDouble(priceWithoutDollar);
            int quantity = Integer.parseInt(parts[3].trim()); // Third part is Quantity
            totalTescoPrice += price * quantity;
        }
        return totalTescoPrice;
    }

    public double calculateTotalLidl() {
        double totalLidlPrice = 0.0;
        for (String item : groceryListLidl) {
            String[] parts = item.split(",");
            String priceWithDollar = parts[2]; // Second part is Price
            String priceWithoutDollar = priceWithDollar.replace("€", "");
            double price = Double.parseDouble(priceWithoutDollar);
            int quantity = Integer.parseInt(parts[3].trim()); // Third part is Quantity
            totalLidlPrice += price * quantity;
        }
        return totalLidlPrice;
    }

    public double calculateTotalDunnes() {
        double totalDunnesPrice = 0.0;
        for (String item : groceryListDunnes) {
            String[] parts = item.split(",");
            String priceWithDollar = parts[2]; // Second part is Price
            String priceWithoutDollar = priceWithDollar.replace("€", "");
            double price = Double.parseDouble(priceWithoutDollar);
            int quantity = Integer.parseInt(parts[3].trim()); // Third part is Quantity
            totalDunnesPrice += price * quantity;
        }
        return totalDunnesPrice;
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this) == ConnectionResult.SUCCESS) {
            // Google Play Services are available
            setContentView(R.layout.activity_acoount);
            // Continue with map setup...
        } else {setContentView(R.layout.activity_acoount);
            // Google Play Services are not available.
            TextView errorMessage = findViewById(R.id.map_error_message);
            errorMessage.setVisibility(View.VISIBLE);
        }
        // Initialize EditTexts
        editTextName = findViewById(R.id.editTextName);
        editTextSurname = findViewById(R.id.editTextSurname);
        memberNumberTextView = findViewById(R.id.editTextMemberNumber);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPhone = findViewById(R.id.editTextPhone);
        editTextAddress = findViewById(R.id.editTextAddress);
        statisticButton = findViewById(R.id.statisticButton);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            // If permission is already granted, get current location
            getCurrentLocation();
        }

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        LinearLayout userInfoLayout = (LinearLayout) findViewById(R.id.userInfoLayout);
        for (int i = 0; i < userInfoLayout.getChildCount(); i++) {
            View child = userInfoLayout.getChildAt(i);
            if (child instanceof EditText) {
                ((EditText) child).setEnabled(false); // or false
            }
        }


        // Create an instance of ShoppingListDbHelper
        ShoppingListDbHelper shoppingHelper = new ShoppingListDbHelper(this);


        Button shopHereButton = findViewById(R.id.shoppingButton);
        shopHereButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AccountActivity.this, MainActivity.class);
                intent.putExtra("fromAccountActivity", true);
                startActivity(intent);
            }
        });


        // Retrieve the groceryList from the Intent
        groceryList = getIntent().getStringArrayListExtra("groceryList");

        // Create ArrayAdapter using the grocery list
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, groceryList);

        listViewTesco = findViewById(R.id.listViewTesco);
        listViewDunnes = findViewById(R.id.listViewDunnes);
        listViewLidl = findViewById(R.id.listViewLidl);

        // Retrieve the groceryList from the Intent
        ArrayList<String> groceryList = getIntent().getStringArrayListExtra("groceryList");

        groceryListTesco = new ArrayList<>();
        groceryListDunnes = new ArrayList<>();
        groceryListLidl = new ArrayList<>();

        // Divide the groceryList into separate lists
        for (String item : groceryList) {
            String[] parts = item.split(",");
           String supermarket = parts[0];
            String productType = parts[1]; // Second part is ProductType
            String price = parts[2]; // Third part is Price
            int quantity = 1; // Initialize the quantity to 1
            String productInfo = supermarket + ", " + productType + ", " + price+ ", " + quantity;// Combine them into a single string

            if (item.contains("Tesco")) {
                groceryListTesco.add(productInfo);
            } else if (item.contains("Dunnes")) {
                groceryListDunnes.add(productInfo);
            } else if (item.contains("Lidl")) {
                groceryListLidl.add(productInfo);
            }
        }

        // Create ArrayAdapter using the grocery lists
        adapterTesco = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, groceryListTesco);
        adapterDunnes = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, groceryListDunnes);
        adapterLidl = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, groceryListLidl);

        // Set the ArrayAdapter as the ListView's adapter
        listViewTesco.setAdapter(adapterTesco);
        listViewDunnes.setAdapter(adapterDunnes);
        listViewLidl.setAdapter(adapterLidl);

        TextView totalTescoPrice = findViewById(R.id.totalTesco);
        TextView totalDunnesPrice = findViewById(R.id.totalDunnes);
        TextView totalLidlPrice = findViewById(R.id.totalLidl);


        double initialTotalTesco = calculateTotalTesco();
        totalTescoPrice.setText("Total: " + String.format("%.2f", initialTotalTesco));

        double initialTotalDunnes = calculateTotalDunnes();
        totalDunnesPrice.setText("Total: " + String.format("%.2f", initialTotalDunnes));

        double initialTotalLidl = calculateTotalLidl();
        totalLidlPrice.setText("Total: " + String.format("%.2f", initialTotalLidl));

        listViewTesco.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = (String) parent.getItemAtPosition(position);
                String[] parts = selectedItem.split(",");
                String supermarket = parts[0];
                String productType = parts[1]; // First part is ProductType
                String price = parts[2]; // Second part is Price
                String quantity = parts[3]; // Third part is Quantity

                AlertDialog.Builder builder = new AlertDialog.Builder(AccountActivity.this);
                builder.setTitle("Edit Quantity");

                // Set up the input
                final EditText input = new EditText(AccountActivity.this);
                // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
                input.setInputType(InputType.TYPE_CLASS_NUMBER);
                input.setText(quantity);
                builder.setView(input);

                // Set up the buttons
                builder.setPositiveButton("Set Quantity", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String newQuantity = input.getText().toString();
                        String newProductInfo = supermarket + ", " + productType + ", " + price + ", " + newQuantity;
                        groceryListTesco.set(position, newProductInfo); // Update the item at the current position
                        adapterTesco.notifyDataSetChanged(); // Notify the adapter about the change

                        double newTotal = calculateTotalTesco(); // Call the method directly
                        totalTescoPrice.setText("Total: " + String.format("%.2f", newTotal)); // Set the calculated total to the TextView
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder.show();
            }
        });



        listViewDunnes.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = (String) parent.getItemAtPosition(position);
                String[] parts = selectedItem.split(",");
                String supermarket = parts[0];
                String productType = parts[1]; // First part is ProductType
                String price = parts[2]; // Second part is Price
                String quantity = parts[3]; // Third part is Quantity

                AlertDialog.Builder builder = new AlertDialog.Builder(AccountActivity.this);
                builder.setTitle("Edit Quantity");

                // Set up the input
                final EditText input = new EditText(AccountActivity.this);
                // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
                input.setInputType(InputType.TYPE_CLASS_NUMBER);
                input.setText(quantity);
                builder.setView(input);

                // Set up the buttons
                builder.setPositiveButton("Set Quantity", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String newQuantity = input.getText().toString();
                        String newProductInfo = supermarket + ", " + productType + ", " + price + ", " + newQuantity;
                        groceryListDunnes.set(position, newProductInfo); // Update the item at the current position
                        adapterDunnes.notifyDataSetChanged(); // Notify the adapter about the change

                        double newTotal = calculateTotalDunnes(); // Call the method directly
                        totalDunnesPrice.setText("Total: " + String.format("%.2f", newTotal)); // Set the calculated total to the TextView
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder.show();
            }
        });

        listViewLidl.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = (String) parent.getItemAtPosition(position);
                String[] parts = selectedItem.split(",");
                String supermarket = parts[0];
                String productType = parts[1]; // First part is ProductType
                String price = parts[2]; // Second part is Price
                String quantity = parts[3]; // Third part is Quantity

                AlertDialog.Builder builder = new AlertDialog.Builder(AccountActivity.this);
                builder.setTitle("Edit Quantity");

                // Set up the input
                final EditText input = new EditText(AccountActivity.this);
                // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
                input.setInputType(InputType.TYPE_CLASS_NUMBER);
                input.setText(quantity);
                builder.setView(input);

                // Set up the buttons
                builder.setPositiveButton("Set Quantity", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String newQuantity = input.getText().toString();
                        String newProductInfo = supermarket + ", " + productType + ", " + price + ", " + newQuantity;
                        groceryListLidl.set(position, newProductInfo); // Update the item at the current position
                        adapterLidl.notifyDataSetChanged(); // Notify the adapter about the change

                        double newTotal = calculateTotalLidl(); // Call the method directly
                        totalLidlPrice.setText("Total: " + String.format("%.2f", newTotal)); // Set the calculated total to the TextView
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder.show();
            }
        });

        // Fetch email from Intent
        String email = getIntent().getStringExtra("email");

        // Get member data
        DatabaseHelper db = new DatabaseHelper(AccountActivity.this);
        Cursor cursor = db.getMemberData(email);

        if (cursor != null && cursor.moveToFirst()) {
            // Fetch data from cursor
            int nameIndex = cursor.getColumnIndex("name");
            int surnameIndex = cursor.getColumnIndex("surname");
            int memberNumberIndex = cursor.getColumnIndex("member_number");
            int phoneIndex = cursor.getColumnIndex("phone");
            int addressIndex = cursor.getColumnIndex("address");

            if (nameIndex != -1 && surnameIndex != -1 && memberNumberIndex != -1 && phoneIndex != -1 && addressIndex != -1) {
                String name = cursor.getString(nameIndex);
                String surname = cursor.getString(surnameIndex);
                String memberNumber = cursor.getString(memberNumberIndex);
                String phone = cursor.getString(phoneIndex);
                String address = cursor.getString(addressIndex);

                // Set data to EditTexts
                editTextName.setText(name);
                editTextSurname.setText(surname);
                memberNumberTextView.setText(memberNumber);
                editTextEmail.setText(email);
                editTextPhone.setText(phone);
                editTextAddress.setText(address);
            } else {
                // Handle case where a column name was not found in the database
                Toast.makeText(this, "A database error occurred.", Toast.LENGTH_SHORT).show();
            }

            cursor.close();  // Don't forget to close the cursor
        }

        ShoppingListDbHelper dbHelper = new ShoppingListDbHelper(AccountActivity.this);
        String memberNumber = memberNumberTextView.getText().toString();

        saveListButton = findViewById(R.id.saveListButton);
        if(memberNumber.startsWith("S")) {
            saveListButton.setVisibility(View.INVISIBLE);
        }else {
            saveListButton.setVisibility(View.VISIBLE);
        }
        saveListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (groceryList != null) {
                    for (String item : groceryList) {
                        String[] parts = item.split(",");
                        // Get current date
                        String date = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
                        dbHelper.insertIntoSavedList(
                                date,          // date
                                parts[0],      // supermarket
                                parts[1],      // productType
                                parts[2],      // price
                                memberNumber   // memberNumber
                        );
                    }
                    Toast.makeText(AccountActivity.this, "Saved", Toast.LENGTH_SHORT).show();

                }
            }
        });


        if (memberNumber.startsWith("G")) {
            statisticButton.setVisibility(View.VISIBLE);
        } else {
            statisticButton.setVisibility(View.GONE);
        }


        Button statisticButton = findViewById(R.id.statisticButton);

        statisticButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(AccountActivity.this,Statistic.class);
                intent.putExtra("memberNumber", memberNumber);
                startActivity(intent);
            }
        });



        editButton = findViewById(R.id.buttonEdit);
        saveButton = findViewById(R.id.buttonSave);

        // Set onClick listener for the edit button
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Enable EditTexts and make the save button visible
                editTextName.setEnabled(true);
                editTextSurname.setEnabled(true);
                editTextPhone.setEnabled(true);
                editTextEmail.setEnabled(true);
                editTextAddress.setEnabled(true);

                saveButton.setVisibility(View.VISIBLE);
                userInfoLayout.setVisibility(View.VISIBLE);
            }
        });

        // Set onClick listener for the save button
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String newEmail = editTextEmail.getText().toString();
                String newName = editTextName.getText().toString();
                String newSurname = editTextSurname.getText().toString();
                String newPhone = editTextPhone.getText().toString();
                String newAddress = editTextAddress.getText().toString();
                String memberNumber = memberNumberTextView.getText().toString();

                boolean isUpdateSuccessful = db.updateMemberData(memberNumber, newEmail, newName, newSurname, newPhone, newAddress);

                if (isUpdateSuccessful) {
                    Toast.makeText(AccountActivity.this, "Update successful", Toast.LENGTH_SHORT).show();
                    // Make EditTexts uneditable again and hide save button
                    editTextName.setEnabled(false);
                    editTextSurname.setEnabled(false);
                    editTextPhone.setEnabled(false);
                    editTextEmail.setEnabled(false);
                    editTextAddress.setEnabled(false);
                    saveButton.setVisibility(View.GONE);
                } else {
                    Toast.makeText(AccountActivity.this, "Update failed", Toast.LENGTH_SHORT).show();
                }
            }
        });

        final Button buttonChangePassword = findViewById(R.id.buttonChangePassword);
        final LinearLayout passwordLayout = findViewById(R.id.passwordLayout);

        buttonChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                passwordLayout.setVisibility(View.VISIBLE);
            }
        });

        editTextOldPassword = findViewById(R.id.editTextOldPassword);
        editTextNewPassword = findViewById(R.id.editTextNewPassword);
        editTextConfirmPassword = findViewById(R.id.editTextConfirmPassword);
        buttonSavePassword = findViewById(R.id.buttonSavePassword);
        buttonSavePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String oldPassword = editTextOldPassword.getText().toString();
                String newPassword = editTextNewPassword.getText().toString();
                String confirmPassword = editTextConfirmPassword.getText().toString();

                // Fetch current password from the database
                String currentPassword = db.getPassword(email);

                // Check if old password is correct
                if (!PasswordHelper.checkPassword(oldPassword, currentPassword)) {
                    Toast.makeText(AccountActivity.this, "Old password is incorrect", Toast.LENGTH_SHORT).show();
                    return;
                }


                // Check if new password and confirm password fields match
                if (!newPassword.equals(confirmPassword)) {
                    Toast.makeText(AccountActivity.this, "New password and confirm password do not match", Toast.LENGTH_SHORT).show();
                    return;
                }
                // Check if new password is strong
                if (!PasswordHelper.isPasswordStrong(newPassword)) {
                    Toast.makeText(AccountActivity.this, "New password is not strong enough", Toast.LENGTH_SHORT).show();
                    return;
                }
                // Update password in the database
                boolean isUpdateSuccessful = db.updatePassword(email, newPassword);
                if (isUpdateSuccessful) {
                    Toast.makeText(AccountActivity.this, "Password update successful", Toast.LENGTH_SHORT).show();

                        } else {
                    Toast.makeText(AccountActivity.this, "Password update failed", Toast.LENGTH_SHORT).show();
                }
                // Hide the password layout
                passwordLayout.setVisibility(View.GONE);
            }
        });

        String userEmail = getIntent().getStringExtra("email");

        // Check if the user is an admin
        if (isAdmin(userEmail)) {
            // Find the Admin button by its ID and make it visible
            Button adminButton = findViewById(R.id.adminButton);
            adminButton.setVisibility(View.VISIBLE);

            // Set a click listener on the Admin button
            adminButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Start the AdminActivity
                    Intent intent = new Intent(AccountActivity.this, AdminActivity.class);
                    startActivity(intent);
                }
            });
        }
    }

    public boolean isAdmin(String userEmail) {
        String adminEmail = "kacmazeren66@gmail.com";
        return userEmail.equals(adminEmail);
    }

    private com.google.android.gms.maps.model.LatLng userLocation, dunnesLocation, tescoLocation , lidlLocation;


    public void getNearbyMarkets(double userLatitude, double userLongitude, String AIzaSyBzp53E_d1EqzoAaR0StZR6m_uuQi07nU0) {



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
                4000,
                "grocery_or_supermarket",
                AIzaSyBzp53E_d1EqzoAaR0StZR6m_uuQi07nU0
        );

        call.enqueue(new Callback<PlacesResponse>() {
            @Override
            public void onResponse(Call<PlacesResponse> call, Response<PlacesResponse> response) {
                if (response.isSuccessful()) {
                    PlacesResponse placesResponse = response.body();

                    // Filter markets to include only Tesco, Dunnes, and Lidl.
                    List<Market> markets = placesResponse.getResults();

                    for (Market market : markets) {
                        double latitude = market.getGeometry().getLocation().getLat();
                        double longitude = market.getGeometry().getLocation().getLng();
                        String marketName = market.getName().toLowerCase();
                        com.google.android.gms.maps.model.LatLng marketLocation =
                                new com.google.android.gms.maps.model.LatLng(latitude, longitude);

                        float[] results = new float[1];
                        Location.distanceBetween(userLatitude, userLongitude, latitude, longitude, results);
                        float distance = results[0];

                        if (marketName.contains("tesco")) {
                            TextView tescoTextView = findViewById(R.id.distanceTesco);
                            tescoTextView.setText("Tesco: " + distance + " meters away");
                            mMap.addMarker(new MarkerOptions().position(marketLocation).title("Tesco"));
                            Marker tescoMarker = mMap.addMarker(new MarkerOptions().position(marketLocation).title("Tesco"));
                            markers.put("tesco", tescoMarker);

                            // Add 75 meters to Tesco's distance for Dunnes
                            //This is because Dunnes stores cannot find on Google Maps by name or Places ID.
                            TextView dunnesTextView = findViewById(R.id.distanceDunnes);
                            dunnesTextView.setText("Dunnes: " + (distance + 75) + " meters away");
                        }

                        if (marketName.contains("lidl")) {
                            TextView lidlTextView = findViewById(R.id.distanceLidl);
                            lidlTextView.setText("Lidl: " + distance + " meters away");
                            mMap.addMarker(new MarkerOptions().position(marketLocation).title("Lidl"));
                            Marker lidlMarker = mMap.addMarker(new MarkerOptions().position(marketLocation).title("Lidl"));
                            markers.put("lidl", lidlMarker);
                        }
                    }

                }
            }

            @Override
            public void onFailure(Call<PlacesResponse> call, Throwable t) {
                // Handle failure
            }
        });
    }

    public void markerTesco(View view) {
        Marker tescoMarker = markers.get("tesco");
        if (tescoMarker != null) {
            float zoomLevel = 16.0f; // Set your desired zoom level
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(tescoMarker.getPosition(), zoomLevel));
            tescoMarker.showInfoWindow();
        } else {
            Toast.makeText(this, "Tesco market could not be found.", Toast.LENGTH_SHORT).show();
        }
    }

    public void markerLidl(View view) {
        Marker lidlMarker = markers.get("lidl");
        if (lidlMarker != null) {
            float zoomLevel = 16.0f; // Set your desired zoom level
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lidlMarker.getPosition(), zoomLevel));
            lidlMarker.showInfoWindow();
        } else {
            Toast.makeText(this, "Lidl market could not be found.", Toast.LENGTH_SHORT).show();
        }
    }
    private void getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if (location != null) {
                        double latitude = location.getLatitude();
                        double longitude = location.getLongitude();
                        userLocation = new com.google.android.gms.maps.model.LatLng(latitude, longitude);

                        // Add a marker for the user's location
                        MarkerOptions markerOptions = new MarkerOptions();
                        markerOptions.position(userLocation);
                        markerOptions.title("My Location");
                        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)); // Set the marker color to blue
                        mMap.addMarker(markerOptions);

                        // Move the camera to the user's location
                        mMap.moveCamera(CameraUpdateFactory.newLatLng(userLocation));
                        mMap.animateCamera(CameraUpdateFactory.zoomTo(10));



                        getNearbyMarkets(latitude, longitude, "AIzaSyBzp53E_d1EqzoAaR0StZR6m_uuQi07nU0");
                    } else {
                        // Handle the case where the location could not be obtained.
                    }
                }
            });
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        }
    }


}
