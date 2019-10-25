package com.ly.masterviewmodelretrofitrxjava.http.basic.exception;


import com.ly.masterviewmodelretrofitrxjava.http.basic.config.HttpCode;

public class TokenInvalidException extends BaseException {
    public TokenInvalidException() {
        super(HttpCode.CODE_TOKEN_INVALID, "Token失效");
    }
}
