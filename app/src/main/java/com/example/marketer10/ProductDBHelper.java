package com.example.marketer10;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ProductDBHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "product.db";
    public static final int DATABASE_VERSION = 1;

    public ProductDBHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase productDBHelper) {
        String CREATE_TABLE = "CREATE TABLE " + ProductEntry.TABLE_NAME + "("
                + ProductEntry.COLUMN_ID + " INTEGER PRIMARY KEY ,"
                + ProductEntry.COLUMN_DATE + " TEXT,"
                + ProductEntry.COLUMN_SUPERMARKET + " TEXT,"
                + ProductEntry.COLUMN_KIND + " TEXT,"
                + ProductEntry.COLUMN_GOODS + " TEXT,"
                + ProductEntry.COLUMN_PRODUCT_TYPE + " TEXT,"
                + ProductEntry.COLUMN_BRAND + " TEXT,"
                + ProductEntry.COLUMN_WEIGHT + " TEXT,"
                + ProductEntry.COLUMN_PRICE + " REAL,"
                + ProductEntry.COLUMN_UNIT_PRICE + " REAL"
                + ")";
        productDBHelper.execSQL(CREATE_TABLE);
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + ProductEntry.TABLE_NAME);
        onCreate(db);
    }

    public void addProduct(Product product) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(ProductEntry.COLUMN_DATE, product.getDate().toString());
        values.put(ProductEntry.COLUMN_SUPERMARKET, product.getSupermarket());
        values.put(ProductEntry.COLUMN_KIND, product.getKind());
        values.put(ProductEntry.COLUMN_GOODS, product.getGoods());
        values.put(ProductEntry.COLUMN_PRODUCT_TYPE, product.getProductType());
        values.put(ProductEntry.COLUMN_BRAND, product.getBrand());
        values.put(ProductEntry.COLUMN_WEIGHT, product.getWeight());
        values.put(ProductEntry.COLUMN_PRICE, product.getPrice());
        values.put(ProductEntry.COLUMN_UNIT_PRICE, product.getUnitPrice());

        db.insert(ProductEntry.TABLE_NAME, null, values);
        db.close();
    }


    public List<Product> getTomatoes() {
        List<Product> productList = new ArrayList<>();

        SQLiteDatabase productDBHelper = this.getReadableDatabase();

        String selectQuery = "SELECT * FROM " + ProductEntry.TABLE_NAME +
                " WHERE " + ProductEntry.COLUMN_GOODS + " = 'Tomatoes' ORDER BY " + ProductEntry.COLUMN_PRICE + " ASC";
        Cursor cursor = productDBHelper.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Product product = new Product();
                int columnIndex;

                columnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_ID);
                if(columnIndex != -1 && !cursor.isNull(columnIndex)) {
                    product.setID(cursor.getInt(columnIndex));
                } else {
                    // handle the error or just ignore it
                }

                columnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_DATE);
                if(columnIndex != -1 && !cursor.isNull(columnIndex)) {
                    product.setDate(new Date(cursor.getLong(columnIndex)));
                } else {
                    // handle the error or just ignore it
                }

                columnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_SUPERMARKET);
                if(columnIndex != -1 && !cursor.isNull(columnIndex)) {
                    product.setSupermarket(cursor.getString(columnIndex));
                }

                columnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_KIND);
                if(columnIndex != -1 && !cursor.isNull(columnIndex)) {
                    product.setKind(cursor.getString(columnIndex));
                }

                columnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_GOODS);
                if(columnIndex != -1 && !cursor.isNull(columnIndex)) {
                    product.setGoods(cursor.getString(columnIndex));
                }

                columnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_TYPE);
                if(columnIndex != -1 && !cursor.isNull(columnIndex)) {
                    product.setProductType(cursor.getString(columnIndex));
                }

                columnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_BRAND);
                if(columnIndex != -1 && !cursor.isNull(columnIndex)) {
                    product.setBrand(cursor.getString(columnIndex));
                }

                columnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_WEIGHT);
                if(columnIndex != -1 && !cursor.isNull(columnIndex)) {
                    product.setWeight(cursor.getString(columnIndex));
                }

                columnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRICE);
                if(columnIndex != -1 && !cursor.isNull(columnIndex)) {
                    product.setPrice(cursor.getDouble(columnIndex));
                }

                columnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_UNIT_PRICE);
                if(columnIndex != -1 && !cursor.isNull(columnIndex)) {
                    product.setUnitPrice(cursor.getDouble(columnIndex));
                }

                productList.add(product);
            } while (cursor.moveToNext());

        }
        cursor.close();
        productDBHelper.close();
        return productList;
    }

    public Cursor getProductDetails(String productId) {
        SQLiteDatabase db = this.getReadableDatabase();

        String[] projection = {
                ProductEntry.COLUMN_ID,
                ProductEntry.COLUMN_DATE,
                ProductEntry.COLUMN_SUPERMARKET,
                ProductEntry.COLUMN_KIND,
                ProductEntry.COLUMN_GOODS,
                ProductEntry.COLUMN_PRODUCT_TYPE,
                ProductEntry.COLUMN_BRAND,
                ProductEntry.COLUMN_WEIGHT,
                ProductEntry.COLUMN_PRICE,
                ProductEntry.COLUMN_UNIT_PRICE
        };

        String selection = ProductEntry.COLUMN_ID + " = ?";
        String[] selectionArgs = { productId };

        Cursor cursor = db.query(
                ProductEntry.TABLE_NAME,  // The table to query
                projection,               // The columns to return
                selection,                // The columns for the WHERE clause
                selectionArgs,            // The values for the WHERE clause
                null,             // Don't group the rows
                null,              // Don't filter by row groups
                null               // The sort order
        );

        return cursor;
    }
    public List<Product> getProductDetailsAsList() {
        SQLiteDatabase db = this.getReadableDatabase();

        String[] projection = {
                ProductEntry.COLUMN_ID,
                ProductEntry.COLUMN_DATE,
                ProductEntry.COLUMN_SUPERMARKET,
                ProductEntry.COLUMN_KIND,
                ProductEntry.COLUMN_GOODS,
                ProductEntry.COLUMN_PRODUCT_TYPE,
                ProductEntry.COLUMN_BRAND,
                ProductEntry.COLUMN_WEIGHT,
                ProductEntry.COLUMN_PRICE,
                ProductEntry.COLUMN_UNIT_PRICE
        };

        Cursor cursor = db.query(
                ProductEntry.TABLE_NAME,
                projection,
                null,
                null,
                null,
                null,
                null
        );

        List<Product> productList = new ArrayList<>();

        try {
            int idColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_ID);
            int dateColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_DATE);
            int supermarketColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_SUPERMARKET);
            int kindColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_KIND);
            int goodsColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_GOODS);
            int productTypeColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_TYPE);
            int brandColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_BRAND);
            int weightColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_WEIGHT);
            int priceColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRICE);
            int unitPriceColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_UNIT_PRICE);

            while (cursor.moveToNext()) {
                Product product = new Product();
                product.setID(cursor.getInt(idColumnIndex));
                product.setDate(new Date(cursor.getLong(dateColumnIndex)));
                product.setSupermarket(cursor.getString(supermarketColumnIndex));
                product.setKind(cursor.getString(kindColumnIndex));
                product.setGoods(cursor.getString(goodsColumnIndex));
                product.setProductType(cursor.getString(productTypeColumnIndex));
                product.setBrand(cursor.getString(brandColumnIndex));

                // Convert the double weight to a String and set it
                double weightColumnValue = cursor.getDouble(weightColumnIndex);
                product.setWeight(Double.toString(weightColumnValue));

                product.setPrice(cursor.getDouble(priceColumnIndex));
                product.setUnitPrice(cursor.getDouble(unitPriceColumnIndex));
                productList.add(product);
            }
        } finally {
            cursor.close();
        }

        return productList;
    }


}
