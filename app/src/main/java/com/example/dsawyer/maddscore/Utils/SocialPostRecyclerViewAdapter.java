package com.example.dsawyer.maddscore.Utils;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.dsawyer.maddscore.Objects.Post;
import com.example.dsawyer.maddscore.ObjectMaps.PostUserMap;
import com.example.dsawyer.maddscore.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class SocialPostRecyclerViewAdapter extends RecyclerView.Adapter<SocialPostRecyclerViewAdapter.ViewHolder> {

    private static final String TAG = "TAG";

    private String userID;
    private ArrayList<PostUserMap> posts;
    private SimpleDateFormat sdf;
    private Context context;

    public interface OnItemClickListener {
        void onItemClicked(View v, int position);
    }
    private OnItemClickListener listener;

    public SocialPostRecyclerViewAdapter(Context context, OnItemClickListener listener, String userID) {
        Log.d(TAG, "SocialPostRecyclerView: created");
        this.posts = new ArrayList<>();
        this.userID = userID;
        this.listener = listener;
        this.context = context;

        sdf = new SimpleDateFormat("MMM dd 'at' h:mm a", Locale.US);
    }

    public void addItems(ArrayList<PostUserMap> posts) {
        Log.d(TAG, "addItems: called");
        int initSize = this.posts.size();
        this.posts.addAll(posts);
        notifyItemRangeChanged(initSize, posts.size());
    }

    public void addItem(PostUserMap post) {
        Log.d(TAG, "addItem: called");
        int initSize = this.posts.size();
        this.posts.add(0, post);
        notifyItemRangeChanged(initSize, posts.size());
    }

    public ArrayList<PostUserMap> getPosts() {
        return posts;
    }

    public Post getPost(int position) {
        return posts.get(position).getPost();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private static final String TAG = "ViewHolder created";

        CircleImageView user_image;
        ImageView post_options, post_like_img, post_comment_img;
        RelativeLayout post_like, post_comment;
        TextView username, name, postDate, postBody, numLikes, numComments;

        public ViewHolder(View itemView) {
            super(itemView);
            Log.d(TAG, "ViewHolder: created");

            user_image = itemView.findViewById(R.id.user_img);
            post_options = itemView.findViewById(R.id.post_options);
            username = itemView.findViewById(R.id.player_username);
            name = itemView.findViewById(R.id.player_name);
            postDate = itemView.findViewById(R.id.post_date);
            postBody = itemView.findViewById(R.id.post_body);
            post_like = itemView.findViewById(R.id.post_like);
            post_like_img = itemView.findViewById(R.id.post_like_img);
            numLikes = itemView.findViewById(R.id.num_post_likes);
            post_comment = itemView.findViewById(R.id.post_comment);
            post_comment_img = itemView.findViewById(R.id.post_comment_img);
            numComments = itemView.findViewById(R.id.num_post_comments);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d(TAG, "onCreateViewHolder: called");
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.snippet_post_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        Log.d(TAG, "onBindViewHolder: called");

        holder.username.setText(posts.get(holder.getAdapterPosition()).getUsername());
        holder.name.setText(posts.get(holder.getAdapterPosition()).getName());
        if (posts.get(holder.getAdapterPosition()).getPhotoUrl() != null)
            Glide.with(context).load(posts.get(holder.getAdapterPosition()).getPhotoUrl()).into(holder.user_image);
        else
            Glide.with(context).load(R.mipmap.default_profile_img).into(holder.user_image);

        String dateString = sdf.format(posts.get(holder.getAdapterPosition()).getPost().getDateCreated());
        holder.postDate.setText("   -   " + dateString);
        holder.postBody.setText(posts.get(holder.getAdapterPosition()).getPost().getPostBody());

        if (posts.get(holder.getAdapterPosition()).getPost().getUserLikesList() != null) {

            if (posts.get(holder.getAdapterPosition()).getPost().getUserLikesList().size() == 0) {
                holder.post_like_img.setColorFilter(ContextCompat.getColor(context, R.color.white));
                holder.numLikes.setText("");
            } else
                holder.numLikes.setText(String.valueOf(posts.get(holder.getAdapterPosition()).getPost().getUserLikesList().size()));

            if (posts.get(holder.getAdapterPosition()).getPost().getUserLikesList().containsKey(userID))
                holder.post_like_img.setColorFilter(ContextCompat.getColor(context, R.color.colorPrimary));
            else
                holder.post_like_img.setColorFilter(ContextCompat.getColor(context, R.color.white));

        } else {
            holder.post_like_img.setColorFilter(ContextCompat.getColor(context, R.color.white));
            holder.numLikes.setText("");
        }

        if (posts.get(holder.getAdapterPosition()).getPost().getComments() != null) {
            if (posts.get(holder.getAdapterPosition()).getPost().getComments().size() == 0)
                holder.numComments.setText("");
            else
                holder.numComments.setText(String.valueOf(posts.get(holder.getAdapterPosition()).getPost().getComments().size()));

        } else holder.numComments.setText("");

        if (posts.get(holder.getAdapterPosition()).getPost().getCreatorID().equals(userID))
            holder.post_options.setVisibility(View.VISIBLE);
        else  holder.post_options.setVisibility(View.GONE);

        holder.post_like.setOnClickListener(v -> {
            if(listener != null) listener.onItemClicked(v, holder.getAdapterPosition());
        });
        holder.post_comment.setOnClickListener(v -> listener.onItemClicked(v, holder.getAdapterPosition()));
        holder.post_options.setOnClickListener(v -> listener.onItemClicked(v, holder.getAdapterPosition()));
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

}
