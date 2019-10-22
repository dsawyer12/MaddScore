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
import com.example.dsawyer.maddscore.R;
import com.example.dsawyer.maddscore.Objects.Post;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class SocialPostRecyclerView extends RecyclerView.Adapter<SocialPostRecyclerView.ViewHolder>{
    private static final String TAG = "TAG";

    private Context context;
    private String userID;
    private ArrayList<Post> posts;

    public interface OnItemClickListener {
        void onItemClicked(View v, int position);
    }
    private OnItemClickListener listener;

    public SocialPostRecyclerView(Context context, OnItemClickListener listener, ArrayList<Post> posts, String userID) {
        Log.d(TAG, "SocialPostRecyclerView: created");
        this.posts = posts;
        this.context = context;
        this.userID = userID;
        this.listener = listener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private static final String TAG = "ViewHolder created";

        CircleImageView user_image;
        TextView username, name, postDate, postBody, numLikes, numComments, postCourse, postPlayDate, like, comment;
        LinearLayout linLayout;

        public ViewHolder(View itemView) {
            super(itemView);
            Log.d(TAG, "ViewHolder: created");

            user_image = itemView.findViewById(R.id.player_img);
            username = itemView.findViewById(R.id.player_username);
            name = itemView.findViewById(R.id.player_name);
            postDate = itemView.findViewById(R.id.post_date);
            postBody = itemView.findViewById(R.id.post_body);
            numLikes = itemView.findViewById(R.id.post_likes);
            numComments = itemView.findViewById(R.id.post_comments);
            postCourse = itemView.findViewById(R.id.post_course);
            postPlayDate = itemView.findViewById(R.id.post_play_date);
            like = itemView.findViewById(R.id.post_liked);
            comment = itemView.findViewById(R.id.post_comment);
            linLayout = itemView.findViewById(R.id.linearLayout);
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
//        holder.username.setText(posts.get(holder.getAdapterPosition()).getCreatorUsername());
//        holder.name.setText(posts.get(holder.getAdapterPosition()).getCreatorName());
        holder.postDate.setText("posted - " + posts.get(holder.getAdapterPosition()).getDateCreated());
        holder.postBody.setText(posts.get(holder.getAdapterPosition()).getPostBody());

//        if (posts.get(position).getCreatorPhoto() != null){
//            Glide.with(context).load(posts.get(position).getCreatorPhoto()).into(holder.user_image);
//        }
//        else{
//            Glide.with(context).load(R.mipmap.default_profile_img).into(holder.user_image);
//        }
//
//        if (posts.get(holder.getAdapterPosition()).getPostLocation() != null){
//            Log.d(TAG, "post location visible");
//            holder.postCourse.setVisibility(View.VISIBLE);
//            holder.postCourse.setText("Where - " + posts.get(holder.getAdapterPosition()).getPostLocation());
//        }
//        else{
//            holder.postCourse.setVisibility(View.GONE);
//        }
//
//        if (posts.get(holder.getAdapterPosition()).getPostTime() != null){
//            Log.d(TAG, "post time visible");
//            holder.postPlayDate.setVisibility(View.VISIBLE);
//            holder.postPlayDate.setText("When - " + posts.get(holder.getAdapterPosition()).getPostTime());
//        }
//        else{
//            holder.postPlayDate.setVisibility(View.GONE);
//        }

        if (posts.get(holder.getAdapterPosition()).getLikes() == 0 && posts.get(holder.getAdapterPosition()).getComments() == null){
            holder.linLayout.setVisibility(View.GONE);
        }
        else{
            holder.linLayout.setVisibility(View.VISIBLE);
        }
        if (posts.get(holder.getAdapterPosition()).getLikes() == 0){
            holder.numLikes.setText("");

        }
        else{
            holder.numLikes.setText(String.valueOf(posts.get(holder.getAdapterPosition()).getLikes()) + " likes");
        }

        if (posts.get(holder.getAdapterPosition()).getComments() != null){
            Log.d(TAG, "# of Comments: " + posts.get(holder.getAdapterPosition()).getComments().size());
            holder.numComments.setText(String.valueOf(posts.get(holder.getAdapterPosition()).getComments().size()) + " comments");
        }
        else{
            holder.numComments.setText("");
            Log.d(TAG, "No Comments");
        }

        if (posts.get(holder.getAdapterPosition()).getUserlist() != null){
            if (posts.get(holder.getAdapterPosition()).getUserlist().containsKey(userID)){
                holder.like.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_thumbs_up_green, 0, 0, 0);
            }
            else{
                holder.like.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_thumbs_up_white, 0, 0, 0);

            }
        }
        else{
            holder.like.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_thumbs_up_white, 0, 0, 0);

        }

        holder.like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(listener != null)
                    listener.onItemClicked(v, holder.getAdapterPosition());
            }
        });
        holder.comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onItemClicked(v, holder.getAdapterPosition());
            }
        });
        holder.numComments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onItemClicked(v, holder.getAdapterPosition());
            }
        });
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

}
