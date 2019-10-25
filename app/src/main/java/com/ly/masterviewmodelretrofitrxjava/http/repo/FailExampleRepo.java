package com.ly.masterviewmodelretrofitrxjava.http.repo;

import android.arch.lifecycle.MutableLiveData;

import com.ly.masterviewmodelretrofitrxjava.http.basic.BaseRepo;
import com.ly.masterviewmodelretrofitrxjava.http.basic.callback.RequestMultiplyCallback;
import com.ly.masterviewmodelretrofitrxjava.http.datasource.base.IFailExampleDataSource;

public class FailExampleRepo extends BaseRepo<IFailExampleDataSource> {
    public FailExampleRepo(IFailExampleDataSource remoteDataSource) {
        super(remoteDataSource);
    }

    public MutableLiveData<String> test1() {
        MutableLiveData<String> newsPackMutableLiveData = new MutableLiveData<>();
        mRemoteDataSource.test1(newsPackMutableLiveData::setValue);
        return newsPackMutableLiveData;
    }

    public void test2(RequestMultiplyCallback<String> callback) {
        mRemoteDataSource.test2(callback);
    }
}
