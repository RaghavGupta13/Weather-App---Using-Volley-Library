package com.android.corsair.weatherapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.ButtonBarLayout;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    Button btnCityId, weatherByCityId, weatherByLocation;
    EditText editText;
    ListView lv_display_weather;
    String city_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Find the views on the layout to make them accessible
        btnCityId = (Button) findViewById(R.id.btn_getCityId);
        weatherByCityId = (Button) findViewById(R.id.btn_getWeatherByCityId);
        weatherByLocation = (Button) findViewById(R.id.btn_getWeatherByLocation);
        editText = (EditText) findViewById(R.id.editTextBox);
        lv_display_weather = (ListView) findViewById(R.id.listView);

        final WeatherDataService weatherDataService = new WeatherDataService(MainActivity.this);

        // click listeners for the buttons
        btnCityId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                weatherDataService.getCityId(editText.getText().toString(), new WeatherDataService.VolleyResponseListener() {
                    @Override
                    public void onError(String message) {
                        Toast.makeText(MainActivity.this, "Something is wrong", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onResponse(String cityId) {
                        Toast.makeText(MainActivity.this, "City Id is: " + cityId, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        weatherByCityId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                weatherDataService.getWeatherById(editText.getText().toString(), new WeatherDataService.ForecastByIdResponse() {
                    @Override
                    public void onError(String message) {
                        Toast.makeText(MainActivity.this, "Something is wrong", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onResponse(List<WeatherReportModel> data) {
                        //display the response in a list view
                        ArrayAdapter<WeatherReportModel> adapter = new ArrayAdapter<>(MainActivity.this,
                                android.R.layout.simple_list_item_1, data);
                        lv_display_weather.setAdapter(adapter);
                    }
                });
            }
        });

        weatherByLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                weatherDataService.getWeatherByLocation(editText.getText().toString(), new WeatherDataService.ForecastByLocation() {
                    @Override
                    public void onError(String message) {
                        Toast.makeText(MainActivity.this, "Something is wrong", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onResponse(List<WeatherReportModel> data) {
                        //display the response in a list view
                        ArrayAdapter<WeatherReportModel> adapter = new ArrayAdapter<>(MainActivity.this,
                                android.R.layout.simple_list_item_1, data);
                        lv_display_weather.setAdapter(adapter);
                    }
                });
            }
        });

    }
}