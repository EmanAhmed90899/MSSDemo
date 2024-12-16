package com.hemaya.mssdemo.utils.useCase;

import android.content.Context;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.hemaya.mssdemo.model.UserModel.User;
import com.hemaya.mssdemo.utils.storage.SaveInLocalStorage;
import com.hemaya.mssdemo.utils.storage.UserDatabaseHelper;
import com.hemaya.mssdemo.utils.views.GenericPopUp;
import com.vasco.digipass.sdk.DigipassSDK;
import com.vasco.digipass.sdk.DigipassSDKConstants;
import com.vasco.digipass.sdk.DigipassSDKReturnCodes;
import com.vasco.digipass.sdk.responses.GenerationResponse;

import java.util.Arrays;

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

        SaveInLocalStorage saveInLocalStorage = new SaveInLocalStorage(context, user.getStorageName(),user.getPlatformFingerPrint());

          GenerationResponse generateResponse = DigipassSDK.generateResponseOnly(
                saveInLocalStorage.getByteData("staticVector"), saveInLocalStorage.getByteData("dynamicVector"), pin, 0,
                DigipassSDKConstants.CRYPTO_APPLICATION_INDEX_APP_1, user.getPlatformFingerPrint()
        );


        if (generateResponse.getReturnCode() == DigipassSDKReturnCodes.SUCCESS) {
            Log.e("** OTP", generateResponse.getResponse());
            return generateResponse.getResponse();
        } else {
            new GenericPopUp(context, "OTP Generation", generateResponse.getReturnCode() + ": " + DigipassSDK.getMessageForReturnCode(generateResponse.getReturnCode())).showCustomPopup();
            return null;

        }
    }
}
