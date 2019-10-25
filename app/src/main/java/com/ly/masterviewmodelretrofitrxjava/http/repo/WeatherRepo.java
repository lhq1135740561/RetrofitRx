package com.ly.masterviewmodelretrofitrxjava.http.repo;

import android.arch.lifecycle.MutableLiveData;

import com.ly.masterviewmodelretrofitrxjava.http.basic.BaseRepo;
import com.ly.masterviewmodelretrofitrxjava.http.datasource.base.IWeatherDataSource;
import com.ly.masterviewmodelretrofitrxjava.model.Weather;

public class WeatherRepo extends BaseRepo<IWeatherDataSource> {
    public WeatherRepo(IWeatherDataSource remoteDataSource) {
        super(remoteDataSource);
    }

    public MutableLiveData<Weather> queryWeather(String cityName) {
        MutableLiveData<Weather> weatherMutableLiveData = new MutableLiveData<>();
        mRemoteDataSource.queryWeather(cityName, weatherMutableLiveData::setValue);
        return weatherMutableLiveData;
    }
}
