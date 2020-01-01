package com.hemangnh18.chatmate.FCM;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

public class ChannelApp extends Application {

    public static final String CHANNEL_ID ="COSTANTLY_NOOBS";

    @Override
    public void onCreate() {
        super.onCreate();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel1 = new NotificationChannel(
                    CHANNEL_ID,
                    "FCM CHANNEL",
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel1.setDescription("MESSAGES THROUGH FCM");
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel1);
        }

    }
}
