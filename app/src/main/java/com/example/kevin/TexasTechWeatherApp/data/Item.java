package com.example.kevin.TexasTechWeatherApp.data;

import org.json.JSONObject;

/**
 * Created by kevin on 10/12/2016.
 */

public class Item implements JSONPopulator {
    private Condition condition;

    public Condition getCondition() {
        return condition;
    }

    @Override
    public void populate(JSONObject data) {
        condition = new Condition();
        condition.populate(data.optJSONObject("condition"));
    }
}
