package com.gkiratbajwa.www.iitdcomplaintapp;

import android.app.AlarmManager;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.DhcpInfo;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONException;
import org.json.JSONObject;

/*The first activity displayed on opening the app.
    It consists of 2 editText fields and a button.
     On pressing the button, the information in the EditText fields (if non-empty) is sent to the server via GET request.
     If data is valid, the a positive response is got from the server and ComplaintActivity opens.
 */
public class LoginActivity extends AppCompatActivity {

    boolean loginState = false;
    String URL;
    RequestQueue mqueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences preferences;
        preferences=getApplicationContext().getSharedPreferences("loginData", MODE_PRIVATE);
        //If user is already logged in, directly open the next activity
        if(preferences.getBoolean("loginSuccess",false)){
            super.onCreate(savedInstanceState);
            Intent intent = new Intent(getApplicationContext(), ComplaintActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
        else {
            //else, continue with the same one.
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_login);
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);

            //default implementation of handling cookies
            final ComplaintAppApplication complaintAppApplication=(ComplaintAppApplication) getApplicationContext();

            //initialize request queue
            mqueue = complaintAppApplication.getmRequestQueue();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    //method to convert an IP address in int form to the general string form we all love
    //done via bitshifting multiples of 8
    public static String intToIp(int addr) {
        return  ((addr & 0xFF) + "." +
                ((addr >>>= 8) & 0xFF) + "." +
                ((addr >>>= 8) & 0xFF) + "." +
                ((addr >>>= 8) & 0xFF));
    }

    //start SignupActivity on pressing Signup Button
    public void signupUser(View view){
        Intent intent=new Intent(getApplicationContext(),SignupActivity.class);
        startActivity(intent);
    }

    public void loginUser(View view)
    {
        if(!loginState)
        {   //If user is not logged in, and is connected to network, pass the data to login function
            ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo network = cm.getActiveNetworkInfo();
            boolean isConnected = network != null && network.isConnectedOrConnecting();
            if (isConnected) {
                final EditText editText_username =(EditText) findViewById(R.id.editText_Username);
                final EditText editText_password =(EditText) findViewById(R.id.editText_Password);
                String username = editText_username.getText().toString();
                String password = editText_password.getText().toString();
                if(username.isEmpty()||password.isEmpty())
                    Toast.makeText(getApplicationContext(),"Some fields are Empty!", Toast.LENGTH_SHORT).show();
                else {
                    final WifiManager manager = (WifiManager) super.getSystemService(WIFI_SERVICE);
                    final DhcpInfo dhcp = manager.getDhcpInfo();
                    String gateway = intToIp(dhcp.gateway);
                    URL = "http://" + gateway + ":8000";
                    login(username, password);
                }
            }
            else {
                DialogFragment showInternet = new showInternetDialogFragment();
                showInternet.show(getFragmentManager(),"showInternet");
            }
        }
    }

    //GET request is sent to the server via login API
    private void login(String username, String password) {
        final CustomJsonRequest request = new CustomJsonRequest(URL + "/default/login.json?userid="+username+"&password="+password,null
                ,new Response.Listener<String>(){
            @Override
            //Parse LOGIN
            public void onResponse(String response1){
                try {
                    JSONObject response = new JSONObject(response1);
                    boolean success = response.getBoolean("success");
                    if(success)
                    {

                        JSONObject user = response.getJSONObject("user");
                        //Data received from JSON response. This data is further needed in other activity, hence is shared.
                        SharedPreferences userData = getApplicationContext().getSharedPreferences("profileData",MODE_PRIVATE);
                        SharedPreferences.Editor editor = userData.edit();
                        editor.putString("firstName",user.getString("first_name"));
                        editor.putString("username",user.getString("username"));
                        editor.putInt("id",user.getInt("id"));
                        editor.putInt("hostelId",user.getInt("hostel_id"));
                        editor.putInt("verified",user.getInt("verified"));
                        editor.putInt("type",user.getInt("type"));
                        editor.apply();
                        SharedPreferences pref1 = getApplicationContext().getSharedPreferences("loginData",MODE_PRIVATE);
                        SharedPreferences.Editor editor1 = pref1.edit();
                        editor1.putBoolean("loginSuccess",true);
                        editor1.apply();

                        Toast.makeText(getApplicationContext(),"Login Successful!", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getApplicationContext(), ComplaintActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    }
                    else {
                        Toast.makeText(getApplicationContext(),"Invalid Credentials!", Toast.LENGTH_SHORT).show();
                        SharedPreferences pref1 = getApplicationContext().getSharedPreferences("loginData", MODE_PRIVATE);
                        SharedPreferences.Editor editor1 = pref1.edit();
                        editor1.putBoolean("loginSuccess", false);
                        editor1.apply();
                    }
                }
                catch (JSONException e){
                    Toast.makeText(getApplicationContext(),e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }
                ,new Response.ErrorListener() {
            @Override
            //Handle Errors
            public void onErrorResponse(VolleyError volleyError) {
                Toast.makeText(getApplicationContext(),"Server not reachable!", Toast.LENGTH_SHORT).show();

            }
        });
        request.setTag("loginRequest");

        mqueue.add(request);


    }

    //To show visible networks in case the user is not connected to a network
    public static class showInternetDialogFragment extends DialogFragment{
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState){
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage("Please Connect to the Internet to continue").setTitle("Not Connected to Internet")
                    .setNegativeButton("Cancel",new DialogInterface.OnClickListener(){
                        //Cancels the dialog
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            showInternetDialogFragment.this.getDialog().cancel();
                        }
                    })
                    .setPositiveButton("Connect", new DialogInterface.OnClickListener() {
                        //Starts activity to open WiFi settings
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                        }
                    });
            return builder.create();
        }
    }
}
