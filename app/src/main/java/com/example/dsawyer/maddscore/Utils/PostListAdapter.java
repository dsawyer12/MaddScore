package com.example.dsawyer.maddscore.Utils;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.example.dsawyer.maddscore.R;
import com.example.dsawyer.maddscore.Objects.Post;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;

public class PostListAdapter extends BaseAdapter {
    private static final String TAG = "TAG";

    private DatabaseReference ref;
    private ArrayList<Post> posts;
    private String mUser;
    private Context context;

    private LayoutInflater inflater;

    public class ViewHolder {
        TextView textView1, textView2, textView3, textView4, textView5, textView6;
        Button join, members_joined;
        ImageView joined, post_like;
        LinearLayout liked, comment;
    }

    public PostListAdapter(Context context, String mUser, ArrayList<Post> posts) {
        inflater = LayoutInflater.from(context);
        this.context = context;
        this.mUser = mUser;
        this.posts = posts;
    }

    @Override
    public int getCount() {
        return posts.size();
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
                convertView = inflater.inflate(R.layout.snippet_post_list_item, null);
                holder.textView1 = convertView.findViewById(R.id.player_username);
                holder.textView2 = convertView.findViewById(R.id.player_name);
                holder.textView3 = convertView.findViewById(R.id.post_body);
                holder.textView4 = convertView.findViewById(R.id.post_date);
                holder.textView5 = convertView.findViewById(R.id.post_likes);
                holder.textView6 = convertView.findViewById(R.id.post_comments);
                holder.liked = convertView.findViewById(R.id.post_liked);
                holder.comment = convertView.findViewById(R.id.post_comment);

                holder.liked.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ((ListView) parent).performItemClick(v, position,0);
                    }
                });
                holder.comment.setOnClickListener(new View.OnClickListener() {
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

//        if (posts.get(position).getCheck()){
//            holder.textView1.setText(posts.get(position).getCreatorUsername());
//            holder.textView2.setText(posts.get(position).getCreatorName());
//            holder.textView3.setText(posts.get(position).getPostBody());
//            holder.textView4.setText(posts.get(position).getPostLocation());
//            holder.textView5.setText(posts.get(position).getPostTime());
//            holder.textView6.setText("posted - " + posts.get(position).getDateCreated());
//
//            if (posts.get(position).getUserlist() != null) {
//                if (!posts.get(position).getUserlist().isEmpty()) {
//                    holder.members_joined.setText(posts.get(position).getUserlist().size() + " members joined");
//                } else {
//                    holder.members_joined.setText("No members joined");
//                }
//                if (posts.get(position).getUserlist().containsKey(mUser)){
//                    holder.joined.setVisibility(View.VISIBLE);
//                }
//                else {
//                    holder.joined.setVisibility(View.GONE);
//                }
//            }
//            else{
//                holder.members_joined.setText("No members joined");
//            }
//        }
//        else{
            if (posts.get(position).getUserlist() != null) {
                if (!posts.get(position).getUserlist().isEmpty()) {
                    holder.textView5.setText(String.valueOf(posts.get(position).getUserlist().size()) + " Likes");
                } else {
                    holder.textView5.setText("");
                }
                if (posts.get(position).getUserlist().containsKey(mUser)){
                    holder.post_like.setColorFilter(ContextCompat.getColor(convertView.getContext(), R.color.colorPrimary));
                }
                else {
                    holder.post_like.setColorFilter(ContextCompat.getColor(convertView.getContext(), R.color.white));
                }
            }
            else{
                holder.textView5.setText("");
            }
//            holder.textView1.setText(posts.get(position).getCreatorUsername());
//            holder.textView2.setText(posts.get(position).getCreatorName());
            holder.textView3.setText(posts.get(position).getPostBody());
            holder.textView4.setText("posted - " + posts.get(position).getDateCreated());
//        }
        return convertView;
    }

    @Override
    public boolean isEnabled(int position) {
        return false;
    }
}
