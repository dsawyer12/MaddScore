package com.example.dsawyer.maddscore.Squad;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.dsawyer.maddscore.Objects.Pin;
import com.example.dsawyer.maddscore.Objects.Post;
import com.example.dsawyer.maddscore.Objects.Squad;
import com.example.dsawyer.maddscore.Objects.User;
import com.example.dsawyer.maddscore.R;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class AboutSquadSubFragment extends Fragment {
    private static final String TAG = "LOG";

    LinearLayout pinRoot, post_root;
    TextView squad_description, pin_since, pin_snippet, post_username, post_name, post_snippet;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.sub_fragment_about_squad, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        pinRoot = view.findViewById(R.id.pin_root);
        post_root = view.findViewById(R.id.post_root);

        squad_description = view.findViewById(R.id.squad_description);
        pin_since = view.findViewById(R.id.pin_since);
        pin_snippet = view.findViewById(R.id.pin_snippet);
        post_username = view.findViewById(R.id.post_username);
        post_name = view.findViewById(R.id.post_name);
        post_snippet = view.findViewById(R.id.post_snippet);
    }

    public void initData(Squad squad, Pin pin, User user) {
        if (squad != null) {

            if (!squad.getDescription().isEmpty())
                squad_description.setText(squad.getDescription());
            else
                squad_description.setText("No description is set for this squad.");
        }

        if (pin != null) {
            pinRoot.setVisibility(View.VISIBLE);

            SimpleDateFormat sdf = new SimpleDateFormat("dd/MMM/YYYY", Locale.US);
            String date = sdf.format(pin.getPinDate());

            pin_since.setText("since - " + date);
            pin_snippet.setText(pin.getSnippet());

            if (pin.getPost() != null && user != null) {
                post_root.setVisibility(View.VISIBLE);

                post_username.setText(user.getUsername());
                post_name.setText(user.getName());
                post_snippet.setText(pin.getPost().getPostBody());
            }
        }

    }
}
