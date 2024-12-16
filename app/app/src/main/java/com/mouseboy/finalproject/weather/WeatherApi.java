package com.mouseboy.finalproject.weather;

import android.content.Context;
import android.location.Location;

import androidx.annotation.NonNull;

import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import com.mouseboy.finalproject.util.OkHttp;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.Date;
import java.util.TimeZone;

@SuppressWarnings("unused")
public class WeatherApi {
    static TimeZone timezone(){
        return TimeZone.getDefault();
    }

    public static class WeatherRequest {
        CurrentWeather currentWeather = new CurrentWeather();

        public static class CurrentWeather{
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
                for(java.lang.reflect.Field field:this.getClass().getFields()){
                    try {
                        if(field.getBoolean(this)){
                            builder.append(field.getName());
                            builder.append(",");
                        }
                    } catch (IllegalAccessException ignore) {
                    }
                }
                if(builder.length() == 0){
                    return "";
                }else{
                    builder.deleteCharAt(builder.length()-1);//remove last comma
                    return "current="+builder;
                }
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
        public double latitude;
        public double longitude;
        public double generationtime_ms;
        public int utc_offset_seconds;
        public String timezone;
        public String timezone_abbreviation;
        public double elevation;
        public CurrentWeather current;
        public CurrentUnits current_units;

        public static class CurrentWeather{
            @JsonAdapter(UnixTimestampAdapter.class)
            public Date time;
            public long interval;
            public double temperature_2m;
            public double relative_humidity;
            public double apparent_temperature;
            @JsonAdapter(NumericBooleanAdapter.class)
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

        public static class CurrentUnits{
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

    public static class UnixTimestampAdapter extends TypeAdapter<Date> {
        @Override
        public void write(JsonWriter out, Date value) throws IOException {
            if (value == null) {
                out.nullValue();
                return;
            }
            out.value(value.getTime() / 1000);
        }

        @Override
        public Date read(JsonReader in) throws IOException {
            if (in.peek() == JsonToken.NULL) {
                in.nextNull();
                return null;
            }
            return new Date(in.nextLong() * 1000);
        }
    }

    public static class NumericBooleanAdapter extends TypeAdapter<Boolean> {
        @Override
        public void write(JsonWriter out, Boolean value) throws IOException {
            out.value(value?1:0);
        }

        @Override
        public Boolean read(JsonReader in) throws IOException {
            return in.nextInt()==1;
        }
    }

    public static void request(Context context, WeatherRequest request, OkHttp.OnResponse<WeatherResult> response, OkHttp.OnFailure error){
        OkHttp.getString(context, request.toRequest(LocationTracker.bestLocation(), timezone()), json -> {
            try {
                WeatherResult result = new GsonBuilder().create().fromJson(json, WeatherResult.class);
                response.onResponse(result);
            } catch (Exception e) {
                error.onFailure(e);
            }
        }, error);
    }
}
