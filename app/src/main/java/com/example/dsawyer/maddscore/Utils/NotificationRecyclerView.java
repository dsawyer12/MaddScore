package com.example.dsawyer.maddscore.Utils;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.dsawyer.maddscore.ObjectMaps.NotificationUserMap;
import com.example.dsawyer.maddscore.Objects.Notification;
import com.example.dsawyer.maddscore.R;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class NotificationRecyclerView extends RecyclerView.Adapter<NotificationRecyclerView.ViewHolder> {
    private static final String TAG = "TAG";

    private Context context;
    private ArrayList<NotificationUserMap> myNotifications;

    public interface OnRequestClickListener {
        void onRequestClicked(Notification n);
        void onRequestAccept(Notification n);
        void onRequestDeclined(Notification n);
    }
    private OnRequestClickListener listener;

    public NotificationRecyclerView(Context context, OnRequestClickListener listener, ArrayList<NotificationUserMap> myNotifications) {
        this.context = context;
        this.myNotifications = myNotifications;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.snippet_notification_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {

        /** If the notification type is a friend request **/
        if (myNotifications.get(holder.getAdapterPosition()).getNotification().getNotificationType() == Notification.FRIEND_REQUEST) {
            holder.senderName.setText(myNotifications.get(holder.getAdapterPosition()).getName());
            if (myNotifications.get(position).getPhotoUrl() != null)
                Glide.with(context).load(myNotifications.get(position).getPhotoUrl()).into(holder.userImage);
            else
                Glide.with(context).load(R.mipmap.default_profile_img).into(holder.userImage);
            holder.text.setText("sent you a friend request!");
        }
        /** If the notification type is a squad invite **/
        else if (myNotifications.get(holder.getAdapterPosition()).getNotification().getNotificationType() == Notification.SQUAD_INVITE) {
            holder.senderName.setText(myNotifications.get(holder.getAdapterPosition()).getName());
            if (myNotifications.get(position).getPhotoUrl() != null)
                Glide.with(context).load(myNotifications.get(position).getPhotoUrl()).into(holder.userImage);
            else
                Glide.with(context).load(R.mipmap.default_profile_img).into(holder.userImage);
            holder.text.setText("invited you to join their Squad!");
            holder.snippet.setText(myNotifications.get(holder.getAdapterPosition()).getSquadName());
        }
        /** If the notification type is a squad request **/
        else if (myNotifications.get(holder.getAdapterPosition()).getNotification().getNotificationType() == Notification.SQUAD_REQUEST) {
            holder.accept.setVisibility(View.VISIBLE);
            holder.decline.setVisibility(View.VISIBLE);
            holder.senderName.setText(myNotifications.get(position).getName());
            if (myNotifications.get(position).getPhotoUrl() != null)
                Glide.with(context).load(myNotifications.get(position).getPhotoUrl()).into(holder.userImage);
            else
                Glide.with(context).load(R.mipmap.default_profile_img).into(holder.userImage);
            holder.text.setText("wants to join your Squad!");
        }
        /** If the notification type is a message **/
        else if (myNotifications.get(holder.getAdapterPosition()).getNotification().getNotificationType() == Notification.MESSAGE) {
            holder.accept.setVisibility(View.GONE);
            holder.decline.setVisibility(View.GONE);
            holder.senderName.setText(myNotifications.get(holder.getAdapterPosition()).getName());
            if (myNotifications.get(holder.getAdapterPosition()).getPhotoUrl() != null)
                Glide.with(context).load(myNotifications.get(position).getPhotoUrl()).into(holder.userImage);
            else
                Glide.with(context).load(R.mipmap.default_profile_img).into(holder.userImage);
            holder.text.setText("sent you a message.");
            holder.snippet.setText(myNotifications.get(holder.getAdapterPosition()).getNotification().getSnippet());
        }

        holder.accept.setOnClickListener(v -> listener.onRequestAccept(myNotifications.get(holder.getAdapterPosition()).getNotification()));

        holder.decline.setOnClickListener(v -> listener.onRequestDeclined(myNotifications.get(holder.getAdapterPosition()).getNotification()));

        holder.rootView.setOnClickListener(v -> {
            if (myNotifications.get(holder.getAdapterPosition()).getNotification().getNotificationType() == Notification.MESSAGE) {
                listener.onRequestClicked(myNotifications.get(holder.getAdapterPosition()).getNotification());
            }
        });
    }

    @Override
    public int getItemCount() {
        return myNotifications.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        CardView rootView;
        TextView senderName, text, snippet;
        CircleImageView userImage;
        Button accept, decline;

        public ViewHolder(View itemView) {
            super(itemView);
            rootView = itemView.findViewById(R.id.rootView);
            senderName = itemView.findViewById(R.id.sender_name);
            userImage = itemView.findViewById(R.id.user_img);
            text = itemView.findViewById(R.id.request_text);
            snippet = itemView.findViewById(R.id.snippet);
            accept = itemView.findViewById(R.id.accept_request_btn);
            decline = itemView.findViewById(R.id.decline_request_btn);
        }
    }
}
