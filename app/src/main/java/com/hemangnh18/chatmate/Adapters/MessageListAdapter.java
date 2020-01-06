package com.hemangnh18.chatmate.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hemangnh18.chatmate.Classes.Methods;
import com.hemangnh18.chatmate.Classes.SocketMessage;
import com.hemangnh18.chatmate.Classes.User;
import com.hemangnh18.chatmate.Compressing.Converter;
import com.hemangnh18.chatmate.Database.DatabaseHandler;
import com.hemangnh18.chatmate.R;
import com.vanniktech.emoji.EmojiTextView;

import java.text.SimpleDateFormat;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageListAdapter extends RecyclerView.Adapter {
    private static final int VIEW_TYPE_MESSAGE_SENT = 1;
    private static final int VIEW_TYPE_MESSAGE_RECEIVED = 2;

    private Context mContext;
    private User oppositeUser;
    private String oppositeUid;
    private List<SocketMessage> mMessageList;
    private SimpleDateFormat formatter;


    public MessageListAdapter(Context context, List<SocketMessage> messageList, String oppositeUid) {
        mContext = context;
        mMessageList = messageList;
        this.oppositeUid = oppositeUid;
        DatabaseHandler databaseHandler = new DatabaseHandler(context);
        this.oppositeUser = databaseHandler.getUser(oppositeUid);
        String dateFormat = "hh:mm aa dd/MM/yyyy";
        formatter = new SimpleDateFormat(dateFormat);
    }

    @Override
    public int getItemCount() {
        return mMessageList.size();
    }

    @Override
    public int getItemViewType(int position) {
        SocketMessage message = mMessageList.get(position);

        if (message.getSender().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
            return VIEW_TYPE_MESSAGE_SENT;
        } else {
            return VIEW_TYPE_MESSAGE_RECEIVED;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;

        if (viewType == VIEW_TYPE_MESSAGE_SENT) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message_sent, parent, false);
            return new SentMessageHolder(view);

        } else if (viewType == VIEW_TYPE_MESSAGE_RECEIVED) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message_received, parent, false);
            return new ReceivedMessageHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        SocketMessage message = mMessageList.get(position);

        switch (holder.getItemViewType()) {
            case VIEW_TYPE_MESSAGE_SENT:
                ((SentMessageHolder) holder).bind(message,position);
                break;
            case VIEW_TYPE_MESSAGE_RECEIVED:
                ((ReceivedMessageHolder) holder).bind(message,position);
        }
    }

    private class SentMessageHolder extends RecyclerView.ViewHolder {
        TextView timeText,msgStatus;
        EmojiTextView messageText;

        SentMessageHolder(View itemView) {
            super(itemView);
            messageText = itemView.findViewById(R.id.text_message_body);
            timeText = itemView.findViewById(R.id.text_message_time);
            msgStatus = itemView.findViewById(R.id.msgstatus);
        }

        void bind(SocketMessage message,int pos) {
            timeText.setText(Methods.getTimeStamp(Long.valueOf(message.getTime()),formatter));
            if(pos==mMessageList.size()-1) {
                msgStatus.setVisibility(View.VISIBLE);
            } else {
                msgStatus.setVisibility(View.GONE);
            }
            UpdateMsgStatus(msgStatus);

            if(isEmoji(message.getMessage())) {
                messageText.setBackgroundColor(Color.argb(0,0,0,0));
                messageText.setEmojiSize(120);
                if(isEmojiis(message.getMessage())) {
                    messageText.setEmojiSize(300);
                }
            } else {
                messageText.setBackground(ContextCompat.getDrawable(mContext,R.drawable.rounded_sent));
                messageText.setEmojiSize(60);
            }
            messageText.setText(message.getMessage());
        }
    }

    private class ReceivedMessageHolder extends RecyclerView.ViewHolder {
        TextView timeText, nameText;EmojiTextView messageText;
        CircleImageView profileImage;

        ReceivedMessageHolder(View itemView) {
            super(itemView);

            messageText = itemView.findViewById(R.id.text_message_body);
            timeText = itemView.findViewById(R.id.text_message_time);
            nameText = itemView.findViewById(R.id.text_message_name);
            profileImage = itemView.findViewById(R.id.image_message_profile);
        }

        void bind(SocketMessage message,int position) {

            timeText.setText(Methods.getTimeStamp(Long.valueOf(message.getTime()),formatter));
            nameText.setText(oppositeUser.getUSERNAME_IN_PHONE());
            profileImage.setImageBitmap(Converter.Base642Bitmap(oppositeUser.getBASE64()));

            if(position!=0){
                SocketMessage message1 = mMessageList.get(position-1);
                if(message1.getSender().equals(message.getSender())){
                    nameText.setVisibility(View.GONE);
                    profileImage.setVisibility(View.INVISIBLE);
                }else {
                    nameText.setVisibility(View.VISIBLE);
                    profileImage.setVisibility(View.VISIBLE);
                }
            }

            if(isEmoji(message.getMessage())) {
                messageText.setBackgroundColor(Color.argb(0,0,0,0));
                messageText.setEmojiSize(120);
                if(isEmojiis(message.getMessage())) {
                    messageText.setEmojiSize(300);
                }
            } else {
                messageText.setBackground(ContextCompat.getDrawable(mContext,R.drawable.chat_message_rounded));
                messageText.setEmojiSize(60);
            }
            messageText.setText(message.getMessage());
        }
    }

    private void UpdateMsgStatus(final TextView view)
    {
        FirebaseDatabase.getInstance().getReference("MsgStatus").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(oppositeUid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String status = dataSnapshot.getValue(String.class);
                view.setText(status);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    private static boolean isEmoji(String message){
        return message.matches("(?:[\uD83C\uDF00-\uD83D\uDDFF]|[\uD83E\uDD00-\uD83E\uDDFF]|" +
                "[\uD83D\uDE00-\uD83D\uDE4F]|[\uD83D\uDE80-\uD83D\uDEFF]|" +
                "[\u2600-\u26FF]\uFE0F?|[\u2700-\u27BF]\uFE0F?|\u24C2\uFE0F?|" +
                "[\uD83C\uDDE6-\uD83C\uDDFF]{1,2}|" +
                "[\uD83C\uDD70\uD83C\uDD71\uD83C\uDD7E\uD83C\uDD7F\uD83C\uDD8E\uD83C\uDD91-\uD83C\uDD9A]\uFE0F?|" +
                "[\u0023\u002A\u0030-\u0039]\uFE0F?\u20E3|[\u2194-\u2199\u21A9-\u21AA]\uFE0F?|[\u2B05-\u2B07\u2B1B\u2B1C\u2B50\u2B55]\uFE0F?|" +
                "[\u2934\u2935]\uFE0F?|[\u3030\u303D]\uFE0F?|[\u3297\u3299]\uFE0F?|" +
                "[\uD83C\uDE01\uD83C\uDE02\uD83C\uDE1A\uD83C\uDE2F\uD83C\uDE32-\uD83C\uDE3A\uD83C\uDE50\uD83C\uDE51]\uFE0F?|" +
                "[\u203C\u2049]\uFE0F?|[\u25AA\u25AB\u25B6\u25C0\u25FB-\u25FE]\uFE0F?|" +
                "[\u00A9\u00AE]\uFE0F?|[\u2122\u2139]\uFE0F?|\uD83C\uDC04\uFE0F?|\uD83C\uDCCF\uFE0F?|" +
                "[\u231A\u231B\u2328\u23CF\u23E9-\u23F3\u23F8-\u23FA]\uFE0F?)+");
    }
    private static boolean isEmojiis(String message){
        return message.matches("(?:[\u2764\uFE0F\u2665]){2}");
    }
}