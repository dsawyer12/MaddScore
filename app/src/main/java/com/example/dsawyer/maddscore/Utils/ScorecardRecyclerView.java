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
import com.example.dsawyer.maddscore.Objects.Course;
import com.example.dsawyer.maddscore.Objects.Scorecard;
import com.example.dsawyer.maddscore.Objects.User;
import com.example.dsawyer.maddscore.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ScorecardRecyclerView extends RecyclerView.Adapter<ScorecardRecyclerView.ViewHolder> {
    private static final String TAG = "TAG";

    Context context;
    ArrayList<Scorecard> scorecards;

    public ScorecardRecyclerView(Context context) {
        this.context = context;
        this.scorecards = new ArrayList<>();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        SimpleDateFormat sdf = new SimpleDateFormat("EEE, MMM dd, yyyy", Locale.US);
        TextView course_name, date, numPlayers;
        LinearLayout linearLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            linearLayout = itemView.findViewById(R.id.linearLayout);
            course_name = itemView.findViewById(R.id.course_name);
            date = itemView.findViewById(R.id.card_date);
            numPlayers = itemView.findViewById(R.id.num_players);
        }
    }

    public void addItems(ArrayList<Scorecard> scorecards) {
        int initSize = this.scorecards.size();
        this.scorecards.addAll(scorecards);
        notifyItemRangeChanged(initSize, scorecards.size());
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int position) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_scorecard_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.course_name.setText(scorecards.get(holder.getAdapterPosition()).getCourse().getName());
        holder.date.setText(holder.sdf.format(scorecards.get(holder.getAdapterPosition()).getDateCreated()));
        holder.numPlayers.setText(scorecards.get(holder.getAdapterPosition()).getCardObject().getUsers().size() + " Players");

//        Query query;
//        for (Map.Entry<String, Boolean> entry : scorecards.get(holder.getAdapterPosition()).getCardObject().getUsers().entrySet()) {
//            query = ref.child("users").child(entry.getKey());
//            query.addListenerForSingleValueEvent(new ValueEventListener() {
//                @Override
//                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                    User user = dataSnapshot.getValue(User.class);
//                    if (user != null)
//                        scorecards.get(holder.getAdapterPosition()).addUser(user);
//
//
//                        Log.d(TAG, "users is greater than 5");
//                        for (int i = 0; i < scorecards.get(holder.getAdapterPosition()).getCardObject().getUsers().size(); i++) {
//                            View view = inflater.inflate(R.layout.snippet_card_player_item, null);
//                            view.setLayoutParams(new LinearLayout.LayoutParams(
//                                    ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, 0));
//                            ((TextView) view.findViewById(R.id.player_username)).setText(user.getUsername());
//
//                            if (user.getPhotoUrl() != null) {
//                                Glide.with(context).load(user.getPhotoUrl())
//                                        .into( ((CircleImageView) view.findViewById(R.id.player_img)) );
//                            }
//                            else {
//                                Glide.with(context).load(R.mipmap.default_profile_img)
//                                        .into( ((CircleImageView) view.findViewById(R.id.player_img)) );
//                            }
//
//                            holder.linearLayout.addView(view);
//                        }
//                }
//
//                @Override
//                public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                }
//            });
//        }
    }

    @Override
    public int getItemCount() {
        return scorecards.size();
    }

}











