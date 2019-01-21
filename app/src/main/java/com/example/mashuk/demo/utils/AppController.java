package com.example.mashuk.demo.utils;

import android.app.Application;


/**
 * Created by Krupa on 22/3/18.
 */

public class AppController extends Application {

    private static final String TAG = AppController.class.getSimpleName();

    private static AppController mInstance;
//    private static RxBus mRxBus;
    private static SharedPreferenceUtil mSharedPreferenceUtil;
    private static ResourceUtils mResourceUtils;
//    private static BuildDetailModel buildDetailModel;

/*    public static BuildDetailModel getBuildDetailModel() {
        if (buildDetailModel != null) {
            return buildDetailModel;
        } else {
            AppController.getInstance().loadData();
            return buildDetailModel;
        }
    }

    public static void setBuildDetailModel(BuildDetailModel buildDetailModel) {
        AppController.buildDetailModel = buildDetailModel;
    }*/

   /* public static RxBus getmRxBus() {
        return mRxBus;
    }*/

    public static SharedPreferenceUtil getmSharedPreferenceUtil() {
        return mSharedPreferenceUtil;
    }

    public static ResourceUtils getmResourceUtils() {
        return mResourceUtils;
    }

    public static synchronized AppController getInstance() {
        return mInstance;
    }

    /*private void loadData() {

        try {
            AssetManager assetManager = getAssets();
            InputStream ims = assetManager.open("BuildDetail.json");
            Gson gson = new Gson();

            Reader reader = new InputStreamReader(ims);
            BuildDetailModel buildDetailModel = gson.fromJson(reader, BuildDetailModel.class);

            AppController.setBuildDetailModel(buildDetailModel);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }*/

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;

        mSharedPreferenceUtil = new SharedPreferenceUtil(this);
        mResourceUtils = new ResourceUtils();
//        mRxBus = new RxBus();
//        loadData();
    }


}
