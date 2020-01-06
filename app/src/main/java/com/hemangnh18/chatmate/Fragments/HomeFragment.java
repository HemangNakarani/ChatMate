package com.hemangnh18.chatmate.Fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.hemangnh18.chatmate.Adapters.ChatListAdapter;
import com.hemangnh18.chatmate.Adapters.ContactUserAdapter;
import com.hemangnh18.chatmate.Adapters.MessageListAdapter;
import com.hemangnh18.chatmate.Classes.DisplayRecent;
import com.hemangnh18.chatmate.Classes.FirstChatUser;
import com.hemangnh18.chatmate.Classes.Methods;
import com.hemangnh18.chatmate.Classes.SocketMessage;
import com.hemangnh18.chatmate.Classes.User;
import com.hemangnh18.chatmate.Database.DatabaseHandler;
import com.hemangnh18.chatmate.Database.MidChatHelper;
import com.hemangnh18.chatmate.R;
import com.hemangnh18.chatmate.Threading.ContactsMatching;
import com.hemangnh18.chatmate.Threading.ContactsMatchingFactory;
import com.hemangnh18.chatmate.Threading.FetchCurrentChats;
import com.hemangnh18.chatmate.Threading.FetchCurrentChatsFactory;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Collections;

public class HomeFragment extends Fragment {

    private RecyclerView recyclerView;
    private ChatListAdapter contactUserAdapter;
    private ArrayList<DisplayRecent> contacts;
    private FetchCurrentChats fetchCurrentChats;

    public HomeFragment() {
    }

    @Override
    public void onResume() {
        super.onResume();
        //Toast.makeText(getActivity(),"Hello Frag ON Pause",Toast.LENGTH_LONG).show();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        EventBus.getDefault().register(this);
        super.onAttach(context);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);
        recyclerView =  view.findViewById(R.id.recycler_home);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(llm);
        contacts =  new ArrayList<>();
        contactUserAdapter = new ChatListAdapter(getContext(),contacts);
        recyclerView.setAdapter(contactUserAdapter);

        FetchCurrentChatsFactory factory = new FetchCurrentChatsFactory(getContext());
        fetchCurrentChats = ViewModelProviders.of(this,factory).get(FetchCurrentChats.class);
        subscribe();
        return view;
    }

    @Subscribe
    public void onFirstChat(DisplayRecent event)
    {
       Event(event);
    }
    private void Event(DisplayRecent displayRecent)
    {
        DatabaseHandler databaseHandler = new DatabaseHandler(getActivity());
        User user = databaseHandler.getUser(displayRecent.getId());
        displayRecent.setBase64(user.getBASE64());
        displayRecent.setUsername(user.getUSERNAME_IN_PHONE());
        contacts.add(displayRecent);
        contactUserAdapter.notifyDataSetChanged();
    }

    private void subscribe() {
        final Observer<ArrayList<DisplayRecent>> elapsedTimeObserver = new Observer<ArrayList<DisplayRecent>>() {
            @Override
            public void onChanged(@Nullable final ArrayList<DisplayRecent> aLong) {
                contacts.clear();
                contacts.addAll(aLong);
                //Collections.sort(contacts, Methods.c);
                contactUserAdapter.notifyDataSetChanged();
            }
        };
        fetchCurrentChats.getElapsedTime().observe(this, elapsedTimeObserver);
    }
}
