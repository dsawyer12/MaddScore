package com.example.dsawyer.maddscore.Utils;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.dsawyer.maddscore.Objects.Message;
import com.example.dsawyer.maddscore.Objects.User;
import com.example.dsawyer.maddscore.R;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageListRecyclerView extends RecyclerView.Adapter<MessageListRecyclerView.ViewHolder> {
    private static final String TAG = "TAG";

    private Context context;
    private User[] recipients;
    private ArrayList<Message> messages;

    public MessageListRecyclerView(Context context, User[] recipients, ArrayList<Message> messages) {
        this.context = context;
        this.recipients = recipients;
        this.messages = messages;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        CircleImageView profileImage;
        TextView message_container;

        public ViewHolder(View itemView) {
            super(itemView);
            profileImage = itemView.findViewById(R.id.user_img);
            message_container = itemView.findViewById(R.id.message_container);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (messages.get(position).getType() == Message.TYPE_ONE)
            return Message.TYPE_ONE;
        else
            return Message.TYPE_TWO;
    }

    @NonNull
    @Override
    public MessageListRecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case(0):
                View view1 = LayoutInflater.from(parent.getContext()).inflate(R.layout.snippet_message_item_type_one, parent, false);
                return new ViewHolder(view1);

            case(1):
                View view2 = LayoutInflater.from(parent.getContext()).inflate(R.layout.snippet_message_item_type_two, parent, false);
                return new ViewHolder(view2);

            default:
                View view3 = LayoutInflater.from(parent.getContext()).inflate(R.layout.snippet_message_item_type_two, parent, false);
                return new ViewHolder(view3);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.message_container.setText(messages.get(holder.getAdapterPosition()).getMessageBody());

        switch (holder.getItemViewType()) {
            case(0):
                if (recipients[0].getPhotoUrl() != null) {
                    Glide.with(context).load(recipients[0].getPhotoUrl()).into(holder.profileImage);
                }
                else {
                    Glide.with(context).load(R.mipmap.default_profile_img).into(holder.profileImage);
                }
                break;

            case(1):
                if (recipients[1].getPhotoUrl() != null)
                    Glide.with(context).load(recipients[1].getPhotoUrl()).into(holder.profileImage);
                else
                    Glide.with(context).load(R.mipmap.default_profile_img).into(holder.profileImage);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

}













