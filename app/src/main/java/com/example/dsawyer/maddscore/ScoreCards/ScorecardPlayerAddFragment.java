package com.example.dsawyer.maddscore.ScoreCards;

import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dsawyer.maddscore.Objects.Course;
import com.example.dsawyer.maddscore.Objects.User;
import com.example.dsawyer.maddscore.Players.CustomPlayerFragment;
import com.example.dsawyer.maddscore.R;
import com.example.dsawyer.maddscore.Utils.SectionsPagerAdapter;

import java.util.ArrayList;

public class ScorecardPlayerAddFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = "TAG";

    private Course course;

    TextView course_name, course_num_holes;
    ImageView back_btn;
    Button start_round;

    public ScorecardPlayerAddFragment() {
        super();
        setArguments(new Bundle());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_scorecard_player_add, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        course = getCourseFromBundle();

        back_btn = view.findViewById(R.id.back_btn);
        course_name = view.findViewById(R.id.card_course_name);
        course_num_holes = view.findViewById(R.id.card_course_holes);
        course_name.setText(course.getName());
        course_num_holes.setText(course.getNumHoles() + "Holes");

        start_round = view.findViewById(R.id.start_round);
        start_round.setOnClickListener(this);
        back_btn.setOnClickListener(this);

        ViewPager viewPager = view.findViewById(R.id.viewpager_container);
        SectionsPagerAdapter adapter = new SectionsPagerAdapter(getChildFragmentManager());
        adapter.addFragment(new ScorecardPlayerListFragment());
        adapter.addFragment(new ScorecardSquadPlayerListFragment());
        adapter.addFragment(new ScorecardCustomPlayerAddFragment());
        viewPager.setAdapter(adapter);

        TabLayout tabLayout = view.findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.getTabAt(0).setText("Friends");
        tabLayout.getTabAt(1).setText("Squad");
        tabLayout.getTabAt(2).setText("Custom");

        View root = tabLayout.getChildAt(0);
        if (root instanceof LinearLayout) {
            ((LinearLayout) root).setShowDividers(LinearLayout.SHOW_DIVIDER_MIDDLE);
            GradientDrawable drawable = new GradientDrawable();
            drawable.setColor(ContextCompat.getColor(getActivity(), R.color.grey));
            drawable.setSize(2, 0);
            ((LinearLayout) root).setDividerPadding(10);
            ((LinearLayout) root).setDividerDrawable(drawable);
        }
    }

    private Course getCourseFromBundle() {
        Bundle bundle = this.getArguments() ;
        if (bundle != null)
            return bundle.getParcelable("course");
        else
            return null;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case(R.id.start_round):
                ArrayList<User> cardUsers = getActivity().getIntent().getParcelableArrayListExtra("users");
                ArrayList<User> cardSquadUsers = getActivity().getIntent().getParcelableArrayListExtra("squadUsers");

                        if (!cardUsers.isEmpty() || !cardSquadUsers.isEmpty()) {
                            Bundle args = new Bundle();
                            args.putParcelable("course", course);
                            args.putSerializable("users", cardUsers);
                            args.putSerializable("squadUsers", cardSquadUsers);
                            Intent intent = new Intent(getActivity(), CardActivity.class).putExtras(args);
                            startActivity(intent);
                        }
                        else {
                            Toast.makeText(getActivity(), "Select Players", Toast.LENGTH_LONG).show();
                            //make the button non clickable and set the background color different
                        }
                    break;

            case(R.id.back_btn):
                getActivity().getSupportFragmentManager().popBackStackImmediate();
                break;
        }
    }
}
















