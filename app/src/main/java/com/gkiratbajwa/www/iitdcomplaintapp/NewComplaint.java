package com.gkiratbajwa.www.iitdcomplaintapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

public class NewComplaint extends AppCompatActivity {
    int type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_complaint);
        Toast.makeText(getApplicationContext(), "New Complaint!", Toast.LENGTH_SHORT).show();
        Intent intent=getIntent();
        type=intent.getIntExtra("type",0);
        Spinner to = (Spinner)findViewById(R.id.spinner_to);
        String[] types = new String[]{"Director", "Dean of Student Affairs","Dean of Academics","Professor","Warden","Caretaker",
                "House Secretary","Maintenance Secretary","Mess Secretary","Sports Secretary","Cultural Secretary","Student"};
        ArrayAdapter<String> toAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, types);
        to.setAdapter(toAdapter);
    }

    public void submitComplaint(View view)
    {

    }
}
