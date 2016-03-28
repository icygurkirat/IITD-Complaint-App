package com.gkiratbajwa.www.iitdcomplaintapp;

import android.content.Intent;
import android.net.DhcpInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class IndividualComplaint extends AppCompatActivity {
    String URL;
    String id;
    RequestQueue mqueue;
    String complaintTo;
    String complaintBy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_individual_complaint);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final WifiManager manager = (WifiManager) super.getSystemService(WIFI_SERVICE);
        final DhcpInfo dhcp = manager.getDhcpInfo();
        String gateway = LoginActivity.intToIp(dhcp.gateway);
        URL = "http://"+gateway +":8000";
        id = getIntent().getExtras().getString("id");
        showIndividualComplaint();
    }



    private void showIndividualComplaint() {
        CustomJsonRequest request = new CustomJsonRequest(URL+"/complaints/complaint.json/"+id,null
                ,new Response.Listener<String>(){
            @Override
            //Parse LOGIN
            public void onResponse(String response1){
                try {
                    final TextView name =(TextView) findViewById(R.id.profile_name);
                    final TextView description =(TextView) findViewById(R.id.profile_description);
                    final TextView datePosted =(TextView) findViewById(R.id.profile_datePosted);
                    final TextView type =(TextView) findViewById(R.id.profile_type);
                    final TextView upvote =(TextView) findViewById(R.id.profile_upvote);
                    final TextView downvote =(TextView) findViewById(R.id.profile_downvote);
                    final TextView dateResolved =(TextView) findViewById(R.id.profile_dateResolved);
                    JSONObject response = new JSONObject(response1);
                    JSONObject complaint = response.getJSONObject("complaint");
                    name.setText(name.getText() + "\t:\t" + complaint.getString("name"));
                    description.setText(description.getText()+"\t:\t"+complaint.getString("description"));
                    datePosted.setText(datePosted.getText()+"\t:\t"+complaint.getString("datePosted"));
                    complaintTo = complaint.getString("complaintTo_id");
                    complaintBy = complaint.getString("user_id");
                    showcomplaintTo();
                    showcomplaintBy();
                    switch(complaint.getString("Complaint_type"))
                    {
                        case "0":
                        {
                            type.setText(type.getText() + "\t:\t" + "Institute Level");
                            break;
                        }
                        case "1":
                        {
                            type.setText(type.getText() + "\t:\t" + "Hostel Level");
                            break;
                        }
                        case "2":
                        {
                            type.setText(type.getText() + "\t:\t" + "Personal Level");
                            break;
                        }
                        case "3":
                        {
                            type.setText(type.getText() + "\t:\t" + "Sign Up Request");
                            break;
                        }

                    }
                    upvote.setText(upvote.getText()+"\t:\t"+complaint.getString("upvote"));
                    downvote.setText(downvote.getText()+"\t:\t"+complaint.getString("downvote"));
                    if(complaint.getString("resolved").equals("0"))
                    {
                        dateResolved.setText(dateResolved.getText()+"\t:\t"+"Not resolved");
                    }
                    else
                    {
                        dateResolved.setText(dateResolved.getText()+"\t:\t"+complaint.getString("dateResolved"));
                    }

                }
                catch(JSONException e){

                }
            }
        }
                ,new Response.ErrorListener() {
            @Override
            //Handle Errors
            public void onErrorResponse(VolleyError volleyError) {
                Toast.makeText(getApplicationContext(), volleyError.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });
        request.setTag("complaintRequest");
        //default implementation of handling cookies
        final ComplaintAppApplication complaintAppApplication=(ComplaintAppApplication)getApplicationContext();

        //initialize request queue
        mqueue = complaintAppApplication.getmRequestQueue();
        mqueue.add(request);
    }

    @Override
    public void onBackPressed()
    {
        Intent intent = new Intent(getApplicationContext(), ComplaintActivity.class);
        startActivity(intent);
    }

    private void showcomplaintTo()
    {
        CustomJsonRequest request = new CustomJsonRequest(URL+"/users/getUser.json/"+complaintTo,null
                ,new Response.Listener<String>(){
            @Override
            //Parse LOGIN
            public void onResponse(String response1){
                try {
                    final TextView to =(TextView) findViewById(R.id.profile_to);
                    JSONObject response = new JSONObject(response1);
                    JSONArray users = response.getJSONArray("users");
                    JSONObject user = users.getJSONObject(0);
                    to.setText(to.getText() + "\t:\t" + user.getString("first_name"));
                }
                catch(JSONException e){

                }
            }
        }
                ,new Response.ErrorListener() {
            @Override
            //Handle Errors
            public void onErrorResponse(VolleyError volleyError) {
                Toast.makeText(getApplicationContext(), volleyError.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });
        request.setTag("complaintRequest");
        //default implementation of handling cookies
        final ComplaintAppApplication complaintAppApplication=(ComplaintAppApplication)getApplicationContext();

        //initialize request queue
        mqueue = complaintAppApplication.getmRequestQueue();
        mqueue.add(request);
    }

    private void showcomplaintBy()
    {
        CustomJsonRequest request = new CustomJsonRequest(URL+"/users/getUser.json/"+complaintBy,null
                ,new Response.Listener<String>(){
            @Override
            //Parse LOGIN
            public void onResponse(String response1){
                try {
                    final TextView by =(TextView) findViewById(R.id.profile_by);
                    JSONObject response = new JSONObject(response1);
                    JSONArray users = response.getJSONArray("users");
                    JSONObject user = users.getJSONObject(0);
                    by.setText(by.getText() + "\t:\t" + user.getString("first_name"));
                }
                catch(JSONException e){

                }
            }
        }
                ,new Response.ErrorListener() {
            @Override
            //Handle Errors
            public void onErrorResponse(VolleyError volleyError) {
                Toast.makeText(getApplicationContext(), volleyError.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });
        request.setTag("complaintRequest");
        //default implementation of handling cookies
        final ComplaintAppApplication complaintAppApplication=(ComplaintAppApplication)getApplicationContext();

        //initialize request queue
        mqueue = complaintAppApplication.getmRequestQueue();
        mqueue.add(request);
    }

}
