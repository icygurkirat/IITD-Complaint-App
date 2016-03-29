package com.gkiratbajwa.www.iitdcomplaintapp;

import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.DhcpInfo;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/*
    This activity is displayed on clicking the SIGNUP button in the LoginActivity and helps to register a new user to the server.
    It has fields for entering name,username, password, type and hostel.
    POST request is implemented on clicking the submit button within this activity
 */
public class SignupActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        //populating the data in spinners
        Spinner typeSpinner = (Spinner)findViewById(R.id.spinner_type);
        String[] types = new String[]{"Director", "Dean of Student Affairs","Dean of Academics","Professor","Warden","Caretaker",
                "House Secretary","Maintenance Secretary","Mess Secretary","Sports Secretary","Cultural Secretary","Student"};
        ArrayAdapter<String> typeAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, types);
        typeSpinner.setAdapter(typeAdapter);
        typeSpinner.setOnItemSelectedListener(this);
        Spinner hostelSpinner = (Spinner)findViewById(R.id.spinner_hostel);
        String[] hostels = new String[]{"Zanskar", "Shivalik", "Vindhyachal", "Kumaon", "Jwalamukhi", "Aravalli", "Karakoram",
                "Nilgiri", "Satpura", "Girnar", "Udaygiri", "Kailash", "Himadri"};
        ArrayAdapter<String> hostelAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, hostels);
        hostelSpinner.setAdapter(hostelAdapter);
    }

    //Function is called whenever the selected item in typeSpinner changes
    public void onItemSelected(AdapterView<?> parent, View view,int pos, long id)
    {
        String type=parent.getItemAtPosition(pos).toString();
        char first=type.charAt(0);
        if(first=='D'||first=='P')
        {
            //if the user is director, dean or professor
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

    //parameters to be posted by POST request
    public HashMap<String,String> getParams()
    {
        EditText signupName=(EditText) findViewById(R.id.signup_editText_name);
        Spinner signupType=(Spinner) findViewById(R.id.spinner_type);
        Spinner signupHostel=(Spinner) findViewById(R.id.spinner_hostel);
        EditText signupUsername=(EditText) findViewById(R.id.signup_editText_username);
        EditText signupPassword=(EditText) findViewById(R.id.signup_editText_password);

        int type=signupType.getSelectedItemPosition()+1;
        int hostelId;
        if(type<=4)
            hostelId=5;
        else
        {
            hostelId=signupHostel.getSelectedItemPosition();
            if(hostelId<=1)
                hostelId+=3;
            else
                hostelId+=4;
        }

        HashMap<String,String> params = new HashMap<String,String>();
        params.put("type",Integer.toString(type));
        params.put("hostel_id",Integer.toString(hostelId));
        params.put("username",signupUsername.getText().toString());
        params.put("password",signupPassword.getText().toString());
        params.put("first_name",signupName.getText().toString());
        return params;
    }

    public boolean checkEmpty()
    {
        EditText signupName=(EditText) findViewById(R.id.signup_editText_name);
        EditText signupUsername=(EditText) findViewById(R.id.signup_editText_username);
        EditText signupPassword=(EditText) findViewById(R.id.signup_editText_password);
        return (signupUsername.getText().toString().isEmpty()||signupPassword.getText().toString().isEmpty()||signupName.getText().toString().isEmpty());
    }

    //Function called when submit button is pressed
    public void submit(View view)
    {
        if(checkEmpty())
        {
            Toast.makeText(getApplicationContext(),"Some Fields are empty!", Toast.LENGTH_SHORT).show();
        }else {

            final ComplaintAppApplication complaintAppApplication = (ComplaintAppApplication) getApplicationContext();
            RequestQueue mqueue = complaintAppApplication.getmRequestQueue();

            final WifiManager manager = (WifiManager) getSystemService(WIFI_SERVICE);
            final DhcpInfo dhcp = manager.getDhcpInfo();
            final String gateway = LoginActivity.intToIp(dhcp.gateway);
            String URL = "http://" + gateway + ":8000/default/register.json";

            ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo network = cm.getActiveNetworkInfo();
            boolean isConnected = network != null && network.isConnectedOrConnecting();

            if (isConnected) {
                CustomJsonRequest request = new CustomJsonRequest(Request.Method.POST, URL, getParams(), new Response.Listener<String>() {
                    //Send JSON request and handle the response
                    @Override
                    public void onResponse(String response1) {
                        try {
                            JSONObject response = new JSONObject(response1);
                            boolean success = response.getBoolean("success");
                            if (success) {
                                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                                startActivity(intent);
                            } else
                                Toast.makeText(getApplicationContext(), "Server declined the request", Toast.LENGTH_SHORT).show();

                        } catch (JSONException e) {
                            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
                    //Handle Error Responses
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Toast.makeText(getApplicationContext(), gateway + volleyError.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
                request.setTag("signUp");
                mqueue.add(request);
            } else {
                DialogFragment showInternet = new LoginActivity.showInternetDialogFragment();
                showInternet.show(getFragmentManager(), "showInternet");
            }
        }
    }
}
