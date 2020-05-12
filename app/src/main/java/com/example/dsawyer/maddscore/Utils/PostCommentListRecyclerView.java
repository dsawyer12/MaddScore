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
import com.example.dsawyer.maddscore.Objects.PostCommentUserMap;
import com.example.dsawyer.maddscore.Objects.User;
import com.example.dsawyer.maddscore.R;
import com.example.dsawyer.maddscore.Objects.PostComment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class PostCommentListRecyclerView extends RecyclerView.Adapter<PostCommentListRecyclerView.ViewHolder> {
    private static final String TAG = "TAG";

    private Context context;
    private ArrayList<PostCommentUserMap> comments;
    private SimpleDateFormat sdf;
    private String dateString;

    public PostCommentListRecyclerView(Context context) {
        this.comments = new ArrayList<>();
        this.context = context;
        sdf = new SimpleDateFormat("MMM dd 'at' h:mm a", Locale.US);
//        this.listener = listener;
    }

    public void addItems(ArrayList<PostCommentUserMap> comments) {
        Log.d(TAG, "addItems: called");
        int initSize = this.comments.size();
        this.comments.addAll(comments);
        notifyItemRangeChanged(initSize, comments.size());
    }

    public ArrayList<PostCommentUserMap> getComments() {
        return comments;
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

        holder.username.setText(comments.get(position).getUsername());
        if (comments.get(position).getPhotoUrl() != null)
            Glide.with(context).load(comments.get(position).getPhotoUrl()).into(holder.user_image);
        else
            Glide.with(context).load(R.mipmap.default_profile_img).into(holder.user_image);

        holder.commentBody.setText(comments.get(position).getPostComment().getCommentBody());
        dateString = sdf.format(comments.get(position).getPostComment().getCommentDate());
        holder.commentDate.setText(dateString);

    }
    @Override
    public int getItemCount() {
        return comments.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        CircleImageView user_image;
        TextView username, commentDate, commentBody;

        public ViewHolder(View itemView) {
            super(itemView);

            username = itemView.findViewById(R.id.usernameAndName);
            commentDate = itemView.findViewById(R.id.comment_date);
            commentBody = itemView.findViewById(R.id.comment_body);
            user_image = itemView.findViewById(R.id.user_image);
        }
    }
}
