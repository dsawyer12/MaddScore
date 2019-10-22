package com.example.dsawyer.maddscore.ScoreCards;

import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;

import com.example.dsawyer.maddscore.Objects.Course;
import com.example.dsawyer.maddscore.Other.ApplicationClass;
import com.example.dsawyer.maddscore.R;
import com.example.dsawyer.maddscore.Utils.SectionsPagerAdapter;

public class NewScorecardCourseSelectActivity extends AppCompatActivity implements View.OnClickListener,
        NearbyCourseListFragment.OnNearbyCourseSelectedListener,
        FavoritesCourseListFragment.OnFavoriteCourseSelectedListener,
        CustomCourseSelectFragment.OnCustomCourseSelectedListener {
    private static final String TAG = "TAG";

    @Override
    public void onNearbyCourseSelected(Course course) {
        onCourseSelected(course);
    }

    @Override
    public void onFavoriteCourseSelected(Course course) {
        onCourseSelected(course);
    }

    @Override
    public void onCustomCourseSelected(Course course) {
        onCourseSelected(course);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_scorecard);

        findViewById(R.id.new_card_back_arrow).setOnClickListener(this);

        setUpVIewPager();
    }

    public void setUpVIewPager() {
        ViewPager viewPager = findViewById(R.id.card_viewpager);
        SectionsPagerAdapter pagerAdapter = new SectionsPagerAdapter(this.getSupportFragmentManager());
        pagerAdapter.addFragment(new NearbyCourseListFragment());
        pagerAdapter.addFragment(new FavoritesCourseListFragment());
        pagerAdapter.addFragment(new CustomCourseSelectFragment());
        viewPager.setAdapter(pagerAdapter);

        TabLayout tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.getTabAt(0).setText("Nearby");
        tabLayout.getTabAt(1).setText("Favorites");
        tabLayout.getTabAt(2).setText("Custom");

        View root = tabLayout.getChildAt(0);
        if (root instanceof LinearLayout) {
            ((LinearLayout) root).setShowDividers(LinearLayout.SHOW_DIVIDER_MIDDLE);
            GradientDrawable drawable = new GradientDrawable();
            drawable.setColor(ContextCompat.getColor(ApplicationClass.getContext(), R.color.grey));
            drawable.setSize(2, 0);
            ((LinearLayout) root).setDividerPadding(10);
            ((LinearLayout) root).setDividerDrawable(drawable);
        }
    }

    public void setFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().add(R.id.new_card_frame, fragment).addToBackStack(null).commit();
    }

    public void onCourseSelected(Course course) {
        Intent intent = new Intent(this, NewScorecardPlayerSelectActivity.class);
        intent.putExtra("courseSelected", course);
        startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case(R.id.new_card_back_arrow):
                finish();
                break;
        }
    }
}
