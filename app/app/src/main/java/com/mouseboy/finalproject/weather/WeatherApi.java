package com.mouseboy.finalproject.weather;

import android.content.Context;
import android.location.Location;

import androidx.annotation.NonNull;

import com.mouseboy.finalproject.util.OkHttp;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.Date;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;

public class WeatherApi {


    static TimeZone timezone(){
        return TimeZone.getDefault();
    }

    public static class WeatherRequest {
        CurrentWeather currentWeather = new CurrentWeather();

        public static class CurrentWeather{
            boolean temperature_2m;
            boolean relative_humidity_2m;
            boolean apparent_temperature;
            boolean is_day;
            boolean precipitation;
            boolean rain;
            boolean showers;
            boolean snowfall;
            boolean weather_code;
            boolean pressure_msl;
            boolean surface_pressure;
            boolean wind_speed_10m;
            boolean wind_direction_10m;
            boolean wind_gusts_10m;

            @NonNull
            @Override
            public String toString() {
                return "current=temperature_2m,relative_humidity_2m,apparent_temperature,is_day,precipitation,rain,showers,snowfall,weather_code,cloud_cover,pressure_msl,surface_pressure,wind_speed_10m,wind_direction_10m,wind_gusts_10m";
            }
        }
        private String toRequest(Location loc, TimeZone zone){
            String locS = "";
            if(loc != null){
                locS = "latitude="+loc.getLatitude()+"&longitude="+loc.getLongitude();
            }
            String zoneS = "timezone=auto";
            if(zone!=null){
                zoneS = "timezone="+ URLEncoder.encode(timezone().getID());
            }
            return "https://api.open-meteo.com/v1/forecast?"+locS+"&"+zoneS+"&timeformat=unixtime&" + currentWeather;
        }
    }

    public static class WeatherResult {
        public CurrentWeather current;

        public WeatherResult(JSONObject json) throws JSONException {
            if(json.has("current")){
                JSONObject current = json.getJSONObject("current");   
                this.current = new CurrentWeather();
                this.current.time = new Date(current.optLong("time"));
                this.current.interval = current.optLong("interval");
                this.current.temperature_2m = current.optDouble("temperature_2m");
                this.current.relative_humidity = current.optLong("relative_humidity");
                this.current.apparent_temperature = current.optLong("apparent_temperature");
                this.current.is_day = current.optBoolean("is_day");
                this.current.precipitation = current.optBoolean("precipitation");
                this.current.rain = current.optBoolean("rain");
                this.current.showers = current.optBoolean("showers");
                this.current.snowfall = current.optBoolean("snowfall");
                this.current.weather_code = current.optInt("weather_code");
                this.current.pressure_msl = current.optLong("pressure_msl");
                this.current.surface_pressure = current.optLong("surface_pressure");
                this.current.wind_speed_10m = current.optLong("wind_speed_10m");
                this.current.wind_direction_10m = current.optLong("wind_direction_10m");
                this.current.wind_gusts_10m = current.optLong("wind_gusts_10m");
            }
        }
        public static class CurrentWeather{
            public Date time;
            public long interval;
            public double temperature_2m;
            public double relative_humidity;
            public double apparent_temperature;
            public boolean is_day;
            public boolean precipitation;
            public boolean rain;
            public boolean showers;
            public boolean snowfall;
            public int weather_code;
            public double pressure_msl;
            public double surface_pressure;
            public double wind_speed_10m;
            public double wind_direction_10m;
            public double wind_gusts_10m;
        }
    }

    public static void request(Context context, WeatherRequest request, OkHttp.OnResponse<WeatherResult> response, OkHttp.OnFailure error){
        OkHttp.getJson(context, request.toRequest(LocationTracker.bestLocation(), timezone()), json -> {
            try {
                response.onResponse(new WeatherResult(json));
            } catch (JSONException e) {
                error.onFailure(e);
            }
        }, error);
    }
}
