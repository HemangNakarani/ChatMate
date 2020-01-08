package com.hemangnh18.chatmate.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.github.tntkhang.fullscreenimageview.library.FullScreenImageViewActivity;
import com.hemangnh18.chatmate.Classes.DisplayRecent;
import com.hemangnh18.chatmate.Classes.Methods;
import com.hemangnh18.chatmate.Classes.User;
import com.hemangnh18.chatmate.Compressing.Converter;
import com.hemangnh18.chatmate.ImageViewer.FullScreenImageViewActivity2;
import com.hemangnh18.chatmate.MessageActivity;
import com.hemangnh18.chatmate.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatListAdapter extends RecyclerView.Adapter<ChatListAdapter.ViewHolder>implements Filterable {

    private Context mContext;
    private List<DisplayRecent> mUsers;
    private List<DisplayRecent> data_dummy;
    private SimpleDateFormat formatter;

    public ChatListAdapter(Context mContext, List<DisplayRecent> users)
    {
        this.mContext= mContext;
        this.mUsers = users;
        this.data_dummy = users;
        String dateFormat = "hh:mm aa dd/MM/yyyy";
        formatter = new SimpleDateFormat(dateFormat);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.contact_item_chat_list,parent,false);
        return new ChatListAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {

        final int k = position;

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it= new Intent(mContext,MessageActivity.class);
                it.putExtra("Opposite",mUsers.get(k).getId());
                mContext.startActivity(it);
            }
        });
        holder.username.setText(mUsers.get(k).getUsername());

        if(mUsers.get(k).getBase64().equals("Default")) {
                holder.dp.setImageResource(R.drawable.man);
        } else {
            holder.dp.setImageBitmap(Converter.Base642Bitmap(mUsers.get(k).getBase64()));
        }
        holder.timestamp.setText(Methods.getTimeStamp(Long.valueOf(mUsers.get(k).getTime()),formatter));

        holder.dp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //TODO DP Click Lisner
                //String dwn = mUsers.get(k).get();
//                if(dwn.equals("Default"))
//                {
//
//                }
//                else
//                {
//                    Intent fullImageIntent = new Intent(mContext, FullScreenImageViewActivity2.class);
//                    ArrayList<String> uriString = new ArrayList<>();
//                    uriString.add(dwn);
//                    fullImageIntent.putExtra(FullScreenImageViewActivity.URI_LIST_DATA, uriString);
//                    fullImageIntent.putExtra(FullScreenImageViewActivity2.URI_DOWNLOAD_DATA, uriString);
//                    fullImageIntent.putExtra(FullScreenImageViewActivity.IMAGE_FULL_SCREEN_CURRENT_POS, 0);
//                    mContext.startActivity(fullImageIntent);
//                }
            }
        });
        holder.lastMessage.setText(mUsers.get(k).getMessage());
    }
    @Override
    public int getItemCount() {
        return mUsers.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        private TextView username,lastMessage,timestamp;
        private CircleImageView dp,dot;
        private CardView cardView;

        public ViewHolder(View itemView)
        {
            super(itemView);
            username = itemView.findViewById(R.id.username_contact_item);
            lastMessage = itemView.findViewById(R.id.message_contact_item);
            dp = itemView.findViewById(R.id.contacts_dp);
            cardView = itemView.findViewById(R.id.contect_card);
            dot = itemView.findViewById(R.id.pending_message_circleImageView);
            timestamp = itemView.findViewById(R.id.timestamp_contact_item);
        }
    }

    @Override
    public Filter getFilter() {
        return examplefilter;
    }

    private Filter examplefilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<DisplayRecent> filtered = new ArrayList<>();

            if(constraint == null || constraint.length()==0){
                filtered.addAll(data_dummy);
                Toast.makeText(mContext,""+data_dummy.size()+" "+mUsers.size(),Toast.LENGTH_SHORT).show();
            }else {
                String check = constraint.toString().toLowerCase().trim();

                for(DisplayRecent items:data_dummy){
                    if(items.getUsername().toLowerCase().contains(check)){
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
