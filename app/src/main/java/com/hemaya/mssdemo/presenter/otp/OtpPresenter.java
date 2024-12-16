package com.hemaya.mssdemo.presenter.otp;


import static androidx.core.content.ContextCompat.getSystemService;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Build;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.hemaya.mssdemo.R;
import com.hemaya.mssdemo.utils.useCase.OtpUseCase;
import com.hemaya.mssdemo.view.otp.OtpViewInterface;

public class OtpPresenter implements OtpPresenterInterface {
    OtpViewInterface otpViewInterface;
    Context context;
    OtpUseCase otpUseCase;

    public OtpPresenter(Context context) {
        this.otpViewInterface = (OtpViewInterface) context;
        this.context = context;
        this.otpUseCase = new OtpUseCase(context);
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void generateOtp(String pin) {
        String otp = otpUseCase.generateOTP(pin);
        if (otp != null) {
            otpViewInterface.onOtpGenerated(otp);
        } else {
            otpViewInterface.onOtpGenerated("Error");
        }
    }

    @Override
    public void copyOtp(String otp) {
        ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("", otp);
        clipboard.setPrimaryClip(clip);
        otpViewInterface.showToast(context.getString(R.string.copiedOtp));

    }
}
