package com.hemaya.mssdemo.view.identification;

import android.widget.EditText;
import android.widget.TextView;

import com.hbb20.CountryCodePicker;

public interface IdentificationViewInterface {

    public void checkTerms(boolean checkTermsVal);
    public void onTypingEditText(EditText editText);
    public void onErrorEditText(EditText editText);
    public void onEmptyEditText(EditText editText);
    void  showError( String error);
    void goToHome();

    void updateLayout(Step step);
    void updateCongratulationLayout();
    void initFourthStep();
    void initSecondStep();
    void initThirdStep();
    void initFifthStep();
    void initSixthStep();
    void restartActivity();
    public void ShowChooseAuthDialog();

    public void showProgress();

    void showLayoutLoading();
    public void hideProgress();

    public void showToast(String message);

    void finishActivity();
}
