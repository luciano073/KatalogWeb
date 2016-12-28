package br.com.katalog.katalogweb.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;

/**
 * Created by luciano on 25/09/2016.
 */

public class Permissions {

    public static boolean hasStoragePermission(Context ctx){
        return ActivityCompat.checkSelfPermission(
                ctx, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }

    public static void requestStoragePermission(Activity activity, int requestCode){
        ActivityCompat.requestPermissions(
                activity,
                new String[]{ Manifest.permission.WRITE_EXTERNAL_STORAGE },
                requestCode);
    }

    public static boolean hasInternetPermission(Context ctx){
        return ActivityCompat.checkSelfPermission(
                ctx,
                Manifest.permission.INTERNET) == PackageManager.PERMISSION_GRANTED;
    }

    public static void requestInternetPersmission(Activity activity, int requestCode){
        ActivityCompat.requestPermissions(
                activity,
                new String[]{ Manifest.permission.INTERNET},
                requestCode
        );
    }

    public static boolean hasGetAccountPermission(Context ctx){
        return ActivityCompat.checkSelfPermission(
                ctx,
                Manifest.permission.GET_ACCOUNTS)
                == PackageManager.PERMISSION_GRANTED;
    }

    public static void requestGetAccountPermission(Activity activity, int requestCode){
        ActivityCompat.requestPermissions(
                activity,
                new String[]{Manifest.permission.GET_ACCOUNTS},
                requestCode
        );
    }
}
