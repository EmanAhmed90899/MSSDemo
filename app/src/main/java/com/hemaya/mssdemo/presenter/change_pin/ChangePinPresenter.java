package com.hemaya.mssdemo.presenter.change_pin;

import static com.hemaya.mssdemo.presenter.identification.IdentificationPresenter.isRepeated;
import static com.hemaya.mssdemo.presenter.identification.IdentificationPresenter.isSequential;

import android.content.Context;
import android.os.Build;

import androidx.annotation.RequiresApi;

import com.hemaya.mssdemo.R;
import com.hemaya.mssdemo.utils.useCase.ChangePinUseCase;
import com.hemaya.mssdemo.view.change_pin.ChangePinViewInterface;

public class ChangePinPresenter {
    private ChangePinViewInterface view;
    private ChangePinUseCase useCase;
    private Context context;

    public ChangePinPresenter(Context context, ChangePinViewInterface view, ChangePinUseCase useCase) {
        this.view = view;
        this.useCase = useCase;
        this.context = context;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void changePin(String oldPin, String newPin, String repeatedNewPin) {
        view.showProgress();
        if (oldPin.isEmpty() || newPin.isEmpty() || repeatedNewPin.isEmpty()) {
            view.showErrorMessage(context.getResources().getString(R.string.pleaseFillAllFields));
        } else {
            if (oldPin.equals(newPin)) {
                view.showErrorMessage(context.getResources().getString(R.string.oldPinandNewPinError));
            } else if (!newPin.equals(repeatedNewPin)) {
                view.showErrorMessage(context.getResources().getString(R.string.newPinNotMatch));
            } else if (isSequential(newPin) || isRepeated(newPin)) {
                view.showErrorMessage(context.getResources().getString(R.string.notValidPin));
            } else {
                useCase.changePin(oldPin, newPin);
            }
        }

    }
}
