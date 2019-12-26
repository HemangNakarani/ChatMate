package com.hemangnh18.chatmate.Adapters;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.provider.ContactsContract;
import android.util.Base64;
import android.util.Log;
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
import com.github.tntkhang.fullscreenimageview.library.FullScreenImageViewActivity;
import com.hemangnh18.chatmate.Classes.ContactDb;
import com.hemangnh18.chatmate.Classes.User;
import com.hemangnh18.chatmate.Compressing.Converter;
import com.hemangnh18.chatmate.ImageViewer.FullScreenImageViewActivity2;
import com.hemangnh18.chatmate.MainActivity;
import com.hemangnh18.chatmate.R;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
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
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {

        final int k =position;
        holder.cardView.setAnimation(AnimationUtils.loadAnimation(mContext,R.anim.fade_scale_animation));
        holder.username.setAnimation(AnimationUtils.loadAnimation(mContext,R.anim.fade_scale_animation));
        holder.ststus.setAnimation(AnimationUtils.loadAnimation(mContext,R.anim.fade_scale_animation));


        String pp=getContactName(mUsers.get(k).getPHONE(),mContext);
        if(!pp.equals(""))
        {
            holder.username.setText(pp);
        }
        else
        {
            holder.username.setText(mUsers.get(k).getPHONE());
        }

        if(mUsers.get(k).getBASE64().equals("Default"))
        {
            if(mUsers.get(k).getGENDER().equals("Male"))
            {
                holder.dp.setImageResource(R.drawable.man);
            }
            else
            {
                holder.dp.setImageResource(R.drawable.girl);
            }
        }
        else
        {
            holder.dp.setImageBitmap(Converter.Base642Bitmap(mUsers.get(k).getBASE64()));
        }

        holder.dp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String dwn = mUsers.get(k).getDOWNLOAD();
                if(dwn.equals("Default"))
                {

                }
                else
                {
                    Intent fullImageIntent = new Intent(mContext, FullScreenImageViewActivity2.class);
                    ArrayList<String> uriString = new ArrayList<>();
                    uriString.add(dwn);
                    fullImageIntent.putExtra(FullScreenImageViewActivity.URI_LIST_DATA, uriString);
                    fullImageIntent.putExtra(FullScreenImageViewActivity2.URI_DOWNLOAD_DATA, uriString);
                    fullImageIntent.putExtra(FullScreenImageViewActivity.IMAGE_FULL_SCREEN_CURRENT_POS, 0);
                    mContext.startActivity(fullImageIntent);
                }
            }
        });

        holder.ststus.setText(mUsers.get(k).getSTATUS());
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


    public String getContactName(final String phoneNumber, Context context)
    {
        Uri uri= Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI,Uri.encode(phoneNumber));

        String[] projection = new String[]{ContactsContract.PhoneLookup.DISPLAY_NAME};

        String contactName="";
        Cursor cursor=context.getContentResolver().query(uri,projection,null,null,null);

        if (cursor != null) {
            if(cursor.moveToFirst()) {
                contactName=cursor.getString(0);
            }
            cursor.close();
        }

        return contactName;
    }

    boolean hasConnection()
    {
        ConnectivityManager cm =
                (ConnectivityManager)mContext.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();

        if(isConnected) {return  true;}
        else {return false;}
    }
}
