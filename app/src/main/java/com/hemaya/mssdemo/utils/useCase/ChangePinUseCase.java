package com.hemaya.mssdemo.utils.useCase;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;

import com.hemaya.mssdemo.R;
import com.hemaya.mssdemo.model.UserModel.User;
import com.hemaya.mssdemo.model.UserModel.UserViewModel;
import com.hemaya.mssdemo.utils.storage.SaveInLocalStorage;
import com.hemaya.mssdemo.utils.storage.UserDatabaseHelper;
import com.vasco.digipass.sdk.DigipassSDK;
import com.vasco.digipass.sdk.DigipassSDKReturnCodes;
import com.vasco.digipass.sdk.responses.GenericResponse;

public class ChangePinUseCase {
    Context context;
    SaveInLocalStorage saveInLocalStorage;
    ChangePinUseCaseInterface changePinUseCaseInterface;
    User user;

    @RequiresApi(api = Build.VERSION_CODES.O)
    public ChangePinUseCase(Context context,UserViewModel userViewModel, ChangePinUseCaseInterface changePinUseCaseInterface) {
        this.context = context;
        this.changePinUseCaseInterface = changePinUseCaseInterface;
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    public void changePin(String oldPinStr, String newPinStr) {
        user = new UserDatabaseHelper(context).getOneUsedUser();
        saveInLocalStorage = new SaveInLocalStorage(context, user.getStorageName(), user.getPlatformFingerPrint());
        GenericResponse genericResponse = DigipassSDK.changePasswordWithFingerprint(
                saveInLocalStorage.getByteData("staticVector"), saveInLocalStorage.getByteData("dynamicVector"), oldPinStr, newPinStr, user.getPlatformFingerPrint()
        );

        String message = "";
        if (genericResponse.getReturnCode() != DigipassSDKReturnCodes.SUCCESS) {

            message += context.getResources().getString(R.string.userPasswordNotChanged);
            changePinUseCaseInterface.showMessage(message);
        } else {

            message += context.getResources().getString(R.string.userPasswordChanged);
            saveInLocalStorage.saveByteData("dynamicVector", genericResponse.getDynamicVector());
            Toast.makeText(context, context.getResources().getText(R.string.pinChangedSuccessfully), Toast.LENGTH_SHORT).show();

            changePinUseCaseInterface.pinResult();
        }


    }

    public interface ChangePinUseCaseInterface {
        void pinResult();
        void showMessage(String message);
    }
}
