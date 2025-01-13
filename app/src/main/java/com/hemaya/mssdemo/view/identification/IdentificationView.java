package com.hemaya.mssdemo.view.identification;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.ScrollingMovementMethod;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.RequiresApi;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.hbb20.CountryCodePicker;
import com.hemaya.mssdemo.R;
import com.hemaya.mssdemo.presenter.identification.IdentificationPresenter;
import com.hemaya.mssdemo.presenter.identification.IdentificationPresenterInterface;
import com.hemaya.mssdemo.utils.broadCast.OtpReceiver;
import com.hemaya.mssdemo.utils.storage.SharedPreferenceStorage;
import com.hemaya.mssdemo.utils.storage.UserDatabaseHelper;
import com.hemaya.mssdemo.utils.useCase.ActivationUseCase;
import com.hemaya.mssdemo.utils.views.GenericPopUp;
import com.hemaya.mssdemo.utils.views.ShowProgressBar;
import com.hemaya.mssdemo.view.BaseActivity;
import com.hemaya.mssdemo.view.home.HomeView;

public class IdentificationView extends BaseActivity implements CountTimer.TimerListener, IdentificationViewInterface, ActivationUseCase.SetResult {
    private ImageView backImg;
    private StepperLineView stepperLineView;
    public static final int REQUEST_CODE = 1;
    private FrameLayout stepContainer;
    RelativeLayout activateButton;
    private OTPDashedView otpDashedView;
    private LinearLayout mainLayout;
    private IdentificationPresenterInterface identificationPresenterInterface;
    private CheckBox acceptTermsAndConditions;
    private Step currentStep = Step.STEP_TWO;
    private EditText nationalIdEd, nationalIdConfEd;
    private TextView errorNational, changeLang;
//    String nationalId, mobileNumber, selectedCountryCode, OTP;
    String OTP;
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
    boolean checkTermsVal = false;
    boolean isAddNewUser = false;
    boolean isDeleteAll = false;
    private TextView titleStepper, contentStepper, counterTxt, errorOTPMess, resendOTP;
    private RelativeLayout nextButton;
    private boolean isManual = false;
    private OtpReceiver otpReceiver;
    private TextView nextTxt;
    private ProgressBar progressBar, progressBarActivate;
    private FrameLayout loading_view;

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
        loading_view = findViewById(R.id.loading_view);
        stepperLineView = findViewById(R.id.stepperLine);
        titleStepper = findViewById(R.id.titleStepper);
        contentStepper = findViewById(R.id.contentStepper);
        stepContainer = findViewById(R.id.stepContainer);
        nextButton = findViewById(R.id.nextBtn);
        nextTxt = findViewById(R.id.nextTxt);
        progressBar = findViewById(R.id.progressBarNext);
        mainLayout = findViewById(R.id.mainLayout);
        userDatabaseHelper = new UserDatabaseHelper(this);
        identificationPresenterInterface = new IdentificationPresenter(this);
        showProgressBar = new ShowProgressBar(this);
        isAddNewUser = getIntent().getBooleanExtra("isAddNewUser", false);
        isDeleteAll = getIntent().getBooleanExtra("isDeleteAll", false);

