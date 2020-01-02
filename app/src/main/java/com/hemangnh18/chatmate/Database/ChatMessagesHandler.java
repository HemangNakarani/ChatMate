package com.hemangnh18.chatmate.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.hemangnh18.chatmate.Classes.ContactDb;
import com.hemangnh18.chatmate.Classes.SocketMessage;
import com.hemangnh18.chatmate.Classes.User;

import java.util.ArrayList;
import java.util.List;


public class ChatMessagesHandler extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "Messages.db";
    private static final String TABLE_CHATS = "contacts";
    private static final String KEY_ID = "id";
    private static final String SENDER = "SENDER";
    private static final String RECIEVER = "RECIEVER";
    private static final String TIME = "TIME";
    private static final String ROOM = "ROOM";
    private static final String MESSAGE = "MESSAGE";
    private static final String TYPE = "TYPE";
    private static final String READ = "READ";

    public ChatMessagesHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_MESSAGES_TABLE = "CREATE TABLE " + TABLE_CHATS + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + MESSAGE + " TEXT,"
                + SENDER + " TEXT,"
                + RECIEVER + " TEXT,"
                + ROOM + " TEXT,"
                + TYPE + " TEXT,"
                + TIME + " TEXT,"
                + READ + " INTEGER"+ ")";

        db.execSQL(CREATE_MESSAGES_TABLE);
    }



    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CHATS);

        onCreate(db);
    }

    public void addMessage(SocketMessage message) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(MESSAGE, message.getMessage());
        values.put(SENDER, message.getSender());
        values.put(RECIEVER, message.getReciever());
        values.put(ROOM, message.getRoom());
        values.put(TYPE, message.getType());
        values.put(TIME, message.getTime());
        values.put(READ, message.getRead());

        db.insert(TABLE_CHATS, null, values);
        db.close();
    }


    public ArrayList<SocketMessage> getAllMessages(String id) {

        ArrayList<SocketMessage> messagesList = new ArrayList<>();

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.query(TABLE_CHATS, null, ROOM + "=?",
                new String[] { id }, null, null, null, null);


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
                messagesList.add(socketMessage);

            } while (cursor.moveToNext());
        }
        db.close();
        return messagesList;
    }


    public void DeleteAll(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from "+ TABLE_CHATS);
        db.close();
    }



   /* public User getUser(String id) {

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_CONTACTS, null, KEY_USER_ID + "=?",
                new String[] { String.valueOf(id) }, null, null, null, null);

        try
        {
            cursor.moveToFirst();
            User user = new User();
            user.setUSERNAME(cursor.getString(1));
            user.setUSERNAME_IN_PHONE(cursor.getString(2));
            user.setPHONE(cursor.getString(3));
            user.setUSER_ID(cursor.getString(4));
            user.setDOWNLOAD(cursor.getString(5));
            user.setBASE64(cursor.getString(6));
            user.setSTATUS(cursor.getString(7));
            user.setGENDER(cursor.getString(8));
            user.setTOKEN(cursor.getString(9));

            return user;
        }
        catch (NullPointerException e)
        {
            e.printStackTrace();
            return new User();
        }
        catch (CursorIndexOutOfBoundsException e)
        {
            e.printStackTrace();
            return new User();
        }

    }*/

}