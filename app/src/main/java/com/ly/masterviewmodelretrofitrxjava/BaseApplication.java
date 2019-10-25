package com.ly.masterviewmodelretrofitrxjava;

import android.app.Application;

/**
 * Create by Allen Liu at 2019/2/19 10:26.
 */
public class BaseApplication extends Application {
    private static Application sAppContext;

    @Override
    public void onCreate() {
        super.onCreate();
        sAppContext = this;
    }

    public static Application getAppContext() {
        return sAppContext;
    }
}