        assign();
    }

    @Override
    protected void onStop() {
        super.onStop();

        try {
            unregisterReceiver(otpReceiver);
        } catch (IllegalArgumentException e) {
            // Receiver was not registered
        }
    }


    @Override
    protected void onPause() {
        super.onPause();

    }


    private void assign() {
        stepperLineView.setTextViews(titleStepper, contentStepper);
        stepperLineView.setSteps(6);

//        stepperLineView.setCurrentStep(5);
//        identificationPresenterInterface.setStep(Step.STEP_FIVE);
//        updateLayout(Step.STEP_FIVE);
//        initFifthStep();

        if (isAddNewUser||isDeleteAll) {
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

        onClick();
    }

    private void onClick() {
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentStep != null) {
                    if (!identificationPresenterInterface.checkPermission()) {
                        switch (currentStep) {
                            case STEP_ONE:
                                boolean valCheck = identificationPresenterInterface.confirmTerms();
//                            if (valCheck) initSecondStep();
                                break;
                            case STEP_TWO:
                                boolean nationalCheck = identificationPresenterInterface.validateNationalId(nationalIdEd, nationalIdConfEd, errorNational);
//                            if (nationalCheck) initThirdStep();
                                break;
                            case STEP_THREE:
                                boolean mobileCheck = identificationPresenterInterface.validatePhone(countryCodePicker.getSelectedCountryNameCode(), countryCode + mobileNumberEd.getText().toString(), mobileNumberEd, countryCodePicker, errorMobile);
//                            if (mobileCheck) initFourthStep();
                                break;
                            case STEP_FOUR:
                                identificationPresenterInterface.successOTP(OTP);

//                            if (!otpDashedView.getOtp().isEmpty()) {
//                                identificationPresenterInterface.successOTP();
////                                initFifthStep();
//                            }
                                break;
                            case STEP_FIVE:

//                            initSixthStep();
                                break;
                            case STEP_SIX:
                                identificationPresenterInterface.pinValidation(etPin, etConfirmPin, errorPin);
                                break;
                        }
                    } else {
                        identificationPresenterInterface.showSettingDialog();
                    }

                }
            }
        });


        backImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isManual) {
                    isManual = false;
                    nextButton.setVisibility(View.VISIBLE);
                    titleStepper.setVisibility(View.VISIBLE);
                    contentStepper.setVisibility(View.VISIBLE);
                    updateLayout(Step.STEP_FIVE);
                } else {
                    identificationPresenterInterface.pressBack(isAddNewUser);
                }
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

        if (currentStep == Step.STEP_TWO) {
            initSecondStep();
        } else if (currentStep == Step.STEP_THREE) {
            initThirdStep();
        } else if (currentStep == Step.STEP_FOUR) {
            initFourthStep();
        } else if (currentStep == Step.STEP_FIVE) {
            initFifthStep();
        } else if (currentStep == Step.STEP_SIX) {
            initSixthStep();
        }

//        if (nationalId != null) {
//            Log.d("** TAG", "updateLayout: " + nationalId);
//            nationalIdEd.setText(nationalId);
//            nationalIdConfEd.setText(nationalId);
//        }
//
//        if (mobileNumber != null) {
//            mobileNumberEd.setText(mobileNumber);
//        }
//
//        if (selectedCountryCode != null) {
//            countryCodePicker.setCountryForNameCode(selectedCountryCode);
//        }
        if (OTP != null) {
            otpDashedView.setOtp(OTP);
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
        progressBarActivate = findViewById(R.id.progressBarActivate);
//        if (sharedPreferenceStorage.getLanguage().equals("ar")) {
//            activationId.setGravity(Gravity.END);
//            activationPassword.setGravity(Gravity.END);
//        }

        applyGravityToAllEditTexts(activationId);
        applyGravityToAllEditTexts(activationPassword);

//        activationId.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//                activationId.setBackground(getResources().getDrawable(R.drawable.border_edittext));
//                errorManualActivation.setText("");
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                activationId.setBackground(getResources().getDrawable(R.drawable.border_edittext));
//                errorManualActivation.setText("");
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//
//            }
//        });
//
//        activationPassword.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//                activationPassword.setBackground(getResources().getDrawable(R.drawable.border_edittext));
//                errorManualActivation.setText("");
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                activationPassword.setBackground(getResources().getDrawable(R.drawable.border_edittext));
//                errorManualActivation.setText("");
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//
//            }
//        });

        activateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!activationId.getText().toString().isEmpty() && !activationPassword.getText().toString().isEmpty()) {
                    progressBarActivate.setVisibility(View.VISIBLE);
                    activateButton.setClickable(false);
                    identificationPresenterInterface.manualActivation(activationId, activationPassword, errorManualActivation);
                }
            }
        });

    }

    @Override
    public void initFourthStep() {
        registerOtpListener();

        otpDashedView = stepContainer.findViewById(R.id.otpDashedView);
        counterTxt = stepContainer.findViewById(R.id.counterTxt);
        errorOTPMess = stepContainer.findViewById(R.id.errorOTPMess);
        resendOTP = stepContainer.findViewById(R.id.resendOTP);
        if (OTP == null) {
            identificationPresenterInterface.validateOtp(counterTxt);
        }

        resendOTP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OTP = null;
                otpDashedView.clearOtp();
                identificationPresenterInterface.resendOTP();
                resendOTP.setVisibility(View.GONE);
                counterTxt.setVisibility(View.VISIBLE);
                identificationPresenterInterface.validateOtp(counterTxt);
            }
        });

        nextButton.setVisibility(View.VISIBLE);

