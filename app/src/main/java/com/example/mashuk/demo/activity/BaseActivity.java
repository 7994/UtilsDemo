package com.example.mashuk.demo.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;

import com.example.mashuk.demo.R;
import com.example.mashuk.demo.permissionutil.PermissionManager;
import com.example.mashuk.demo.permissionutil.Permissions;
import com.example.mashuk.demo.utils.AppLog;
import com.example.mashuk.demo.utils.CommonUtils;
import com.example.mashuk.demo.utils.FragmentUtil;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.ronin.app.utility.Constants;
import com.ronin.app.utility.DistanceCalculator;
import com.ronin.app.utility.DownloadManagerUtils;
import com.ronin.app.utility.PreferenceManager;

import java.util.List;

/**
 * Created by harsh on 3/11/17.
 */

public abstract class BaseActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener {

    // Required for setting API
    protected static final int REQUEST_CHECK_GPS_SETTINGS = 0x2;
    private static final long ONE_MIN = 1000 * 60;
    private static final long TWO_MIN = ONE_MIN * 2;
    private static final long FIVE_MIN = ONE_MIN * 5;
    private static final long POLLING_FREQ = 1000 * 30;
    private static final long FASTEST_UPDATE_FREQ = 1000 * 5;
    private static final float MIN_ACCURACY = 25.0f;
    private static final float MIN_LAST_READ_ACCURACY = 500.0f;
    private static String TAG = "" + BaseActivity.class.getSimpleName();
    GoogleApiClient mGoogleApiClient;
    private PreferenceManager mPreferenceManager = PreferenceManager.getInstance();
    private FragmentUtil mFragmentUtil = FragmentUtil.getInstance();
    private DownloadManagerUtils mDMUtil = DownloadManagerUtils.getInstance();
    private LocationRequest mLocationRequest;
    private Status status;

    private Handler mPermissionHandle = new Handler();
    private Runnable mPermissionRunnable;
    /**
     * @purpose : Custom listener to show user Grant, deny, reject or never ask for the permission
     * @Date : 02/06/2017
     * @author : Harsh Patel
     * @version : 1.0.0
     * @param :
     * @return :
     * @throws :
     * @Change History :
     * @since : 1.0.0
     */
    private final PermissionManager.PermissionListener permissionListener = new PermissionManager.PermissionListener() {
        @Override
        public void onPermissionsGranted(List<String> perms) {
            if (perms.size() == Permissions.LOCATION_PERMISSIONS.length) {
                AppLog.LogE(TAG, "We have all required permission, moving on fetching location!");

                // Notify to all set listeners that location permission is available
                notifyLocationPermissionStatus(true);
            } else {
                AppLog.LogE(TAG, "User denied some of required permissions! "
                        + "Even though we have following permissions now, "
                        + "task will still be aborted.\n" + CommonUtils.getStringFromList(perms));

                // Notify to all set listeners that location permission is not available
                notifyLocationPermissionStatus(false);
            }
        }

        @Override
        public void onPermissionsDenied(List<String> perms) {
            AppLog.LogE(TAG, "User denied required permissions!\n" + CommonUtils.getStringFromList(perms));

            // Notify to all set listeners that location permission is not available
            notifyLocationPermissionStatus(false);
        }

        @Override
        public void onPermissionRequestRejected() {
            AppLog.LogE(TAG, "User didn't even let us to ask for permission!");

            // Notify to all set listeners that location permission is not available
            notifyLocationPermissionStatus(false);
        }

        @Override
        public void onPermissionNeverAsked(List<String> perms) {
            AppLog.LogE(TAG, "User denied required permissions with never ask!\n" + CommonUtils.getStringFromList(perms));

            // Notify to all set listeners that location permission is not available
            notifyLocationPermissionStatus(false);
        }
    };

