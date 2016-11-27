package com.example.kevin.TexasTechWeatherApp;

/**
 * Created by Stephen on 11/20/2016.
 */

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kevin.TexasTechWeatherApp.data.Channel;
import com.example.kevin.TexasTechWeatherApp.data.Condition;
import com.example.kevin.TexasTechWeatherApp.data.Forecast;
import com.example.kevin.TexasTechWeatherApp.data.Item;
import com.example.kevin.TexasTechWeatherApp.service.Notify;
import com.example.kevin.TexasTechWeatherApp.service.WeatherServiceCallback;
import com.example.kevin.TexasTechWeatherApp.service.YahooWeatherService;

import java.util.Calendar;

/**
 * Created by Stephen on 11/12/2016.
 * For every added location page
 */

public class NewPageNonGPS extends AppCompatActivity implements WeatherServiceCallback,OnGestureListener{

    private TextView temperatureTextView;
    private TextView conditionTextView;
    private TextView locationTextView;
    private GestureDetector detector;
    private static final int SWIPE_THRESHOLD = 100;
    private static final int SWIPE_VELOCITY_THRESHOLD = 100;

    private YahooWeatherService service;
    private ProgressDialog dialog;
    public Boolean notificationSet=false;

    public static SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_page);
        getSupportActionBar().setIcon(R.mipmap.ttu_icon);
        detector = new GestureDetector(this, this);
        temperatureTextView = (TextView) findViewById(R.id.temperatureTextView);
        conditionTextView = (TextView) findViewById(R.id.conditionTextView);
        locationTextView = (TextView) findViewById(R.id.locationTextView);


        SharedPreferences location_number = getSharedPreferences(getString(R.string.Location_Number), 0);
        SharedPreferences location_name = getSharedPreferences(getString(R.string.PREF_NAME), 0);

        service = new YahooWeatherService(this);
        dialog = new ProgressDialog(this);
        dialog.setMessage("Loading...");
        dialog.show();

        //if gps is enabled during non gps pages go to corresponding gps pages
        LocationManager lm = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (lm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Location Services Have Been Changed");
            builder.setMessage("GPS Has Been Enabled");
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialogInterface, int i) {
                    SharedPreferences location_number = getSharedPreferences("Location_Number", 0);
                    SharedPreferences.Editor editor = location_number.edit();
                    editor.putInt("location_number", 0);//reset page location back to zero
                    editor.apply();

                    SharedPreferences pref= getSharedPreferences(getString(R.string.GPS_Enabled),0);
                    Boolean gps_enabled= true;
                    SharedPreferences.Editor editor1= pref.edit();
                    editor1.putBoolean("GPS_Enabled",gps_enabled);
                    editor1.apply();
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);//go to gps main page
                    startActivity(intent);
                }
            });
            Dialog alertDialog = builder.create();
            alertDialog.setCanceledOnTouchOutside(false);
            alertDialog.show();
        }

        int i = location_number.getInt("location_number", 0);
        String locationCheck = location_name.getString("location_name" + i, "Lubbock, TX");
        service.refreshWeather(locationCheck);

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
                //location position passed to new activity
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
    @Override
    public void serviceSuccess(Channel channel) {
        dialog.hide();

        Item item = channel.getItem();
        String str = "drawable/back_image";

        int backimageId = getResources().getIdentifier(str + item.getCondition().getCode(), null, getPackageName());

        //for getting temperature unit string value
        preferences= PreferenceManager.getDefaultSharedPreferences(this);
        String preftemp= preferences.getString("temperature_unit","Null");

        //check what type of weather condition it is
        //apply new image
        RelativeLayout image= (RelativeLayout)findViewById(R.id.new_page);
        //for getting which image to set
        image.setBackgroundResource(backimageId);

        int temperature=item.getCondition().getTemperature();

        //checks to see if temperature unit value is C and gets new temp for Celsius
        if( preftemp.equals("C")){
            int newtemp=FarenheitToCelsius(temperature);
            temperatureTextView.setText(newtemp + "\u00B0" + "C"); //Already known to be celsius
        }

        else{
            temperatureTextView.setText(temperature + "\u00B0" + channel.getUnits().getTemperature());
        }
        conditionTextView.setText(item.getCondition().getDescription());
        locationTextView.setText(service.getLocation());
        saveWeekForecast(channel);
        saveTemp(channel);
    }

    @Override
    public void serviceFailure(Exception exception) {
        dialog.hide();
        onServiceFailure();
        Toast weather = Toast.makeText(this,"Current Weather Information could not be updated", Toast.LENGTH_SHORT);
        //for centering toast text
        TextView v = (TextView) weather.getView().findViewById(android.R.id.message);
        v.setGravity(Gravity.CENTER);
        weather.show();
    }


    //for if service failure happens we want previous values
    public void onServiceFailure(){
        //for getting all prev temp values
        SharedPreferences pref=getSharedPreferences(getString(R.string.Prev_Temp),0);
        SharedPreferences pref1=getSharedPreferences(getString(R.string.Prev_Code),0);
        SharedPreferences pref2=getSharedPreferences(getString(R.string.Prev_Condition),0);
        SharedPreferences oldlocation=getSharedPreferences(getString(R.string.Location_Number),0);
        SharedPreferences oldlocationname=getSharedPreferences(getString(R.string.PREF_NAME),0);

        //get all prev temp values
        int i=oldlocation.getInt("location_number",0);
        int oldtemp=pref.getInt("Prev_Temp"+i,0);
        int oldcode=pref1.getInt("Prev_Code"+i,0);
        String oldcondition=pref2.getString("Prev_Condition"+i,"Unknown");
        String oldname=oldlocationname.getString("location_name"+i,"Lubbock, TX");
        String str = "drawable/back_image";

        int backimageId = getResources().getIdentifier(str+oldcode, null, getPackageName());


        //for getting temperature unit string value
        preferences= PreferenceManager.getDefaultSharedPreferences(this);
        String preftemp= preferences.getString("temperature_unit","Null");
        //for getting which image to set and where
        RelativeLayout image= (RelativeLayout)findViewById(R.id.new_page);;
        image.setBackgroundResource(backimageId);//default image

        //checks to see if temperature unit value is C and gets new temp for Celsius
        if( preftemp.equals("C")){
            int newtemp=FarenheitToCelsius(oldtemp);
            temperatureTextView.setText(newtemp + "\u00B0" + "C"); //Already known to be celsius
        }

        else{
            temperatureTextView.setText(oldtemp + "\u00B0" + "F");//Already known to be Fahrenheit
        }

        conditionTextView.setText(oldcondition);
        locationTextView.setText(oldname);

    }

    ///for saving new values when service is successful
    public void saveTemp(Channel channel){


        Condition item = channel.getItem().getCondition();

        //for getting all prev temp values
        SharedPreferences pref=getSharedPreferences(getString(R.string.Prev_Temp),0);
        SharedPreferences pref1=getSharedPreferences(getString(R.string.Prev_Code),0);
        SharedPreferences pref2=getSharedPreferences(getString(R.string.Prev_Condition),0);
        SharedPreferences oldlocation=getSharedPreferences(getString(R.string.Location_Number),0);

        //get all prev temp values
        int i=oldlocation.getInt("location_number",0);
        int oldtemp=item.getTemperature();
        int oldcode=item.getCode();
        String oldcondition=item.getDescription();

        //be able to edit shared preference values
        SharedPreferences.Editor editor = pref.edit();
        SharedPreferences.Editor editor1 = pref1.edit();
        SharedPreferences.Editor editor2 = pref2.edit();
        //store all previous values from weather api
        editor.putInt("Prev_Temp"+i,oldtemp);
        editor1.putInt("Prev_Code"+i,oldcode);
        editor2.putString("Prev_Condition"+i,oldcondition);
        //apply all changes
        editor.apply();
        editor1.apply();
        editor2.apply();


    }

    public void saveWeekForecast(Channel channel){
        Forecast def=channel.getItem().getForecast();

        //for getting temperature unit string value
        preferences= PreferenceManager.getDefaultSharedPreferences(this);
        String preftemp= preferences.getString("temperature_unit","Null");

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
        SharedPreferences oldlocation=getSharedPreferences(getString(R.string.Location_Number),0);
        int i=oldlocation.getInt("location_number",0);

        //be able to edit shared preference values
        SharedPreferences.Editor ed1= high1.edit();
        SharedPreferences.Editor ed2 = high2.edit();
        SharedPreferences.Editor ed3 = high3.edit();
        //be able to edit shared preference values
        SharedPreferences.Editor ed4 = high4.edit();
        SharedPreferences.Editor ed5 = high5.edit();
        SharedPreferences.Editor ed6 = high6.edit();
        //be able to edit shared preference values
        SharedPreferences.Editor ed7 = high7.edit();
        SharedPreferences.Editor ed8 = low1.edit();
        SharedPreferences.Editor ed9 = low2.edit();
        //be able to edit shared preference values
        SharedPreferences.Editor ed10 = low3.edit();
        SharedPreferences.Editor ed11 = low4.edit();
        SharedPreferences.Editor ed12 = low5.edit();
        //be able to edit shared preference values
        SharedPreferences.Editor ed13 = low6.edit();
        SharedPreferences.Editor ed14 = low7.edit();

        //initate all days
        String day1;
        String day2;
        String day3;
        String day4;
        String day5;
        String day6;
        String day7;

        day1 = "Date: " + def.getDate() + "\n" + "Day: " + def.getDay() + "\nCondition: " + def.getDescription() + "\n";
        day2 = "Date: " + def.getDate2() + "\n" + "Day: " + def.getDay2() + "\nCondition: " + def.getDescription2() + "\n";
        day3 = "Date: " + def.getDate3() + "\n" + "Day: " + def.getDay3() + "\nCondition: " + def.getDescription3() + "\n";
        day4 = "Date: " + def.getDate4() + "\n" + "Day: " + def.getDay4() + "\nCondition: " + def.getDescription4() + "\n";
        day5 = "Date: " + def.getDate5() + "\n" + "Day: " + def.getDay5() + "\nCondition: " + def.getDescription5() + "\n";
        day6 = "Date: " + def.getDate6() + "\n" + "Day: " + def.getDay6() + "\nCondition: " + def.getDescription6() + "\n";
        day7 = "Date: " + def.getDate7() + "\n" + "Day: " + def.getDay7() + "\nCondition: " + def.getDescription7() + "\n";


        //store all previous highs and lows for on service failure when accessing forecast page
        ed1.putInt("High1"+i,def.getHigh());
        ed2.putInt("High2"+i,def.getHigh2());
        ed3.putInt("High3"+i,def.getHigh3());
        ed4.putInt("High4"+i,def.getHigh4());
        ed5.putInt("High5"+i,def.getHigh5());
        ed6.putInt("High6"+i,def.getHigh6());
        ed7.putInt("High7"+i,def.getHigh7());
        ed8.putInt("Low1"+i,def.getLow());
        ed9.putInt("Low2"+i,def.getLow2());
        ed10.putInt("Low3"+i,def.getLow3());
        ed11.putInt("Low4"+i,def.getLow4());
        ed12.putInt("Low5"+i,def.getLow5());
        ed13.putInt("Low6"+i,def.getLow6());
        ed14.putInt("Low7"+i,def.getLow7());

        //apply all stored highs and lows
        ed1.apply();
        ed2.apply();
        ed3.apply();
        ed4.apply();
        ed5.apply();
        ed6.apply();
        ed7.apply();
        ed8.apply();
        ed9.apply();
        ed10.apply();
        ed11.apply();
        ed12.apply();
        ed13.apply();
        ed14.apply();


        //get places for storage
        SharedPreferences pref=getSharedPreferences(getString(R.string.Forecast_Day1),0);
        SharedPreferences pref1=getSharedPreferences(getString(R.string.Forecast_Day2),0);
        SharedPreferences pref2=getSharedPreferences(getString(R.string.Forecast_Day3),0);
        SharedPreferences pref3=getSharedPreferences(getString(R.string.Forecast_Day4),0);
        SharedPreferences pref4=getSharedPreferences(getString(R.string.Forecast_Day5),0);
        SharedPreferences pref5=getSharedPreferences(getString(R.string.Forecast_Day6),0);
        SharedPreferences pref6=getSharedPreferences(getString(R.string.Forecast_Day7),0);

        //get editors
        SharedPreferences.Editor editor= pref.edit();
        SharedPreferences.Editor editor1= pref1.edit();
        SharedPreferences.Editor editor2= pref2.edit();
        SharedPreferences.Editor editor3= pref3.edit();
        SharedPreferences.Editor editor4= pref4.edit();
        SharedPreferences.Editor editor5= pref5.edit();
        SharedPreferences.Editor editor6= pref6.edit();

        //set all values
        editor.putString("Forecast_Day1"+i,day1);
        editor1.putString("Forecast_Day2"+i,day2);
        editor2.putString("Forecast_Day3"+i,day3);
        editor3.putString("Forecast_Day4"+i,day4);
        editor4.putString("Forecast_Day5"+i,day5);
        editor5.putString("Forecast_Day6"+i,day6);
        editor6.putString("Forecast_Day7"+i,day7);

        //apply all values
        editor.apply();
        editor1.apply();
        editor2.apply();
        editor3.apply();
        editor4.apply();
        editor5.apply();
        editor6.apply();
    }



    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return detector.onTouchEvent(event);
    }
    //all for implementing OnGestureListener
    @Override
    public boolean onDown(MotionEvent e) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSupportNavigateUp() {
        //reset i to 0
        SharedPreferences oldlocation=getSharedPreferences(getString(R.string.Location_Number),0);
        SharedPreferences.Editor editor= oldlocation.edit();
        //if gps is enabled during non gps pages go to corresponding gps pages
        LocationManager lm = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (lm.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            editor.putInt("location_number",0);//reset to location number to 0
            editor.apply();
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }
        else{
            editor.putInt("location_number",0);//reset to location number to 0
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

    @Override//no use for back button on any new pages
    public void onBackPressed()
    {
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
            if (Math.abs(diffX) > Math.abs(diffY)) {
                if (Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                    SharedPreferences loc_number = getSharedPreferences(getString(R.string.Location_Number), 0);
                    int i = loc_number.getInt("location_number", 0);
                    if (diffX > 0) {//swiped right
                        //used to access cache locations
                        i--;//swiping right decrements
                        SharedPreferences.Editor editor = loc_number.edit();
                        editor.putInt("location_number", i);
                        editor.apply();
                        if (i == 0) {//if going back to main activity page
                            //reset page location to 0
                            editor.putInt("location_number", 0);//put 0 in for page location
                            editor.apply();
                            //if gps is enabled during non gps pages go to corresponding gps pages
                            LocationManager lm = (LocationManager) getSystemService(LOCATION_SERVICE);
                            if (lm.isProviderEnabled(LocationManager.GPS_PROVIDER)){
                                Intent intent = new Intent(this, MainActivity.class);
                                startActivity(intent);
                            }
                            else{
                                Intent intent= new Intent(this,MainNonGPS.class);
                                startActivity(intent);
                            }
                        } else {//go to new page activity
                            //if gps is enabled during non gps pages go to corresponding gps pages
                            LocationManager lm = (LocationManager) getSystemService(LOCATION_SERVICE);
                            if (lm.isProviderEnabled(LocationManager.GPS_PROVIDER)){
                                Intent intent = new Intent(this, NewPage.class);
                                startActivity(intent);
                            }
                            else{
                                Intent intent= new Intent(this,NewPageNonGPS.class);
                                startActivity(intent);
                            }
                        }
                    } else {//swiped left
                        //used to access cache locations
                        SharedPreferences loc_name = getSharedPreferences(getString(R.string.PREF_NAME), 0);
                        String str=loc_name.getString("location_name"+(i+1),null);
                        if(str!=null) {
                            i++;//swiping left increments
                            //increase location position
                            SharedPreferences.Editor editor = loc_number.edit();
                            editor.putInt("location_number", i);
                            editor.apply();
                            //if gps is enabled during non gps pages go to corresponding gps pages
                            LocationManager lm = (LocationManager) getSystemService(LOCATION_SERVICE);
                            if (lm.isProviderEnabled(LocationManager.GPS_PROVIDER)){
                                Intent intent = new Intent(this, NewPage.class);
                                startActivity(intent);
                            }
                            else{
                                Intent intent= new Intent(this,NewPageNonGPS.class);
                                startActivity(intent);
                            }
                        }
                    }
                }
                result = true;
            }  else if (Math.abs(diffY) > SWIPE_THRESHOLD && Math.abs(velocityY) > SWIPE_VELOCITY_THRESHOLD){
                //swiped down refresh page
                if(diffY>0){
                    startActivity(getIntent());//refresh page

                }
                else{//swiped up go to forecast
                    Intent intent = new Intent(NewPageNonGPS.this, ForecastPage.class);//go to forecast page on swipe down
                    startActivity(intent);
                }
            } result = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
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
            Intent intentAlarm = new Intent(NewPageNonGPS.this, Notify.class);
            AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
            PendingIntent result = PendingIntent.getBroadcast(NewPageNonGPS.this, 0, intentAlarm, PendingIntent.FLAG_UPDATE_CURRENT);
            alarmManager.setRepeating(AlarmManager.RTC, calendar.getTimeInMillis(), 60000, result);//every minute update notification
            editor.putBoolean("Notification_Enabled",true);//update notification enabling
            editor.apply();
        }
        else{//cancel
            Toast.makeText(this, "Notification Cancelled", Toast.LENGTH_SHORT).show();
            Intent intentAlarm = new Intent(NewPageNonGPS.this, Notify.class);
            AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
            PendingIntent result = PendingIntent.getBroadcast(NewPageNonGPS.this, 0, intentAlarm, PendingIntent.FLAG_UPDATE_CURRENT);
            alarmManager.cancel(result);//every minute update notification
            editor.putBoolean("Notification_Enabled",false);//update notification enabling
            editor.apply();
        }
    }
}
