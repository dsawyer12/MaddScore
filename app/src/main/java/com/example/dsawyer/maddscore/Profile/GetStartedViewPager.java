package com.example.dsawyer.maddscore.Profile;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.dsawyer.maddscore.Other.ApplicationClass;
import com.example.dsawyer.maddscore.R;
import com.example.dsawyer.maddscore.Other.GetStartedSliderAdapter;

import java.util.Objects;

public class GetStartedViewPager extends Fragment{
    private static final String TAG = "TAG";

    private ViewPager viewPager;
    private LinearLayout linearLayout;
    private GetStartedSliderAdapter adapter;
    private TextView[] dots;
    private Button back, next;
    private int page;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_get_started_viewpager, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewPager = view.findViewById(R.id.get_started_viewpager);
        linearLayout = view.findViewById(R.id.get_started_linLayout);

        adapter = new GetStartedSliderAdapter(getActivity());
        viewPager.setAdapter(adapter);

        addDots(0);

        viewPager.addOnPageChangeListener(viewListener);

        back = view.findViewById(R.id.backBtn);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewPager.setCurrentItem(page - 1);
            }
        });

        next = view.findViewById(R.id.nextBtn);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(next.getText().equals("FINISH")){
                    Log.d(TAG, "onClick: next button equals finish");
                    (Objects.requireNonNull(getActivity())).getSupportFragmentManager().popBackStackImmediate();
                }
                viewPager.setCurrentItem(page + 1);
            }
        });

    }

    public void addDots(int position){
        dots = new TextView[3];
        linearLayout.removeAllViews();

        for (int i = 0; i<dots.length; i++){
            dots[i] = new TextView(getActivity());
            dots[i].setText(Html.fromHtml("&#8226;"));
            dots[i].setTextSize(35);
            dots[i].setTextColor(getResources().getColor(R.color.grey));
            linearLayout.addView(dots[i]);
        }
        if(dots.length>0){
            dots[position].setTextColor(getResources().getColor(R.color.white));
        }
    }

    ViewPager.OnPageChangeListener viewListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }
        @Override
        public void onPageSelected(int position) {
            addDots(position);
            page = position;
            if (position == 0){
                back.setEnabled(false);
                back.setVisibility(View.GONE);
                back.setText("");
                next.setEnabled(true);
                next.setText("NEXT");
            } else if (position == dots.length - 1) {
                back.setEnabled(true);
                back.setVisibility(View.VISIBLE);
                next.setEnabled(true);

                next.setText("FINISH");
                back.setText("BACK");
            }
            else{
                back.setEnabled(true);
                back.setVisibility(View.VISIBLE);
                back.setText("BACK");
                next.setEnabled(true);
                next.setText("NEXT");
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };
}
