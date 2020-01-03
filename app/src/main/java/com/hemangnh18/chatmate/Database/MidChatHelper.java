package com.hemangnh18.chatmate.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.hemangnh18.chatmate.Classes.FirstChatUser;

import org.greenrobot.eventbus.EventBus;

public class MidChatHelper extends SQLiteOpenHelper {


    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "MidChat.db";
    private static final String TABLE_CONTACTS = "MidChat";
    private static final String KEY_ID = "id";
    private static final String KEY_USER_ID = "USER_ID";
    private static final String KEY_LAST_MSG = "LAST_MSG";
    private Context context;


    public MidChatHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context =context;
    }


    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_CONTACTS_TABLE = "CREATE TABLE " + TABLE_CONTACTS + "("
                + KEY_ID + " INTEGER PRIMARY KEY,"
                + KEY_USER_ID + " TEXT,"
                + KEY_LAST_MSG + " TEXT" + ")";


        db.execSQL(CREATE_CONTACTS_TABLE);
    }



    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CONTACTS);
        onCreate(db);
    }

    public void addUser(String id) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_USER_ID, id);
        db.insert(TABLE_CONTACTS, null, values);
        db.close();
    }


    public void Exists(String id)
    {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_CONTACTS, null, KEY_USER_ID + "=?",
                new String[] { String.valueOf(id) }, null, null, null, null);

        if(!cursor.moveToFirst())
        {
            Log.e("ADDDEEEED",id);
            EventBus.getDefault().post(new FirstChatUser(id));
            addUser(id);
        }
    }




   /* public ArrayList<User> getAllMidChats() {

        ArrayList<User> userList = new ArrayList<User>();
        DatabaseHandler databaseHandler= new DatabaseHandler(context);
        String selectQuery = "SELECT  * FROM " + TABLE_CONTACTS;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);


        if (cursor.moveToFirst()) {
            do {

                userList.add(databaseHandler.getUser(cursor.getString(1)));

            } while (cursor.moveToNext());
        }
        db.close();
        return userList;
    }*/

    public void DeleteAll(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from "+ TABLE_CONTACTS);
        db.close();
    }


}
