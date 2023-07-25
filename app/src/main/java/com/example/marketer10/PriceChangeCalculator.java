package com.example.marketer10;

import android.content.Context;
import android.content.res.AssetManager;

import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

public class PriceChangeCalculator {

    public static List<InflationRate> inflationRateList = new ArrayList<>();

    private static class Item {
        String id;
        double price;

        Item(String id, double price) {
            this.id = id;
            this.price = price;
        }
    }

    public static class InflationRate {
        String id;
        String kind;
        String goods;
        double inflationRate;

        InflationRate(String id, String kind, String goods, double inflationRate) {
            this.id = id;
            this.kind = kind;
            this.goods = goods;
            this.inflationRate = inflationRate;
        }

        @Override
        public String toString() {
            return "ID: " + id + ", Kind: " + kind + ", Goods: " + goods + ", Inflation Rate: " + inflationRate;
        }
    }

    public static void calculateInflationRates(Context context) throws Exception {
        AssetManager assetManager = context.getAssets();
        InputStream oldPricesStream = assetManager.open("oldItemPrice.csv");
        InputStream newPricesStream = assetManager.open("itemPrice.csv");

        BufferedReader oldPricesReader = new BufferedReader(new InputStreamReader(oldPricesStream));
        BufferedReader newPricesReader = new BufferedReader(new InputStreamReader(newPricesStream));

        CSVReader oldPriceCsvReader = new CSVReaderBuilder(oldPricesReader).withSkipLines(1).build();
        CSVReader newPriceCsvReader = new CSVReaderBuilder(newPricesReader).withSkipLines(1).build();

        Map<String, Item> oldPriceMap = new HashMap<>();
        String[] record;
        while ((record = oldPriceCsvReader.readNext()) != null) {
            oldPriceMap.put(record[0], new Item(record[0], Double.parseDouble(record[8])));
        }

        while ((record = newPriceCsvReader.readNext()) != null) {
            Item oldItem = oldPriceMap.get(record[0]);
            if (oldItem != null) {
                double newPrice = Double.parseDouble(record[8]);
                double inflationRate = (newPrice - oldItem.price) / oldItem.price;
                inflationRateList.add(new InflationRate(record[0], record[3], record[4], inflationRate));
            }
        }

        // Print out the inflation rate list
        for (InflationRate rate : inflationRateList) {

        }
    }

    public static double computeAverageInflationRate(String kind) {
        double sum = 0;
        int count = 0;

        for (InflationRate rate : inflationRateList) {
            if (rate.kind.equals(kind)) {
                sum += rate.inflationRate;
                count++;
            }
        }

        return count > 0 ? sum / count : 0;
    }

    public static double calculateAndReturnAvgInflationRate(String kind, Context context) throws Exception {
        calculateInflationRates(context);
        return computeAverageInflationRate(kind);
    }

    public static Map<String, List<InflationRate>> calculatePersonalInflationRates(Context context, List<String> matchedProductIDs) throws Exception {
        if (inflationRateList.isEmpty()) {
            calculateInflationRates(context);
        }

        // Initialize a map to hold the list of InflationRate objects for each kind of goods
        Map<String, List<InflationRate>> personalInflationRates = new HashMap<>();

        for (String productID : matchedProductIDs) {
            for (InflationRate rate : inflationRateList) {
                if (rate.id.equals(productID)) {
                    // Add the InflationRate object to the list associated with the kind of goods
                    personalInflationRates.computeIfAbsent(rate.kind, k -> new ArrayList<>()).add(rate);
                    break;
                }
            }
        }

        return personalInflationRates;
    }

    public static Map<String, Double> calculateAveragePersonalInflationRates(Context context, List<String> matchedProductIDs) throws Exception {
        Map<String, List<InflationRate>> personalInflationRates = calculatePersonalInflationRates(context, matchedProductIDs);

        // Initialize a map to hold the average inflation rate for each kind of goods
        Map<String, Double> averageInflationRates = new HashMap<>();

        for (Map.Entry<String, List<InflationRate>> entry : personalInflationRates.entrySet()) {
            String kind = entry.getKey();
            List<InflationRate> rates = entry.getValue();

            double sum = 0;
            for (InflationRate rate : rates) {
                sum += rate.inflationRate;
            }

            averageInflationRates.put(kind, sum / rates.size());
        }

        return averageInflationRates;
    }



}
