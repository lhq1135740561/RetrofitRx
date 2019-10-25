package com.ly.masterviewmodelretrofitrxjava.view.activity;

import android.arch.lifecycle.ViewModel;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.ly.masterviewmodelretrofitrxjava.R;
import com.ly.masterviewmodelretrofitrxjava.model.QrCode;
import com.ly.masterviewmodelretrofitrxjava.view.base.BaseActivity;
import com.ly.masterviewmodelretrofitrxjava.view_model.QrCodeViewModel;
import com.ly.masterviewmodelretrofitrxjava.view_model.base.LViewModelProviders;

public class QrCodeActivity extends BaseActivity {

    private QrCodeViewModel mQrCodeViewModel;
    private EditText mEtText;
    private ImageView mIvQrCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_code);
        mEtText = findViewById(R.id.et_text);
        mIvQrCode = findViewById(R.id.iv_qrCode);
    }

    @Override
    protected ViewModel initViewModel() {
        mQrCodeViewModel = LViewModelProviders.of(this, QrCodeViewModel.class);
        mQrCodeViewModel.getQrCodeMutableLiveData().observe(this, this::handleQrCode);
        return mQrCodeViewModel;
    }

    private void handleQrCode(QrCode qrCode) {
        mIvQrCode.setImageBitmap(qrCode.getBitmap());
    }

    public void createQrCode(View view) {
        mIvQrCode.setImageBitmap(null);
        mQrCodeViewModel.createQrCode(mEtText.getText().toString(), 600);
    }
}
