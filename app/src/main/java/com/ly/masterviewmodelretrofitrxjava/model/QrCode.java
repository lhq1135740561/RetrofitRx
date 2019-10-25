package com.ly.masterviewmodelretrofitrxjava.model;

import android.graphics.Bitmap;

public class QrCode {
    private String base64_image;

    private Bitmap bitmap;

    public String getBase64_image() {
        return base64_image;
    }

    public void setBase64_image(String base64_image) {
        this.base64_image = base64_image;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }
}
