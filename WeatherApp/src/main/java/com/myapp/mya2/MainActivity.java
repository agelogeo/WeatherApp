package com.myapp.mya2;


import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.Settings;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AlertDialog;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;

public class MainActivity extends ActionBarActivity {
    private static final String apikey = "36f9c425ce97b80906ce6c00b96bb0fb";
    private static final int reminder = 24*60*60*1000;
    private static final int delayID= 30*1000;
    private static boolean alarmFlag = false;
    Button btnShowLocation;
    GPSTracker gps;
    Intent intentAlarm;
    AlarmManager alarmManager;
    PendingIntent pIntent;
    protected ConnectivityManager connManager;
    private AlarmManager alarmMgr;
    private PendingIntent alarmIntent;
    private Context context = MainActivity.this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnShowLocation = (Button) findViewById(R.id.testButton);

        btnShowLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gps = new GPSTracker(MainActivity.this);
                if (gps.canGetLocation()) {
                    double latitude = gps.getLatitude();
                    double longitude = gps.getLongitude();
                    setLatitude(latitude);
                    setLongitude(longitude);

                    //String url = "http://api.openweathermap.org/data/2.5/weather?lat="+latitude+"&lon="+longitude;
                    String url = "http://api.openweathermap.org/data/2.5/forecast/daily?lat=" + latitude + "&lon=" + longitude + "&cnt=1&mode=json&units=metric&APPID=" + apikey;

                    System.out.println("Check #0");
                    connManager = (ConnectivityManager) MainActivity.this.getSystemService(Context.CONNECTIVITY_SERVICE);

                    if (connManager.getActiveNetworkInfo() != null && connManager.getActiveNetworkInfo().isAvailable() && connManager.getActiveNetworkInfo().isConnected()) {
                        new WeatherServiceAsync(MainActivity.this).execute(url);
                    } else {
                        showNetworkAlert();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Error while trying to connect to the server", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        /*
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);*/

        switch (item.getItemId()) {
            case R.id.my_profile:
                if (item.isChecked())
                    item.setChecked(false);
                else
                    item.setChecked(true);
                Toast.makeText(MainActivity.this, "My Profile", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.preferences:
                if(item.isChecked())
                    item.setChecked(false);
                else
                    item.setChecked(true);
                Toast.makeText(MainActivity.this,"Preferences",Toast.LENGTH_SHORT).show();
                return true;
            case R.id.options:
                if(item.isChecked())
                    item.setChecked(false);
                else
                    item.setChecked(true);
                Toast.makeText(MainActivity.this,"Options",Toast.LENGTH_SHORT).show();
                return true;
            case R.id.enable:
                if(item.isChecked())
                    item.setChecked(false);
                else
                    item.setChecked(true);
                startAlarmForNotification();
                return true;
            case R.id.disable:
                if(item.isChecked())
                    item.setChecked(false);
                else
                    item.setChecked(true);
                stopAlarmForNotification();
                return true;
            default:
                Toast.makeText(MainActivity.this,"Nothing!!",Toast.LENGTH_SHORT).show();
                return super.onOptionsItemSelected(item);

        }


    }

    public void SetDescription(String description) {
        TextView descriptionValue = (TextView) findViewById(R.id.descValue);
        descriptionValue.setText(description);
    }

    public void SetTemperature(double temperature) {
        TextView tempValue = (TextView) findViewById(R.id.tempValue);
        DecimalFormat df = new DecimalFormat("##.#");
        tempValue.setText(df.format(temperature)+" °C");
    }

    public void SetPressure(double pressure) {
        TextView pressureValue = (TextView) findViewById(R.id.pressureValue);
        pressureValue.setText(Double.toString(pressure)+" hpa");
    }

    public void SetHumidity(double humidity) {
        TextView humidityValue = (TextView) findViewById(R.id.humidityValue);
        humidityValue.setText(Double.toString(humidity)+" %");

    }

    public void setCityNameAndCountry(String city,String country) {
        TextView cityValue = (TextView) findViewById(R.id.cityValue);
        cityValue.setText(city+","+country);
    }

    public void setLatitude(double latitude){
        TextView latValue = (TextView) findViewById(R.id.latValue);
        latValue.setText(Double.toString(latitude));
    }

    public void setLongitude(double longitude){
        TextView longValue = (TextView) findViewById(R.id.longValue);
        longValue.setText(Double.toString(longitude));
    }

    public void SetBitmap(Bitmap bitmap) {
        ImageView image = (ImageView) findViewById(R.id.imagebox);
        image.setImageBitmap(bitmap);
    }

    public void startAlarmForNotification(){
        if(!alarmFlag) {
            //create an Intent and set the class which will execute when Alarm triggers
            intentAlarm = new Intent(this, AlarmReceiver.class);

            //create the object
            alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

            //set the alarm for particular time
            pIntent= PendingIntent.getBroadcast(this, 0, intentAlarm, PendingIntent.FLAG_UPDATE_CURRENT);
            alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + reminder, reminder, pIntent);
            Toast.makeText(this, "Alarm scheduled", Toast.LENGTH_SHORT).show();
            alarmFlag=true;
            Toast.makeText(MainActivity.this,"Notifications are online.",Toast.LENGTH_SHORT).show();

        }else{
            Toast.makeText(this, "Notifications are already online.", Toast.LENGTH_SHORT).show();
        }

    }

    public void stopAlarmForNotification(){
        if(alarmFlag){
            alarmManager.cancel(pIntent);
            alarmFlag=false;
            Toast.makeText(MainActivity.this,"Notifications are offline.",Toast.LENGTH_SHORT).show();

        }else{
            Toast.makeText(this, "Notifications are already offline.", Toast.LENGTH_SHORT).show();
        }
    }

    public void showNetworkAlert(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);
        alertDialog.setTitle("Network's settings");
        alertDialog.setMessage("WiFi or 3G is not enabled.");
        alertDialog.setPositiveButton("WiFi", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_WIFI_SETTINGS);
                MainActivity.this.startActivity(intent);
            }
        });

        alertDialog.setNegativeButton("3G Data", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_DATA_ROAMING_SETTINGS);
                MainActivity.this.startActivity(intent);
            }
        });

        alertDialog.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        alertDialog.show();
    }
}
