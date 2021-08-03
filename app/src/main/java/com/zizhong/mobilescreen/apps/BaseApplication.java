package com.zizhong.mobilescreen.apps;


import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.util.DisplayMetrics;

import com.zizhong.mobilescreen.base.BaseActivity;




public class BaseApplication extends Application {

    public static BaseApplication baseApplication;
    public static BaseActivity topActivity; //栈顶activity
    private Resources resources;

    private Configuration config;
    private static Context context;
    private DisplayMetrics dm;


    @Override
    public void onCreate() {
        super.onCreate();
        baseApplication = this;



    }





    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);

    }
    public static Context getAppContext() {
        return BaseApplication.context;
    }


}
