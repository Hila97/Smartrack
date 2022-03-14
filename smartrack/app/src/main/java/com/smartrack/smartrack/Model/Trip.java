package com.smartrack.smartrack.Model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;

@Entity
public class Trip implements Parcelable {
    @PrimaryKey
    @NonNull
    private String id_trip;
    private String travelerMail;
    private String tripDestination;
    private String tripName;
    private int tripDaysNumber;
    private String date;

    @Ignore
    public Trip(){ }
    public Trip(String id_trip,String date,String travelerMail,String tripDestination, String tripName,int tripDaysNumber) {
        this.id_trip = id_trip;
        this.tripDestination = tripDestination;
        this.tripName = tripName;
        this.tripDaysNumber = tripDaysNumber;
        this.travelerMail=travelerMail;
        this.date=date;
    }


    protected Trip(Parcel in) {
        id_trip = in.readString();
        travelerMail = in.readString();
        tripDestination = in.readString();
        tripName = in.readString();
        tripDaysNumber = in.readInt();
        date = in.readString();
    }

    public static final Creator<Trip> CREATOR = new Creator<Trip>() {
        @Override
        public Trip createFromParcel(Parcel in) {
            return new Trip(in);
        }

        @Override
        public Trip[] newArray(int size) {
            return new Trip[size];
        }
    };

    @NonNull
    public String getId_trip() {
        return id_trip;
    }

    public void setId_trip(@NonNull String id_trip) {
        this.id_trip = id_trip;
    }

    public String getTravelerMail() {
        return travelerMail;
    }

    public void setTravelerMail(String travelerMail) {
        this.travelerMail = travelerMail;
    }

    public String getTripDestination() {
        return tripDestination;
    }

    public void setTripDestination(String tripDestination) {
        this.tripDestination = tripDestination;
    }

    public String getTripName() {
        return tripName;
    }

    public void setTripName(String tripName) {
        this.tripName = tripName;
    }

    public int getTripDaysNumber() {
        return tripDaysNumber;
    }

    public void setTripDaysNumber(int tripDaysNumber) {
        this.tripDaysNumber = tripDaysNumber;
    }


    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id_trip);
        dest.writeString(travelerMail);
        dest.writeString(tripDestination);
        dest.writeString(tripName);
        dest.writeInt(tripDaysNumber);
        dest.writeString(date);
    }

}