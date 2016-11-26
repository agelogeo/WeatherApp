package com.myapp.mya2;

import android.app.NotificationManager;
import android.content.Context;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

/**
 * Created by Admin on 22/7/2015.
 */
public class NotificationPackage {
    NotificationCompat.Builder notification;
    private static final int uniqueID = 45612;
    Context context;

    public NotificationPackage(Context context){
        this.context=context;
        Toast.makeText(context,"Notification", Toast.LENGTH_SHORT);
        setupNotfication();
    }

    public void setupNotfication(){
        Uri sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        notification = new NotificationCompat.Builder(context);
        notification.setAutoCancel(true);
        notification.setContentTitle("Here is the title !!");
        notification.setContentText("This is the body text!");
        //notification.setContentIntent(pIntent);
        notification.setSound(sound);
        notification.setSmallIcon(R.mipmap.ic_launcher);
        notification.setTicker("This is the ticker!");
        notification.setWhen(System.currentTimeMillis());

        //Builds notification and issues it
        NotificationManager nM = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        nM.notify(uniqueID, notification.build());

    }
}
