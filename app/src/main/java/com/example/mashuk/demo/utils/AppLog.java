package com.example.mashuk.demo.utils;

import android.util.Log;

/**
 * AppLog.java : this class is responsible to
 * handle log in application
 * @Date : 27/01/2017
 * @version : 1.0.0
 */

public class AppLog {

    private static boolean isProd = false;

	public static void LogD(String tag, String message) {
		if (!isProd) {
			Log.d(tag, message);
		}
	}

	public static void LogE(String tag, String message) {
		if (!isProd) {
			Log.e(tag, message);
		}
	}

	public static void LogW(String tag, String message) {
		if (!isProd) {
			Log.d(tag, message);
		}
	}

	public static void LogI(String tag, String message) {
		if (!isProd) {
			Log.i(tag, message);
		}
	}
}
