package com.example.distressapplication;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.view.View;

public class RegisterActivity extends AppCompatActivity {

    EditText user_name, user_number,emergency_name,emergency_number;
    Button Save, Delete, View;
    DBHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        user_name = findViewById(R.id.user_name);
        user_number = findViewById(R.id.user_number);
        emergency_name = findViewById(R.id.emergency_name);
        emergency_number = findViewById(R.id.emergency_number);

        Save = findViewById(R.id.btnSave);
        Delete = findViewById(R.id.btnDelete);
        View = findViewById(R.id.btnView);

        db = new DBHelper(this);

        Save.setOnClickListener(new View.OnClickListener(){
           @Override
            public void onClick(android.view.View view){
                String unameTXT = user_name.getText().toString();
                String unumberTXT = user_number.getText().toString();
                String emnameTXT = emergency_name.getText().toString();
                String emnumberTXT = emergency_number.getText().toString();

                Boolean checkinsertdata = db.insertuserdata(unameTXT, unumberTXT, emnameTXT,emnumberTXT);
                if(checkinsertdata == true)
                    Toast.makeText(RegisterActivity.this , "New Entry Saved", Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(RegisterActivity.this, "New Entry Not Saved", Toast.LENGTH_SHORT).show();

            }
        });

        Delete.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(android.view.View view){
                String unameTXT = user_name.getText().toString();


                Boolean deletedata = db.deletedata(unameTXT);
                if(deletedata == true)
                    Toast.makeText(RegisterActivity.this , "Entry Deleted", Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(RegisterActivity.this, "Entry Not Deleted", Toast.LENGTH_SHORT).show();

            }
        });

        View.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(android.view.View view) {
                Cursor res = db.getdata();
                if (res.getCount() == 0) {
                    Toast.makeText(RegisterActivity.this, "No Entry Exists", Toast.LENGTH_SHORT).show();
                    return;
                }
                StringBuffer buffer = new StringBuffer();
                while(res.moveToNext()){
                    buffer.append("Name :"+res.getString(0)+"\n");
                    buffer.append("Your Number :"+res.getString(1)+"\n");
                    buffer.append("Emergency contact's name :"+res.getString(2)+"\n");
                    buffer.append("Emergency contact's number :"+res.getString(3)+"\n\n");
                }

                AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                builder.setCancelable(true);
                builder.setTitle(" User Entries");
                builder.setMessage(buffer.toString());
                builder.show();
            }
        });
    }



}
