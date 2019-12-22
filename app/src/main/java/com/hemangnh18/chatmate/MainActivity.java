package com.hemangnh18.chatmate;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.hitomi.smlibrary.OnSpinMenuStateChangeListener;
import com.hitomi.smlibrary.SpinMenu;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private SpinMenu spinMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();

        final FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            startActivity(new Intent(this, AuthenticationActivity.class));
            finish();
        }


        spinMenu = (SpinMenu) findViewById(R.id.spin_menu);
        List<String> hintStrList = new ArrayList<>();
        hintStrList.add("Home");
        hintStrList.add("Contact");
        hintStrList.add("Profile");

        spinMenu.setMenuItemScaleValue(0.53f);
        spinMenu.setHintTextStrList(hintStrList);
        spinMenu.setHintTextSize(14);

        spinMenu.setEnableGesture(true);

        final List<Fragment> fragmentList = new ArrayList<>();
        fragmentList.add(HomeFragment.newInstance());
        fragmentList.add(ContactFragment.newInstance());
        fragmentList.add(ProfileFragment.newInstance());


        FragmentPagerAdapter fragmentPagerAdapter = new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return fragmentList.get(position);
            }

            @Override
            public int getCount() {
                return fragmentList.size();
            }
        };
        spinMenu.setFragmentAdapter(fragmentPagerAdapter);

        spinMenu.setOnSpinMenuStateChangeListener(new OnSpinMenuStateChangeListener() {
            @Override
            public void onMenuOpened() {
                // Toast.makeText(MainActivity.this, "SpinMenu opened", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onMenuClosed() {
                //Toast.makeText(MainActivity.this, "SpinMenu closed", Toast.LENGTH_SHORT).show();
            }
        });
    }
}