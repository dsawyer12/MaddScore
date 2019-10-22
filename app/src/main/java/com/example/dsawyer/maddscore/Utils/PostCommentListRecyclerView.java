package com.example.dsawyer.maddscore.Utils;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.dsawyer.maddscore.Objects.User;
import com.example.dsawyer.maddscore.R;
import com.example.dsawyer.maddscore.Objects.PostComment;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class PostCommentListRecyclerView extends RecyclerView.Adapter<PostCommentListRecyclerView.ViewHolder>{
    private static final String TAG = "TAG";

    private ArrayList<PostComment> comments;
    private User mUser;
    private Context context;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClicked(View v, int position);
    }

    public PostCommentListRecyclerView(Context context, ArrayList<PostComment> comments, User mUser) {
        Log.d(TAG, "POST LIST SIZE: " + comments.size());
        this.comments = comments;
        this.context = context;
        this.mUser = mUser;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.snippet_post_comment_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        Log.d(TAG, "onBindViewHolder: called");

        holder.username.setText(comments.get(position).getUsername() + " - " + comments.get(position).getName());
        holder.commentBody.setText(comments.get(position).getCommentBody());
        holder.commentDate.setText(String.valueOf(comments.get(position).getCommentDate()));

        if (comments.get(position).getPhotoID() != null){
            Glide.with(context).load(comments.get(position).getPhotoID()).into(holder.profileImage);
        }
        else{
            Glide.with(context).load(R.mipmap.default_profile_img).into(holder.profileImage);
        }
    }
    @Override
    public int getItemCount() {
        return comments.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        CircleImageView profileImage;
        TextView username, commentDate, commentBody;

        public ViewHolder(View itemView) {
            super(itemView);

            username = itemView.findViewById(R.id.usernameAndName);
            commentDate = itemView.findViewById(R.id.comment_date);
            commentBody = itemView.findViewById(R.id.comment_body);
            profileImage = itemView.findViewById(R.id.user_image);
        }
    }
}
