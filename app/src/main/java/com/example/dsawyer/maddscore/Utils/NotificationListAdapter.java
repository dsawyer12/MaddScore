package com.example.dsawyer.maddscore.Utils;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.dsawyer.maddscore.Other.ApplicationClass;
import com.example.dsawyer.maddscore.Objects.Notification;
import com.example.dsawyer.maddscore.Objects.User;
import com.example.dsawyer.maddscore.R;
import com.example.dsawyer.maddscore.Objects.Squad;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class NotificationListAdapter extends BaseAdapter {
    private static final String TAG = "TAG";

    private DatabaseReference ref;
    private static final int FRIEND_REQUEST = 1;
    private static final int SQUAD_INVITE = 2;
    private ArrayList<Notification> myNotifications;
    private ArrayList<User> userList;

    private LayoutInflater inflater;
    private Context context;

    private class ViewHolder {
        TextView senderName, text, squadname;
        CircleImageView userImage;
        Button accept, decline;
    }

    public NotificationListAdapter(Context context, ArrayList<Notification> myNotifications, ArrayList<User> userList) {
        inflater = LayoutInflater.from(context);
        this.myNotifications = myNotifications;
        this.userList = userList;
        this.context = context;
    }

    @Override
    public int getCount() {
        return myNotifications.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }

    public View getView(final int position, View convertView, final ViewGroup parent) {
        ViewHolder holder = null;
        if(convertView == null) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.snippet_notification_list, null);
            holder.senderName = convertView.findViewById(R.id.sender_name);
            holder.userImage = convertView.findViewById(R.id.user_img);
            holder.text = convertView.findViewById(R.id.request_text);
            holder.squadname = convertView.findViewById(R.id.squad_name);

            holder.accept = convertView.findViewById(R.id.accept_request_btn);
            holder.accept.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((ListView) parent).performItemClick(v, position, 0);
                }
            });
            holder.decline = convertView.findViewById(R.id.decline_request_btn);
            holder.decline.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((ListView) parent).performItemClick(v, position, 0);
                }
            });
                convertView.setTag(holder);
        }
        else {
            holder = (ViewHolder) convertView.getTag();
        }
        if (myNotifications.get(position).getNotificationType() == FRIEND_REQUEST){
            holder.senderName.setText(userList.get(position).getName());
            if (userList.get(position).getPhotoUrl() != null){
                Glide.with(context).load(userList.get(position).getPhotoUrl()).into(holder.userImage);
            }
            else{
                Glide.with(context).load(R.mipmap.default_profile_img).into(holder.userImage);
            }
            holder.text.setText("sent you a friend request!");
        }

        else if (myNotifications.get(position).getNotificationType() == SQUAD_INVITE){
            holder.senderName.setText(userList.get(position).getName());
            if (userList.get(position).getPhotoUrl() != null){
                Glide.with(context).load(userList.get(position).getPhotoUrl()).into(holder.userImage);
            }
            else{
                Glide.with(context).load(R.mipmap.default_profile_img).into(holder.userImage);
            }
            holder.text.setText("Invited you to thier Squad!");

            ref = FirebaseDatabase.getInstance().getReference()
                    .child(ApplicationClass.getContext().getResources().getString(R.string.firebase_node_squads))
                    .child(userList.get(position).getSquad());
            Query query = ref;
            final ViewHolder finalHolder = holder;
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()){
                        Squad squad = dataSnapshot.getValue(Squad.class);
                        if (squad != null){
                            finalHolder.squadname.setText(squad.getSquadName());
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
        return convertView;
    }
}
