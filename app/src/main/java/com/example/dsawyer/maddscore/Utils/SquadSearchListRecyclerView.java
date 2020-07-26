package com.example.dsawyer.maddscore.Utils;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.dsawyer.maddscore.R;
import com.example.dsawyer.maddscore.Objects.Squad;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class SquadSearchListRecyclerView extends RecyclerView.Adapter<SquadSearchListRecyclerView.ViewHolder>{
    private static final String TAG = "TAG";

    private Context context;
    private ArrayList<Squad> squadList;

    public interface OnSquadSelectedListener{
        void onSquadSelected(Squad squad);
    }
    OnSquadSelectedListener listener;

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.snippet_squad_list_item, parent, false);
        return (new ViewHolder(view));
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
//        holder.squad_name.setText(squadList.get(position).getSquadName());
//        holder.creator_username.setText(squadList.get(position).getCreatorUsername());
//        holder.creator_name.setText(squadList.get(position).getCreatorName());
//        holder.num_members.setText(String.valueOf(squadList.get(position).getUserList().size()));
//
//        if (squadList.get(position).getCreatorPhotoId() != null){
//            Glide.with(context).load(squadList.get(position).getCreatorPhotoId()).into(holder.creator_photId);
//        }
//        else{
//            Glide.with(context).load(R.mipmap.default_profile_img).into(holder.creator_photId);
//        }
//
        holder.rootView.setOnClickListener(v -> listener.onSquadSelected(squadList.get(holder.getAdapterPosition())));
    }

    @Override
    public int getItemCount() {
        return squadList.size();
    }

    public SquadSearchListRecyclerView(Context context, ArrayList<Squad> squadList, OnSquadSelectedListener listener) {
        this.context = context;
        this.squadList = squadList;
        this.listener = listener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        LinearLayout rootView;
        TextView squad_name, creator_name, creator_username, num_members;
        CircleImageView creator_photId;

        public ViewHolder(View itemView) {
            super(itemView);
            rootView = itemView.findViewById(R.id.rootView);
            squad_name = itemView.findViewById(R.id.squad_name);
            creator_name = itemView.findViewById(R.id.squad_creator_name);
            creator_username = itemView.findViewById(R.id.squad_creator_username);
            num_members = itemView.findViewById(R.id.sqaud_num_members);
            creator_photId = itemView.findViewById(R.id.squad_creator_photoId);
        }
    }
}
