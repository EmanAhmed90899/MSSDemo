// Copyright ® 2024 OneSpan North America, Inc. All rights reserved. 


/////////////////////////////////////////////////////////////////////////////
//
//
// This file is example source code. It is provided for your information and
// assistance. See your licence agreement for details and the terms and
// conditions of the licence which governs the use of the source code. By using
// such source code you will be accepting these terms and conditions. If you do
// not wish to accept these terms and conditions, DO NOT OPEN THE FILE OR USE
// THE SOURCE CODE.
//
// Note that there is NO WARRANTY.
//
//////////////////////////////////////////////////////////////////////////////


package com.hemaya.mssdemo.utils.views;

import android.app.Activity;

import com.hemaya.mssdemo.R;
import com.hemaya.mssdemo.view.identification.IdentificationView;
import com.vasco.digipass.sdk.utils.biometricsensor.BiometricSensorSDKScanListener;

/**
 * The class extending BiometricSensorSDKScanListener. It is used by BiometricSensorSDK as a
 * callback of the biometric events.
 */
public class BiometricSensorSDKScanListenerImpl implements BiometricSensorSDKScanListener {

    private final IdentificationView activity;

    public BiometricSensorSDKScanListenerImpl(Activity activity) {
        this.activity = (IdentificationView)activity;
    }

    @Override
    public void onBiometryScanFailed(int failCode, String failMessage) {
        activity.errorBiometric();
        activity.showToast(activity.getString(R.string.scan_fail) + " : " + failMessage);
    }

    @Override
    public void onBiometryScanError(int errorCode, String errorMessage) {
        activity.errorBiometric();
        activity.showToast(activity.getString(R.string.scan_error) + " : " + errorMessage);
    }

    @Override
    public void onBiometryScanSucceeded() {
        activity.successBiometric();
        activity.showToast(activity.getString(R.string.scan_success));

    }

    @Override
    public void onBiometryScanCancelled() {
        activity.showToast(activity.getString(R.string.scan_cancelled));

    }

    @Override
    public void onBiometryNegativeButtonClicked() {
        activity.showToast(activity.getString(R.string.negative_button_called));

    }
}
