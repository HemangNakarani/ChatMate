package com.hemangnh18.chatmate.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
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
    private static final String KEY_ID = "id";
    private static final String KEY_USERNAME = "USERNAME";
    private static final String KEY_PHONE = "PHONE";
    private static final String KEY_USER_ID = "USER_ID";
    private static final String KEY_DOWNLOAD = "DOWNLOAD";
    private static final String KEY_STATUS = "STATUS";
    private static final String KEY_GENDER = "GENDER";

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables  
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_CONTACTS_TABLE = "CREATE TABLE " + TABLE_CONTACTS + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_USERNAME + " TEXT,"
                + KEY_PHONE + " TEXT,"
                + KEY_USER_ID + " TEXT,"
                + KEY_DOWNLOAD + " TEXT,"
                + KEY_STATUS + " TEXT,"
                + KEY_GENDER + " TEXT" + ")";
        db.execSQL(CREATE_CONTACTS_TABLE);
    }



    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CONTACTS);
        onCreate(db);
    }

    public void addUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_USERNAME, user.getUSERNAME());
        values.put(KEY_PHONE, user.getPHONE());
        values.put(KEY_USER_ID, user.getUSER_ID());
        values.put(KEY_DOWNLOAD, user.getDOWNLOAD());
        values.put(KEY_STATUS, user.getSTATUS());
        values.put(KEY_GENDER, user.getGENDER());

        db.insert(TABLE_CONTACTS, null, values);
        db.close();
    }



    public ArrayList<User> getAllUsers() {

        ArrayList<User> userList = new ArrayList<User>();
        /*SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.query(TABLE_CONTACTS, new String[] { KEY_ID,
                        KEY_NAME, KEY_PH_NO }, KEY_NAME + "=?",
                new String[] { "hemang" }, null, null, null, null);
*/


        String selectQuery = "SELECT  * FROM " + TABLE_CONTACTS;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);


        if (cursor.moveToFirst()) {
            do {

                User user =new User();
                user.setUSERNAME(cursor.getString(1));
                user.setPHONE(cursor.getString(2));
                user.setUSER_ID(cursor.getString(3));
                user.setDOWNLOAD(cursor.getString(4));
                user.setSTATUS(cursor.getString(5));
                user.setGENDER(cursor.getString(6));
                userList.add(user);

            } while (cursor.moveToNext());
        }
        return userList;
    }


   public void DeleteAll(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from "+ TABLE_CONTACTS);
        db.close();
    }

    /*ContactDb getContact(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_CONTACTS, new String[] { KEY_ID,
                        KEY_NAME, KEY_PH_NO }, KEY_ID + "=?",
                new String[] { String.valueOf(id) }, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        Student contact = new Student(cursor.getString(1), cursor.getString(2));
        // return contact
        return contact;
    }*/

/*
    public void deleteContact(Student contact) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_CONTACTS, KEY_ID + " = ?",
                new String[] { String.valueOf(contact.get_id())});
        db.close();
    }*/

   /* public void updateContact(Student contact) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NAME, contact.getName());
        values.put(KEY_PH_NO, contact.getPhone());

        // updating row
        db.update(TABLE_CONTACTS, values, KEY_ID + " = ?",
                new String[] { String.valueOf(contact.get_id()) });

    }*/
}  