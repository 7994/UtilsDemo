package com.example.mashuk.demo.utils;

import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.support.annotation.ArrayRes;
import android.support.annotation.ColorRes;
import android.support.annotation.DimenRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;
import android.support.v4.content.ContextCompat;


public class ResourceUtils {

    public static String getString(@StringRes int stringId) {
        return AppController.getInstance().getString(stringId);
    }

    public static String[] getStringArray(@ArrayRes int stringId) {
        return AppController.getInstance().getResources().getStringArray(stringId);
    }

    public static Drawable getDrawable(@DrawableRes int drawableId) {
        return ContextCompat.getDrawable(AppController.getInstance(), drawableId);
    }

    public static int getColor(@ColorRes int colorId) {
        return AppController.getInstance().getResources().getColor(colorId);
    }

    public static AssetManager getAsset() {
        return AppController.getInstance().getAssets();
    }

    public static int getDimen(@DimenRes int dimenId) {
        return (int) AppController.getInstance().getResources().getDimension(dimenId);
    }

    public static int dpToPx(int dp) {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }

    public static int pxToDp(int px) {
        return (int) (px / Resources.getSystem().getDisplayMetrics().density);
    }

    public static Drawable changeDrawableColorFromImage(Context context, int icon, int newColor) {
        Drawable mDrawable = ContextCompat.getDrawable(context, icon).mutate();
        mDrawable.setColorFilter(new PorterDuffColorFilter(newColor, PorterDuff.Mode.SRC_IN));
        return mDrawable;
    }

    public static void changeDrawableColor(Context context, Drawable drawable, int newColor) {
        drawable.setColorFilter(new PorterDuffColorFilter(context.getResources().getColor(newColor), PorterDuff.Mode.SRC_IN));
    }

}
