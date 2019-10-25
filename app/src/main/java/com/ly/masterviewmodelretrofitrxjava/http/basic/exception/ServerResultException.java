package com.ly.masterviewmodelretrofitrxjava.http.basic.exception;

public class ServerResultException extends BaseException {
    public ServerResultException(int code, String message) {
        super(code, message);
    }
}
