package com.example.dsawyer.maddscore.Squad;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.example.dsawyer.maddscore.R;

public class NoSquadActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "LOG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_no_squad);

        findViewById(R.id.join_squad_button).setOnClickListener(this);
        findViewById(R.id.create_squad_button).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case(R.id.join_squad_button):
                startActivity(new Intent(this, JoinSquadActivity.class));
                finish();
                break;

            case(R.id.create_squad_button):
                startActivity(new Intent(this, CreateSquadActivity.class));
                finish();
                break;
        }
    }
}
