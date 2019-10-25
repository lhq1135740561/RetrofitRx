package com.ly.masterviewmodelretrofitrxjava.view_model.base;

import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import com.ly.masterviewmodelretrofitrxjava.event.BaseActionEvent;

public class BaseViewModel extends ViewModel implements IViewModelAction {

    private MutableLiveData<BaseActionEvent> mActionLiveData;

    protected LifecycleOwner mLifecycleOwner;

    public BaseViewModel() {
        mActionLiveData = new MutableLiveData<>();
    }

    @Override
    public void startLoading() {
        startLoading(null);
    }

    @Override
    public void startLoading(String message) {
        BaseActionEvent baseActionEvent = new BaseActionEvent(BaseActionEvent.SHOW_LOADING_DIALOG);
        baseActionEvent.setMessage(message);
        mActionLiveData.setValue(baseActionEvent);
    }

    @Override
    public void dismissLoading() {
        mActionLiveData.setValue(new BaseActionEvent(BaseActionEvent.DISMISS_LOADING_DIALOG));
    }

    @Override
    public void showToast(String message) {
        BaseActionEvent baseActionEvent = new BaseActionEvent(BaseActionEvent.SHOW_TOAST);
        baseActionEvent.setMessage(message);
        mActionLiveData.setValue(baseActionEvent);
    }

    @Override
    public void finish() {
        mActionLiveData.setValue(new BaseActionEvent(BaseActionEvent.FINISH));
    }

    @Override
    public void finishWithResultOk() {
        mActionLiveData.setValue(new BaseActionEvent(BaseActionEvent.FINISH_WITH_RESULT_OK));
    }

    @Override
    public MutableLiveData<BaseActionEvent> getActionLiveData() {
        return mActionLiveData;
    }

    public void setLifecycleOwner(LifecycleOwner lifecycleOwner) {
        mLifecycleOwner = lifecycleOwner;
    }
}
