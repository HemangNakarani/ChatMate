package com.hemangnh18.chatmate.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.hemangnh18.chatmate.Classes.ContactDb;
import com.hemangnh18.chatmate.Classes.User;

import java.util.ArrayList;
import java.util.List;


public class DatabaseHandler extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "Contacts.db";
    private static final String TABLE_CONTACTS = "contacts";
    private static final String TABLE_ROOM = "ROOM";
    private static final String KEY_ID = "id";
    private static final String KEY_USERNAME = "USERNAME";
    private static final String KEY_USERNAME_IN_PHONE = "USERNAME_IN_PHONE";
    private static final String KEY_PHONE = "PHONE";
    private static final String KEY_USER_ID = "USER_ID";
    private static final String KEY_DOWNLOAD = "DOWNLOAD";
    private static final String KEY_BASE64 = "BASE64";
    private static final String KEY_STATUS = "STATUS";
    private static final String KEY_GENDER = "GENDER";
    private static final String KEY_ROOM = "ROOM";

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables  
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_CONTACTS_TABLE = "CREATE TABLE " + TABLE_CONTACTS + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_USERNAME + " TEXT,"
                + KEY_USERNAME_IN_PHONE + " TEXT,"
                + KEY_PHONE + " TEXT,"
                + KEY_USER_ID + " TEXT,"
                + KEY_DOWNLOAD + " TEXT,"
                + KEY_BASE64 + " TEXT,"
                + KEY_STATUS + " TEXT,"
                + KEY_GENDER + " TEXT" + ")";

        String CREATE_ROOM_TABLE = "CREATE TABLE " + TABLE_ROOM + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_PHONE + " TEXT,"
                + KEY_ROOM + " TEXT" + ")";

        db.execSQL(CREATE_CONTACTS_TABLE);
        db.execSQL(CREATE_ROOM_TABLE);
    }



    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CONTACTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ROOM);

        onCreate(db);
    }

    public void addUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_USERNAME, user.getUSERNAME());
        values.put(KEY_USERNAME_IN_PHONE, user.getUSERNAME_IN_PHONE());
        values.put(KEY_PHONE, user.getPHONE());
        values.put(KEY_USER_ID, user.getUSER_ID());
        values.put(KEY_DOWNLOAD, user.getDOWNLOAD());
        values.put(KEY_BASE64, user.getBASE64());
        values.put(KEY_STATUS, user.getSTATUS());
        values.put(KEY_GENDER, user.getGENDER());

        db.insert(TABLE_CONTACTS, null, values);
        db.close();
    }

    public void addUserRoom(String number,String roomid) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_PHONE, number);
        values.put(KEY_ROOM, roomid);

        db.insert(TABLE_ROOM, null, values);
        db.close();
    }





    public ArrayList<User> getAllUsers() {

        ArrayList<User> userList = new ArrayList<User>();

        String selectQuery = "SELECT  * FROM " + TABLE_CONTACTS;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);


        if (cursor.moveToFirst()) {
            do {

                User user =new User();
                user.setUSERNAME(cursor.getString(1));
                user.setUSERNAME_IN_PHONE(cursor.getString(2));
                user.setPHONE(cursor.getString(3));
                user.setUSER_ID(cursor.getString(4));
                user.setDOWNLOAD(cursor.getString(5));
                user.setBASE64(cursor.getString(6));
                user.setSTATUS(cursor.getString(7));
                user.setGENDER(cursor.getString(8));
                userList.add(user);

            } while (cursor.moveToNext());
        }
        db.close();
        return userList;
    }


   public void DeleteAll(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from "+ TABLE_CONTACTS);
        db.close();
    }

    public String getRoom(String number) {

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_ROOM, new String[] { KEY_ROOM }, KEY_PHONE + "=?",
                new String[] { String.valueOf(number) }, null, null, null, null);

        try
        {
            cursor.moveToFirst();
            return cursor.getString(0);
        }
        catch (NullPointerException e)
        {
            e.printStackTrace();
            return "";
        }
        catch (CursorIndexOutOfBoundsException e)
        {
            e.printStackTrace();
            return "";
        }

    }

/*
    public void deleteContact(Student contact) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_CONTACTS, KEY_ID + " = ?",
                new String[] { String.valueOf(contact.get_id())});
        db.close();
    }*/

    /*public void updateRoom(User user,String room) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_ROOM, room);
        db.update(TABLE_CONTACTS, values, KEY_PHONE + " = ?",
                new String[] { String.valueOf(user.getPHONE()) });
        db.close();

    }*/
}  