package com.ly.masterviewmodelretrofitrxjava.http.basic;

import android.widget.Toast;

import com.ly.masterviewmodelretrofitrxjava.holder.ContextHolder;
import com.ly.masterviewmodelretrofitrxjava.http.basic.callback.RequestCallback;
import com.ly.masterviewmodelretrofitrxjava.http.basic.callback.RequestMultiplyCallback;
import com.ly.masterviewmodelretrofitrxjava.http.basic.config.HttpCode;
import com.ly.masterviewmodelretrofitrxjava.http.basic.exception.BaseException;
import com.ly.masterviewmodelretrofitrxjava.view_model.base.BaseViewModel;

import io.reactivex.observers.DisposableObserver;

public class BaseSubscriber<T> extends DisposableObserver<T> {

    private BaseViewModel mBaseViewModel;

    private RequestCallback<T> mRequestCallback;

    public BaseSubscriber(BaseViewModel baseViewModel) {
        mBaseViewModel = baseViewModel;
    }

    public BaseSubscriber(BaseViewModel baseViewModel, RequestCallback<T> requestCallback) {
        mBaseViewModel = baseViewModel;
        mRequestCallback = requestCallback;
    }

    @Override
    public void onNext(T t) {
        if (mRequestCallback != null) {
            mRequestCallback.onSuccess(t);
        }
    }

    @Override
    public void onError(Throwable e) {
        e.printStackTrace();
        if (mRequestCallback instanceof RequestMultiplyCallback) {
            RequestMultiplyCallback callback = (RequestMultiplyCallback) mRequestCallback;
            if (e instanceof BaseException) {
                callback.onFail((BaseException) e);
            } else {
                callback.onFail(new BaseException(HttpCode.CODE_UNKNOWN, e.getMessage()));
            }
        } else {
            if (mBaseViewModel == null) {
                Toast.makeText(ContextHolder.getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            } else {
                mBaseViewModel.showToast(e.getMessage());
            }
        }
    }

    @Override
    public void onComplete() {

    }
}
