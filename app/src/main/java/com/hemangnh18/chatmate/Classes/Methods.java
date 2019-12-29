package com.hemangnh18.chatmate.Classes;

import android.content.Context;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.provider.ContactsContract;

import java.util.Comparator;


public class Methods {

    public static String getContactName(final String phoneNumber, Context context)
    {
        Uri uri= Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI,Uri.encode(phoneNumber));

        String[] projection = new String[]{ContactsContract.PhoneLookup.DISPLAY_NAME};

        String contactName="";
        Cursor cursor=context.getContentResolver().query(uri,projection,null,null,null);

        if (cursor != null) {
            if(cursor.moveToFirst()) {
                contactName=cursor.getString(0);
            }
            cursor.close();
        }

        return contactName;
    }

    public static boolean hasConnection(Context mContext)
    {
        ConnectivityManager cm =
                (ConnectivityManager)mContext.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();

        if(isConnected) {return  true;}
        else {return false;}
    }


    public static Comparator<User> c = new Comparator<User>()
    {
        public int compare(User u1, User u2)
        {
            return u1.getUSERNAME_IN_PHONE().compareTo(u2.getUSERNAME_IN_PHONE());
        }
    };
}
