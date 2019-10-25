package com.ly.masterviewmodelretrofitrxjava.view.base;

import android.app.ProgressDialog;
import android.arch.lifecycle.ViewModel;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.ly.masterviewmodelretrofitrxjava.event.BaseActionEvent;
import com.ly.masterviewmodelretrofitrxjava.view_model.base.IViewModelAction;

import java.util.ArrayList;
import java.util.List;

/**
 * Create by Allen Liu at 2019/2/19 10:28.
 */
public abstract class BaseActivity extends AppCompatActivity {
    private ProgressDialog mLoadingDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initViewModelEvent();
    }

    protected abstract ViewModel initViewModel();

    protected List<ViewModel> initViewModelList() {
        return null;
    }

    private void initViewModelEvent() {
        List<ViewModel> viewModelList = initViewModelList();
        if (viewModelList != null && viewModelList.size() > 0) {
            observeEvent(viewModelList);
        } else {
            ViewModel viewModel = initViewModel();
            if (viewModel != null) {
                List<ViewModel> modelList = new ArrayList<>();
                modelList.add(viewModel);
                observeEvent(modelList);
            }
        }
    }

    private void observeEvent(List<ViewModel> viewModelList) {
        for (ViewModel viewModel : viewModelList) {
            if (viewModel instanceof IViewModelAction) {
                IViewModelAction viewModelAction = (IViewModelAction) viewModel;
                viewModelAction.getActionLiveData().observe(this, baseActionEvent -> {
                    if (baseActionEvent != null) {
                        switch (baseActionEvent.getAction()) {
                            case BaseActionEvent.SHOW_LOADING_DIALOG:
                                startLoading(baseActionEvent.getMessage());
                                break;
                            case BaseActionEvent.DISMISS_LOADING_DIALOG:
                                dismissLoading();
                                break;
                            case BaseActionEvent.SHOW_TOAST:
                                showToast(baseActionEvent.getMessage());
                                break;
                            case BaseActionEvent.FINISH:
                                finish();
                                break;
                            case BaseActionEvent.FINISH_WITH_RESULT_OK:
                                setResult(RESULT_OK);
                                break;
                        }
                    }
                });
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dismissLoading();
    }

    private void startLoading() {
        startLoading(null);
    }

    protected void startLoading(String message) {
        if (mLoadingDialog == null) {
            mLoadingDialog = new ProgressDialog(this);
            mLoadingDialog.setCancelable(false);
            mLoadingDialog.setCanceledOnTouchOutside(false);
        }
        mLoadingDialog.setTitle(message);
        mLoadingDialog.show();
    }

    protected void dismissLoading() {
        if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
            mLoadingDialog.dismiss();
        }
    }

    protected void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    protected void finishWithResultOk() {
        setResult(RESULT_OK);
        finish();
    }

    protected BaseActivity getContext() {
        return BaseActivity.this;
    }

    protected void startActivity(Class clazz) {
        startActivity(new Intent(this, clazz));
    }

    protected void startActivityForResult(Class clazz, int requestCode) {
        startActivityForResult(new Intent(this, clazz), requestCode);
    }

    protected boolean isFinishingOrDestroyed() {
        return isFinishing() || isDestroyed();
    }
}
