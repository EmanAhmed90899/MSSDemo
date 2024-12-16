package com.hemaya.mssdemo.view.identification;

import android.content.Intent;
import android.graphics.BlendMode;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.ScrollingMovementMethod;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.hbb20.CountryCodePicker;
import com.hemaya.mssdemo.R;
import com.hemaya.mssdemo.presenter.identification.IdentificationPresenter;
import com.hemaya.mssdemo.presenter.identification.IdentificationPresenterInterface;
import com.hemaya.mssdemo.utils.storage.SharedPreferenceStorage;
import com.hemaya.mssdemo.utils.storage.UserDatabaseHelper;
import com.hemaya.mssdemo.utils.useCase.ActivationUseCase;
import com.hemaya.mssdemo.utils.views.ShowProgressBar;
import com.hemaya.mssdemo.view.BaseActivity;
import com.hemaya.mssdemo.view.home.HomeView;

public class IdentificationView extends BaseActivity implements CountTimer.TimerListener, IdentificationViewInterface, ActivationUseCase.SetResult {
    private ImageView backImg;
    private StepperLineView stepperLineView;
    private TextView titleStepper, contentStepper, counterTxt, errorOTPMess;
    private FrameLayout stepContainer;
    private Button nextButton;
    private OTPDashedView otpDashedView;
    private LinearLayout mainLayout;
    private IdentificationPresenterInterface identificationPresenterInterface;
    private CheckBox acceptTermsAndConditions;
    private Step currentStep = Step.STEP_TWO;
    private EditText nationalIdEd, nationalIdConfEd;
    private TextView errorNational;

    private EditText mobileNumberEd;
    private TextView errorMobile;
    String countryCode;
    private CountryCodePicker countryCodePicker;

    private ShowProgressBar showProgressBar;

    private LinearLayout scanLayout, createPinLayout, manualLayout;

    private EditText etPin, etConfirmPin;
    private TextView errorPin, biometricText;
    private LinearLayout biometricLayout;
    private ImageView biometricImg;
    private SharedPreferenceStorage sharedPreferenceStorage;

    private UserDatabaseHelper userDatabaseHelper;

