package com.ly.masterviewmodelretrofitrxjava.http.basic.callback;

import com.ly.masterviewmodelretrofitrxjava.http.basic.exception.BaseException;

public interface RequestMultiplyCallback<T> extends RequestCallback<T> {
    void onFail(BaseException e);
}
