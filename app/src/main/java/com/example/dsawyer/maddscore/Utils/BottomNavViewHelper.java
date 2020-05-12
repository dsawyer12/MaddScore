package com.example.dsawyer.maddscore.Utils;


import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.example.dsawyer.maddscore.Courses.CoursesActivity;
import com.example.dsawyer.maddscore.Players.PlayersActivity;
import com.example.dsawyer.maddscore.Profile.ProfileActivity;
import com.example.dsawyer.maddscore.R;
import com.example.dsawyer.maddscore.ScoreCards.ScorecardActivity;
import com.example.dsawyer.maddscore.Squad.SquadActivity;

public abstract class BottomNavViewHelper extends AppCompatActivity {

    public static void enableBottomNavView(final Context context, BottomNavigationView view){
        view.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.bottom_nav_profile:
                        Intent intent1 = new Intent(context, ProfileActivity.class);
                        context.startActivity(intent1);
                        intent1.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        break;
                    case R.id.bottom_nav_players:
                        Intent intent2 = new Intent(context, PlayersActivity.class);
                        context.startActivity(intent2);
                        intent2.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        break;
                    case R.id.bottom_nav_courses:
                        Intent intent3 = new Intent(context, CoursesActivity.class);
                        context.startActivity(intent3);
                        intent3.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        break;
                    case R.id.bottom_nav_social:
                        Intent intent4 = new Intent(context, SquadActivity.class);
                        context.startActivity(intent4);
                        intent4.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        break;
                    case R.id.bottom_nav_cards:
                        Intent intent5 = new Intent(context, ScorecardActivity.class);
                        context.startActivity(intent5);
                        intent5.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        break;
                }
                return false;
            }
        });
    }
}
