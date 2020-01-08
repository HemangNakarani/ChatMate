package com.hemangnh18.chatmate;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.util.Pair;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;

import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.hemangnh18.chatmate.Adapters.ChatListAdapter;
import com.hemangnh18.chatmate.Adapters.ContactUserAdapter;
import com.hemangnh18.chatmate.Fragments.ContactFragment;
import com.hemangnh18.chatmate.Fragments.HomeFragment;
import com.hemangnh18.chatmate.Fragments.Profilefragment;
import com.hemangnh18.chatmate.Socket.SocketHandler;
import com.hemangnh18.chatmate.Socket.SocketMethods;
import com.rupins.drawercardbehaviour.CardDrawerLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import eu.long1.spacetablayout.SpaceTabLayout;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    private SpaceTabLayout tabLayout;
    private SocketMethods socketMethods;
    private FirebaseAuth mAuth;
    private CardDrawerLayout drawer;
    private DatabaseReference infoConnected = FirebaseDatabase.getInstance().getReference(".info/connected");
    private Window window;
    private ViewPager viewPager;
    private NavigationView navigationView;
    private Handler handler;
    private Boolean hasConnection = false;
    private Socket mSocket;
    private LinearLayout mSplash;
    private Toolbar toolbar;
    {
        try {
            IO.Options opts = new IO.Options();
            opts.reconnection = true;
            opts.reconnectionDelay =1000;
            opts.transports = new String[]{"websocket"};
            opts.reconnectionDelayMax=5000;
            opts.upgrade= false;
            opts.reconnectionAttempts= 1000000;
            mSocket = IO.socket("https://afternoon-harbor-46312.herokuapp.com/",opts);
            SocketHandler.setSocket(mSocket);

        } catch (URISyntaxException e) {}
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSplash = findViewById(R.id.splash);
        toolbar = findViewById(R.id.toolbar);

        final Handler handler1 = new Handler();
        toolbar.setVisibility(View.INVISIBLE);
        handler1.postDelayed(new Runnable() {
            @Override
            public void run() {
                mSplash.setVisibility(View.GONE);
                toolbar.setVisibility(View.VISIBLE);
            }
        }, 2000);

        mAuth = FirebaseAuth.getInstance();
        final FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            startActivity(new Intent(this, AuthenticationActivity.class));
            finish();
        }

        handler = new Handler();
        socketMethods = new SocketMethods(MainActivity.this);
        DatabaseReference infoConnected = FirebaseDatabase.getInstance().getReference(".info/connected");
        final DatabaseReference UpdateRef = FirebaseDatabase.getInstance().getReference("/Status/"+mAuth.getUid());
        infoConnected.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                boolean isCon = dataSnapshot.getValue(Boolean.class);
                if(isCon)
                {
                   DatabaseReference reference = UpdateRef.child("Status");
                   reference.setValue("Online");

                   reference.onDisconnect().setValue("Offline");

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        //----bottom navigation----
        List<Fragment> fragmentList = new ArrayList<>();
        fragmentList.add(new ContactFragment());
        fragmentList.add(new HomeFragment());
        fragmentList.add(new Profilefragment());

        final CoordinatorLayout coordinatorLayout = (CoordinatorLayout) findViewById(R.id.activity_main);

        viewPager = (ViewPager) findViewById(R.id.viewPager);
        tabLayout = (SpaceTabLayout) findViewById(R.id.spaceTabLayout);
        tabLayout.initialize(viewPager, this.getSupportFragmentManager(), fragmentList,savedInstanceState);
        tabLayout.setTabOneOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Snackbar snackbar = Snackbar.make(coordinatorLayout, "Welcome to SpaceTabLayout", Snackbar.LENGTH_SHORT);
                snackbar.show();
            }
        });

        tabLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(getApplication(), "" + tabLayout.getCurrentPosition(), Toast.LENGTH_SHORT).show();
            }
        });

        //----Drawer----

        setSupportActionBar(toolbar);
        window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar,R.string.navigation_drawer_open, R.string.navigation_drawer_close){
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    window.setStatusBarColor(Color.parseColor("#539CE4"));
                }
            }
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    window.setStatusBarColor(Color.parseColor("#F2DBF7"));
                }
            }
        };
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.getMenu().getItem(0).setChecked(true);
        drawer.setViewScale(Gravity.START, 0.9f);
        drawer.setRadius(Gravity.START, 35);
        drawer.setViewElevation(Gravity.START, 20);

        //---INIT SOCKET-----
        if(savedInstanceState != null){
            hasConnection = savedInstanceState.getBoolean("hasConnection");
        }

        if(hasConnection)
        { }else {
            mSocket.connect();
            mSocket.on("connect user", socketMethods.onNewUser);
            mSocket.on("chat message",socketMethods.onNewMessage);
            mSocket.on("on typing", socketMethods.onTyping);
            mSocket.on("Establish", socketMethods.Establish);

            JSONObject userId = new JSONObject();

            try {
                if(FirebaseAuth.getInstance().getCurrentUser()!=null) {
                    userId.put("username", FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber());
                    mSocket.emit("connect user", userId);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        Log.i("CONNECTION STATUS>>>>", "onCreate: " + hasConnection);
        hasConnection = true;

    }


    //----navigation drawer----
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.nav_home) {
            viewPager.setCurrentItem(1);
            navigationView.getMenu().getItem(0).setChecked(true);
            navigationView.getMenu().getItem(2).setChecked(false);
            navigationView.getMenu().getItem(1).setChecked(false);

        } else if (id == R.id.nav_contact) {
            viewPager.setCurrentItem(0);
            navigationView.getMenu().getItem(1).setChecked(true);
            navigationView.getMenu().getItem(0).setChecked(false);
            navigationView.getMenu().getItem(2).setChecked(false);

        }else if (id == R.id.nav_profile) {
            viewPager.setCurrentItem(2);
            navigationView.getMenu().getItem(2).setChecked(true);
            navigationView.getMenu().getItem(0).setChecked(false);
            navigationView.getMenu().getItem(1).setChecked(false);

        }else if (id == R.id.nav_suggestion) {

        }else if (id == R.id.nav_help) {

        }else if (id == R.id.nav_invite) {

        }else if (id == R.id.nav_aboutapp) {

        }

        delay();
        return true;

    }



    //----delay---------
    public void delay(){
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                drawer.closeDrawer(GravityCompat.START);
            }
        },200);
        return;
    }

    //----menu bar----
    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.mainactivity_menu,menu);
        return true;
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
                   *//* if(viewPager.getCurrentItem()==1)
                    {
                        //mChatListAdapter.getFilter().filter(s);
                    }
                    if(viewPager.getCurrentItem()==0)
                    {
                        mContactUserAdapter.getFilter().filter(s);
                    }*//*
                    return false;
                }
            });
            return true;
        }
        else if(item.getItemId()==R.id.menu_settings)
        {
            return true;
        }
        else if(item.getItemId()==R.id.menu_profile)
        {
            return true;
        }
        else {
                return false;
        }

    }*/


    //----firebase----
    private void SignOut()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setMessage("Are You Sure ?")
                .setCancelable(true)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        FirebaseAuth.getInstance().signOut();
                        startActivity(new Intent(MainActivity.this, AuthenticationActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP));
                        finish();

                    }

                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.dismiss();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }



    @Override
    public void onDestroy() {

        super.onDestroy();


        if(isFinishing()){

            mSocket.emit("disconnect");
            Log.e("DESTROYING MAIN", "onDestroy: ");

            mSocket.disconnect();
            mSocket.off("chat message",socketMethods.onNewMessage);
            mSocket.off("connect user", socketMethods.onNewUser);
            mSocket.off("on typing", socketMethods.onTyping);
            mSocket.off("hey", socketMethods.Establish);

        }else {
            Log.i("DESTROYED ACTIVITY>>>>", "onDestroy: is rotating.....");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        FirebaseDatabase.getInstance().getReference("/Status/"+mAuth.getUid()).child("Status").setValue("Online");
    }

    @Override
    protected void onPause() {
        super.onPause();
        FirebaseDatabase.getInstance().getReference("/Status/"+mAuth.getUid()).child("Status").setValue("Offline");
    }
}