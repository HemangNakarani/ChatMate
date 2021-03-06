package com.hemangnh18.chatmate;

import android.animation.Animator;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;
import com.hbb20.CountryCodePicker;
import com.hemangnh18.chatmate.Classes.User;
import com.hemangnh18.chatmate.Threading.UpdateUser;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class AuthenticationActivity extends AppCompatActivity {

    private EditText mPhonenumber,mOTP;
    private CountryCodePicker mCCP;
    private ConstraintLayout mInitial,mOTPView;

    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    private static final String TAG = "PhoneAuthActivity";
    private static final String KEY_VERIFY_IN_PROGRESS = "key_verify_in_progress";
    private boolean mVerificationInProgress = false;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private FirebaseAuth mAuth;
    private Window window;
    private String mVerificationId,phonenumber;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private RelativeLayout mProgress;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authentication);

        if (savedInstanceState != null) {
            onRestoreInstanceState(savedInstanceState);
        }

        window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(Color.parseColor("#FFFFFFFF"));
        }

        preferences = getSharedPreferences("UserInfo", Context.MODE_PRIVATE);
        editor = preferences.edit();
        /*Gson gson = new Gson();
        User user =  new User();
        editor.putString("User",gson.toJson(user));
        editor.apply();*/


        Button mVerifyBTN = findViewById(R.id.verifyBTn);
        mPhonenumber = findViewById(R.id.phonenumber);
        mOTP = findViewById(R.id.otp);
        Button mVerifyOTP = findViewById(R.id.verifyOTP);
        mCCP = findViewById(R.id.ccp);
        mInitial = findViewById(R.id.initialView);
        mOTPView = findViewById(R.id.OTPView);
        mProgress = findViewById(R.id.progress);
        mProgress.setVisibility(View.INVISIBLE);
        mAuth = FirebaseAuth.getInstance();

        mVerifyBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validatePhoneNumber()){
                     phonenumber = mCCP.getSelectedCountryCodeWithPlus() + mPhonenumber.getText().toString();
                     startPhoneVerification(phonenumber);
                }else {
                    Toast.makeText(getApplicationContext(),"Enter Phone Number Properly.", Toast.LENGTH_LONG).show();
                }
            }
        });

        mVerifyOTP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validateOTP()){
                    verifyPhoneNumberWithCode(mVerificationId,mOTP.getText().toString());
                    mProgress.setVisibility(View.VISIBLE);
                }else {
                    Toast.makeText(getApplicationContext(),"Enter OTP Properly.", Toast.LENGTH_LONG).show();
                }
            }
        });

        TextView resend = findViewById(R.id.resend);
        resend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resendVerificationCode(phonenumber, mResendToken);
            }
        });

        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onVerificationCompleted(PhoneAuthCredential credential) {
                Log.d(TAG, "onVerificationCompleted:" + credential);
                signInWithPhoneAuthCredential(credential);
            }
            @Override
            public void onVerificationFailed(FirebaseException e) {
                mVerificationInProgress=false;
                mInitial.setVisibility(View.VISIBLE);
                mOTPView.setVisibility(View.INVISIBLE);
                mProgress.setVisibility(View.INVISIBLE);
                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    Toast.makeText(getApplicationContext(),"Invalid Detail\nSomething Went Wrong :(", Toast.LENGTH_LONG).show();
                } else if (e instanceof FirebaseTooManyRequestsException) {
                    Toast.makeText(getApplicationContext(),"Too Many Users At Same Time\nPlease Wait...", Toast.LENGTH_LONG).show();
                }
            }
            @Override
            public void onCodeSent(@NonNull String verificationId, @NonNull PhoneAuthProvider.ForceResendingToken token) {
                mInitial.animate().alpha(0).setDuration(500).setListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        Animation animation1,animation2,animation3;
                        animation1 = AnimationUtils.loadAnimation(AuthenticationActivity.this,R.anim.down_up);
                        animation2= AnimationUtils.loadAnimation(AuthenticationActivity.this,R.anim.down_up2);
                        animation3= AnimationUtils.loadAnimation(AuthenticationActivity.this,R.anim.down_up3);
                        animation.addListener(new Animator.AnimatorListener() {
                            @Override
                            public void onAnimationStart(Animator animation) {}
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                mProgress.setVisibility(View.INVISIBLE);
                                mOTPView.setVisibility(View.VISIBLE);
                            }
                            @Override
                            public void onAnimationCancel(Animator animation) {}
                            @Override
                            public void onAnimationRepeat(Animator animation) {}
                        });
                        //mOTPView.setAnimation(animation1);
                        findViewById(R.id.OTPCard).setAnimation(animation1);
                        findViewById(R.id.verifyOTP).setAnimation(animation3);
                        findViewById(R.id.ImagePoster).setAnimation(animation2);
                    }
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        mInitial.setVisibility(View.INVISIBLE);
                    }
                    @Override
                    public void onAnimationCancel(Animator animation) {}
                    @Override
                    public void onAnimationRepeat(Animator animation) {}
                });
                mVerificationId = verificationId;
                mResendToken = token;
                Toast.makeText(getApplicationContext(),"Code Sent Successfully.", Toast.LENGTH_LONG).show();
            }
        };
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser!=null){
            startActivity(new Intent(this,UserInfo.class));
            finish();
        }
        if (mVerificationInProgress && validatePhoneNumber()) {
            startPhoneVerification(mPhonenumber.getText().toString());
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(KEY_VERIFY_IN_PROGRESS, mVerificationInProgress);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mVerificationInProgress = savedInstanceState.getBoolean(KEY_VERIFY_IN_PROGRESS);
    }

    private void startPhoneVerification(String phoneNumber){
        PhoneAuthProvider.getInstance().verifyPhoneNumber(phoneNumber, 60, TimeUnit.SECONDS, this, mCallbacks);
        mVerificationInProgress = true;
        mProgress.setVisibility(View.VISIBLE);
    }

    private boolean validatePhoneNumber() {
        String phoneNumber = mPhonenumber.getText().toString();
        return !TextUtils.isEmpty(phoneNumber) && phoneNumber.length() == 10;
    }

    private boolean validateOTP() {
        String otp = mOTP.getText().toString();
        return !TextUtils.isEmpty(otp) && otp.length() == 6;
    }

    private void verifyPhoneNumberWithCode(String verificationId, String code) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
        signInWithPhoneAuthCredential(credential);
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        mProgress.setVisibility(View.INVISIBLE);
                        if (task.isSuccessful()) {
                            Log.d(TAG, "signInWithCredential:success");

                            Gson gson = new Gson();
                            User user1 = new User();
                            user1.setUSER_ID(FirebaseAuth.getInstance().getCurrentUser().getUid());
                            user1.setPHONE(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber());
                            editor.putString("User",gson.toJson(user1));
                            editor.apply();

                            new UpdateUser(AuthenticationActivity.this).execute(user1);

                            startActivity(new Intent(AuthenticationActivity.this,UserInfo.class));
                            finish();
                        } else {
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                Toast.makeText(getApplicationContext(),"Invalid OTP Code :(", Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                });
    }

    private void resendVerificationCode(String phoneNumber, PhoneAuthProvider.ForceResendingToken token) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,
                60,
                TimeUnit.SECONDS,
                this,
                mCallbacks,
                token);
    }
}
