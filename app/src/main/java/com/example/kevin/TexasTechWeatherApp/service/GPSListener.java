package com.example.kevin.TexasTechWeatherApp.service;

/**
 * Created by Stephen on 11/20/2016.
 */

import com .example.kevin.TexasTechWeatherApp.data.LocationResult;


public interface GPSListener {
    void geocodeSuccess(LocationResult location);
    void geocodeFailure(Exception exception);
}
