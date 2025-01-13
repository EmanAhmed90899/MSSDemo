package com.hemaya.mssdemo.utils.useCase;


import static android.app.Activity.RESULT_OK;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;
import android.widget.TextView;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.gson.Gson;
import com.hemaya.mssdemo.R;
import com.hemaya.mssdemo.model.ActivationModel;
import com.hemaya.mssdemo.model.activationData.Request.ActivationDataRequest;
import com.hemaya.mssdemo.model.activationData.Response.ActivationDataResponse;
import com.hemaya.mssdemo.model.ephemeral.request.DSAPPSRPEphemeralRequest;
import com.hemaya.mssdemo.model.ephemeral.response.DSAPPSRPEphemeralResponse;
import com.hemaya.mssdemo.model.identification.storeUserRequest.StoreUserRequest;
import com.hemaya.mssdemo.model.identification.storeUserResponse.StoreUserResponse;
import com.hemaya.mssdemo.model.mdlActivate.Request.MdlActivateRequest;
import com.hemaya.mssdemo.model.mdlActivate.Response.MdlActivateResponse;
import com.hemaya.mssdemo.model.mdlAddDevice.Request.MdlAddDeviceRequest;
import com.hemaya.mssdemo.model.mdlAddDevice.Response.MdlAddDeviceResponse;
import com.hemaya.mssdemo.model.synchronizeModel.SynchronizeResponse;
import com.hemaya.mssdemo.network.ApiClient;
import com.hemaya.mssdemo.network.ApiService;
import com.hemaya.mssdemo.network.domain.AuthData;
import com.hemaya.mssdemo.network.interceptor.DynamicHeaderInterceptor;
import com.hemaya.mssdemo.presenter.synchronize.SynchronizePresenter;
import com.hemaya.mssdemo.utils.storage.GenerateRandomToken;
import com.hemaya.mssdemo.utils.storage.GetDevicePlatform;
import com.hemaya.mssdemo.utils.storage.SVFFileReader;
import com.hemaya.mssdemo.utils.storage.SaveInLocalStorage;
import com.hemaya.mssdemo.utils.storage.SharedPreferenceStorage;
import com.hemaya.mssdemo.utils.storage.UserDatabaseHelper;
import com.hemaya.mssdemo.utils.views.GenericPopUp;
import com.hemaya.mssdemo.utils.views.RequestPermissionDialog;
import com.hemaya.mssdemo.utils.views.SettingPermissionDialog;
import com.hemaya.mssdemo.view.home.HomeView;
import com.vasco.digipass.sdk.DigipassSDK;
import com.vasco.digipass.sdk.DigipassSDKReturnCodes;
import com.vasco.digipass.sdk.responses.ActivationResponse;
import com.vasco.digipass.sdk.responses.GenerationResponse;
import com.vasco.digipass.sdk.responses.SecureChannelParseResponse;
import com.vasco.digipass.sdk.utils.qrcodescanner.QRCodeScannerSDKActivity;
import com.vasco.digipass.sdk.utils.qrcodescanner.QRCodeScannerSDKConstants;
import com.vasco.dsapp.client.DSAPPClient;
import com.vasco.dsapp.client.exceptions.DSAPPException;
import com.vasco.message.client.CredentialsData;
import com.vasco.message.client.SecureMessagingSDKClient;
import com.vasco.message.exception.SecureMessagingSDKException;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.TimeZone;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;


public class ActivationUseCase {

    private Context context;
    private RequestPermissionDialog requestPermissionDialog;

    ActivationModel activationModel;


    private ActivityResultLauncher<Intent> activityResultLauncher;

    SharedPreferenceStorage sharedPreferenceStorage;
    SaveInLocalStorage saveInLocalStorage;

    SetResult setResult;
    UserDatabaseHelper userDatabaseHelper;

    String instanceActivationMessage;
    Gson gson;

    @RequiresApi(api = Build.VERSION_CODES.O)
    public ActivationUseCase(Context context) {
        this.context = context;
        gson = new Gson();

        init();
    }

