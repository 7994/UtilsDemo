package com.example.mashuk.demo.activity;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;

import com.example.mashuk.demo.R;
import com.example.mashuk.demo.utils.CommonUtils;

/**
 * SplashActivity.java : this activity is responsible to start the app.
 *
 * @version : 1.0.0
 * @Date : 27/01/2017
 */

public class SplashActivity extends Activity {

    private Handler redirectHandler;
    private static final int REDIRECTION_TIMEOUT = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        CommonUtils.setFullScreen(SplashActivity.this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
    }

    /**
     * This method is called when app goes to background.
     * @version : 1.0.0
     * @Date : 27/03/2017
     */
    @Override
    protected void onPause() {
        super.onPause();
        if (redirectHandler != null && redirectRunnable != null) {
            redirectHandler.removeCallbacks(redirectRunnable);
        }
    }

    /**
     * This method is called when app start as well as resume form background
     * @version : 1.0.0
     * @Date : 27/03/2017
     */
    @Override
    protected void onResume() {
        super.onResume();
        redirectHandler = new Handler();
        redirectHandler.postDelayed(redirectRunnable, REDIRECTION_TIMEOUT);
    }

    /**
     * Starts executing the active part of the class' code. This method is
     * called when a thread is started that has been created with a class which
     * implements {@code Runnable}.
     * <p>
     * Use for Activity re-direction
     *
     * @version : 1.0.0
     * @Date : 27/03/2017
     */
    private final Runnable redirectRunnable = new Runnable() {
        public void run() {
                CommonUtils.passIntent(SplashActivity.this, MainActivity1.class);

//            if (AppSharedPref.getInstance(SplashActivity.this).getSaveLogedIn()) {
//                CommonUtils.passIntent(SplashActivity.this, DashboardActivity.class);
//                finish();
//            } else {
//                CommonUtils.passIntent(SplashActivity.this, LogInActivity.class);
//                finish();
//            }
        }
    };
}
