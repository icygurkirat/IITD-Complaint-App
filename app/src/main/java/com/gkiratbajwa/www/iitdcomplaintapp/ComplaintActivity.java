// This is the Java file that holds the tabs
package com.gkiratbajwa.www.iitdcomplaintapp;

import android.app.DialogFragment;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.DhcpInfo;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.TabLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NotificationCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.getbase.floatingactionbutton.FloatingActionsMenu;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

//This class displays the incoming, sent and resolved complaints. Moreover, it has buttons for new complaint, reload logout.
public class ComplaintActivity extends AppCompatActivity {

    String firstname,username;
    int id,hostelId,verified,type;

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complaint);

        //Getting loginData from shared preferences
        SharedPreferences userData = getApplication().getSharedPreferences("profileData", MODE_PRIVATE);
        firstname=userData.getString("firstname","");
        username=userData.getString("username","");
        id=userData.getInt("id",-1);
        hostelId=userData.getInt("hostelId",-1);
        type=userData.getInt("type",-1);
        verified=userData.getInt("verified",-1);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setOffscreenPageLimit(2);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);


        //Populating the floatingactionsmenu with add complaints buttons and logout button
        FloatingActionsMenu menu=(FloatingActionsMenu)findViewById(R.id.multiple_actions);
        com.getbase.floatingactionbutton.FloatingActionButton complaint1=new com.getbase.floatingactionbutton.FloatingActionButton(this);
        complaint1.setSize(com.getbase.floatingactionbutton.FloatingActionButton.SIZE_MINI);
        complaint1.setTitle("Send individual complaint");
        complaint1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(verified==1)
                    newComplaint(2);
                 else
                    Toast.makeText(getApplicationContext(), "User Not Verified!", Toast.LENGTH_SHORT).show();
            }
        });
        menu.addButton(complaint1);

        com.getbase.floatingactionbutton.FloatingActionButton complaint2=new com.getbase.floatingactionbutton.FloatingActionButton(this);
        complaint2.setSize(com.getbase.floatingactionbutton.FloatingActionButton.SIZE_MINI);
        complaint2.setTitle("Send hostel level complaint");
        complaint2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(verified==1)
                    newComplaint(1);
                else
                    Toast.makeText(getApplicationContext(), "User Not Verified!", Toast.LENGTH_SHORT).show();
            }
        });
        menu.addButton(complaint2);

        com.getbase.floatingactionbutton.FloatingActionButton complaint3=new com.getbase.floatingactionbutton.FloatingActionButton(this);
        complaint3.setSize(com.getbase.floatingactionbutton.FloatingActionButton.SIZE_MINI);
        complaint3.setTitle("Send institute level complaint");
        complaint3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(verified==1)
                    newComplaint(0);
                else
                    Toast.makeText(getApplicationContext(), "User Not Verified!", Toast.LENGTH_SHORT).show();
            }
        });
        menu.addButton(complaint3);

        com.getbase.floatingactionbutton.FloatingActionButton reload=new com.getbase.floatingactionbutton.FloatingActionButton(this);
        reload.setSize(com.getbase.floatingactionbutton.FloatingActionButton.SIZE_MINI);
        reload.setTitle("Reload Complaints");
        reload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Reload Activity
                Toast.makeText(getApplicationContext(), "Reloading Complaints!", Toast.LENGTH_SHORT).show();
                Intent intent = getIntent();
                finish();
                startActivity(intent);
            }
        });
        menu.addButton(reload);

        com.getbase.floatingactionbutton.FloatingActionButton logout=new com.getbase.floatingactionbutton.FloatingActionButton(this);
        logout.setSize(com.getbase.floatingactionbutton.FloatingActionButton.SIZE_MINI);
        logout.setTitle("Log out");
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logoutUser();
            }
        });
        menu.addButton(logout);



    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_complaint, menu);
        return true;
    }

    private boolean backExit=false;
    //implement proper backstack
    @Override
    public void onBackPressed() {
        final ComplaintAppApplication complaintAppApplication=(ComplaintAppApplication) getApplicationContext();
        RequestQueue mqueue= complaintAppApplication.getmRequestQueue();
        mqueue.cancelAll("");
        if(backExit){
            finish();
            System.exit(0);
        }
        else {
            Toast.makeText(this, "Press Back again to Exit.",
                    Toast.LENGTH_SHORT).show();
            backExit = true;
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    backExit = false;
                }
            }, 3 * 1000);
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

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            Integer section = getArguments().getInt(ARG_SECTION_NUMBER);
            View rootView  = inflater.inflate(R.layout.fragment_sent, container, false);
            return rootView;
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            switch (position){
                case 0:
                    //page 1
                    return new SentFragment();
                case 1:
                    //page 2
                    return new IncomingFragment();
                case 2:
                    //page 3
                    return new ResolvedFragment();
                default:
                    //this page does not exists
                    return null;
            }
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Sent";
                case 1:
                    return "Incoming";
                case 2:
                    return "Resolved";
            }
            return null;
        }
    }

    public void logoutUser()
    {
        //function called when logout button is pressed. It uses GET request with logout API
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

            CustomJsonRequest request = new CustomJsonRequest(URL + "/default/logout.json", null
                    , new Response.Listener<String>() {
                @Override
                //Parse LOGIN
                public void onResponse(String response1)
                {
                    try
                    {
                        JSONObject response=new JSONObject(response1);
                        SharedPreferences pref1 = getApplicationContext().getSharedPreferences("loginData", MODE_PRIVATE);
                        SharedPreferences.Editor editor1 = pref1.edit();
                        editor1.putBoolean("loginSuccess", false);
                        editor1.apply();
                        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        Toast.makeText(getApplicationContext(), "Logout Successful!", Toast.LENGTH_SHORT).show();
                        startActivity(intent);

                    }
                    catch (JSONException e) {
                        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
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
            request.setTag("logoutRequest");

            mqueue.add(request);
        }
        else
        {
            DialogFragment showInternet = new LoginActivity.showInternetDialogFragment();
            showInternet.show(getFragmentManager(), "showInternet");
        }
    }

    //Open NewComplaint activity on clicking new complaint button
    public void newComplaint(int type)
    {
        Intent intent=new Intent(getApplicationContext(),NewComplaint.class);
        intent.putExtra("type",type);
        startActivity(intent);
    }
}
