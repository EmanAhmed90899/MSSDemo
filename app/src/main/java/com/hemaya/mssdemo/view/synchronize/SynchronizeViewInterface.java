package com.hemaya.mssdemo.view.synchronize;

public interface SynchronizeViewInterface {
        void showProgress();

        void hideProgress();

        void showSuccess(String message);

        void showError(String message);

}
