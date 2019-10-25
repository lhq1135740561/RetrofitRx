package com.ly.masterviewmodelretrofitrxjava.view_model.base;

import android.arch.lifecycle.MutableLiveData;

import com.ly.masterviewmodelretrofitrxjava.event.BaseActionEvent;

public interface IViewModelAction {
    void startLoading();

    void startLoading(String message);

    void dismissLoading();

    void showToast(String message);

    void finish();

    void finishWithResultOk();

    MutableLiveData<BaseActionEvent> getActionLiveData();
}
