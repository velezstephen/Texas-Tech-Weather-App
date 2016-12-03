package com.example.kevin.TexasTechWeatherApp.data;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by kevin on 10/12/2016.
 */

public class Channel implements JSONPopulator {
    private Item item;
    private Units units;
    private Atmosphere atmosphere;
    private Astronomy astronomy;
    private Wind wind;

    public Item getItem() {
        return item;
    }

    public Units getUnits() {
        return units;
    }

    public Atmosphere getAtmosphere() {
        return atmosphere;
    }

    public Astronomy getAstronomy() {
        return astronomy;
    }

    public Wind getWind() {
        return wind;
    }

    @Override
    public void populate(JSONObject data) {

        units = new Units();
        units.populate(data.optJSONObject("units"));

        item = new Item();
        item.populate(data.optJSONObject("item"));

        atmosphere= new Atmosphere();
        atmosphere.populate(data.optJSONObject("atmosphere"));

        astronomy=new Astronomy();
        astronomy.populate(data.optJSONObject("astronomy"));

        wind= new Wind();
        wind.populate(data.optJSONObject("wind"));
    }

}
