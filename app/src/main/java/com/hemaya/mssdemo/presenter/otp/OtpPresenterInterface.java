package com.hemaya.mssdemo.presenter.otp;

import android.widget.TextView;

public interface OtpPresenterInterface {
    void generateOtp(String pin);
    void copyOtp(String otp);
}
