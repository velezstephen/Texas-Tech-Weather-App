package com.example.kevin.TexasTechWeatherApp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kevin.TexasTechWeatherApp.data.Channel;
import com.example.kevin.TexasTechWeatherApp.data.Condition;
import com.example.kevin.TexasTechWeatherApp.data.Item;
import com.example.kevin.TexasTechWeatherApp.service.WeatherServiceCallback;
import com.example.kevin.TexasTechWeatherApp.service.YahooWeatherService;

/**
 * Created by Stephen on 11/12/2016.
 * For every added location page
 */

public class NewPage extends AppCompatActivity implements WeatherServiceCallback,OnGestureListener{

    private TextView temperatureTextView;
    private TextView conditionTextView;
    private TextView locationTextView;
    private GestureDetector detector;
    private static final int SWIPE_THRESHOLD = 100;
    private static final int SWIPE_VELOCITY_THRESHOLD = 100;

    private YahooWeatherService service;
    private ProgressDialog dialog;

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
        saveTemp(channel);
    }

    @Override
    public void serviceFailure(Exception exception) {
        dialog.hide();
        onServiceFailure();
        Toast.makeText(this,"Current Weather Information could not be updated", Toast.LENGTH_LONG).show();
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
        //This method is called when the up button is pressed. Just the pop back stack.
        Intent intent= new Intent(this, MainActivity.class);
        startActivity(intent);
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
                            Intent intent = new Intent(NewPage.this, MainActivity.class);
                            startActivity(intent);
                        } else {//go to new page activity
                            Intent intent = new Intent(NewPage.this, NewPage.class);
                            startActivity(intent);
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
                            Intent intent = new Intent(NewPage.this, NewPage.class);
                            startActivity(intent);
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
}
