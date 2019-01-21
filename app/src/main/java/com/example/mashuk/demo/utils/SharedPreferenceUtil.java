package com.example.mashuk.demo.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * SharedPreferenceUtil.java : All required Methods related to SharedPreference.
 *
 * @author : Krupa Maradiya
 * @version : 1.0.0
 * @Date : 2/5/2017
 * @Change History :
 * {Change ID:#} :
 */
public class SharedPreferenceUtil {


    private static String TAG = "SharedPreferenceUtil";

    public static PreferenceManager mPreferenceManager;

    private SharedPreferences mSharedPreferences;
    private SharedPreferences.Editor prefsEditor;

    public static PreferenceManager getInstance() {
        return mPreferenceManager;
    }

    public SharedPreferenceUtil(Context context) {
        mSharedPreferences = context.getSharedPreferences(Constant.SHAREDPREFRENCE, Context.MODE_PRIVATE);
        prefsEditor = mSharedPreferences.edit();
    }

    public void setStringPreference(String key, String value) {
        prefsEditor.putString("" + key, value + "");
        prefsEditor.commit();
    }

    public String getStringPreference(String key) {
        return mSharedPreferences.getString("" + key, "");
    }

    public void setBooleanPreference(String key, Boolean value) {
        prefsEditor.putBoolean("" + key, value);
        prefsEditor.commit();
    }

    public Boolean getBooleanPreference(String key) {
        return mSharedPreferences.getBoolean("" + key, false);
    }

    public void setIntegerPreference(String key, Integer value) {
        prefsEditor.putInt("" + key, value);
        prefsEditor.commit();
    }

    public Integer getIntegerPreference(String key) {
        return mSharedPreferences.getInt("" + key, -1);
    }

    public void clearPreference(String key) {
        prefsEditor.remove("" + key);
        prefsEditor.commit();
    }

    public boolean checkKeyIsAvailableOrNot(String key) {
        return mSharedPreferences.contains(key + "");
    }

    public void clearAllPreferences() {
        prefsEditor.clear();
        prefsEditor.commit();
    }
}