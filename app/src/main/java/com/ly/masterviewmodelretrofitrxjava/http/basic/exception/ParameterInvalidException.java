package com.ly.masterviewmodelretrofitrxjava.http.basic.exception;

import com.ly.masterviewmodelretrofitrxjava.http.basic.config.HttpCode;

public class ParameterInvalidException extends BaseException {
    public ParameterInvalidException() {
        super(HttpCode.CODE_PARAMETER_INVALID, "参数有误");
    }
}
