package com.example.dsawyer.maddscore.Utils;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.dsawyer.maddscore.Objects.ActiveRequest;
import com.example.dsawyer.maddscore.Objects.User;
import com.example.dsawyer.maddscore.R;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class SquadAddMemberRecyclerView extends RecyclerView.Adapter<SquadAddMemberRecyclerView.ViewHolder> {
    private static final String TAG = "TAG";

    private final String CANCEL = "Cancel";
    private final String INVITE = "Invite";

    private Context context;
    private ArrayList<User> memberList;
    private ArrayList<ActiveRequest> activeRequests;
    private OnUserClickedListener listener;

    public interface OnUserClickedListener {
        void onInvite(User user);
        void onCancel(String userID, String notificationID);
    }

    public SquadAddMemberRecyclerView(Context context,
                                      ArrayList<User> memberList,
                                      ArrayList<ActiveRequest> activeRequests,
                                      OnUserClickedListener listener) {
        this.context = context;
        this.memberList = memberList;
        this.listener = listener;
        this.activeRequests = activeRequests;
    }

    public void updateList(ActiveRequest activeRequest, Boolean sent) {
        if (sent) {
            activeRequests.add(activeRequest);
            Log.d(TAG, "updateList: added active request to adapter");
        }
        notifyDataSetChanged();
    }

    public void removeRequest(String userID, String notificationID){
        for (int i = 0; i < activeRequests.size(); i++) {
            if (activeRequests.get(i).getReceiver().equals(userID)
                    && activeRequests.get(i).getNotificationID().equals(notificationID)) {
                activeRequests.remove(i);
                Log.d(TAG, "removeRequest: removed from adapter");
            }
            notifyDataSetChanged();
        }
    }

    public void replaceList(ArrayList<User> memberList, ArrayList<ActiveRequest> activeRequests) {
        this.memberList = memberList;
        this.activeRequests = activeRequests;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView playerUsername, playerName;
        CircleImageView profileImage;
        Button invite;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            playerUsername = itemView.findViewById(R.id.player_username);
            playerName = itemView.findViewById(R.id.player_name);
            profileImage = itemView.findViewById(R.id.user_img);
            invite = itemView.findViewById(R.id.invite_btn);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int position) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.snippet_squad_add_member_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        holder.playerUsername.setText(memberList.get(position).getUsername());
        holder.playerName.setText(memberList.get(position).getName());
        if (memberList.get(position).getPhotoUrl() != null)
            Glide.with(context).load(memberList.get(position).getPhotoUrl()).into(holder.profileImage);
        else
            Glide.with(context).load(R.mipmap.default_profile_img).into(holder.profileImage);

        if (!activeRequests.isEmpty()) {
            for (int i = 0; i < activeRequests.size(); i++) {
                if (activeRequests.get(i).getReceiver().equals(memberList.get(holder.getAdapterPosition()).getUserID())
                && activeRequests.get(i).getNotificationType() == 3)
                    holder.invite.setText(CANCEL);
                else
                    holder.invite.setText(INVITE);
            }
        }
        else
            holder.invite.setText(INVITE);

        holder.invite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.invite.getText().equals(INVITE)) {
                    listener.onInvite(memberList.get(holder.getAdapterPosition()));
                }
                else
                   for (int i = 0; i < activeRequests.size(); i++) {
                       if (activeRequests.get(i).getReceiver().equals(memberList.get(holder.getAdapterPosition()).getUserID())
                       && activeRequests.get(i).getNotificationType() == 3) {
                           listener.onCancel(memberList.get(holder.getAdapterPosition()).getUserID(),
                                   activeRequests.get(i).getNotificationID());
                       }
                   }
            }
        });
    }

    @Override
    public int getItemCount() {
        return memberList.size();
    }
}














