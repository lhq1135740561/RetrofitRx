package com.ly.masterviewmodelretrofitrxjava.http.basic;

import com.ly.masterviewmodelretrofitrxjava.BuildConfig;
import com.ly.masterviewmodelretrofitrxjava.holder.ContextHolder;
import com.ly.masterviewmodelretrofitrxjava.http.basic.config.HttpCode;
import com.ly.masterviewmodelretrofitrxjava.http.basic.config.HttpConfig;
import com.ly.masterviewmodelretrofitrxjava.http.basic.exception.AccountInvalidException;
import com.ly.masterviewmodelretrofitrxjava.http.basic.exception.ServerResultException;
import com.ly.masterviewmodelretrofitrxjava.http.basic.exception.TokenInvalidException;
import com.ly.masterviewmodelretrofitrxjava.http.basic.interceptor.FilterInterceptor;
import com.ly.masterviewmodelretrofitrxjava.http.basic.interceptor.HeaderInterceptor;
import com.ly.masterviewmodelretrofitrxjava.http.basic.interceptor.HttpInterceptor;
import com.ly.masterviewmodelretrofitrxjava.http.basic.model.BaseResponseBody;
import com.readystatesoftware.chuck.ChuckInterceptor;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitManagement {
    private static final long READ_TIMEOUT = 6000;

    private static final long WRITE_TIMEOUT = 6000;

    private static final long CONNECT_TIMEOUT = 6000;

    private final Map<String, Object> mServiceMap = new ConcurrentHashMap<>();

    private RetrofitManagement() {
    }

    public static RetrofitManagement getInstance() {
        return RetrofitHolder.mRetrofitManagement;
    }

    private static class RetrofitHolder {
        private static final RetrofitManagement mRetrofitManagement = new RetrofitManagement();
    }

    private Retrofit createRetrofit(String url) {
        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .readTimeout(READ_TIMEOUT, TimeUnit.SECONDS)
                .writeTimeout(WRITE_TIMEOUT, TimeUnit.SECONDS)
                .connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS)
                .addInterceptor(new HttpInterceptor())
                .addInterceptor(new HeaderInterceptor())
                .addInterceptor(new FilterInterceptor())
                .retryOnConnectionFailure(true);
        if (BuildConfig.DEBUG) {
            HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
            httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            builder.addInterceptor(httpLoggingInterceptor);
            builder.addInterceptor(new ChuckInterceptor(ContextHolder.getContext()));
        }
        OkHttpClient client = builder.build();
        return new Retrofit.Builder()
                .client(client)
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
    }

    <T> ObservableTransformer<BaseResponseBody<T>, T> applySchedulers() {
        return observable -> observable.subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap(result -> {
                    switch (result.getCode()) {
                        case HttpCode.CODE_SUCCESS:
                            return createData(result.getData());
                        case HttpCode.CODE_TOKEN_INVALID: {
                            throw new TokenInvalidException();
                        }
                        case HttpCode.CODE_ACCOUNT_INVALID: {
                            throw new AccountInvalidException();
                        }
                        default: {
                            throw new ServerResultException(result.getCode(), result.getMsg());
                        }
                    }
                });
    }

    private <T> ObservableSource<? extends T> createData(T t) {
        return Observable.create(emitter -> {
            try {
                emitter.onNext(t);
                emitter.onComplete();
            } catch (Exception e) {
                emitter.onError(e);
            }
        });
    }

    <T> T getService(Class<T> clazz) {
        return getService(clazz, HttpConfig.BASE_URL_WEATHER);
    }

    @SuppressWarnings("unchecked")
    <T> T getService(Class<T> clazz, String host) {
        T value;
        if (mServiceMap.containsKey(host)) {
            Object obj = mServiceMap.get(host);
            if (obj == null) {
                value = createRetrofit(host).create(clazz);
                mServiceMap.put(host, value);
            } else {
                value = (T) obj;
            }
        } else {
            value = createRetrofit(host).create(clazz);
            mServiceMap.put(host, value);
        }
        return value;
    }
}
