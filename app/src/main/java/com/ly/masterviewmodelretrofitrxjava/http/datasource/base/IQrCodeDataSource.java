package com.ly.masterviewmodelretrofitrxjava.http.datasource.base;

import com.ly.masterviewmodelretrofitrxjava.http.basic.callback.RequestCallback;
import com.ly.masterviewmodelretrofitrxjava.model.QrCode;

public interface IQrCodeDataSource {
    void createQrCode(String text, int width, RequestCallback<QrCode> callback);
}
