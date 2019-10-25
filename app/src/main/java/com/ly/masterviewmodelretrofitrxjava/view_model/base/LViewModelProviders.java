package com.ly.masterviewmodelretrofitrxjava.view_model.base;

import android.arch.lifecycle.ViewModelProviders;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;

/**
 * Create by Allen Liu at 2019/2/19 11:00.
 */
public class LViewModelProviders {
    public static <T extends BaseViewModel> T of(FragmentActivity activity, Class<T> modelClass) {
        T t = ViewModelProviders.of(activity).get(modelClass);
        t.setLifecycleOwner(activity);
        return t;
    }

    public static <T extends BaseViewModel> T of(@NonNull Fragment activity, Class<T> modelClass) {
        T t = ViewModelProviders.of(activity).get(modelClass);
        t.setLifecycleOwner(activity);
        return t;
    }
}
