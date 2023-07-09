package com.example.marketer10;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import androidx.appcompat.app.AppCompatActivity;
import java.text.SimpleDateFormat;
import java.util.Date;
public class EmailActivity extends AppCompatActivity {

    private EditText emailEditText, passwordEditText, nameEditText, surnameEditText, phoneEditText, addressEditText, newPasswordEditText;
    private RadioGroup membershipTypeRadioGroup;
    private Button signInButton, registerButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email);

        // Initialize views
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        nameEditText = findViewById(R.id.nameEditText);
        surnameEditText = findViewById(R.id.surnameEditText);
        phoneEditText = findViewById(R.id.phoneEditText);
        addressEditText = findViewById(R.id.addressEditText);
        newPasswordEditText = findViewById(R.id.newPasswordEditText);
        membershipTypeRadioGroup = findViewById(R.id.membershipTypeRadioGroup);
        signInButton = findViewById(R.id.signInButton);
        registerButton = findViewById(R.id.registerButton);

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailEditText.getText().toString().trim();
                String password = passwordEditText.getText().toString().trim();

                if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
                    Toast.makeText(EmailActivity.this, "Please enter email and password", Toast.LENGTH_SHORT).show();
                } else {
                    // Check if email exists and password is valid
                    DatabaseHelper db = new DatabaseHelper(EmailActivity.this);
                    boolean isValidCredentials = db.checkCredentials(email, password);

                    if (isValidCredentials) {
                        openAccountPage(email);
                    } else {
                        Toast.makeText(EmailActivity.this, "Invalid email or password", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailEditText.getText().toString().trim();
                String name = nameEditText.getText().toString().trim();
                String surname = surnameEditText.getText().toString().trim();
                String phone = phoneEditText.getText().toString().trim();
                String address = addressEditText.getText().toString().trim();
                String newPassword = newPasswordEditText.getText().toString().trim();

                int selectedId = membershipTypeRadioGroup.getCheckedRadioButtonId();

                if (selectedId != -1) {
                    RadioButton radioButton = findViewById(selectedId);
                    String membershipType = radioButton.getText().toString().trim();

                    if (TextUtils.isEmpty(email) || TextUtils.isEmpty(name) || TextUtils.isEmpty(surname) ||
                            TextUtils.isEmpty(phone) || TextUtils.isEmpty(address) || TextUtils.isEmpty(newPassword)) {
                        Toast.makeText(EmailActivity.this, "Please enter all the information", Toast.LENGTH_SHORT).show();
                    }else {
                        // Add password strength check
                        if (!PasswordHelper.isPasswordStrong(newPassword)) {
                            Toast.makeText(EmailActivity.this, "Password is not strong enough, please make sure it contains at least 4 characters, a digit, a lower case letter, an upper case letter and a special character.", Toast.LENGTH_LONG).show();
                        } else {
                            // Check if the email is already registered
                            DatabaseHelper db = new DatabaseHelper(EmailActivity.this);
                            boolean isMemberExists = db.isMemberExists(email);
                            if (isMemberExists) {
                                Toast.makeText(EmailActivity.this, "This email is already registered", Toast.LENGTH_SHORT).show();
                            } else {
                                // Generate member number based on selection
                                String memberNumber;
                                if (membershipType.equals("Silver Member")) {
                                    memberNumber = generateMemberNumber("S");
                                } else {
                                    memberNumber = generateMemberNumber("G");
                                }

                                boolean isSaved = db.saveMemberToDatabase(email, name, surname, phone, address, newPassword, memberNumber);
                                if (isSaved) {
                                    sendRegistrationEmail(email);
                                    openAccountPage(email);
                                } else {
                                    Toast.makeText(EmailActivity.this, "Failed to save member data", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    }
                } else {
                    Toast.makeText(EmailActivity.this, "Please select a membership type", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }


        private void openAccountPage(String email) {
        // Perform the necessary action after successful sign-in or registration
        Intent intent = new Intent(EmailActivity.this, AccountActivity.class);
        intent.putExtra("email", email);
        startActivity(intent);
    }

    private void sendRegistrationEmail(String recipientEmail) {
        // Create new SendMail object and execute it on its own thread
        String emailBody = "You have successfully registered. Thank you for using the marketer.";
        SendMail sm = new SendMail(this, recipientEmail, "Successful Registration", emailBody);
        sm.execute();
    }

    private String generateMemberNumber(String prefix) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        String dateTimeString = dateFormat.format(new Date());
        return prefix + dateTimeString;
    }





}