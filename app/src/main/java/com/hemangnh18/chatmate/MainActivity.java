package com.hemangnh18.chatmate;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import com.hemangnh18.chatmate.Classes.ContactsList;
import com.hemangnh18.chatmate.DownloadManager.DirectoryHelper;
import com.hemangnh18.chatmate.DownloadManager.DownloadSongService;
import com.hemangnh18.chatmate.Fragments.ContactFragment;
import com.hemangnh18.chatmate.Fragments.HomeFragment;
import com.hemangnh18.chatmate.Fragments.Profilefragment;
import com.rupins.drawercardbehaviour.CardDrawerLayout;

import java.util.ArrayList;
import java.util.List;

import eu.long1.spacetablayout.SpaceTabLayout;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    private SpaceTabLayout tabLayout;
    private FirebaseAuth mAuth;
    private CardDrawerLayout drawer;
    private Window window;
    private ViewPager viewPager;
    private NavigationView navigationView;
    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        handler = new Handler();

        final FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            startActivity(new Intent(this, AuthenticationActivity.class));
            finish();
        }

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

        Toolbar toolbar = findViewById(R.id.toolbar);
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
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.mainactivity_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId()==R.id.logout)
        {
            SignOut();
            return true;
        }
        else {
                return false;
        }

    }

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

}