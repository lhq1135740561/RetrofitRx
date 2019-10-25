package com.ly.masterviewmodelretrofitrxjava.http.basic;

import com.ly.masterviewmodelretrofitrxjava.http.basic.callback.RequestCallback;
import com.ly.masterviewmodelretrofitrxjava.http.basic.callback.RequestMultiplyCallback;
import com.ly.masterviewmodelretrofitrxjava.http.basic.model.BaseResponseBody;
import com.ly.masterviewmodelretrofitrxjava.view_model.base.BaseViewModel;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableTransformer;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public abstract class BaseRemoteDataSource {
    private CompositeDisposable mCompositeDisposable;
    private BaseViewModel mBaseViewModel;

    public BaseRemoteDataSource(BaseViewModel baseViewModel) {
        mCompositeDisposable = new CompositeDisposable();
        mBaseViewModel = baseViewModel;
    }

    protected <T> T getService(Class<T> clazz) {
        return RetrofitManagement.getInstance().getService(clazz);
    }

    protected <T> T getService(Class<T> clazz, String host) {
        return RetrofitManagement.getInstance().getService(clazz, host);
    }

    private <T> ObservableTransformer<BaseResponseBody<T>, T> applySchedulers() {
        return RetrofitManagement.getInstance().applySchedulers();
    }

    protected <T> void execute(Observable observable, RequestCallback<T> callback) {
        execute(observable, new BaseSubscriber<>(mBaseViewModel, callback), true);
    }

    protected <T> void execute(Observable observable, RequestMultiplyCallback<T> callback) {
        execute(observable, new BaseSubscriber<>(mBaseViewModel, callback), true);
    }

    public void executeWithoutDismiss(Observable observable, Observer observer) {
        execute(observable, observer, false);
    }

    @SuppressWarnings("unchecked")
    private void execute(Observable observable, Observer observer, boolean isDismiss) {
        Disposable disposable = (Disposable) observable
                .throttleFirst(500, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(applySchedulers())
                .compose(isDismiss ? loadingTransformer() : loadingTransformerWithoutDismiss())
                .subscribeWith(observer);
        addDisposable(disposable);
    }

    private <T> ObservableTransformer<T, T> loadingTransformer() {
        return observable -> observable
                .subscribeOn(AndroidSchedulers.mainThread())
                .unsubscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(disposable -> startLoading())
                .doFinally(this::dismissLoading);
    }

    private ObservableTransformer loadingTransformerWithoutDismiss() {
        return observable -> observable
                .subscribeOn(AndroidSchedulers.mainThread())
                .unsubscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(disposable -> startLoading());
    }

    private void addDisposable(Disposable disposable) {
        mCompositeDisposable.add(disposable);
    }

    public void dispose() {
        if (!mCompositeDisposable.isDisposed()) {
            mCompositeDisposable.dispose();
        }
    }

    private void startLoading() {
        if (mBaseViewModel != null) {
            mBaseViewModel.startLoading();
        }
    }

    private void dismissLoading() {
        if (mBaseViewModel != null) {
            mBaseViewModel.dismissLoading();
        }
    }
}
