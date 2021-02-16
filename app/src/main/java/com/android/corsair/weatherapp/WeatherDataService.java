package com.android.corsair.weatherapp;

import android.content.Context;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class WeatherDataService {

    public static final String QUERY_FOR_CITY_ID = "https://www.metaweather.com/api/location/search/?query=";
    public static final String QUERY_FOR_WEATHER_BY_ID = "https://www.metaweather.com/api/location/";
    Context context;
    String city_id = "";

    public WeatherDataService(Context context) {
        this.context = context;
    }

    //creating an interface for implementing callback methods
    public interface VolleyResponseListener {
        void onError(String message);

        void onResponse(String cityId);
    }

    public void getCityId(String cityName, final VolleyResponseListener volleyResponseListener) {
        String url =
                QUERY_FOR_CITY_ID + cityName;
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {

                try {
                    JSONObject cityInfo = response.getJSONObject(0);
                    city_id = cityInfo.getString("woeid");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                // Toast.makeText(context, "City ID:" + city_id, Toast.LENGTH_SHORT).show();
                volleyResponseListener.onResponse(city_id);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // Toast.makeText(context, "An error occured", Toast.LENGTH_SHORT).show();
                volleyResponseListener.onError("Something is wrong");
            }
        });
        MySingleton.getInstance(context).addToRequestQueue(request);
    }

    // Creating another interface for 'Weather By ID' callbacks
    public interface ForecastByIdResponse {
        void onError(String message);

        void onResponse(List<WeatherReportModel> weatherReportModels);
    }

    //Getting weather by city ID
    public void getWeatherById(String city_id, final ForecastByIdResponse forecastByIdResponse) {

        final ArrayList<WeatherReportModel> weatherReport = new ArrayList<>();

        String url = QUERY_FOR_WEATHER_BY_ID + city_id;
        JsonObjectRequest request2 = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {

                    JSONArray consolidated_weather_list = response.getJSONArray("consolidated_weather");
                    for (int i = 0; i < consolidated_weather_list.length(); i++) {
                        WeatherReportModel one_day_weather = new WeatherReportModel();
                        JSONObject oneDayWeatherArray = consolidated_weather_list.getJSONObject(i);
                        one_day_weather.setApplicable_date(oneDayWeatherArray.getString("applicable_date"));
                        one_day_weather.setWeather_state_name(oneDayWeatherArray.getString("weather_state_name"));
                        one_day_weather.setThe_temp(oneDayWeatherArray.getLong("the_temp"));
                        one_day_weather.setMin_temp(oneDayWeatherArray.getLong("min_temp"));
                        one_day_weather.setMax_temp(oneDayWeatherArray.getLong("max_temp"));

                        weatherReport.add(one_day_weather);
                    }
                    forecastByIdResponse.onResponse(weatherReport);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        MySingleton.getInstance(context).addToRequestQueue(request2);
    }

    public interface ForecastByLocation {
        void onError(String message);

        void onResponse(List<WeatherReportModel> weatherReportModels);
    }


    public void getWeatherByLocation(String city_name, final ForecastByLocation forecastByLocation) {
        getCityId(city_name, new VolleyResponseListener() {
            @Override
            public void onError(String message) {

            }

            @Override
            public void onResponse(String cityId) {
                getWeatherById(city_id, new ForecastByIdResponse() {
                    @Override
                    public void onError(String message) {

                    }

                    @Override
                    public void onResponse(List<WeatherReportModel> weatherReportModels) {
                        forecastByLocation.onResponse(weatherReportModels);
                    }
                });
            }
        });

    }
}
