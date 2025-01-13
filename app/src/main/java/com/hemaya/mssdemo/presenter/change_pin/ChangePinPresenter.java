package com.hemaya.mssdemo.presenter.change_pin;

import static com.hemaya.mssdemo.presenter.identification.IdentificationPresenter.isRepeated;
import static com.hemaya.mssdemo.presenter.identification.IdentificationPresenter.isSequential;

import android.content.Context;
import android.os.Build;

import androidx.annotation.RequiresApi;

import com.hemaya.mssdemo.R;
import com.hemaya.mssdemo.utils.useCase.ChangePinUseCase;
import com.hemaya.mssdemo.view.change_pin.ChangePinViewInterface;

public class ChangePinPresenter implements ChangePinPresenterInterface {
    private ChangePinViewInterface view;
    private ChangePinUseCase useCase;
    private Context context;

    public ChangePinPresenter(Context context, ChangePinViewInterface view, ChangePinUseCase useCase) {
        this.view = view;
        this.useCase = useCase;
        this.context = context;
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void changePin(String oldPin, String newPin, String repeatedNewPin) {
        if (oldPin.isEmpty() || newPin.isEmpty() || repeatedNewPin.isEmpty()) {
            view.showMessage(context.getResources().getString(R.string.pleaseFillAllFields));
        } else {
            if (oldPin.equals(newPin)) {
                view.showMessage(context.getResources().getString(R.string.oldPinandNewPinError));
            } else if (!newPin.equals(repeatedNewPin)) {
                view.showMessage(context.getResources().getString(R.string.newPinNotMatch));
            } else if (isSequential(newPin) || isRepeated(newPin)) {
                view.showMessage(context.getResources().getString(R.string.notValidPin));
            } else {
                useCase.changePin(oldPin, newPin);
            }
        }

    }
}
