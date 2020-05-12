package com.example.dsawyer.maddscore.Utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;

public class Permissions {

    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    public static final int PERMISSION_REQUEST_CODE = 7426;
    public static final int PICK_IMAGE_REQUEST = 71;

    public static final String[] CAMERA_PERMISSIONS = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA
    };

    public static final String[] LOCATION_PERMISSIONS = {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
    };

    public static boolean checkCameraPermissions(Context context) {
        for (int i = 0; i < CAMERA_PERMISSIONS.length; i++) {
            String permission = CAMERA_PERMISSIONS[i];
            if (!checkPermission(context, permission))
                return false;
        }
        return true;
    }

    public static void  verifyCameraPermissions(Activity activity) {
        ActivityCompat.requestPermissions(activity, CAMERA_PERMISSIONS, PICK_IMAGE_REQUEST);
    }

    public static boolean checkPermissionsArray(Context context, String[] permissions) {
        for (int i = 0; i < permissions.length; i++) {
            String check = permissions[i];
            if (!checkPermission(context, check))
                return false;
        }
        return true;
    }

    public static boolean checkPermission(Context context, String permission) {
        int permissionRequest = ActivityCompat.checkSelfPermission(context, permission);
        if (permissionRequest != PackageManager.PERMISSION_GRANTED)
            return false;
        else
            return true;
    }
}
