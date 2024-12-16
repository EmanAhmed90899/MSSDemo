package com.hemaya.mssdemo.utils.storage;

import android.content.Context;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.vasco.digipass.sdk.utils.securestorage.SecureStorageSDK;
import com.vasco.digipass.sdk.utils.securestorage.SecureStorageSDKErrorCodes;
import com.vasco.digipass.sdk.utils.securestorage.SecureStorageSDKException;

import java.util.Arrays;


public class SaveInLocalStorage {
    private SecureStorageSDK secureStorage;
    private Context context;
    String fileName;
    String platformFingerPrint;

    @RequiresApi(api = Build.VERSION_CODES.O)
    public SaveInLocalStorage(Context context, String fileName, String platformFingerPrint) {
        this.context = context;
        this.fileName = fileName;
        this.platformFingerPrint = platformFingerPrint;
        getPlatformFingerPrint();
        initSecureStorage();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void initSecureStorage() {
        try {
            // Initialize a secure storage
            Log.e("** PLATFO", platformFingerPrint);
            Log.e("** FILENAM", fileName);

            secureStorage =
                    SecureStorageSDK.init(
                            fileName, platformFingerPrint, getIterationNumber(), context);
            Log.e("** initSecureStorage", "Secure Storage initialized successfully");
        } catch (SecureStorageSDKException e) {
            Log.e("** initSecureStorageError", "Failed to init Secure Storage, " + getErrorMessage(e));
            handleUnreadableAndCorruptedStorageException(e);
        }

    }

    // Get a readable error message from an Exception
    private String getErrorMessage(SecureStorageSDKException e) {
        switch (e.getErrorCode()) {
            case SecureStorageSDKErrorCodes.INTERNAL_ERROR:
                return "Internal error: " + e.getMessage();
            case SecureStorageSDKErrorCodes.STORAGE_NAME_NULL:
                return "Name of the storage null";
            case SecureStorageSDKErrorCodes.STORAGE_NAME_INCORRECT_LENGTH:
                return "Name of the storage too long";
            case SecureStorageSDKErrorCodes.STORAGE_NAME_INCORRECT_FORMAT:
                return "Name of the storage contains invalid characters. Must be alphanumeric";
            case SecureStorageSDKErrorCodes.UNKNOWN_STORAGE:
                return "Storage does not exist";
            case SecureStorageSDKErrorCodes.UNREADABLE_STORAGE:
                return "Storage not readable";
            case SecureStorageSDKErrorCodes.CONTEXT_NULL:
                return "Android context null";
            case SecureStorageSDKErrorCodes.ITERATION_COUNT_INCORRECT:
                return "Iteration count must be >0";
            case SecureStorageSDKErrorCodes.KEY_NULL:
                return "Key null";
            case SecureStorageSDKErrorCodes.KEY_INCORRECT_LENGTH:
                return "Key has incorrect length";
            case SecureStorageSDKErrorCodes.KEY_INCORRECT_FORMAT:
                return "Key contains invalid character";
            case SecureStorageSDKErrorCodes.UNKNOWN_KEY:
                return "Storage does not contains requested key";
            case SecureStorageSDKErrorCodes.VALUE_NULL:
                return "Value null";
            case SecureStorageSDKErrorCodes.VALUE_INCORRECT_FORMAT:
                return "Value contains invalid character";
            default:
                return "Unknown error";
        }
    }

    /**
     * Return the recommended iteration number to support old Android devices. If you don't need the
     * support of old devices the recommended iteration number is 8000.
     *
     * @return The recommended iteration number to support old Android devices.
     */
    private int getIterationNumber() {
        return 300;}

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void handleUnreadableAndCorruptedStorageException(SecureStorageSDKException exception) {
        if (exception.getErrorCode() == SecureStorageSDKErrorCodes.STORAGE_CORRUPTED
                || exception.getErrorCode() == SecureStorageSDKErrorCodes.UNREADABLE_STORAGE) {
            try {
                SecureStorageSDK.delete(fileName, context);
                secureStorage =
                        SecureStorageSDK.init(
                                fileName,
                                platformFingerPrint,
                                getIterationNumber(),
                                context);
            } catch (SecureStorageSDKException e) {
                Log.e("** handleUnreadableAndCorruptedStorageException ", "Failed to delete and init Secure Storage " + e.getErrorCode());

            }
        }
    }

    /**
     * Save the secure storage on persistent storage.
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void saveSecureStorage() {
        try {
            // Save secure storage on persistent storage.
            secureStorage.write(platformFingerPrint, getIterationNumber(), context);
        } catch (SecureStorageSDKException e) {
            Log.e("** saveSecureStorageError", "Failed to save Secure Storage, " + getErrorMessage(e));
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void saveStringData(String key, String value) {
        try {
            secureStorage.putString(key, value);
            saveSecureStorage();
        } catch (SecureStorageSDKException e) {
            Log.e("** saveDataError", "Failed to save data, " + getErrorMessage(e));
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void saveByteData(String key, byte[] value) {
        Log.e("** fileName", fileName);
        Log.e("** key", key);
        Log.e("** value", Arrays.toString(value));
        try {
            secureStorage.putBytes(key, value);
            saveSecureStorage();
        } catch (SecureStorageSDKException e) {
            Log.e("** saveDataError", "Failed to save data, " + getErrorMessage(e));
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public byte[] getByteData(String key) {
        try {
            return secureStorage.getBytes(key);

        } catch (SecureStorageSDKException e) {
            Log.e("** getDataError", "Failed to get data, " + getErrorMessage(e));
            return null;
        }
    }

    public String getStringData(String key) {
        try {
            return secureStorage.getString(key);
        } catch (SecureStorageSDKException e) {
            Log.e("** getDataError", "Failed to get data, " + getErrorMessage(e));
            return null;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void updateStringData(String key, String value) {
        try {
            secureStorage.remove(key);
            secureStorage.putString(key, value);
            saveSecureStorage();
        } catch (SecureStorageSDKException e) {
            Log.e("** updateDataError", "Failed to update data, " + getErrorMessage(e));
        }
    }

    public boolean deleteStorage(String fileName) {
        try {
            SecureStorageSDK.delete(fileName, context);
            return true;
        } catch (SecureStorageSDKException e) {
            Log.e("** deleteStorageError", "Failed to delete storage, " + getErrorMessage(e));
            return false;
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void getPlatformFingerPrint() {
        if (platformFingerPrint.isEmpty()) {
            platformFingerPrint = new GetDevicePlatform(context).getFingerPrint();
        }
    }
}
