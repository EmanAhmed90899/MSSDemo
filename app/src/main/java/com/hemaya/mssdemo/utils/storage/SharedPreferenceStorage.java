package com.hemaya.mssdemo.utils.storage;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferenceStorage {
    private static final String PREF_NAME = "User"; // Shared Preference file name
    private static final String KEY_USER_ID = "userId";
    private static final String KEY_PLATFORM_FINGER_PRINT = "platformFingerPrint";
    private static final String KEY_STORAGE_NAME = "storageName";
    private static final String KEY_INITIATE_DETECTION = "initiateDetection";
    private static final String KEY_Language = "language";
    private static final String KEY_REGISTRATION_ID = "registrationId";
    private static final String KEY_MESSAGE_INSTANCE = "messageInstance";
    private static final String KEY_TIME_SHIFT = "messageInstance";

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    // Constructor to initialize SharedPreferences
    public SharedPreferenceStorage(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }


    // Method to clear the SharedPreferences
    public void clearData() {
        setStorageName(null);
        setUserId(null);
        setPlatformFingerPrint(null);

    }


    public String getUserId() {
        return sharedPreferences.getString(KEY_USER_ID, null);
    }

    public void setUserId(String userId) {
        editor.putString(KEY_USER_ID, userId);
        editor.apply();
    }
    public String getPlatformFingerPrint() {
        return sharedPreferences.getString(KEY_PLATFORM_FINGER_PRINT, null);
    }

    public void setPlatformFingerPrint(String platformFingerPrint) {
        editor.putString(KEY_PLATFORM_FINGER_PRINT, platformFingerPrint);
        editor.apply();
    }

    public String getStorageName() {
        return sharedPreferences.getString(KEY_STORAGE_NAME, null);
    }


    public void setStorageName(String storageName) {
        editor.putString(KEY_STORAGE_NAME, storageName);
        editor.apply();
    }
    public String getLanguage() {
        return sharedPreferences.getString(KEY_Language, "en");
    }

    public void setLanguage(String language) {
        editor.putString(KEY_Language, language);
        editor.apply();
    }

    public boolean getInitiateDetection() {
        return sharedPreferences.getBoolean(KEY_INITIATE_DETECTION, false);
    }

    public void setInitiateDetection(boolean initiateDetection) {
        editor.putBoolean(KEY_INITIATE_DETECTION, initiateDetection);
        editor.apply();
    }

    public String getRegistrationId() {
        return sharedPreferences.getString(KEY_REGISTRATION_ID, null);
    }
    public void setRegistrationId(String registrationId) {
        editor.putString(KEY_REGISTRATION_ID, registrationId);
        editor.apply();
    }

    public String getMessageInstance() {
        return sharedPreferences.getString(KEY_MESSAGE_INSTANCE, null);
    }
    public void setMessageInstance(String messageInstance) {
        editor.putString(KEY_MESSAGE_INSTANCE, messageInstance);
        editor.apply();
    }

    public long getTimeShift() {
        return sharedPreferences.getLong(KEY_TIME_SHIFT, -1);
    }
    public void setTimeShift(long timeShift) {
        editor.putLong(KEY_TIME_SHIFT, timeShift);
        editor.apply();
    }
}
