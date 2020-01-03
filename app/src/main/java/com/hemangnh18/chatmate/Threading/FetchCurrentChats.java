package com.hemangnh18.chatmate.Threading;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.hemangnh18.chatmate.Classes.DisplayRecent;
import com.hemangnh18.chatmate.Classes.SocketMessage;
import com.hemangnh18.chatmate.Classes.User;
import com.hemangnh18.chatmate.Database.ChatMessagesHandler;
import com.hemangnh18.chatmate.Database.DatabaseHandler;
import com.hemangnh18.chatmate.Database.MidChatHelper;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FetchCurrentChats extends ViewModel {

    private MutableLiveData<ArrayList<DisplayRecent>> messagesList = new MutableLiveData<>();
    private DatabaseHandler databaseHandler;
    private MidChatHelper midChatHelper;
    private static final String TABLE_CONTACTS = "MidChat";


    public FetchCurrentChats(Context context) {
        midChatHelper = new MidChatHelper(context);
        databaseHandler = new DatabaseHandler(context);
        init();
    }

    public void init()
    {
        final ArrayList<DisplayRecent> userList = new ArrayList<>();
        final SQLiteDatabase db = midChatHelper.getWritableDatabase();
        final String selectQuery = "SELECT  * FROM " + TABLE_CONTACTS;

        final ExecutorService service =  Executors.newSingleThreadExecutor();
        service.submit(new Runnable() {
            @Override
            public void run() {

                Cursor cursor = db.rawQuery(selectQuery, null);

                if (cursor.moveToFirst()) {
                    do {

                        User user = databaseHandler.getUser(cursor.getString(1));
                        DisplayRecent displayRecent = new DisplayRecent(cursor.getString(1),cursor.getString(2),cursor.getString(3),cursor.getInt(4)==1);
                        displayRecent.setBase64(user.getBASE64());
                        displayRecent.setUsername(user.getUSERNAME_IN_PHONE());
                        userList.add(displayRecent);

                    } while (cursor.moveToNext());
                }

                messagesList.postValue(userList);
                service.shutdown();
                db.close();

            }
        });

    }

    public LiveData<ArrayList<DisplayRecent>> getElapsedTime() {
        return messagesList;
    }

}