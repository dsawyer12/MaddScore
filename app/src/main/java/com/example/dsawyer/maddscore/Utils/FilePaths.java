package com.example.dsawyer.maddscore.Utils;

import android.os.Environment;

public class FilePaths {

    //storage/emulated/0
    public static String ROOT_DIR = Environment.getExternalStorageDirectory().getPath();

    public static String PICTURES = ROOT_DIR + "/Pictures";

    public static String CAMERA = ROOT_DIR + "/DCIM/camera";

    public static String FIREBASE_IMG_STORAGE = "photos/users/";
}
