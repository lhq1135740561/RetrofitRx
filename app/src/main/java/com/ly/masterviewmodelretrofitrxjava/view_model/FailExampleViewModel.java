package com.ly.masterviewmodelretrofitrxjava.view_model;

import android.arch.lifecycle.MutableLiveData;

import com.ly.masterviewmodelretrofitrxjava.http.basic.callback.RequestMultiplyCallback;
import com.ly.masterviewmodelretrofitrxjava.http.basic.exception.BaseException;
import com.ly.masterviewmodelretrofitrxjava.http.datasource.FailExampleDataSource;
import com.ly.masterviewmodelretrofitrxjava.http.repo.FailExampleRepo;
import com.ly.masterviewmodelretrofitrxjava.view_model.base.BaseViewModel;

public class FailExampleViewModel extends BaseViewModel {
    private MutableLiveData<String> mTest1LiveData = new MutableLiveData<>();
    private MutableLiveData<String> mTest2LiveData = new MutableLiveData<>();

    private FailExampleRepo mFailExampleRepo = new FailExampleRepo(new FailExampleDataSource(this));

    public void test1() {
        mFailExampleRepo.test1().observe(mLifecycleOwner, s -> mTest1LiveData.setValue(s));
    }

    public void test2() {
        mFailExampleRepo.test2(new RequestMultiplyCallback<String>() {
            @Override
            public void onFail(BaseException e) {
                showToast("test2方法请求失败：" + e.getMessage());
                finish();
            }

            @Override
            public void onSuccess(String s) {
                mTest2LiveData.setValue(s);
            }
        });
    }
}
