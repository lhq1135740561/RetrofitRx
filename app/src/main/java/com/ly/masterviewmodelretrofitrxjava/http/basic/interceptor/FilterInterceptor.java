package com.ly.masterviewmodelretrofitrxjava.http.basic.interceptor;

import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.ly.masterviewmodelretrofitrxjava.http.basic.config.HttpConfig;

import java.io.IOException;

import okhttp3.Headers;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class FilterInterceptor implements Interceptor {
    @NonNull
    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        Request originalRequest = chain.request();
        HttpUrl.Builder httpBuilder = originalRequest.url().newBuilder();
        Headers headers = originalRequest.headers();
        if (headers.size() > 0) {
            String requestType = headers.get(HttpConfig.HTTP_REQUEST_TYPE_KEY);
            if (!TextUtils.isEmpty(requestType)) {
                assert requestType != null;
                switch (requestType) {
                    case HttpConfig.BASE_URL_WEATHER:
                        httpBuilder.addQueryParameter(HttpConfig.KEY, HttpConfig.KEY_WEATHER);
                        break;
                    case HttpConfig.BASE_URL_QR_CODE:
                        httpBuilder.addQueryParameter(HttpConfig.KEY, HttpConfig.KEY_QR_CODE);
                        break;
                    case HttpConfig.BASE_URL_NEWS:
                        httpBuilder.addQueryParameter(HttpConfig.KEY, HttpConfig.KEY_NEWS);
                        break;
                }
            }
        }
        Request.Builder requestBuilder = originalRequest.newBuilder()
                .removeHeader(HttpConfig.HTTP_REQUEST_TYPE_KEY)
                .url(httpBuilder.build());
        return chain.proceed(requestBuilder.build());

        /**
         *  HAL, expected position 1041744 , only wrote 1041736
         * 2019-10-25 09:25:47.792 18301-18383/com.ly.masterviewmodelretrofitrxjava
         * D/OkHttp: --> GET http://op.juhe.cn/onebox/weather/query?cityname=%E5%B9%BF%E5%B7%9E
         * 2019-10-25 09:25:47.792 18301-18383/com.ly.masterviewmodelretrofitrxjava D/OkHttp: Accept-Encoding: gzip
         * 2019-10-25 09:25:47.792 18301-18383/com.ly.masterviewmodelretrofitrxjava D/OkHttp: Accept: application/json
         * 2019-10-25 09:25:47.792 18301-18383/com.ly.masterviewmodelretrofitrxjava D/OkHttp: Content_Type: application/json; charset=utf-8
         * 2019-10-25 09:25:47.792 18301-18383/com.ly.masterviewmodelretrofitrxjava D/OkHttp: --> END GET
         */
    }
}
