package com.ly.masterviewmodelretrofitrxjava.http.basic.callback;

public interface RequestCallback<T> {
    void onSuccess(T t);
}
