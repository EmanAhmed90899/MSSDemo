package com.hemaya.mssdemo.utils.useCase;


import static android.app.Activity.RESULT_OK;

import static androidx.core.content.ContextCompat.startActivity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;
import android.widget.TextView;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.hemaya.mssdemo.R;
import com.hemaya.mssdemo.model.ActivationModel;
import com.hemaya.mssdemo.model.activationData.Request.ActivationDataRequest;
import com.hemaya.mssdemo.model.activationData.Response.ActivationDataResponse;
import com.hemaya.mssdemo.model.ephemeral.request.DSAPPSRPEphemeralRequest;
import com.hemaya.mssdemo.model.ephemeral.response.DSAPPSRPEphemeralResponse;
import com.hemaya.mssdemo.model.mdlActivate.Response.MdlActivateResponse;
import com.hemaya.mssdemo.model.mdlActivate.Request.MdlActivateRequest;
import com.hemaya.mssdemo.model.mdlAddDevice.Request.MdlAddDeviceRequest;
import com.hemaya.mssdemo.model.mdlAddDevice.Response.MdlAddDeviceResponse;
import com.hemaya.mssdemo.model.synchronizeModel.SynchronizeResponse;
import com.hemaya.mssdemo.network.ApiClient;
import com.hemaya.mssdemo.network.ApiService;
import com.hemaya.mssdemo.utils.storage.GenerateRandomToken;
import com.hemaya.mssdemo.utils.storage.GetDevicePlatform;
import com.hemaya.mssdemo.utils.storage.SVFFileReader;
import com.hemaya.mssdemo.utils.storage.SaveInLocalStorage;
import com.hemaya.mssdemo.utils.storage.SharedPreferenceStorage;
import com.hemaya.mssdemo.utils.storage.UserDatabaseHelper;
import com.hemaya.mssdemo.utils.views.GenericPopUp;
import com.hemaya.mssdemo.utils.views.RequestPermissionDialog;
import com.hemaya.mssdemo.view.home.HomeView;
import com.vasco.digipass.sdk.DigipassSDK;
import com.vasco.digipass.sdk.DigipassSDKReturnCodes;
import com.vasco.digipass.sdk.responses.ActivationResponse;
import com.vasco.digipass.sdk.responses.GenerationResponse;
import com.vasco.digipass.sdk.responses.SecureChannelParseResponse;
import com.vasco.digipass.sdk.utils.biometricsensor.BiometricSensorSDK;
import com.vasco.digipass.sdk.utils.biometricsensor.BiometricSensorSDKException;
import com.vasco.digipass.sdk.utils.qrcodescanner.QRCodeScannerSDKActivity;
import com.vasco.digipass.sdk.utils.qrcodescanner.QRCodeScannerSDKConstants;
import com.vasco.dsapp.client.DSAPPClient;
import com.vasco.dsapp.client.exceptions.DSAPPException;
import com.vasco.message.client.CredentialsData;
import com.vasco.message.client.SecureMessagingSDKClient;
import com.vasco.message.exception.SecureMessagingSDKException;


