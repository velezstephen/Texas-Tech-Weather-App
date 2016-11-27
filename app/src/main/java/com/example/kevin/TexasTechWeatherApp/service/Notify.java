package com.example.kevin.TexasTechWeatherApp.service;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import com.example.kevin.TexasTechWeatherApp.MainActivity;
import com.example.kevin.TexasTechWeatherApp.R;

import static android.content.Context.LOCATION_SERVICE;

/**
 * Created by Stephen on 11/25/2016.
 */


public class Notify extends BroadcastReceiver {

    public static SharedPreferences preferences;

    @Override
    public void onReceive(Context context, Intent intent) {

        //for the rest of temp info
        //for getting all prev temp values
        SharedPreferences pref = context.getSharedPreferences(context.getString(R.string.Prev_Temp), 0);
        SharedPreferences pref1= context.getSharedPreferences(context.getString(R.string.GPS),0);
        SharedPreferences pref2 = context.getSharedPreferences(context.getString(R.string.Prev_Condition), 0);
        SharedPreferences location_name=context.getSharedPreferences(context.getString(R.string.PREF_NAME),0);
        SharedPreferences oldlocation = context.getSharedPreferences(context.getString(R.string.Location_Number), 0);

        //get all prev temp values
        int i=oldlocation.getInt("location_number",0);
        int oldtemp=pref.getInt("Prev_Temp"+i,0);
        String oldcondition=pref2.getString("Prev_Condition"+i,"Unknown");
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
        String preftemp= preferences.getString("temperature_unit","F");//preferred unit is Farenheit
        String locationCheck = location_name.getString("location_name" + i, "Unknown");


        //initate which activity to return to
        Intent repeating_Intent= new Intent(context, MainActivity.class);
        PendingIntent pendingIntent=PendingIntent.getActivity(context,0,repeating_Intent, PendingIntent.FLAG_UPDATE_CURRENT);

        //initiate ui content
        NotificationCompat.Builder nbuilder= new NotificationCompat.Builder(context)
                .setSmallIcon(R.mipmap.ttu_icon)
                .setContentIntent(pendingIntent)
                .setContentTitle("Current Weather Information");

        //For checking if using gps location on default page
        LocationManager lm= (LocationManager) context.getSystemService(LOCATION_SERVICE);
        Boolean location_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        Boolean gps_enabled_pref = preferences.getBoolean("geolocation_enabled",false);//if geolocation is enabled in settings

        //if locationCheck is equal to string "Unknown" then user is on the main page
        if(locationCheck.equals("Unknown"))
            if(gps_enabled_pref && location_enabled){ //if location is enabled and the geolocation pref is set in the settings
                String gps_loc=pref1.getString("GPS_Location","Austin, TX");//default Austin, TX
                nbuilder.setContentText(gps_loc);
            }
            else{nbuilder.setContentText("Lubbock, TX");}
            //checks to see if temperature unit value is C and gets new temp for Celsius
        else{//else set the Text to location of page
            nbuilder.setContentText(locationCheck);
        }
        if( preftemp.equals("C")){
            oldtemp=FarenheitToCelsius(oldtemp);
            nbuilder.setContentInfo(String.valueOf(oldtemp) + "°" + preftemp + " " + oldcondition);
        }
        else {
            nbuilder.setContentInfo(String.valueOf(oldtemp) + "°" + preftemp + " " + oldcondition);
        }

        //for notification id
        //only current page location notification is built
        NotificationManager mNotifyManager=(NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotifyManager.notify(i,nbuilder.build());//i is also used as notification id
    }

    // Converts From Farenheit to Celsius
    private int FarenheitToCelsius(int fahrenheit) {
        return ((fahrenheit - 32) * 5 / 9);
    }
}