package com.example.kevin.TexasTechWeatherApp.data;

import org.json.JSONObject;

/**
 * Created by Stephen on 12/2/2016.
 * This is used to get atmosphere readings such as humidity and visibility
 */

public class Atmosphere implements JSONPopulator{
    private int humidity;
    private int visibility;

    public int getHumidity() {
        return humidity;
    }

    public int getVisibility() {
        return visibility;
    }

    @Override
    public void populate(JSONObject data) {

        humidity= data.optInt("humidity");
        visibility=data.optInt("visibility");

    }
}
