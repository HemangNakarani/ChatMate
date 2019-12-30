package com.hemangnh18.chatmate;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import androidx.appcompat.widget.Toolbar;

import com.hemangnh18.chatmate.Adapters.MessageListAdapter;
import com.hemangnh18.chatmate.Classes.Message;

import java.util.List;

import hani.momanii.supernova_emoji_library.Actions.EmojIconActions;
import hani.momanii.supernova_emoji_library.Helper.EmojiconEditText;

public class MessageActivity extends AppCompatActivity {

    private RecyclerView mMessageRecycler;
    private MessageListAdapter mMessageAdapter;
    private List<Message> messageList;

    private LinearLayout chatbox;
    private EmojiconEditText emojiconEditText;
    private ImageView emoji;
    private Toolbar toolbar;
    EmojIconActions emojiIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        chatbox = findViewById(R.id.layout_chatbox);
        emojiconEditText = findViewById(R.id.edittext_chatbox);
        emoji = findViewById(R.id.emoji_btn);
        toolbar = findViewById(R.id.toolbar);

        emojiIcon = new EmojIconActions(this, chatbox, emojiconEditText, emoji);
        emojiIcon.ShowEmojIcon();
        emojiIcon.setUseSystemEmoji(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
        }

        //TODO
        // Get Chats Form Database And Store IT in messageList.


//        mMessageRecycler = findViewById(R.id.reyclerview_message_list);
//        mMessageAdapter = new MessageListAdapter(this, messageList);
//        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
//        linearLayoutManager.setReverseLayout(true);
//        linearLayoutManager.setStackFromEnd(true);
//        mMessageRecycler.setLayoutManager(linearLayoutManager);
//        mMessageRecycler.setAdapter(mMessageAdapter);
    }
}
