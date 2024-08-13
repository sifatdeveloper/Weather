package com.example.weather;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private FusedLocationProviderClient fusedLocationProviderClient;

    TextView temptv, humidity, pressure, wind, country, day;
    TextView day1, day2, day3, day4, day5, day6;
    SearchView search;
    private WeatherAdapter weatherAdapter;
    private List<WeatherForecast> weatherItemList;
    TextView mintemp1, maxtemp1, mintemp2, maxtemp2, mintemp3, maxtemp3, mintemp4, maxtemp4, mintemp5, maxtemp5, mintemp6, maxtemp6;
    ImageView image, icon1, icon2, icon3, icon4, icon5, icon6;
    RecyclerView recyclerView;
    TextView  uvtemp;
    TextView des;
    TextView sunset,sunrise;
    TextView prayerTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sunrise=findViewById(R.id.textView57);
        search=findViewById(R.id.searchView);
        sunset=findViewById(R.id.textView54);
        mintemp1 = findViewById(R.id.mintemp1);
        icon1 = findViewById(R.id.icon1);
      // uv = findViewById(R.id.textView3);
        des=findViewById(R.id.textView8);
        uvtemp = findViewById(R.id.textView15);
        icon2 = findViewById(R.id.icon2);
        icon3 = findViewById(R.id.icon3);
        icon4 = findViewById(R.id.icon4);
        icon5 = findViewById(R.id.icon5);
        icon6 = findViewById(R.id.icon6);
        maxtemp1 = findViewById(R.id.maxtemp1);
        mintemp2 = findViewById(R.id.mintemp2);
        maxtemp2 = findViewById(R.id.maxtemp2);
        recyclerView = findViewById(R.id.recytime);
        mintemp3 = findViewById(R.id.mintemp3);
        maxtemp3 = findViewById(R.id.maxtemp3);
        mintemp4 = findViewById(R.id.mintemp4);
        maxtemp4 = findViewById(R.id.maxtemp4);
        mintemp5 = findViewById(R.id.mintemp5);
        maxtemp5 = findViewById(R.id.maxtemp5);
        mintemp6 = findViewById(R.id.mintemp6);
        maxtemp6 = findViewById(R.id.maxtemp6);
        prayerTextView = findViewById(R.id.textView3);

        humidity = findViewById(R.id.textView5);
        day1 = findViewById(R.id.day1);
        day2 = findViewById(R.id.day2);
        day3 = findViewById(R.id.day3);
        day4 = findViewById(R.id.day4);
        day5 = findViewById(R.id.day5);
        day6 = findViewById(R.id.day6);
        temptv = findViewById(R.id.textView2);
        pressure = findViewById(R.id.textView05);
        wind = findViewById(R.id.textView7);
        image = findViewById(R.id.imageView);
       // day = findViewById(R.id.textView3);
        country = findViewById(R.id.textView);
        LinearLayoutManager manager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(manager);

        weatherItemList = new ArrayList<>();
        weatherAdapter = new WeatherAdapter(weatherItemList, this);
        recyclerView.setAdapter(weatherAdapter);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        getCurrentLocationAndFetchWeather();
        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (!query.isEmpty()) {
                    fetchWeatherDataByCity(query);
                    search.setQuery("", false);
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }


    private void fetchPrayerTimes(String cityName) {
        String url = "https://api.aladhan.com/v1/timingsByCity/12-08-2024?city="+cityName+"&country=Pakistan&method=8";
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONObject data = jsonObject.getJSONObject("data");
                    JSONObject timings = data.getJSONObject("timings");

                    // Get prayer times
                    String fajr = timings.getString("Fajr");
                    String dhuhr = timings.getString("Dhuhr");
                    String asr = timings.getString("Asr");
                    String maghrib = timings.getString("Maghrib");
                    String isha = timings.getString("Isha");

                    // Get current time
                    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm a", java.util.Locale.getDefault());
                    String currentTime = sdf.format(new Date());

                    // Determine the current prayer
                    String currentPrayer = getCurrentPrayer(fajr, dhuhr, asr, maghrib, isha, currentTime);

                    // Display the current prayer
                    prayerTextView.setText("Current Prayer: \n" + currentPrayer+"  "+currentTime);

                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(MainActivity.this, "Error parsing prayer times", Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this, "Error: " + error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        Volley.newRequestQueue(this).add(stringRequest);
    }

    private String getCurrentPrayer(String fajr, String dhuhr, String asr, String maghrib, String isha, String currentTime) {
        // Logic to determine the current prayer based on the current time
        // For simplicity, we'll assume the times are in HH:mm format
        if (currentTime.compareTo(fajr) < 0) {
            return "Fajr";
        } else if (currentTime.compareTo(dhuhr) < 0) {
            return "Dhuhr";
        } else if (currentTime.compareTo(asr) < 0) {
            return "Asr";
        } else if (currentTime.compareTo(maghrib) < 0) {
            return "Maghrib";
        } else if (currentTime.compareTo(isha) < 0) {
            return "Isha";
        } else {
            return "No prayer time available";
        }
    }


    private void getCurrentLocationAndFetchWeather() {
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
            return;
        }

        fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                Location location = task.getResult();
                if (location != null) {
                    double latitude = location.getLatitude();
                    double longitude = location.getLongitude();
                    FindWeather(latitude, longitude);
                    FindWeatherForecast(latitude, longitude);
                    fetchWeatherData(latitude, longitude);
                } else {
                    Toast.makeText(MainActivity.this, "Unable to find location. Please try again.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    private void fetchWeatherDataByCity(String cityName){
        String apiKey = "dc1444b71e04120759905ff65a3bc317";
        String url = "https://api.openweathermap.org/data/2.5/weather?q=" + cityName + "&appid=" + apiKey + "&units=metric";
        JsonObjectRequest jsonObjectRequest=new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONObject jsonObject = new JSONObject(String.valueOf(response));
                    Log.d("APIResponse", jsonObject.toString()); // Log the entire API response

                    // Find temperature

                    JSONObject mainObject = jsonObject.getJSONObject("main");
                    int temp = (int) mainObject.getDouble("temp");
                    temptv.setText(temp + "°C");

                    // Find humidity
                    int humidity_find = mainObject.getInt("humidity");
                    humidity.setText(humidity_find + " %");

                    // Find pressure
                    String pressure_find = mainObject.getString("pressure");
                    pressure.setText(pressure_find + " hPa");

                    // Find wind speed
                    JSONObject windObject = jsonObject.getJSONObject("wind");
                    String wind_find = windObject.getString("speed");
                    wind.setText(wind_find + " km/h");

                    // Find weather icon
                    JSONArray weatherArray = jsonObject.getJSONArray("weather");
                    if (weatherArray.length() > 0) {
                        JSONObject weatherObject = weatherArray.getJSONObject(0);
                        String iconCode = weatherObject.getString("icon");
                        String weatherDescription = weatherObject.getString("description");
                        Log.d("WeatherCondition", "Icon Code: " + iconCode + ", Description: " + weatherDescription);

                        // Use the icon code to load the appropriate image
                        if (iconCode != null && !iconCode.isEmpty()) {
                            String iconUrl = "https://openweathermap.org/img/wn/" + iconCode + "@2x.png";
                            Log.d("WeatherIconURL", iconUrl); // Log the URL for debugging
                            des.setText(weatherDescription);

                            Glide.with(MainActivity.this)
                                    .load(iconUrl)
                                    .into(image);
                        } else {
                            Log.e("WeatherIcon", "Icon code is null or empty");
                            Toast.makeText(MainActivity.this, "WeatherIcon: Icon code is null or empty", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Log.e("WeatherData", "Weather array is empty or missing");
                        Toast.makeText(MainActivity.this, "Error: Weather data is missing", Toast.LENGTH_SHORT).show();
                    }

                    // Find and display city name
                    String cityName = jsonObject.getString("name");
                    country.setText(cityName);

                    // Set current day with date
                    SimpleDateFormat sdf = new SimpleDateFormat("EEEE, MMMM d, yyyy", java.util.Locale.getDefault());
                    String currentDayWithDate = sdf.format(new Date());
                    // day.setText(currentDayWithDate);
                    JSONObject sysObject = jsonObject.getJSONObject("sys");
                    long sunriseTimestamp = sysObject.getLong("sunrise");
                    long sunsetTimestamp = sysObject.getLong("sunset");
                    SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a", java.util.Locale.getDefault());
                    String sunriseTime = timeFormat.format(new Date(sunriseTimestamp * 1000L));
                    String sunsetTime = timeFormat.format(new Date(sunsetTimestamp * 1000L));
                    sunrise.setText(sunriseTime);
                    sunset.setText(sunsetTime);
                    // Find min and max temperature
                    int minTemp = (int) mainObject.getDouble("temp_min");
                    int maxTemp = (int) mainObject.getDouble("temp_max");
                    //uv.setText( minTemp + "°C"+" ~ "+maxTemp+"°C");

                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(MainActivity.this, "Error parsing weather data", Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this, "Error: " + error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();

            }
        });
        Volley.newRequestQueue(this).add(jsonObjectRequest);
        Forecastcity(cityName);
        Fetchcitydaily(cityName);
    }
    private void Fetchcitydaily(String cityName){

        String apiKey = "dc1444b71e04120759905ff65a3bc317";
        String url = "https://api.openweathermap.org/data/2.5/forecast?q=" + cityName + "&appid=" + apiKey + "&units=metric";
        StringRequest request=new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray listArray = jsonObject.getJSONArray("list");

                    // Clear previous data
                    weatherItemList.clear();

                    // Setup date formats
                    SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", java.util.Locale.getDefault());
                    SimpleDateFormat outputFormat = new SimpleDateFormat("HH:mm", java.util.Locale.getDefault());
                    SimpleDateFormat dayFormat = new SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault());

                    // Get current day
                    String currentDay = dayFormat.format(new Date());

                    // Loop through forecast data
                    for (int i = 0; i < listArray.length(); i++) {
                        JSONObject dayObject = listArray.getJSONObject(i);
                        String dateText = dayObject.getString("dt_txt");
                        JSONObject mainObject = dayObject.getJSONObject("main");
                        int temp = (int) mainObject.getDouble("temp");

                        // Get weather icon code
                        JSONArray weatherArray = dayObject.getJSONArray("weather");
                        String iconCode = weatherArray.getJSONObject(0).getString("icon");
                        String iconUrl = "https://openweathermap.org/img/wn/" + iconCode + "@2x.png";

                        // Extract rain data if available
                        int rainPercentage = 0;
                        if (dayObject.has("rain")) {
                            JSONObject rainObject = dayObject.getJSONObject("rain");
                            rainPercentage = (int) rainObject.optDouble("3h", 0);
                        }

                        // Parse the date and time
                        Date date = inputFormat.parse(dateText);
                        String time = outputFormat.format(date);
                        String day = dayFormat.format(date);

                        // Debugging logs
                        Log.d("WeatherData", "Date: " + dateText);
                        Log.d("WeatherData", "Temperature: " + temp);
                        Log.d("WeatherData", "Icon URL: " + iconUrl);
                        Log.d("WeatherData", "Rain Percentage: " + rainPercentage);

                        // Check if forecast is for today
                        if (day.equals(currentDay)) {
                            weatherItemList.add(new WeatherForecast(time, temp, iconUrl, rainPercentage));
                        }
                    }

                    // Notify adapter to update UI
                    weatherAdapter.notifyDataSetChanged();

                } catch (JSONException | ParseException e) {
                    e.printStackTrace();
                    Toast.makeText(MainActivity.this, "Error parsing weather data", Toast.LENGTH_SHORT).show();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this, "Error: " + error, Toast.LENGTH_SHORT).show();

            }
        });
        Volley.newRequestQueue(this).add(request);

    }
    private void Forecastcity(String cityName) {
        String apiKey = "dc1444b71e04120759905ff65a3bc317";
        String url = "https://api.openweathermap.org/data/2.5/forecast?q=" + cityName + "&appid=" + apiKey + "&units=metric";

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);

                    // Initialize calendar for forecasting
                    Calendar calendar = Calendar.getInstance();
                    SimpleDateFormat dayFormat = new SimpleDateFormat("EEEE, d", java.util.Locale.getDefault());

                    // Initialize arrays to store day and temperature TextViews
                    TextView[] days = {day1, day2, day3, day4, day5, day6};
                    TextView[] minTemps = {mintemp1, mintemp2, mintemp3, mintemp4, mintemp5, mintemp6};
                    TextView[] maxTemps = {maxtemp1, maxtemp2, maxtemp3, maxtemp4, maxtemp5, maxtemp6};
                    ImageView[] weatherIcons = {icon1, icon2, icon3, icon4, icon5, icon6};

                    // Get forecast data
                    JSONArray listArray = jsonObject.getJSONArray("list");

                    // Loop through the next 6 days
                    for (int i = 0; i < 6; i++) {
                        // Move to the next day
                        calendar.add(Calendar.DAY_OF_YEAR, 1);
                        String dayName = dayFormat.format(calendar.getTime());

                        // Initialize variables for min and max temperatures, and icon selection
                        double minTemp = Double.POSITIVE_INFINITY;  // Initialize with a very high value
                        double maxTemp = Double.NEGATIVE_INFINITY;  // Initialize with a very low value
                        String selectedIconCode = null;  // Icon closest to noon

                        boolean temperatureFound = false;  // Flag to check if temperatures are found

                        for (int j = 0; j < listArray.length(); j++) {
                            JSONObject dayObject = listArray.getJSONObject(j);
                            String dateText = dayObject.getString("dt_txt");

                            // Check if this forecast entry corresponds to the current day
                            if (dateText.startsWith(new SimpleDateFormat("yyyy-MM-dd").format(calendar.getTime()))) {
                                JSONObject dayMainObject = dayObject.getJSONObject("main");
                                int tempMin = (int) dayMainObject.getDouble("temp_min");
                                int tempMax = (int) dayMainObject.getDouble("temp_max");

                                // Update min and max temperatures
                                if (tempMin < minTemp) {
                                    minTemp = tempMin;
                                }
                                if (tempMax > maxTemp) {
                                    maxTemp = tempMax;
                                }

                                // Get the weather icon code closest to 12:00 PM
                                String timePart = dateText.split(" ")[1];
                                if (timePart.startsWith("12:00")) {
                                    JSONArray weatherArray = dayObject.getJSONArray("weather");
                                    if (weatherArray.length() > 0) {
                                        selectedIconCode = weatherArray.getJSONObject(0).getString("icon");
                                    }
                                }

                                temperatureFound = true;  // Mark that we have found temperatures
                            }
                        }

                        // Only update the UI if valid temperatures were found
                        if (temperatureFound) {
                            days[i].setText(dayName);
                            minTemps[i].setText("Min: " + String.format("%.1f", minTemp) + "°C");
                            maxTemps[i].setText("Max: " + String.format("%.1f", maxTemp) + "°C");

                            // Load the weather icon using Glide
                            if (selectedIconCode != null) {
                                String iconUrl = "https://openweathermap.org/img/wn/" + selectedIconCode + "@2x.png";
                                Glide.with(MainActivity.this)
                                        .load(iconUrl)
                                        .into(weatherIcons[i]);
                            }
                        } else {
                            // Handle the case where no temperatures were found
                            days[i].setText(dayName);
                            minTemps[i].setText("Min: N/A");
                            maxTemps[i].setText("Max: N/A");
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(MainActivity.this, "Error parsing weather data", Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this, "Error: " + error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        Volley.newRequestQueue(this).add(stringRequest);
    }

    private void FindWeather(double latitude, double longitude) {
        // Construct URL with latitude and longitude
        String url = "https://api.openweathermap.org/data/2.5/weather?lat=" + latitude + "&lon=" + longitude + "&appid=dc1444b71e04120759905ff65a3bc317&units=metric";

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            Log.d("APIResponse", jsonObject.toString()); // Log the entire API response

                            // Find temperature

                            JSONObject mainObject = jsonObject.getJSONObject("main");
                            int temp = (int) mainObject.getDouble("temp");
                            temptv.setText(temp + "°C");

                            // Find humidity
                            int humidity_find = mainObject.getInt("humidity");
                            humidity.setText(humidity_find + " %");

                            // Find pressure
                            String pressure_find = mainObject.getString("pressure");
                            pressure.setText(pressure_find + " hPa");

                            // Find wind speed
                            JSONObject windObject = jsonObject.getJSONObject("wind");
                            String wind_find = windObject.getString("speed");
                            wind.setText(wind_find + " km/h");

                            // Find weather icon
                            JSONArray weatherArray = jsonObject.getJSONArray("weather");
                            if (weatherArray.length() > 0) {
                                JSONObject weatherObject = weatherArray.getJSONObject(0);
                                String iconCode = weatherObject.getString("icon");
                                String weatherDescription = weatherObject.getString("description");
                                Log.d("WeatherCondition", "Icon Code: " + iconCode + ", Description: " + weatherDescription);

                                // Use the icon code to load the appropriate image
                                if (iconCode != null && !iconCode.isEmpty()) {
                                    String iconUrl = "https://openweathermap.org/img/wn/" + iconCode + "@2x.png";
                                    Log.d("WeatherIconURL", iconUrl); // Log the URL for debugging
                                    des.setText(weatherDescription);

                                    Glide.with(MainActivity.this)
                                            .load(iconUrl)
                                            .into(image);
                                } else {
                                    Log.e("WeatherIcon", "Icon code is null or empty");
                                    Toast.makeText(MainActivity.this, "WeatherIcon: Icon code is null or empty", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Log.e("WeatherData", "Weather array is empty or missing");
                                Toast.makeText(MainActivity.this, "Error: Weather data is missing", Toast.LENGTH_SHORT).show();
                            }

                            // Find and display city name
                            String cityName = jsonObject.getString("name");
                            country.setText(cityName);
                            fetchPrayerTimes(cityName);

                            // Set current day with date
                            SimpleDateFormat sdf = new SimpleDateFormat("EEEE, MMMM d, yyyy", java.util.Locale.getDefault());
                            String currentDayWithDate = sdf.format(new Date());
                           // day.setText(currentDayWithDate);
                            JSONObject sysObject = jsonObject.getJSONObject("sys");
                            long sunriseTimestamp = sysObject.getLong("sunrise");
                            long sunsetTimestamp = sysObject.getLong("sunset");
                            SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a", java.util.Locale.getDefault());
                            String sunriseTime = timeFormat.format(new Date(sunriseTimestamp * 1000L));
                            String sunsetTime = timeFormat.format(new Date(sunsetTimestamp * 1000L));
                            sunrise.setText(sunriseTime);
                            sunset.setText(sunsetTime);
                            // Find min and max temperature
                            int minTemp = (int) mainObject.getDouble("temp_min");
                            int maxTemp = (int) mainObject.getDouble("temp_max");
                           // uv.setText( minTemp + "°C"+" ~ "+maxTemp+"°C");

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(MainActivity.this, "Error parsing weather data", Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this, "Error: " + error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);
        requestQueue.add(stringRequest);
    }





    private void FindWeatherForecast(double latitude, double longitude) {
        String url = "https://api.openweathermap.org/data/2.5/forecast?lat=" + latitude + "&lon=" + longitude + "&appid=dc1444b71e04120759905ff65a3bc317&units=metric";

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);

                            // Initialize calendar for forecasting
                            Calendar calendar = Calendar.getInstance();
                            SimpleDateFormat dayFormat = new SimpleDateFormat("EEEE, d", java.util.Locale.getDefault());

                            // Initialize arrays to store day and temperature TextViews
                            TextView[] days = {day1, day2, day3, day4, day5, day6};
                            TextView[] minTemps = {mintemp1, mintemp2, mintemp3, mintemp4, mintemp5, mintemp6};
                            TextView[] maxTemps = {maxtemp1, maxtemp2, maxtemp3, maxtemp4, maxtemp5, maxtemp6};
                            ImageView[] weatherIcons = {icon1, icon2, icon3, icon4, icon5, icon6};

                            // Get forecast data
                            JSONArray listArray = jsonObject.getJSONArray("list");

                            // Loop through the next 6 days
                            for (int i = 0; i < 6; i++) {
                                // Move to the next day
                                calendar.add(Calendar.DAY_OF_YEAR, 1);
                                String dayName = dayFormat.format(calendar.getTime());

                                // Initialize variables for min and max temperatures, and icon selection
                                double minTemp = Double.POSITIVE_INFINITY;  // Initialize with a very high value
                                double maxTemp = Double.NEGATIVE_INFINITY;  // Initialize with a very low value
                                String selectedIconCode = null;  // Icon closest to noon

                                boolean temperatureFound = false;  // Flag to check if temperatures are found

                                for (int j = 0; j < listArray.length(); j++) {
                                    JSONObject dayObject = listArray.getJSONObject(j);
                                    String dateText = dayObject.getString("dt_txt");

                                    // Check if this forecast entry corresponds to the current day
                                    if (dateText.startsWith(new SimpleDateFormat("yyyy-MM-dd").format(calendar.getTime()))) {
                                        JSONObject dayMainObject = dayObject.getJSONObject("main");
                                        int tempMin = (int) dayMainObject.getDouble("temp_min");
                                        int tempMax = (int) dayMainObject.getDouble("temp_max");

                                        // Update min and max temperatures
                                        if (tempMin < minTemp) {
                                            minTemp = tempMin;
                                        }
                                        if (tempMax > maxTemp) {
                                            maxTemp = tempMax;
                                        }

                                        // Get the weather icon code closest to 12:00 PM
                                        String timePart = dateText.split(" ")[1];
                                        if (timePart.startsWith("12:00")) {
                                            JSONArray weatherArray = dayObject.getJSONArray("weather");
                                            if (weatherArray.length() > 0) {
                                                selectedIconCode = weatherArray.getJSONObject(0).getString("icon");
                                            }
                                        }

                                        temperatureFound = true;  // Mark that we have found temperatures
                                    }
                                }

                                // Only update the UI if valid temperatures were found
                                if (temperatureFound) {
                                    days[i].setText(dayName);
                                    minTemps[i].setText("Min: " + String.format("%.1f", minTemp) + "°C");
                                    maxTemps[i].setText("Max: " + String.format("%.1f", maxTemp) + "°C");

                                    // Load the weather icon using Glide
                                    if (selectedIconCode != null) {
                                        String iconUrl = "https://openweathermap.org/img/wn/" + selectedIconCode + "@2x.png";
                                        Glide.with(MainActivity.this)
                                                .load(iconUrl)
                                                .into(weatherIcons[i]);
                                    }
                                } else {
                                    // Handle the case where no temperatures were found
                                    days[i].setText(dayName);
                                    minTemps[i].setText("Min: N/A");
                                    maxTemps[i].setText("Max: N/A");
                                }
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(MainActivity.this, "Error parsing weather data", Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this, "Error: " + error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);
        requestQueue.add(stringRequest);
    }


    private void fetchWeatherData(double latitude, double longitude) {
        // Construct the URL
        String url = "https://api.openweathermap.org/data/2.5/forecast?lat=" + latitude + "&lon=" + longitude + "&appid=dc1444b71e04120759905ff65a3bc317&units=metric";

        // Make the request
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            JSONArray listArray = jsonObject.getJSONArray("list");

                            // Clear previous data
                            weatherItemList.clear();

                            // Setup date formats
                            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", java.util.Locale.getDefault());
                            SimpleDateFormat outputFormat = new SimpleDateFormat("HH:mm", java.util.Locale.getDefault());
                            SimpleDateFormat dayFormat = new SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault());

                            // Get current day
                            String currentDay = dayFormat.format(new Date());

                            // Loop through forecast data
                            for (int i = 0; i < listArray.length(); i++) {
                                JSONObject dayObject = listArray.getJSONObject(i);
                                String dateText = dayObject.getString("dt_txt");

                                // Extract temperature
                                JSONObject mainObject = dayObject.getJSONObject("main");
                                int temp = (int) mainObject.getDouble("temp");

                                // Get weather icon code
                                JSONArray weatherArray = dayObject.getJSONArray("weather");
                                String iconCode = weatherArray.getJSONObject(0).getString("icon");
                                String iconUrl = "https://openweathermap.org/img/wn/" + iconCode + "@2x.png";

                                // Extract rain data if available
                                int rainPercentage = 0;
                                if (dayObject.has("rain")) {
                                    JSONObject rainObject = dayObject.getJSONObject("rain");
                                    rainPercentage = (int) rainObject.optDouble("1h", 0);
                                }

                                // Parse the date and time
                                Date date = inputFormat.parse(dateText);
                                String time = outputFormat.format(date);
                                String day = dayFormat.format(date);

                                // Log parsed time and day
                                Log.d("WeatherData", "Parsed Time: " + time + ", Day: " + day);

                                // Add all intervals for today to the list
                                if (day.equals(currentDay)) {
                                    weatherItemList.add(new WeatherForecast(time, temp, iconUrl, rainPercentage));
                                }
                            }

                            // Notify adapter to update UI
                            weatherAdapter.notifyDataSetChanged();

                        } catch (JSONException | ParseException e) {
                            e.printStackTrace();
                            Toast.makeText(MainActivity.this, "Error parsing weather data", Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // Detailed error handling
                String errorMessage = "Unknown error";
                if (error != null) {
                    if (error.networkResponse != null) {
                        errorMessage = "Error code: " + error.networkResponse.statusCode;
                    } else if (error.getMessage() != null) {
                        errorMessage = error.getMessage();
                    }
                }
                Toast.makeText(MainActivity.this, "Error: " + errorMessage, Toast.LENGTH_SHORT).show();
            }
        });

        // Add the request to the RequestQueue
        RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);
        requestQueue.add(stringRequest);
    }


}

