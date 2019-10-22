package com.example.dsawyer.maddscore.UselessButGoodInfo;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.dsawyer.maddscore.R;
import com.example.dsawyer.maddscore.Utils.SectionsPagerAdapter;

import java.io.IOException;

public class EditProfileImageActivity extends AppCompatActivity{
    private static final String TAG = "TAG";

    private Uri filePath;
    private final int PICK_IMAGE_REQUEST = 71;
    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile_image);

        imageView = findViewById(R.id.imageView);
        TextView finish = findViewById(R.id.finish_gallery_btn);
        finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImage();
            }
        });

//        setUpViewPager();
    }

//    private void setUpViewPager() {
//        SectionsPagerAdapter adapter = new SectionsPagerAdapter(getSupportFragmentManager());
//        adapter.addFragment(new GalleryFragment());
//        adapter.addFragment(new CameraFragment());
//        ViewPager viewPager = findViewById(R.id.viewpager_container);
//        viewPager.setAdapter(adapter);
//
//        TabLayout tabLayout = findViewById(R.id.tabsBottom);
//        tabLayout.setupWithViewPager(viewPager);
//
//        tabLayout.getTabAt(0).setText("Gallery");
//        tabLayout.getTabAt(1).setText("Camera");
//
//        View root = tabLayout.getChildAt(0);
//        if (root instanceof LinearLayout){
//            ((LinearLayout) root).setShowDividers(LinearLayout.SHOW_DIVIDER_MIDDLE);
//            GradientDrawable drawable = new GradientDrawable();
//            drawable.setColor(getResources().getColor(R.color.grey));
//            drawable.setSize(2, 0);
//            ((LinearLayout) root).setDividerPadding(10);
//            ((LinearLayout) root).setDividerDrawable(drawable);
//        }
//    }

    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null )
        {
            filePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                imageView.setImageBitmap(bitmap);
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }
}
