package com.example.kevin.TexasTechWeatherApp.data;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by kevin on 10/12/2016.
 */

public class Item implements JSONPopulator{
    private Condition condition;
    private Forecast forecast;

    public Condition getCondition() {
        return condition;
    }
    public Forecast getForecast() {return forecast;}

    @Override
    public void populate(JSONObject data) {
        condition = new Condition();
        condition.populate(data.optJSONObject("condition"));
        forecast= new Forecast();
        forecast.populate(data.optJSONArray("forecast"));
    }


}
