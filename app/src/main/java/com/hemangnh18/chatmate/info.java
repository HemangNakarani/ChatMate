package com.hemangnh18.chatmate;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;

import android.media.Image;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.hemangnh18.chatmate.Classes.User;
import com.hemangnh18.chatmate.Database.DatabaseHandler;

public class info extends AppCompatActivity {

    private Toolbar mToolbar;
    private TextView mStatus;
    private ImageView mDp;
    private TextView mPhonenumber;
    private SwitchCompat mSwitchCompact;
    private LinearLayout mClearChat;
    private LinearLayout mBlock;
    private DatabaseHandler databaseHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        mToolbar = findViewById(R.id.profile_toolbar);
        mDp = findViewById(R.id.header);
        mStatus = findViewById(R.id.profile_status);
        mPhonenumber = findViewById(R.id.profile_phonenumber);
        mSwitchCompact = findViewById(R.id.profile_notification);
        mClearChat = findViewById(R.id.profile_clearchat);
        mBlock = findViewById(R.id.profile_block);
        databaseHandler= new DatabaseHandler(info.this);

        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        if(getIntent().hasExtra("USER")){
            String oppositeUID = getIntent().getStringExtra("USER");
            User oppositeUser = databaseHandler.getUser(oppositeUID);
            mToolbar.setTitle(oppositeUser.getUSERNAME_IN_PHONE());
            mStatus.setText(oppositeUser.getSTATUS());
            mPhonenumber.setText(oppositeUser.getPHONE());
            Glide.with(this).load(oppositeUser.getDOWNLOAD()).into(mDp);

        }

        //todo
        mClearChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        //todo
        mBlock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        //todo
        // open contact for edit in contact app
        mPhonenumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        //todo
        mSwitchCompact.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

            }
        });
    }
}
