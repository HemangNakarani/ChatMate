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
import com.google.gson.Gson;
import com.hemangnh18.chatmate.Classes.SocketMessage;
import com.hemangnh18.chatmate.Database.ChatMessagesHandler;
import com.hemangnh18.chatmate.R;

import java.util.Random;

import static com.hemangnh18.chatmate.FCM.ChannelApp.CHANNEL_ID;

public class FirebaseMessageService extends FirebaseMessagingService {

    public static final String GROUP_NOTIFY= "APNA GROUP AAYEGA";
    public static final int  SUMMARY_ID = 0;

    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);

        Log.e("Token",s);

    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);


        Log.e("MESSAGE FCM",remoteMessage.getData().toString());

        SocketMessage socketMessage= new SocketMessage();

        socketMessage.setMessage(remoteMessage.getData().get("Message"));
        socketMessage.setSender(remoteMessage.getData().get("Sender"));
        socketMessage.setReciever(remoteMessage.getData().get("Reciever"));
        socketMessage.setRoom(remoteMessage.getData().get("Sender"));
        socketMessage.setType(remoteMessage.getData().get("Type"));
        socketMessage.setTime(remoteMessage.getData().get("Time"));

        ChatMessagesHandler chatMessagesHandler = new ChatMessagesHandler(this);
        chatMessagesHandler.addMessage(socketMessage);
        FirebaseDatabase.getInstance().getReference("MsgStatus").child(socketMessage.getSender()).child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue("Delivered");

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.emoji_backspace)
                .setContentTitle(remoteMessage.getData().get("Sender"))
                .setContentText(remoteMessage.getData().get("Message"))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .setGroup(GROUP_NOTIFY)
                .build();

        Notification summaryNotification =
                new NotificationCompat.Builder(this, CHANNEL_ID)
                        .setContentTitle("New Messages")
                        .setSmallIcon(R.drawable.sendicon)
                        .setStyle(new NotificationCompat.InboxStyle())
                        .setGroup(GROUP_NOTIFY)
                        .setGroupSummary(true)
                        .build();

        notificationManager.notify((int)System.currentTimeMillis(), notification);
        notificationManager.notify(SUMMARY_ID, summaryNotification);

    }
}
