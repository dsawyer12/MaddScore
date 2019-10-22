package com.example.dsawyer.maddscore.Other;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.dsawyer.maddscore.R;

public class GetStartedSliderAdapter extends PagerAdapter {
    private static final String TAG = "TAG";

    private Context context;

    public GetStartedSliderAdapter(Context context){
        this.context = context;
    }

    private int[] slide_images = {
            R.mipmap.triliogy,
            R.mipmap.squad,
            R.mipmap.leaderboards
    };

    private String[] slide_headings = {
            context.getString(R.string.welcome_user),
            context.getString(R.string.Squads),
            context.getString(R.string.Leaderboards)
    };

    private String[] slide_descriptions = {
            context.getString(R.string.get_started_welcome_message),
            context.getString(R.string.get_started_squads_message),
            context.getString(R.string.get_started_leaderboards_message)

    };

    @Override
    public int getCount() {
        return slide_headings.length;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return (view == object);
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view  = null;
        if (layoutInflater != null) {
            view = layoutInflater.inflate(R.layout.snippet_slide_layout, container, false);
        }
        else{
            Log.d(TAG, "instantiateItem: layoutInflater = null");
        }

        ImageView image = view.findViewById(R.id.slide_image);
        TextView heading = view.findViewById(R.id.heading1);
        TextView description = view.findViewById(R.id.description);

        image.setImageResource(slide_images[position]);
        heading.setText(slide_headings[position]);
        description.setText(slide_descriptions[position]);

        container.addView(view);

        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((RelativeLayout)object);
    }
}
