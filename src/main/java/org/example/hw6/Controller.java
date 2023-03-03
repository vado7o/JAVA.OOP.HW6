package org.example.hw6;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Controller {
    private WeatherModel weatherModel = new AccuweatherModel();
    private Map<Integer, Period> variants = new HashMap<>();


    public Controller() {
        variants.put(1, Period.ONE_DAY);
        variants.put(5, Period.FIVE_DAYS);
    }
    public void getWeather(String command, String city) throws IOException {
        Integer userOption = Integer.parseInt(command);
        switch (variants.get(userOption)) {
            case ONE_DAY:
                weatherModel.getWeather(city, "1day");
                break;
            case FIVE_DAYS:
                weatherModel.getWeather(city, "5day");
                break;
        }
    }
}
