package com.myapp.mya2;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by jcity_000
 * on 12/27/13.
 */

public class WeatherServiceAsync extends AsyncTask<String, Void, String> {
    private final MainActivity WeatherActivity;
    Bitmap bitmap;

// this constructor takes the activity as the parameter.
// that way we can use the activity later to populate the weather value fields
// on the screen
    public WeatherServiceAsync(MainActivity weatherActivity) {
        this.WeatherActivity = weatherActivity;
    }

    @Override
    protected String doInBackground(String... urls) {
// this weather service method will be called after the service executes.
// it will run as a seperate process, and will populate the activity in the onPostExecute
// method below

        String response = "";
// loop through the urls (there should only be one!) and call an http Get using the URL passed
// to this service

        for (String url : urls) {
            System.out.println("Check #1");
            DefaultHttpClient client = new DefaultHttpClient();
            HttpGet httpGet = new HttpGet(url);
            try {
// make the http request for the weather data
                HttpResponse execute = client.execute(httpGet);

// get the content of the result returned when the response comes back
// it should be a json object
                InputStream content = execute.getEntity().getContent();

                BufferedReader buffer = new BufferedReader(new InputStreamReader(content));
                String s;

// populate the response string which will be passed later into the post execution
                while ((s = buffer.readLine()) != null) {
                    response += s;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return response;
    }

    @Override
    protected void onPostExecute(String result) {
        System.out.println("Check #2");
        try {
            // parse the json result returned from the service
            JSONObject jsonResult = new JSONObject(result);

            String city = jsonResult.getJSONObject("city").getString("name");
            String country = jsonResult.getJSONObject("city").getString("country");
            System.out.println(city);
            System.out.println(country);
            // parse out the temperature from the JSON result
            double temperature = jsonResult.getJSONArray("list").getJSONObject(0).getJSONObject("temp").getDouble("day");
            System.out.println(temperature);
            // parse out the pressure from the JSON Result
            double pressure = jsonResult.getJSONArray("list").getJSONObject(0).getDouble("pressure");
            System.out.println(pressure);
            // parse out the humidity from the JSON result
            double humidity = jsonResult.getJSONArray("list").getJSONObject(0).getDouble("humidity");
            System.out.println(humidity);
            // parse out the description from the JSON result
            String description = jsonResult.getJSONArray("list").getJSONObject(0).getJSONArray("weather").getJSONObject(0).getString("description");
            final String icon = jsonResult.getJSONArray("list").getJSONObject(0).getJSONArray("weather").getJSONObject(0).getString("icon");

            Runnable r = new Runnable() {
                @Override
                public void run() {
                    try{
                        WeatherActivity.SetBitmap(getImageBitmap("http://openweathermap.org/img/w/"+icon+".png"));
                        System.out.println(bitmap);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            };

            Thread myThread = new Thread(r);
            myThread.start();
            System.out.println("Check #3");

            this.WeatherActivity.setCityNameAndCountry(city, country);
            this.WeatherActivity.SetDescription(description);
            this.WeatherActivity.SetTemperature(temperature);
            this.WeatherActivity.SetPressure(pressure);
            this.WeatherActivity.SetHumidity(humidity);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private Bitmap getImageBitmap(String src) {
        Bitmap bm = null;
        try {
            URL aURL = new URL(src);
            URLConnection conn = aURL.openConnection();
            conn.connect();
            InputStream is = conn.getInputStream();
            BufferedInputStream bis = new BufferedInputStream(is);
            bm = BitmapFactory.decodeStream(bis);
            bis.close();
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return bm;
    }
}
