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

public class NewScorecardPlayerSelectActivity extends AppCompatActivity implements
        View.OnClickListener {
    private static final String TAG = "TAG";

    Course course;
    ImageView back_btn;
    Button start_round;
    TextView cardName, numHoles;

    ArrayList<User> cardUsers;
    ArrayList<User> cardSquadUsers;
    ArrayList<User> customCardUsers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_scorecard_player_select);

        start_round = findViewById(R.id.start_round_btn);
        back_btn = findViewById(R.id.new_card_back_arrow);
        cardName = findViewById(R.id.card_course_name);
        numHoles = findViewById(R.id.card_course_size);
        back_btn.setOnClickListener(this);
        start_round.setOnClickListener(this);

        if (this.getIntent() != null) {
            course = this.getIntent().getParcelableExtra("courseSelected");
            cardName.setText(course.getName().trim());
            numHoles.setText(course.getNumHoles() + " holes");
        }

        setUpViewPager();

    }

    public void setUpViewPager() {
        ViewPager viewPager = findViewById(R.id.card_viewpager);
        SectionsPagerAdapter adapter = new SectionsPagerAdapter(this.getSupportFragmentManager());

        adapter.addFragment(new ScorecardPlayerListFragment());
        adapter.addFragment(new ScorecardSquadPlayerListFragment());
        viewPager.setAdapter(adapter);

        TabLayout tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        tabLayout.getTabAt(0).setText("All friends");
        tabLayout.getTabAt(1).setText("Squad");

        View root = tabLayout.getChildAt(0);
        if (root instanceof LinearLayout) {
            ((LinearLayout) root).setShowDividers(LinearLayout.SHOW_DIVIDER_MIDDLE);
            GradientDrawable drawable = new GradientDrawable();
            drawable.setColor(ContextCompat.getColor(this, R.color.grey));
            drawable.setSize(2, 0);
            ((LinearLayout) root).setDividerPadding(10);
            ((LinearLayout) root).setDividerDrawable(drawable);
        }
    }

    public void setFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().add(R.id.new_card_frame, fragment).addToBackStack(null).commit();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case(R.id.new_card_back_arrow):
                finish();
                break;

            case(R.id.start_round_btn):

                if (this.getIntent() != null) {
                    cardUsers = this.getIntent().getParcelableArrayListExtra("users");
                    cardSquadUsers = this.getIntent().getParcelableArrayListExtra("squadUsers");
                    if( (cardUsers == null || cardUsers.isEmpty())
                    && (cardSquadUsers == null || cardSquadUsers.isEmpty()))
                        Toast.makeText(this, "Select your players", Toast.LENGTH_SHORT).show();
                    else {
                        Intent intent = new Intent(this, CardActivity.class);
                        intent.putExtra("course", course);
                        intent.putParcelableArrayListExtra("users", cardUsers);
                        intent.putParcelableArrayListExtra("squadUsers", cardSquadUsers);

                        startActivity(intent);
                        finish();
                    }
                }
                break;
        }
    }
}




















