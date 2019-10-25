package com.ly.masterviewmodelretrofitrxjava.http.datasource.base;

import com.ly.masterviewmodelretrofitrxjava.http.basic.callback.RequestCallback;
import com.ly.masterviewmodelretrofitrxjava.model.Weather;

public interface IWeatherDataSource {
    void queryWeather(String cityName, RequestCallback<Weather> callback);
}
