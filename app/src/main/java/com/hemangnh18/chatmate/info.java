package com.hemangnh18.chatmate;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.appbar.CollapsingToolbarLayout;

public class info extends AppCompatActivity {

    private Toolbar mToolbar;
    private TextView mStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        mToolbar = findViewById(R.id.toolbar);
        //TODO
        // mToolbar.setTitle();

        mStatus = findViewById(R.id.status);
        //TODO
        // mStatus.setText();

        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}
