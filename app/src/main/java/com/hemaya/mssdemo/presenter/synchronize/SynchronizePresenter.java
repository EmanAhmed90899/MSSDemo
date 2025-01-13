package com.hemaya.mssdemo.presenter.synchronize;

import android.content.Context;
import android.os.Build;

import androidx.annotation.RequiresApi;

import com.hemaya.mssdemo.model.ActivationModel;
import com.hemaya.mssdemo.utils.useCase.ActivationUseCase;
import com.hemaya.mssdemo.view.synchronize.SynchronizeViewInterface;

public class SynchronizePresenter implements SynchronizePresenterInterface {
    private SynchronizeViewInterface view;
    private ActivationUseCase activationUseCase;

    @RequiresApi(api = Build.VERSION_CODES.O)
    public SynchronizePresenter(SynchronizeViewInterface view) {
        this.view = view;
        activationUseCase = new ActivationUseCase((Context) view);
    }

    @Override
    public void synchronize() {
        view.showProgress();
        activationUseCase.resetToken(this);
    }

    public void onSuccess(String message) {
        view.hideProgress();
        view.showSuccess(message);
    }

    public void onError(String message) {
        view.hideProgress();
        view.showError(message);
    }
}
