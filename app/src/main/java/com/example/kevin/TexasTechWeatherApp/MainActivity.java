package com.example.kevin.TexasTechWeatherApp;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.view.Menu;

import com.example.kevin.TexasTechWeatherApp.data.Channel;
import com.example.kevin.TexasTechWeatherApp.data.Condition;
import com.example.kevin.TexasTechWeatherApp.data.Item;
import com .example.kevin.TexasTechWeatherApp.data.LocationResult;
import com.example.kevin.TexasTechWeatherApp.service.GPSListener;
import com.example.kevin.TexasTechWeatherApp.service.GPS;
import com.example.kevin.TexasTechWeatherApp.service.WeatherServiceCallback;
import com.example.kevin.TexasTechWeatherApp.service.YahooWeatherService;



public class MainActivity extends AppCompatActivity implements WeatherServiceCallback, OnGestureListener, LocationListener,GPSListener {

    private TextView temperatureTextView;
    private TextView conditionTextView;
    private TextView locationTextView;
    private GestureDetector detector;
    private static final int SWIPE_THRESHOLD = 100;
    private static final int SWIPE_VELOCITY_THRESHOLD = 100;

    public static SharedPreferences preferences;
    private LocationManager locationManager;//for GPS

    private YahooWeatherService service;
    private GPS geocodingService;
    private ProgressDialog dialog;

