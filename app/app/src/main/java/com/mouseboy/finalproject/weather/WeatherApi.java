package com.mouseboy.finalproject.weather;

import android.content.Context;
import android.location.Location;

import androidx.annotation.NonNull;

import com.google.gson.annotations.JsonAdapter;
import com.mouseboy.finalproject.util.Gson;
import com.mouseboy.finalproject.util.OkHttp;

import java.net.URLEncoder;
import java.util.Date;
import java.util.TimeZone;

@SuppressWarnings("unused")
public class WeatherApi {
    static TimeZone timezone() {
        return TimeZone.getDefault();
    }

    public static class WeatherRequest {

        transient Location loc;
        transient TimeZone timeZone;
        CurrentWeather currentWeather = new CurrentWeather();

        public WeatherRequest(){
            loc = LocationTracker.bestLocation();
            timeZone = timezone();
        }
        public WeatherRequest(Location loc){
            this.loc = loc;
            timeZone = timezone();
        }

        public static class CurrentWeather {
            public boolean temperature_2m = true;
            public boolean relative_humidity_2m = true;
            public boolean apparent_temperature = true;
            public boolean is_day = true;
            public boolean precipitation = true;
            public boolean rain = true;
            public boolean showers = true;
            public boolean snowfall = true;
            public boolean weather_code = true;
            public boolean pressure_msl = true;
            public boolean surface_pressure = true;
            public boolean wind_speed_10m = true;
            public boolean wind_direction_10m = true;
            public boolean wind_gusts_10m = true;

            @NonNull
            @Override
            public String toString() {
                StringBuilder builder = new StringBuilder();
                for (java.lang.reflect.Field field : this.getClass().getFields()) {
                    try {
                        if (field.getBoolean(this)) {
                            builder.append(field.getName());
                            builder.append(",");
                        }
                    } catch (IllegalAccessException ignore) {
                    }
                }
                if (builder.length() == 0) {
                    return "";
                } else {
                    builder.deleteCharAt(builder.length() - 1);//remove last comma
                    return "current=" + builder;
                }
            }
        }

        private String toRequest() {
            String locS = "";
            if (loc != null) {
                locS = "latitude=" + loc.getLatitude() + "&longitude=" + loc.getLongitude();
            }
            String zoneS = "timezone=auto";
            if (timeZone != null) {
                zoneS = "timezone=" + URLEncoder.encode(timezone().getID());
            }
            return "https://api.open-meteo.com/v1/forecast?" + locS + "&" + zoneS + "&timeformat=unixtime&" + currentWeather;
        }
    }

    public static class WeatherResult {
        public double latitude;
        public double longitude;
        public double generationtime_ms;
        public int utc_offset_seconds;
        public String timezone;
        public String timezone_abbreviation;
        public double elevation;
        public CurrentWeather current;
        public CurrentUnits current_units;

        public static class CurrentWeather {
            @JsonAdapter(Gson.UnixTimestampAdapter.class)
            public Date time;
            public long interval;
            public double temperature_2m;
            public double relative_humidity;
            public double apparent_temperature;
            @JsonAdapter(Gson.NumericBooleanAdapter.class)
            public boolean is_day;
            public double precipitation;
            public double rain;
            public double showers;
            public double snowfall;
            public int weather_code;
            public double pressure_msl;
            public double surface_pressure;
            public double wind_speed_10m;
            public double wind_direction_10m;
            public double wind_gusts_10m;
        }

        public static class CurrentUnits {
            public String time;
            public String interval;
            public String temperature_2m;
            public String relative_humidity;
            public String apparent_temperature;
            public String is_day;
            public String precipitation;
            public String rain;
            public String showers;
            public String snowfall;
            public String weather_code;
            public String pressure_msl;
            public String surface_pressure;
            public String wind_speed_10m;
            public String wind_direction_10m;
            public String wind_gusts_10m;
        }
    }

    public static void request(Context context, WeatherRequest request, OkHttp.OnResponse<WeatherResult> response, OkHttp.OnFailure error) {
        OkHttp.getJson(
                context,
                request.toRequest(),
                WeatherResult.class,
                response,
                error
        );
    }
}
