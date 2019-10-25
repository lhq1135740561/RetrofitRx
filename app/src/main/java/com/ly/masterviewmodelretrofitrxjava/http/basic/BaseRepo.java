package com.ly.masterviewmodelretrofitrxjava.http.basic;

public class BaseRepo<T> {
    protected T mRemoteDataSource;

    public BaseRepo(T remoteDataSource) {
        mRemoteDataSource = remoteDataSource;
    }
}
