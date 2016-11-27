package com.example.kevin.TexasTechWeatherApp.data;

/**
 * Created by Stephen on 11/20/2016.
 */
import org.json.JSONException;
import org.json.JSONObject;

public class LocationResult implements JSONPopulator {
    private String address;

    public String getAddress() {
        String newaddress;
        String newaddress1;
        newaddress=address.substring(address.indexOf(",")+1);//first want to get rid of local home addresses
        newaddress1=newaddress.substring(1,newaddress.indexOf(",")+4); //start at 1 to get rid of extra space
        return newaddress1;//returns just the city and state
    }

    @Override
    public void populate(JSONObject data) {
        address = data.optString("formatted_address");
    }

}
