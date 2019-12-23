package com.hemangnh18.chatmate.Threading;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.webkit.MimeTypeMap;

import com.google.android.gms.tasks.OnSuccessListener;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;

public class UploadDocsAsyncTask extends AsyncTask<Uri, Void, Void> {
    private static final String TAG = "UploadDocsAsyncTask";


    private FirebaseUser firebaseUser;
    private Context context;
    private StorageReference storageReference;

    public UploadDocsAsyncTask(Context cxt) {

        this.context=cxt;
        storageReference = FirebaseStorage.getInstance().getReference("UserProfiles");
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    }

    private String getFileExtention(Uri uri)
    {
        ContentResolver contentResolver = context.getContentResolver();
        MimeTypeMap mimeTypeMap =MimeTypeMap.getSingleton();
        return mimeTypeMap.getFileExtensionFromUrl(contentResolver.getType(uri));
    }


    @Override
    protected Void doInBackground(Uri... uris) {


        final ArrayList<UploadTask> tasks =new ArrayList<>();

        Uri imageUri = uris[0];

        final StorageReference fileReference = storageReference.child(firebaseUser.getUid() +"."+ getFileExtention(imageUri));
        Log.d(TAG, "Uploading: " + imageUri.toString());
        UploadTask uploadTask = fileReference.putFile(imageUri);
        Log.d(TAG, "Upload task created");


        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {

                        SharedPreferences preferences = context.getSharedPreferences("UserInfo",Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = preferences.edit();

                        Gson gson = new Gson();
                        String json = preferences.getString("User","");
                        User user = gson.fromJson(json,User.class);
                        user.setDOWNLOAD(uri.toString());
                        editor.putString("User",gson.toJson(user));
                        editor.apply();

                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
                        HashMap<String ,Object> hashMap = new HashMap<>();
                        hashMap.put("DOWNLOAD",user.getDOWNLOAD());
                        reference.updateChildren(hashMap);
                        Log.e("DownLoad URI",uri.toString());
                    }
                });
            }
        });

        tasks.add(uploadTask);

        try {
            Log.d(TAG, "Waiting...");
            Tasks.await(Tasks.whenAll(tasks));
        }
        catch (InterruptedException | ExecutionException e)
        {
            e.printStackTrace();
        }

        Log.d(TAG, "End of background processing");
        return null;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        Log.d(TAG, "Pre-Execute");
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        Log.e(TAG,"EXECUTED   >>>>>>>");
    }
}