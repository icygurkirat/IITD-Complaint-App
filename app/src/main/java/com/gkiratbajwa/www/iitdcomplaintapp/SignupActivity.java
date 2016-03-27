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

public class SignupActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
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

    public HashMap<String,String> getParams()
    {
        EditText signupName=(EditText) findViewById(R.id.signup_editText_name);
        Spinner signupType=(Spinner) findViewById(R.id.spinner_type);
        Spinner signupHostel=(Spinner) findViewById(R.id.spinner_hostel);
        EditText signupUsername=(EditText) findViewById(R.id.signup_editText_username);
        EditText signupPassword=(EditText) findViewById(R.id.signup_editText_password);

        HashMap<String,String> params = new HashMap<String,String>();
        params.put("hostel_id",Integer.toString(signupHostel.getSelectedItemPosition()));
        params.put("type",Integer.toString(signupType.getSelectedItemPosition()+1));
        params.put("username",signupUsername.toString());
        params.put("password",signupPassword.toString());
        params.put("first_name",signupName.toString());
        return params;
    }

    public void submit(View view)
    {
        final ComplaintAppApplication complaintAppApplication = (ComplaintAppApplication) getApplicationContext();
        RequestQueue mqueue = complaintAppApplication.getmRequestQueue();

        final WifiManager manager = (WifiManager) getSystemService(WIFI_SERVICE);
        final DhcpInfo dhcp = manager.getDhcpInfo();
        final String gateway = LoginActivity.intToIp(dhcp.gateway);
        String URL = "http://" + gateway + ":8000/default/register";

        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo network = cm.getActiveNetworkInfo();
        boolean isConnected = network != null && network.isConnectedOrConnecting();

        if(isConnected)
        {
            CustomJsonRequest request = new CustomJsonRequest(Request.Method.POST, URL, getParams(), new Response.Listener<String>() {
                //Send JSON request and handle the response
                @Override
                public void onResponse(String response1)
                {
                    try
                    {
                        JSONObject response = new JSONObject(response1);
                        boolean success = response.getBoolean("success");
                        if(success)
                        {
                            Intent intent = new Intent(getApplicationContext(), ComplaintActivity.class);
                            startActivity(intent);
                        }
                        else
                            Toast.makeText(getApplicationContext(),"Server declined the request", Toast.LENGTH_SHORT).show();

                    }
                    catch (JSONException e) {
                        Toast.makeText(getApplicationContext(),e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }, new Response.ErrorListener()
            {
                //Handle Error Responses
                @Override
                public void onErrorResponse(VolleyError volleyError) {
                    Toast.makeText(getApplicationContext(), gateway + volleyError.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
            request.setTag("signUp");
            mqueue.add(request);
        }
        else
        {
            DialogFragment showInternet = new LoginActivity.showInternetDialogFragment();
            showInternet.show(getFragmentManager(), "showInternet");
        }
    }
}
