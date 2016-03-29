package com.gkiratbajwa.www.iitdcomplaintapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.DhcpInfo;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ResolvedFragment extends Fragment {
    private List<Complaint> complaintsList2 = new ArrayList<>();
    private RecyclerView recyclerView;
    private ComplaintAdapter mAdapter;
    View rootView;
    String URL;
    SharedPreferences user;
    int user_Id;
    RequestQueue mqueue;
    private List<Complaint> complaintsList = new ArrayList<>();

    public ResolvedFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final WifiManager manager = (WifiManager) super.getActivity().getSystemService(getActivity().WIFI_SERVICE);
        final DhcpInfo dhcp = manager.getDhcpInfo();
        String gateway = LoginActivity.intToIp(dhcp.gateway);
        URL = "http://"+gateway +":8000";
        user = getActivity().getApplication().getSharedPreferences("profileData", getActivity().MODE_PRIVATE);
        user_Id = user.getInt("id", 0);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        rootView = inflater.inflate(R.layout.fragment_sent, container, false);
        recyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);
        Complaint c = new Complaint("Please Wait","Complaints are being received","Some time","dead");
        complaintsList.add(c);
        mAdapter = new ComplaintAdapter(complaintsList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);
        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getActivity().getApplicationContext(), recyclerView, new ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Complaint complaint = complaintsList2.get(position);
                Toast.makeText(getActivity().getApplicationContext(), complaint.getName() + " is selected!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getContext(), IndividualComplaint.class);
                intent.putExtra("id", complaint.getId());
                intent.putExtra("resolve","0");
                startActivity(intent);
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));
        showComplaints1();
        return rootView;
    }

    public interface ClickListener {
        void onClick(View view, int position);

        void onLongClick(View view, int position);
    }

    public static class RecyclerTouchListener implements RecyclerView.OnItemTouchListener {

        private GestureDetector gestureDetector;
        private ResolvedFragment.ClickListener clickListener;

        public RecyclerTouchListener(Context context, final RecyclerView recyclerView, final ResolvedFragment.ClickListener clickListener) {
            this.clickListener = clickListener;
            gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }

                @Override
                public void onLongPress(MotionEvent e) {
                    View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
                    if (child != null && clickListener != null) {
                        clickListener.onLongClick(child, recyclerView.getChildPosition(child));
                    }
                }
            });
        }

        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {

            View child = rv.findChildViewUnder(e.getX(), e.getY());
            if (child != null && clickListener != null && gestureDetector.onTouchEvent(e)) {
                clickListener.onClick(child, rv.getChildPosition(child));
            }
            return false;
        }

        @Override
        public void onTouchEvent(RecyclerView rv, MotionEvent e) {
        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

        }
    }

    // this function populates the resolved complaints in the fragment
    private void showComplaints1() {
        CustomJsonRequest request = new CustomJsonRequest(URL+"/complaints/get_resolved.json"+"/"+user_Id,null
                ,new Response.Listener<String>(){
            @Override
            //Parse LOGIN
            public void onResponse(String response1){
                try {
                    JSONObject response = new JSONObject(response1);
                    JSONArray complaints = response.getJSONArray("complaints");
                    Complaint d;
                    for(int i=0;i<complaints.length();i++){
                        final JSONObject complaint = complaints.getJSONObject(i);
                        String name = complaint.getString("name");
                        String description = complaint.getString("description");
                        String date = complaint.getString("datePosted");
                        String complaintId = complaint.getString("id");
                        d = new Complaint(name, description, date, complaintId);
                        complaintsList2.add(d);
                    }
                    mAdapter = new ComplaintAdapter(complaintsList2);
                    RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
                    recyclerView.setLayoutManager(mLayoutManager);
                    recyclerView.setItemAnimator(new DefaultItemAnimator());
                    recyclerView.setAdapter(mAdapter);
                    recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getActivity().getApplicationContext(), recyclerView, new ClickListener() {
                        @Override
                        public void onClick(View view, int position) {
                            Complaint complaint = complaintsList2.get(position);
                            Toast.makeText(getActivity().getApplicationContext(), complaint.getName() + " is selected!", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(getContext(), IndividualComplaint.class);
                            intent.putExtra("id", complaint.getId());
                            startActivity(intent);
                        }

                        @Override
                        public void onLongClick(View view, int position) {

                        }
                    }));

                }
                catch(JSONException e){

                }
            }
        }
                ,new Response.ErrorListener() {
            @Override
            //Handle Errors
            public void onErrorResponse(VolleyError volleyError) {
                Toast.makeText(getContext(), volleyError.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });
        request.setTag("complaintRequest");
        //default implementation of handling cookies
        final ComplaintAppApplication complaintAppApplication=(ComplaintAppApplication) getActivity().getApplicationContext();

        //initialize request queue
        mqueue = complaintAppApplication.getmRequestQueue();
        mqueue.add(request);
    }

}
