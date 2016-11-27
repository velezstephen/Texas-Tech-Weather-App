package com.example.kevin.TexasTechWeatherApp.data;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Stephen on 11/26/2016.
 */

public class Forecast{

    JSONObject dataday1;
    JSONObject dataday2;
    JSONObject dataday3;
    JSONObject dataday4;
    JSONObject dataday5;
    JSONObject dataday6;
    JSONObject dataday7;
    
    //for day 1
    private String date;
    private String day;
    private int high;
    private int low;
    private String description;

    //for day 2
    private String date2;
    private String day2;
    private int high2;
    private int low2;
    private String description2;

    //for day 3
    private String date3;
    private String day3;
    private int high3;
    private int low3;
    private String description3;

    //for day 4
    private String date4;
    private String day4;
    private int high4;
    private int low4;
    private String description4;

    //for day 5
    private String date5;
    private String day5;
    private int high5;
    private int low5;
    private String description5;

    //for day 6
    private String date6;
    private String day6;
    private int high6;
    private int low6;
    private String description6;

    //for day 7
    private String date7;
    private String day7;
    private int high7;
    private int low7;
    private String description7;

    //all the getters for all nformation on each day

    public String getDate() {
        return date;
    }
    public String getDay(){
        return day;
    }

    public int getHigh(){
        return high;
    }

    public int getLow(){
        return low;
    }

    public String getDescription() {
        return description;
    }

    public String getDate2() {
        return date2;
    }

    public String getDay2() {
        return day2;
    }

    public int getHigh2() {
        return high2;
    }

    public int getLow2() {
        return low2;
    }

    public String getDescription2() {
        return description2;
    }

    public String getDate3() {
        return date3;
    }

    public String getDay3() {
        return day3;
    }

    public int getHigh3() {
        return high3;
    }

    public int getLow3() {
        return low3;
    }

    public String getDescription3() {
        return description3;
    }

    public String getDate4() {
        return date4;
    }

    public String getDay4() {
        return day4;
    }

    public int getHigh4() {
        return high4;
    }

    public int getLow4() {
        return low4;
    }

    public String getDescription4() {
        return description4;
    }

    public String getDate5() {
        return date5;
    }

    public String getDay5() {
        return day5;
    }

    public int getHigh5() {
        return high5;
    }

    public int getLow5() {
        return low5;
    }

    public String getDescription5() {
        return description5;
    }

    public String getDate6() {
        return date6;
    }

    public String getDay6() {
        return day6;
    }

    public int getHigh6() {
        return high6;
    }

    public int getLow6() {
        return low6;
    }

    public String getDescription6() {
        return description6;
    }

    public String getDate7() {
        return date7;
    }

    public String getDay7() {
        return day7;
    }

    public int getHigh7() {
        return high7;
    }

    public int getLow7() {
        return low7;
    }

    public String getDescription7() {
        return description7;
    }

    public void populate(JSONArray data) {
        dataday1=data.optJSONObject(0);//get first element in array
        date=dataday1.optString("date");
        day=dataday1.optString("day");
        high=dataday1.optInt("high");
        low=dataday1.optInt("low");
        description=dataday1.optString("text");
        
        //day 2
        dataday2=data.optJSONObject(1);//get first element in array
        date2=dataday2.optString("date");
        day2=dataday2.optString("day");
        high2=dataday2.optInt("high");
        low2=dataday2.optInt("low");
        description2=dataday2.optString("text");
        
        //day 3
        dataday3=data.optJSONObject(2);//get first element in array
        date3=dataday3.optString("date");
        day3=dataday3.optString("day");
        high3=dataday3.optInt("high");
        low3=dataday3.optInt("low");
        description3=dataday3.optString("text");
        
        //day 4
        dataday4=data.optJSONObject(3);//get first element in array
        date4=dataday4.optString("date");
        day4=dataday4.optString("day");
        high4=dataday4.optInt("high");
        low4=dataday4.optInt("low");
        description4=dataday4.optString("text");
        
        //day  5
        dataday5=data.optJSONObject(4);//get first element in array
        date5=dataday5.optString("date");
        day5=dataday5.optString("day");
        high5=dataday5.optInt("high");
        low5=dataday5.optInt("low");
        description5=dataday5.optString("text");
        
        //day 6
        dataday6=data.optJSONObject(5);//get first element in array
        date6=dataday6.optString("date");
        day6=dataday6.optString("day");
        high6=dataday6.optInt("high");
        low6=dataday6.optInt("low");
        description6=dataday6.optString("text");
        
        //day 7
        dataday7=data.optJSONObject(6);//get first element in array
        date7=dataday7.optString("date");
        day7=dataday7.optString("day");
        high7=dataday7.optInt("high");
        low7=dataday7.optInt("low");
        description7=dataday7.optString("text");
    }

}

