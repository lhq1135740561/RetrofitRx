package com.ly.masterviewmodelretrofitrxjava.view.activity;

import android.arch.lifecycle.ViewModel;
import android.os.Bundle;
import android.view.View;

import com.ly.masterviewmodelretrofitrxjava.R;
import com.ly.masterviewmodelretrofitrxjava.view.base.BaseActivity;

/**
 * Create by Allen Liu at 2019/2/19 9:42.
 */
public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected ViewModel initViewModel() {
        return null;
    }

    public void queryWeather(View view) {
        startActivity(QueryWeatherActivity.class);
    }

    public void queryNews(View view) {
        startActivity(QueryNewsActivity.class);
    }

    public void createQrCode(View view) {
        startActivity(QrCodeActivity.class);
    }

    public void failExample(View view) {
        startActivity(FailExampleActivity.class);
    }
}
