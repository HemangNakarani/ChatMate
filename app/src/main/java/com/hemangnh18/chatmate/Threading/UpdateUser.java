package com.hemangnh18.chatmate.Threading;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.webkit.MimeTypeMap;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;
import com.hemangnh18.chatmate.Classes.User;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;

public class UpdateUser extends AsyncTask<User, Void, Void> {

    private static final String TAG = "UpdateUser";
    private FirebaseUser firebaseUser;
    private Context context;
    private DatabaseReference reference;

    public UpdateUser(Context cxt) {

        this.context=cxt;
        reference = FirebaseDatabase.getInstance().getReference("Users");
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    }


    @Override
    protected Void doInBackground(User... users) {

        User user = users[0];
        final DatabaseReference usersRef = reference.child(firebaseUser.getUid());
        final HashMap<String,Object> mapdata = new HashMap<>();
        mapdata.put("USERNAME",user.getUSERNAME());
        mapdata.put("STATUS",user.getSTATUS());
        mapdata.put("PHONE",user.getPHONE());
        mapdata.put("GENDER",user.getGENDER());
        mapdata.put("DOWNLOAD",user.getDOWNLOAD());
        usersRef.setValue(mapdata).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Log.e(TAG,task.toString());
            }
        });

        return null;
    }
}