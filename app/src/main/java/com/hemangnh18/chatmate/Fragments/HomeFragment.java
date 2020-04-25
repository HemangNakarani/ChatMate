package com.hemangnh18.chatmate.Fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.hemangnh18.chatmate.Adapters.ChatListAdapter;
import com.hemangnh18.chatmate.Classes.DisplayRecent;
import com.hemangnh18.chatmate.Classes.Methods;
import com.hemangnh18.chatmate.Classes.SocketMessage;
import com.hemangnh18.chatmate.Classes.User;
import com.hemangnh18.chatmate.Database.DatabaseHandler;
import com.hemangnh18.chatmate.Database.MidChatHelper;
import com.hemangnh18.chatmate.R;
import com.hemangnh18.chatmate.Threading.FetchCurrentChats;
import com.hemangnh18.chatmate.Threading.FetchCurrentChatsFactory;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Collections;

public class HomeFragment extends Fragment  {

    private RecyclerView recyclerView;
    private ChatListAdapter contactUserAdapter;
    private ArrayList<DisplayRecent> contacts;
    private FetchCurrentChats fetchCurrentChats;
    private MidChatHelper midChatHelper;

    public HomeFragment() {
    }

    @Override
    public void onResume() {
        super.onResume();
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
        midChatHelper = new MidChatHelper(getContext());

        FetchCurrentChatsFactory factory = new FetchCurrentChatsFactory(getContext());
        fetchCurrentChats = ViewModelProviders.of(this,factory).get(FetchCurrentChats.class);
        subscribe();
        return view;

    }




    @Subscribe
    public void onMessageEvent(SocketMessage event)
    {
        FirebaseDatabase.getInstance().getReference("MsgStatus").child(event.getSender()).child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue("Delivered");
        midChatHelper.Exists(event.getSender(),event.getMessage(),event.getTime());
    }

    @Subscribe
    public void onFirstChat(DisplayRecent event)
    {
        Event(event);
        Log.e("BHOSADBILAAA>>>",event.getMessage());
    }

    private void Event(DisplayRecent displayRecent)
    {
        DatabaseHandler databaseHandler = new DatabaseHandler(getActivity());
        User user = databaseHandler.getUser(displayRecent.getId());
        displayRecent.setBase64(user.getBASE64());
        displayRecent.setUsername(user.getUSERNAME_IN_PHONE());

        // Code Part -------------------------------------------
        int flag=-1;
        for(int i=0;i<contacts.size();i++)
        {
            if(contacts.get(i).getId().equals(displayRecent.getId()))
            {
                    flag=i;

                    break;
            }
        }

        if(flag!=-1)
        {
            contacts.remove(flag);
            contacts.add(0,displayRecent);
        }
        else
        {
            contacts.add(0,displayRecent);
        }


        contactUserAdapter.notifyDataSetChanged();
    }

    private void subscribe() {
        final Observer<ArrayList<DisplayRecent>> elapsedTimeObserver = new Observer<ArrayList<DisplayRecent>>() {
            @Override
            public void onChanged(@Nullable final ArrayList<DisplayRecent> aLong) {
                contacts.clear();
                contacts.addAll(aLong);
                Collections.sort(contacts, Methods.ttt);
                contactUserAdapter = new ChatListAdapter(getContext(),contacts);
                recyclerView.setAdapter(contactUserAdapter);
            }
        };

        fetchCurrentChats.getElapsedTime().observe(this, elapsedTimeObserver);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.mainactivity_menu,menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId()==R.id.menu_search)
        {
            SearchView searchView = (SearchView) item.getActionView();
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String s) {
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String s) {
                    contactUserAdapter.getFilter().filter(s);
                    return false;
                }
            });
            return true;
        }
        else if(item.getItemId()==R.id.menu_profile)
        {
            return true;
        }
        else {
            return false;
        }

    }
}
