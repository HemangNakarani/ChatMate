package com.hemangnh18.chatmate.Classes;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.vanniktech.emoji.EmojiEditText;

import java.util.Comparator;

public class Methods {

    //TODO GIVEN TIME IS CURRENTTIME IN MILLS, CHANGE IN APPROPRIATE FORM TODAY,YESTERDAY,OR DATE all with time;


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

    public static void CloseKeyboard(Activity activity)
    {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        View view = activity.getCurrentFocus();
        if (view == null) {
            view = new View(activity);
        }

        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static void OpenKeyboard(Activity activity, EmojiEditText text)
    {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.toggleSoftInputFromWindow(text.getApplicationWindowToken(), InputMethodManager.SHOW_FORCED, 0);


    }

    public static Comparator<User> c = new Comparator<User>()
    {
        public int compare(User u1, User u2)
        {
            return u1.getUSERNAME_IN_PHONE().compareTo(u2.getUSERNAME_IN_PHONE());
        }
    };


   public static void getToken()
    {

        if(FirebaseAuth.getInstance().getCurrentUser()!=null)
        {
            FirebaseInstanceId.getInstance().getInstanceId()
                    .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                        @Override
                        public void onComplete(@NonNull Task<InstanceIdResult> task) {
                            if (!task.isSuccessful()) {
                                Log.w("FAILED TOKEN", "getInstanceId failed", task.getException());
                                return;
                            }

                            String token = task.getResult().getToken();
                            Log.e("TOKEN",token);
                            FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber()).child("TOKEN").setValue(token);
                        }
                    });
        }
    }

}
