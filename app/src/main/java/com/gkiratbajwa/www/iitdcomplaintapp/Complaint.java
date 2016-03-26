// This Java file is the file for the objects of complaints
package com.gkiratbajwa.www.iitdcomplaintapp;

/**
 * Created by Gautam on 26/03/16.
 */
public class Complaint {
    private String name, date, sentTo;

    public Complaint() {
    }

    public Complaint(String name, String date, String sentTo) {
        this.name = name;
        this.date = date;
        this.sentTo = sentTo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getsentTo() {
        return sentTo;
    }

    public void setsentTo(String sentTo) {
        this.sentTo = sentTo;
    }
}
