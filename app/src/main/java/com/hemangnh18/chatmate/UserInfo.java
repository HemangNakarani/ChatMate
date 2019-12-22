package com.hemangnh18.chatmate;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.android.material.textfield.TextInputLayout;
import com.hemangnh18.chatmate.Classes.User;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserInfo extends AppCompatActivity {

    private ConstraintLayout mDPChanger;
    private CircleImageView mDp;
    private TextInputLayout mUsername,mStatus;
    private Button mProceed;
    private RadioGroup mGender;
    private RadioButton mMale,mFemale;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);

        mDPChanger = findViewById(R.id.dp_changer);
        mDp = findViewById(R.id.dp);
        mUsername = findViewById(R.id.username);
        mStatus = findViewById(R.id.status);
        mProceed = findViewById(R.id.proceed);
        mGender = findViewById(R.id.gender);
        mMale = findViewById(R.id.male);
        mFemale = findViewById(R.id.female);

        mProceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String user = mUsername.getEditText().getText().toString().trim();
                String status = mStatus.getEditText().getText().toString().trim();
                if(user.equals("")){
                    Toast.makeText(getApplicationContext(),"Username is required.",Toast.LENGTH_LONG).show();
                    return;
                }
                if(status.equals("")){
                    Toast.makeText(getApplicationContext(),"Set Status",Toast.LENGTH_LONG).show();
                    return;
                }
                if(!mMale.isChecked() && !mFemale.isChecked()){
                    Toast.makeText(getApplicationContext(),"Select Gender",Toast.LENGTH_LONG).show();
                    return;
                }
                User user1 = new User();
                user1.setUSERNAME(user);
                user1.setSTATUS(status);
                if(mMale.isChecked()){
                    user1.setGENDER("Male");
                }else {
                    user1.setGENDER("Female");
                }
                //TODO
                //user1.setDP();
                //user1.setUSER_ID();

                //TODO
                //Firebase Add
            }
        });
    }
}
