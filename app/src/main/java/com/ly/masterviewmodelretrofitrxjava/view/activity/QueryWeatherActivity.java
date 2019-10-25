package com.ly.masterviewmodelretrofitrxjava.view.activity;

import android.arch.lifecycle.ViewModel;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.Gson;
import com.ly.masterviewmodelretrofitrxjava.R;
import com.ly.masterviewmodelretrofitrxjava.model.Weather;
import com.ly.masterviewmodelretrofitrxjava.view.base.BaseActivity;
import com.ly.masterviewmodelretrofitrxjava.view_model.WeatherViewModel;
import com.ly.masterviewmodelretrofitrxjava.view_model.base.LViewModelProviders;

public class QueryWeatherActivity extends BaseActivity {

    private WeatherViewModel mWeatherViewModel;
    private EditText mEtCityName;
    private TextView mTvWeather;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_query_weather);
        mEtCityName = findViewById(R.id.et_cityName);
        mTvWeather = findViewById(R.id.tv_weather);
    }

    @Override
    protected ViewModel initViewModel() {
        mWeatherViewModel = LViewModelProviders.of(this, WeatherViewModel.class);
        mWeatherViewModel.getWeatherLiveData().observe(this, this::handlerWeather);
        return null;
    }

    private void handlerWeather(Weather weather) {
        StringBuilder result = new StringBuilder();
        for (Weather.InnerWeather.NearestWeather nearestWeather : weather.getData().getWeather()) {
            result.append("\n\n").append(new Gson().toJson(nearestWeather));
        }
        mTvWeather.setText(result.toString());
    }

    public void queryWeather(View view) {
        mTvWeather.setText(null);
        mWeatherViewModel.queryWeather(mEtCityName.getText().toString());
    }
}
