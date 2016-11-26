package com.myapp.mya2;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

/**
 * Created by Admin on 26/7/2015.
 */
public class SplashScreen extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splashscreen);

        Runnable r = new Runnable() {
            @Override
            public void run() {
                try{
                    Thread.sleep(2500);
                    Intent i = new Intent(SplashScreen.this,MainActivity.class);
                    startActivity(i);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        };
        Thread myThread = new Thread(r);
        myThread.start();

    }
}
