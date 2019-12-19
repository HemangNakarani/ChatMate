package com.hemangnh18.chatmate;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.hemangnh18.authentication.Auth_It;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Auth_It auth_it = new Auth_It(MainActivity.this);
                auth_it.Authenticate();
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==Auth_It.AUTH_KEY)
        {
                if(resultCode==RESULT_OK)
                {
                    Toast.makeText(MainActivity.this,"Abey Saale...",Toast.LENGTH_LONG).show();
                }
                else
                {
                    Toast.makeText(MainActivity.this,"Tameez se...",Toast.LENGTH_LONG).show();
                }
        }
    }
}
