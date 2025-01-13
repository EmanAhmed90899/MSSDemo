package com.hemaya.mssdemo.presenter.identification;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentActivity;

import com.google.android.material.navigation.NavigationView;
import com.hbb20.CountryCodePicker;
import com.hemaya.mssdemo.R;
import com.hemaya.mssdemo.model.ActivationModel;
import com.hemaya.mssdemo.utils.langauge.LocaleHelper;
import com.hemaya.mssdemo.utils.storage.SharedPreferenceStorage;
import com.hemaya.mssdemo.utils.useCase.ActivationUseCase;
import com.hemaya.mssdemo.utils.useCase.IdentificationUseCase;
import com.hemaya.mssdemo.utils.views.BiometricSensorSDKScanListenerImpl;
import com.hemaya.mssdemo.view.identification.CountTimer;
import com.hemaya.mssdemo.view.identification.IdentificationViewInterface;
import com.hemaya.mssdemo.view.identification.Step;
import com.vasco.digipass.sdk.utils.biometricsensor.BiometricSensorSDK;
import com.vasco.digipass.sdk.utils.biometricsensor.BiometricSensorSDKErrorCodes;
import com.vasco.digipass.sdk.utils.biometricsensor.BiometricSensorSDKException;
import com.vasco.digipass.sdk.utils.biometricsensor.BiometricSensorSDKParams;
import com.vasco.digipass.sdk.utils.biometricsensor.BiometricSensorSDKScanListener;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class IdentificationPresenter implements IdentificationPresenterInterface, IdentificationUseCase.IdentificationUseCaseListener {


    IdentificationViewInterface identificationViewInterface;
    private Step currentStep = Step.STEP_ONE;
    private CountTimer countTimer;
    private Context context;
    private boolean checkTermsVal = false;
    boolean isAddNewUser = false;
    ActivationUseCase activationUseCase;
    private IdentificationUseCase identificationUseCase;
    private ActivationModel activationModel;

    @RequiresApi(api = Build.VERSION_CODES.O)
    public IdentificationPresenter(IdentificationViewInterface identificationViewInterface) {
        this.identificationViewInterface = identificationViewInterface;
        this.context = (Context) identificationViewInterface;
        identificationUseCase = new IdentificationUseCase(context);
        activationUseCase = new ActivationUseCase(context);
        activationUseCase.setSetResult((ActivationUseCase.SetResult) context);
        identificationUseCase.setIdentificationUseCaseListener(this);
    }

    @Override
    public void checkTerms() {
        checkTermsVal = !checkTermsVal;
        identificationViewInterface.checkTerms(checkTermsVal);
    }



    @Override
    public void pressBack(boolean isAddNewUser) {
        this.isAddNewUser = isAddNewUser;

        if (isAddNewUser) {
            identificationViewInterface.goToHome();
        }else if (currentStep == Step.STEP_ONE) {
            identificationViewInterface.finishActivity();
        } else {
            if (currentStep == Step.STEP_FOUR) {
                countTimer.cancelTimer();
            }
            currentStep = Step.STEP_ONE;
            identificationViewInterface.updateLayout(currentStep);
        }
    }

    @Override
    public Runnable timerFinished() {
        if (countTimer != null) {
            if (countTimer.countDownTimer != null) {
                countTimer.countDownTimer.cancel();
                countTimer.countDownTimer = null;
            }
        }
        return null;
    }

    @Override
    public boolean confirmTerms() {
        if (checkTermsVal) {
            currentStep = currentStep.getNext();
            identificationViewInterface.updateLayout(currentStep);
        }

        return checkTermsVal;
    }

    @Override
    public boolean validateNationalId(EditText nationalIdEd, EditText nationalIdConfEd, TextView messageErrorTxt) {


        String nationalId = nationalIdEd.getText().toString();
        String nationalIdConf = nationalIdConfEd.getText().toString();

        if (nationalId.isEmpty() && nationalIdConf.isEmpty()) {
            return false;
        } else {
            nationalId = nationalId.trim();
            nationalIdConf = nationalIdConf.trim();
            if (!nationalId.equals(nationalIdConf)) {
                identificationViewInterface.showError(context.getResources().getString(R.string.identity_not_match));
                return false;
            } else if (isValidNID(nationalId, new StringBuilder()) || isValidPassport(nationalId)) {

                showProgress();
                identificationUseCase.validateUser(nationalId);
                return true;
            } else {
                identificationViewInterface.showError(context.getResources().getString(R.string.invalid_national_idOrPassport));
                return false;
            }

        }
    }

    @Override
    public void validateOtp(TextView counterTxt) {
        countTimer = new CountTimer(counterTxt, (Context) identificationViewInterface, 1, context.getResources().getString(R.string.remaining));
        countTimer.startOTPTimer();

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public boolean pinValidation(EditText pin, EditText confirmPin, TextView messageErrorTxt) {
        String pinStr = pin.getText().toString();
        String confirmPinStr = confirmPin.getText().toString();
        if (!pinStr.isEmpty() && !confirmPinStr.isEmpty()) {
            if (!pinStr.equals(confirmPinStr)) {
                identificationViewInterface.showError(context.getResources().getString(R.string.pinNotMatch));
                return false;
            } else if (pinStr.length() != 6 || isSequential(pinStr) || isRepeated(pinStr)) {
                identificationViewInterface.showError(context.getResources().getString(R.string.pinNotSequentialOrRepeatedNumber));
                return false;
            } else {
                if
                (pinStr.equals(confirmPinStr)) {
                    createPin(pinStr);
                    return true;
                } else {
                    return false;
                }
            }
        } else {
            return false;
        }
    }

    @Override
    public void changeLang() {
        SharedPreferenceStorage sharedPreferenceStorage = new SharedPreferenceStorage(context);

        if (sharedPreferenceStorage.getLanguage().equals("en")) {
            LocaleHelper.setLocale(context, "ar");
            sharedPreferenceStorage.setLanguage("ar");
        } else if (sharedPreferenceStorage.getLanguage().equals("ar")) {
            LocaleHelper.setLocale(context, "en");
            sharedPreferenceStorage.setLanguage("en");
        }
        identificationViewInterface.restartActivity();
    }

    @Override
    public void resendOTP() {
        identificationUseCase.resendOtp();
    }

    @Override
    public void showSettingDialog() {
        activationUseCase.redirectToSettings();
    }

    @Override
    public void onTypingEditText(EditText editText) {
        identificationViewInterface.onTypingEditText(editText);
    }

    @Override
    public void onErrorEditText(EditText editText) {
        identificationViewInterface.onErrorEditText(editText);
    }

    @Override
    public void onEmptyEditText(EditText editText) {
        identificationViewInterface.onEmptyEditText(editText);
    }

    @Override
    public boolean validatePhone(String region, String phoneNumber, EditText phone, CountryCodePicker ccp, TextView messageErrorTxt) {
        if (!phoneNumber.isEmpty()) {
            showProgress();
            identificationUseCase.checkMobile(phoneNumber, region);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void successOTP(String otp) {
        if (otp != null) {
            showProgress();
            identificationUseCase.validateOtp(otp);
        }
    }


    @Override
    public void takePermission() {
        activationUseCase.takePermission();
    }

    @Override
    public boolean checkPermission() {
        return activationUseCase.checkAllPermission();
    }

    @Override
    public void scanCrontoCode() {
        activationUseCase.scanVerificationData();
    }

    @Override
    public void setAuthType() {

    }

    @Override
    public void openDrawer(DrawerLayout drawerLayout, NavigationView navigationView) {
        drawerLayout.openDrawer(navigationView);
    }

    @Override
    public void pressMenu(DrawerLayout drawerLayout, int id) {

    }

    @Override
    public void showChooseAuthDialog() {
        identificationViewInterface.ShowChooseAuthDialog();
    }

    @Override
    public void showToast(String message) {

    }

    @Override
    public void openCreatePin() {
        currentStep = Step.STEP_FIVE;
        currentStep = currentStep.getNext();
        identificationViewInterface.updateLayout(currentStep);
        identificationViewInterface.initSixthStep();

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void createPin(String password) {
        activationUseCase.MultiDeviceActivationInstance(password);
    }

    @Override
    public void checkBiometric() {
        BiometricSensorSDKScanListener listener =
                new BiometricSensorSDKScanListenerImpl((Activity) context);


        try {
            // Create the parameters used during the user biometry verification
            BiometricSensorSDKParams biometricSensorSDKParams =
                    createBiometricSensorSDKParams();

            // Start the user biometric verification
            BiometricSensorSDK.verifyUserBiometry(
                    listener,
                    (FragmentActivity) context,
                    biometricSensorSDKParams);
        } catch (BiometricSensorSDKException e) {
            // Handle properly the BiometricSensorSDKException
            displayError(e);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void setBiometric() {
        activationUseCase.enableBiometric();
    }

    @Override
    public void setStep(Step step) {
        currentStep = step;
    }

    @Override
    public void manualActivation(EditText activationCode, EditText password, TextView messageErrorTxt) {
        String activationCodeStr = activationCode.getText().toString();
        String passwordStr = password.getText().toString();
        if (!activationCodeStr.isEmpty() && !passwordStr.isEmpty()) {
            activationUseCase.manualActivation(activationCodeStr, passwordStr, messageErrorTxt);

        }
    }

    @Override
    public void showProgress() {
        identificationViewInterface.showProgress();
    }

    @Override
    public void hideProgress() {
        identificationViewInterface.hideProgress();
    }


    // Method to check if the PIN is sequential (increasing or decreasing)
    public static boolean isSequential(String pin) {
        boolean isIncreasing = true;
        boolean isDecreasing = true;

        for (int i = 0; i < pin.length() - 1; i++) {
            int currentDigit = Character.getNumericValue(pin.charAt(i));
            int nextDigit = Character.getNumericValue(pin.charAt(i + 1));

            // Check if each next digit is greater than the current one
            if (currentDigit + 1 != nextDigit) {
                isIncreasing = false;
            }

            // Check if each next digit is smaller than the current one
            if (currentDigit - 1 != nextDigit) {
                isDecreasing = false;
            }
        }

        // Return true if either increasing or decreasing sequentially
        return isIncreasing || isDecreasing;
    }

    public static boolean isRepeated(String pin) {
        char firstChar = pin.charAt(0);

        for (int i = 1; i < pin.length(); i++) {
            if (pin.charAt(i) != firstChar) {
                return false;  // PIN is not repeated
            }
        }

        return true;  // All digits are the same (repeated)
    }

    private BiometricSensorSDKParams createBiometricSensorSDKParams()
            throws BiometricSensorSDKException {
        // Create Builder for BiometricSensorSDKParams model
        return new BiometricSensorSDKParams.Builder()
                // Set the title of the biometric prompt. The title is mandatory, when it is not
                // set it takes default value: "Please, use your fingerprint scanner to
                // authenticate.".
                .setTitle(context.getString(R.string.biometric_prompt_title))

                // Set the subtitle of the biometric prompt.
                // The subtitle is optional, when it is not set it is not visible.
                .setSubtitle(context.getString(R.string.biometric_prompt_subtitle))

                // Set the description of the biometric prompt.
                // The description is optional, when it is not set it is not visible.
                .setDescription(context.getString(R.string.biometric_prompt_description))

                // Set the negative button text in the biometric prompt.
                // The text is mandatory, by default it is "Cancel".
                // The negative button closes the biometric prompt and it is tightly connected with
                // BiometricSensorSDKScanListener.onBiometryNegativeButtonClicked() method.
                .setNegativeButtonText(context.getString(R.string.biometric_prompt_button))

                // Set the message which is returned when the biometric user authentication failed.
                .setAuthenticationFailedText(context.getString(R.string.authentication_failed_text))

                // Create BiometricSensorSDKParams using Builder
                .create();
    }

    private void displayError(BiometricSensorSDKException e) {
        switch (e.getErrorCode()) {
            case BiometricSensorSDKErrorCodes.INTERNAL_ERROR: {
                showToast(R.string.internal_error + " : " + e);
                break;
            }
            case BiometricSensorSDKErrorCodes.PROMPT_PARAM_INVALID: {
                showToast(R.string.prompt_parameter_invalid + " : " + e);
                break;
            }
            case BiometricSensorSDKErrorCodes.ACTIVITY_NULL: {
                showToast(R.string.activity_null + " : " + e);
                break;
            }
            case BiometricSensorSDKErrorCodes.LISTENER_NULL: {
                showToast(R.string.biometric_listener_null + " : " + e);
                break;
            }
            case BiometricSensorSDKErrorCodes.CONTEXT_NULL: {
                showToast(R.string.context_null + " : " + e);
                break;
            }
            case BiometricSensorSDKErrorCodes.FRAGMENT_NULL: {
                showToast(R.string.fragment_null + " : " + e);
                break;
            }
            case BiometricSensorSDKErrorCodes.BIOMETRIC_NOT_USABLE: {
                showToast(R.string.biometry_not_usable + " : " + e);
                break;
            }
            case BiometricSensorSDKErrorCodes.BIOMETRIC_PERMISSION_DENIED: {
                showToast(R.string.permission_denied + " : " + e);
                break;
            }
            case BiometricSensorSDKErrorCodes.NO_BIOMETRY_ENROLLED: {
                showToast(R.string.no_biometry_enrolled + " : " + e);
                break;
            }
            default: {
                showToast(R.string.unknown_error + " : " + e);
                break;
            }
        }
    }

    public boolean isValidNID(String NID, StringBuilder traceLog) {
        traceLog.append("\n#Validating NID: ").append(NID).append("\n");

        // Check if NID is empty
        if (NID.isEmpty()) {
            traceLog.append("NID ").append(NID).append(" is empty.\n");
            return false;
        }

        // Validate length and numeric characters (14 digits)
        String regExpNIDLengthNumbers = "^[0-9]{14}$";
        Pattern patternLengthNumber = Pattern.compile(regExpNIDLengthNumbers);
        Matcher matcherLengthNumber = patternLengthNumber.matcher(NID);
        if (!matcherLengthNumber.find()) {
            traceLog.append("NID ").append(NID).append(" is invalid: incorrect length or contains non-numeric characters.\n");
            return false;
        }

        traceLog.append("NID length = ").append(NID.length()).append("\n");

        // Validate the NID pattern
        String regExpNID = "^([23])\\s?(?:([0-9]{2})\\s?(?:(?:(0[13578]|1[02])\\s?(0[1-9]|[12][0-9]|3[01]))\\s?|(?:(0[469]|11)\\s?(0[1-9]|[12][0-9]|30))\\s?|(?:(02)\\s?(0[1-9]|1[0-9]|2[0-8]))\\s?)|(?:(04|08|[2468][048]|[13579][26]|(?<=3)00)\\s?(02)\\s?(29)\\s?))(0[1-6]|[12][1-9]|3[1-5]|88)\\s?([0-9]{3}([0-9]))\\s?([0-9])$";
        Pattern pattern = Pattern.compile(regExpNID);
        Matcher matcher = pattern.matcher(NID);
        if (!matcher.find()) {
            traceLog.append("Invalid NID. NID doesn't match the specified pattern.\n");
            return false;
        }

        traceLog.append("NID is valid.\n");
        return true;
    }

    public boolean isValidPassport(String passport) {
        // Example regex for generic passport validation
        String regex = "^[A-Z0-9]{6,9}$";
        return passport.matches(regex);
    }

    @Override
    public void onValidateUserSuccess(String mobileNumber, String userName, String nationalId, String passportNumber) {
        activationUseCase.setActivationModel(userName, mobileNumber, nationalId);

        currentStep = currentStep.getNext();
        new SharedPreferenceStorage(context).setUserName(userName);
        identificationViewInterface.updateLayout(currentStep);
    }

    @Override
    public void onFailure(String message) {
        identificationViewInterface.showError(message);
    }

    @Override
    public void onErrorMessage(String message) {
        identificationViewInterface.showError(message);
    }

    @Override
    public void onMobileSuccess() {
//        currentStep = currentStep.getNext();
//        identificationViewInterface.updateLayout(currentStep);

    }

    @Override
    public void onOtpSentSuccess(String uuid) {
        currentStep = currentStep.getNext();
        identificationViewInterface.updateLayout(currentStep);
    }

    @Override
    public void onOtpVerifiedSuccess() {
        countTimer.cancelTimer();
        currentStep = currentStep.getNext();
        identificationViewInterface.updateLayout(currentStep);
    }

    @Override
    public void onResendOtpSuccess(String message) {
        identificationViewInterface.showToast(message);

    }
}
