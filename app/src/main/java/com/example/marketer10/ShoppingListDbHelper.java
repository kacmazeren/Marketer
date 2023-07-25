package com.example.marketer10;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import com.example.marketer10.ShoppingItem;

public class ShoppingListDbHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "ShoppingList.db";
    public static final int DATABASE_VERSION = 2;

    public ShoppingListDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String SQL_CREATE_ENTRIES =
                "CREATE TABLE " + ShoppingListContract.ShoppingListEntry.TABLE_NAME + " (" +
                        ShoppingListContract.ShoppingListEntry.COLUMN_NAME_DATE + " TEXT," +
                        ShoppingListContract.ShoppingListEntry.COLUMN_NAME_SUPERMARKET + " TEXT," +
                        ShoppingListContract.ShoppingListEntry.COLUMN_NAME_PRODUCTTYPE + " TEXT," +
                        ShoppingListContract.ShoppingListEntry.COLUMN_NAME_PRICE + " REAL," +
                        ShoppingListContract.ShoppingListEntry.COLUMN_NAME_MEMBER_NUMBER + " TEXT)";

        db.execSQL(SQL_CREATE_ENTRIES);
    }



    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + ShoppingListContract.ShoppingListEntry.TABLE_NAME);
        onCreate(db);
    }

    public void insertIntoSavedList(String date, String supermarket, String productType, String price, String memberNumber) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(ShoppingListContract.ShoppingListEntry.COLUMN_NAME_DATE, date);
        cv.put(ShoppingListContract.ShoppingListEntry.COLUMN_NAME_SUPERMARKET, supermarket);
        cv.put(ShoppingListContract.ShoppingListEntry.COLUMN_NAME_PRODUCTTYPE, productType);

        // Remove the dollar sign before parsing
        price = price.replace("€", "");
        cv.put(ShoppingListContract.ShoppingListEntry.COLUMN_NAME_PRICE, Double.parseDouble(price));

        cv.put(ShoppingListContract.ShoppingListEntry.COLUMN_NAME_MEMBER_NUMBER, memberNumber);

        db.insert(ShoppingListContract.ShoppingListEntry.TABLE_NAME, null, cv);
    }


    public List<ShoppingItem> getAllShoppingItems() {
        List<ShoppingItem> shoppingItems = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(
                ShoppingListContract.ShoppingListEntry.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                null
        );

        while (cursor.moveToNext()) {
            String date = cursor.getString(
                    cursor.getColumnIndexOrThrow(ShoppingListContract.ShoppingListEntry.COLUMN_NAME_DATE));
            String supermarket = cursor.getString(
                    cursor.getColumnIndexOrThrow(ShoppingListContract.ShoppingListEntry.COLUMN_NAME_SUPERMARKET));
            String productType = cursor.getString(
                    cursor.getColumnIndexOrThrow(ShoppingListContract.ShoppingListEntry.COLUMN_NAME_PRODUCTTYPE));
            double price = cursor.getDouble(
                    cursor.getColumnIndexOrThrow(ShoppingListContract.ShoppingListEntry.COLUMN_NAME_PRICE));
            String memberNumber = cursor.getString(
                    cursor.getColumnIndexOrThrow(ShoppingListContract.ShoppingListEntry.COLUMN_NAME_MEMBER_NUMBER));

            ShoppingItem item = new ShoppingItem(date, supermarket, productType, price, memberNumber);
            shoppingItems.add(item);
        }
        cursor.close();

        for (ShoppingItem item : shoppingItems) {
            Log.d("ShoppingListDbHelper", item.toString());
        }
        return shoppingItems;
    }

    public void deleteAllRecords() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(ShoppingListContract.ShoppingListEntry.TABLE_NAME, null, null);
    }

}
