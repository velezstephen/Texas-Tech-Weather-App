package com.example.kevin.TexasTechWeatherApp.data;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by kevin on 10/12/2016.
 */

public class Units implements JSONPopulator {
    private String temperature;

    public String getTemperature() {
        return temperature;
    }

    @Override
    public void populate(JSONObject data) {
        temperature = data.optString("temperature");
    }
}
