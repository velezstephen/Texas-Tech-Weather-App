package com.example.kevin.TexasTechWeatherApp.data;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by kevin on 10/12/2016.
 */

public class Channel implements JSONPopulator {
    private Item item;
    private Units units;

    public Item getItem() {
        return item;
    }

    public Units getUnits() {
        return units;
    }

    @Override
    public void populate(JSONObject data) {

        units = new Units();
        units.populate(data.optJSONObject("units"));

        item = new Item();
        item.populate(data.optJSONObject("item"));
    }

    @Override
    public JSONObject toJSON() {

        JSONObject data = new JSONObject();

        try {
            data.put("units", units.toJSON());
            data.put("item", item.toJSON());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return data;
    }
}
