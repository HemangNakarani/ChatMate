package com.hemangnh18.chatmate.Threading;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.hemangnh18.chatmate.Classes.SocketMessage;
import com.hemangnh18.chatmate.Database.ChatMessagesHandler;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FetchMessages extends ViewModel {

    private MutableLiveData<ArrayList<SocketMessage>> messagesList = new MutableLiveData<>();
    private ChatMessagesHandler chatMessagesHandler;
    private Context context;
    private String id;
    private static final String TABLE_CHATS = "contacts";
    private static final String ROOM = "ROOM";


    public FetchMessages(Context context,String id) {
        this.context = context;
        this.id = id;
        chatMessagesHandler = new ChatMessagesHandler(context);
        init();
    }

    public void init()
    {

        final ArrayList<SocketMessage> msgs = new ArrayList<>();
        final SQLiteDatabase db = chatMessagesHandler.getWritableDatabase();
        final Cursor cursor = db.query(TABLE_CHATS, null, ROOM + "=?",
                new String[] { id }, null, null, null, null);

        final ExecutorService service =  Executors.newSingleThreadExecutor();
        service.submit(new Runnable() {
            @Override
            public void run() {

                if (cursor.moveToFirst()) {
                    do {

                        SocketMessage socketMessage = new SocketMessage();
                        socketMessage.setMessage(cursor.getString(1));
                        socketMessage.setSender(cursor.getString(2));
                        socketMessage.setReciever(cursor.getString(3));
                        socketMessage.setRoom(cursor.getString(4));
                        socketMessage.setType(cursor.getString(5));
                        socketMessage.setTime(cursor.getString(6));
                        socketMessage.setRead(cursor.getInt(7));
                        msgs.add(socketMessage);

                    } while (cursor.moveToNext());
                }

                messagesList.postValue(msgs);
                service.shutdown();
                db.close();

            }
        });

    }

    public LiveData<ArrayList<SocketMessage>> getElapsedTime() {
        return messagesList;
    }

}