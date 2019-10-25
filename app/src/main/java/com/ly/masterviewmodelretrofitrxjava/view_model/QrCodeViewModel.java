package com.ly.masterviewmodelretrofitrxjava.view_model;

import android.arch.lifecycle.MutableLiveData;

import com.ly.masterviewmodelretrofitrxjava.http.datasource.QrCodeDataSource;
import com.ly.masterviewmodelretrofitrxjava.http.repo.QrCodeRepo;
import com.ly.masterviewmodelretrofitrxjava.model.QrCode;
import com.ly.masterviewmodelretrofitrxjava.view_model.base.BaseViewModel;

public class QrCodeViewModel extends BaseViewModel {
    private MutableLiveData<QrCode> mQrCodeMutableLiveData = new MutableLiveData<>();

    private QrCodeRepo mQrCodeRepo = new QrCodeRepo(new QrCodeDataSource(this));

    public void createQrCode(String text, int width) {
        mQrCodeRepo.createQrCode(text, width).observe(mLifecycleOwner, qrCode ->
                mQrCodeMutableLiveData.setValue(qrCode));
    }

    public MutableLiveData<QrCode> getQrCodeMutableLiveData() {
        return mQrCodeMutableLiveData;
    }
}
