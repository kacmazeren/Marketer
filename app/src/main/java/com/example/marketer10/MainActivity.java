package com.example.marketer10;



import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;


import android.Manifest;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;

import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;

import android.widget.Toast;


import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private Button loginRegisterButton, vegetablesButton, dairyButton, petButton, alcoholButton,cleaningButton, setBasketButton, button_login, button_cancel, button_register  ;
    private EditText edit_email,edit_password;
    private ImageButton tomatoesButton, cheeseButton;
    private MarketDBHelper dbHelper;
    private SQLiteDatabase db;
    private List<Product> productList;
    private List<String> groceryList = new ArrayList<>();

    private ArrayAdapter<String> groceryListAdapter;
    private ProductAdapter productAdapter;
    private ProductDBHelper productDBHelper;
    private ListView lvProducts, grocery;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        lvProducts = findViewById(R.id.lv_products);
        grocery = findViewById(R.id.grocery);
        groceryListAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, groceryList);
        grocery.setAdapter(groceryListAdapter);

        boolean fromAccountActivity = getIntent().getBooleanExtra("fromAccountActivity", false);

        Button loginRegisterButton = findViewById(R.id.loginRegisterButton);
        if (fromAccountActivity) {
            loginRegisterButton.setVisibility(View.GONE);
        }

        setBasketButton = findViewById(R.id.setBasketButton);
        setBasketButton.findViewById(R.id.setBasketButton);
        setBasketButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences prefs = getSharedPreferences("MyApp", MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                Gson gson = new Gson();
                String json = gson.toJson(groceryList);
                editor.putString("groceryList", json);
                editor.apply();

                Button loginRegisterButton = findViewById(R.id.loginRegisterButton);


                if (fromAccountActivity ) {
                    String email = prefs.getString("loggedInEmail", "");
                    Intent intent = new Intent(MainActivity.this, AccountActivity.class);
                    intent.putExtra("email", email);
                    intent.putStringArrayListExtra("groceryList", new ArrayList<>(groceryList));
                    startActivity(intent);
                } else {
                // Now show the login dialog
                final Dialog dialog = new Dialog(MainActivity.this);
                dialog.setContentView(R.layout.basket_login);

                // Get the dialog buttons and fields
                Button buttonLogin = dialog.findViewById(R.id.button_login);
                Button buttonCancel = dialog.findViewById(R.id.button_cancel);
                Button buttonRegister = dialog.findViewById(R.id.button_register);
                final EditText editEmail = dialog.findViewById(R.id.edit_email);
                final EditText editPassword = dialog.findViewById(R.id.edit_password);

                edit_email = dialog.findViewById(R.id.edit_email);
                edit_password = dialog.findViewById(R.id.edit_password);

                // Set their click listeners
                buttonLogin.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        String email = edit_email.getText().toString().trim();
                        String password = edit_password.getText().toString().trim();

                        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
                            Toast.makeText(MainActivity.this, "Please enter email and password", Toast.LENGTH_SHORT).show();
                        } else {
                            // Check if email exists and password is valid
                            DatabaseHelper db = new DatabaseHelper(MainActivity.this);
                            boolean isValidCredentials = db.checkCredentials(email, password);

                            if (isValidCredentials) {
                                // Save the login state and email to SharedPreferences
                                SharedPreferences prefs = getSharedPreferences("MyApp", MODE_PRIVATE);
                                SharedPreferences.Editor editor = prefs.edit();
                                editor.putBoolean("isUserLoggedIn", true);
                                editor.putString("loggedInEmail", email);
                                editor.apply();

                                Intent intent = new Intent(MainActivity.this, AccountActivity.class);
                                intent.putExtra("email", email);
                                intent.putStringArrayListExtra("groceryList", new ArrayList<>(groceryList));
                                startActivity(intent);

                                // Dismiss the dialog
                                dialog.dismiss();
                            } else {
                                Toast.makeText(MainActivity.this, "Invalid email or password", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });

                buttonCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Dismiss the dialog
                        dialog.dismiss();
                    }
                });

                buttonRegister.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Redirect to the registration activity
                        Intent intent = new Intent(MainActivity.this, EmailActivity.class);
                        startActivity(intent);
                        // Dismiss the dialog
                        dialog.dismiss();
                    }
                });

                // Show the dialog
                dialog.show();
                }
            }
        });
        lvProducts.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Get the selected item text from ListView
                String selectedItem = (String) parent.getItemAtPosition(position);

                // Add this item to groceryList
                groceryList.add(selectedItem);
                groceryListAdapter.notifyDataSetChanged();
                // Log for debug
                Log.d("MainActivity", "Item added to grocery list: " + selectedItem);
            }
        });


        lvProducts = findViewById(R.id.lv_products);
         // Initialize the accountButton
        loginRegisterButton = findViewById(R.id.loginRegisterButton);

        // Set a click listener for the accountButton
        loginRegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Open a new page/activity to get the user's email
                Intent intent = new Intent(MainActivity.this, EmailActivity.class);
                startActivityForResult(intent, 1);
            }
        });




        Button vegetablesButton = findViewById(R.id.vegetablesButton);
        Button dairyButton = findViewById(R.id.dairyButton);
        Button petButton = findViewById(R.id.petButton);
        Button alcoholButton = findViewById(R.id.alcoholButton);
        Button cleaningButton = findViewById(R.id.cleaningButton);
        ImageButton paperButton = findViewById(R.id.paperButton);
        ImageButton tomatoesButton = findViewById(R.id.tomatoesButton);
        ImageButton cheeseButton = findViewById(R.id.cheeseButton);
        ImageButton catButton = findViewById(R.id.catButton);
        ImageButton irelandButton = findViewById(R.id.irelandButton);
        productDBHelper = new ProductDBHelper(this);
        productList = productDBHelper.getTomatoes();
        productAdapter = new ProductAdapter(productList);

        cleaningButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                paperButton.setVisibility(View.VISIBLE);
                irelandButton.setVisibility(View.GONE);
                catButton.setVisibility(View.GONE);
                tomatoesButton.setVisibility(View.GONE);
                cheeseButton.setVisibility(View.GONE);
            }
        });
        alcoholButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                paperButton.setVisibility(View.GONE);
                irelandButton.setVisibility(View.VISIBLE);
                catButton.setVisibility(View.GONE);
                tomatoesButton.setVisibility(View.GONE);
                cheeseButton.setVisibility(View.GONE);
            }
        });
        petButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                paperButton.setVisibility(View.GONE);
                irelandButton.setVisibility(View.GONE);
                catButton.setVisibility(View.VISIBLE);
                tomatoesButton.setVisibility(View.GONE);
                cheeseButton.setVisibility(View.GONE);
            }
        });
        vegetablesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                paperButton.setVisibility(View.GONE);
                irelandButton.setVisibility(View.GONE);
                catButton.setVisibility(View.GONE);
                tomatoesButton.setVisibility(View.VISIBLE);
                cheeseButton.setVisibility(View.GONE);
            }
        });
        dairyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                paperButton.setVisibility(View.GONE);
                irelandButton.setVisibility(View.GONE);
                catButton.setVisibility(View.GONE);
                tomatoesButton.setVisibility(View.GONE);
                cheeseButton.setVisibility(View.VISIBLE);

            }
        });
        catButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadData("Litter");
            }
        });
        irelandButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadData("Alcohol");
            }
        });
        paperButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadData("Toilet Paper");
            }
        });
        cheeseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadData("Cheese");
            }
        });
        tomatoesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               loadData("Tomatoes");
            }
        });

        requestLocationPermissions();
    }
    private void openAccountPage(String email) {
        // Perform the necessary action after successful sign-in or registration
        Intent intent = new Intent(MainActivity.this, AccountActivity.class);
        intent.putExtra("email", email);
        startActivity(intent);
    }

    private void loadData(String productNameToSearch) {
        List<Product> products = new ArrayList<>();

        try {
            InputStream inputStream = getAssets().open("itemPrice.csv");
            PriceCSVReader priceCSVReader = new PriceCSVReader(inputStream);
            List<Product> productList = priceCSVReader.parse();

            for (Product product : productList) {
                String productName = product.getGoods().replace("\"", "").trim();
                if (productName.equalsIgnoreCase(productNameToSearch)) {
                    products.add(product);
                }
            }

            // Sort by price, if equal, sort by unit price
            Collections.sort(products, new Comparator<Product>() {
                @Override
                public int compare(Product p1, Product p2) {
                    int priceCompare = Double.compare(p1.getPrice(), p2.getPrice());
                    if (priceCompare == 0) { // if prices are equal, compare unit prices
                        return Double.compare(p1.getUnitPrice(), p2.getUnitPrice());
                    } else {
                        return priceCompare;
                    }
                }
            });

        } catch (IOException e) {
            e.printStackTrace();
        }

        List<String> displayData = new ArrayList<>(); // This list will be used for ArrayAdapter
        for (Product product : products) {
            String dataLine = product.getSupermarket() + ",    " + product.getProductType() + " , " + "€" + product.getPrice() + ",    " + "€" + product.getUnitPrice();
            displayData.add(dataLine);
        }



// Setup the ListView with ArrayAdapter
        ListView listView = (ListView) findViewById(R.id.lv_products);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, displayData);
        listView.setAdapter(adapter);
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
