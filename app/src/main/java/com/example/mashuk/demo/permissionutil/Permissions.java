package com.example.mashuk.demo.permissionutil;

import android.Manifest;

/**
 * Permissions.java : This class is used to initialize Runtime permission
 * @author : Harsh Patel
 * @version : 1.0.0
 * @Date : 05/06/2017
 * @Change History :
 * <p>
 * {Change ID:#} 05/06/2017 :
 */
public class Permissions {

    public static final String[] STORAGE_PERMISSIONS = new String[] {
            Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA
    };

    public static final String[] LOCATION_PERMISSIONS = new String[] {
            Manifest.permission.ACCESS_FINE_LOCATION
    };
}
