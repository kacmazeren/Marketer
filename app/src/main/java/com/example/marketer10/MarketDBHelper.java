package com.example.marketer10;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MarketDBHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "market.db";
    private static final int DATABASE_VERSION = 1;

    public static final String TABLE_MARKETS = "markets";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_ADDRESS = "address";
    public static final String COLUMN_LATITUDE = "latitude";
    public static final String COLUMN_LONGITUDE = "longitude";
    private static final String CREATE_TABLE_MARKETS = "CREATE TABLE " + TABLE_MARKETS + " (" +
            COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_NAME + " TEXT, " +
            COLUMN_ADDRESS + " TEXT," +
            COLUMN_LATITUDE + " REAL, " +
            COLUMN_LONGITUDE + " REAL" +
            ")";
    public MarketDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_MARKETS);
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MARKETS);
        onCreate(db);
    }


}
