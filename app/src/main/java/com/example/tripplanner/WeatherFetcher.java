package com.example.tripplanner;

import android.os.AsyncTask;

import com.example.tripplanner.models.Weather;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class WeatherFetcher extends AsyncTask<String, Void, JsonObject> {
    private final String apiKey;
    private IWeatherDataListener listener;

    public WeatherFetcher(String apiKey, IWeatherDataListener listener) {
        this.apiKey = apiKey;
        this.listener = listener;
    }

    public void fetchWeather(String city) {
        String urlString = String.format("https://api.weatherapi.com/v1/current.json?key=%s&q=%s&aqi=no", apiKey, city);
        execute(urlString);
    }

    @Override
    protected JsonObject doInBackground(String... params) {
        try {
            URL url = new URL(params[0]);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setDoOutput(false);
            connection.setConnectTimeout(10000);
            connection.setReadTimeout(10000);
            connection.connect();

            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                InputStream inputStream = connection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuilder stringBuilder = new StringBuilder();
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    stringBuilder.append(line);
                }
                bufferedReader.close();
                inputStream.close();

                JsonParser parser = new JsonParser();
                return parser.parse(stringBuilder.toString()).getAsJsonObject();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(JsonObject result) {
        // process the result
        if (listener != null && result != null) {
            Weather weather = getWeatherObject(result);
            listener.onDataReceived(weather);
        }
    }

    private Weather getWeatherObject(JsonObject data) {
        JsonObject location = data.getAsJsonObject("location");
        JsonObject condition = data.getAsJsonObject("current").getAsJsonObject("condition");
        JsonObject current = data.getAsJsonObject("current");

        String name = location.get("name").getAsString();
        String region = location.get("region").getAsString();
        String country = location.get("country").getAsString();
        String text = condition.get("text").getAsString();
        String icon = condition.get("icon").getAsString();
        int temp_c = current.get("temp_c").getAsInt();
        int humidityVal = current.get("humidity").getAsInt();
        int wind_kph = current.get("wind_kph").getAsInt();

        String city = String.format("Weather in %s", name);
        String weatherIcon = String.format("https:%s", icon);
        String temp = String.format("%d°C", temp_c);
        String humidity = String.format("Humidity: %d%%", humidityVal);
        String wind = String.format("Wind speed: %dkm/h", wind_kph);

        return new Weather(city, weatherIcon, temp, humidity, wind);
    }
}
