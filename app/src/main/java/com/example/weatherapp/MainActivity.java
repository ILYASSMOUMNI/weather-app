package com.example.weatherapp;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import org.json.JSONObject;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Scanner;

public class MainActivity extends AppCompatActivity {

    private static final String CITY = "sidi kacem";
    private static final String API = "d07db523b4cbe8ed047a67ccf0b5c8aa"; // Use API key

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        new weatherTask().execute();
    }

    class weatherTask extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            /* Showing the ProgressBar, Making the main design GONE */
            findViewById(R.id.loader).setVisibility(View.VISIBLE);
            findViewById(R.id.mainContainer).setVisibility(View.GONE);
            findViewById(R.id.errorText).setVisibility(View.GONE);
        }

        @Override
        protected String doInBackground(String... params) {
            String response;
            try {
                response = new Scanner(new URL("https://api.openweathermap.org/data/2.5/weather?q=" + CITY + "&units=metric&appid=" + API).openStream(), "UTF-8").useDelimiter("\\A").next();
            } catch (Exception e) {
                response = null;
            }
            return response;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            try {
                /* Extracting JSON returns from the API */
                JSONObject jsonObj = new JSONObject(result);
                JSONObject main = jsonObj.getJSONObject("main");
                JSONObject sys = jsonObj.getJSONObject("sys");
                JSONObject wind = jsonObj.getJSONObject("wind");
                JSONObject weather = jsonObj.getJSONArray("weather").getJSONObject(0);

                long updatedAt = jsonObj.getLong("dt");
                String updatedAtText = "Updated at: " + new SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.ENGLISH).format(new Date(updatedAt * 1000));
                String temp = main.getString("temp") + "°C";
                String tempMin = "Min Temp: " + main.getString("temp_min") + "°C";
                String tempMax = "Max Temp: " + main.getString("temp_max") + "°C";
                String pressure = main.getString("pressure");
                String humidity = main.getString("humidity");

                long sunrise = sys.getLong("sunrise");
                long sunset = sys.getLong("sunset");
                String windSpeed = wind.getString("speed");
                String weatherDescription = weather.getString("description");

                String address = jsonObj.getString("name") + ", " + sys.getString("country");

                /* Populating extracted data into our views */
                ((TextView) findViewById(R.id.address)).setText(address);
                ((TextView) findViewById(R.id.updated_at)).setText(updatedAtText);
                ((TextView) findViewById(R.id.status)).setText(weatherDescription.substring(0, 1).toUpperCase() + weatherDescription.substring(1));
                ((TextView) findViewById(R.id.temp)).setText(temp);
                ((TextView) findViewById(R.id.temp_min)).setText(tempMin);
                ((TextView) findViewById(R.id.temp_max)).setText(tempMax);
                ((TextView) findViewById(R.id.sunrise)).setText(new SimpleDateFormat("hh:mm a", Locale.ENGLISH).format(new Date(sunrise * 1000)));
                ((TextView) findViewById(R.id.sunset)).setText(new SimpleDateFormat("hh:mm a", Locale.ENGLISH).format(new Date(sunset * 1000)));
                ((TextView) findViewById(R.id.wind)).setText(windSpeed);
                ((TextView) findViewById(R.id.pressure)).setText(pressure);
                ((TextView) findViewById(R.id.humidity)).setText(humidity);
                // weather image code:
                ImageView weatherImage = (ImageView) findViewById(R.id.imagestat);
                if (weatherDescription.equals("Broken clouds")) {
                    weatherImage.setImageResource(R.drawable.sun_cloud_rain);

                    // ... add more cases for each weather status ...
                }

                /* Views populated, Hiding the loader, Showing the main design */
                findViewById(R.id.loader).setVisibility(View.GONE);
                findViewById(R.id.mainContainer).setVisibility(View.VISIBLE);


            } catch (Exception e) {
                findViewById(R.id.loader).setVisibility(View.GONE);
                findViewById(R.id.errorText).setVisibility(View.VISIBLE);
            }
        }
    }
}
