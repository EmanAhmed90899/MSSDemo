package com.hemaya.mssdemo.utils.storage;

import android.content.Context;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.vasco.digipass.sdk.utils.devicebinding.DeviceBinding;
import com.vasco.digipass.sdk.utils.devicebinding.DeviceBindingSDKException;

import java.security.SecureRandom;
import java.util.Base64;

public class GetDevicePlatform {
    Context context;

    public GetDevicePlatform(Context context) {
        this.context = context;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public String getFingerPrint() {
        String fingerprint;
        try {
            if (new SharedPreferenceStorage(context).getPlatformFingerPrint() != null) {
                fingerprint = new SharedPreferenceStorage(context).getPlatformFingerPrint();
            } else {
                fingerprint = DeviceBinding.createDeviceBinding(context, DeviceBinding.FingerprintType.HARDWARE).fingerprint(generateSalt(64));
                new SharedPreferenceStorage(context).setPlatformFingerPrint(fingerprint);
            }
        } catch (DeviceBindingSDKException e) {
            throw new RuntimeException(e);
        }
        Log.e("** Fingerprint **", fingerprint);
        return fingerprint;

    }


    // Method to generate a salt string of the desired length
    @RequiresApi(api = Build.VERSION_CODES.O)
    public String generateSalt(int length) {
        // Create a SecureRandom instance
        SecureRandom secureRandom = new SecureRandom();

        // Create a byte array to hold the random values
        byte[] saltBytes = new byte[length];

        // Generate the random values
        secureRandom.nextBytes(saltBytes);

        // Convert the byte array to a Base64 encoded string
        return Base64.getEncoder().encodeToString(saltBytes);
    }
}
