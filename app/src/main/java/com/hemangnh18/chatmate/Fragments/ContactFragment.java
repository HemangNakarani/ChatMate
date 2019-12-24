package com.hemangnh18.chatmate.Fragments;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hemangnh18.chatmate.Adapters.ContactUserAdapter;
import com.hemangnh18.chatmate.Classes.User;
import com.hemangnh18.chatmate.R;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class ContactFragment extends Fragment {

    private RecyclerView recyclerView;
    private ContactUserAdapter contactUserAdapter;
    private ArrayList<User> contacts;
    public ContactFragment() {
        // Required empty public constructor
    }


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

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for(DataSnapshot snapshot:dataSnapshot.getChildren())
                {
                    User user= snapshot.getValue(User.class);
                    contacts.add(user);
                    contacts.add(user);
                    contacts.add(user);
                    contacts.add(user);
                    contacts.add(user);
                    contacts.add(user);
                    contacts.add(user);
                    contacts.add(user);
                }
                contactUserAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        return view;
    }

}
