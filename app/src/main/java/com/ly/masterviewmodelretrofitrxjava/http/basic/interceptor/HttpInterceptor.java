package com.ly.masterviewmodelretrofitrxjava.http.basic.interceptor;

import android.support.annotation.NonNull;

import com.ly.masterviewmodelretrofitrxjava.http.basic.exception.ConnectionException;
import com.ly.masterviewmodelretrofitrxjava.http.basic.exception.ResultInvalidException;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.BufferedSource;

public class HttpInterceptor implements Interceptor {
    @NonNull
    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        Request request = chain.request();
        Response originalResponse;
        try {
            originalResponse = chain.proceed(request);
        } catch (Exception e) {
            throw new ConnectionException();
        }
        if (originalResponse.code() != 200) {
            throw new ResultInvalidException();
        }
        assert originalResponse.body() != null;
        BufferedSource source = originalResponse.body().source();
        source.request(Integer.MAX_VALUE);
        String byteString = source.buffer().snapshot().utf8();
        ResponseBody responseBody = ResponseBody.create(null, byteString);
        return originalResponse.newBuilder().body(responseBody).build();
    }
}
