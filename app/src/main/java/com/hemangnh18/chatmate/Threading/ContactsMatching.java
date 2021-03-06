package com.hemangnh18.chatmate.Threading;

import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.util.Log;
import android.util.Pair;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.hemangnh18.chatmate.Classes.ContactDb;
import com.hemangnh18.chatmate.Classes.Methods;
import com.hemangnh18.chatmate.Classes.User;
import com.hemangnh18.chatmate.Database.DatabaseHandler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class ContactsMatching extends ViewModel {

    private MutableLiveData<ArrayList<User>> mContactsList = new MutableLiveData<>();
    private ArrayList<User> userList = new ArrayList<>();
    private Context context;

    public ContactsMatching(Context context) {
        this.context = context;
        init();
    }

    public void init()
    {


        final HashSet<ContactDb> arr = new HashSet<>();
        final Map<String, String> namePhoneMap = new HashMap<String, String>();
        final ExecutorService service =  Executors.newSingleThreadExecutor();
        service.submit(new Runnable() {
            @Override
            public void run() {

                final Cursor phones = context.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);

                while (phones.moveToNext()) {
                    String name = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                    String phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                    phoneNumber = phoneNumber.replaceAll("[()\\s-]+", "");
                    if(phoneNumber.length()==10)
                    {
                        phoneNumber="+91"+phoneNumber;
                    }

                    if(phoneNumber.length()==13)
                    {
                        arr.add(new ContactDb(name, phoneNumber));
                        namePhoneMap.put(phoneNumber, name);
                    }
                }

                final DatabaseHandler databaseHandler = new DatabaseHandler(context);

                for (final Map.Entry<String, String> entry : namePhoneMap.entrySet()) {

                    final DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
                    reference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            //Log.e("EXXX",entry.getKey());
                            if (dataSnapshot.hasChild(entry.getKey())) {
                                final DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference("Users").child(entry.getKey());
                                reference1.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                        User user = dataSnapshot.getValue(User.class);
                                        user.setUSERNAME_IN_PHONE(entry.getValue());
                                        userList.add(user);
                                        databaseHandler.UpdateUser(user);
                                        mContactsList.postValue(userList);
                                        Log.e("HELLO?>>>>",user.getUSERNAME_IN_PHONE()+" "+user.getUSER_ID());
                                        reference1.removeEventListener(this);
                                    }
                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
                                reference.removeEventListener(this);
                            } else {
                                Log.e("NO>>>>", "NOOOOO");
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }


                phones.close();
                service.shutdown();

            }
        });

    }

    public LiveData<ArrayList<User>> getElapsedTime() {
        return mContactsList;
    }

}