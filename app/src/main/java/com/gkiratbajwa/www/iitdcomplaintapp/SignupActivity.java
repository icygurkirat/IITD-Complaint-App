package com.gkiratbajwa.www.iitdcomplaintapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

public class SignupActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        Spinner typeSpinner = (Spinner)findViewById(R.id.spinner_type);
        String[] types = new String[]{"Director", "Dean of Student Affairs","Dean of Academics","Professor","Warden","Caretaker",
                "House Secretary","Maintenance Secretary","Mess Secretary","Sports Secretary","Cultural Secretary","Student"};
        ArrayAdapter<String> typeAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, types);
        typeSpinner.setAdapter(typeAdapter);
        typeSpinner.setOnItemSelectedListener(this);
        Spinner hostelSpinner = (Spinner)findViewById(R.id.spinner_hostel);
        String[] hostels = new String[]{"Jwalamukhi", "Aravali", "Karakoram", "Nilgiri", "Kumaon", "Vindhyachal", "Shivalik", "Satpura",
                "Zanskar", "Girnar", "Udaygiri", "Kailash", "Himadri"};
        ArrayAdapter<String> hostelAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, hostels);
        hostelSpinner.setAdapter(hostelAdapter);
    }


    public void onItemSelected(AdapterView<?> parent, View view,int pos, long id)
    {
        String type=parent.getItemAtPosition(pos).toString();
        char first=type.charAt(0);
        if(first=='D'||first=='P')
        {
            Spinner hostelSpinner = (Spinner)findViewById(R.id.spinner_hostel);
            hostelSpinner.setEnabled(false);
        }
        else
        {
            Spinner hostelSpinner = (Spinner)findViewById(R.id.spinner_hostel);
            hostelSpinner.setEnabled(true);
        }

    }

    public void onNothingSelected(AdapterView<?> parent)
    {
    }

    public void submit(View view){

    }
}
