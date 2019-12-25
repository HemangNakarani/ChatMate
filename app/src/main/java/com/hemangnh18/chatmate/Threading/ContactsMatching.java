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
import com.hemangnh18.chatmate.Classes.User;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ContactsMatching extends ViewModel {

    private MutableLiveData<ArrayList<User>> mContactsList = new MutableLiveData<>();
    private ArrayList<User> UserList = new ArrayList<>();
    private long mInitialTime;
    private Context context;

    public ContactsMatching(Context context) {
        this.context = context;
        init();
    }

    public void init()
    {

        final Map<String, String> namePhoneMap = new HashMap<String, String>();
        final HashMap<String,User> linkmap = new HashMap<>();
        final TreeSet<User> userSet = new TreeSet<>(new Comparator<User>(){
            @Override
            public int compare(User s1, User s2)
            {
                try {
                    return s1.getUSERNAME().toLowerCase().compareTo(s2.getUSERNAME().toLowerCase());
                }catch (NullPointerException e)
                {
                    e.printStackTrace();
                    return 1;

                }

            }
        });
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
                    namePhoneMap.put(phoneNumber, name);
                    Log.e("TAG>>>>",phoneNumber+" "+name);
                }

                phones.close();
                service.shutdown();

                final DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
                reference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        UserList.clear();
                        for(DataSnapshot snapshot:dataSnapshot.getChildren())
                        {

                            User user = snapshot.getValue(User.class);
                            assert user != null;
                            user.setUSER_ID(snapshot.getKey());
                            SharedPreferences preferences = context.getSharedPreferences("UserInfo",Context.MODE_PRIVATE);
                            String json= preferences.getString("User","");
                            Gson gson =new Gson();
                            User pote= gson.fromJson(json,User.class);
                            if(namePhoneMap.containsKey(user.getPHONE()) && !user.getPHONE().equals(pote.getPHONE()))
                            {
                                linkmap.put(snapshot.getKey(),user);
                                userSet.add(user);

                            }
                        }

                        UserList.clear();
                        UserList.addAll(userSet);
                        mContactsList.postValue(UserList);
                        reference.removeEventListener(this);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });
            }
        });

    }

    public LiveData<ArrayList<User>> getElapsedTime() {
        return mContactsList;
    }

}