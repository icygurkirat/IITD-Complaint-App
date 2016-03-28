package com.gkiratbajwa.www.iitdcomplaintapp;

import android.net.DhcpInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

public class IndividualComplaint extends AppCompatActivity {
    String URL;
    String id;

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

    private void showIndividualComplaint()
    {

    }

}
