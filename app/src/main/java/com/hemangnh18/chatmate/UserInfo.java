package com.hemangnh18.chatmate;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.gson.Gson;
import com.hemangnh18.chatmate.Classes.User;
import com.hemangnh18.chatmate.Compressing.Compressor;
import com.hemangnh18.chatmate.Compressing.Converter;
import com.hemangnh18.chatmate.DownloadManager.DirectoryHelper;
import com.hemangnh18.chatmate.Threading.UploadDocsAsyncTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import de.hdodenhof.circleimageview.CircleImageView;

public class UserInfo extends AppCompatActivity {


    private CircleImageView mDp,mDPChanger;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private TextInputLayout mUsername,mStatus;
    private Button mProceed;
    private RadioGroup mGender;
    private RadioButton mMale,mFemale;
    private DatabaseReference reference;
    private Uri imageUri;
    private static final int WRITE_EXTERNAL_STORAGE_REQUEST_CODE = 54654;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        mDPChanger = findViewById(R.id.edit_dp);
        mDp = findViewById(R.id.dp);
        mUsername = findViewById(R.id.username);
        mStatus = findViewById(R.id.status);
        mProceed = findViewById(R.id.proceed);
        mGender = findViewById(R.id.gender);
        mMale = findViewById(R.id.male);
        mFemale = findViewById(R.id.female);


        findViewById(R.id.dp_changer).setAnimation(AnimationUtils.loadAnimation(getApplicationContext(),R.anim.pop_in));
        mProceed.setAnimation(AnimationUtils.loadAnimation(getApplicationContext(),R.anim.pop_in));
        mGender.setAnimation(AnimationUtils.loadAnimation(getApplicationContext(),R.anim.pop_in));
        mUsername.setAnimation(AnimationUtils.loadAnimation(getApplicationContext(),R.anim.pop_in));
        mStatus.setAnimation(AnimationUtils.loadAnimation(getApplicationContext(),R.anim.pop_in));

        mDPChanger.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE}, WRITE_EXTERNAL_STORAGE_REQUEST_CODE);
                    return;
                }
                openImage();
            }
        });

        mProceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String user = mUsername.getEditText().getText().toString().trim();
                String status = mStatus.getEditText().getText().toString().trim();

                SharedPreferences preferences = getSharedPreferences("UserInfo",Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();

                Gson gson = new Gson();
                String json = preferences.getString("User","");
                User user1 = gson.fromJson(json,User.class);

                if(user.equals(""))
                {
                    user1.setUSERNAME("User "+ firebaseUser.getUid());
                }
                else
                {
                    user1.setUSERNAME(user);
                }

                if(mMale.isChecked())
                {
                    user1.setGENDER("Male");
                }
                else if(mFemale.isChecked())
                {
                    user1.setGENDER("Female");
                }

                if(imageUri!=null)
                {
                    user1.setDP(imageUri.toString());
                }

                if(!status.equals("")){
                    user1.setSTATUS(status);
                }


                editor.putString("User",gson.toJson(user1));
                editor.apply();
                DatabaseReference reference=FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getPhoneNumber());
                HashMap<String,Object> hashMap = new HashMap<>();
                hashMap.put("PHONE",firebaseUser.getPhoneNumber());
                hashMap.put("USER_ID",firebaseUser.getUid());
                hashMap.put("USERNAME",user);
                hashMap.put("STATUS",status);
                hashMap.put("GENDER",user1.getGENDER());
                hashMap.put("DOWNLOAD",user1.getDOWNLOAD());
                hashMap.put("BASE64",user1.getBASE64());
                hashMap.put("TOKEN",user1.getTOKEN());

                reference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        //TODO SHOW LOADING : DEVEN

                        Intent intent = new Intent(UserInfo.this,MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                });

            }
        });


        DirectoryHelper.createDirectory(this);

    }


    private void openImage() {

        try
        {
            CropImage.activity().setCropShape(CropImageView.CropShape.OVAL).setAspectRatio(1,1).start(UserInfo.this);
        }
        catch (NullPointerException e)
        {
            e.printStackTrace();
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                imageUri = result.getUri();
                if(imageUri==null)
                {
                    Toast.makeText(UserInfo.this,"No Image Selected",Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Glide.with(this).load(imageUri.getPath()).into(mDp);
                    customCompressImage(new File(imageUri.getPath()));
                    new UploadDocsAsyncTask(this).execute(imageUri);
                }
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                Toast.makeText(UserInfo.this,error.toString(),Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == WRITE_EXTERNAL_STORAGE_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
                DirectoryHelper.createDirectory(this);
                openImage();
        }
    }

    public void customCompressImage(File actualImage) {
        if (actualImage == null) {

        } else {
            try {
                File compressedImage = new Compressor(this)
                        .setMaxWidth(640)
                        .setMaxHeight(480)
                        .setQuality(75)
                        .setCompressFormat(Bitmap.CompressFormat.WEBP)
                        .setDestinationDirectoryPath(Environment.getExternalStorageDirectory().getAbsolutePath()+"/ChatMate/DP")
                        .compressToFile(actualImage);

                String base64 = Converter.File2Base64(compressedImage);

                SharedPreferences preferences = getSharedPreferences("UserInfo",Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();

                Gson gson = new Gson();
                String json = preferences.getString("User","");
                User user = gson.fromJson(json,User.class);
                user.setBASE64(base64);
                editor.putString("User",gson.toJson(user));
                editor.apply();

                reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getPhoneNumber());
                HashMap<String ,Object> hashMap = new HashMap<>();
                hashMap.put("BASE64",base64);
                reference.updateChildren(hashMap);
                Log.e("BASE64>>>",base64);

            } catch (IOException e) {
                e.printStackTrace();

            }
        }
    }



}
