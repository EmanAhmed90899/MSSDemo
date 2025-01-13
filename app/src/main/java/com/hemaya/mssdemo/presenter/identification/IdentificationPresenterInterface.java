package com.hemaya.mssdemo.presenter.identification;

import android.widget.EditText;
import android.widget.TextView;

import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
import com.hbb20.CountryCodePicker;
import com.hemaya.mssdemo.view.identification.Step;

public interface IdentificationPresenterInterface {
    void checkTerms();
    void pressBack(boolean isAddNewUser);
    Runnable timerFinished();
    boolean confirmTerms();
    boolean validateNationalId(EditText nationalId,EditText nationalIdConf,TextView messageErrorTxt);
    void validateOtp(TextView counterTxt);

    boolean checkPermission();
    boolean pinValidation(EditText pin, EditText confirmPin, TextView messageErrorTxt);
    void changeLang();
    void resendOTP();
    void showSettingDialog();
    void onTypingEditText(EditText editText);
    void onErrorEditText(EditText editText);
    void onEmptyEditText(EditText editText);

    boolean validatePhone(String region,String mobile,EditText phone, CountryCodePicker ccp, TextView messageErrorTxt);

    void successOTP(String otp);

    public void takePermission();

    public void scanCrontoCode();

    public void setAuthType();

    public void openDrawer(DrawerLayout drawerLayout, NavigationView navigationView);

    public void pressMenu(DrawerLayout drawerLayout,int id);

    public void showChooseAuthDialog();

    public void showProgress();

     void hideProgress();

     void showToast(String message);
    void openCreatePin();
    void createPin(String password);

    void checkBiometric();

    void setBiometric();

    void setStep(Step step);
    void manualActivation(EditText activationCode, EditText password, TextView messageErrorTxt);
}
