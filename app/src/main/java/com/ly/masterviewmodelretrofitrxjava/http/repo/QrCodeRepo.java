package com.ly.masterviewmodelretrofitrxjava.http.repo;

import android.arch.lifecycle.MutableLiveData;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import com.ly.masterviewmodelretrofitrxjava.http.basic.BaseRepo;
import com.ly.masterviewmodelretrofitrxjava.http.datasource.base.IQrCodeDataSource;
import com.ly.masterviewmodelretrofitrxjava.model.QrCode;

import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class QrCodeRepo extends BaseRepo<IQrCodeDataSource> {
    public QrCodeRepo(IQrCodeDataSource remoteDataSource) {
        super(remoteDataSource);
    }

    public MutableLiveData<QrCode> createQrCode(String text, int width) {
        MutableLiveData<QrCode> liveData = new MutableLiveData<>();
        mRemoteDataSource.createQrCode(text, width, qrCode -> Observable
                .create((ObservableOnSubscribe<Bitmap>) emitter -> {
                    Bitmap bitmap = base64ToBitmap(qrCode.getBase64_image());
                    emitter.onNext(bitmap);
                    emitter.onComplete();
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(bitmap -> {
                    qrCode.setBitmap(bitmap);
                    liveData.setValue(qrCode);
                }));
        return liveData;
    }

    private static Bitmap base64ToBitmap(String base64String) {
        byte[] decode = Base64.decode(base64String, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decode, 0, decode.length);
    }
}
