package com.ly.masterviewmodelretrofitrxjava.http.datasource;

import com.ly.masterviewmodelretrofitrxjava.http.basic.BaseRemoteDataSource;
import com.ly.masterviewmodelretrofitrxjava.http.basic.callback.RequestCallback;
import com.ly.masterviewmodelretrofitrxjava.http.basic.config.HttpConfig;
import com.ly.masterviewmodelretrofitrxjava.http.datasource.base.IQrCodeDataSource;
import com.ly.masterviewmodelretrofitrxjava.http.service.ApiService;
import com.ly.masterviewmodelretrofitrxjava.model.QrCode;
import com.ly.masterviewmodelretrofitrxjava.view_model.base.BaseViewModel;

public class QrCodeDataSource extends BaseRemoteDataSource implements IQrCodeDataSource {
    public QrCodeDataSource(BaseViewModel baseViewModel) {
        super(baseViewModel);
    }

    @Override
    public void createQrCode(String text, int width, RequestCallback<QrCode> callback) {
        execute(getService(ApiService.class, HttpConfig.BASE_URL_QR_CODE).createQrCode(text, width), callback);
    }
}
