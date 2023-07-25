package com.example.marketer10;

import android.content.Intent;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class Statistic extends AppCompatActivity {

    private ListView orderHistoryListView;
    Button deleteButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.statistic);




        ShoppingListDbHelper dbHelper = new ShoppingListDbHelper(this);

        // Get all shopping items
        List<ShoppingItem> shoppingItems = dbHelper.getAllShoppingItems();

        // Read itemPrice.csv and store the items in a HashMap
        HashMap<String, Product> productMap = new HashMap<>();
        ArrayList<Product> productList = new ArrayList<>();
        try {
            AssetManager assetManager = getAssets();
            InputStream inputStream = assetManager.open("itemPrice.csv");
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            // Skip the first line (header)
            reader.readLine();
            String line;
            while ((line = reader.readLine()) != null) {
                String[] nextLine = line.split(",");
                String idString =nextLine[0];
                int id = Integer.parseInt(idString);
                Date date = new SimpleDateFormat("yyyy-MM-dd").parse(nextLine[1]);
                String supermarket = nextLine[2];
                String kind = nextLine[3];
                String goods = nextLine[4];
                String productType = nextLine[5];
                String brand = nextLine[6];
                String weight = nextLine[7];
                String priceString = nextLine[8];
                String unitPriceString = nextLine[9];
                double price = Double.parseDouble(priceString);
                double unitPrice = Double.parseDouble(unitPriceString);

                // Create a new Product object and add it to the productList
                Product product = new Product(id, date, supermarket, kind, goods, productType, brand, weight, price, unitPrice);
                productList.add(product);
                productMap.put(supermarket + productType, product);
            }
            reader.close();
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }

        // For each item in the shopping list, find the product in the HashMap and get all its information
        ArrayList<String> matchedProducts = new ArrayList<>();
        SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd");

        for (ShoppingItem item : shoppingItems) {
            String supermarket = item.getSupermarket().trim();
            String productType = item.getProductType().trim();
            String productMemberNumber = item.getMemberNumber();
            Intent intent = getIntent();
            String memberNumber = intent.getStringExtra("memberNumber");


            Product product = productMap.get(supermarket + productType);
            if(  productMemberNumber.equals(memberNumber)) {
                String formattedDate = outputFormat.format(product.getDate());
                String productInfo = product.getID() + " - " + formattedDate + " - " + product.getSupermarket() + " - " + product.getProductType() + " - " + product.getWeight() + " - " + product.getPrice() + " - " + item.getMemberNumber();
                matchedProducts.add(productInfo);
            }
        }

        // Display the matched products in a ListView
        TableLayout historyTable = findViewById(R.id.historyTable);

        for (String product : matchedProducts) {
            // Split product info into an array
            String[] productInfo = product.split(" - ");

            // Create a new TableRow
            TableRow row = new TableRow(this);

            // Add a TextView for each piece of product info
            for (String info : productInfo) {
                TextView text = new TextView(this);
                text.setText(info);
                row.addView(text);
            }

            // Add the row to the table
            historyTable.addView(row);
        }

        TextView goodsVegetablesInflation = findViewById(R.id.goodsVegetablesInflation);
        try {
            double avgVegetablesInflation = PriceChangeCalculator.calculateAndReturnAvgInflationRate("Vegetables", this);
            goodsVegetablesInflation.setText(String.format("%.2f%%", avgVegetablesInflation * 100));
        } catch (Exception e) {
            // Handle exception
            e.printStackTrace();
        }

        TextView goodslDairyInflation = findViewById(R.id.goodslDairyInflation);
        try {
            double avgDairyInflation = PriceChangeCalculator.calculateAndReturnAvgInflationRate("Dairy", this);
            goodslDairyInflation.setText(String.format("%.2f%%", avgDairyInflation * 100));
        } catch (Exception e) {
            // Handle exception
            e.printStackTrace();
        }
        TextView goodsPetInflation = findViewById(R.id.goodsPetInflation);
        try {
            double avgPetInflation = PriceChangeCalculator.calculateAndReturnAvgInflationRate("Pet", this);
            goodsPetInflation.setText(String.format("%.2f%%", avgPetInflation * 100));
        } catch (Exception e) {
            // Handle exception
            e.printStackTrace();
        }
        TextView goodsAlcoholInflation = findViewById(R.id.goodsAlcoholInflation);
        try {
            double avgAlcoholInflation = PriceChangeCalculator.calculateAndReturnAvgInflationRate("Drinks", this);
            goodsAlcoholInflation.setText(String.format("%.2f%%", avgAlcoholInflation * 100));
        } catch (Exception e) {
            // Handle exception
            e.printStackTrace();
        }
        TextView goodsCleaningInflation = findViewById(R.id.goodsCleaningInflation);
        try {
            double avgCleainingInflation = PriceChangeCalculator.calculateAndReturnAvgInflationRate("Cleaning", this);
            goodsCleaningInflation.setText(String.format("%.2f%%", avgCleainingInflation * 100));
        } catch (Exception e) {
            // Handle exception
            e.printStackTrace();
        }
        TextView gnrlInflationRate = findViewById(R.id.gnrlInflationRate);
        try {
            double avgCleaningInflation = PriceChangeCalculator.calculateAndReturnAvgInflationRate("Cleaning", this);
            double avgAlcoholInflation = PriceChangeCalculator.calculateAndReturnAvgInflationRate("Alcohol", this);
            double avgPetInflation = PriceChangeCalculator.calculateAndReturnAvgInflationRate("Pet", this);
            double avgDairyInflation = PriceChangeCalculator.calculateAndReturnAvgInflationRate("Dairy", this);
            double avgVegetablesInflation = PriceChangeCalculator.calculateAndReturnAvgInflationRate("Vegetables", this);

            double generalInflationRate = (avgCleaningInflation + avgAlcoholInflation + avgPetInflation + avgDairyInflation + avgVegetablesInflation) / 5;


            gnrlInflationRate.setText(String.format("%.2f%%", generalInflationRate * 100));
        } catch (Exception e) {
            // Handle exception
            e.printStackTrace();
        }

        Button deleteButton = findViewById(R.id.deleteButton);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShoppingListDbHelper dbHelper = new ShoppingListDbHelper(Statistic.this);
                dbHelper.deleteAllRecords();


            }
        });

        List<String> matchedProductIDs = new ArrayList<>();
        for (String product : matchedProducts) {
            // Split product info into an array
            String[] productInfo = product.split(" - ");
            // The first element is the product ID
            matchedProductIDs.add(productInfo[0]);
        }

        try {
            Map<String, Double> personalInflationRates = PriceChangeCalculator.calculateAveragePersonalInflationRates(this, matchedProductIDs);
            ((TextView)findViewById(R.id.personalVegetablesInflation)).setText(String.format(Locale.US, "%.2f%%", personalInflationRates.get("Vegetables") * 100));
            ((TextView)findViewById(R.id.personalDairyInflation)).setText(String.format(Locale.US, "%.2f%%", personalInflationRates.get("Dairy") * 100));
            ((TextView)findViewById(R.id.personalPetInflation)).setText(String.format(Locale.US, "%.2f%%", personalInflationRates.get("Pet") * 100));
            ((TextView)findViewById(R.id.personalAlcoholInflation)).setText(String.format(Locale.US, "%.2f%%", personalInflationRates.get("Drinks") * 100));
            ((TextView)findViewById(R.id.personalCleaningInflation)).setText(String.format(Locale.US, "%.2f%%", personalInflationRates.get("Cleaning") * 100));
            double avgPersonalInflationRate = (personalInflationRates.get("Vegetables")
                    + personalInflationRates.get("Dairy")
                    + personalInflationRates.get("Pet")
                    + personalInflationRates.get("Drinks")
                    + personalInflationRates.get("Cleaning")) / 5;

            ((TextView)findViewById(R.id.averagePersonalInflationRate)).setText(String.format(Locale.US, "%.2f%%", avgPersonalInflationRate * 100));


        } catch (Exception e) {
            // Handle exceptions, for example:
            Toast.makeText(this, "Error calculating personal inflation rates: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }

    }

}
