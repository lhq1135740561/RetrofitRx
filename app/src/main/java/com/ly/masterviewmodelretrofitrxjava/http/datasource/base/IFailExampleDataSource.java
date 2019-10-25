package com.ly.masterviewmodelretrofitrxjava.http.datasource.base;

import com.ly.masterviewmodelretrofitrxjava.http.basic.callback.RequestCallback;

public interface IFailExampleDataSource {
    void test1(RequestCallback<String> callback);

    void test2(RequestCallback<String> callback);
}
