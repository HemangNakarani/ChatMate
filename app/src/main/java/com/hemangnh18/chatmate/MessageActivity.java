package com.hemangnh18.chatmate;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hemangnh18.chatmate.Adapters.MessageListAdapter;
import com.hemangnh18.chatmate.Classes.Methods;
import com.hemangnh18.chatmate.Classes.SocketMessage;
import com.hemangnh18.chatmate.Classes.User;
import com.hemangnh18.chatmate.Database.DatabaseHandler;
import com.hemangnh18.chatmate.FCM.APIService;
import com.hemangnh18.chatmate.FCM.Client;
import com.hemangnh18.chatmate.FCM.MyResponse;
import com.hemangnh18.chatmate.FCM.SenderBox;
import com.hemangnh18.chatmate.Socket.SocketHandler;
import com.vanniktech.emoji.EmojiEditText;
import com.vanniktech.emoji.EmojiManager;
import com.vanniktech.emoji.EmojiPopup;
import com.vanniktech.emoji.ios.IosEmojiProvider;
import com.vanniktech.emoji.listeners.OnSoftKeyboardCloseListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MessageActivity extends AppCompatActivity {

    private RecyclerView mMessageRecycler;
    private MessageListAdapter mMessageAdapter;
    private List<SocketMessage> messageList;
    private APIService apiService;
    private User oppositeUser= new User();
    private String OppositeUid;
    private ImageButton sendButton;
    private EmojiPopup emojiPopup;
    private LinearLayout chatbox;
    private EmojiEditText textField;
    private ImageView emoji;
    private Toolbar toolbar;
    private Boolean isOnline=false;


    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EmojiManager.install(new IosEmojiProvider());
        setContentView(R.layout.activity_message);


        DatabaseReference infoConnected = FirebaseDatabase.getInstance().getReference(".info/connected");
        final DatabaseReference UpdateRef = FirebaseDatabase.getInstance().getReference("/Status/"+FirebaseAuth.getInstance().getCurrentUser().getUid());
        infoConnected.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                boolean isCon = dataSnapshot.getValue(Boolean.class);
                if(isCon)
                {
                    DatabaseReference reference = UpdateRef.child("Status");
                    reference.setValue("Online");

                    reference.onDisconnect().setValue("Offline");

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        apiService = Client.getClient("https://fcm.googleapis.com/").create(APIService.class);
        DatabaseHandler handler = new DatabaseHandler(MessageActivity.this);
        textField= findViewById(R.id.edittext_chatbox);
        sendButton = findViewById(R.id.button_chatbox_send);
        OppositeUid = getIntent().getStringExtra("Opposite");
        Check(OppositeUid);
        oppositeUser = handler.getUser(OppositeUid);
        textField.setText(OppositeUid);
        chatbox = findViewById(R.id.layout_chatbox);
        emoji = findViewById(R.id.emoji_btn);
        toolbar = findViewById(R.id.toolbar);



        emojiPopup = EmojiPopup.Builder.fromRootView(chatbox).setBackgroundColor(Color.parseColor("#F2DBF7")).setKeyboardAnimationStyle(R.style.emoji_fade_animation_style).setOnSoftKeyboardCloseListener(new OnSoftKeyboardCloseListener() {
            @Override
            public void onKeyboardClose() {
                emoji.setImageDrawable(getResources().getDrawable((R.drawable.smily)));
            }
        }).build(textField);

        emoji.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(emojiPopup.isShowing())
                {
                    emojiPopup.dismiss();
                    emoji.setImageDrawable(getResources().getDrawable((R.drawable.smily)));

                }
                else {
                    emojiPopup.toggle(); // Toggles visibility of the Popup.
                    emoji.setImageDrawable(getResources().getDrawable((R.drawable.ic_keyboard_black_24dp)));

                }

            }
        });


        messageList = new ArrayList<>();
        mMessageRecycler = findViewById(R.id.reyclerview_message_list);
        mMessageAdapter = new MessageListAdapter(this, messageList,OppositeUid);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        //linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        mMessageRecycler.setLayoutManager(linearLayoutManager);
        mMessageRecycler.setAdapter(mMessageAdapter);
    }


    public void sendMessage(View view){

        String message = textField.getText().toString().trim();

        if (TextUtils.isEmpty(message)) {
            return;
        }
        textField.setText("");
        SocketMessage socketMessage = new SocketMessage(message, FirebaseAuth.getInstance().getCurrentUser().getUid(), OppositeUid, String.valueOf(System.currentTimeMillis()), OppositeUid, "text");
        appendMessage(socketMessage);

        if(isOnline) {

            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("message", message);
                jsonObject.put("sender", FirebaseAuth.getInstance().getCurrentUser().getUid());
                jsonObject.put("reciever", OppositeUid);
                jsonObject.put("time", System.currentTimeMillis());
                jsonObject.put("type", "text");

            } catch (JSONException e) {
                e.printStackTrace();
            }


            SenderBox sender = new SenderBox(socketMessage,oppositeUser.getTOKEN());
            apiService.sendNotification(sender).enqueue(new Callback<MyResponse>() {
                @Override
                public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                    if(response.code()==200)
                    {
                        if(response.body().success!=1)
                        {
                            Toast.makeText(MessageActivity.this,"Failed",Toast.LENGTH_SHORT).show();
                        }
                    }
                }

                @Override
                public void onFailure(Call<MyResponse> call, Throwable t) {

                }
            });


            SocketHandler.getSocket().emit("chat message", jsonObject);
        }
        else
        {
            /*SenderBox sender = new SenderBox(socketMessage,oppositeUser.getTOKEN());
            apiService.sendNotification(sender).enqueue(new Callback<MyResponse>() {
                @Override
                public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                    if(response.code()==200)
                    {
                        if(response.body().success!=1)
                        {
                            Toast.makeText(MessageActivity.this,"Failed",Toast.LENGTH_SHORT).show();
                        }
                    }
                }

                @Override
                public void onFailure(Call<MyResponse> call, Throwable t) {

                }
            });*/
        }


        onTypeButtonEnable();

    }



    public void onTypeButtonEnable(){
        textField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                JSONObject onTyping = new JSONObject();
                try {
                    onTyping.put("typing", true);
                    onTyping.put("sender", FirebaseAuth.getInstance().getCurrentUser().getUid());
                    onTyping.put("reciever", OppositeUid);
                    SocketHandler.getSocket().emit("on typing", onTyping);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if (charSequence.toString().trim().length() > 0) {
                    sendButton.setEnabled(true);
                } else {
                    sendButton.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
    }
    @Subscribe
    public void onMessageEvent(SocketMessage event)
    {
       appendMessage(event);
    }

    private void appendMessage(SocketMessage message)
    {
        messageList.add(message);
        mMessageAdapter.notifyDataSetChanged();
    }


    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }


    public void ShowKeyboard(View view)
    {
        if(emojiPopup.isShowing())
        {
            emojiPopup.dismiss();
            emoji.setImageDrawable(getResources().getDrawable((R.drawable.smily)));
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        FirebaseDatabase.getInstance().getReference("/Status/"+FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Status").setValue("Offline");
    }

    @Override
    protected void onResume() {
        super.onResume();
        FirebaseDatabase.getInstance().getReference("/Status/"+FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Status").setValue("Online");
    }


    private void Check(String id)
    {
            FirebaseDatabase.getInstance().getReference("Status").child(id).child("Status").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.getValue(String.class).equals("Online"))
                    {
                        isOnline=true;
                    }
                    else
                    {
                        isOnline=false;
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
    }

}
