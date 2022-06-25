package com.example.ds_project;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.graphics.Color;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.EditText;

import android.widget.Toast;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    Button loginBtn, cancelBtn;
    EditText passEnter, nameEnter;
    TextView usernameText, passText, errTxt;
    int counter = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loginBtn = (Button)findViewById(R.id.loginBtn);
        cancelBtn = (Button)findViewById(R.id.cancelBtn);
        nameEnter = (EditText)findViewById(R.id.nameEnter);
        passEnter = (EditText)findViewById(R.id.passEnter);
        usernameText = (TextView)findViewById(R.id.usernameText);
        passText = (TextView)findViewById(R.id.passText);


        errTxt = (TextView)findViewById(R.id.errTxt);
        errTxt.setVisibility(View.GONE);

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(nameEnter.getText().toString().equals("admin") &&
                        passEnter.getText().toString().equals("admin")) {
                    Toast.makeText(getApplicationContext(), "Wrong Credentials",Toast.LENGTH_SHORT).show();

                    errTxt.setVisibility(View.VISIBLE);
                    errTxt.setBackgroundColor(Color.RED);
                    counter--;
                    errTxt.setText(Integer.toString(counter));

                    if (counter == 0) {
                        loginBtn.setEnabled(false);
                    }
                }

                else {
                    Toast.makeText(getApplicationContext(),
                            "Redirecting...",Toast.LENGTH_SHORT).show();
                    String theuser = nameEnter.getText().toString();
                    Intent myIntent = new Intent(v.getContext(), First_Activity.class);

                    myIntent.putExtra("NEEDED_USERNAME", theuser);
                    startActivity(myIntent);

                }
            }
        });

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }
}



