package com.example.mashuk.demo.utils;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.location.LocationManager;
import android.media.ExifInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Base64;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.mashuk.demo.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.GoogleMap;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * {@link CommonUtils} : this class is used to declare common methods.
 */
public class CommonUtils {

    public final static Pattern INVALID_EMAIL_PATTERN = Pattern.compile("^[0-9-]+[_0-9-]*(\\.[_0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$");
    public final static Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9-]+[_A-Za-z0-9-]*(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$");
    public final static Pattern PASSWORD_VALIDATION = Pattern.compile("[A-Za-z0-9\\@\\#\\_\\'\\^\\*\\=\\:\\-\\+\\`]+$");
    public final static Pattern FIRST_LAST_NAME_PATTERN = Pattern.compile("^[A-Za-z0-9]+[A-Za-z-\\.\\-\\_\\']*$");
    public static final int REQUEST_CAMERA = 1234;
    public static final int SELECT_FILE = 4321;
    public static Uri imageURI;
    public static String mCurrentPhotoPath;
    private static File mFinalFile;

    private static boolean mDoubleBackToExitPressedOnce = false;

    /**
     * changes status bar color
     * @param statusColor value of color want to set to status bar
     */
    public static void setStatusBarColor(Activity activity, int statusColor) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = activity.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(statusColor);
        }
    }

    /**
     * checks if the string is blank.
     * @param string the string to be checked.
     * @return true if string is blank else false
     */
    public static boolean isNullString(String string) {
        try {
            return string == null || string.trim().equalsIgnoreCase("null") || string.trim().length() < 0
                    || string.trim().equals("");
        } catch (Exception e) {
            return true;
        }
    }

    /**
     * to display toast on screen
     */
    public static Void displayToast(Context context, String strToast) {
        Toast.makeText(context, strToast, Toast.LENGTH_SHORT).show();
        return null;
    }

    /**
     * checks if the email is correct or not
     * @return true if email is correct else false
     */
    public static boolean checkEmail(String email) {
        return !INVALID_EMAIL_PATTERN.matcher(email).matches() && EMAIL_PATTERN.matcher(email).matches();
    }

    /**
     * checks if the password is correct or not
     * @return true if password matches the pattern
     */
    public static boolean checkPassword(String password) {
        return PASSWORD_VALIDATION.matcher(password).matches();
    }

    /**
     * to check if there is internet connection or not
     */
    public static boolean isInternetAvailable(Context context) {
        try {
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = cm.getActiveNetworkInfo();
            if (netInfo != null && netInfo.isConnectedOrConnecting()) {
                return true;
            } else {
                displayToast(context, context.getString(R.string.str_no_internet));
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * checks if the name is correct or not
     * @return true if name matches the pattern
     */
    public static boolean checkFirstLastName(String name) {
        return FIRST_LAST_NAME_PATTERN.matcher(name).matches();
    }

    /**
     * loads image in the imageview passed in parameter
     */
    public static void loadImage(Context context, String url, ImageView imageView) {
        Glide.with(context)
                .load(url)
                .apply(new RequestOptions().placeholder(R.drawable.ic_launcher_background))
                .into(imageView);
    }

//    implementation 'com.github.bumptech.glide:glide:4.7.1'
//    implementation 'de.hdodenhof:circleimageview:2.2.0'
//    implementation 'com.google.code.gson:gson:2.8.2'

    public static void loadCircularImage(Context context, String url, CircleImageView imageView) {
        Glide.with(context)
                .load(url)
                .apply(new RequestOptions().placeholder(R.mipmap.ic_launcher_round))
                .into(imageView);
    }

    /**
     * This method is used to Format date in desired format.
     * @return formatted date in string form
     */
    public static String dateFormatter(String dateFromJSON, String expectedFormat, String oldFormat) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(oldFormat);
        Date date;
        String convertedDate = null;
        try {
            date = dateFormat.parse(dateFromJSON);
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(expectedFormat);
            convertedDate = simpleDateFormat.format(date);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return convertedDate;
    }

    public static Bitmap getRotatedBitmap(String path, Bitmap bitmap) {
        Bitmap rotatedBitmap = bitmap;
        Matrix matrix = new Matrix();
        ExifInterface exif = null;
        int orientation = 1;
        try {
            if (path != null) {
                // Getting Exif information of the file
                exif = new ExifInterface(path);
            }
            if (exif != null) {
                orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 0);
                switch (orientation) {
                    case ExifInterface.ORIENTATION_ROTATE_270:
                        matrix.preRotate(270);
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_90:
                        matrix.preRotate(90);
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_180:
                        matrix.preRotate(180);
                        break;
                }
                // Rotates the image according to the orientation
                rotatedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight()
                        , matrix, true);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return rotatedBitmap;
    }

    public static String getFilePath(Context context, Uri data) {
        String path = "";

        // For non-gallery application
        path = data.getPath();

        // For gallery application
        String[] filePathColumn = {MediaStore.Images.Media.DATA};
        Cursor cursor = context.getContentResolver().query(data, filePathColumn, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            path = cursor.getString(columnIndex);
            cursor.close();
        }
        return path;
    }

    public static Class<?> getActivityClassName(String activityName) {
        Class<?> classByName = null;
        try {
            classByName = Class.forName(activityName);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return classByName;
    }

    public static void quitApp(Activity context) {
        if (mDoubleBackToExitPressedOnce && context != null) {
            (context).finish();
        }

        if (!mDoubleBackToExitPressedOnce) {
            mDoubleBackToExitPressedOnce = true;
            try {
                displayToast(context,context.getString(R.string.back_press_toast)+" "+context.getString(R.string.app_name));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                mDoubleBackToExitPressedOnce = false;
            }
        }, 1900);
    }

    public static Fragment getFragmentFromClassName(String strFragmentPath) {
        Fragment classByName = null;
        try {
            classByName = (Fragment) Class.forName(strFragmentPath).newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return classByName;
    }

    public static void showSoftKeyboard(Context context) {
        try {
            InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
            inputMethodManager.toggleSoftInputFromWindow(((Activity) context).getCurrentFocus().getWindowToken(), InputMethodManager.SHOW_FORCED, 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void hideSoftKeyboard(Context context) {
        try {
            InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(((Activity) context).getCurrentFocus().getWindowToken(), 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void setFullScreen(Activity activity) {
        Window window = activity.getWindow();
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    /**
     * @purpose : method is used to transaction between
     * one activity to another
     */
    public static void passIntent(Context context, Class className) {
        Intent intent = new Intent(context, className);
        context.startActivity(intent);
    }

    public static boolean isPlayServiceAvailable(Activity context) {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(context);

        if (ConnectionResult.SUCCESS == resultCode) {
            return true;
        } else {
            GooglePlayServicesUtil.getErrorDialog(resultCode, context, 0).show();
            return false;
        }
    }

    public static boolean checkGPS(Context context) {
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            return true;
            //Toast.makeText(getActivity(), "GPS is Enabled in your devide", Toast.LENGTH_SHORT).show();
        } else {
            showGPSDisabledAlertToUser(context);
            return false;
        }
    }

    public static void showGPSDisabledAlertToUser(final Context context) {
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(context);
        //builder.setTitle("");
        builder.setMessage("GPS is disabled. Enable it to get location.");
        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int flag) {
                Intent callGPSSettingIntent = new Intent(
                        android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                context.startActivity(callGPSSettingIntent);
                dialogInterface.dismiss();
            }
        });
        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int flag) {
                dialogInterface.dismiss();
            }
        });

        android.support.v7.app.AlertDialog alertDialog = builder.create();
        alertDialog.setCancelable(false);
        alertDialog.show();
    }

    public static void moveToLocation(GoogleMap googleMap, com.google.android.gms.maps.model.LatLng latLng, float zoomLevel, boolean isAnimate) {
        if (googleMap == null) {
            return;
        }

        com.google.android.gms.maps.model.CameraPosition position = new com.google.android.gms.maps.model.CameraPosition.Builder()
                .target(latLng) // Sets the new camera position
                .zoom(zoomLevel) // Sets the zoom// Set the camera tilt
                .build(); // Creates a CameraPosition from the builder

        if (isAnimate) {
            googleMap.animateCamera(com.google.android.gms.maps.CameraUpdateFactory
                    .newCameraPosition(position));
        } else {
            googleMap.moveCamera(com.google.android.gms.maps.CameraUpdateFactory
                    .newCameraPosition(position));
        }
    }

    public static String imageToBase64(File fileName) {
        String encodedString = "";
        try {
            InputStream inputStream = new FileInputStream(fileName);//You can get an inputStream using any IO API
            byte[] bytes;
            byte[] buffer = new byte[8192];
            int bytesRead;
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            try {
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    output.write(buffer, 0, bytesRead);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            bytes = output.toByteArray();
            encodedString = Base64.encodeToString(bytes, Base64.DEFAULT);
            AppLog.LogE("imageToBase64", "Encoded String:" + encodedString);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return encodedString;
    }

    public static Bitmap byteArrayToBitmap(String TAG,byte[] byteArray) {
        AppLog.LogE(TAG, "byteArray.length: " + byteArray.length);
        Bitmap bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
        AppLog.LogE(TAG, "bitmap: " + bitmap);
        return bitmap;
    }

    public static byte[] bitmapToByteArray(Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        if (width > 0 && height > 0) {
            int size = bitmap.getRowBytes() * bitmap.getHeight();
            ByteBuffer byteBuffer = ByteBuffer.allocate(size);
            bitmap.copyPixelsToBuffer(byteBuffer);
            byte[] byteArray = byteBuffer.array();

            return byteArray;
        } else {
            return new byte[0];
        }
    }

    public static String getStringFromList(List<String> list) {
        String result = "[ ";
        int size = list.size();
        for (int i = 0; i < size; i++) {
            result += list.get(i);
            if (i == size - 1) {
                result += " ]";
            } else {
                result += ", ";
            }
        }
        return result;
    }

}