package com.ly.masterviewmodelretrofitrxjava.http.basic.exception;

import com.ly.masterviewmodelretrofitrxjava.http.basic.config.HttpCode;

public class ResultInvalidException extends BaseException {
    public ResultInvalidException() {
        super(HttpCode.CODE_RESULT_INVALID, "无效请求");
    }
}
