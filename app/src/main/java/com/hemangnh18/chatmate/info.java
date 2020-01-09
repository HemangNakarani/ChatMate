package com.hemangnh18.chatmate;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.hemangnh18.chatmate.Classes.User;
import com.hemangnh18.chatmate.Compressing.Converter;

public class info extends AppCompatActivity {

    private Toolbar mToolbar;
    private TextView mStatus;
    private TextView mPhonenumber;
    private Switch mSwitch;
    private LinearLayout mClearChat;
    private LinearLayout mBlock;
    private ImageView header;

    private SharedPreferences mSharedPreferences;
    private SharedPreferences.Editor mEditor;
    private User oppositeUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        mToolbar = findViewById(R.id.profile_toolbar);
        mStatus = findViewById(R.id.profile_status);
        mPhonenumber = findViewById(R.id.profile_phonenumber);
        mSwitch = findViewById(R.id.profile_notification);
        mClearChat = findViewById(R.id.profile_clearchat);
        mBlock = findViewById(R.id.profile_block);
        header = findViewById(R.id.header);

        mSharedPreferences = getSharedPreferences("Muted_Notification",MODE_PRIVATE);
        mEditor = mSharedPreferences.edit();

        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        if(getIntent().hasExtra("USER")){
            oppositeUser = getIntent().getParcelableExtra("USER");
            mToolbar.setTitle(oppositeUser.getUSERNAME_IN_PHONE());
            mStatus.setText(oppositeUser.getSTATUS());
            mPhonenumber.setText(oppositeUser.getPHONE());
            header.setImageBitmap(Converter.Base642Bitmap(oppositeUser.getBASE64()));
            Glide.with(this).load(oppositeUser.getDOWNLOAD()).into(header);
        }
        Boolean mute = mSharedPreferences.getBoolean(oppositeUser.getUSER_ID(),false);
        //Toast.makeText(getApplicationContext(),""+mute,Toast.LENGTH_LONG).show();
        mSwitch.setChecked(mute);

        mSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                //Toast.makeText(getApplicationContext(),""+b,Toast.LENGTH_LONG).show();
                mEditor.putBoolean(oppositeUser.getUSER_ID(),b);
                mEditor.commit();
            }
        });

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
    }
}
