package com.ly.masterviewmodelretrofitrxjava.http.datasource;

import com.ly.masterviewmodelretrofitrxjava.http.basic.BaseRemoteDataSource;
import com.ly.masterviewmodelretrofitrxjava.http.basic.callback.RequestCallback;
import com.ly.masterviewmodelretrofitrxjava.http.datasource.base.IWeatherDataSource;
import com.ly.masterviewmodelretrofitrxjava.http.service.ApiService;
import com.ly.masterviewmodelretrofitrxjava.model.Weather;
import com.ly.masterviewmodelretrofitrxjava.view_model.base.BaseViewModel;

public class WeatherDataSource extends BaseRemoteDataSource implements IWeatherDataSource {
    public WeatherDataSource(BaseViewModel baseViewModel) {
        super(baseViewModel);
    }

    @Override
    public void queryWeather(String cityName, RequestCallback<Weather> callback) {
        execute(getService(ApiService.class).queryWeather(cityName), callback);
    }
}
