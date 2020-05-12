package com.example.dsawyer.maddscore.Utils;

import android.content.Context;
import android.content.SharedPreferences;

public class ApplicationPreferences {
    private static SharedPreferences sharedPreferences;
    public static final String GET_STARTED_SCREEN = "get_started_screen";
    public static final String HIDE_SQUAD_CARD = "hide_squad_card";
    public static final String HIDE_STATS_CARD = "hide_stats_card";

    public static final int USER_TASK_KEY = 0;
    public static final int USER_STATS_TASK_KEY = 1;

    public static void getSharedPreferences(Context context, String preferences) {
        sharedPreferences = context.getSharedPreferences(preferences, Context.MODE_PRIVATE);
    }

    public static void setPreference(String preference, boolean value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(preference, value).apply();
    }

    public static boolean getProfilePreferences(String preferences) {
        return sharedPreferences.getBoolean(preferences, false);
    }
}
