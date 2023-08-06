package com.example.marketer10;

import static android.content.Context.MODE_PRIVATE;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "MarketerDB";
    private static final int DATABASE_VERSION = 1;
    private Context context;
    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_MEMBERS_TABLE = "CREATE TABLE members (id INTEGER PRIMARY KEY, email TEXT, name TEXT, surname TEXT, phone TEXT, address TEXT, password TEXT, member_number TEXT)";
        db.execSQL(CREATE_MEMBERS_TABLE);
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS members");
        onCreate(db);
    }
    public boolean isMemberExists(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM members WHERE email = ?";
        Cursor cursor = db.rawQuery(query, new String[]{email});
        int count = cursor.getCount();
        cursor.close();
        return count > 0;
    }
    public boolean checkCredentials(String email, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM members WHERE email = ?";
        Cursor cursor = db.rawQuery(query, new String[]{email});
        if (cursor != null && cursor.moveToFirst()) {
            int passwordIndex = cursor.getColumnIndex("password");
            if (passwordIndex != -1) {
                String storedHashedPassword = cursor.getString(passwordIndex);
                boolean isMatch = PasswordHelper.checkPassword(password, storedHashedPassword);

                // If the password matches, save the logged in status and email
                if (isMatch) {
                    SharedPreferences prefs = context.getSharedPreferences("MyApp", MODE_PRIVATE);
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putBoolean("isUserLoggedIn", true);
                    editor.putString("loggedInEmail", email);
                    editor.apply();
                }
                cursor.close();
                return isMatch;
            }
            cursor.close();
        }
        return false;
    }



    public boolean saveMemberToDatabase(String email, String name, String surname, String phone, String address, String password, String memberNumber) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        String hashedPassword = PasswordHelper.hashPassword(password);

        contentValues.put("email", email);
        contentValues.put("name", name);
        contentValues.put("surname", surname);
        contentValues.put("phone", phone);
        contentValues.put("address", address);
        contentValues.put("password", hashedPassword);
        contentValues.put("member_number", memberNumber);

        long result = db.insert("members", null, contentValues);

        // returns true if insert operation was successful, and false if an error occurred
        return result != -1;
    }
    public Cursor getAllMembers() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM members", null);
    }

    public Cursor getMemberData(String email) {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM members WHERE email = ?";
        return db.rawQuery(query, new String[]{email});
    }
    public Cursor findMemberByName(String name) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("SELECT * FROM members WHERE name = ?", new String[]{name});
        return res;
    }

    public boolean updateMemberData(String memberNumber, String newEmail, String name, String surname, String phone, String address) {
        SQLiteDatabase db = this.getWritableDatabase();

        // Check if the new email already exists in the database
        boolean isNewEmailAvailable = !isMemberExists(newEmail) || newEmail.equals(getEmailByMemberNumber(memberNumber));
        if (!isNewEmailAvailable) {
            // New email address is already registered, so return false indicating failure
            db.close();
            return false;
        }

        ContentValues contentValues = new ContentValues();
        contentValues.put("email", newEmail);
        contentValues.put("name", name);
        contentValues.put("surname", surname);
        contentValues.put("phone", phone);
        contentValues.put("address", address);

        // Updating the rows for the specified member number
        int result = db.update("members", contentValues, "member_number = ?", new String[]{memberNumber});

        // Close the database connection
        db.close();

        // returns true if update operation was successful, and false if an error occurred
        return result > 0;
    }

    public String getEmailByMemberNumber(String memberNumber) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT email FROM members WHERE member_number = ?";
        Cursor cursor = db.rawQuery(query, new String[]{memberNumber});
        String email = "";

        if (cursor != null && cursor.moveToFirst()) {
            int emailIndex = cursor.getColumnIndex("email");
            if (emailIndex != -1) {
                email = cursor.getString(emailIndex);
            }
            cursor.close();
        }

        return email;
    }
    public boolean deleteMember(String memberNumber) {
        SQLiteDatabase db = this.getWritableDatabase();
        int result = db.delete("members", "member_number = ?", new String[]{memberNumber});

        if (result == -1) {

            return false;
        } else {

          return true;
        }
    }

    public String getPassword(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query("Members", new String[]{"password"}, "email = ?", new String[]{email}, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            int passwordIndex = cursor.getColumnIndex("password");

            if (passwordIndex != -1) {
                String passwordHash = cursor.getString(passwordIndex);
                cursor.close();
                return passwordHash;
            }
        }
        return null;  // or throw an exception
    }


    public boolean updatePassword(String email, String newPassword) {
        String hashedPassword = PasswordHelper.hashPassword(newPassword);
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("password", hashedPassword);
        return db.update("Members", contentValues, "email = ?", new String[]{email}) > 0;
    }

    // Add a new protected method for getting a readable database
    protected SQLiteDatabase getReadableDb() {
        return this.getReadableDatabase();
    }

    // And a similar one for getting a writable database
    protected SQLiteDatabase getWritableDb() {
        return this.getWritableDatabase();
    }
}
