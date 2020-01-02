package com.hemangnh18.chatmate.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
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

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageListAdapter extends RecyclerView.Adapter {
    private static final int VIEW_TYPE_MESSAGE_SENT = 1;
    private static final int VIEW_TYPE_MESSAGE_RECEIVED = 2;

    private Context mContext;
    private User oppositeUser;

    private List<SocketMessage> mMessageList;

    public MessageListAdapter(Context context, List<SocketMessage> messageList, String oppositeUid) {
        mContext = context;
        mMessageList = messageList;
        DatabaseHandler databaseHandler = new DatabaseHandler(context);
        this.oppositeUser = databaseHandler.getUser(oppositeUid);
    }

    @Override
    public int getItemCount() {
        return mMessageList.size();
    }

    @Override
    public int getItemViewType(int position) {
        SocketMessage message = mMessageList.get(position);

        //TODO
        // Check whether message is sent or received
        if (message.getSender().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
            // If the current user is the sender of the message
            return VIEW_TYPE_MESSAGE_SENT;
        } else {
            // If some other user sent the message
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
            UpdateMsgStatus(msgStatus);

        }

        void bind(SocketMessage message,int position) {
            messageText.setText(message.getMessage());
            timeText.setText(message.getTime());
            if(position==mMessageList.size()-1)
            {
                msgStatus.setVisibility(View.VISIBLE);
            }
            else
            {
                msgStatus.setVisibility(View.GONE);
            }
        }
    }

    private class ReceivedMessageHolder extends RecyclerView.ViewHolder {
        TextView timeText, nameText;
        EmojiTextView messageText;
        CircleImageView profileImage;

        ReceivedMessageHolder(View itemView) {
            super(itemView);

            messageText = itemView.findViewById(R.id.text_message_body);
            timeText = itemView.findViewById(R.id.text_message_time);
            nameText = itemView.findViewById(R.id.text_message_name);
            profileImage = itemView.findViewById(R.id.image_message_profile);
        }

        void bind(SocketMessage message,int position) {
            messageText.setText(message.getMessage());
            timeText.setText(message.getTime());
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
        }
    }


    private void UpdateMsgStatus(final TextView view)
    {
        FirebaseDatabase.getInstance().getReference("MsgStatus").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(oppositeUser.getUSER_ID()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                String statusMsg = dataSnapshot.getValue(String.class);
                view.setText(statusMsg);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}