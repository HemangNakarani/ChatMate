package com.hemangnh18.chatmate.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hemangnh18.chatmate.Classes.User;
import com.hemangnh18.chatmate.R;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ContactUserAdapter extends RecyclerView.Adapter<ContactUserAdapter.ViewHolder> {


    private Context mContext;
    private List<User> mUsers;

    public ContactUserAdapter(Context mContext,List<User> users)
    {
        this.mContext= mContext;
        this.mUsers = users;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       View view = LayoutInflater.from(mContext).inflate(R.layout.user_contacts_layout,parent,false);
       return new ContactUserAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        holder.cardView.setAnimation(AnimationUtils.loadAnimation(mContext,R.anim.fade_scale_animation));
        holder.username.setAnimation(AnimationUtils.loadAnimation(mContext,R.anim.fade_scale_animation));
        holder.ststus.setAnimation(AnimationUtils.loadAnimation(mContext,R.anim.fade_scale_animation));

        holder.username.setText(mUsers.get(position).getUSERNAME());
        holder.ststus.setText(mUsers.get(position).getSTATUS());
    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {

        private TextView username,ststus;
        private CircleImageView dp;
        private CardView cardView;


        public ViewHolder(View itemView)
        {
            super(itemView);
            username = itemView.findViewById(R.id.contacts_username);
            ststus = itemView.findViewById(R.id.contacts_status);
            dp = itemView.findViewById(R.id.contacts_dp);
            cardView = itemView.findViewById(R.id.contacts_card);
        }

    }

    /*private void LastMessage(final String userid, final TextView last_message, final ImageView last_photo, final TextView last_time)
    {
        lastestmessage = "WelCome";
        lasttime="time";
        type = "text";
        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if(firebaseUser!=null)
        {
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Chats");
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for(DataSnapshot snapshot: dataSnapshot.getChildren())
                    {
                        Chats chats = snapshot.getValue(Chats.class);
                        if((chats.getReciever().equals(firebaseUser.getUid()) &&  chats.getSender().equals(userid)) || (chats.getSender().equals(firebaseUser.getUid()) &&  chats.getReciever().equals(userid)))
                        {
                            lastestmessage = chats.getMessage();
                            lasttime = chats.getTime();
                            type = chats.getType();
                        }
                    }

                    switch (type)
                    {
                        case "text":
                        {
                            switch (lastestmessage)
                            {
                                case "WelCome":
                                    last_message.setText("");
                                    break;
                                default:
                                    last_message.setText(lastestmessage);
                                    last_time.setText(lasttime);
                                    break;
                            }
                            last_photo.setVisibility(View.GONE);
                            break;
                        }
                        case "photo":
                        {
                            last_message.setText("");
                            last_time.setText(lasttime);
                            last_photo.setVisibility(View.VISIBLE);
                        }
                    }


                    lastestmessage="WelCome";
                    type="text";
                    lasttime="time";

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }*/
}
