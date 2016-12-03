package com.example.kevin.TexasTechWeatherApp.data;

import org.json.JSONObject;

/**
 * Created by Stephen on 12/2/2016.
 */

public class Wind implements JSONPopulator{
    private int windchill;
    private int windspeed;

    public int getWindchill() {
        return windchill;
    }

    public int getWindspeed() {
        return windspeed;
    }

    @Override
    public void populate(JSONObject data) {
        windchill=data.optInt("chill");
        windspeed=data.optInt("speed");
    }
}
