package com.ly.masterviewmodelretrofitrxjava.http.basic.exception;

import com.ly.masterviewmodelretrofitrxjava.http.basic.config.HttpCode;

public class ForbiddenException extends BaseException {
    public ForbiddenException() {
        super(HttpCode.CODE_PARAMETER_INVALID, "404错误");
    }
}