    private boolean isActualGpsOpen = false;
    private BroadcastReceiver gpsChangeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //Calling necessary method.
            isGps();
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppLog.LogE(TAG, "onCreate");
    }

    @Override
    protected void onResume() {
        //Registering Receiver to notify GPS state.
        BaseActivity.this.registerReceiver(gpsChangeReceiver, new IntentFilter(LocationManager.PROVIDERS_CHANGED_ACTION));
        super.onResume();

        mPermissionHandle.postDelayed(mPermissionRunnable = new Runnable() {
            @Override
            public void run() {
                // Check location permission to get user current latitude and longitude
                if (!PermissionManager.hasPermissions(BaseActivity.this, Permissions.LOCATION_PERMISSIONS)) {
                    AppLog.LogE(TAG, "Location permission asked");
                    askForLocationPermission();
                } else {
                    isGps();
                }
            }
        }, 2000);

        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
    }

    @Override
    protected void onPause() {

        if (mPermissionHandle != null && mPermissionRunnable != null) {
            mPermissionHandle.removeCallbacks(mPermissionRunnable);
        }

        //Un registering Receiver.
        try {
            BaseActivity.this.unregisterReceiver(gpsChangeReceiver);
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        super.onPause();
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CHECK_GPS_SETTINGS) {
            //isGps();
        } else {
            Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.container);
            if (fragment != null) {
                // return onRequestPermissionsResult() method in respected fragment
                fragment.onActivityResult(requestCode, resultCode, data);
            } else {
                super.onActivityResult(requestCode, resultCode, data);
            }
        }
    }


    @Override
    protected void onDestroy() {
        try {
            if (gpsChangeReceiver != null) {
                BaseActivity.this.unregisterReceiver(gpsChangeReceiver);
            }
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        super.onDestroy();
    }

    /**
     * @param :
     * @return :
     * @throws :
     * @purpose : Shows permission allow or deny
     * @Date : 02/06/2017
     * @author : Harsh Patel
     * @version : 1.0.0
     * @Change History :
     * @since : 1.0.0
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        AppLog.LogE(TAG, "onRequestPermissionsResult: requestCode - " + requestCode);
        // All the location functionality is here like Check location permission,
        // GPS and cals API to updateFragment user location. So we check permission result here instead
        // to redirect to respected fragment.
        if (requestCode == Constants.CODE_RUNTIME_LOCATION_PERMISSION) {
            PermissionManager.onRequestPermissionsResult(BaseActivity.this, permissionListener,
                    requestCode, permissions, grantResults);
        } else {
            Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.container);
            AppLog.LogE(TAG, "onRequestPermissionsResult->CurrentFrag:" + fragment);
            if (fragment != null) {
                fragment.onRequestPermissionsResult(requestCode, permissions, grantResults);
            } else {
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            }
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        // Get first reading. Get additional location updates if necessary
        AppLog.LogE(TAG, "onConnected");
        if (CommonUtil.isPlayServiceAvailable(BaseActivity.this)) {
            if (PermissionManager.hasPermissions(BaseActivity.this, Permissions.LOCATION_PERMISSIONS)) {
                try {
                    if (mGoogleApiClient != null && mLocationRequest != null) {
                        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                            return;
                        }
                        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        AppLog.LogE(TAG, "onConnectionSuspended: " + i);
        // mPreferenceManager.saveUserLatLng("", "");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        AppLog.LogE(TAG, "onConnectionFailed: " + connectionResult.getErrorMessage());
        //  mPreferenceManager.saveUserLatLng("", "");
    }

    @Override
    public void onLocationChanged(Location location) {
        AppLog.LogE(TAG, "Location Update: Accuracy-" + location.getAccuracy() + ", Latitude-" + location.getLatitude() + ", Longitude-" + location.getLongitude());

        double currentLat = location.getLatitude();
        double currentLng = location.getLongitude();

        String savedLat = "" + mPreferenceManager.getUserLat();
        String savedLng = "" + mPreferenceManager.getUserLng();

        if (CommonUtil.isNullString(savedLat) || CommonUtil.isNullString(savedLng)) {
            // Save updated latitude, longitude in shared preference for future use if user here at first time.
            mPreferenceManager.saveUserLatLng("" + currentLat, "" + currentLng);

            notifyLocationChanged(location);

            callApiForUserLocationUpdate();
        } else {
            // Get user movement.

            // Calculate distance between current lat, lng to saved lat, lng
            double distanceBwTwoLocation = DistanceCalculator.calculate(currentLat, currentLng,
                    Double.parseDouble("" + savedLat), Double.parseDouble("" + savedLng),
                    "" + DistanceCalculator.UNIT.FOOT, 2);

            AppLog.LogE(TAG, "Distance: " + distanceBwTwoLocation);

            if (distanceBwTwoLocation > 25) {
                // Save updated latitude, longitude in shared preference for future use if user here at first time.
                mPreferenceManager.saveUserLatLng("" + currentLat, "" + currentLng);

                notifyLocationChanged(location);

                // If user move 25 foot from last Latitude and longitude. Update user location in DB and shared preference.
                callApiForUserLocationUpdate();
            }
        }
    }

    /**
     * @purpose : set up and call api for user location updateFragment. Response is detected in onOkHttpSuccess.
     * <p>
     * This method first check internet connection. If not available, it will return with message.
     * @Date : 30/10/2017
     * @author : Harsh Patel
     * @version : 1.0.0
     * @Change History :
     * @since : 1.0.0
     */
    private void callApiForUserLocationUpdate() {
        // Check internet connection to proceed
        if (!CommonUtil.isInternetAvailable(BaseActivity.this)) {
            return;
        }

        new OkHttpRequest(BaseActivity.this,
                OkHttpRequest.Method.POST,
                Constants.USER_UPDATE_LOCATION,
                RequestParam.userLocationUpdate("" + mPreferenceManager.getUserId(),
                        "" + mPreferenceManager.getUserLat(), "" + mPreferenceManager.getUserLng()),
                RequestParam.getNull(),
                Constants.CODE_USER_UPDATE_LOCATION,
                false, this);
    }

    public void askForLocationPermission() {
        if (PermissionManager.hasPermissions(BaseActivity.this, Permissions.LOCATION_PERMISSIONS)) {
            // Notify to all set listeners that location permission is available
            notifyLocationPermissionStatus(true);
        } else {
            PermissionManager.requestPermissions(BaseActivity.this, Constants.CODE_RUNTIME_LOCATION_PERMISSION, permissionListener,
                    "", Permissions.LOCATION_PERMISSIONS);
        }
    }

    /**
     * @param :
     * @return :
     * @throws :
     * @purpose : Checks whether GPS is ON or OFF using Setting API.
     * @Date : 30/10/2017
     * @author : Harsh Patel
     * @version : 1.0.0
     * @Change History :
     * @since : 1.0.0
     */
    public void isGps() {
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
            mGoogleApiClient = null;
        }

        mGoogleApiClient = new GoogleApiClient.Builder(BaseActivity.this)
                .addApiIfAvailable(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        mGoogleApiClient.connect();

        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(POLLING_FREQ);
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_FREQ);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest);

        builder.setAlwaysShow(false);

        /*LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest).setAlwaysShow(true);*/

        PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi
                .checkLocationSettings(mGoogleApiClient, builder.build());

        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult result) {
                status = result.getStatus();
                final LocationSettingsStates state = result
                        .getLocationSettingsStates();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        AppLog.LogE(TAG, "GPS: SUCCESS");
                        if (CommonUtil.isInternetAvailable(BaseActivity.this)) {
                            // If location permission is granted and GPS is on.
                            //initLocationService();

                            // Notify all listeners that GPS is available
                            notifyGpsStatus(true);
                            //showGpsDialog(true);
                        }
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        //isCustomGpsOpen=true;
                        AppLog.LogE(TAG, "GPS: RESOLUTION_REQUIRED");
                        // Location settings are not satisfied. But could be
                        // fixed by showing the user
                        // a dialog.
                        try {
                            // Show the dialog by calling
                            // startResolutionForResult(),
                            // and check the result in onActivityResult().
                            // Notify to all set listeners that GPS is available
                           /* status.startResolutionForResult(BaseActivity.this, REQUEST_CHECK_GPS_SETTINGS);
                            // Notify all listeners that GPS is not available
                            notifyGpsStatus(false);*/
                            // mPreferenceManager.saveUserLatLng("", "");
                            showGpsDialog(status);

                        } catch (Exception e) {
                            // Ignore the error.
                            showGpsDialog(status);
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        AppLog.LogE(TAG, "GPS: SETTINGS_CHANGE_UNAVAILABLE");
                        // Location settings are not satisfied. However, we have
                        // no way to fix the
                        // settings so we won't show the dialog.
                        // Notify all listeners that GPS is not available
                        //mPreferenceManager.saveUserLatLng("", "");
                        notifyGpsStatus(false);
                        break;
                    case LocationSettingsStatusCodes.CANCELED:
                        AppLog.LogE(TAG, "GPS: CANCELED");
                        // Notify all listeners that GPS is not available
                        //mPreferenceManager.saveUserLatLng("", "");
                        notifyGpsStatus(false);
                        break;
                }
            }
        });
    }

    /**
     * @param :
     * @param status
     * @return :
     * @throws :
     * @purpose : Inorder to show Custom GPS dialog.
     * @Date : 03/01/2018
     * @author : Harsh Patel
     * @version : 1.0.0
     * @Change History :
     * @since : 1.0.0
     */
    protected void showGpsDialog(final Status status) {
        AlertDialog.Builder builder = new AlertDialog.Builder(BaseActivity.this);
        builder.setMessage(getResources().getString(R.string.toast_custom_gps))
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // continue with delete
                        try {
                            status.startResolutionForResult(BaseActivity.this, REQUEST_CHECK_GPS_SETTINGS);
                        } catch (IntentSender.SendIntentException e) {
                            e.printStackTrace();
                        }
                        // Notify all listeners that GPS is not available
                        notifyGpsStatus(false);
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setCancelable(false)
                .show();
    }

    protected void notifyGpsStatus(boolean isAvailable) {
        //AppLog.LogE(TAG, "notifyGpsStatus in BaseActivity: " + isAvailable);
        Fragment currentFragHomeActivity = getHomeActivityCurrentFragment();

        if (currentFragHomeActivity != null) {
            String currentFragTag = currentFragHomeActivity.getTag();
            AppLog.LogE(TAG, "notifyGpsStatus Current frg" + currentFragTag);

            // Redirect result to respected fragment
            if (currentFragTag.equals(Constants.TAG_EXPLORE_HOME_FRAGMENT)) {
                ((ExploreHomeFragment) currentFragHomeActivity).notifyGpsStatus(isAvailable);
            } else if (currentFragTag.equals(Constants.TAG_EXPLORE_CREATE_GYM_FRAGMENT)) {
                ((ExploreCreateGymFragment) currentFragHomeActivity).notifyGpsStatus(isAvailable);
            }
        }
    }

    protected void notifyLocationPermissionStatus(boolean isGranted) {
        //AppLog.LogE(TAG, "notifyLocationPermission in BaseActivity: " + isGranted);
        // Notify all listeners that location permission is available or not
        if (isGranted) {
            AppLog.LogE(TAG, "notifyLocationPermissionStatus");
            isGps();
        } else {
            // Remove saved location as we have need only latest location.
            //mPreferenceManager.saveUserLatLng("", "");
        }


        Fragment currentFragHomeActivity = getHomeActivityCurrentFragment();

        if (currentFragHomeActivity != null) {
            String currentFragTag = currentFragHomeActivity.getTag();
            AppLog.LogE(TAG, "notifyLocationPermission Current frg" + currentFragTag);

            // Redirect result to respected fragment
            if (currentFragTag.equals(Constants.TAG_EXPLORE_HOME_FRAGMENT)) {
                ((ExploreHomeFragment) currentFragHomeActivity).notifyLocationPermissionStatus(isGranted);
            } else if (currentFragTag.equals(Constants.TAG_EXPLORE_CREATE_GYM_FRAGMENT)) {
                ((ExploreCreateGymFragment) currentFragHomeActivity).notifyLocationPermissionStatus(isGranted);
            }
        }
    }

    protected void notifyLocationChanged(Location location) {
        Fragment currentFragHomeActivity = getHomeActivityCurrentFragment();

        if (currentFragHomeActivity != null) {
            String currentFragTag = currentFragHomeActivity.getTag();
            //AppLog.LogE(TAG, "notifyLocationPermission Current frg" + currentFragTag);

            // Redirect result to respected fragment
            if (currentFragTag.equals(Constants.TAG_EXPLORE_HOME_FRAGMENT)) {
                ((ExploreHomeFragment) currentFragHomeActivity).notifyLocationChanged(location);
            } else if (currentFragTag.equals(Constants.TAG_EXPLORE_CREATE_GYM_FRAGMENT)) {
                ((ExploreCreateGymFragment) currentFragHomeActivity).notifyLocationChanged(location);
            }
        }
    }

    /**
     * @param :
     * @return : current fragment
     * @throws :
     * @purpose : It is intended to get current fragment
     * @Date : 03/11/2017
     * @author : Harsh Patel
     * @version : 1.0.0
     * @Change History :
     * @since : 1.0.0
     */
    public Fragment getHomeActivityCurrentFragment() {
        return getSupportFragmentManager().findFragmentById(R.id.container);
    }
}
