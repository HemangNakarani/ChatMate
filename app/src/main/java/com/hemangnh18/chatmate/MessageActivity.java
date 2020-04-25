package com.hemangnh18.chatmate;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
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
import android.widget.TextView;
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
import com.hemangnh18.chatmate.Classes.Typing;
import com.hemangnh18.chatmate.Classes.User;
import com.hemangnh18.chatmate.Compressing.Converter;
import com.hemangnh18.chatmate.Database.ChatMessagesHandler;
import com.hemangnh18.chatmate.Database.DatabaseHandler;
import com.hemangnh18.chatmate.Database.MidChatHelper;
import com.hemangnh18.chatmate.FCM.APIService;
import com.hemangnh18.chatmate.FCM.Client;
import com.hemangnh18.chatmate.FCM.MyResponse;
import com.hemangnh18.chatmate.FCM.SenderBox;
import com.hemangnh18.chatmate.Socket.SocketHandler;
import com.hemangnh18.chatmate.Threading.ContactsMatching;
import com.hemangnh18.chatmate.Threading.ContactsMatchingFactory;
import com.hemangnh18.chatmate.Threading.FetchMessages;
import com.hemangnh18.chatmate.Threading.FetchMessagesFactory;
import com.leinardi.android.speeddial.SpeedDialActionItem;
import com.leinardi.android.speeddial.SpeedDialView;
import com.vanniktech.emoji.EmojiEditText;
import com.vanniktech.emoji.EmojiManager;
import com.vanniktech.emoji.EmojiPopup;
import com.vanniktech.emoji.ios.IosEmojiProvider;
import com.vanniktech.emoji.listeners.OnSoftKeyboardCloseListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
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
    private Button mScrollDown;
    private ChatMessagesHandler chatMessagesHandler;
    private MidChatHelper midChatHelper;
    private FetchMessages fetchMessages;

    private ImageButton mBackToolbar;
    private TextView mUsernameToolbar;
    private CircleImageView mDPToolbar;
    private TextView mStatusToolbar;

    private SpeedDialView mAttach_menu;

    private final static int SELECT_IMAGE = 251;
    private final static int SELECT_VIDEO = 252;
    private final static int SELECT_FILE = 253 ;
    private final static int SELECT_CONTACT = 254;



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

        mBackToolbar = findViewById(R.id.backBtn);
        mUsernameToolbar = findViewById(R.id.name);
        mDPToolbar = findViewById(R.id.dp_view_toolbar);
        mStatusToolbar = findViewById(R.id.status_toolbar);
        mAttach_menu = findViewById(R.id.attach_menu);
        mAttach_menu.inflate(R.menu.attchfilemenu);
        mAttach_menu.setAlpha((float)0.4);
        mAttach_menu.setOnChangeListener(new SpeedDialView.OnChangeListener() {
            @Override
            public boolean onMainActionSelected() {
                return false;
            }

            @Override
            public void onToggleChanged(boolean isOpen) {
                    if(isOpen)mAttach_menu.setAlpha((float)1);
                    else mAttach_menu.setAlpha((float)0.4);
            }
        });
        mAttach_menu.setOnActionSelectedListener(new SpeedDialView.OnActionSelectedListener() {
            @Override
            public boolean onActionSelected(SpeedDialActionItem actionItem) {

                int id = actionItem.getId();

                if(id==R.id.attach_photo)
                {
                    Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(i, SELECT_IMAGE);
                }

                if(id==R.id.attach_video)
                {
                    Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(i, SELECT_VIDEO);
                }

                if(id==R.id.attach_files)
                {
                    Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                    intent.setType("*/*");
                    intent.addCategory(Intent.CATEGORY_OPENABLE);
                    try {
                        startActivityForResult(
                                Intent.createChooser(intent, "Select a File to Upload"),
                                SELECT_FILE);
                    } catch (android.content.ActivityNotFoundException ex) {
                        // Potentially direct the user to the Market with a Dialog
                        Toast.makeText(MessageActivity.this, "Please install a File Manager.", Toast.LENGTH_SHORT).show();
                    }
                }

                if(id==R.id.attach_contact)
                {
                    Toast.makeText(MessageActivity.this,"Contacts Attached Soon..",Toast.LENGTH_LONG).show();
                }

                return false;

            }
        });


        final DatabaseReference infoConnected = FirebaseDatabase.getInstance().getReference(".info/connected");
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
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });

        apiService = Client.getClient("https://fcm.googleapis.com/").create(APIService.class);
        DatabaseHandler handler = new DatabaseHandler(MessageActivity.this);
        chatMessagesHandler = new ChatMessagesHandler(MessageActivity.this);
        midChatHelper = new MidChatHelper(MessageActivity.this);

        textField= findViewById(R.id.edittext_chatbox);
        sendButton = findViewById(R.id.button_chatbox_send);
        OppositeUid = getIntent().getStringExtra("Opposite");
        FirebaseDatabase.getInstance().getReference("MsgStatus").child(OppositeUid).child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue("Seen");
        Check(OppositeUid);
        oppositeUser = handler.getUser(OppositeUid);

        chatbox = findViewById(R.id.layout_chatbox);
        emoji = findViewById(R.id.emoji_btn);
        toolbar = findViewById(R.id.toolbar);
        mScrollDown = findViewById(R.id.scrollDown);
        mScrollDown.setVisibility(View.INVISIBLE);
        toolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MessageActivity.this,info.class);
                intent.putExtra("USER",oppositeUser);
                startActivity(intent);
            }
        });
        mBackToolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        mUsernameToolbar.setText(oppositeUser.getUSERNAME_IN_PHONE());
        mDPToolbar.setImageBitmap(Converter.Base642Bitmap(oppositeUser.getBASE64()));

        FetchMessagesFactory factory = new FetchMessagesFactory(this,OppositeUid);
        fetchMessages = ViewModelProviders.of(this, factory).get(FetchMessages.class);
        getMessages();


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
        SharedPreferences mStorage = getSharedPreferences("Setting",MODE_PRIVATE);
        mMessageRecycler = findViewById(R.id.reyclerview_message_list);
        mMessageAdapter = new MessageListAdapter(this, messageList,OppositeUid,mStorage.getInt("FontSize",1));
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        mMessageRecycler.setLayoutManager(linearLayoutManager);
        mMessageRecycler.setAdapter(mMessageAdapter);

        mMessageRecycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (!recyclerView.canScrollVertically(1)) {
                    mScrollDown.setVisibility(View.INVISIBLE);
                }
            }
        });

        mScrollDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMessageRecycler.smoothScrollToPosition(messageList.size()-1);
                mMessageRecycler.scrollTo(messageList.size()-1, 0);
                mScrollDown.setVisibility(View.INVISIBLE);
            }
        });
    }

    public void sendMessage(View view){

        String message = textField.getText().toString().trim();
        String curTime = String.valueOf(System.currentTimeMillis());
        if (TextUtils.isEmpty(message)) {
            return;
        }

        midChatHelper.Exists(OppositeUid,message,curTime);

        textField.setText("");
        FirebaseDatabase.getInstance().getReference("MsgStatus").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(OppositeUid).setValue("Sent");
        SocketMessage socketMessage = new SocketMessage(message, FirebaseAuth.getInstance().getCurrentUser().getUid(), OppositeUid, curTime, OppositeUid, "text");
        chatMessagesHandler.addMessage(socketMessage);
        appendMessage(socketMessage);

        if(isOnline) {

            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("message", message);
                jsonObject.put("sender", FirebaseAuth.getInstance().getCurrentUser().getUid());
                jsonObject.put("reciever", OppositeUid);
                jsonObject.put("time", curTime);
                jsonObject.put("type", "text");

            } catch (JSONException e) {
                e.printStackTrace();
            }


            SocketHandler.getSocket().emit("chat message", jsonObject);
        }
        else
        {
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
        if(event.getSender().equals(OppositeUid)) {
            FirebaseDatabase.getInstance().getReference("MsgStatus").child(OppositeUid).child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue("Seen");
            appendMessage(event);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onTypeEvent(Typing typing)
    {
        if(typing.getSender().equals(OppositeUid)) {
            if(typing.isTyping())
            {
                mStatusToolbar.setText("Typing...");
            }
            else
            {
                if(isOnline)mStatusToolbar.setText("Online");
                else mStatusToolbar.setText("Offline");
            }
        }
    }

    private void appendMessage(SocketMessage message)
    {
        messageList.add(message);
        mMessageAdapter.notifyDataSetChanged();
        if(message.getSender().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
            mMessageRecycler.scrollToPosition(messageList.size()-1);
        }else {
            LinearLayoutManager layoutManager = ((LinearLayoutManager) mMessageRecycler.getLayoutManager());
            if(layoutManager.findLastCompletelyVisibleItemPosition()!=messageList.size()-2){
                //Toast.makeText(getApplicationContext(),"New Message Arrived",Toast.LENGTH_LONG).show();
                mScrollDown.setVisibility(View.VISIBLE);
            }else {
                mMessageRecycler.scrollToPosition(messageList.size()-1);
            }
        }
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
                        mStatusToolbar.setText("Online");
                    }
                    else
                    {
                        isOnline=false;
                        mStatusToolbar.setText("Offline");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
    }

    private void getMessages() {
        final Observer<ArrayList<SocketMessage>> elapsedTimeObserver = new Observer<ArrayList<SocketMessage>>() {
            @Override
            public void onChanged(@Nullable final ArrayList<SocketMessage> aLong) {
                messageList.clear();
                messageList.addAll(aLong);
                mMessageAdapter.notifyDataSetChanged();
                if(messageList.size()>0) {
                    mMessageRecycler.scrollToPosition(messageList.size() - 1);
                    //midChatHelper.Exists(OppositeUid,messageList.get(messageList.size()-1).getMessage(),messageList.get(messageList.size()-1).getTime());
                }
            }
        };

        fetchMessages.getElapsedTime().observe(this, elapsedTimeObserver);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SELECT_IMAGE) {
            if (resultCode == Activity.RESULT_OK)
            {
                if (data != null) {
                    Toast.makeText(MessageActivity.this, data.getData().getPath().toString(), Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

}