import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.TimeZone;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ActivationUseCase {

    private Context context;
    private RequestPermissionDialog requestPermissionDialog;

    ActivationModel activationModel;


    private ActivityResultLauncher<Intent> activityResultLauncher;
    ApiService apiService;

    SharedPreferenceStorage sharedPreferenceStorage;
    SaveInLocalStorage saveInLocalStorage;

    SetResult setResult;
    UserDatabaseHelper userDatabaseHelper;

    @RequiresApi(api = Build.VERSION_CODES.O)
    public ActivationUseCase(Context context) {
        this.context = context;
        this.setResult = (SetResult) context;

        init();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void init() {
        requestPermissionDialog = new RequestPermissionDialog(context);
        requestPermissionDialog.init();

        activationModel = ActivationModel.getInstance();

        sharedPreferenceStorage = new SharedPreferenceStorage(context);
        apiService = ApiClient.getClient().create(ApiService.class);

//        activationModel.setPlatformFingerprint(new GetDevicePlatform(context).getFingerPrint());

        getSecureStorageName();
        saveInLocalStorage = new SaveInLocalStorage(context, sharedPreferenceStorage.getStorageName(), "");
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
        if (sharedPreferenceStorage.getTimeShift() == -1) {
            resetToken();
        }

    }

    public void setUserName(String name) {
        activationModel.setName(name);
    }

    public void getSecureStorageName() {
        if (sharedPreferenceStorage.getStorageName() == null) {
            activationModel.setStorageName("SecureStorage" + new GenerateRandomToken().generateToken(5));
            sharedPreferenceStorage.setStorageName(activationModel.getStorageName());
        } else {
            activationModel.setStorageName(sharedPreferenceStorage.getStorageName());
        }
    }

    // Method to request permission
    public void takePermission() {
        if (checkCameraPermission() || checkPhoneStatePermission()) {
            requestPermissionDialog.create();
        }
    }

    private boolean checkCameraPermission() {
        return ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED;

    }

    private boolean checkPhoneStatePermission() {
        return ContextCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED;

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
            setResult.showProgress();
            try {
                activationModel.setCredentialsData(SecureMessagingSDKClient.parseCredentialsMessage(activationModel.getScannedImageData()));
                if (!activationModel.getName().equals(activationModel.getCredentialsData().getUserIdentifier())) {

                    new GenericPopUp(context, context.getString(R.string.error), context.getResources().getString(R.string.errorName)).showCustomPopup();

                } else {
                    ValidateSRPUserPasswordChecksum(activationModel.getCredentialsData().getActivationPassword(), null);
                }
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
        Log.e("** ActivationCode", activationCode);

    }

    private void ValidateSRPUserPasswordChecksum(String password, TextView messageErrorTxt) {
        try {
            DSAPPClient.validateSRPUserPasswordChecksum(password);
            GenerateSRPClientEphemeralKey();
        } catch (DSAPPException e) {
            setResult.hideProgress();
            if (messageErrorTxt != null)
                messageErrorTxt.setText(e.getMessage());
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
        Call<DSAPPSRPEphemeralResponse> dsappsrpEphemeralResponseCall = apiService.generateEphemeralKey(new DSAPPSRPEphemeralRequest(activationModel.getCredentialsData().getRegistrationIdentifier(), activationModel.getSrpClientEphemeralKeyResponse().getClientEphemeralPublicKey()));
        dsappsrpEphemeralResponseCall.enqueue(new Callback<DSAPPSRPEphemeralResponse>() {
            @Override
            public void onResponse(Call<DSAPPSRPEphemeralResponse> call, Response<DSAPPSRPEphemeralResponse> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        activationModel.setDsappsrpEphemeralResponse(response.body());
                        try {
                            if (activationModel.getDsappsrpEphemeralResponse().getResult() == null) {
                                setResult.hideProgress();
                                new GenericPopUp(context, activationModel.getDsappsrpEphemeralResponse().getResultCodes().getStatusCode() + "", activationModel.getDsappsrpEphemeralResponse().getResultCodes().getStatusCodeEnum()).showCustomPopup();
                            } else {
                                activationModel.setSrpSessionKeyResponse(DSAPPClient.generateSRPSessionKey(activationModel.getSrpClientEphemeralKeyResponse().getClientEphemeralPublicKey(), activationModel.getSrpClientEphemeralKeyResponse().getClientEphemeralPrivateKey(), activationModel.getDsappsrpEphemeralResponse().getResult().getServerEphemeralPublicKey(), activationModel.getCredentialsData().getRegistrationIdentifier(), activationModel.getCredentialsData().getActivationPassword(), activationModel.getDsappsrpEphemeralResponse().getResult().getSalt()));
                                GenerateActivationDate();
                            }
                        } catch (DSAPPException e) {
                            new GenericPopUp(context, context.getString(R.string.error), e.getMessage()).showCustomPopup();
                            setResult.hideProgress();

                        }
                    } else {
                        new GenericPopUp(context, "generateEphemeral " + context.getString(R.string.error), context.getString(R.string.something_went_wrong)).showCustomPopup();
                        setResult.hideProgress();
                    }
                }
            }

            @Override
            public void onFailure(Call<DSAPPSRPEphemeralResponse> call, Throwable t) {
                new GenericPopUp(context, context.getString(R.string.error), context.getString(R.string.invalidActivtionData)).showCustomPopup();
                setResult.hideProgress();

            }
        });
    }


    private void GenerateActivationDate() {
        Call<ActivationDataResponse> activationDataResponseCall = apiService.generateActivationData(new ActivationDataRequest(activationModel.getCredentialsData().getRegistrationIdentifier(), activationModel.getSrpSessionKeyResponse().getClientEvidenceMessage()));
        activationDataResponseCall.enqueue(new Callback<ActivationDataResponse>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onResponse(Call<ActivationDataResponse> call, Response<ActivationDataResponse> response) {
                if (response.isSuccessful()) {
                    Log.e("*** ACTIVATION DATA", response.body().toString());
                    if (response.body() != null) {
                        activationModel.setActivationDataResponse(response.body());
                        if (activationModel.getActivationDataResponse().getResult() == null) {
                            new GenericPopUp(context, activationModel.getActivationDataResponse().getResultCodes().getStatusCode() + "", activationModel.getActivationDataResponse().getResultCodes().getStatusCodeEnum()).showCustomPopup();
                            setResult.hideProgress();
                        } else {
                            try {
                                Log.e("** ActivationData", activationModel.getActivationDataResponse().getResult().getEncryptedLicenseActivationMessage());
                                activationModel.setEncryptionKey(DSAPPClient.decryptSRPData(activationModel.getSrpSessionKeyResponse().getSessionKey(), activationModel.getActivationDataResponse().getResult().getEncryptedLicenseActivationMessage(), activationModel.getActivationDataResponse().getResult().getEncryptedCounter(), activationModel.getActivationDataResponse().getResult().getMac()));
                                MultiDeviceActivateLicense();
                            } catch (DSAPPException e) {
                                setResult.showToast(e.getMessage());
                                setResult.hideProgress();
                                new GenericPopUp(context, context.getString(R.string.error), e.getMessage()).showCustomPopup();
                            }
                        }

                    } else {
                        new GenericPopUp(context, "GenerateActivationDate" + context.getString(R.string.error), context.getString(R.string.something_went_wrong)).showCustomPopup();
                        setResult.hideProgress();

                    }
                }
            }

            @Override
            public void onFailure(Call<ActivationDataResponse> call, Throwable t) {
                new GenericPopUp(context, context.getString(R.string.error), t.getMessage()).showCustomPopup();
                setResult.hideProgress();

            }
        });
    }

    private SecureChannelParseResponse getSecureChannelParseResponse(String message) {
        return DigipassSDK.parseSecureChannelMessage(message);
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    private String MultiDeviceActivateLicense() {
        Log.e("** EncryptionKey", new String(activationModel.getEncryptionKey(), StandardCharsets.UTF_8));
        SecureChannelParseResponse secureChannelParseResponse = getSecureChannelParseResponse(new String(activationModel.getEncryptionKey(), StandardCharsets.UTF_8));
        String message = "";
        if (secureChannelParseResponse.getReturnCode() != DigipassSDKReturnCodes.SUCCESS) {
            Log.e("** SecureChannelParseResponse", secureChannelParseResponse.getReturnCode() + "");
            message += "Parse secure channel message FAILED - [ " + secureChannelParseResponse.getReturnCode() + ": " + DigipassSDK.getMessageForReturnCode(secureChannelParseResponse.getReturnCode()) + " ]\n";
            new GenericPopUp(context, context.getString(R.string.error), message).showCustomPopup();
            return message;
        }

        try {
            activationModel.setMultiDeviceLicenseActivationResponse(DigipassSDK.multiDeviceActivateLicense(secureChannelParseResponse.getMessage(), readStaticVector(), activationModel.getPlatformFingerprint(), activationModel.getJailbreakStatus(), activationModel.getClientServerTimeShift()));
        } catch (Exception e) {
            new GenericPopUp(context, context.getString(R.string.error), e.getMessage()).showCustomPopup();
        }

        if (activationModel.getMultiDeviceLicenseActivationResponse().getReturnCode() != DigipassSDKReturnCodes.SUCCESS) {
            message += "Multi-device license activation FAILED - [ " + activationModel.getMultiDeviceLicenseActivationResponse().getReturnCode() + ": " + DigipassSDK.getMessageForReturnCode(activationModel.getMultiDeviceLicenseActivationResponse().getReturnCode()) + " ]\n";
            new GenericPopUp(context, context.getString(R.string.error), message).showCustomPopup();

            return message;
        }

        saveInLocalStorage.saveByteData("dynamicVector", activationModel.getMultiDeviceLicenseActivationResponse().getDynamicVector());
        saveInLocalStorage.saveByteData("staticVector", activationModel.getMultiDeviceLicenseActivationResponse().getStaticVector());
        addMDLDevice();
        return message;

    }

    private void addMDLDevice() {
        Call<MdlAddDeviceResponse> mdlAddDeviceResponseCall = apiService.addDeviceMDL(new MdlAddDeviceRequest(activationModel.getCredentialsData().getRegistrationIdentifier(), "", activationModel.getMultiDeviceLicenseActivationResponse().getDeviceCode()));
        mdlAddDeviceResponseCall.enqueue(new Callback<MdlAddDeviceResponse>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onResponse(Call<MdlAddDeviceResponse> call, Response<MdlAddDeviceResponse> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        if (response.body().getResult() == null) {
                            new GenericPopUp(context, response.body().getResultCodes().getStatusCode() + "", response.body().getResultCodes().getStatusCodeEnum()).showCustomPopup();
                            setResult.hideProgress();

                        } else {
                            sharedPreferenceStorage.setRegistrationId(activationModel.getCredentialsData().getRegistrationIdentifier());

//                            if (sharedPreferenceStorage.getUserId() != null) {
//                                enableBiometric();
//                            } else {
                            activationModel.setInstanceActivationMessage(response.body().getResult().getInstanceActivationMessage());
                            sharedPreferenceStorage.setMessageInstance(response.body().getResult().getInstanceActivationMessage());
                            setResult.hideProgress();
                            setResult.showCongratulation();
//                            }

                        }
                    } else {
                        new GenericPopUp(context, context.getString(R.string.error), context.getString(R.string.something_went_wrong)).showCustomPopup();
                        setResult.hideProgress();

                    }
                } else {
                    new GenericPopUp(context, "AddMDLDevice" + context.getString(R.string.error), context.getString(R.string.something_went_wrong)).showCustomPopup();
                    setResult.hideProgress();

                }
            }

            @Override
            public void onFailure(Call<MdlAddDeviceResponse> call, Throwable t) {
                new GenericPopUp(context, context.getString(R.string.error), t.getMessage()).showCustomPopup();
                setResult.hideProgress();

            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public String MultiDeviceActivationInstance(String password) {
        Log.e("** Password", password);
//        activationModel.setPassword(password);
        setResult.showProgress();
        Log.e("** SecureChannel", sharedPreferenceStorage.getMessageInstance());

        SecureChannelParseResponse secureChannelParseResponse = getSecureChannelParseResponse(sharedPreferenceStorage.getMessageInstance());
        String message = "";
        if (secureChannelParseResponse.getReturnCode() != DigipassSDKReturnCodes.SUCCESS) {
            message += "Parse secure channel message FAILED - [ " + secureChannelParseResponse.getReturnCode() + ": " + DigipassSDK.getMessageForReturnCode(
                    secureChannelParseResponse.getReturnCode()
            ) + " ]\n";
            new GenericPopUp(context, context.getString(R.string.error), message).showCustomPopup();
            setResult.hideProgress();


            return message;
        } else {
            Log.e("** StaticVector", Arrays.toString(saveInLocalStorage.getByteData("staticVector")));
            Log.e("** DynamicVector", Arrays.toString(saveInLocalStorage.getByteData("dynamicVector")));
            Log.e("** PlatformFingerPrint", sharedPreferenceStorage.getPlatformFingerPrint());
            Log.e("** Password set", password);
            ActivationResponse activationResponse = DigipassSDK.multiDeviceActivateInstance(saveInLocalStorage.getByteData("staticVector"), saveInLocalStorage.getByteData("dynamicVector"), secureChannelParseResponse.getMessage(), password, sharedPreferenceStorage.getPlatformFingerPrint());

            if (activationResponse.getReturnCode() != DigipassSDKReturnCodes.SUCCESS) {
                message += "Multi-device instance activation FAILED - [ " + activationResponse.getReturnCode() + ": " + DigipassSDK.getMessageForReturnCode(activationResponse.getReturnCode()) + " ]\n";
                new GenericPopUp(context, context.getString(R.string.error), message).showCustomPopup();
                setResult.hideProgress();


                return message;
            } else {
                byte[] dynamicVectorResponse = activationResponse.getDynamicVector();
                Log.e("** DynamicVector", Arrays.toString(dynamicVectorResponse));
                Log.e("** StaticVectore", Arrays.toString(activationResponse.getStaticVector()));

                saveInLocalStorage.saveByteData("dynamicVector", dynamicVectorResponse);
                generateSignatureFromSecureChannelMessage(secureChannelParseResponse, password);

            }
        }


        return message;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public String MultiDeviceActivationInstanceWithKey() {
        setResult.showProgress();
        SecureChannelParseResponse secureChannelParseResponse = getSecureChannelParseResponse(sharedPreferenceStorage.getMessageInstance());
        String message = "";
        if (secureChannelParseResponse.getReturnCode() != DigipassSDKReturnCodes.SUCCESS) {
            message += "Parse secure channel message FAILED - [ " + secureChannelParseResponse.getReturnCode() + ": " + DigipassSDK.getMessageForReturnCode(
                    secureChannelParseResponse.getReturnCode()
            ) + " ]\n";
            new GenericPopUp(context, context.getString(R.string.error), message).showCustomPopup();
            setResult.hideProgress();
            return message;
        }
        ActivationResponse activationResponse = DigipassSDK.multiDeviceActivateInstanceWithKey(saveInLocalStorage.getByteData("staticVector"), saveInLocalStorage.getByteData("dynamicVector"), secureChannelParseResponse.getMessage(), saveInLocalStorage.getByteData("biometricKey"), sharedPreferenceStorage.getPlatformFingerPrint());

        if (activationResponse.getReturnCode() != DigipassSDKReturnCodes.SUCCESS) {
            message += "Multi-device instance activation FAILED - [ " + activationResponse.getReturnCode() + ": " + DigipassSDK.getMessageForReturnCode(activationResponse.getReturnCode()) + " ]\n";
            new GenericPopUp(context, context.getString(R.string.error), message).showCustomPopup();
            setResult.hideProgress();

            return message;

        }
        byte[] dynamicVectorResponse = activationResponse.getDynamicVector();
        Log.e("** DynamicVector", Arrays.toString(dynamicVectorResponse));
        Log.e("** StaticVectore", Arrays.toString(activationResponse.getStaticVector()));
        saveInLocalStorage.saveByteData("dynamicVector", dynamicVectorResponse);
        generateSignatureFromSecureChannelMessageWithKey(secureChannelParseResponse);
        return message;
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    public String generateSignatureFromSecureChannelMessage(SecureChannelParseResponse secureChannelParseResponse, String password) {

        String message = "";
        GenerationResponse generationResponse = DigipassSDK.generateSignatureFromSecureChannelMessage(saveInLocalStorage.getByteData("staticVector"), saveInLocalStorage.getByteData("dynamicVector"), secureChannelParseResponse.getMessage(), password, activationModel.getClientServerTimeShift(), activationModel.getCryptoApplicationIndex(), sharedPreferenceStorage.getPlatformFingerPrint());

        if (generationResponse.getReturnCode() != DigipassSDKReturnCodes.SUCCESS) {
            message += "Generate signature from secure channel message FAILED - [ " + generationResponse.getReturnCode() + ": " + DigipassSDK.getMessageForReturnCode(generationResponse.getReturnCode()) + " ]";
            new GenericPopUp(context, context.getString(R.string.error), message).showCustomPopup();
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
        GenerationResponse generationResponse = DigipassSDK.generateSignatureFromSecureChannelMessageWithKey(saveInLocalStorage.getByteData("staticVector"), saveInLocalStorage.getByteData("dynamicVector"), secureChannelParseResponse.getMessage(), saveInLocalStorage.getByteData("biometricKey"), activationModel.getClientServerTimeShift(), activationModel.getCryptoApplicationIndex(), sharedPreferenceStorage.getPlatformFingerPrint());

        if (generationResponse.getReturnCode() != DigipassSDKReturnCodes.SUCCESS) {
            message += "Generate signature from secure channel message FAILED - [ " + generationResponse.getReturnCode() + ": " + DigipassSDK.getMessageForReturnCode(generationResponse.getReturnCode()) + " ]";
            new GenericPopUp(context, context.getString(R.string.error), message).showCustomPopup();
            setResult.hideProgress();


            return message;

        }
        activationModel.setSignature(generationResponse.getResponse());
        MdlActivate(true);
        return message;

    }

    private void MdlActivate(boolean isBiometric) {
        Call<MdlActivateResponse> mdlActivateResponseCall = apiService.activateMDL(new MdlActivateRequest(sharedPreferenceStorage.getRegistrationId(), activationModel.getSignature()));
        mdlActivateResponseCall.enqueue(new Callback<MdlActivateResponse>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onResponse(Call<MdlActivateResponse> call, Response<MdlActivateResponse> response) {
                setResult.hideProgress();
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        Log.e("** MDLActivate", response.body().toString());
//                        if (isBiometric) {
//                            userDatabaseHelper.addUserBiometric(Integer.parseInt(sharedPreferenceStorage.getUserId()), sharedPreferenceStorage.getPlatformFingerPrint(), response.body().getResult().getSerialNumber(), sharedPreferenceStorage.getStorageName(), false);
//                        } else {
                        if (activationModel.getName().equals(response.body().getResult().getUserID())) {
                            long id = userDatabaseHelper.addUser(sharedPreferenceStorage.getPlatformFingerPrint(), response.body().getResult().getSerialNumber(), response.body().getResult().getSerialNumber(), true, sharedPreferenceStorage.getStorageName());
                            sharedPreferenceStorage.setRegistrationId(null);
                            sharedPreferenceStorage.setMessageInstance(null);
                            sharedPreferenceStorage.setUserId(id + "");
                            userDatabaseHelper.updateIsUsed(id + "", true);
                            Intent intent = new Intent(context, HomeView.class);
                            context.startActivity(intent);
                        } else {
                            sharedPreferenceStorage.setRegistrationId(null);
                            sharedPreferenceStorage.setMessageInstance(null);
                            new GenericPopUp(context, "MDLActivate" + context.getString(R.string.error), context.getResources().getString(R.string.errorName)).showCustomPopup();
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
                }
            }

            @Override
            public void onFailure(Call<MdlActivateResponse> call, Throwable t) {
                setResult.hideProgress();
                new GenericPopUp(context, "MDLActivate" + context.getString(R.string.error), t.getMessage()).showCustomPopup();
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

    public void resetToken() {
        Call<SynchronizeResponse> callSynchronize = apiService.synchronize();
        callSynchronize.enqueue(new Callback<SynchronizeResponse>() {
            @Override
            public void onResponse(Call<SynchronizeResponse> call, Response<SynchronizeResponse> response) {
                if (response.isSuccessful()) {
                    SynchronizeResponse synchronizeResponse = response.body();
                    if (synchronizeResponse != null) {
                        if (synchronizeResponse.getResultCodes().getStatusCode() == 0) {
                            Log.e("** serverTimeShift", synchronizeResponse.getResult().getServerTime() + "");
                            long currentTimeMillis = System.currentTimeMillis();

                            convertEpochToDate(currentTimeMillis, synchronizeResponse.getResult().getServerTime());


                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<SynchronizeResponse> call, Throwable t) {
                Log.e("** error", t.getMessage());
            }
        });
    }

    public void convertEpochToDate(long currentTimeMillis, long epochSeconds) {
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
    }

    public interface SetResult {
        void showToast(String message);

        void showProgress();

        void hideProgress();

        void showCongratulation();

    }
}


