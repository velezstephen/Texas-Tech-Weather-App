package com.example.kevin.TexasTechWeatherApp.data;

import org.json.JSONObject;

/**
 * Created by Stephen on 12/2/2016.
 * Used for getting sunrise and sunset
 */

public class Astronomy implements JSONPopulator {
    private String sunrise;
    private String sunset;

    public String getSunset() {
        return sunset;
    }

    public String getSunrise() {
        return sunrise;
    }

    @Override
    public void populate(JSONObject data) {

        sunrise= data.optString("sunrise");
        sunset=data.optString("sunset");

    }
}
