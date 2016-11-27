package com.example.kevin.TexasTechWeatherApp;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.GestureDetector.OnGestureListener;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kevin.TexasTechWeatherApp.service.Notify;

import java.util.Calendar;


/**
 * Created by Stephen on 11/26/2016.
 */

public class ForecastPage extends AppCompatActivity implements OnGestureListener, LocationListener{
    private TextView forecast;
    private static final int SWIPE_THRESHOLD = 100;
    private static final int SWIPE_VELOCITY_THRESHOLD = 100;
    public static SharedPreferences preferences;
    public Boolean notificationSet=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.forecast);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.mipmap.ttu_icon1);

        //get all textview places
        forecast=(TextView)findViewById(R.id.forecastView);

        //get and show forecast data
        getForecast();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.location:
                Intent intent = new Intent(this, Add_Location.class);
                startActivity(intent);
                return true;
            case R.id.settings:
                Intent intent1= new Intent(this,Settings.class);
                startActivity(intent1);
                return true;
            case R.id.notifications:
                startNotification();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    public void getForecast(){
        //get places for storage
        SharedPreferences pref=getSharedPreferences(getString(R.string.Forecast_Day1),0);
        SharedPreferences pref1=getSharedPreferences(getString(R.string.Forecast_Day2),0);
        SharedPreferences pref2=getSharedPreferences(getString(R.string.Forecast_Day3),0);
        SharedPreferences pref3=getSharedPreferences(getString(R.string.Forecast_Day4),0);
        SharedPreferences pref4=getSharedPreferences(getString(R.string.Forecast_Day5),0);
        SharedPreferences pref5=getSharedPreferences(getString(R.string.Forecast_Day6),0);
        SharedPreferences pref6=getSharedPreferences(getString(R.string.Forecast_Day7),0);
        SharedPreferences oldlocation=getSharedPreferences(getString(R.string.Location_Number),0);
        SharedPreferences pref7=getSharedPreferences(getString(R.string.Prev_Code),0);

        //get all preferences for old highs and lows]
        SharedPreferences high1=getSharedPreferences(getString(R.string.High1),0);
        SharedPreferences high2=getSharedPreferences(getString(R.string.High2),0);
        SharedPreferences high3=getSharedPreferences(getString(R.string.High3),0);
        SharedPreferences high4=getSharedPreferences(getString(R.string.High4),0);
        SharedPreferences high5=getSharedPreferences(getString(R.string.High5),0);
        SharedPreferences high6=getSharedPreferences(getString(R.string.High6),0);
        SharedPreferences high7=getSharedPreferences(getString(R.string.High7),0);

        SharedPreferences low1=getSharedPreferences(getString(R.string.Low1),0);
        SharedPreferences low2=getSharedPreferences(getString(R.string.Low2),0);
        SharedPreferences low3=getSharedPreferences(getString(R.string.Low3),0);
        SharedPreferences low4=getSharedPreferences(getString(R.string.Low4),0);
        SharedPreferences low5=getSharedPreferences(getString(R.string.Low5),0);
        SharedPreferences low6=getSharedPreferences(getString(R.string.Low6),0);
        SharedPreferences low7=getSharedPreferences(getString(R.string.Low7),0);

        int i=oldlocation.getInt("location_number",0);
        int prev_code=pref7.getInt("Prev_Code"+i,0);
        String day1=pref.getString("Forecast_Day1"+i,"Unknown\n");
        String day2=pref1.getString("Forecast_Day2"+i,"Unknown\n");
        String day3=pref2.getString("Forecast_Day3"+i,"Unknown\n");
        String day4=pref3.getString("Forecast_Day4"+i,"Unknown\n");
        String day5=pref4.getString("Forecast_Day5"+i,"Unknown\n");
        String day6=pref5.getString("Forecast_Day6"+i,"Unknown\n");
        String day7=pref6.getString("Forecast_Day7"+i,"Unknown\n");

        int h1=high1.getInt("High1"+i,0);
        int h2=high2.getInt("High2"+i,0);
        int h3=high3.getInt("High3"+i,0);
        int h4=high4.getInt("High4"+i,0);
        int h5=high5.getInt("High5"+i,0);
        int h6=high6.getInt("High6"+i,0);
        int h7=high7.getInt("High7"+i,0);
        int l1=low1.getInt("Low1"+i,0);
        int l2=low2.getInt("Low2"+i,0);
        int l3=low3.getInt("Low3"+i,0);
        int l4=low4.getInt("Low4"+i,0);
        int l5=low5.getInt("Low5"+i,0);
        int l6=low6.getInt("Low6"+i,0);
        int l7=low7.getInt("Low7"+i,0);

        //for getting temperature unit string value
        preferences= PreferenceManager.getDefaultSharedPreferences(this);
        String preftemp= preferences.getString("temperature_unit","Null");
        if(preftemp.equals("C")){//convert all highs and lows to celsius
            //all week forecast weather with celsius highs and lows
            day1=day1+"High: "+FarenheitToCelsius(h1)+ "\u00B0" + "C\n"+"Low: "+ FarenheitToCelsius(l1)+ "\u00B0" + "C"+"\n\n\n";
            day2=day2+"High: "+FarenheitToCelsius(h2)+ "\u00B0" + "C\n"+"Low: "+ FarenheitToCelsius(l2)+ "\u00B0" + "C"+"\n\n\n";
            day3=day3+"High: "+FarenheitToCelsius(h3)+ "\u00B0" + "C\n"+"Low: "+ FarenheitToCelsius(l3)+ "\u00B0" + "C"+"\n\n\n";
            day4=day4+"High: "+FarenheitToCelsius(h4)+ "\u00B0" + "C\n"+"Low: "+ FarenheitToCelsius(l4)+ "\u00B0" + "C"+"\n\n\n";
            day5=day5+"High: "+FarenheitToCelsius(h5)+ "\u00B0" + "C\n"+"Low: "+ FarenheitToCelsius(l5)+ "\u00B0" + "C"+"\n\n\n";
            day6=day6+"High: "+FarenheitToCelsius(h6)+ "\u00B0" + "C\n"+"Low: "+ FarenheitToCelsius(l6)+ "\u00B0" + "C"+"\n\n\n";
            day7=day7+"High: "+FarenheitToCelsius(h7)+ "\u00B0" + "C\n"+"Low: "+ FarenheitToCelsius(l7)+ "\u00B0" + "C"+"\n\n\n\n\n";

        }
        else{//all week forecast weather with farenheit highs and lows
            day1=day1+"High: "+h1+ "\u00B0" + "F\n"+"Low: "+ l1+ "\u00B0" + "F"+"\n\n\n";
            day2=day2+"High: "+h2+ "\u00B0" + "F\n"+"Low: "+ l2+ "\u00B0" + "F"+"\n\n\n";
            day3=day3+"High: "+h3+ "\u00B0" + "F\n"+"Low: "+ l3+ "\u00B0" + "F"+"\n\n\n";
            day4=day4+"High: "+h4+ "\u00B0" + "F\n"+"Low: "+ l4+ "\u00B0" + "F"+"\n\n\n";
            day5=day5+"High: "+h5+ "\u00B0" + "F\n"+"Low: "+ l5+ "\u00B0" + "F"+"\n\n\n";
            day6=day6+"High: "+h6+ "\u00B0" + "F\n"+"Low: "+ l6+ "\u00B0" + "F"+"\n\n\n";
            day7=day7+"High: "+h7+ "\u00B0" + "F\n"+"Low: "+ l7+ "\u00B0" + "F"+"\n\n\n\n\n";
        }



        //For checking if using gps location on default page
        preferences = PreferenceManager.getDefaultSharedPreferences(this);

        Boolean gps_enabled_pref = preferences.getBoolean("geolocation_enabled",false);
        LocationManager lm = (LocationManager) getSystemService(LOCATION_SERVICE);//to check gps
        Boolean location_enabled=lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        String str;
        if(i==0) {//if on main page forecast
            if (gps_enabled_pref&& location_enabled) {//if location is enabled and gps_pref is on in settings
                str = "drawable/back_image" + prev_code;//if gps is enabled
            } else {//just use normal background from main page
                str = "drawable/ttu_main";
            }
        }
        else{
            str="drawable/back_image"+ prev_code;//always use back image for new page forecasts
        }

        int backimageId = getResources().getIdentifier(str, null, getPackageName());
        //for getting which image to set and where
        RelativeLayout image= (RelativeLayout)findViewById(R.id.forecast_page);
        image.setBackgroundResource(backimageId);//default image


        forecast.setText(day1+day2+day3+day4+day5+day6+day7);
    }

    public void startNotification() {
        SharedPreferences oldlocation=getSharedPreferences(getString(R.string.Location_Number),0);
        int i=oldlocation.getInt("location_number",0);
        preferences=getSharedPreferences(getString(R.string.Notification_Enabled),0);
        notificationSet=preferences.getBoolean("Notification_Enabled",false);
        SharedPreferences.Editor editor= preferences.edit();

        if(!notificationSet) {//set notification
            Toast.makeText(this, "Notification Started", Toast.LENGTH_SHORT).show();
            Calendar calendar = Calendar.getInstance();
            Intent intentAlarm = new Intent(ForecastPage.this, Notify.class);
            AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
            PendingIntent result = PendingIntent.getBroadcast(ForecastPage.this, 0, intentAlarm, PendingIntent.FLAG_UPDATE_CURRENT);//use unique id for each
            alarmManager.setRepeating(AlarmManager.RTC, calendar.getTimeInMillis(), 60000, result);//every minute update notification
            editor.putBoolean("Notification_Enabled",true);//update notification enabling
            editor.apply();
        }
        else{//cancel
            Toast.makeText(this, "Notification Cancelled", Toast.LENGTH_SHORT).show();
            Intent intentAlarm = new Intent(ForecastPage.this, Notify.class);
            AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
            PendingIntent result = PendingIntent.getBroadcast(ForecastPage.this, 0, intentAlarm, PendingIntent.FLAG_UPDATE_CURRENT);
            alarmManager.cancel(result);//every minute update notification
            editor.putBoolean("Notification_Enabled",false);//update notification enabling
            editor.apply();
        }
    }

    @Override
    public boolean onDown(MotionEvent e) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {

    }

    //The only one we need to use for swiping
    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        boolean result = false;
        try {
            float diffY = e2.getY() - e1.getY();
            float diffX = e2.getX() - e1.getX();
            LocationManager lm = (LocationManager) getSystemService(LOCATION_SERVICE);
            if (Math.abs(diffX) > Math.abs(diffY)) {
                if (Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                    if (diffX > 0) {
                        /*don't need swipe right gesture action on main activity//
                        Toast.makeText(this, "You have swiped right", Toast.LENGTH_SHORT).show();*/
                    } else {//on swipe left
                    }
                }
                result = true;
            }
            else if (Math.abs(diffY) > SWIPE_THRESHOLD && Math.abs(velocityY) > SWIPE_VELOCITY_THRESHOLD){
                //swiped down
                if(diffY>0){
                    startActivity(getIntent());//refresh page

                }
                else{//swiped up used to refresh page
                    if(lm.isProviderEnabled(LocationManager.GPS_PROVIDER)){
                        Intent intent = new Intent(ForecastPage.this, MainActivity.class);//go to forecast page on swipe down
                        startActivity(intent);
                    }
                    else{
                        Intent intent= new Intent(ForecastPage.this,MainNonGPS.class);
                        startActivity(intent);
                    }
                }
            }result=true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    //if back button pressed go back to main page automatically
    @Override
    public void onBackPressed()
    {
        SharedPreferences pref=getSharedPreferences(getString(R.string.Location_Number),0);
        int i= pref.getInt("location_number",0);
        LocationManager lm = (LocationManager) getSystemService(LOCATION_SERVICE);//to check gps

        if(i==0) {//if on home page go back to home page
            //if gps is enabled during non gps pages go to corresponding gps pages
            if (lm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
            } else {
                Intent intent = new Intent(this, MainNonGPS.class);
                startActivity(intent);
            }
        }
        else{//go to new page
            if (lm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                Intent intent = new Intent(this, NewPage.class);
                startActivity(intent);
            } else {
                Intent intent = new Intent(this, NewPageNonGPS.class);
                startActivity(intent);
            }
        }
    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public boolean onSupportNavigateUp() {
        //reset i to 0
        SharedPreferences oldlocation=getSharedPreferences(getString(R.string.Location_Number),0);
        SharedPreferences.Editor editor= oldlocation.edit();
        //if gps is enabled during non gps pages go to corresponding gps pages
        LocationManager lm = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (lm.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            editor.putInt("location_number",0);//reset to 0
            editor.apply();
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }
        else{
            editor.putInt("location_number",0);//reset to 0
            editor.apply();
            Intent intent= new Intent(this,MainNonGPS.class);
            startActivity(intent);
        }
        return true;
    }

    // Converts From Farenheit to Celsius
    private int FarenheitToCelsius(int fahrenheit) {
        return ((fahrenheit - 32) * 5 / 9);
    }
}
