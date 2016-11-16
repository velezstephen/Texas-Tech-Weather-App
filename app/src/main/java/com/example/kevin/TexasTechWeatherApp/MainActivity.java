package com.example.kevin.TexasTechWeatherApp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.view.Menu;

import com.example.kevin.TexasTechWeatherApp.data.Channel;
import com.example.kevin.TexasTechWeatherApp.data.Condition;
import com.example.kevin.TexasTechWeatherApp.data.Item;
import com.example.kevin.TexasTechWeatherApp.service.WeatherServiceCallback;
import com.example.kevin.TexasTechWeatherApp.service.YahooWeatherService;

public class MainActivity extends AppCompatActivity implements WeatherServiceCallback,OnGestureListener{

    private TextView temperatureTextView;
    private TextView conditionTextView;
    private TextView locationTextView;
    private GestureDetector detector;
    private static final int SWIPE_THRESHOLD = 100;
    private static final int SWIPE_VELOCITY_THRESHOLD = 100;

    private YahooWeatherService service;
    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.mipmap.ttu_icon);
        detector = new GestureDetector(this,this);
        temperatureTextView = (TextView) findViewById(R.id.temperatureTextView);
        conditionTextView = (TextView) findViewById(R.id.conditionTextView);
        locationTextView = (TextView) findViewById(R.id.locationTextView);


        service = new YahooWeatherService(this);
        dialog = new ProgressDialog(this);
        dialog.setMessage("Loading...");
        dialog.show();

        service.refreshWeather("Lubbock, Texas");

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
        String str = "drawable/ttu_main";

        int backimageId = getResources().getIdentifier(str, null, getPackageName());


        //check what type of weather condition it is
        //apply new image
        RelativeLayout image= (RelativeLayout)findViewById(R.id.activity_main);

        //for getting which image to set
        image.setBackgroundResource(backimageId);//default image

        temperatureTextView.setText(item.getCondition().getTemperature() + "\u00B0" + channel.getUnits().getTemperature());
        conditionTextView.setText(item.getCondition().getDescription());
        locationTextView.setText(service.getLocation());
    }

    @Override
    public void serviceFailure(Exception exception) {
        dialog.hide();
        Toast.makeText(this, exception.getMessage(), Toast.LENGTH_LONG).show();
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
                            editor.putInt("location_numbers", i);
                            editor.commit();
                            if(str != null) {//if have locations go to new page
                                i = 1;//position assigned to second page
                                editor.putInt("location_number", i);
                                editor.commit();
                                Intent intent =new Intent(MainActivity.this,NewPage.class);
                                startActivity(intent);
                            }
                        }
                        else {
                            i = 1;
                            editor.putInt("location_number", i);
                            editor.commit();
                            Intent intent = new Intent(MainActivity.this, NewPage.class);
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