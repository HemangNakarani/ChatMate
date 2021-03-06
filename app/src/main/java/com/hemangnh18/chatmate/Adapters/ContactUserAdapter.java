package com.hemangnh18.chatmate.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.github.tntkhang.fullscreenimageview.library.FullScreenImageViewActivity;
import com.hemangnh18.chatmate.Classes.User;
import com.hemangnh18.chatmate.Compressing.Converter;
import com.hemangnh18.chatmate.Database.MidChatHelper;
import com.hemangnh18.chatmate.ImageViewer.FullScreenImageViewActivity2;
import com.hemangnh18.chatmate.MessageActivity;
import com.hemangnh18.chatmate.R;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ContactUserAdapter extends RecyclerView.Adapter<ContactUserAdapter.ViewHolder> implements Filterable {

    private Context mContext;
    private List<User> mUsers;
    private List<User> data_dummy;

    public ContactUserAdapter(Context mContext, List<User> users)
    {
        this.mContext= mContext;
        this.mUsers = users;
        data_dummy = new ArrayList<>(users);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       View view = LayoutInflater.from(mContext).inflate(R.layout.user_contacts_layout,parent,false);
       return new ContactUserAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {

        final int k = position;
        holder.cardView.setAnimation(AnimationUtils.loadAnimation(mContext,R.anim.fade_scale_animation));
        holder.username.setAnimation(AnimationUtils.loadAnimation(mContext,R.anim.fade_scale_animation));
        holder.ststus.setAnimation(AnimationUtils.loadAnimation(mContext,R.anim.fade_scale_animation));

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent it= new Intent(mContext,MessageActivity.class);
                it.putExtra("Opposite",mUsers.get(k).getUSER_ID());
                mContext.startActivity(it);

            }
        });


        holder.username.setText(mUsers.get(k).getUSERNAME_IN_PHONE());

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

    @Override
    public Filter getFilter() {
        return examplefilter;
    }

    private Filter examplefilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<User> filtered = new ArrayList<>();

            if(constraint == null || constraint.length()==0){
                filtered.addAll(data_dummy);
            }else {
                String check = constraint.toString().toLowerCase().trim();

                for(User items:data_dummy){
                    if(items.getUSERNAME_IN_PHONE().toLowerCase().contains(check)){
                        filtered.add(items);
                    }
                }
            }

            FilterResults results = new FilterResults();
            results.values = filtered;

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            mUsers.clear();
            mUsers.addAll((List) results.values);
            notifyDataSetChanged();
        }
    };

}
