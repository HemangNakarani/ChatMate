package com.hemangnh18.chatmate.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.hemangnh18.chatmate.Classes.DisplayRecent;
import com.hemangnh18.chatmate.Classes.FirstChatUser;
import com.hemangnh18.chatmate.Classes.User;

import org.greenrobot.eventbus.EventBus;

public class MidChatHelper extends SQLiteOpenHelper {


    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "MidChat.db";
    private static final String TABLE_CONTACTS = "MidChat";
    private static final String KEY_ID = "id";
    private static final String KEY_USER_ID = "USER_ID";
    private static final String KEY_LAST_MSG = "LAST_MSG";
    private static final String KEY_TIME = "LAST_TIME";
    private static final String KEY_READ = "READ";
    private Context context;


    public MidChatHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context =context;
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_CONTACTS_TABLE = "CREATE TABLE " + TABLE_CONTACTS + "("
                + KEY_ID + " INTEGER PRIMARY KEY,"
                + KEY_USER_ID + " TEXT,"
                + KEY_LAST_MSG + " TEXT,"
                + KEY_TIME + " TEXT,"
                + KEY_READ + " INTEGER" + ")";


        db.execSQL(CREATE_CONTACTS_TABLE);
    }



    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CONTACTS);
        onCreate(db);
    }

    public void addUser(String id,String message,String time) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_USER_ID, id);
        values.put(KEY_LAST_MSG, message);
        values.put(KEY_TIME, time);
        values.put(KEY_READ, 1);
        db.insert(TABLE_CONTACTS, null, values);
        db.close();
    }


    public void Exists(String id,String message,String time)
    {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_CONTACTS, null, KEY_USER_ID + "=?",
                new String[] { String.valueOf(id) }, null, null, null, null);

        if(!cursor.moveToFirst())
        {
            DisplayRecent displayRecent = new DisplayRecent(id,message,time,true);
            EventBus.getDefault().post(displayRecent);
            addUser(id,message,time);
        }
    }


    public void UpdateLast(String id,String message,String time)
    {
        ContentValues args = new ContentValues();
        SQLiteDatabase db= this.getWritableDatabase();
        args.put(KEY_LAST_MSG, message);
        args.put(KEY_READ, time);
        db.update(TABLE_CONTACTS,args, KEY_USER_ID + " = ?",new String[]{id});
        db.close();
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
