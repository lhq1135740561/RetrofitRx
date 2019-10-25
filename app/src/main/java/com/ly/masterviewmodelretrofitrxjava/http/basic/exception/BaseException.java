package com.ly.masterviewmodelretrofitrxjava.http.basic.exception;

import com.ly.masterviewmodelretrofitrxjava.http.basic.config.HttpCode;

public class BaseException extends RuntimeException {
    private int mErrorCode = HttpCode.CODE_UNKNOWN;

    public BaseException() {
    }

    public BaseException(int errorCode, String message) {
        super(message);
        mErrorCode = errorCode;
    }

    public int getErrorCode() {
        return mErrorCode;
    }
}
