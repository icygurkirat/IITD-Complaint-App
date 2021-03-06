package com.gkiratbajwa.www.iitdcomplaintapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
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
import android.text.Html;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
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
    Intent intent;
    Button button;
    String id_user;

    // This function gets the main URL of the server
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_individual_complaint);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final WifiManager manager = (WifiManager) super.getSystemService(WIFI_SERVICE);
        final DhcpInfo dhcp = manager.getDhcpInfo();
        String gateway = LoginActivity.intToIp(dhcp.gateway);
        button = (Button) findViewById(R.id.resolveButton);
        URL = "http://"+gateway +":8000";
        intent = getIntent();
        id = intent.getExtras().getString("id");
        SharedPreferences userData = getApplication().getSharedPreferences("profileData", MODE_PRIVATE);
        id_user=Integer.toString(userData.getInt("id",-1));
        showIndividualComplaint();
    }


    // This function is basically used to populate the details of the complaints
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
                    if((complaint.getString("resolved").equals("1"))||!(complaintTo.equals(id_user)))
                    {
                        button.setVisibility(View.INVISIBLE);
                    }
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
                    // To populate the comments of a complaint
                    JSONArray comments =  response.getJSONArray("comments");
                    for(int i=0;i<comments.length();i++){

                        RelativeLayout layout = (RelativeLayout) findViewById(R.id.complaintLayout);
                        final String commentUser=comments.getJSONObject(i).getString("user_name");
                        final String comment=comments.getJSONObject(i).getString("description");
                        String display="<b>"+commentUser+"</b> : "+comment;
                        TextView txt=new TextView(getApplicationContext());
                        txt.setText(Html.fromHtml(display));
                        txt.setTextColor(Color.parseColor("#000000"));
                        txt.setId(i+10);
                        txt.setTextSize(TypedValue.COMPLEX_UNIT_PT, 9);
                        RelativeLayout.LayoutParams lay= new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                        if(i==0)
                            lay.addRule(RelativeLayout.BELOW, R.id.resolveButton);
                        else
                            lay.addRule(RelativeLayout.BELOW, i+9);
                        txt.setLayoutParams(lay);
                        layout.addView(txt);

                    }
                    sendComment();

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
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    // this function uses the api to convert the userId to whom the complaint has been sent to the name
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

    // this function uses the api to convert the userId of the person sending the complaint to the name
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

    // this function handles adding new comments
    private void sendComment()
    {
        RelativeLayout layout = (RelativeLayout) findViewById(R.id.postComments);

        Button sendButton = new Button(getApplicationContext());
        sendButton.setText("SEND");
        RelativeLayout.LayoutParams lay2= new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        lay2.addRule(RelativeLayout.BELOW, R.id.comText);
        lay2.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        sendButton.setLayoutParams(lay2);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    EditText comment_send = (EditText) findViewById(R.id.comText);
                    String desc = comment_send.getText().toString().replaceAll(" ", "%20");
                    if (desc.length() == 0) {
                        Toast.makeText(getApplicationContext(),"Empty comments are not allowed",Toast.LENGTH_SHORT).show();
                        return;
                    }
                    CustomJsonRequest request = new CustomJsonRequest(URL + "/complaints/post_comment.json?Complaint_id="+id+"&description="+desc, null
                            , new Response.Listener<String>() {
                        @Override
                        //Parse LOGIN
                        public void onResponse(String response) {

                            try {
                                JSONObject response1 = new JSONObject(response);
                                boolean success = response1.getBoolean("success");
                                if (success) {
                                    Toast.makeText(getApplicationContext(), "Comment successfully posted", Toast.LENGTH_SHORT).show();
                                    Intent intent = getIntent();
                                    finish();
                                    startActivity(intent);
                                } else {
                                    Toast.makeText(getApplicationContext(), "Comment failed to post", Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException e) {
                                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                            , new Response.ErrorListener() {
                        @Override
                        //Handle Errors
                        public void onErrorResponse(VolleyError volleyError) {
                            Toast.makeText(getApplicationContext(), volleyError.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                    request.setTag("commentRequest");
                    final ComplaintAppApplication complaintAppApplication = (ComplaintAppApplication) getApplicationContext();

                    //initialize request queue
                    mqueue = complaintAppApplication.getmRequestQueue();
                    mqueue.add(request);
                } catch (Exception e) {

                }
            }
        });
        layout.addView(sendButton);

    }

    // this function handles if the user upvotes a complaint
    public void doUpvote(View view)
    {
        CustomJsonRequest request = new CustomJsonRequest(URL+"/complaints/upvote.json/"+id,null
                ,new Response.Listener<String>(){
            @Override
            //Parse LOGIN
            public void onResponse(String response1){
                try {
                    JSONObject response = new JSONObject(response1);
                    Boolean upvote = response.getBoolean("success");
                    if(upvote)
                    {
                        Toast.makeText(getApplicationContext(), "Your vote has been registered", Toast.LENGTH_SHORT).show();
                        Intent intent = getIntent();
                        finish();
                        startActivity(intent);

                    }
                    else
                    {
                        Toast.makeText(getApplicationContext(), "You have already voted", Toast.LENGTH_SHORT).show();
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

    // this function handles if the user downvotes a complaint
    public void doDownvote(View view)
    {
        CustomJsonRequest request = new CustomJsonRequest(URL+"/complaints/downvote.json/"+id,null
                ,new Response.Listener<String>(){
            @Override
            //Parse LOGIN
            public void onResponse(String response1){
                try {
                    JSONObject response = new JSONObject(response1);
                    Boolean downvote = response.getBoolean("success");
                    if(downvote)
                    {
                        Toast.makeText(getApplicationContext(), "Your vote has been registered", Toast.LENGTH_SHORT).show();
                        Intent intent = getIntent();
                        finish();
                        startActivity(intent);

                    }
                    else
                    {
                        Toast.makeText(getApplicationContext(), "You have already voted", Toast.LENGTH_SHORT).show();
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

    // this function handles the resolve button
    public void doResolve(View view)
    {
        CustomJsonRequest request = new CustomJsonRequest(URL+"/complaints/resolve.json/"+id,null
                ,new Response.Listener<String>(){
            @Override
            //Parse LOGIN
            public void onResponse(String response1){
                try {
                    JSONObject response = new JSONObject(response1);
                    Boolean resolve = response.getBoolean("success");
                    if(resolve)
                    {
                        Toast.makeText(getApplicationContext(), "The complaint has been resolved", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getApplicationContext(), IndividualComplaint.class);
                        intent.putExtra("id", id);
                        startActivity(intent);

                    }
                    else
                    {
                        Toast.makeText(getApplicationContext(), "Wrong input", Toast.LENGTH_SHORT).show();
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


}