    private TextView errorManualActivation;
    private EditText activationId, activationPassword;
    Button activateButton;
    boolean isAddNewUser = false;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_identification);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        init();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void init() {
        sharedPreferenceStorage = new SharedPreferenceStorage(this);
        backImg = findViewById(R.id.backImg);
        stepperLineView = findViewById(R.id.stepperLine);
        titleStepper = findViewById(R.id.titleStepper);
        contentStepper = findViewById(R.id.contentStepper);
        stepContainer = findViewById(R.id.stepContainer);
        nextButton = findViewById(R.id.nextBtn);
        mainLayout = findViewById(R.id.mainLayout);
        userDatabaseHelper = new UserDatabaseHelper(this);
        identificationPresenterInterface = new IdentificationPresenter(this);
        showProgressBar = new ShowProgressBar(this);
        isAddNewUser = getIntent().getBooleanExtra("isAddNewUser", false);
        assign();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void assign() {
        stepperLineView.setTextViews(titleStepper, contentStepper);
        stepperLineView.setSteps(6);

        if (sharedPreferenceStorage.getRegistrationId() != null) {
            stepperLineView.setCurrentStep(5);
            updateCongratulationLayout();
        }
//        else if(sharedPreferenceStorage.getUserId()!=null) {
//            stepperLineView.setCurrentStep(4);
//            updateLayout(Step.STEP_FOUR);
//        }
        else if (isAddNewUser) {
            stepperLineView.setCurrentStep(2);
            identificationPresenterInterface.setStep(Step.STEP_TWO);
            updateLayout(Step.STEP_TWO);
            initSecondStep();
        } else {
            stepperLineView.setCurrentStep(1);
            updateLayout(Step.STEP_ONE);
        }


        identificationPresenterInterface.takePermission();

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int screenWidth = displayMetrics.widthPixels;


        ViewGroup.LayoutParams params = nextButton.getLayoutParams();
        params.width = (screenWidth / 2) + 100; // Half of screen width
        nextButton.setLayoutParams(params);

        onClick();
    }

    private void onClick() {
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentStep != null) {
                    switch (currentStep) {
                        case STEP_ONE:
                            boolean valCheck = identificationPresenterInterface.confirmTerms();
                            if (valCheck) initSecondStep();
                            break;
                        case STEP_TWO:
                            boolean nationalCheck = identificationPresenterInterface.validateNationalId(nationalIdEd, nationalIdConfEd, errorNational);
                            if (nationalCheck) initThirdStep();
                            break;
                        case STEP_THREE:
                            boolean mobileCheck = identificationPresenterInterface.validatePhone(countryCode + mobileNumberEd.getText().toString(), mobileNumberEd, countryCodePicker, errorMobile);
                            if (mobileCheck) initFourthStep();
                            break;
                        case STEP_FOUR:
                            if (!otpDashedView.getOtp().isEmpty()) {
                                identificationPresenterInterface.successOTP();
                                initFifthStep();
                            }
                            break;
                        case STEP_FIVE:
                            initSixthStep();
                            break;
                        case STEP_SIX:
                            identificationPresenterInterface.pinValidation(etPin, etConfirmPin, errorPin);
                            break;
                    }

                }
            }
        });


        backImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                identificationPresenterInterface.pressBack();
            }
        });


    }

    @Override
    public void updateLayout(Step currentStep) {
        this.currentStep = currentStep;
        // Replace the content with the corresponding layout for the current step
        stepContainer.removeAllViews();
        View newContentView = getLayoutInflater().inflate(currentStep.getLayoutResId(), stepContainer, false);
        stepContainer.addView(newContentView);
        // Update the stepper line
        stepperLineView.setCurrentStep(currentStep.getStep());
        // Calculate 3/4 of the screen height
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        int screenHeight = displayMetrics.heightPixels;
        int thresholdHeight = (int) (screenHeight * 0.55);
        int contentHeight = (int) (screenHeight * 0.7);
        mainLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, contentHeight));
        if (currentStep == Step.STEP_ONE) {
            acceptTermsAndConditions = findViewById(R.id.acceptTermsAndConditions);
            initFirstStep();
            contentStepper.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, thresholdHeight));
            stepContainer.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        } else {
            contentStepper.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            stepContainer.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, thresholdHeight));

        }
    }

    @Override
    public void updateCongratulationLayout() {
        nextButton.setVisibility(View.GONE);
        titleStepper.setVisibility(View.GONE);
        contentStepper.setVisibility(View.GONE);
        stepContainer.removeAllViews();
        View newContentView = getLayoutInflater().inflate(R.layout.congratulation_layout, stepContainer, false);
        stepContainer.addView(newContentView);
        createPinLayout = findViewById(R.id.createPinLayout);
        createPinLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                identificationPresenterInterface.openCreatePin();
            }
        });

    }

    public void updateManualActivationLayout() {
        nextButton.setVisibility(View.GONE);
        titleStepper.setVisibility(View.GONE);
        contentStepper.setVisibility(View.GONE);
        stepContainer.removeAllViews();
        View newContentView = getLayoutInflater().inflate(R.layout.manual_activation_layout, stepContainer, false);
        stepContainer.addView(newContentView);

        activationId = findViewById(R.id.activationId);
        activationPassword = findViewById(R.id.activationPassword);
        errorManualActivation = findViewById(R.id.errorManualActivation);
        activateButton = findViewById(R.id.activateButton);
//        if (sharedPreferenceStorage.getLanguage().equals("ar")) {
//            activationId.setGravity(Gravity.END);
//            activationPassword.setGravity(Gravity.END);
//        }
        activationId.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                activationId.setBackground(getResources().getDrawable(R.drawable.border_edittext));
                errorManualActivation.setText("");
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                activationId.setBackground(getResources().getDrawable(R.drawable.border_edittext));
                errorManualActivation.setText("");
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        activationPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                activationPassword.setBackground(getResources().getDrawable(R.drawable.border_edittext));
                errorManualActivation.setText("");
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                activationPassword.setBackground(getResources().getDrawable(R.drawable.border_edittext));
                errorManualActivation.setText("");
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        activateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                identificationPresenterInterface.manualActivation(activationId, activationPassword, errorManualActivation);
            }
        });

    }

    @Override
    public void initFourthStep() {
        otpDashedView = stepContainer.findViewById(R.id.otpDashedView);
        counterTxt = stepContainer.findViewById(R.id.counterTxt);
        errorOTPMess = stepContainer.findViewById(R.id.errorOTPMess);
        identificationPresenterInterface.validateOtp(counterTxt);

    }

    @Override
    public void initSecondStep() {
        errorNational = stepContainer.findViewById(R.id.errorNationalId);
        nationalIdEd = stepContainer.findViewById(R.id.et_national_id);
        nationalIdConfEd = stepContainer.findViewById(R.id.et_national_id_confirm);
//        if (sharedPreferenceStorage.getLanguage().equals("ar")) {
//            nationalIdEd.setGravity(Gravity.END);
//            nationalIdConfEd.setGravity(Gravity.END);
//        }
        nationalIdEd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Log.d("** TAG", "onTextChanged: " + count);
                if (nationalIdEd.getText().toString().isEmpty()) {
                    identificationPresenterInterface.onEmptyEditText(nationalIdEd);
                } else {
                    identificationPresenterInterface.onTypingEditText(nationalIdEd);
                }
                errorNational.setText("");
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        nationalIdConfEd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (nationalIdConfEd.getText().toString().isEmpty()) {
                    identificationPresenterInterface.onEmptyEditText(nationalIdConfEd);
                } else {
                    identificationPresenterInterface.onTypingEditText(nationalIdConfEd);
                }
                errorNational.setText("");


            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        nextButton.setTextColor(getResources().getColor(R.color.white));
    }

    public void initFirstStep() {
        drawText();
        acceptTermsAndConditions.setOnCheckedChangeListener((buttonView, isChecked) -> {
            identificationPresenterInterface.checkTerms();
        });


    }

    @Override
    public void initThirdStep() {

        mobileNumberEd = stepContainer.findViewById(R.id.et_mobile_number);
//        if (sharedPreferenceStorage.getLanguage().equals("ar")) {
//            mobileNumberEd.setGravity(Gravity.END);
//        }
        errorMobile = stepContainer.findViewById(R.id.errorMobile);
        countryCodePicker = stepContainer.findViewById(R.id.countryCodePicker);
        if (sharedPreferenceStorage.getLanguage().equals("ar")) {
            countryCodePicker.changeDefaultLanguage(CountryCodePicker.Language.ARABIC);

        }else {
            countryCodePicker.changeDefaultLanguage(CountryCodePicker.Language.ENGLISH);
        }
        countryCodePicker.setCountryForNameCode("EG");
        countryCode = countryCodePicker.getSelectedCountryCode();
        countryCodePicker.setOnCountryChangeListener(new CountryCodePicker.OnCountryChangeListener() {
            @Override
            public void onCountrySelected() {
                countryCode = countryCodePicker.getSelectedCountryCode();
                Log.d("** TAG", "onCountrySelected: " + countryCodePicker.getSelectedCountryCode());
            }
        });
        mobileNumberEd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Log.d("** TAG", "onTextChanged: " + count);
                if (mobileNumberEd.getText().toString().isEmpty()) {
                    identificationPresenterInterface.onEmptyEditText(mobileNumberEd);
                } else {
                    identificationPresenterInterface.onTypingEditText(mobileNumberEd);
                }
                errorMobile.setText("");
                countryCodePicker.setBackground(getResources().getDrawable(R.drawable.border_edittext));
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    @Override
    public void initFifthStep() {
        scanLayout = findViewById(R.id.scanLayout);
        scanLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                identificationPresenterInterface.scanCrontoCode();
//                updateCongratulationLayout();
            }
        });
        manualLayout = findViewById(R.id.manualLayout);

        nextButton.setVisibility(View.GONE);

        manualLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateManualActivationLayout();
            }
        });

    }

    @Override
    public void initSixthStep() {
        titleStepper.setVisibility(View.VISIBLE);
        contentStepper.setVisibility(View.VISIBLE);
        nextButton.setVisibility(View.VISIBLE);
        nextButton.setText(getResources().getText(R.string.ok));

        etPin = stepContainer.findViewById(R.id.et_pin);
        etConfirmPin = stepContainer.findViewById(R.id.et_confirm_pin);
        errorPin = stepContainer.findViewById(R.id.errorPin);
        biometricText = stepContainer.findViewById(R.id.biometricText);
        biometricLayout = stepContainer.findViewById(R.id.biometricLayout);
        biometricImg = stepContainer.findViewById(R.id.biometricImg);
        if (sharedPreferenceStorage.getLanguage().equals("ar")) {
            etPin.setGravity(Gravity.END);
            etConfirmPin.setGravity(Gravity.END);
        }
        biometricLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                identificationPresenterInterface.checkBiometric();
            }
        });

        etPin.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (etPin.getText().toString().isEmpty()) {
                    identificationPresenterInterface.onEmptyEditText(etPin);
                } else {
                    identificationPresenterInterface.onTypingEditText(etPin);
                }
                errorPin.setText("");
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        etConfirmPin.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (etConfirmPin.getText().toString().isEmpty()) {
                    identificationPresenterInterface.onEmptyEditText(etConfirmPin);
                } else {
                    identificationPresenterInterface.onTypingEditText(etConfirmPin);
                }
                errorPin.setText("");
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

    }

    @Override
    public void ShowChooseAuthDialog() {

    }

    @Override
    public void showProgress() {
        showProgressBar.showLoadingDialog();
    }

    @Override
    public void hideProgress() {
        showProgressBar.dismissLoadingDialog();
    }

    @Override
    public void showCongratulation() {
        updateCongratulationLayout();
    }

    @Override
    public void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }


    private void drawText() {

        // Wait for the TextView to be laid out to measure its height
        contentStepper.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        // Remove the listener to avoid re-triggering
                        contentStepper.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                        // Check if the TextView content height exceeds 3/4 of screen height
                        if (contentStepper.getLayout().getHeight() > contentStepper.getHeight()) {
                            contentStepper.setMovementMethod(new ScrollingMovementMethod());
                        }
                    }
                });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        identificationPresenterInterface.timerFinished();
    }


    private void doSomething() {
        otpDashedView.setOtp("123456");
        if (otpDashedView.getOtp().equals("123456")) {
            stepContainer.findViewById(R.id.otpLayout).setBackground(getResources().getDrawable(R.drawable.border_edittext));
            counterTxt.setVisibility(View.GONE);
//new Handler().postDelayed(identificationPresenterInterface.timerFinished(), 2000); // 2000 ms = 2 seconds


        } else {
            stepContainer.findViewById(R.id.otpLayout).setBackground(getResources().getDrawable(R.drawable.border_edittext_error));
            otpDashedView.textPaint.setColor(Color.RED);
            counterTxt.setVisibility(View.GONE);
            stepContainer.findViewById(R.id.resendOTP).setVisibility(View.VISIBLE);
            errorOTPMess.setVisibility(View.VISIBLE);

        }
        identificationPresenterInterface.timerFinished();


    }

    @Override
    public void checkTerms(boolean isChecked) {
        if (isChecked) {
            nextButton.setTextColor(getResources().getColor(R.color.white));
        } else {
            nextButton.setTextColor(getResources().getColor(R.color.textGray));
        }
        acceptTermsAndConditions.setChecked(isChecked);
    }

    @Override
    public void onTypingEditText(EditText editText) {
        editText.setBackground(getResources().getDrawable(R.drawable.border_edittext));
        editText.setTextColor(getResources().getColor(R.color.textGray));
    }

    @Override
    public void onErrorEditText(EditText editText) {
        editText.setBackground(getResources().getDrawable(R.drawable.border_edittext_error));
        editText.setTextColor(getResources().getColor(R.color.red));
    }

    @Override
    public void onEmptyEditText(EditText editText) {
        editText.setBackground(getResources().getDrawable(R.drawable.border_grey));
        editText.setTextColor(getResources().getColor(R.color.textGray));
    }

    @Override
    public void showError(TextView textView, String error) {
        textView.setText(error);
    }

    @Override
    public void onErrorCountryPicker(CountryCodePicker ccp) {
        ccp.setBackground(getResources().getDrawable(R.drawable.border_edittext_error));
    }

    @Override
    public void onErrorOTP() {

    }

    @Override
    public void onEmptyOTP() {

    }

    @Override
    public void onSetOtp() {
        doSomething();
    }

    @Override
    public void onTimerFinish() {
        stepContainer.findViewById(R.id.resendOTP).setVisibility(View.VISIBLE);
        counterTxt.setVisibility(View.GONE);
    }


    public void errorBiometric() {
        biometricText.setText(getResources().getString(R.string.biometricAuthFailed));
        biometricImg.setBackgroundTintList(getResources().getColorStateList(R.color.red));
    }

    public void successBiometric() {
        biometricText.setText(getResources().getString(R.string.biometricAuthSuccess));
        biometricImg.setBackgroundTintList(getResources().getColorStateList(R.color.appColor));
        identificationPresenterInterface.setBiometric();
    }

}
