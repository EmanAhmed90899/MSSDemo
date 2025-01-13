package com.hemaya.mssdemo.presenter.otp;

public interface OtpPresenterInterface {
    void generateOtp(String pin);
    void copyOtp(String otp);
    void destroyApp();
}
