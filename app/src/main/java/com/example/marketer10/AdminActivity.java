package com.example.marketer10;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class AdminActivity extends AppCompatActivity {
    private Button buttonEnter;
    private EditText editTextUsername, editTextPassword;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);
        editTextUsername = findViewById(R.id.editTextUsername);
        editTextPassword = findViewById(R.id.editTextPassword);
        buttonEnter = findViewById(R.id.buttonEnter);

        buttonEnter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = ((EditText)findViewById(R.id.editTextUsername)).getText().toString();
                String password = ((EditText)findViewById(R.id.editTextPassword)).getText().toString();
                if (username.equals("1111") && password.equals("1111")) {
                    // Open admin page
                    Intent intent = new Intent(AdminActivity.this, AdminPageActivity.class);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(AdminActivity.this, AdminActivity.class);
                    Toast.makeText(AdminActivity.this, "Invalid username or password", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }
}