    @TargetApi(23)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.mipmap.ttu_icon);
        detector = new GestureDetector(this, this);
        temperatureTextView = (TextView) findViewById(R.id.temperatureTextView);
        conditionTextView = (TextView) findViewById(R.id.conditionTextView);
        locationTextView = (TextView) findViewById(R.id.locationTextView);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        geocodingService= new GPS(this);

        //checking location permission
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION
            }, 10);//set code to 10 which is used in onRequestPermissionResult
            return;
        }
        else {
            locationManager.requestLocationUpdates("gps", 30000, 1000, this);//every 30 seconds update or every 1000 meters difference from last location
        }


        //for getting yahoo weather information and setting up a loading alert dialog
        service = new YahooWeatherService(this);
        dialog = new ProgressDialog(this);
        dialog.setMessage("Loading...");
        dialog.show();

        preferences =PreferenceManager.getDefaultSharedPreferences(this);
        Boolean gps_enabled_pref = preferences.getBoolean("geolocation_enabled",false);

        if (gps_enabled_pref){
            preferences=getSharedPreferences(getString(R.string.GPS),0);
            String gps_loc=preferences.getString("GPS_Location","Austin, TX");//default Austin, TX
            service.refreshWeather(gps_loc);//pass Austin as default for both
            //need to set default data

        }
        else{
            service.refreshWeather("Lubock, TX");
            //need to set default data
        }
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
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    @Override
    public void serviceSuccess(Channel channel) {
        dialog.hide();

        Item item = channel.getItem();
        //For checking if using gps location on default page
        preferences =PreferenceManager.getDefaultSharedPreferences(this);
        Boolean gps_enabled_pref = preferences.getBoolean("geolocation_enabled",false);
        String str=null;
        if(gps_enabled_pref){
            str="drawable/back_image"+item.getCondition().getCode();
        }
        else{
            str = "drawable/ttu_main";
        }

        int backimageId = getResources().getIdentifier(str, null, getPackageName());

        //for getting temperature unit string value
        preferences= PreferenceManager.getDefaultSharedPreferences(this);
        String preftemp= preferences.getString("temperature_unit","Null");
        //for getting which image to set and where
        RelativeLayout image= (RelativeLayout)findViewById(R.id.activity_main);
        image.setBackgroundResource(backimageId);//default image

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
        SharedPreferences pref2=getSharedPreferences(getString(R.string.Prev_Condition),0);
        SharedPreferences oldlocation=getSharedPreferences(getString(R.string.Location_Number),0);
        SharedPreferences oldlocationname=getSharedPreferences(getString(R.string.PREF_NAME),0);

        //get all prev temp values
        int i=oldlocation.getInt("location_number",0);
        int oldtemp=pref.getInt("Prev_Temp"+i,0);
        String oldcondition=pref2.getString("Prev_Condition"+i,"Unknown");
        String oldname=oldlocationname.getString("location_name"+i,"Lubbock, TX");
        String str = "drawable/ttu_main";

        int backimageId = getResources().getIdentifier(str, null, getPackageName());


        //for getting temperature unit string value
        preferences= PreferenceManager.getDefaultSharedPreferences(this);
        String preftemp= preferences.getString("temperature_unit","Null");
        //for getting which image to set and where
        RelativeLayout image= (RelativeLayout)findViewById(R.id.activity_main);
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
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return false;
    }

    //no use for the back button in our main page
    @Override
    public void onBackPressed()
    {
    }

    @Override
    public void onLongPress(MotionEvent e) {

    }

    // Converts From Farenheit to Celsius
    private int FarenheitToCelsius(int fahrenheit) {
        return ((fahrenheit - 32) * 5 / 9);
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
                    if (diffX > 0) {
                        /*don't need swipe right gesture action on main activity//
                        Toast.makeText(this, "You have swiped right", Toast.LENGTH_SHORT).show();*/
                    } else {//on swipe left
                        SharedPreferences loc_number = getSharedPreferences(getString(R.string.Location_Number), 0);//get loc_number and use operating mode 0
                        SharedPreferences loc_name = getSharedPreferences(getString(R.string.PREF_NAME),0);//get location name and use operating mode 0
                        SharedPreferences.Editor editor = loc_number.edit();
                        String str= loc_name.getString("location_name1",null);//for second location
                        int i=loc_number.getInt("location_number",0);
                        if(i<=0){//if been no locations or back to main page
                            i=0;
                            editor.putInt("location_number", i);
                            editor.apply();
                            if(str != null) {//if have locations go to new page
                                i = 1;//position assigned to second page
                                editor.putInt("location_number", i);
                                editor.apply();
                                //if gps is enabled during non gps pages go to corresponding gps pages
                                LocationManager lm = (LocationManager) getSystemService(LOCATION_SERVICE);
                                if (lm.isProviderEnabled(LocationManager.GPS_PROVIDER)){
                                    Intent intent = new Intent(MainActivity.this, NewPage.class);
                                    startActivity(intent);
                                }
                                else{
                                    Intent intent= new Intent(MainActivity.this,NewPageNonGPS.class);
                                    startActivity(intent);
                                }
                            }
                        }
                        else {
                            i = 1;
                            editor.putInt("location_number", i);
                            editor.apply();
                            //if gps is enabled during non gps pages go to corresponding gps pages
                            LocationManager lm = (LocationManager) getSystemService(LOCATION_SERVICE);
                            if (lm.isProviderEnabled(LocationManager.GPS_PROVIDER)){
                                Intent intent = new Intent(MainActivity.this, NewPage.class);
                                startActivity(intent);
                            }
                            else{
                                Intent intent= new Intent(MainActivity.this,NewPageNonGPS.class);
                                startActivity(intent);
                            }
                        }
                    }
                }
                result = true;
            } else result = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    //for GPS and implementation of Location Listener

    //when gps location is changed
    @Override
    public void onLocationChanged(Location location) {
        if(location!= null) {
            geocodingService.refreshLocation(location);
        }

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    //when gps is enabled
    @Override
    public void onProviderEnabled(String provider) {
    }
    //when gps is disabled
    @Override
    public void onProviderDisabled(String provider) {
// Get Location Manager and check for GPS location services
        LocationManager lm = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (!lm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            // Build the alert dialog
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Location Services Not Active");
            builder.setMessage("Please enable Your GPS");
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialogInterface, int i) {
                    // Show location settings when the user acknowledges the alert dialog
                    SharedPreferences pref= getSharedPreferences(getString(R.string.GPS_Enabled),0);
                    Boolean gps_enabled= true;
                    SharedPreferences.Editor editor= pref.edit();
                    editor.putBoolean("GPS_Enabled",gps_enabled);
                    editor.apply();
                    Intent intent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(intent);
                }
            });
            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialogInterface, int i) {
                            // go to non gps main activity
                            SharedPreferences pref= getSharedPreferences(getString(R.string.GPS_Enabled),0);
                            Boolean gps_enabled= false;// gps is not enabled
                            SharedPreferences.Editor editor= pref.edit();
                            editor.putBoolean("GPS_Enabled",gps_enabled);
                            editor.apply();
                            Intent intent = new Intent(getApplicationContext(), MainNonGPS.class);
                            startActivity(intent);
                        }
                    }
            );
            Dialog alertDialog = builder.create();
            alertDialog.setCanceledOnTouchOutside(false);
            alertDialog.show();

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch(requestCode){
            case 10:
            if(grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {//permission granted
                //initialize gps
                locationManager.requestLocationUpdates("gps",30000, 1000, this); //every 30 seconds update or 1000 meters update
                startActivity(getIntent());//refresh page for when user accepts
            }
                else{//go to non gps main page
                Intent intent= new Intent(this,MainNonGPS.class);
                startActivity(intent);
            }

        }
    }

    @Override
    public void geocodeSuccess(LocationResult location) {
        /* For Testing GPS locations when they succeed
        Toast gps_success = Toast.makeText(this,location.getAddress(), Toast.LENGTH_SHORT);
        //for centering toast text
        TextView v = (TextView) gps_success.getView().findViewById(android.R.id.message);
        v.setGravity(Gravity.CENTER);
        gps_success.show();
        */


        //Every successful time for gps the location is saved
        SharedPreferences gpslocation=getSharedPreferences(getString(R.string.GPS),0);

        //get all prev temp values
        String gps_location=location.getAddress();//current successful location is saved
        //be able to edit shared preference values
        SharedPreferences.Editor editor = gpslocation.edit();
        //store new gps location to shared prefs
        editor.putString("GPS_Location",gps_location);//update saved gps location
        //apply all changes
        editor.apply();
    }

    @Override
    public void geocodeFailure(Exception exception) {
        // GeoCoding failed, show toast
        Toast gps_failure = Toast.makeText(this,"GPS Information could not be updated", Toast.LENGTH_SHORT);
        //for centering toast text
        TextView v = (TextView) gps_failure.getView().findViewById(android.R.id.message);
        v.setGravity(Gravity.CENTER);
        gps_failure.show();
    }



}