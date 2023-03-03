package org.example.hw6;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import java.io.IOException;

public class AccuweatherModel implements WeatherModel {

    private static final String PROTOCOL = "https";
    private static final String BASE_HOST = "dataservice.accuweather.com";
    private static final String FORECASTS = "forecasts";
    private static final String VERSION = "v1";
    private static final String DAILY = "daily";
    private static final String ONE_DAY = "1day";
    private static final String FIVE_DAYS = "5day";
    private static final String API_KEY = "Txk7FVH4MFdbAPCWBGI6dihIw54duq3W";
    private static final String API_KEY_QUERY_PARAM = "apikey";
    private static final String LOCATIONS = "locations";
    private static final String CITIES = "cities";
    private static final String AUTOCOMPLETE = "autocomplete";

    private static final OkHttpClient okHttpClient = new OkHttpClient();
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public void getWeather(String selectedCity, String period) throws IOException {
            try {
                HttpUrl httpUrl = new HttpUrl.Builder()
                        .scheme(PROTOCOL)
                        .host(BASE_HOST)
                        .addPathSegment(FORECASTS)
                        .addPathSegment(VERSION)
                        .addPathSegment(DAILY)
                        .addPathSegment(period)
                        .addPathSegment(detectCityKey(selectedCity))
                        .addQueryParameter(API_KEY_QUERY_PARAM, API_KEY)
                        .build();

                Request request = new Request.Builder()
                        .url(httpUrl)
                        .build();

                Response oneDayForecastResponse = okHttpClient.newCall(request).execute();
                String weatherResponse = oneDayForecastResponse.body().string();
                String result = weatherResponse;
                ObjectMapper mapper = new ObjectMapper();
                JsonFactory factory = mapper.getJsonFactory();
                JsonParser parser = factory.createJsonParser(result);
                JsonNode obj = mapper.readTree(parser);
                if (period.equals("1day")) {
                    System.out.println("Погода для города " + selectedCity + ":");
                    System.out.println("Минимальная температура воздуха сегодня: " + countCelcium(obj.get("DailyForecasts").get(0).get("Temperature")
                            .get("Minimum").get("Value").toString()) + " C");
                    System.out.println("Максимальная температура воздуха сегодня: " + countCelcium(obj.get("DailyForecasts").get(0).get("Temperature")
                            .get("Maximum").get("Value").toString()) + " C");
                }
                if (period.equals("5day")) {
                    System.out.println("Погода для города " + selectedCity + ":");
                    int i = 0;
                    while (i < 5) {
                        System.out.println("\nДень " + (i + 1) + ": ");
                        System.out.println("Минимальная температура воздуха: " + countCelcium(obj.get("DailyForecasts").get(i).get("Temperature")
                                .get("Minimum").get("Value").toString()) + " C");
                        System.out.println("Максимальная температура воздуха: " + countCelcium(obj.get("DailyForecasts").get(i).get("Temperature")
                                .get("Maximum").get("Value").toString()) + " C");
                        i++;
                    }
                }
            } catch (Exception e) {
                System.out.println(e);
            }
    }

    private String detectCityKey(String selectCity) throws IOException {
        HttpUrl httpUrl = new HttpUrl.Builder()
                .scheme(PROTOCOL)
                .host(BASE_HOST)
                .addPathSegment(LOCATIONS)
                .addPathSegment(VERSION)
                .addPathSegment(CITIES)
                .addPathSegment(AUTOCOMPLETE)
                .addQueryParameter(API_KEY_QUERY_PARAM, API_KEY)
                .addQueryParameter("q", selectCity)
                .build();

        Request request = new Request.Builder()
                .url(httpUrl)
                .get()
                .addHeader("accept", "application/json")
                .build();

        Response response = okHttpClient.newCall(request).execute();
        String responseString = response.body().string();

        String cityKey = objectMapper.readTree(responseString).get(0).at("/Key").asText();
        return cityKey;
    }

    public Double countCelcium(String far) {
        Double farengeit = Double.parseDouble(far);
        return (farengeit - 32) / 2 + ((farengeit - 32) / 2 / 10);
    }

}
