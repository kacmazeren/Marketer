package com.example.marketer10;

import static androidx.core.location.LocationManagerCompat.getCurrentLocation;

import android.Manifest;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
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

public class AccountActivity extends AppCompatActivity {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    FusedLocationProviderClient fusedLocationProviderClient;
    EditText editTextName, editTextSurname, editTextPhone, editTextLocation, editTextEmail, editTextAddress,editTextOldPassword, editTextNewPassword, editTextConfirmPassword;
    TextView memberNumberTextView;
    Button editButton, saveButton, buttonSavePassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_acoount);

        // Initialize EditTexts
        editTextName = findViewById(R.id.editTextName);
        editTextSurname = findViewById(R.id.editTextSurname);
        memberNumberTextView = findViewById(R.id.editTextMemberNumber);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPhone = findViewById(R.id.editTextPhone);
        editTextAddress = findViewById(R.id.editTextAddress);
        editTextLocation = findViewById(R.id.editTextLocation);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            // If permission is already granted, get current location
            getCurrentLocation();
        }

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
                    // Send an email notification
                    String subject = "Password Change Notification";
                    String message = "Your password has been successfully changed.";
                    SendMail sendMail = new SendMail(AccountActivity.this, email, subject, message);
                    sendMail.execute();
                } else {
                    Toast.makeText(AccountActivity.this, "Password update failed", Toast.LENGTH_SHORT).show();
                }
                // Hide the password layout
                passwordLayout.setVisibility(View.GONE);
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
                        Geocoder geocoder = new Geocoder(AccountActivity.this, Locale.getDefault());
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

}
