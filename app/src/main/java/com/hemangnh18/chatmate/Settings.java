package com.hemangnh18.chatmate;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;


public class Settings extends AppCompatActivity {

    private Switch mNotification;
    private SeekBar mFontSize;
    private TextView mTextSend,mTextRecive;
    private CardView mDeleteAccount;
    private Toolbar mToolbar;

    private SharedPreferences mSharedPreference;
    private SharedPreferences.Editor mEditor;
    private Integer mMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        mNotification = findViewById(R.id.notification_switch);
        mFontSize = findViewById(R.id.fontsize_seekbar);
        mTextRecive = findViewById(R.id.text_message_body);
        mTextSend = findViewById(R.id.tmb);
        mDeleteAccount = findViewById(R.id.deleteAccount);
        mToolbar = findViewById(R.id.toolbar_settings);

        mSharedPreference = getSharedPreferences("Setting",MODE_PRIVATE);
        mEditor = mSharedPreference.edit();
        mMode = mSharedPreference.getInt("FontSize",1);
        mFontSize.setProgress(mMode);
        mNotification.setChecked(mSharedPreference.getBoolean("Notification",true));
        setPreview();

        mFontSize.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                mMode = i;
                setPreview();
                mEditor.putInt("FontSize",mMode);
                mEditor.apply();
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        mDeleteAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO
            }
        });

        mNotification.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                Toast.makeText(getApplicationContext(),""+b,Toast.LENGTH_LONG).show();
                mEditor.putBoolean("Notification",b);
                mEditor.commit();
            }
        });

        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void setPreview() {
        switch (mMode){
            case 0:
                mTextRecive.setTextSize(12);
                mTextSend.setTextSize(12);
                break;
            case 1:
                mTextRecive.setTextSize(14);
                mTextSend.setTextSize(14);
                break;
            case 2:
                mTextRecive.setTextSize(16);
                mTextSend.setTextSize(16);
                break;
        }
    }
}
