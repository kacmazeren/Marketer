package com.example.marketer10;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.database.Cursor;
import android.widget.Toast;

import java.util.ArrayList;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;




public class AdminPageActivity extends AppCompatActivity {
    private Button listMembersButton;
    private Button findMemberButton;
    private Button exitButton;
    private ListView lvMembers;
    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_page);

        listMembersButton = findViewById(R.id.btn_list_members);
        findMemberButton = findViewById(R.id.btn_find_member);
        exitButton = findViewById(R.id.btn_exit);
        lvMembers = findViewById(R.id.lv_members);
        databaseHelper = new DatabaseHelper(this);

        listMembersButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadMemberData();
            }
        });
        lvMembers.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = (String) parent.getItemAtPosition(position);
                Intent intent = new Intent(AdminPageActivity.this, MemberEdit.class);
                intent.putExtra("memberData", selectedItem);
                startActivity(intent);
            }
        });

        findMemberButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(AdminPageActivity.this);
                builder.setTitle("Enter Member Name");
                final EditText input = new EditText(AdminPageActivity.this);
                input.setInputType(InputType.TYPE_CLASS_TEXT);
                builder.setView(input);
                builder.setPositiveButton("Search", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String searchName = input.getText().toString();
                        Cursor cursor = databaseHelper.findMemberByName(searchName);
                        if (cursor != null && cursor.moveToFirst()) {
                            ArrayList<String> memberList = new ArrayList<>();
                            do {String memberData = cursor.getString(0) + " - " + cursor.getString(1) + " - " + cursor.getString(2) + " - " + cursor.getString(3) + " - " + cursor.getString(4) + " - " + cursor.getString(5) + " - " + cursor.getString(6) + " - " + cursor.getString(7);
                                memberList.add(memberData);
                            } while (cursor.moveToNext());
                            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(AdminPageActivity.this, android.R.layout.simple_list_item_1, memberList);
                            lvMembers.setAdapter(arrayAdapter);
                            cursor.close();
                        } else {Toast.makeText(AdminPageActivity.this, "No member found with this name", Toast.LENGTH_SHORT).show();
                        }                    }                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();                    }                });
                builder.show();            }        });


        exitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Close the AdminPage
                finish();
            }
        });
    }
    private void loadMemberData() {
        ArrayList<String> memberList = new ArrayList<>();
        Cursor cursor = databaseHelper.getAllMembers();

        if (cursor.moveToFirst()) {
            do {
                String memberData = cursor.getString(0) + " - " + cursor.getString(1) + " - " + cursor.getString(2) + " - "
                        + cursor.getString(3) + " - " + cursor.getString(4) + " - " + cursor.getString(5) + " - "
                        + cursor.getString(6) + " - " + cursor.getString(7);
                memberList.add(memberData);
            } while (cursor.moveToNext());
        }
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, memberList);
        lvMembers.setAdapter(arrayAdapter);
    }
}
