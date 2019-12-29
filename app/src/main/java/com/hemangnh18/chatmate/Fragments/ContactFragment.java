package com.hemangnh18.chatmate.Fragments;


import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hemangnh18.chatmate.Adapters.ContactUserAdapter;
import com.hemangnh18.chatmate.Classes.ContactDb;
import com.hemangnh18.chatmate.Classes.Methods;
import com.hemangnh18.chatmate.Classes.User;
import com.hemangnh18.chatmate.Database.DatabaseHandler;
import com.hemangnh18.chatmate.R;
import com.hemangnh18.chatmate.Threading.ContactsMatching;
import com.hemangnh18.chatmate.Threading.ContactsMatchingFactory;

import java.util.ArrayList;
import java.util.Collections;

/**
 * A simple {@link Fragment} subclass.
 */
public class ContactFragment extends Fragment {

    private RecyclerView recyclerView;
    private ContactUserAdapter contactUserAdapter;
    private ArrayList<User> contacts;
    private ContactsMatching contactsMatching;
    public ContactFragment() {
        // Required empty public constructor
    }

    //TODO : SEARCHBAR in RecyclerView,All items in recycler view are sorted according to username.



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_contact, container, false);
        recyclerView =  view.findViewById(R.id.recycler_contacts);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(llm);
        contacts = new ArrayList<>();
        contactUserAdapter = new ContactUserAdapter(getContext(),contacts);
        recyclerView.setAdapter(contactUserAdapter);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED ) {
            requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, 404);
        }
        else
        {
            if(hasConnection()) {
                ContactsMatchingFactory factory = new ContactsMatchingFactory(getContext());
                contactsMatching = ViewModelProviders.of(this, factory).get(ContactsMatching.class);
                subscribe();
            }
            else
            {
                Toast.makeText(getContext(),"No Connection",Toast.LENGTH_LONG).show();
                    DatabaseHandler handler = new DatabaseHandler(getContext());
                    contacts.clear();
                    contacts.addAll(handler.getAllUsers());
                    Collections.sort(contacts,Methods.c);
                    contactUserAdapter.notifyDataSetChanged();
            }

        }

        return view;
    }


    private void subscribe() {
        final Observer<ArrayList<User>> elapsedTimeObserver = new Observer<ArrayList<User>>() {
            @Override
            public void onChanged(@Nullable final ArrayList<User> aLong) {

                contacts.clear();
                contacts.addAll(aLong);
                Collections.sort(contacts,Methods.c);
                contactUserAdapter.notifyDataSetChanged();
            }
        };

        contactsMatching.getElapsedTime().observe(this, elapsedTimeObserver);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 404) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED )
            {
                ContactsMatchingFactory factory = new ContactsMatchingFactory(getContext());
                contactsMatching = ViewModelProviders.of(this,factory).get(ContactsMatching.class);
                if(hasConnection()) {
                    subscribe();
                }

            }
            else
            {
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", getActivity().getPackageName(), null);
                intent.setData(uri);
                startActivity(intent);
                //getActivity().finish();
            }
        }
    }


    boolean hasConnection()
    {
        try {
            ConnectivityManager cm =
                    (ConnectivityManager)getContext().getSystemService(Context.CONNECTIVITY_SERVICE);

            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            boolean isConnected = activeNetwork != null &&
                    activeNetwork.isConnectedOrConnecting();

            if(isConnected) {return  true;}
            else {return false;}

        }
        catch (NullPointerException e)
        {
            e.printStackTrace();
            return  false;
        }

    }

}
