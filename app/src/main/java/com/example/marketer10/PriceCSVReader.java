package com.example.marketer10;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;


public class PriceCSVReader {

        private InputStream inputStream;
        public PriceCSVReader(InputStream inputStream) {
            this.inputStream = inputStream;
        }
        public List<Product> parse() {
            List<Product> productList = new ArrayList<>();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            try {
                String csvLine;
                boolean firstLine = true;
                while ((csvLine = reader.readLine()) != null) {
                    if (firstLine) {
                        firstLine = false;
                        continue;
                    }
                    String[] row = csvLine.split(",");
                    String rawDate = row[1].replace("\"", "").trim();
                    String rawSupermarket = row[2].replace("\"", "").trim();
                    String rawPrice = row[8].replace("\"", "").trim();
                    String rawUnitPrice = row[9].replace("\"", "").trim();
                    Product product = new Product();
                    product.setID(0);
                    product.setDate(new SimpleDateFormat("yyyy-MM-dd").parse(rawDate));
                    product.setSupermarket(rawSupermarket);
                    product.setKind(row[3]);
                    product.setGoods(row[4]);
                    product.setProductType(row[5]);
                    product.setBrand(row[6]);
                    product.setWeight(row[7]);
                    product.setPrice(Double.parseDouble(rawPrice));
                    product.setUnitPrice(Double.parseDouble(rawUnitPrice));
                    productList.add(product);
                }
            } catch (Exception e) {
                throw new RuntimeException("Error while reading CSV file: " + e);
            }
            return productList;
        }
    }


