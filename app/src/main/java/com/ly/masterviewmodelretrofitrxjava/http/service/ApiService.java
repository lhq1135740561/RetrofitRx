package com.ly.masterviewmodelretrofitrxjava.http.service;

import com.ly.masterviewmodelretrofitrxjava.http.basic.config.HttpConfig;
import com.ly.masterviewmodelretrofitrxjava.http.basic.model.BaseResponseBody;
import com.ly.masterviewmodelretrofitrxjava.model.NewsPack;
import com.ly.masterviewmodelretrofitrxjava.model.QrCode;
import com.ly.masterviewmodelretrofitrxjava.model.Weather;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Query;

public interface ApiService {
    @Headers({HttpConfig.HTTP_REQUEST_TYPE_KEY + ":" + HttpConfig.HTTP_REQUEST_WEATHER})
    @GET("onebox/weather/query")
    Observable<BaseResponseBody<Weather>> queryWeather(@Query("cityname") String cityName);

    @Headers({HttpConfig.HTTP_REQUEST_TYPE_KEY + ":" + HttpConfig.HTTP_REQUEST_QR_CODE})
    @GET("qrcode/api")
    Observable<BaseResponseBody<QrCode>> createQrCode(@Query("text") String text, @Query("w") int width);

    @Headers({HttpConfig.HTTP_REQUEST_TYPE_KEY + ":" + HttpConfig.HTTP_REQUEST_NEWS})
    @GET("toutiao/index")
    Observable<BaseResponseBody<NewsPack>> getNews();

    @GET("leavesC/test1")
    Observable<BaseResponseBody<String>> test1();

    @GET("leavesC/test2")
    Observable<BaseResponseBody<String>> test2();
}
