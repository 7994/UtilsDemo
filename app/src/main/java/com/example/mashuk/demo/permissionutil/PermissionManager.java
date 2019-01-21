package com.example.mashuk.demo.permissionutil;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.Settings;
import android.support.annotation.StringRes;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;

import com.example.mashuk.demo.R;
import com.example.mashuk.demo.utils.AppLog;

import java.util.ArrayList;
import java.util.List;

/**
 * Last Edited by Harsh Patel on 02/23/17.
 * Edited by Yahya Bayramoglu on 09/02/16.
 * Original Source: https://github.com/googlesamples/easypermissions
 */

/**
 * PermissionManager.java : this class is used for manage  Runtime permissions
 * @author : Harsh Patel
 * @version : 1.0.0
 * @Date : 05/06/2017
 * @Change History :
 * <p>
 * {Change ID:#} 05/06/2017 :
 */
public class PermissionManager {

    private static int RUNTIME_PERMISSION = -1;

    // Callbacks to present result to requested activity/fragment
    public interface PermissionListener {

        void onPermissionsGranted(List<String> perms);

        void onPermissionsDenied(List<String> perms);

        void onPermissionRequestRejected();

        void onPermissionNeverAsked(List<String> perms);

    }

    /**
     * @purpose : Check user has a runtime permission or not

     * @Date : 02/06/2017
     * @author : Harsh Patel
     * @version : 1.0.0
     * @param :
     * @return :
     * @throws :
     * @since : 1.0.0
     * @Change History :
     */
    public static boolean hasPermissions(Context context, String... perms) {
        if (context == null) {
            return false;
        }

        for (String perm : perms) {
                boolean hasPerm = (ContextCompat.checkSelfPermission(context, perm) == PackageManager.PERMISSION_GRANTED);
                if (!hasPerm) {
                    return false;
                }
        }
        return true;
    }


    /**
     * @purpose : Request a runtime permission

     * @Date : 02/06/2017
     * @author : Harsh Patel
     * @version : 1.0.0
     * @param :
     * @return :
     * @throws :
     * @since : 1.0.0
     * @Change History :
     */
    public static void requestPermissions(Object object, int requestCode, PermissionListener listener, String rationale, final String... perms) {
        RUNTIME_PERMISSION = requestCode;
        requestPermissions(object, listener, rationale, android.R.string.ok, android.R.string.cancel, perms);
    }


    /**
     * @purpose : Logic goes here to request permission

     * @Date : 02/06/2017
     * @author : Harsh Patel
     * @version : 1.0.0
     * @param :
     * @return :
     * @throws :
     * @since : 1.0.0
     * @Change History :
     */
    public static void requestPermissions(final Object object, final PermissionListener listener, String rationale,
                                          @StringRes int positiveButton,
                                          @StringRes int negativeButton, final String... perms) {

        checkCallingObjectSuitability(object);

        // SharedPreference is used to distinguish between user has "denied" permission or "Never asked" permission
        SharedPreferences mSharedPreferences = getActivity(object).getSharedPreferences("permission_preference", Context.MODE_PRIVATE);
        SharedPreferences.Editor mEditor = mSharedPreferences.edit();

        boolean shouldShowRationale = false;
        for (String perm : perms) {
            shouldShowRationale = shouldShowRationale || shouldShowRequestPermissionRationale(object, perm);
        }

        // Logic goes here for permission rational
        if (shouldShowRationale && !TextUtils.isEmpty(rationale)) {
            executePermissionsRequest(object, perms, RUNTIME_PERMISSION);
            /*AlertDialog dialog = new AlertDialog.Builder(getActivity(object))
                    .setMessage(rationale)
                    .setCancelable(false)
                    .setPositiveButton(positiveButton, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            executePermissionsRequest(object, perms, PermissionCode.RUNTIME_PERMISSION);
                        }
                    })
                    .setNegativeButton(negativeButton, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // Do nothing, user does not want to request
                            listener.onPermissionRequestRejected();
                        }
                    }).create();
            dialog.show();*/
        } else { // If permission not rational, means user request permission first time or after "Never asked" optional selected

            // Initialise to calculate user has a "Never asked" option selected or not
            int grantedCount = 0;
            int neverAskedCount = 0;

            ArrayList<String> neveraskedperms = new ArrayList<>();
            for (int i = 0; i < perms.length; i++) {
                String perm = perms[i];

                // Check if user has a requested permission and has a permission rational
                if (!hasPermissions(getActivity(object), perm)) {
                    boolean isRational = shouldShowRequestPermissionRationale(object, perm);
                    if (!isRational) {
                        if (mSharedPreferences.getBoolean(perm, false)) {
                            // If sharedPreference has a TRUE value means user has a "Never asked" permission.
                            neveraskedperms.add(perm);
                            neverAskedCount++; // Count each "Never asked" permission
                        } else {
                            // updateFragment flag to TRUE when user request first time.
                            mEditor.putBoolean(perm, true).commit();
                        }
                    }
                } else {
                    grantedCount++; // Count each "Granted" permission count.
                }
            }

            // If sum of "Granted" and "Never asked" count are same as total requested permission,
            // then it tell us to navigate in setting window to enable permission.
            // It means no more default permission dialog available to show user.
            // If sum not match then there are dialog/s to show user. Now request for runtime permission dialog
            if (grantedCount + neverAskedCount == perms.length) {
                // Report "Never asked" permission, if any.
                neverAskedDialog(object,listener, neveraskedperms);
                //listener.onPermissionNeverAsked(neveraskedperms);
            } else {
                executePermissionsRequest(object, perms, RUNTIME_PERMISSION);
            }
        }
    }


