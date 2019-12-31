package com.hemangnh18.chatmate;

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
import com.hemangnh18.chatmate.Adapters.MessageListAdapter;
import com.hemangnh18.chatmate.Classes.Methods;
import com.hemangnh18.chatmate.Classes.SocketMessage;
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

public class MessageActivity extends AppCompatActivity {

    private RecyclerView mMessageRecycler;
    private MessageListAdapter mMessageAdapter;
    private List<SocketMessage> messageList;

    private String OppositeUid;
    private ImageButton sendButton;
    private EmojiPopup emojiPopup;
    private LinearLayout chatbox;
    private EmojiEditText textField;
    private ImageView emoji;
    private Toolbar toolbar;


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

        textField= findViewById(R.id.edittext_chatbox);
        sendButton = findViewById(R.id.button_chatbox_send);
        OppositeUid = getIntent().getStringExtra("Opposite");
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
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        mMessageRecycler.setLayoutManager(linearLayoutManager);
        mMessageRecycler.setAdapter(mMessageAdapter);
    }


    public void sendMessage(View view){

        String message = textField.getText().toString().trim();

        if(TextUtils.isEmpty(message)){
            return;
        }
        textField.setText("");
        SocketMessage socketMessage = new SocketMessage(message,FirebaseAuth.getInstance().getCurrentUser().getUid(),OppositeUid,String.valueOf(System.currentTimeMillis()),OppositeUid,"text");
        appendMessage(socketMessage);

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("message", message);
            jsonObject.put("sender", FirebaseAuth.getInstance().getCurrentUser().getUid());
            jsonObject.put("reciever", OppositeUid);
            jsonObject.put("time",System.currentTimeMillis());
            jsonObject.put("type","text");

        } catch (JSONException e) {
            e.printStackTrace();
        }

        SocketHandler.getSocket().emit("chat message", jsonObject);
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
}
