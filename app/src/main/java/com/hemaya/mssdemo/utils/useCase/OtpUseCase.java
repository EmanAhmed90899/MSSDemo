package com.hemaya.mssdemo.utils.useCase;

import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.annotation.RequiresApi;

import com.hemaya.mssdemo.R;
import com.hemaya.mssdemo.model.UserModel.User;
import com.hemaya.mssdemo.utils.storage.GetDevicePlatform;
import com.hemaya.mssdemo.utils.storage.SaveInLocalStorage;
import com.hemaya.mssdemo.utils.storage.SharedPreferenceStorage;
import com.hemaya.mssdemo.utils.storage.UserDatabaseHelper;
import com.hemaya.mssdemo.utils.views.GenericPopUp;
import com.hemaya.mssdemo.view.identification.IdentificationView;
import com.vasco.digipass.sdk.DigipassSDK;
import com.vasco.digipass.sdk.DigipassSDKConstants;
import com.vasco.digipass.sdk.DigipassSDKReturnCodes;
import com.vasco.digipass.sdk.responses.GenerationResponse;

public class OtpUseCase {
    Context context;
    UserDatabaseHelper userDatabaseHelper;
    User user;

    public OtpUseCase(Context context) {
        this.context = context;
        userDatabaseHelper = new UserDatabaseHelper(context);
        init();
    }

    private void init() {
        user = userDatabaseHelper.getOneUsedUser();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public String generateOTP(String pin) {
        String platformFingerPrint = new GetDevicePlatform(context).getFingerPrint();

        SaveInLocalStorage saveInLocalStorage = new SaveInLocalStorage(context, user.getStorageName());

        GenerationResponse generateResponse = DigipassSDK.generateResponseOnly(
                saveInLocalStorage.getByteData("staticVector"), saveInLocalStorage.getByteData("dynamicVector"), pin, 0,
                DigipassSDKConstants.CRYPTO_APPLICATION_INDEX_APP_1, platformFingerPrint
        );


        if (generateResponse.getReturnCode() == DigipassSDKReturnCodes.SUCCESS) {
            return generateResponse.getResponse();
        } else {
            if (generateResponse.getReturnCode() == DigipassSDKReturnCodes.PASSWORD_WRONG) {
                new GenericPopUp(context, context.getResources().getString(R.string.invalid_pin)).showCustomPopup();
            } else {
                new GenericPopUp(context, context.getResources().getString(R.string.error_tryAgain)).showCustomPopup();

            }
            return null;

        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void clearAllData() {
        userDatabaseHelper.deleteAllUsers();
        new SharedPreferenceStorage(context).clearData();
        new SaveInLocalStorage(context, user.getStorageName()).clearData();


        Intent intent = new Intent(context, IdentificationView.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(intent);
    }
}
