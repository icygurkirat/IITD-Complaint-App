package com.gkiratbajwa.www.iitdcomplaintapp;

import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class NewComplaint extends AppCompatActivity {
    int type;
    int user_id,hostel_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_complaint);

        Toast.makeText(getApplicationContext(), "New Complaint!", Toast.LENGTH_SHORT).show();
        Intent intent=getIntent();
        type=intent.getExtras().getInt("type");

        populateProfs();

        SharedPreferences userData = getApplication().getSharedPreferences("profileData", MODE_PRIVATE);
        user_id=userData.getInt("id",-1);
        hostel_id=userData.getInt("hostelId",-1);

        Spinner to = (Spinner)findViewById(R.id.spinner_to);
        String[] types = new String[]{"Director", "Dean of Student Affairs","Dean of Academics","Professor","Warden","Caretaker",
                "House Secretary","Maintenance Secretary","Mess Secretary","Sports Secretary","Cultural Secretary","Student"};
        ArrayAdapter<String> toAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, types);
        to.setAdapter(toAdapter);
        to.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String type = parent.getItemAtPosition(position).toString();
                TextView profName = (TextView) findViewById(R.id.newComplaint_prof);
                Spinner profList = (Spinner) findViewById(R.id.spinner_prof);
                if (type.equals("Professor")) {
                    profName.setVisibility(View.VISIBLE);
                    profList.setVisibility(View.VISIBLE);
                } else {
                    profName.setVisibility(View.INVISIBLE);
                    profList.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    //parameters to be posted by POST request
    public HashMap<String,String> getParams()
    {
        EditText name=(EditText)findViewById(R.id.newComplaint_editText_name);
        EditText description=(EditText)findViewById(R.id.newComplaint_editText_desc);
        Spinner to=(Spinner)findViewById(R.id.spinner_to);

        HashMap<String,String> params = new HashMap<String,String>();
        params.put("name",name.getText().toString());
        params.put("description",description.getText().toString());
        params.put("user_id",Integer.toString(user_id));
        params.put("hostel_id",Integer.toString(hostel_id));
        params.put("Complaint_type",Integer.toString(type));
        params.put("ComplaintTo_type", Integer.toString(to.getSelectedItemPosition() + 1));
        if(to.getSelectedItemPosition()==3)
        {
            SharedPreferences profIds = getApplication().getSharedPreferences("profIds", MODE_PRIVATE);
            params.put("prof_id",Integer.toString(profIds.getInt("id"+to.getSelectedItemPosition(),0)));
        }

        return params;
    }

    //To check if editText fields are empty
    public boolean checkEmpty()
    {
        EditText name=(EditText)findViewById(R.id.newComplaint_editText_name);
        EditText description=(EditText)findViewById(R.id.newComplaint_editText_desc);
        return (name.getText().toString().isEmpty()||description.getText().toString().isEmpty());
    }

    //Method called when submit button is pressed
    public void submitComplaint(View view)
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
            String URL = "http://" + gateway + ":8000/complaints/post_complaint.json";

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
                                Intent intent = new Intent(getApplicationContext(), ComplaintActivity.class);
                                Toast.makeText(getApplicationContext(), "Complaint Successfully posted!", Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(getApplicationContext(), "Server not reachable!", Toast.LENGTH_SHORT).show();
                    }
                });
                request.setTag("sendComplaint");
                mqueue.add(request);
            } else {
                DialogFragment showInternet = new LoginActivity.showInternetDialogFragment();
                showInternet.show(getFragmentManager(), "showInternet");
            }
        }
    }

    public void populateProfs()
    {
        final ComplaintAppApplication complaintAppApplication = (ComplaintAppApplication) getApplicationContext();
        RequestQueue mqueue = complaintAppApplication.getmRequestQueue();

        final WifiManager manager = (WifiManager) getSystemService(WIFI_SERVICE);
        final DhcpInfo dhcp = manager.getDhcpInfo();
        final String gateway = LoginActivity.intToIp(dhcp.gateway);
        String URL = "http://" + gateway + ":8000";

        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo network = cm.getActiveNetworkInfo();
        boolean isConnected = network != null && network.isConnectedOrConnecting();

        if (isConnected)
        {

            CustomJsonRequest request = new CustomJsonRequest(URL + "/users/getProfs.json", null
                    , new Response.Listener<String>() {
                @Override
                //Parse LOGIN
                public void onResponse(String response1)
                {
                    try
                    {
                        JSONObject response=new JSONObject(response1);
                        JSONArray profs=response.getJSONArray("users");
                        String[] profsArray=new String[profs.length()];

                        SharedPreferences profIds = getApplicationContext().getSharedPreferences("profIds", MODE_PRIVATE);
                        SharedPreferences.Editor editor = profIds.edit();
                        for(int j=0;j<profs.length();j++)
                        {
                            profsArray[j]=profs.getJSONObject(j).getString("first_name");
                            editor.putInt("id" + j, profs.getJSONObject(j).getInt("id"));
                        }
                        Spinner profList = (Spinner) findViewById(R.id.spinner_prof);
                        ArrayAdapter<String> profAdapter = new ArrayAdapter<String>(NewComplaint.this, android.R.layout.simple_spinner_dropdown_item, profsArray);
                        profList.setAdapter(profAdapter);
                        editor.apply();
                    }
                    catch (JSONException e) {
                        Toast.makeText(getApplicationContext(), "Server declined the request!", Toast.LENGTH_SHORT).show();
                    }
                }
            }
                    , new Response.ErrorListener() {
                @Override
                //Handle Errors
                public void onErrorResponse(VolleyError volleyError) {
                    Toast.makeText(getApplicationContext(), "Server not reachable!", Toast.LENGTH_SHORT).show();

                }
            });
            request.setTag("ProfList");

            mqueue.add(request);
        }
        else
        {
            DialogFragment showInternet = new LoginActivity.showInternetDialogFragment();
            showInternet.show(getFragmentManager(), "showInternet");
        }
    }
}