    public void setSetResult(SetResult setResult) {
        this.setResult = setResult;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void init() {
        requestPermissionDialog = new RequestPermissionDialog(context);
        requestPermissionDialog.init();

//        if (activationModel == null) {
        activationModel = ActivationModel.getInstance();
//        }

        sharedPreferenceStorage = new SharedPreferenceStorage(context);


        getSecureStorageName();
        saveInLocalStorage = new SaveInLocalStorage(context, sharedPreferenceStorage.getStorageName());
        userDatabaseHelper = new UserDatabaseHelper(context);
        activationModel.setPlatformFingerprint(new GetDevicePlatform(context).getFingerPrint());


        activityResultLauncher = ((AppCompatActivity) context).registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<androidx.activity.result.ActivityResult>() {
                    @Override
                    public void onActivityResult(androidx.activity.result.ActivityResult result) {
                        if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                            int scannedImageFormat =
                                    result.getData().getIntExtra(QRCodeScannerSDKConstants.OUTPUT_CODE_TYPE, 0);
                            processQRCodeData(scannedImageFormat, result.getData());
                        }
                    }


                }
        );
        resetToken(null);
//

    }

    public void setActivationModel(String name, String mobile, String national) {
        activationModel.setName(name);
        activationModel.setPhoneNumber(mobile);
        activationModel.setNationalId(national);
    }

    public void redirectToSettings() {
        new SettingPermissionDialog(context).show();
    }



    public void getSecureStorageName() {
//        if (sharedPreferenceStorage.getStorageName() == null) {
            activationModel.setStorageName("SecureStorage" + new GenerateRandomToken().generateToken(5));
            sharedPreferenceStorage.setStorageName(activationModel.getStorageName());
//        } else {
//            activationModel.setStorageName(sharedPreferenceStorage.getStorageName());
//        }
    }

    // Method to request permission
    public void takePermission() {
        if (checkCameraPermission() || checkPhoneStatePermission() || checkReadSmsPermission() || checkReceiveSmsPermission()) {
            requestPermissionDialog.create();
        }
    }

    public boolean checkAllPermission() {
        return checkCameraPermission() || checkPhoneStatePermission() || checkReadSmsPermission() || checkReceiveSmsPermission();
    }

    private boolean checkCameraPermission() {
        return ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED;

    }

    private boolean checkPhoneStatePermission() {
        return ContextCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED;

    }

    private boolean checkReadSmsPermission() {
        return ContextCompat.checkSelfPermission(context, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED;

    }

    private boolean checkReceiveSmsPermission() {
        return ContextCompat.checkSelfPermission(context, Manifest.permission.RECEIVE_SMS) != PackageManager.PERMISSION_GRANTED;

    }


    // Method to scan cronto code to get activation data
    public void scanVerificationData() {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            takePermission();
        } else {
            Intent intent = new Intent(context, QRCodeScannerSDKActivity.class);
            intent.putExtra(QRCodeScannerSDKConstants.EXTRA_VIBRATE, true);
            intent.putExtra(QRCodeScannerSDKConstants.EXTRA_CODE_TYPE, QRCodeScannerSDKConstants.QR_CODE + QRCodeScannerSDKConstants.CRONTO_CODE);
            intent.putExtra(QRCodeScannerSDKConstants.EXTRA_SCANNER_OVERLAY, true);
            activityResultLauncher.launch(intent);
        }

    }


    public void processQRCodeData(int scannedImageFormat, Intent data) {
        activationModel.setScannedImageData(data.getStringExtra(QRCodeScannerSDKConstants.OUTPUT_RESULT));
        activationModel.setCodeFormated(scannedImageFormat == QRCodeScannerSDKConstants.CRONTO_CODE
                ? "Cronto Sign"
                : "QR Code");
        parseCredentials();

    }

    public void parseCredentials() {
        if (activationModel.getScannedImageData().isEmpty()) {
            setResult.showToast(context.getResources().getString(R.string.no_data_decrypt));
        } else {
            setResult.showLayoutLoading();
            try {
                activationModel.setCredentialsData(SecureMessagingSDKClient.parseCredentialsMessage(activationModel.getScannedImageData()));
//                if (!activationModel.getName().equals(activationModel.getCredentialsData().getUserIdentifier())) {

//                    new GenericPopUp(context, context.getResources().getString(R.string.errorName)).showCustomPopup();

//                }
//            else {
                    ValidateSRPUserPasswordChecksum(activationModel.getCredentialsData().getActivationPassword(), null);
//                }
            } catch (SecureMessagingSDKException e) {
                setResult.showToast(e.getMessage());
                setResult.hideProgress();

            }
        }

    }

    public void manualActivation(String activationCode, String password, TextView messageErrorTxt) {
        setResult.showProgress();
        activationModel.setCredentialsData(new CredentialsData(0, activationCode, "", password, ""));
        ValidateSRPUserPasswordChecksum(activationModel.getCredentialsData().getActivationPassword(), messageErrorTxt);

    }

    private void ValidateSRPUserPasswordChecksum(String password, TextView messageErrorTxt) {
        try {
            DSAPPClient.validateSRPUserPasswordChecksum(password);
            GenerateSRPClientEphemeralKey();
        } catch (DSAPPException e) {
            setResult.hideProgress();
            if (messageErrorTxt != null)
                new GenericPopUp(context, context.getResources().getString(R.string.errorUser)).showCustomPopup();
        }
    }

    private void GenerateSRPClientEphemeralKey() {

        try {
            activationModel.setSrpClientEphemeralKeyResponse(DSAPPClient.generateSRPClientEphemeralKey());
            generateEphemeralCall();

        } catch (DSAPPException e) {
            setResult.hideProgress();
            setResult.showToast(e.getMessage());
        }
    }

    private void generateEphemeralCall() {
        Log.e("** RegistrationIdentifier", activationModel.getCredentialsData().getRegistrationIdentifier());
        Log.e("** ClientEphemeralPublicKey", activationModel.getSrpClientEphemeralKeyResponse().getClientEphemeralPublicKey());
        DSAPPSRPEphemeralRequest dsappsrpEphemeralRequest = new DSAPPSRPEphemeralRequest(activationModel.getCredentialsData().getRegistrationIdentifier(), activationModel.getSrpClientEphemeralKeyResponse().getClientEphemeralPublicKey());
        String jsonString = gson.toJson(dsappsrpEphemeralRequest);

        Call<DSAPPSRPEphemeralResponse> dsappsrpEphemeralResponseCall = getRetrofit(AuthData.generateHmac(jsonString)).generateEphemeralKey(dsappsrpEphemeralRequest);
        dsappsrpEphemeralResponseCall.enqueue(new Callback<DSAPPSRPEphemeralResponse>() {
            @Override
            public void onResponse(Call<DSAPPSRPEphemeralResponse> call, Response<DSAPPSRPEphemeralResponse> response) {
                Log.e("** Response", response.body().toString());
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        activationModel.setDsappsrpEphemeralResponse(response.body());
                        try {
                            if (activationModel.getDsappsrpEphemeralResponse().getResult() == null) {
                                setResult.hideProgress();
                                new GenericPopUp(context, context.getString(R.string.invalidActivtionData)).showCustomPopup();
                            } else {
                                activationModel.setSrpSessionKeyResponse(DSAPPClient.generateSRPSessionKey(activationModel.getSrpClientEphemeralKeyResponse().getClientEphemeralPublicKey(), activationModel.getSrpClientEphemeralKeyResponse().getClientEphemeralPrivateKey(), activationModel.getDsappsrpEphemeralResponse().getResult().getServerEphemeralPublicKey(), activationModel.getCredentialsData().getRegistrationIdentifier(), activationModel.getCredentialsData().getActivationPassword(), activationModel.getDsappsrpEphemeralResponse().getResult().getSalt()));
                                GenerateActivationDate();
                            }
                        } catch (DSAPPException e) {
                            new GenericPopUp(context, context.getResources().getString(R.string.something_went_wrong)).showCustomPopup();
                            setResult.hideProgress();

                        }
                    } else {
                        new GenericPopUp(context,  context.getString(R.string.something_went_wrong)).showCustomPopup();
                        setResult.hideProgress();
                    }
                }
            }

            @Override
            public void onFailure(Call<DSAPPSRPEphemeralResponse> call, Throwable t) {

                new GenericPopUp(context, context.getString(R.string.invalidActivtionData)).showCustomPopup();
                setResult.hideProgress();

            }
        });
    }

    private void GenerateActivationDate() {
        ActivationDataRequest activationDataRequest = new ActivationDataRequest(activationModel.getCredentialsData().getRegistrationIdentifier(), activationModel.getSrpSessionKeyResponse().getClientEvidenceMessage());
        String jsonString = gson.toJson(activationDataRequest);

        Call<ActivationDataResponse> activationDataResponseCall = getRetrofit(AuthData.generateHmac(jsonString)).generateActivationData(activationDataRequest);
        activationDataResponseCall.enqueue(new Callback<ActivationDataResponse>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onResponse(Call<ActivationDataResponse> call, Response<ActivationDataResponse> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        activationModel.setActivationDataResponse(response.body());
                        if (activationModel.getActivationDataResponse().getResult() == null) {
                            new GenericPopUp(context, context.getResources().getString(R.string.invalidActivtionData)).showCustomPopup();
                            setResult.hideProgress();
                        } else {
                            try {
                                activationModel.setEncryptionKey(DSAPPClient.decryptSRPData(activationModel.getSrpSessionKeyResponse().getSessionKey(), activationModel.getActivationDataResponse().getResult().getEncryptedLicenseActivationMessage(), activationModel.getActivationDataResponse().getResult().getEncryptedCounter(), activationModel.getActivationDataResponse().getResult().getMac()));
                                MultiDeviceActivateLicense();
                            } catch (DSAPPException e) {
                                setResult.showToast(e.getMessage());
                                setResult.hideProgress();
                                new GenericPopUp(context, e.getMessage()).showCustomPopup();
                            }
                        }

                    } else {
                        new GenericPopUp(context,  context.getString(R.string.something_went_wrong)).showCustomPopup();
                        setResult.hideProgress();

                    }
                }
            }

            @Override
            public void onFailure(Call<ActivationDataResponse> call, Throwable t) {
                new GenericPopUp(context,  context.getString(R.string.something_went_wrong)).showCustomPopup();
                setResult.hideProgress();

            }
        });
    }

    private SecureChannelParseResponse getSecureChannelParseResponse(String message) {
        return DigipassSDK.parseSecureChannelMessage(message);
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    private String MultiDeviceActivateLicense() {
        SecureChannelParseResponse secureChannelParseResponse = getSecureChannelParseResponse(new String(activationModel.getEncryptionKey(), StandardCharsets.UTF_8));
        String message = "";
        if (secureChannelParseResponse.getReturnCode() != DigipassSDKReturnCodes.SUCCESS) {
            message += "Parse secure channel message FAILED - [ " + secureChannelParseResponse.getReturnCode() + ": " + DigipassSDK.getMessageForReturnCode(secureChannelParseResponse.getReturnCode()) + " ]\n";
            new GenericPopUp(context, message).showCustomPopup();
            return message;
        }

        try {
            activationModel.setMultiDeviceLicenseActivationResponse(DigipassSDK.multiDeviceActivateLicense(secureChannelParseResponse.getMessage(), readStaticVector(), activationModel.getPlatformFingerprint(), activationModel.getJailbreakStatus(), activationModel.getClientServerTimeShift()));
        } catch (Exception e) {
            new GenericPopUp(context, e.getMessage()).showCustomPopup();
        }

        if (activationModel.getMultiDeviceLicenseActivationResponse().getReturnCode() != DigipassSDKReturnCodes.SUCCESS) {
            message += "Multi-device license activation FAILED - [ " + activationModel.getMultiDeviceLicenseActivationResponse().getReturnCode() + ": " + DigipassSDK.getMessageForReturnCode(activationModel.getMultiDeviceLicenseActivationResponse().getReturnCode()) + " ]\n";
            new GenericPopUp(context, message).showCustomPopup();

            return message;
        }

        saveInLocalStorage.saveByteData("dynamicVector", activationModel.getMultiDeviceLicenseActivationResponse().getDynamicVector());
        saveInLocalStorage.saveByteData("staticVector", activationModel.getMultiDeviceLicenseActivationResponse().getStaticVector());
        addMDLDevice();
        return message;

    }

    private void addMDLDevice() {
        MdlAddDeviceRequest mdlAddDeviceRequest = new MdlAddDeviceRequest(activationModel.getCredentialsData().getRegistrationIdentifier(), "description", activationModel.getMultiDeviceLicenseActivationResponse().getDeviceCode());
        String jsonString = gson.toJson(mdlAddDeviceRequest);

        Call<MdlAddDeviceResponse> mdlAddDeviceResponseCall = getRetrofit(AuthData.generateHmac(jsonString)).addDeviceMDL(mdlAddDeviceRequest);
        mdlAddDeviceResponseCall.enqueue(new Callback<MdlAddDeviceResponse>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onResponse(Call<MdlAddDeviceResponse> call, Response<MdlAddDeviceResponse> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        if (response.body().getResult() == null) {
                            new GenericPopUp(context,  context.getString(R.string.invalidActivtionData)).showCustomPopup();
                            setResult.hideProgress();

                        } else {

//                            if (sharedPreferenceStorage.getUserId() != null) {
//                                enableBiometric();
//                            } else {
                            activationModel.setInstanceActivationMessage(response.body().getResult().getInstanceActivationMessage());
                            instanceActivationMessage = (response.body().getResult().getInstanceActivationMessage());
                            setResult.hideProgress();
                            setResult.showCongratulation();
//                            }

                        }
                    } else {
                        new GenericPopUp(context, context.getString(R.string.something_went_wrong)).showCustomPopup();
                        setResult.hideProgress();

                    }
                } else {

                    new GenericPopUp(context, context.getString(R.string.something_went_wrong)).showCustomPopup();
                    setResult.hideProgress();

                }
            }

            @Override
            public void onFailure(Call<MdlAddDeviceResponse> call, Throwable t) {
                new GenericPopUp(context,  context.getString(R.string.something_went_wrong)).showCustomPopup();
                setResult.hideProgress();

            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public String MultiDeviceActivationInstance(String password) {
//        activationModel.setPassword(password);
        setResult.showProgress();

        SecureChannelParseResponse secureChannelParseResponse = getSecureChannelParseResponse(instanceActivationMessage);
        String message = "";
        if (secureChannelParseResponse.getReturnCode() != DigipassSDKReturnCodes.SUCCESS) {
            message += "Parse secure channel message FAILED - [ " + secureChannelParseResponse.getReturnCode() + ": " + DigipassSDK.getMessageForReturnCode(
                    secureChannelParseResponse.getReturnCode()
            ) + " ]\n";
            new GenericPopUp(context, message).showCustomPopup();
            setResult.hideProgress();


            return message;
        } else {

            ActivationResponse activationResponse = DigipassSDK.multiDeviceActivateInstance(saveInLocalStorage.getByteData("staticVector"), saveInLocalStorage.getByteData("dynamicVector"), secureChannelParseResponse.getMessage(), password, activationModel.getPlatformFingerprint());

            if (activationResponse.getReturnCode() != DigipassSDKReturnCodes.SUCCESS) {
                message += "Multi-device instance activation FAILED - [ " + activationResponse.getReturnCode() + ": " + DigipassSDK.getMessageForReturnCode(activationResponse.getReturnCode()) + " ]\n";
                new GenericPopUp(context, message).showCustomPopup();
                setResult.hideProgress();


                return message;
            } else {
                byte[] dynamicVectorResponse = activationResponse.getDynamicVector();
                saveInLocalStorage.saveByteData("dynamicVector", dynamicVectorResponse);
                generateSignatureFromSecureChannelMessage(secureChannelParseResponse, password);

            }
        }


        return message;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public String MultiDeviceActivationInstanceWithKey() {
        setResult.showProgress();
        SecureChannelParseResponse secureChannelParseResponse = getSecureChannelParseResponse(instanceActivationMessage);
        String message = "";
        if (secureChannelParseResponse.getReturnCode() != DigipassSDKReturnCodes.SUCCESS) {
            message += "Parse secure channel message FAILED - [ " + secureChannelParseResponse.getReturnCode() + ": " + DigipassSDK.getMessageForReturnCode(
                    secureChannelParseResponse.getReturnCode()
            ) + " ]\n";
            new GenericPopUp(context, message).showCustomPopup();
            setResult.hideProgress();
            return message;
        }
        ActivationResponse activationResponse = DigipassSDK.multiDeviceActivateInstanceWithKey(saveInLocalStorage.getByteData("staticVector"), saveInLocalStorage.getByteData("dynamicVector"), secureChannelParseResponse.getMessage(), saveInLocalStorage.getByteData("biometricKey"), activationModel.getPlatformFingerprint());

        if (activationResponse.getReturnCode() != DigipassSDKReturnCodes.SUCCESS) {
            message += "Multi-device instance activation FAILED - [ " + activationResponse.getReturnCode() + ": " + DigipassSDK.getMessageForReturnCode(activationResponse.getReturnCode()) + " ]\n";
            new GenericPopUp(context, message).showCustomPopup();
            setResult.hideProgress();

            return message;

        }
        byte[] dynamicVectorResponse = activationResponse.getDynamicVector();
        saveInLocalStorage.saveByteData("dynamicVector", dynamicVectorResponse);
        generateSignatureFromSecureChannelMessageWithKey(secureChannelParseResponse);
        return message;
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    public String generateSignatureFromSecureChannelMessage(SecureChannelParseResponse secureChannelParseResponse, String password) {

        String message = "";
        GenerationResponse generationResponse = DigipassSDK.generateSignatureFromSecureChannelMessage(saveInLocalStorage.getByteData("staticVector"), saveInLocalStorage.getByteData("dynamicVector"), secureChannelParseResponse.getMessage(), password, activationModel.getClientServerTimeShift(), activationModel.getCryptoApplicationIndex(), activationModel.getPlatformFingerprint());

        if (generationResponse.getReturnCode() != DigipassSDKReturnCodes.SUCCESS) {
            message += "Generate signature from secure channel message FAILED - [ " + generationResponse.getReturnCode() + ": " + DigipassSDK.getMessageForReturnCode(generationResponse.getReturnCode()) + " ]";
            new GenericPopUp(context, message).showCustomPopup();
            setResult.hideProgress();

            return message;
        }
        activationModel.setSignature(generationResponse.getResponse());

        Log.e("** Signature", "created");
//            if(activationModel.getBiometricKey() != null){
//                MultiDeviceActivationInstanceWithKey();
//            }
        MdlActivate(false);
        return message;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public String generateSignatureFromSecureChannelMessageWithKey(SecureChannelParseResponse secureChannelParseResponse) {
        String message = "";
        GenerationResponse generationResponse = DigipassSDK.generateSignatureFromSecureChannelMessageWithKey(saveInLocalStorage.getByteData("staticVector"), saveInLocalStorage.getByteData("dynamicVector"), secureChannelParseResponse.getMessage(), saveInLocalStorage.getByteData("biometricKey"), activationModel.getClientServerTimeShift(), activationModel.getCryptoApplicationIndex(), activationModel.getPlatformFingerprint());

        if (generationResponse.getReturnCode() != DigipassSDKReturnCodes.SUCCESS) {
            message += "Generate signature from secure channel message FAILED - [ " + generationResponse.getReturnCode() + ": " + DigipassSDK.getMessageForReturnCode(generationResponse.getReturnCode()) + " ]";
            new GenericPopUp(context, message).showCustomPopup();
            setResult.hideProgress();


            return message;

        }
        activationModel.setSignature(generationResponse.getResponse());
        MdlActivate(true);
        return message;

    }

    private void MdlActivate(boolean isBiometric) {
        MdlActivateRequest mdlActivateRequest = new MdlActivateRequest(activationModel.getCredentialsData().getRegistrationIdentifier(), activationModel.getSignature());
        String jsonString = gson.toJson(mdlActivateRequest);

        Call<MdlActivateResponse> mdlActivateResponseCall = getRetrofit(AuthData.generateHmac(jsonString)).activateMDL(mdlActivateRequest);
        mdlActivateResponseCall.enqueue(new Callback<MdlActivateResponse>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onResponse(Call<MdlActivateResponse> call, Response<MdlActivateResponse> response) {
                setResult.hideProgress();
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        Log.e("** Response", response.body().toString());
                        Log.e("** actvation", activationModel.getName());
//                        activationModel.setUserName("test2_user");
                        if (activationModel.getName().equals(response.body().getResult().getUserID())) {
                            activationModel.setSerialNumber(response.body().getResult().getSerialNumber());
                            long id = userDatabaseHelper.addUser(activationModel.getPlatformFingerprint(), response.body().getResult().getSerialNumber(), response.body().getResult().getSerialNumber(), true, sharedPreferenceStorage.getStorageName());
                            storeUser();
                            sharedPreferenceStorage.setUserName("");
                            sharedPreferenceStorage.setUserId(id + "");
                            userDatabaseHelper.updateIsUsed(id + "", true);
                            Intent intent = new Intent(context, HomeView.class);
                            context.startActivity(intent);
                        } else {
                            sharedPreferenceStorage.setUserName("");
                            new GenericPopUp(context, context.getResources().getString(R.string.errorName)).showCustomPopup();
                        }

//                        }
//                        if (isBiometric && !sharedPreferenceStorage.getInitiateDetection()) {
//                            try {
//                                BiometricSensorSDK.initiateBiometryChangeDetection(context);
//                                sharedPreferenceStorage.setInitiateDetection(true);
//
//                            } catch (BiometricSensorSDKException e) {
//                                throw new RuntimeException(e);
//                            }
//                        }


                    }
                }else{
                    new GenericPopUp(context,  context.getString(R.string.something_went_wrong)).showCustomPopup();
                }
            }

            @Override
            public void onFailure(Call<MdlActivateResponse> call, Throwable t) {
                setResult.hideProgress();
                new GenericPopUp(context,  context.getString(R.string.something_went_wrong)).showCustomPopup();
            }
        });
    }


    private String readStaticVector() {
        SVFFileReader svfFileReader = new SVFFileReader();
        return svfFileReader.readSVFFileAsString(context, "export.svf");
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void enableBiometric() {
        activationModel.setBiometricKey(getBiometricKey(16));
        saveInLocalStorage.saveStringData("biometricKey", activationModel.getBiometricKey().toString());
        MultiDeviceActivationInstanceWithKey();
    }

    public byte[] getBiometricKey(int size) {
        SecureRandom random = new SecureRandom();
        byte[] byteArray = new byte[size];
        random.nextBytes(byteArray);
        return byteArray;
    }

    public void resetToken(SynchronizePresenter viewInterface) {
        Call<SynchronizeResponse> callSynchronize = getRetrofit(AuthData.generateHmac("[]")).synchronize();
        callSynchronize.enqueue(new Callback<SynchronizeResponse>() {
            @Override
            public void onResponse(Call<SynchronizeResponse> call, Response<SynchronizeResponse> response) {
                if (response.isSuccessful()) {
                    SynchronizeResponse synchronizeResponse = response.body();
                    if (synchronizeResponse != null) {
                        if (synchronizeResponse.getResultCodes().getStatusCode() == 0) {
                            Log.e("** serverTimeShift", synchronizeResponse.getResult().getServerTime() + "");
                            long currentTimeMillis = System.currentTimeMillis();

                            convertEpochToDate(viewInterface, currentTimeMillis, synchronizeResponse.getResult().getServerTime());


                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<SynchronizeResponse> call, Throwable t) {
                if (viewInterface != null) {
                    viewInterface.onError(t.getMessage());
                }
            }
        });
    }

    ApiService getRetrofit(String header) {
        DynamicHeaderInterceptor headerInterceptor = new DynamicHeaderInterceptor(header, context);
        Retrofit retrofit = ApiClient.getClient(header, context);
        ApiService apiService = retrofit.create(ApiService.class);
        return apiService;
    }

    public void convertEpochToDate(SynchronizePresenter viewInterface, long currentTimeMillis, long epochSeconds) {
        // Convert seconds to milliseconds
        long timestampMillis = epochSeconds * 1000;

        // Convert to Date object
        Date date = new Date(timestampMillis);

        // Format the Date
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC")); // Set timezone if needed
        String formattedDate = sdf.format(date);

        // Display the formatted date
        System.out.println("Formatted Date: " + currentTimeMillis);
        System.out.println("Formatted Date: " + timestampMillis);


        // Calculate the time shift (difference)
        long timeShiftMillis = currentTimeMillis - timestampMillis;
        sharedPreferenceStorage.setTimeShift(timeShiftMillis);
        if (viewInterface != null) {
            viewInterface.onSuccess(timeShiftMillis + "");
        }
    }

    private void storeUser() {
        StoreUserRequest storeUserRequest = new StoreUserRequest(activationModel.getNationalId(), activationModel.getSerialNumber(), activationModel.getPhoneNumber(), activationModel.getName(), getUUid());
        String jsonString = gson.toJson(storeUserRequest);
        String signature = AuthData.generateHmac(jsonString);
        Call<StoreUserResponse> storeUserResponseCall = getRetrofit(signature).storeUser(storeUserRequest);
        storeUserResponseCall.enqueue(new Callback<StoreUserResponse>() {
            @Override
            public void onResponse(Call<StoreUserResponse> call, Response<StoreUserResponse> response) {
                Log.e("** ResponseStore", response.toString());
            }

            @Override
            public void onFailure(Call<StoreUserResponse> call, Throwable t) {
                Log.e("** errorStore", t.getMessage());
                //                new GenericPopUp(context, t.getMessage()).showCustomPopup();
            }
        });


    }

    private String getUUid() {
        String androidId = Settings.Secure.getString(
                context.getContentResolver(),
                Settings.Secure.ANDROID_ID
        );
        return androidId;
    }


    public interface SetResult {
        void showToast(String message);

        void showProgress();

        void showLayoutLoading();

        void hideProgress();

        void showCongratulation();

    }


}


