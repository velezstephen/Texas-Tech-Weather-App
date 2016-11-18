package com.example.kevin.TexasTechWeatherApp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;
import android.view.View;
import android.widget.Button;
import android.view.View.OnClickListener;
import android.widget.Toast;


/**
 * Created by Stephen on 11/12/2016.
 */

public class Add_Location extends AppCompatActivity{

    private EditText inputEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_location);


        inputEditText = (EditText) findViewById(R.id.inputEditText);

        Button searchButton = (Button) findViewById(R.id.searchButton);
        searchButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                String str = inputEditText.getText().toString();
                if (str.length() != 0) {//if something is entered in edittext
                    //get initial position
                    //increment location position
                    SharedPreferences loc_number = getSharedPreferences(getString(R.string.Location_Number), 0);
                    int i= loc_number.getInt("location_number",0);
                    i++;
                    SharedPreferences.Editor editor = loc_number.edit();
                    editor.putInt("location_number", i);
                    editor.apply();
                    //local storage of each added location name
                    SharedPreferences locations = getSharedPreferences(getString(R.string.PREF_NAME), 0);
                    SharedPreferences.Editor editor1 = locations.edit();
                    editor1.putString("location_name"+i , str);
                    editor1.apply();
                    Intent newintent =new Intent(Add_Location.this, NewPage.class);
                    startActivity(newintent);
                } else {
                    Toast.makeText(Add_Location.this, "Enter Your Location", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    //if back  button is pressed go back to main page and reset page location
    @Override
    public void onBackPressed()
    {

        SharedPreferences loc_number = getSharedPreferences(getString(R.string.Location_Number), 0);
        int i= loc_number.getInt("location_number",0);
        SharedPreferences.Editor editor = loc_number.edit();
        editor.putInt("location_number", 0);//put 0 in for page location
        editor.apply();
        Intent intent= new Intent(this,MainActivity.class);
        startActivity(intent);
    }

}
