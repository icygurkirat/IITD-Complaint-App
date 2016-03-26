// This Java file defines the Adapter for the complaints
package com.gkiratbajwa.www.iitdcomplaintapp;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Gautam on 26/03/16.
 */
public class ComplaintAdapter extends RecyclerView.Adapter<ComplaintAdapter.MyViewHolder> {

    private List<Complaint> complaintsList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView name, date, sentTo;

        public MyViewHolder(View view) {
            super(view);
            name = (TextView) view.findViewById(R.id.name);
            date = (TextView) view.findViewById(R.id.date);
            sentTo = (TextView) view.findViewById(R.id.sentTo);
        }
    }


    public ComplaintAdapter(List<Complaint> complaintsList) {
        this.complaintsList = complaintsList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.complaint_list_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Complaint complaint = complaintsList.get(position);
        holder.name.setText(complaint.getName());
        holder.sentTo.setText(complaint.getsentTo());
        holder.date.setText(complaint.getDate());
    }

    @Override
    public int getItemCount() {
        return complaintsList.size();
    }
}
