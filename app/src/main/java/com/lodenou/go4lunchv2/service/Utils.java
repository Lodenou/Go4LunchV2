package com.lodenou.go4lunchv2.service;



import android.Manifest;
import android.content.pm.PackageManager;

import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.lodenou.go4lunchv2.model.nearbysearch.OpeningHours;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Utils {

    public static String convertDateToHour(Date date){
        DateFormat dfTime = new SimpleDateFormat("HH:mm");
        return dfTime.format(date);
    }

    public static String isOpenOrNot(OpeningHours openingHours){
        if (openingHours != null) {
            if (openingHours.getOpenNow()) {
                return ("Open");
            } else {
                return ("Closed");
            }
        } else {
            return ("Pas d'horaire spécifié");
        }
    }

    public static String formatLocation(Double lat ,Double lng){
        return lat.toString() + "," + lng.toString();
    }

}
