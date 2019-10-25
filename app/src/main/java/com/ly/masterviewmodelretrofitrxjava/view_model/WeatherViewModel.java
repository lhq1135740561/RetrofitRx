package com.ly.masterviewmodelretrofitrxjava.view_model;

import android.arch.lifecycle.MutableLiveData;

import com.ly.masterviewmodelretrofitrxjava.http.datasource.WeatherDataSource;
import com.ly.masterviewmodelretrofitrxjava.http.repo.WeatherRepo;
import com.ly.masterviewmodelretrofitrxjava.model.Weather;
import com.ly.masterviewmodelretrofitrxjava.view_model.base.BaseViewModel;

/**
 * Create by Allen Liu at 2019/2/19 10:59.
 */
public class WeatherViewModel extends BaseViewModel {

    private MutableLiveData<Weather> mWeatherLiveData;

    private WeatherRepo mWeatherRepo;

    public WeatherViewModel() {
        mWeatherLiveData = new MutableLiveData<>();
        mWeatherRepo = new WeatherRepo(new WeatherDataSource(this));
    }

    public void queryWeather(String cityName) {
        mWeatherRepo.queryWeather(cityName).observe(mLifecycleOwner, weather ->
                mWeatherLiveData.setValue(weather));
    }

    public MutableLiveData<Weather> getWeatherLiveData() {
        return mWeatherLiveData;
    }
}
