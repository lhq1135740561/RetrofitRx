package com.ly.masterviewmodelretrofitrxjava.http.basic.exception;

import com.ly.masterviewmodelretrofitrxjava.http.basic.config.HttpCode;

public class ConnectionException extends BaseException {
    public ConnectionException() {
        super(HttpCode.CODE_CONNECTION_FAILED, "网络请求失败");
    }
}
