package org.example.hw6;

import java.io.IOException;

public interface WeatherModel {
    void getWeather(String selectedCity, String period) throws IOException;
}
