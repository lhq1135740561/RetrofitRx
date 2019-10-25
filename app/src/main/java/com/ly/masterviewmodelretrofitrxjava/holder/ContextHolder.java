package com.ly.masterviewmodelretrofitrxjava.holder;

import android.content.Context;

import com.ly.masterviewmodelretrofitrxjava.BaseApplication;

public class ContextHolder {
    public static Context getContext() {
        return BaseApplication.getAppContext();
    }
}
