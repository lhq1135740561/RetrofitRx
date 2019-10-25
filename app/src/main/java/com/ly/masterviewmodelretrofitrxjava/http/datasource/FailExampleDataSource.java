package com.ly.masterviewmodelretrofitrxjava.http.datasource;

import com.ly.masterviewmodelretrofitrxjava.http.basic.BaseRemoteDataSource;
import com.ly.masterviewmodelretrofitrxjava.http.basic.callback.RequestCallback;
import com.ly.masterviewmodelretrofitrxjava.http.datasource.base.IFailExampleDataSource;
import com.ly.masterviewmodelretrofitrxjava.http.service.ApiService;
import com.ly.masterviewmodelretrofitrxjava.view_model.base.BaseViewModel;

public class FailExampleDataSource extends BaseRemoteDataSource implements IFailExampleDataSource {

    public FailExampleDataSource(BaseViewModel baseViewModel) {
        super(baseViewModel);
    }

    @Override
    public void test1(RequestCallback<String> callback) {
        execute(getService(ApiService.class).test1(), callback);
    }

    @Override
    public void test2(RequestCallback<String> callback) {
        execute(getService(ApiService.class).test2(), callback);
    }
}