    /**
     * @purpose :Callback which return all granted and ejected permission
     *           Logic goes here to distinguish between "Granted" and "Denied" permission
     * @Date : 02/06/2017
     * @author : Harsh Patel
     * @version : 1.0.0
     * @param :
     * @return :
     * @throws :
     * @since : 1.0.0
     * @Change History :
     */
    public static void onRequestPermissionsResult(Object object, PermissionListener callbacks, int requestCode, String[] permissions,
                                                  int[] grantResults) {

        if (requestCode == RUNTIME_PERMISSION) {

        }

        checkCallingObjectSuitability(object);

        // Make a collection of granted and denied permissions from the request.
        ArrayList<String> granted = new ArrayList<>();
        ArrayList<String> denied = new ArrayList<>();

        for (int i = 0; i < permissions.length; i++) {
            String perm = permissions[i];
            if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                AppLog.LogE("PermissionManager", "Allow");
                granted.add(perm);
            } else {
                AppLog.LogE("PermissionManager", "Deny");
                if (hasPermissions(getActivity(object), perm)) {
                    granted.add(perm);
                } else {
                    denied.add(perm);
                }
            }
        }

        // Report granted permissions, if any.
        if (!granted.isEmpty()) {
            // Notify callbacks
            callbacks.onPermissionsGranted(granted);
        }

        // Report denied permissions, if any.
        if (!denied.isEmpty()) {
            callbacks.onPermissionsDenied(denied);
        }
    }

    /**
     * @purpose : It checks the permission status
     * @Date : 02/06/2017
     * @author : Harsh Patel
     * @version : 1.0.0
     * @param :
     * @return :
     * @throws :
     * @since : 1.0.0
     * @Change History :
     */
    private static boolean shouldShowRequestPermissionRationale(Object object, String perm) {
        if (object instanceof Activity) {
            return ActivityCompat.shouldShowRequestPermissionRationale((Activity) object, perm);
        } else if (object instanceof Fragment) {
            return ((Fragment) object).shouldShowRequestPermissionRationale(perm);
        } else {
            return false;
        }
    }

    /**
     * @purpose : It Display permission Dialog
     * @Date : 02/06/2017
     * @author : Harsh Patel
     * @version : 1.0.0
     * @param :
     * @return :
     * @throws :
     * @since : 1.0.0
     * @Change History :
     */
    private static void executePermissionsRequest(Object object, String[] perms, int requestCode) {
        checkCallingObjectSuitability(object);

        if (object instanceof Activity) {
            ActivityCompat.requestPermissions((Activity) object, perms, requestCode);
        } else if (object instanceof Fragment) {
            ((Fragment) object).requestPermissions(perms, requestCode);
        }
    }

    /**
     * @purpose : Extract Activity from object
     * @Date : 02/06/2017
     * @author : Harsh Patel
     * @version : 1.0.0
     * @param :
     * @return :
     * @throws :
     * @since : 1.0.0
     * @Change History :
     */
    private static Activity getActivity(Object object) {
        if (object instanceof Activity) {
            return ((Activity) object);
        } else if (object instanceof Fragment) {
            return ((Fragment) object).getActivity();
        } else {
            return null;
        }
    }

    /**
     * @purpose :Make sure Object is an Activity or Fragment
     * @Date : 02/06/2017
     * @author : Harsh Patel
     * @version : 1.0.0
     * @param : {Object} Any Comonent Object
     * @return :
     * @throws : Illegal Argument Exception
     * @since : 1.0.0
     * @Change History :
     */
    private static void checkCallingObjectSuitability(Object object) {
        if (!((object instanceof Fragment) || (object instanceof Activity))) {
            throw new IllegalArgumentException("Caller must be an Activity or a Fragment.");
        }
    }


    /**
     * @purpose : It shows never ask dialog
     * @Date : 02/06/2017
     * @author : Harsh Patel
     * @version : 1.0.0
     * @param :
     * @return :
     * @throws :
     * @since : 1.0.0
     * @Change History :
     */
    private static void neverAskedDialog(final Object object, final PermissionListener listener, final ArrayList<String> neveraskedperms) {
       AlertDialog dialog = new AlertDialog.Builder(getActivity(object), R.style.MyAlertDialogStyle)
//                    .setMessage(""+getActivity(object).getResources().getString(R.string.permission_never_asked))
                    .setMessage("This app required some permissions to proceed. Enable them from setting.")
                    .setCancelable(false)
                    .setPositiveButton(""+getActivity(object).getResources().getString(R.string.go_to_setting), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            openSetting(object);

                        }
                    })
                    .setNegativeButton(""+getActivity(object).getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // Do nothing, user does not want to request
                            listener.onPermissionNeverAsked(neveraskedperms);
                            dialog.dismiss();
                        }
                    }).create();
            dialog.show();
    }


    /**
     * @purpose : It redirects to permission setting of current Application
     * @Date : 02/06/2017
     * @author : Harsh Patel
     * @version : 1.0.0
     * @param :
     * @return :
     * @throws :
     * @since : 1.0.0
     * @Change History :
     */
    private static void openSetting(final Object object) {
        final Intent i = new Intent();
        i.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        i.addCategory(Intent.CATEGORY_DEFAULT);
        i.setData(Uri.parse("package:" + getActivity(object).getPackageName()));
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        i.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        getActivity(object).startActivity(i);
    }
}