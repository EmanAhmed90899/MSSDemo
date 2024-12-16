package com.hemaya.mssdemo.view.identification;

import android.widget.EditText;
import android.widget.TextView;

import com.hbb20.CountryCodePicker;

public interface IdentificationViewInterface {

    public void checkTerms(boolean checkTermsVal);
    public void onTypingEditText(EditText editText);
    public void onErrorEditText(EditText editText);
    public void onEmptyEditText(EditText editText);
    void  showError(TextView editText, String error);

    void onErrorCountryPicker(CountryCodePicker ccp);
    public void onErrorOTP();
    public void onEmptyOTP();
    public void onSetOtp();
    public void onTimerFinish();

    void updateLayout(Step step);
    void updateCongratulationLayout();
    void initFourthStep();
    void initSecondStep();
    void initThirdStep();
    void initFifthStep();
    void initSixthStep();

    public void ShowChooseAuthDialog();

    public void showProgress();

    public void hideProgress();

    public void showToast(String message);

}
