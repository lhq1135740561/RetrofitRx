package com.ly.masterviewmodelretrofitrxjava.view.activity;

import android.arch.lifecycle.ViewModel;
import android.os.Bundle;
import android.view.View;

import com.ly.masterviewmodelretrofitrxjava.R;
import com.ly.masterviewmodelretrofitrxjava.view.base.BaseActivity;
import com.ly.masterviewmodelretrofitrxjava.view_model.FailExampleViewModel;
import com.ly.masterviewmodelretrofitrxjava.view_model.base.LViewModelProviders;

public class FailExampleActivity extends BaseActivity {

    private FailExampleViewModel mFailExampleViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fail_example);
    }

    @Override
    protected ViewModel initViewModel() {
        mFailExampleViewModel = LViewModelProviders.of(this, FailExampleViewModel.class);
        return mFailExampleViewModel;
    }

    public void test1(View view) {
        mFailExampleViewModel.test1();
    }

    public void test2(View view) {
        mFailExampleViewModel.test2();
    }
}
