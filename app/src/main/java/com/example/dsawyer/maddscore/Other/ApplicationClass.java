package com.example.dsawyer.maddscore.Other;

import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;

import com.google.firebase.database.FirebaseDatabase;

public class ApplicationClass extends Application {
    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        context = this;
    }

    public static Context getContext(){
        return context;
    }
}