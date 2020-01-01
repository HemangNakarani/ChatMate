package com.hemangnh18.chatmate.FCM;

import android.app.Notification;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.hemangnh18.chatmate.R;

import java.util.Random;

import static com.hemangnh18.chatmate.FCM.ChannelApp.CHANNEL_ID;

public class FirebaseMessageService extends FirebaseMessagingService {

    public static final String GROUP_NOTIFY= "APNA GROUP AAYEGA";

    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);

        Log.e("Token",s);

    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);


            Log.e("MESSAGE",remoteMessage.getData().get("Message")+"    "+ remoteMessage.getData().get("Sender"));

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.emoji_backspace)
                .setGroupSummary(true)
                .setContentTitle(remoteMessage.getData().get("Time"))
                .setContentText(remoteMessage.getData().get("Message"))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .setGroup(GROUP_NOTIFY)
                .build();

        notificationManager.notify((int)System.currentTimeMillis(), notification);

    }
}
