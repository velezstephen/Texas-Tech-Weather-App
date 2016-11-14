package com.example.kevin.TexasTechWeatherApp.service;

import com.example.kevin.TexasTechWeatherApp.data.Channel;

/**
 * Created by kevin on 10/12/2016.
 */

public interface WeatherServiceCallback {
    void serviceSuccess(Channel channel);

    void serviceFailure(Exception exception);
}