//        mobileNumber = mobileNumberEd.getText().toString();
//        selectedCountryCode = countryCodePicker.getSelectedCountryNameCode();
//
        doSomething();

    }

    @Override
    public void initSecondStep() {
        if (acceptTermsAndConditions != null) {
            checkTermsVal = acceptTermsAndConditions.isChecked();
        }
        if (changeLang == null) {
            changeLang = findViewById(R.id.changeLang);
        }
        changeLang.setVisibility(View.GONE);
        errorNational = stepContainer.findViewById(R.id.errorNationalId);
        nationalIdEd = stepContainer.findViewById(R.id.et_national_id);
        nationalIdConfEd = stepContainer.findViewById(R.id.et_national_id_confirm);


        applyGravityToAllEditTexts(nationalIdEd);
        applyGravityToAllEditTexts(nationalIdConfEd);

//        if (sharedPreferenceStorage.getLanguage().equals("ar")) {
//            nationalIdEd.setGravity(Gravity.END);
//            nationalIdConfEd.setGravity(Gravity.END);
//        }


//        nationalIdEd.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                Log.d("** TAG", "onTextChanged: " + count);
//                if (nationalIdEd.getText().toString().isEmpty()) {
//                    identificationPresenterInterface.onEmptyEditText(nationalIdEd);
//                } else {
//                    identificationPresenterInterface.onTypingEditText(nationalIdEd);
//                }
//                errorNational.setText("");
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//            }
//        });

//        nationalIdConfEd.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                if (nationalIdConfEd.getText().toString().isEmpty()) {
//                    identificationPresenterInterface.onEmptyEditText(nationalIdConfEd);
//                } else {
//                    identificationPresenterInterface.onTypingEditText(nationalIdConfEd);
//                }
//                errorNational.setText("");
//
//
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//            }
//        });

        nextTxt.setTextColor(getResources().getColor(R.color.white));

    }

    public void initFirstStep() {
        changeLang = findViewById(R.id.changeLang);
        changeLang.setVisibility(View.VISIBLE);
        nextButton.setVisibility(View.VISIBLE);
        changeLang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                identificationPresenterInterface.changeLang();
            }
        });
        drawText();
        if (checkTermsVal) {
            acceptTermsAndConditions.setChecked(true);
        }
        acceptTermsAndConditions.setOnCheckedChangeListener((buttonView, isChecked) -> {
            identificationPresenterInterface.checkTerms();
        });


    }

    @Override
    public void initThirdStep() {
        try {
            unregisterReceiver(otpReceiver);
        } catch (IllegalArgumentException e) {
            // Receiver was not registered
        }
//        nationalId = nationalIdEd.getText().toString();
//
//        Log.d("** TAG", "initThirdStep: " + nationalId);
        mobileNumberEd = stepContainer.findViewById(R.id.et_mobile_number);
        errorMobile = stepContainer.findViewById(R.id.errorMobile);
        countryCodePicker = stepContainer.findViewById(R.id.countryCodePicker);
        if (sharedPreferenceStorage.getLanguage().equals("ar")) {
            countryCodePicker.changeDefaultLanguage(CountryCodePicker.Language.ARABIC);
        } else {
            countryCodePicker.changeDefaultLanguage(CountryCodePicker.Language.ENGLISH);
        }
        countryCodePicker.setCountryForNameCode("EG");
        countryCode = countryCodePicker.getSelectedCountryCode();

        applyGravityToAllEditTexts(mobileNumberEd);

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
        try {
            unregisterReceiver(otpReceiver);
        } catch (IllegalArgumentException e) {
            // Receiver was not registered
        }
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
                isManual = true;
                updateManualActivationLayout();
            }
        });

    }

    @Override
    public void initSixthStep() {
        titleStepper.setVisibility(View.VISIBLE);
        contentStepper.setVisibility(View.VISIBLE);
        nextButton.setVisibility(View.VISIBLE);
        nextTxt.setText(getResources().getText(R.string.ok));

        etPin = stepContainer.findViewById(R.id.et_pin);
        etConfirmPin = stepContainer.findViewById(R.id.et_confirm_pin);
        errorPin = stepContainer.findViewById(R.id.errorPin);
        biometricText = stepContainer.findViewById(R.id.biometricText);
        biometricLayout = stepContainer.findViewById(R.id.biometricLayout);
        biometricImg = stepContainer.findViewById(R.id.biometricImg);

        applyGravityToAllEditTexts(etPin);
        applyGravityToAllEditTexts(etConfirmPin);


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
    public void restartActivity() {
        Intent intent = new Intent(this, IdentificationView.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void ShowChooseAuthDialog() {

    }

    @Override
    public void showProgress() {
        nextButton.setClickable(false);
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void showLayoutLoading() {
        loading_view.setVisibility(View.VISIBLE);
        loading_view.setOnTouchListener((v, event) -> true);

    }


    @Override
    public void hideProgress() {
        loading_view.setVisibility(View.GONE);
        loading_view.setOnTouchListener((v, event) -> false);

        nextButton.setClickable(true);
        if (progressBarActivate != null) {
            progressBarActivate.setVisibility(View.INVISIBLE);
            activateButton.setClickable(true);
        }

        progressBar.setVisibility(View.INVISIBLE);
    }

    @Override
    public void showCongratulation() {
        if (progressBarActivate != null) {
            progressBarActivate.setVisibility(View.INVISIBLE);
        } else {
            loading_view.setVisibility(View.GONE);
        }
        updateCongratulationLayout();
    }


    @Override
    public void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void finishActivity() {
        finishAffinity();

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
        Log.d("** TAG", "onDestroy: ");
        identificationPresenterInterface.timerFinished();
    }


    private void doSomething() {
        otpDashedView.setOtp("123456");
        if (otpDashedView.getOtp().equals("123456")) {
            OTP = otpDashedView.getOtp();
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
            nextTxt.setTextColor(getResources().getColor(R.color.white));
        } else {
            nextTxt.setTextColor(getResources().getColor(R.color.textGray));
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
    public void showError(String error) {
        new GenericPopUp(this, error).showCustomPopup();
//        textView.setText(error);
    }

    @Override
    public void goToHome() {
        Intent intent = new Intent(this, HomeView.class);
        startActivity(intent);
    }


    @Override
    public void onTimerFinish() {
        resendOTP = stepContainer.findViewById(R.id.resendOTP);
        resendOTP.setVisibility(View.VISIBLE);
        counterTxt.setVisibility(View.GONE);
    }

    @Override
    public void onRegeneratedEnabled() {

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

    private void registerOtpListener() {
        otpReceiver = new OtpReceiver();
        IntentFilter filter = new IntentFilter("android.provider.Telephony.SMS_RECEIVED");
        registerReceiver(otpReceiver, filter);
        Log.d("** TAG", "registerOtpListener: ");
        otpReceiver.setOtpListener(new OtpReceiver.OtpListener() {
            @Override
            public void onOtpReceived(String otp) {
                OTP = otp;
                otpDashedView.setOtp(otp);
                stepContainer.findViewById(R.id.otpLayout).setBackground(getResources().getDrawable(R.drawable.border_edittext));
            }
        });


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE) {
            for (int i = 0; i < permissions.length; i++) {
                if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                    identificationPresenterInterface.showSettingDialog();
                    break;
                }

            }
        }
    }

    private void applyGravityToAllEditTexts(EditText view) {
        if (sharedPreferenceStorage.getLanguage().equals("ar")) {
            view.setGravity(Gravity.RIGHT);
        } else {
            view.setGravity(Gravity.LEFT);
        }
    }

    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {
        if (isManual) {
            isManual = false;
            nextButton.setVisibility(View.VISIBLE);
            titleStepper.setVisibility(View.VISIBLE);
            contentStepper.setVisibility(View.VISIBLE);
            updateLayout(Step.STEP_FIVE);
        } else {
            identificationPresenterInterface.pressBack(isAddNewUser);
        }
    }

}
